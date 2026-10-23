package com.gathin.tvhome;

import android.content.Context;
import android.content.SharedPreferences;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * PIN lock for chosen apps. Off until a PIN is set in Launcher settings: with no PIN, nothing is locked and
 * HomeKeyService asks for no accessibility events. Entries are package names, plus two launcher screens.
 * An unlock lasts until the screen turns off (HomeKeyService) or the launcher process ends.
 */
final class AppLock {
    static final String LAUNCHER_SETTINGS = "launcher_settings";
    static final String INPUTS = "inputs";
    private static final String PREFS = "app_lock";
    private static final Set<String> unlocked = new HashSet<>();

    private AppLock() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    static boolean active(Context context) {
        return prefs(context).contains("pin");
    }

    static Set<String> entries(Context context) {
        return new HashSet<>(prefs(context).getStringSet("entries", new HashSet<>()));
    }

    /** What the Inputs entry also covers: the Live TV app and the TV's own source panel (config.xml). Read once,
     *  since HomeKeyService asks on every window change. */
    private static Set<String> inputPackages;

    static synchronized Set<String> inputPackages(Context context) {
        if (inputPackages != null) return inputPackages;
        Set<String> packages = new HashSet<>();
        Config config = Config.load(context);
        if (!config.liveTvPackage.isEmpty()) packages.add(config.liveTvPackage);
        for (String p : context.getResources().getStringArray(R.array.input_lock_packages)) packages.add(p);
        return inputPackages = packages;
    }

    static boolean needsPin(Context context, String entry) {
        if (entry == null || !active(context) || unlocked.contains(entry)) return false;
        Set<String> locked = entries(context);
        if (locked.contains(entry)) return true;
        return locked.contains(INPUTS) && inputPackages(context).contains(entry);
    }

    static void unlock(String entry) {
        unlocked.add(entry);
    }

    static void relockAll() {
        unlocked.clear();
    }

    static boolean check(Context context, String pin) {
        SharedPreferences p = prefs(context);
        return p.getString("pin", "").equals(hash(p.getString("salt", ""), pin));
    }

    static void setPin(Context context, String pin) {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        String salt = hex(bytes);
        prefs(context).edit().putString("salt", salt).putString("pin", hash(salt, pin)).commit();
        HomeKeyService.lockChanged();
    }

    static void setEntries(Context context, Set<String> entries) {
        prefs(context).edit().putStringSet("entries", entries).commit();
        HomeKeyService.lockChanged();
    }

    static void turnOff(Context context) {
        prefs(context).edit().clear().commit();
        relockAll();
        HomeKeyService.lockChanged();
    }

    static String hash(String salt, String pin) {
        try {
            return hex(MessageDigest.getInstance("SHA-256").digest((salt + pin).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (byte b : bytes) s.append(String.format("%02x", b));
        return s.toString();
    }
}
