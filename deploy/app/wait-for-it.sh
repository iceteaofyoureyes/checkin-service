#!/usr/bin/env bash
set -e

host="$1"
port="$2"
shift 2
cmd="$@"

until nc -z -w 30 "$host" "$port"; do
  echo "Waiting for $host:$port..."
  sleep 2
done

echo "$host:$port is available, executing command..."
exec $cmd