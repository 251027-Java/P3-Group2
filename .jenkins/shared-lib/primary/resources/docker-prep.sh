#!/usr/bin/bash

# allow for cpu arch emulation
docker run --privileged --rm tonistiigi/binfmt --install all
