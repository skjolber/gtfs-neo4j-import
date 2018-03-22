#!/bin/bash
set -e

$1/bin/neo4j start

# wait for neo4j to start
end="$((SECONDS+30))"
while true; do
    [[ "200" = "$(curl --silent --write-out %{http_code} --output /dev/null http://localhost:7474)" ]] && break
    [[ "${SECONDS}" -ge "${end}" ]] && exit 1
    sleep 1
done

# run init script
while IFS='' read -r line || [[ -n "$line" ]]; do
	echo $line
	curl -X POST -H 'Content-type: application/json' http://neo4j:$3@localhost:7474/db/data/transaction/commit -d '{"statements": [{"statement": "'"$line"'"}]}'
	echo
done < "$2"

$1/bin/neo4j stop

