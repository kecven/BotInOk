#!/bin/bash

os_type=$(uname -s)

if [[ "$os_type" == "Linux" ]]; then
  go build -o botinok-win.exe main.go
elif [[ "$os_type" == "MINGW"* || "$os_type" == "CYGWIN"* ]]; then
  GOOS=windows GOARCH=amd64 go build -o botinok-win.exe main.go

else
    echo "Unknown OS: $os_type"
fi

