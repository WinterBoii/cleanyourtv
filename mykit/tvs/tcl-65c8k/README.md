# TCL 65C8K: what was changed and how to undo it

Done on 2026-10-22 over network ADB. TV: G08 board, Android 14 (Google TV), firmware
`UTT2.250416.001 AU02`. Files are in `mykit/tvs/tcl-65c8k/`. Run everything from the repo root, on the `my-tv-kit` branch, with `TV_HOST` set to the TV's IP
(see your router). Debugging must be on again
(Settings > System > Developer options > USB debugging) for any of these commands.

| File | What it is |
|---|---|
| `debloat.txt` | The 35 packages that were disabled, grouped and commented |
| `restore.sh` | Undo everything: re-enables the 35, Projectivy back as Home with its accessibility service, old DNS and location |
| `install.sh` | Rebuild the launcher, install it as Home with the gear button helper, push the glass look |
| `tv.json` | This TV's own launcher keys (Live TV app, gear keycode 83), merged over a look by `mykit/push-look.sh` |
| `backup/20261022-2042/` (not in git) | Package lists, getprop, all settings, Home and display state from before any change |

## What changed

- **Disabled** (not uninstalled): TCL tracking (Samba TV ACR, log upload, user center), TCL content
  and promos, partner auto-installs, store demo, e-manual, Alexa, the TCL cleaner, the Google ambient
  screensaver, and by your choice Assistant, Chromecast, Netflix, Prime Video, Google TV app, Miracast,
  T-Cast, USB media player and gallery, T-Solo and the soundbar updater. Full list: `debloat.txt`.
- **Kept**: Live TV and HDMI (`com.tcl.tv`), the Input panel, keyboard, remote pairing, Play Store and
  Services, AirPlay, game bar, firmware updates. Your own apps were not touched.
- **Launcher**: `com.gathin.tvhome` is Home. Projectivy is still installed, but its accessibility
  service is off so it no longer takes the Home button. Aerial Views is still the screensaver.
- **Gear button**: works through the launcher's HomeKeyService. Trade-off measured on this TV: while
  that helper is on, holding volume moves one step per press instead of ramping. Turn the helper off
  to get the ramp back (the gear then does nothing):
  `tools/venv/bin/python tools/tv.py "settings put secure enabled_accessibility_services ''"`.
  A force stop of the launcher (Settings > Apps) also switches it off; `install.sh` turns it on again.
- **More privacy** (second pass): Google's ad modules (Privacy Sandbox ad services, on-device ad
  personalisation, federated learning), Health Connect and calendar sync are disabled too, and
  location is off (`location_mode 0`), so Google no longer gets nearby Wi-Fi scans. The cost is that
  the Wi-Fi pill shows "Wi-Fi" instead of the network name. Two switches only you can flip, because
  the shell is not allowed to open them: Settings > Privacy > Usage & diagnostics (off), and
  Settings > Privacy > Ads (delete the advertising ID, or switch personalisation off).
- **DNS**: Private DNS set to `dns.adguard-dns.com` (it was the older `dns.adguard.com`). It blocks ad
  and tracker domains, not YouTube ads. Undo: Settings > Network > Private DNS, or
  `tools/venv/bin/python tools/tv.py 'settings put global private_dns_mode opportunistic'`.

## App lock (PIN)

Hold OK on the gear > Launcher settings > App lock. The first time it asks for a new 4-digit PIN
twice, then shows the list of what to lock. Launcher settings and TV system settings come pre-ticked,
so the lock can't be switched off without the PIN. "Inputs and Live TV" covers the Inputs menu, the
remote's Input button and the Live TV app. Any TV app can be ticked below that.

- The PIN pad appears whenever a locked app or screen comes to the front: from Home, Recents, a cast
  or a link. Enter it with the arrows and OK, or with the remote's number keys. Cancel or Back goes Home.
- Once unlocked, an app stays open until the TV goes to sleep. After five wrong tries the pad waits 30 seconds.
- With the Input button, the first press shows the PIN pad and the next press opens the input panel.
- It runs in the launcher's Home-button helper. It stops working if that helper is off, for example
  after a force stop of the launcher, until `install.sh` runs again. TV Settings being locked is
  what stops a child from doing that force stop.
- A test PIN was set during setup; change it under App lock > Change PIN.
- Forgot the PIN (debugging must be on): this clears all launcher choices, not just the lock.
  Push the preset again afterwards.
  `tools/venv/bin/python tools/tv.py 'pm clear com.gathin.tvhome'`, then `mykit/tvs/tcl-65c8k/install.sh`.

## Re-enable one app

On the TV: Settings > Apps > See all apps > Show system apps > the app > Enable. Over ADB:

```bash
TV_HOST=<tv ip> tools/venv/bin/python tools/tv.py 'pm enable com.netflix.ninja'
```

Netflix needs `com.netflix.tokenmanager` too. Comment the line out in `debloat.txt` so a later
re-run keeps it.

## Change the dock order or size

On the TV: hold OK on the gear > Edit dock (swap an app) or Reorder apps; Launcher settings > Apps per
row sets the slot count.

From the computer: the active preset's `dock_apps` (order, left to right) and `columns` win over
`config.xml`, so edit `mykit/looks/glass/config.json` (and `black`), then
`TV_HOST=<tv ip> mykit/push-look.sh glass tcl-65c8k`. `config.xml` only matters with no preset
pushed or after a rebuild. Package names:
`tools/venv/bin/python tools/tv.py 'pm list packages -3'`.

## Change the look

On the TV, no computer needed: hold OK on the gear button in the top bar > Launcher settings. There you
pick the wallpaper (16 images, or None for pitch black), dock band opacity, apps per row and rows
below the dock. "Follow theme" undoes those choices.

Two looks live in `mykit/looks/`, sharing 15 wallpapers plus their own `wallpaper.jpg` for the on-TV picker:

| Preset | Look |
|---|---|
| `glass` (active) | Frosted-glass dock, cinematic top shelf in the focused app's colour, round gear, glass-navy wallpaper |
| `black` | Apple TV style pitch black, labelled Settings pill, no wallpaper until you pick one |

Switch with debugging on: `TV_HOST=<tv ip> mykit/push-look.sh black tcl-65c8k` (or `glass`).
Pushing a preset clears the on-TV choices. To add a wallpaper, drop a dark 1920x1080 JPEG under 300 KB
into `mykit/looks/wallpapers/` and push again. Other keys (accent glow, columns, clock screensaver) are
in `themes/README.md`; `tools/theme.sh --reset` drops the preset and uses `config.xml` alone.

## Undo everything

```bash
TV_HOST=<tv ip> mykit/tvs/tcl-65c8k/restore.sh
```
