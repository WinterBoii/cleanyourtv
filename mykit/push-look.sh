#!/usr/bin/env bash
# Push a look to the launcher, with one TV's own keys on top (Live TV app, remote keycodes).
# The look gives style, dock and wallpaper.jpg; looks/wallpapers/ is shared by all looks and
# feeds the on-TV wallpaper picker. Wraps tools/theme.sh, which restarts Home.
# Usage: TV_HOST=<tv ip> mykit/push-look.sh glass tcl-65c8k
set -euo pipefail
cd "$(dirname "$0")"
LOOK="looks/${1:?usage: push-look.sh <look> [tv]}"
TV="${2:+tvs/$2/tv.json}"
OUT="$(mktemp -d)"
trap 'rm -rf "$OUT"' EXIT
mkdir "$OUT/wallpapers"
cp looks/wallpapers/*.jpg "$OUT/wallpapers/"
[ -f "$LOOK/wallpaper.jpg" ] && cp "$LOOK/wallpaper.jpg" "$OUT/"
python3 -c 'import json,sys; c=json.load(open(sys.argv[1])); c.update(json.load(open(sys.argv[2])) if len(sys.argv) > 3 else {}); json.dump(c, open(sys.argv[-1], "w"), indent=2)' \
  "$LOOK/config.json" ${TV:+"$TV"} "$OUT/config.json"
../tools/theme.sh "$OUT"
