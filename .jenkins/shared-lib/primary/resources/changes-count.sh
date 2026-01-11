#!/usr/bin/bash

ref=$1
pattern=$2

git --no-pager diff --name-only $ref..HEAD | grep -c "$pattern" || true
