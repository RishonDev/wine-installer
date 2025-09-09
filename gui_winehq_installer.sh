#!/bin/bash
# ======================================================
# Startup script for Wine Installer
# Make this executable: chmod +x run.sh
# ======================================================

# Get the directory of the script itself
DIR="$(cd "$(dirname "$0")" && pwd)"

# Use bundled custom JRE if it exists, otherwise fall back to system java
if [ -x "$DIR/runtimes/custom-jre/bin/java" ]; then
    JAVA="$DIR/runtimes/custom-jre/bin/java"
else
    JAVA=$(command -v java)
    if [ -z "$JAVA" ]; then
        echo "No Java found! Please install Java or bundle a JRE in runtimes/custom-jre."
        exit 1
    fi
fi

# Build classpath: main jar + all jars in lib/
CP="$DIR/app.jar"
if [ -d "$DIR/lib" ]; then
    for J in "$DIR/lib/"*.jar; do
        CP="$CP:$J"
    done
fi

# Run the app
exec "$JAVA" -cp "$CP" org.javaopensoft.WINE
