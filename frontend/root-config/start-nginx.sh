#!/bin/sh

# obtains only the environment variables that start with a given prefix and formats it
# to string matching: "$PREFIX_<name1>,$PREFIX_<name2>,..."
prefix="MKPL"
target_file="importmap.json"
root_dir="/usr/share/nginx/html"

target_env_vars=$(printenv | awk -F= '{print $1}' | grep "^$prefix" | sed 's/^/\$/g' | paste -sd,)

if [ -n "$target_env_vars" ]; then
    files=$(find "$root_dir" -type f -name "$target_file")

    # there should only be one file
    for file in $files; do
        # https://www.gnu.org/software/gettext/manual/html_node/envsubst-Invocation.html
        # updates the file by performing substitution
        tmp=$(mktemp)
        chmod "$(stat -c "%a" $file)" $tmp
        envsubst $target_env_vars < "$file" > "$tmp"
        mv "$tmp" "$file"
    done
fi

nginx -g 'daemon off;'
