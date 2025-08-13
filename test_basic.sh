#!/bin/bash
# set -euo
test_once() {
  alg=$1
  dataset=$2
  img=$3
  rops=$4
  wops=$5
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
  metapath=/runtime/meta/metaid/${db_name}_${dataset}_sampleid.txt
  allidpath=/runtime/meta/metaid/${db_name}_${dataset}_allid.txt
  python test.py -i $img \
  -r 1 \
  -e JAVA_OPTIONS="-Xms1G -Xmn128M -Xmx100G" \
  -e METAPATH=$metapath \
  -e ALLIDPATH=$allidpath \
  -e DBNAME=$db_name \
  -e GRAPHNAME=$dataset \
  -e rops=$rops \
  -e wops=$wops \
  -s runtime/data/tmp.json \
  -d > /dev/null 2>&1

  python3 stats.py --path=runtime/debug.log -w $alg
}

# DATASETS=("com-dblp.ungraph.json3")
# DATASETS=('wikipedia.json3')
DATASETS=("wikipedia.json3")
# DATASETS=('wikipedia.json3' "com-orkut.ungraph.json3")
# IMAGES=(dbtrento/gremlin-orientdb)
# IMAGES=(dbtrento/gremlin-orientdb)
IMAGES=(dbtrento/gremlin-orientdb dbtrento/gremlin-arangodb dbtrento/gremlin-pg dbtrento/gremlin-janusgraph)

# ratios=(0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9)
ratios=(0.5)

for dataset in "${DATASETS[@]}"
do
  for img in "${IMAGES[@]}"
  do
    db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
    # echo $db_name
    for ratio in "${ratios[@]}"
    do
      rop=$(python3 -c "print(int(000000*$ratio))")
      wop=$(python3 -c "print(int(000000*(1-$ratio)))")
      echo "testing $img on $dataset: rops=$rop, wops=$wop ..." 
      test_once get-and-add.groovy $dataset $img $rop $wop
      cp runtime/debug.log runtime/logs/${db_name}_${dataset}_${alg}.log
      cp runtime/errors.log runtime/logs/${db_name}_${dataset}_${alg}.errors.log
    done
  done
done