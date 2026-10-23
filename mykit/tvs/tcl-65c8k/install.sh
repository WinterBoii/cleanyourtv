#!/usr/bin/env bash
# Rebuild the launcher, install it as Home on the TCL 65C8K, then push the glass look with this TV's keys.
# HOME_KEY_SERVICE=1 keeps the gear button and the app lock working; Android switches the helper off on every
# reinstall, so it is passed each time. Projectivy was Home before, so no stock launcher is disabled.
# Usage: TV_HOST=<tv ip> mykit/tvs/tcl-65c8k/install.sh [look]
set -euo pipefail
cd "$(dirname "$0")/../../.."
: "${TV_HOST:?set TV_HOST to the TV IP address}"
STOCK_LAUNCHER=none HOME_KEY_SERVICE=1 launcher/build-and-install.sh
mykit/push-look.sh "${1:-glass}" tcl-65c8k
