# Addendum for my TVs

Paste [PROMPT.md](../PROMPT.md) (the part between the `---` lines) with the model filled in, then this
underneath. It adds what I settled on with the first TV (TCL 65C8K, see `tvs/tcl-65c8k/`).

---

This repo is on the `my-tv-kit` branch: the launcher already has an app lock, a wallpaper picker with
thumbnails and the CleanFrame-style settings screen. My kit is in `mykit/` (read `mykit/README.md`).

Extra steps, after the normal debloat and launcher steps:
- Run `tools/privacy.py` and show me what it finds. Then offer `mykit/privacy-extras.txt` (Google's ad
  and learning modules) as its own small batch, with a screenshot afterwards.
- Ask me whether to turn location off (`settings put secure location_mode 0`). Tell me the Wi-Fi pill
  then shows "Wi-Fi" instead of the network name. Note the old value for the restore script.
- Remind me to switch off Settings > Privacy > Usage & diagnostics and Ads myself: the shell can't open them.
- Start from my dock and looks in `mykit/looks/`. Ask before changing the dock order. Put this TV's own
  keys (live_tv_package, remote keycodes) in `mykit/tvs/<brand-model>/tv.json`, not in `config.xml`, and
  push with `mykit/push-look.sh glass <brand-model>`.
- Install the launcher with `HOME_KEY_SERVICE=1` if the remote's gear button needs it or I want the app
  lock. Measure volume key-repeat before and after, and ask me which one I keep.
- App lock: walk me through Launcher settings > App lock on the TV. Don't set a PIN for me. If a test
  PIN is unavoidable, tell me it plainly so I change it.
- If the remote's Input button opens a panel that isn't the Live TV app, find its package
  (`dumpsys window | grep mCurrentFocus` while it is open). If it differs from `com.tcl.suspension`, add it to
  `input_lock_packages` in `config.xml` and rebuild.
- Save this TV as `mykit/tvs/<brand-model>/`: debloat.txt, restore.sh, tv.json and README.md, like the
  TCL folder. No IP addresses, Wi-Fi names or serials in it. Commit to `my-tv-kit`.
- Closing ADB: some firmware keeps `service.adb.tcp.port=5555` after a reboot. If `tools/adb-closed.py`
  still finds the port open after I've switched debugging off, check `settings get global adb_enabled`.
  If it says 1, ask me, then run `settings put global adb_enabled 0` and check again.
