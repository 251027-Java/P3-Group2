#!/usr/bin/sh

git ls-remote --symref origin HEAD | awk '/^ref:/ {sub("refs/heads/", "", $2); print $2}'
