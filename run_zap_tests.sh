#!/usr/bin/env bash
sbt \
  -Dbrowser=chrome \
  -Dzap.proxy=true \
  acceptance:test zap:test
