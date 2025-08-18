#!/bin/bash
targets=(gremlin-neo4j-tp3 gremlin-arangodb gremlin-pg gremlin-orientdb gremlin-janusgraph)
cd images

if [[ ! -f libs/janusgraph-1.0.0.zip ]]; then
  wget -P libs https://github.com/JanusGraph/janusgraph/releases/download/v1.0.0/janusgraph-1.0.0.zip
fi

for t in "${targets[@]}"
do
  if docker image inspect "dbtrento/$t" >/dev/null 2>&1; then
    echo "Image '$t' already exists. Skipping."
    continue
  fi
  errorlog=build_image_$t.err.log
  stdout=build_image_$t.log
  echo "building $t.dockerfile ..."
  make $t.dockerfile >$stdout 2>$errorlog
done

cd ..