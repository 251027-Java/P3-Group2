#!/usr/bin/sh

printenv | sort | sed 's/^/    /'
