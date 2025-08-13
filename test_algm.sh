#!/bin/bash
# set -euo
test_once() {
  alg=$1
  dataset=$2
  img=$3
  rm -f docker.log && rm -f runtime/errors.log && rm -f runtime/debug.log
  cat<<EOF > runtime/data/tmp.json
  {
      "datasets": [
        "$dataset"
      ],
      "queries": [
          "$alg"
      ]
  }
EOF
  db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
  allidpath=/runtime/meta/metaid/${db_name}_${dataset}_allid.txt
  if [[ "$db_name" == *janusgraph ]]; then
    sleep 10
  fi
  python test.py -i $img \
  -r 1 \
  -e JAVA_OPTIONS="-Xms1G -Xmn128M -Xmx100G" \
  -e ALLIDPATH=$allidpath \
  -e DBNAME=$db_name \
  -e GRAPHNAME=$dataset \
  -e GRAPHMAPPING="/runtime/meta/graphid/$db_name.$dataset.mapping" \
  -e SOURCE_VERTEX=1 \
  -e DST_VERTEX=30 \
  -e BFS_SOURCE_VERTEX=28 \
  -s runtime/data/tmp.json \
  -d > /dev/null 2>&1

  cat runtime/debug.log
  echo "-------------"
}

# DATASETS=("cit-patents.json3")
# DATASETS=("wikitalk.json3")
DATASETS=("twitter-2010.json3")
IMAGES=(dbtrento/gremlin-neo4j-tp3)
# IMAGES=(dbtrento/gremlin-neo4j-tp3 dbtrento/gremlin-orientdb dbtrento/gremlin-janusgraph)
# IMAGES=(dbtrento/gremlin-neo4j-tp3 dbtrento/gremlin-orientdb dbtrento/gremlin-pg dbtrento/gremlin-janusgraph dbtrento/gremlin-arangodb)
# ALGMS=(pr.groovy cdlp.groovy wcc.groovy shortest-path-new.groovy bfs.groovy)
# IMAGES=(dbtrento/gremlin-arangodb)
# ALGMS=(bfs.groovy shortest-path-new.groovy)
ALGMS=(pr.groovy cdlp.groovy wcc.groovy)

# wiki-talk
# -e SOURCE_VERTEX=32822 \
# -e DST_VERTEX=33 \
# -e BFS_SOURCE_VERTEX=6765 \
# cit-patents
# -e SOURCE_VERTEX=3494505 \
# -e DST_VERTEX=754148 \
# -e BFS_SOURCE_VERTEX=3494505 \

for dataset in "${DATASETS[@]}"
do
  for img in "${IMAGES[@]}"
  do
    db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
    # echo $db_name
    for algm in "${ALGMS[@]}"
    do
      echo "testing $img on $dataset: algm=$algm ..." 
      test_once $algm $dataset $img
      cp runtime/debug.log runtime/logs/${db_name}_${dataset}_${alg}.log
      cp runtime/errors.log runtime/logs/${db_name}_${dataset}_${alg}.errors.log
    done
  done
done