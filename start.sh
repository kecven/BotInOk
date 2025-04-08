#!/bin/bash

# Определяем операционную систему
os_type=$(uname -s)
cd $(dirname $0)
set -Eeuo pipefail

# Проверка ОС
if [[ "$os_type" == "Linux" ]]; then
  git pull
  ./gradlew bootJar

  java --module-path ./libs/linux-javafx-sdk-21.0.5/lib --add-modules javafx.controls -jar build/libs/BotInOk-0.3.0.jar &
elif [[ "$os_type" == "MINGW"* || "$os_type" == "CYGWIN"* ]]; then
#    ./gradlew.bat build
    java.exe --module-path ./libs/windows-javafx-sdk-21.0.5/lib --add-modules javafx.controls -jar build/libs/BotInOk-0.3.0.jar

else
    echo "Неизвестная операционная система: $os_type"
fi
