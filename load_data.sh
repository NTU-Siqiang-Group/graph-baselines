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
  python test.py -i $img \
    -r 1 \
    -e JAVA_OPTIONS="-Xms1G -Xmn128M -Xmx100G" \
    -e DBNAME=$db_name \
    -e GRAPHNAME=$dataset \
    -s runtime/data/tmp.json \
    --load_only \
    -d > /dev/null 2>&1
  cat runtime/errors.log
}

DATASETS=("cit-patents.json3")
# IMAGES=(dbtrento/gremlin-neo4j-tp3 dbtrento/gremlin-orientdb dbtrento/gremlin-arangodb dbtrento/gremlin-pg dbtrento/gremlin-janusgraph)
IMAGES=(dbtrento/gremlin-janusgraph)

for dataset in "${DATASETS[@]}"
do
  for img in "${IMAGES[@]}"
  do
    db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
    # echo $db_name
    test_once get-and-add.groovy $dataset $img
    cp runtime/debug.log runtime/logs/${db_name}_${dataset}_${alg}.log
    cp runtime/errors.log runtime/logs/${db_name}_${dataset}_${alg}.errors.log
  done
done