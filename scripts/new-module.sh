#!/usr/bin/env bash
# Scaffolds a course module from the templates and registers it in the parent POM.
# usage: scripts/new-module.sh <module-id>        e.g. m03-creational-construction
set -euo pipefail
cd "$(dirname "$0")/.."

id=${1:?usage: $0 <module-id>}
[[ $id =~ ^(m[0-9]{2})-[a-z0-9-]+$ ]] || { echo "module id must look like mNN-some-name"; exit 2; }
mnn=${BASH_REMATCH[1]}
dir=modules/$id
[[ -e $dir ]] && { echo "$dir already exists"; exit 1; }

pkg=io/github/aliturgutbozkurt/patterns/$mnn
mkdir -p "$dir"/{lesson/img,assignments} "$dir/src/main/java/$pkg"/{examples,exercises,solutions} \
         "$dir/src/test/java/$pkg"/{examples,exercises,solutions}

cat > "$dir/pom.xml" <<POM
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>io.github.aliturgutbozkurt.patterns</groupId>
        <artifactId>design-patterns-in-java</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../../pom.xml</relativePath>
    </parent>

    <artifactId>$id</artifactId>
</project>
POM

for area in examples exercises solutions; do
  cat > "$dir/src/main/java/$pkg/$area/package-info.java" <<JAVA
/** Module $id — $area. */
package io.github.aliturgutbozkurt.patterns.$mnn.$area;
JAVA
done

cp docs/templates/lesson.md "$dir/lesson/lesson.en.md"
cp docs/templates/lesson.md "$dir/lesson/lesson.tr.md"
cat > "$dir/README.md" <<MD
# $id

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) <!-- · [PDF](lesson/lesson.en.pdf) --> | [Markdown](lesson/lesson.tr.md) <!-- · [PDF](lesson/lesson.tr.pdf) --> |
| Spec | [SPEC-$id](../../specs/SPEC-$id.md) | |

## Run / Çalıştır

\`\`\`bash
./mvnw -q -pl $dir verify                     # build + tests
./mvnw -pl $dir test -Pexercises              # your exercise tests / ödev testleriniz
\`\`\`
MD

# Register in parent POM (before </modules>), keeping existing order.
python3 - "$dir" <<'PY'
import pathlib, sys
p = pathlib.Path("pom.xml"); s = p.read_text()
entry = f"        <module>{sys.argv[1]}</module>\n"
if entry not in s:
    s = s.replace("    </modules>", entry + "    </modules>", 1)
    p.write_text(s)
PY
echo "✓ scaffolded $dir (registered in pom.xml)"
