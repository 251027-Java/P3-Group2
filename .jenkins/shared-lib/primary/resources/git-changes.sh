#!/usr/bin/bash

ref=$1

git --no-pager diff --name-only $ref..HEAD
