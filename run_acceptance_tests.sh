#!/usr/bin/env bash
sbt \
  -Dapplication.router=testOnlyDoNotUseInAppConf.Routes \
  -Dbrowser=chrome \
  acceptance:test
