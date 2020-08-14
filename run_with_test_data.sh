#!/usr/bin/env bash
sbt \
  -J-Xmx256m \
  -J-Xms256m \
  -Dconfig.resource=testOnlyApplication.conf \
  -Dapplication.router=testOnlyDoNotUseInAppConf.Routes \
  run
