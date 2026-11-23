#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p out
find src/main/java -name '*.java' -print0 | xargs -0 javac -d out
exec java -cp out kz.support.escalation.Main
