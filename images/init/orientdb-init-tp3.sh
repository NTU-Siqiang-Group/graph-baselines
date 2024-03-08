#!/bin/bash
# Specific OrientDB dataset loader (via gremlin is too slow)
# maintainer: Lissandrini, Brugnara
set -exuo pipefail
IFS=$'\n\t'


if [[ -z ${JAVA_OPTIONS+x} ]]; then
   echo "NO JAVA_OPTIONS SET - SETTING DEFAULT"
   export JAVA_OPTIONS='-Xms4g -Xmx20g -XX:+UseG1GC -Dstorage.diskCache.bufferSize=102400'
fi

echo "JAVA_OPTIONS=$JAVA_OPTIONS"


if [[ -z ${ORIENTDB_OPTS_MEMORY+x} ]]; then
  echo "NO ORIENTDB_OPTS_MEMORY"
else
  echo "ORIENTDB_OPTS_MEMORY=${ORIENTDB_OPTS_MEMORY}"
fi


# NOTE: now (2.2.13)
# ISSUE OPEN:  https://github.com/orientechnologies/orientdb/issues/6577
# The following use native loader


if [[ "$QUERY" == *loader.groovy ]]; then
    # TODO: refactor, gen outise $RUNTIME_DIR, rm after run

    echo "OrientDB (removing slash for) loading $DATASET" | tee -a ${RUNTIME_DIR}/errors.log
    DATASET_NAME=$(basename "${DATASET}")
    SAFE_DATASET="/tmp/${DATASET_NAME}_noslash.json"

    # sed '/\// s//__/g' "$DATASET"  > "$SAFE_DATASET"
    perl -pe "s/\//__/g" < "$DATASET" > "$SAFE_DATASET"
    echo "Created $SAFE_DATASET" | tee -a ${RUNTIME_DIR}/errors.log
    ls -lh ${SAFE_DATASET} | tee -a ${RUNTIME_DIR}/errors.log

    # database path (/srv/db) must be the same as DB_FILE in $RUNTIME_DIR/tp2/header.groovy.sh
    if [[ -z ${MINIMUMCLUSTERS+x} ]]; then
      echo "Disable MINIMUM CLUSTERS"  | tee -a ${RUNTIME_DIR}/errors.log
      echo "CONNECT ENV embedded:/srv admin admin; CREATE DATABASE db plocal users (admin identified by 'admin' role admin);ALTER DATABASE minimumclusters 1 ;QUIT" | "$ORIENTDB_HOME"/bin/console.sh >> ${RUNTIME_DIR}/errors.log
    else
      echo "CONNECT ENV embedded:/srv admin admin; CREATE DATABASE db plocal users (admin identified by 'admin' role admin);QUIT" | "$ORIENTDB_HOME"/bin/console.sh >> ${RUNTIME_DIR}/errors.log
    fi

    if [[ -z ${NATIVE_LOADING+x} ]]; then
        echo "Not load with native loader"
        echo "Load with gremlin for ${SAFE_DATASET}"
        DATASET=${SAFE_DATASET}
    else
        echo "load with native loader"
        export NATIVE_LOADING=True
        echo "Start loading"
        # https://stackoverflow.com/questions/8903239/how-to-calculate-time-difference-in-bash-script
        SECONDS=0
        echo "CONNECT ENV embedded:/srv admin admin;OPEN db admin admin;IMPORT DATABASE ${SAFE_DATASET} -format=graphson -merge=true;QUIT" | time "$ORIENTDB_HOME"/bin/console.sh
        end_time=$SECONDS
        echo  "${DATABASE},${DATASET},${QUERY},,,,${SECONDS},native" | tee -a ${RUNTIME_DIR}/results.csv
        echo "Done loading" >> ${RUNTIME_DIR}/errors.log
    fi
fi

. ${RUNTIME_DIR}/tp3/execute.sh

if [[ "$QUERY" == *loader.groovy ]]; then
    if [[ -z ${DEBUG+x} ]]; then
        echo "Not in debug mode: removing converted dataset"
        rm -v "${SAFE_DATASET}"
    fi
fi
echo 'Done'
