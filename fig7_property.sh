#!/bin/bash
source "$(conda info --base)/etc/profile.d/conda.sh" > /dev/null 2>&1
conda activate py27 > /dev/null 2>&1

test_once() {
  alg=$1
  img=$2
  dataset=$3

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

  rm -f docker.log && rm -f runtime/errors.log && rm -f runtime/debug.log
  echo "testing $img with property ..." 
  db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
  metapath=/runtime/meta/metaid/${db_name}_${dataset}_sampleid.txt
  allidpath=/runtime/meta/metaid/${db_name}_${dataset}_allid.txt
  echo $img
  python test.py -i $img \
  -r 1 \
  -e JAVA_OPTIONS="-Xms1G -Xmn128M -Xmx100G" \
  -e METAPATH=$metapath \
  -e ALLIDPATH=$allidpath \
  -s runtime/data/tmp.json \
  -d > tmp.log 2>&1
}

algs=(
  delete-edge-property.groovy \
  delete-node-property.groovy \
  edge-specific-property-search.groovy \
  insert-edge-property.groovy \
  insert-node-property.groovy \
  node-property-search.groovy \
  update-edge-property.groovy \
  update-node-property.groovy
)

# targets=(gremlin-orientdb)
targets=(gremlin-neo4j-tp3 gremlin-janusgraph gremlin-arangodb gremlin-orientdb gremlin-pg)
#dataset=ldbc.json2
DATASET_ALIAS="${1:-null}"
resolve_dataset() {
  case "$1" in
    ldbc)       echo "ldbc.json2" ;;
    freebase)
                echo "freebase_large.json2" ;;
    *)
      echo "Unknown dataset alias: $1" >&2
      return 1
      ;;
  esac
}
dataset="$(resolve_dataset "$DATASET_ALIAS")"

rm fig7_property.dat

for t in "${targets[@]}"
do
  for alg in "${algs[@]}"
  do
    img=dbtrento/$t
    db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
    logfile=runtime/logs/${db_name}_${dataset}_${alg}.log
    errfile=runtime/logs/${db_name}_${dataset}_${alg}.errors.log
    test_once $alg $img $dataset
    cp runtime/debug.log $logfile
    cp runtime/errors.log $errfile

    python3 stats.py --path=$logfile -w $alg >> fig7_property.dat
  done
done