#!/bin/bash
source "$(conda info --base)/etc/profile.d/conda.sh" > /dev/null 2>&1
conda activate py27 > /dev/null 2>&1

test_once() {
  alg=$1
  dataset=$2
  img=$3
  rops=$4
  wops=$5

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
  -e rops=$rops \
  -e wops=$wops \
  -s runtime/data/tmp.json \
  -d > tmp.log 2>&1
}

# targets=(gremlin-janusgraph)
# targets=(gremlin-orientdb gremlin-janusgraph gremlin-arangodb gremlin-pg)
targets=(gremlin-neo4j-tp3 gremlin-arangodb gremlin-pg gremlin-orientdb gremlin-janusgraph)
# datasets=(com-dblp.ungraph.json3 com-orkut.ungraph.json3)
dataset=com-dblp.ungraph.json3

rm fig6.dat # clean result
echo "db,get_neighbors,add_edge"
for t in "${targets[@]}"
do
  echo "Testing fig6 experiment for $t ..."
  img=dbtrento/$t
  db_name=$(IFS='/' read -ra strs <<< "$img"; echo ${strs[1]})
  for rratio in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9
  # for rratio in 0.5
  do
    rop=$(python3 -c "print(int(1000000*$rratio))")
    wop=$(python3 -c "print(int(1000000*(1-$rratio)))")
    logfile=runtime/logs/${db_name}_${dataset}_get-and-add.groovy.log
    errfile=runtime/logs/${db_name}_${dataset}_get-and-add.errors.log
    echo "logfile: $logfile, db_name: $db_name, img: $img ..."
    test_once get-and-add.groovy $dataset $img $rop $wop
    cp runtime/debug.log $logfile
    cp runtime/errors.log $errfile
    python3 stats.py --path=$logfile -w get-and-add.groovy >> fig6.dat
  done
done