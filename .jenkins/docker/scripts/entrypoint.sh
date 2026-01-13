#!/usr/bin/bash

./setup-ssh.sh

# the default entrypoint for jenkins with -s
# -s is to resolve:
# [WARN  tini (22)] Tini is not running as PID 1 and isn't registered as a child subreaper.
# Zombie processes will not be re-parented to Tini, so zombie reaping won't work.
/usr/bin/tini -s -- /usr/local/bin/jenkins.sh
