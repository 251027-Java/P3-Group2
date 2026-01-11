#!/usr/bin/bash

printenv | sort | sed 's/^/    /'
