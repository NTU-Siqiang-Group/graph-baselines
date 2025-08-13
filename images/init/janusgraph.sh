#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

if [[ -z ${JAVA_OPTIONS+x} ]]; then
   echo "NO JAVA_OPTIONS SET - SETTING DEFAULT"
   export JAVA_OPTIONS='-Xms4g -Xmx400g -XX:+UseG1GC -Dstorage.diskCache.bufferSize=102400'
fi

echo $JAVA_OPTIONS >> $JANUS_HOME/conf/janus-jvm.options
export JAVA_OPTIONS_FILE=$JANUS_HOME/conf/janus-jvm.options
cat $JANUS_HOME/conf/janus-jvm.options
export JANUS_CONF=$JANUS_HOME/conf/gremlin-server/gremlin-server-berkeleyje.yaml
export JANUS_PROPERTIES=$JANUS_HOME/conf/janusgraph-berkeleyje.properties
# start server
$JANUS_HOME/bin/janusgraph-server.sh start $JANUS_CONF

# wait for janusgraph start
sleep 20
rm -f $JANUS_HOME/conf/../db/berkeley/je.lck

. ${RUNTIME_DIR}/tp3/execute.sh

if [[ "$QUERY" == *loader.groovy ]]; then
    if [[ -z ${DEBUG+x} ]]; then
        echo "Not in debug mode: removing converted dataset"
        # rm -v "${SAFE_DATASET}"
    fi
fi