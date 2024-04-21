#!/bin/bash
# set -euo
# DATASETS=("com-orkut.ungraph.json3")
# DATASETS=("twitter-2010.json3")
DATASETS=("wikipedia.json3")
# DATASETS=("com-orkut.ungraph.json3")
IMAGES=(dbtrento/gremlin-orientdb)
# ALGMS=(bfs.groovy)
# IMAGES=(dbtrento/gremlin-neo4j-tp3 dbtrento/gremlin-arangodb dbtrento/gremlin-pg dbtrento/gremlin-orientdb )
# ALGMS=(bfs.groovy random-walk.groovy get-and-add.groovy shortest-path-new.groovy ppr.groovy)
# ALGMS=(ppr.groovy)
# IMAGES=(dbtrento/gremlin-arangodb dbtrento/gremlin-pg dbtrento/gremlin-orientdb dbtrento/gremlin-janusgraph)

ALGMS=(allids.groovy)
# ALGMS=(get-and-add.groovy)

for dataset in "${DATASETS[@]}"
do
  for alg in "${ALGMS[@]}"
  do
    for img in "${IMAGES[@]}"
    do
      db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
      # echo $db_name
      ./test_baseline.sh $alg $dataset $img
      cp runtime/debug.log runtime/logs/${db_name}_${dataset}_${alg}.log
      cp runtime/errors.log runtime/logs/${db_name}_${dataset}_${alg}.errors.log
      sleep 5
    done
  done
done