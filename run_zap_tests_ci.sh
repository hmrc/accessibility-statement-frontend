#!/usr/bin/env bash
sbt -mem 8192 \
  -Dbrowser=chrome \
  -Dzap.proxy=true \
  -Dlocal.services.accessibility-statement-frontend.port=6001 \
  acceptance:test zap:test
