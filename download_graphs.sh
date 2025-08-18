#!/bin/bash
mkdir -p runtime/data

cd runtime/data

################################################
echo "Downloading datasets ..."
wget --no-check-certificate https://disi.unitn.it/\~brugnara/data/GraphDatabaseComparison_LissandriniBV_VLDB19.tar.gz
wget https://snap.stanford.edu/data/bigdata/communities/com-dblp.ungraph.txt.gz
################################################

echo "Unzip datasets ..."

tar -xvzf GraphDatabaseComparison_LissandriniBV_VLDB19.tar.gz -C ./
gunzip -c com-dblp.ungraph.txt.gz > com-dblp.ungraph.json3

cd ../..