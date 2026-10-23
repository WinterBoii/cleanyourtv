#!/usr/bin/env bash
# Put the TCL 65C8K back the way it was on 2026-10-22 before the cleanup:
# re-enable the 35 packages in mykit/tvs/tcl-65c8k/debloat.txt, Projectivy back as Home with its accessibility
# service, and Private DNS back to its old resolver. The new launcher stays installed but unused.
# Usage: TV_HOST=<tv ip> mykit/tvs/tcl-65c8k/restore.sh
set -euo pipefail
cd "$(dirname "$0")/../../.."
: "${TV_HOST:?set TV_HOST to the TV IP address}"
tools/restore.sh mykit/tvs/tcl-65c8k/debloat.txt com.spocky.projengmenu
T() { tools/venv/bin/python -I tools/tv.py "$@"; }
T 'settings put secure enabled_accessibility_services com.spocky.projengmenu/.services.ProjectivyAccessibilityService'
T 'settings put secure accessibility_enabled 1'
T 'settings put global private_dns_specifier dns.adguard.com'
T 'settings put global private_dns_mode hostname'
T 'settings put secure location_mode 3'
T 'cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME | tail -n 1'
