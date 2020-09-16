#!/usr/bin/env bash
sbt \
  -J-Xmx256m \
  -J-Xms256m \
  -Dservices.directory=testOnlyServices \
  run
