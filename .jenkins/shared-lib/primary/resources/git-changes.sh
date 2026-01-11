#!/usr/bin/sh

ref=$1

git --no-pager diff --name-only $ref..HEAD
