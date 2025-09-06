#!/bin/bash
APP_NAME="WineHQInstaller"
MAIN_CLASS="org.javaopensoft.Main"
JAR="target/winehq-installer-gui-1.0-SNAPSHOT.jar"

jpackage \
  --type app-image \
  --input target \
  --name "$APP_NAME" \
  --main-jar "$(basename $JAR)" \
  --main-class "$MAIN_CLASS" \
  --icon src/main/resources/icon.png \
  --runtime-image $(jlink --add-modules java.base,java.desktop --output custom-runtime)

echo "✅ Built $APP_NAME (Linux ELF binary wrapper)"
