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
  echo $img
  python test.py -i $img \
  -r 1 \
  -e JAVA_OPTIONS="-Xms1G -Xmn128M -Xmx100G" \
  -e METAPATH=$metapath \
  -e ALLIDPATH=$allidpath \
  -e DBNAME=$db_name \
  -e GRAPHNAME=$dataset \
  -s runtime/data/tmp.json \
  -d > tmp.log 2>&1

  python3 stats.py --path=runtime/debug.log -w $alg
}

# targets=(gremlin-neo4j-tp3)
targets=(gremlin-neo4j-tp3 gremlin-orientdb gremlin-janusgraph gremlin-arangodb gremlin-pg)
# datasets=(com-dblp.ungraph.json3 com-orkut.ungraph.json3 ldbc.json2 freebase_large.json2)
# dataset=com-dblp.ungraph.json3
# dataset=ldbc.json2
# dataset=cit-patents.json3
datasets=(com-dblp.ungraph.json3 com-orkut.ungraph.json3 wikipedia.json3 twitter-2010.json3)

for dataset in "${datasets[@]}"
do
  for t in "${targets[@]}"
  do
    echo "Preparing data for $t ..."
    img=dbtrento/$t
    db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
    logfile=runtime/logs/${db_name}_${dataset}_allids.groovy.log
    errfile=runtime/logs/${db_name}_${dataset}_allids.errors.log
    echo "logfile: $logfile, db_name: $db_name, img: $img ..."
    test_once allids.groovy $dataset $img
    cp runtime/debug.log $logfile
    cp runtime/errors.log $errfile
    python3 stats.py --path=$logfile -w get_all_ids
  done
done