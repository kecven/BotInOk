#!/bin/bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

OS_TYPE="$(uname -s)"
APP_NAME="BotInOk"
LINUX_JAR_PATTERN='build/libs/BotInOk-.*\.jar'
WINDOWS_JAR_PATH='build/libs/BotInOk-0.4.0.jar'

stop_running_linux_app() {
  local pids
  pids="$(pgrep -f "$LINUX_JAR_PATTERN" || true)"

  if [[ -z "$pids" ]]; then
    echo "No running $APP_NAME process found."
    return
  fi

  echo "Stopping running $APP_NAME process(es): $pids"
  kill $pids

  for _ in {1..10}; do
    sleep 1
    if ! pgrep -f "$LINUX_JAR_PATTERN" >/dev/null 2>&1; then
      echo "$APP_NAME process stopped."
      return
    fi
  done

  pids="$(pgrep -f "$LINUX_JAR_PATTERN" || true)"
  if [[ -n "$pids" ]]; then
    echo "Force killing $APP_NAME process(es): $pids"
    kill -9 $pids
  fi
}

find_linux_jar() {
  local jar_path
  jar_path="$(ls -t build/libs/BotInOk-*.jar 2>/dev/null | head -n 1 || true)"

  if [[ -z "$jar_path" ]]; then
    echo "Cannot find BotInOk jar in build/libs" >&2
    exit 1
  fi

  echo "$jar_path"
}

if [[ "$OS_TYPE" == "Linux" ]]; then
  stop_running_linux_app

  git pull
  ./gradlew bootJar

  JAR_PATH="$(find_linux_jar)"
  echo "Starting $APP_NAME from $JAR_PATH"

  java --module-path ./libs/linux-javafx-sdk-21.0.5/lib --add-modules javafx.controls -jar "$JAR_PATH" &
  echo "$APP_NAME started with PID $!"
elif [[ "$OS_TYPE" == MINGW* || "$OS_TYPE" == CYGWIN* ]]; then
  echo "Starting $APP_NAME on Windows"
  java.exe --module-path ./libs/windows-javafx-sdk-21.0.5/lib --add-modules javafx.controls -jar "$WINDOWS_JAR_PATH"
else
  echo "Неизвестная операционная система: $OS_TYPE"
  exit 1
fi
