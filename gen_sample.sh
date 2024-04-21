#!/bin/bash
# set -euo
DATASETS=("com-orkut.ungraph.json3" "com-dblp.ungraph.json3")
IMAGES=(dbtrento/gremlin-neo4j-tp3 dbtrento/gremlin-janusgraph dbtrento/gremlin-arangodb dbtrento/gremlin-pg dbtrento/gremlin-orientdb)

for dataset in "${DATASETS[@]}"
do
  for img in "${IMAGES[@]}"
  do
    db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
    logfile=runtime/logs/${db_name}_${dataset}_sample.groovy.log
    python3 stats.py --path=$logfile -w gen_sample
    # echo $logfile
  done
done