#!/bin/bash
set -e

cd "$(dirname "$0")/.."

OUT=build/classes
rm -rf "$OUT"
mkdir -p "$OUT"

JUNIT_CP="lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar"

# Compile all sources except the SWT-dependent GUI
find src -name "*.java" \
  -not -path "src/interfaces/GraphicUI.java" \
  | xargs javac -encoding ISO-8859-1 -d "$OUT" -sourcepath src -cp "$JUNIT_CP"

# Copy test resources (script files, XML fixtures)
cp -r src/interfaces/test/script "$OUT/interfaces/test/" 2>/dev/null || true
cp src/interfaces/test/*.xml "$OUT/interfaces/test/" 2>/dev/null || true
cp src/controller/test/*.xsd "$OUT/controller/test/" 2>/dev/null || true
cp src/controller/test/*.xml "$OUT/controller/test/" 2>/dev/null || true

echo "Build successful."
