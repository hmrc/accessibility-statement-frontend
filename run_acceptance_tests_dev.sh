#!/usr/bin/env bash
sbt -mem 8192 \
  -Dbrowser=remote-chrome \
  -Denvironment=dev \
  acceptance:test
