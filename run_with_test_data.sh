#!/usr/bin/env bash
sbt \
  -J-Xmx256m \
  -J-Xms256m \
  -Dfeatures.use-test-data=true \
  -Dapplication.router=testOnlyDoNotUseInAppConf.Routes \
  run
