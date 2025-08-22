#!/bin/bash
source "$(conda info --base)/etc/profile.d/conda.sh" > /dev/null 2>&1
conda activate py27 > /dev/null 2>&1

test_once() {
  alg=$1
  dataset=$2
  img=$3

  rm -f docker.log && rm -f runtime/errors.log && rm -f runtime/debug.log
  echo "testing $img with $alg on $dataset ..." 
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
  metapath=/runtime/meta/metaid/${db_name}_${dataset}_sampleid.txt
  allidpath=/runtime/meta/metaid/${db_name}_${dataset}_allid.txt
  # graphmapping=/runtime/meta/graphid/${db_name}.${dataset}.mapping
  # if [[ $db_name == *"arango"* ]]; then
  #   graphmapping=/runtime/meta/graphid/gremlin-neo4j-tp3.${dataset}.mapping
  # fi
  graphmapping=$allidpath

  if [[ $dataset == *"cit-patents"* ]]; then
    # cit-patents
    SOURCE_VERTEX=3494505
    DST_VERTEX=754148
    BFS_SOURCE_VERTEX=3494505
  else
    SOURCE_VERTEX=32822
    DST_VERTEX=33
    BFS_SOURCE_VERTEX=6765
  fi
  echo $img
  python test.py -i $img \
  -r 1 \
  -e JAVA_OPTIONS="-Xms1G -Xmn128M -Xmx100G" \
  -e METAPATH=$metapath \
  -e ALLIDPATH=$allidpath \
  -e DBNAME=$db_name \
  -e GRAPHNAME=$dataset \
  -e GRAPHMAPPING=$graphmapping \
  -e SOURCE_VERTEX=$SOURCE_VERTEX \
  -e DST_VERTEX=$DST_VERTEX \
  -e BFS_SOURCE_VERTEX=$BFS_SOURCE_VERTEX \
  -s runtime/data/tmp.json \
  -d > tmp.log 2>&1
}

# targets=(gremlin-orientdb)
targets=(gremlin-neo4j-tp3 gremlin-orientdb gremlin-janusgraph gremlin-arangodb gremlin-pg)
# datasets=(com-dblp.ungraph.json3 com-orkut.ungraph.json3 ldbc.json2 freebase_large.json2)
dataset=cit-patents.json3
algms=(pr.groovy cdlp.groovy wcc.groovy shortest-path-new.groovy bfs.groovy)

for alg in "${algms[@]}"
do
  for db in "${targets[@]}"
  do
    img=dbtrento/$db
    db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
    logfile=runtime/logs/${db_name}_${dataset}_${alg}.groovy.log
    errfile=runtime/logs/${db_name}_${dataset}_${alg}.errors.log
    echo "logfile: $logfile, db_name: $db_name, img: $img, alg: $alg ..."
    test_once $alg $dataset $img
    cp runtime/debug.log $logfile
    cp runtime/errors.log $errfile
    python3 stats.py --path=$logfile -w $alg >> tab6.dat
  done
done