#!/usr/bin/env bash
# EN/TR parity, relative links, and lesson code ↔ source checks. See scripts/lib/check_docs.py.
exec python3 "$(dirname "$0")/lib/check_docs.py" "$@"
