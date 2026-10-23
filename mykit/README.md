# My TV kit

My settings for cleaning a TV with this repo, kept on the `my-tv-kit` branch so they can be reused
on the next TV. The repo's own tools and PROMPT.md do the work; this folder only holds my choices.

## On a new TV

1. `git checkout my-tv-kit`
2. Paste [PROMPT.md](../PROMPT.md) with the model filled in, followed by [PROMPT-addendum.md](PROMPT-addendum.md),
   into Claude Code in this repo.
3. When it's done, the TV has its own folder in `tvs/`, committed to this branch.

## What's here

| Path | What it is |
|---|---|
| `PROMPT-addendum.md` | The extra steps I want on every TV: privacy pass, location, looks, app lock, closing ADB |
| `privacy-extras.txt` | Google ad and learning modules to disable on Android 12+ (`tools/disable.sh`) |
| `looks/glass`, `looks/black` | My two looks: frosted glass, or Apple TV black. Both carry my dock |
| `looks/wallpapers/` | 15 wallpapers shared by both looks, for the on-TV picker |
| `push-look.sh` | `TV_HOST=<tv ip> mykit/push-look.sh glass <tv>` pushes a look with that TV's `tv.json` on top |
| `tvs/<brand-model>/` | One TV each: `debloat.txt`, `restore.sh`, `tv.json` (its Live TV app, remote keys), `README.md` |

## Launcher features on this branch

All are off until used, so a TV that doesn't need them is unchanged.

- **App lock**: Launcher settings > App lock. A 4-digit PIN for chosen apps, Launcher settings, TV Settings
  and Inputs. It needs the launcher installed with `HOME_KEY_SERVICE=1`. `input_lock_packages` in
  `launcher/app/src/main/res/values/config.xml` lists the TV's own Input panel (TCL: `com.tcl.suspension`).
- **Wallpaper picker** shows the pictures.
- **Settings screen** in CleanFrame's style: a solid light plate on focus, an outline on the applied choice.

The APK is about 491,470 bytes. The repo's CI gate is 491,520, so anything added to the launcher will
push it over. That only matters if this goes upstream.

## Dock

The dock is in `looks/*/config.json` (`dock_apps`, left to right). Apps a TV doesn't have are skipped.
Edit both looks, then push again.
