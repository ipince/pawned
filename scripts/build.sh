#!/bin/bash
set -e

cd "$(dirname "$0")/.."

OUT=build/classes
rm -rf "$OUT"
mkdir -p "$OUT"

# Compile all sources except the SWT-dependent GUI and test files
find src -name "*.java" \
  -not -path "src/interfaces/GraphicUI.java" \
  -not -path "*/test/*" \
  -not -name "AllTests.java" \
  -not -name "DebugInfo.java" \
  | xargs javac -encoding ISO-8859-1 -d "$OUT" -sourcepath src

echo "Build successful."
