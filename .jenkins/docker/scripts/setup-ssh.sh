#!/usr/bin/bash

rm -rf $HOME/.ssh

if [ -e "/custom/ssh" ]; then
    echo "user ssh files found, copying into ~/.ssh"
    
    cp -r /custom/ssh $HOME/.ssh
    chmod 600 $HOME/.ssh/*
    
    # ensure files do not have the '\r' line endings (important files are from windows)
    find $HOME/.ssh -type f -exec sed -i 's/\r$//' {} +

    echo "finished copying ssh files"
fi

if [ -f "$HOME/.ssh/config" ]; then
    echo "ssh config found, creating known_hosts"

    config_file=$HOME/.ssh/config
    known_hosts_file=$HOME/.ssh/known_hosts

    get-all-hosts() {
        grep -E '^Host ' $config_file | awk '{print $2}'
    }

    get-port() {
        local host=$1
        ssh -G "$host" -F $config_file | awk '/^port / {print $2}'
    }

    get-hostname() {
        local host=$1
        ssh -G "$host" -F $config_file | awk '/^hostname / {print $2}'
    }

    add-known-host() {
        local ssh_hostname=$1
        local port=$2
        ssh-keyscan -H -p "$port" "$ssh_hostname" >> $known_hosts_file
    }

    rm -rf $known_hosts_file
    touch $known_hosts_file

    hosts=$(get-all-hosts)

    for host in "${hosts[@]}"; do
        ssh_hostname=$(get-hostname $host)
        port=$(get-port $host)

        echo "adding $ssh_hostname | $port"

        add-known-host $ssh_hostname $port
    done

    echo "finished generating known_hosts"
fi
