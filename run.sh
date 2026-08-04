#!/usr/bin/env sh
set -e
cd "$(dirname "$0")"
if [ ! -f target/javicon-browser.jar ]; then
    echo "Building project..."
    mvn -q package
fi
java -jar target/javicon-browser.jar