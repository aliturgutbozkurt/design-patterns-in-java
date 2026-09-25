#!/usr/bin/env bash
# Builds PDFs from lesson Markdown (EN + TR) with pandoc → Typst. Mermaid blocks are pre-rendered.
#
# usage: scripts/build-pdf.sh <dir> [<dir> …]   e.g. modules/m03-creational-construction  or  capstone
#        scripts/build-pdf.sh --all
# Converts <dir>/lesson/*.{en,tr}.md and <dir>/*.{en,tr}.md (capstone guide) into sibling .pdf files.
# Requires: pandoc ≥ 3.1, typst ≥ 0.12, node (npx) for mermaid-cli. Fonts are Typst's bundled fonts.
set -euo pipefail
cd "$(dirname "$0")/.."

for tool in pandoc typst npx; do
  command -v "$tool" >/dev/null || { echo "missing tool: $tool (see CONTRIBUTING.md)"; exit 1; }
done

dirs=("$@")
if [[ ${#dirs[@]} -eq 0 ]]; then echo "usage: $0 <dir>… | --all"; exit 2; fi
if [[ ${dirs[0]} == --all ]]; then
  dirs=()
  for d in modules/*/ capstone/; do [[ -d $d ]] && dirs+=("${d%/}"); done
fi

export MERMAID_CACHE="$PWD/build/mermaid"
count=0
for dir in "${dirs[@]}"; do
  shopt -s nullglob
  for md in "$dir"/lesson/*.en.md "$dir"/lesson/*.tr.md "$dir"/*.en.md "$dir"/*.tr.md; do
    case "$md" in *.en.md) lang=en ;; *.tr.md) lang=tr ;; esac
    pdf="${md%.md}.pdf"
    # Resource path lets images referenced relative to the Markdown file resolve.
    SOURCE_DIR="$(dirname "$md")" pandoc --defaults docs/pdf/defaults.yaml \
      --resource-path "$(dirname "$md")":. \
      --metadata lang="$lang" \
      --pdf-engine-opt=--root=/ \
      -o "$pdf" "$md"
    echo "✓ $pdf"
    count=$((count + 1))
  done
done
echo "built $count PDF(s)"
