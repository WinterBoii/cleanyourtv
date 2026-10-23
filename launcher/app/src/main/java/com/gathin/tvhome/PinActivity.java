package com.gathin.tvhome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Asks for the AppLock PIN. EXTRA_TARGET unlocks that entry and returns RESULT_OK; EXTRA_SET asks for a new PIN
 * twice and saves it. Back or Cancel over a locked app goes Home, so the app stays covered.
 */
public final class PinActivity extends Activity {
    static final String EXTRA_TARGET = "com.gathin.tvhome.extra.LOCK_TARGET";
    static final String EXTRA_SET = "com.gathin.tvhome.extra.SET_PIN";
    private static final int LENGTH = 4;
    // ponytail: process-wide back-off, reset when the launcher restarts; enough against a child guessing by hand.
    private static int failures;
    private static long waitUntil;

    private final StringBuilder digits = new StringBuilder();
    private String first;
    private TextView title;
    private TextView dots;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_pin);
        title = findViewById(R.id.pinTitle);
        dots = findViewById(R.id.pinDots);
        bindKeys((ViewGroup) dots.getParent());
        findViewById(R.id.pinCancel).setOnClickListener(v -> cancel());
        findViewById(R.id.pinDelete).setOnClickListener(v -> delete());
        reset(isSetting() ? R.string.pin_new : R.string.pin_enter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        reset(isSetting() ? R.string.pin_new : R.string.pin_enter);
    }

    private void bindKeys(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof ViewGroup) bindKeys((ViewGroup) child);
            else if (child instanceof TextView && child.getId() == View.NO_ID && child.isFocusable()) {
                TextView key = (TextView) child;
                key.setOnClickListener(v -> type(key.getText().charAt(0)));
            }
        }
    }

    private boolean isSetting() {
        return getIntent().getBooleanExtra(EXTRA_SET, false);
    }

    private void reset(int message) {
        digits.setLength(0);
        title.setText(message);
        showDots();
    }

    private void showDots() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) s.append(i < digits.length() ? '●' : '○');
        dots.setText(s);
    }

    private void type(char digit) {
        if (SystemClock.elapsedRealtime() < waitUntil) {
            title.setText(R.string.pin_wait);
            return;
        }
        if (digits.length() >= LENGTH) return;
        digits.append(digit);
        showDots();
        if (digits.length() == LENGTH) dots.postDelayed(this::submit, 150);
    }

    private void delete() {
        if (digits.length() > 0) digits.setLength(digits.length() - 1);
        showDots();
    }

    private void submit() {
        String pin = digits.toString();
        if (isSetting()) {
            if (first == null) {
                first = pin;
                reset(R.string.pin_confirm);
            } else if (first.equals(pin)) {
                AppLock.setPin(this, pin);
                setResult(RESULT_OK);
                finish();
            } else {
                first = null;
                reset(R.string.pin_mismatch);
            }
            return;
        }
        if (AppLock.check(this, pin)) {
            failures = 0;
            String target = getIntent().getStringExtra(EXTRA_TARGET);
            if (target != null) AppLock.unlock(target);
            setResult(RESULT_OK);
            finish();
        } else {
            if (++failures % 5 == 0) waitUntil = SystemClock.elapsedRealtime() + 30_000;
            reset(R.string.pin_wrong);
        }
    }

    private void cancel() {
        // Started over a locked app by HomeKeyService (new task, no caller): leave for Home instead of uncovering it.
        if (getCallingActivity() == null && !isSetting()) {
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setPackage(getPackageName())
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            type((char) ('0' + keyCode - KeyEvent.KEYCODE_0));
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            delete();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
