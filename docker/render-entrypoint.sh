#!/bin/sh
set -eu

APP_PORT="${PORT:-10000}"

sed -i \
  "s|port=\"8080\" protocol=\"HTTP/1.1\"|port=\"${APP_PORT}\" protocol=\"HTTP/1.1\"|" \
  "${CATALINA_HOME}/conf/server.xml"

exec catalina.sh run
