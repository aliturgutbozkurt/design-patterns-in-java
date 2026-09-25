#!/usr/bin/env python3
"""Documentation checks (CLAUDE.md §7, plan AD6/AD7).

1. Parity   — every X.en.md has an X.tr.md (and README.md ↔ README.tr.md) with the same heading levels in the same
               order and the same code blocks (language + `// file:` marker).
2. Links    — relative Markdown links/images point to files that exist.
3. Code     — every ```java block starts with `// file: <path suffix>` (or `// snippet` for deliberately
               non-compiled fragments) and its lines appear, in order, in that real source file.
               A line containing only `// ...` skips any number of source lines.
Exit code 0 = clean.
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SKIP_DIRS = {".git", "target", "build", "node_modules", "templates"}
FENCE = re.compile(r"^(```+|~~~+)\s*([\w{}.\-=\" ]*)")
HEADING = re.compile(r"^(#{1,6})\s+\S")
LINK = re.compile(r"!?\[[^\]]*\]\(([^)\s]+)(?:\s+\"[^\"]*\")?\)")
MARKER = re.compile(r"^//\s*file:\s*(\S+)\s*$")

errors: list[str] = []


def rel(p: Path) -> str:
    return str(p.relative_to(ROOT))


def markdown_files():
    for p in sorted(ROOT.rglob("*.md")):
        if not SKIP_DIRS.intersection(p.relative_to(ROOT).parts):
            yield p


def parse(path: Path):
    """Returns (headings, code_blocks, links) where code blocks are (lang, lines, start_line)."""
    headings, blocks, links = [], [], []
    in_code, fence, lang, buf, start = False, "", "", [], 0
    text = path.read_text(encoding="utf-8")
    # Blank out HTML comments (template instructions, disabled links) but keep line numbers.
    text = re.sub(r"<!--.*?-->", lambda m: "\n" * m.group().count("\n"), text, flags=re.S)
    for no, line in enumerate(text.splitlines(), 1):
        m = FENCE.match(line)
        if in_code:
            if re.fullmatch(re.escape(fence[0]) + "{%d,}" % len(fence), line.strip()):
                blocks.append((lang, buf, start))
                in_code = False
            else:
                buf.append(line)
            continue
        if m:
            in_code, fence, lang, buf, start = True, m.group(1), m.group(2).strip().strip("{}.").split()[0] if m.group(2).strip() else "", [], no
            continue
        h = HEADING.match(line)
        if h:
            headings.append((len(h.group(1)), no, line.strip()))
        links += [(no, t) for t in LINK.findall(line)]
    return headings, blocks, links


def block_signature(block):
    lang, lines, _ = block
    marker = next((MARKER.match(l.strip()).group(1) for l in lines[:1] if MARKER.match(l.strip())), None)
    return lang, marker


def check_parity(en: Path, tr: Path):
    if not tr.exists():
        errors.append(f"{rel(en)}: missing Turkish counterpart {rel(tr)}")
        return
    he, be, _ = parse(en)
    ht, bt, _ = parse(tr)
    for i in range(max(len(he), len(ht))):
        a = he[i] if i < len(he) else None
        b = ht[i] if i < len(ht) else None
        if a is None or b is None or a[0] != b[0]:
            where_en = f"{rel(en)}:{a[1]} {a[2]!r}" if a else f"{rel(en)}: <no heading>"
            where_tr = f"{rel(tr)}:{b[1]} {b[2]!r}" if b else f"{rel(tr)}: <no heading>"
            errors.append(f"heading parity #{i + 1}: {where_en}  ≠  {where_tr}")
            break
    se, st = [block_signature(b) for b in be], [block_signature(b) for b in bt]
    if se != st:
        for i in range(max(len(se), len(st))):
            a = se[i] if i < len(se) else None
            b = st[i] if i < len(st) else None
            if a != b:
                le = be[i][2] if i < len(be) else "-"
                lt = bt[i][2] if i < len(bt) else "-"
                errors.append(f"code-block parity #{i + 1}: {rel(en)}:{le} {a} ≠ {rel(tr)}:{lt} {b}")
                break


def check_links(md: Path):
    for no, target in parse(md)[2]:
        if re.match(r"^[a-zA-Z][\w+.-]*:", target) or target.startswith("#"):
            continue
        path = target.split("#", 1)[0]
        if path and not (md.parent / path).exists():
            errors.append(f"{rel(md)}:{no}: broken link → {target}")


def module_root(md: Path):
    for d in md.parents:
        if (d / "pom.xml").exists() and d != ROOT:
            return d
        if d == ROOT:
            return None
    return None


def norm(lines):
    return [l.strip() for l in lines if l.strip()]


def contains_in_order(chunks, source):
    pos = 0
    for chunk in chunks:
        if not chunk:
            continue
        found = False
        while pos + len(chunk) <= len(source):
            if source[pos : pos + len(chunk)] == chunk:
                pos += len(chunk)
                found = True
                break
            pos += 1
        if not found:
            return chunk
    return None


def check_code(md: Path):
    if md.relative_to(ROOT).parts[0] not in ("modules", "capstone"):
        return  # engineering docs (CLAUDE.md, CONTRIBUTING.md) may show illustrative code
    root = module_root(md)
    for lang, lines, start in parse(md)[1]:
        if lang != "java":
            continue
        first = lines[0].strip() if lines else ""
        if first.startswith("// snippet"):
            continue
        m = MARKER.match(first)
        if not m:
            errors.append(f"{rel(md)}:{start}: java block must start with '// file: <path>' or '// snippet'")
            continue
        if root is None:
            errors.append(f"{rel(md)}:{start}: '// file:' used outside a Maven module")
            continue
        suffix = "/" + m.group(1).lstrip("/")
        matches = [p for p in (root / "src").rglob("*.java") if str(p).endswith(suffix)]
        if len(matches) != 1:
            errors.append(f"{rel(md)}:{start}: '// file: {m.group(1)}' matches {len(matches)} files in {rel(root)}")
            continue
        chunks, cur = [], []
        for l in lines[1:]:
            if l.strip() in ("// ...", "// …"):
                chunks.append(norm(cur))
                cur = []
            else:
                cur.append(l)
        chunks.append(norm(cur))
        source = norm(matches[0].read_text(encoding="utf-8").splitlines())
        missing = contains_in_order(chunks, source)
        if missing is not None:
            culprit = next((l for l in missing if l not in source), missing[0])
            errors.append(f"{rel(md)}:{start}: code block differs from {rel(matches[0])} near: {culprit!r}")


def main():
    files = list(markdown_files())
    for md in files:
        check_links(md)
        check_code(md)
        if md.name.endswith(".en.md"):
            check_parity(md, md.with_name(md.name[: -len(".en.md")] + ".tr.md"))
        elif md.name.endswith(".tr.md") and md != ROOT / "README.tr.md":
            if not md.with_name(md.name[: -len(".tr.md")] + ".en.md").exists():
                errors.append(f"{rel(md)}: missing English counterpart")
    readme = ROOT / "README.md"
    if (ROOT / "README.tr.md").exists():
        check_parity(readme, ROOT / "README.tr.md")
    for e in errors:
        print("✗", e)
    print(f"checked {len(files)} Markdown file(s): {len(errors)} problem(s)")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
