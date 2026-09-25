#!/usr/bin/env bash
# Verifies that every exercise starter compiles AND still fails its contract tests,
# i.e. no starter accidentally ships a working solution. Used by CI; safe to run locally.
#
# usage: scripts/check-starters.sh [maven -pl selector, e.g. modules/m03-creational-construction]
set -uo pipefail
cd "$(dirname "$0")/.."

pl=()
[[ $# -gt 0 ]] && pl=(-pl "$1")

find . -path '*/target/surefire-reports' -type d -prune -exec rm -rf {} + 2>/dev/null
./mvnw -q -B test -Pexercises -Dmaven.test.failure.ignore=true "${pl[@]}" >/dev/null 2>&1
status=$?
if [[ $status -ne 0 ]]; then
  echo "✗ build failed (starters or tests do not compile) — run: ./mvnw test -Pexercises ${pl[*]}"
  exit 1
fi

checked=0; bad=0
while IFS= read -r report; do
  checked=$((checked + 1))
  suite=$(basename "$report" .xml); suite=${suite#TEST-}
  failures=$(grep -o '<testsuite [^>]*' "$report" | grep -o ' failures="[0-9]*"' | grep -o '[0-9]*')
  errors=$(grep -o '<testsuite [^>]*' "$report" | grep -o ' errors="[0-9]*"' | grep -o '[0-9]*')
  if [[ $((failures + errors)) -eq 0 ]]; then
    echo "✗ $suite passes on the starter code — the starter must not contain a solution"
    bad=$((bad + 1))
  else
    echo "✓ $suite fails on the starter as expected ($((failures + errors)) failing)"
  fi
done < <(find . -path '*/target/surefire-reports/TEST-*ExerciseTest.xml' | sort)

echo "checked $checked exercise suite(s), $bad problem(s)"
[[ $bad -eq 0 ]]
