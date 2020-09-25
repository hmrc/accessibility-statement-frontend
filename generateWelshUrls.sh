#!/usr/bin/env bash

ls conf/services | grep -v example | grep \.cy\.yml | sed -E 's/([a-z0-9-]+)\.cy\.yml/https:\/\/www.tax.service.gov.uk\/accessibility-statement\/\1/'

