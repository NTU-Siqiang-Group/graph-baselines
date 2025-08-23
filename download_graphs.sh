#!/bin/bash
mkdir -p runtime/data

cd runtime/data

################################################
echo "Downloading datasets ..."
wget --no-check-certificate https://disi.unitn.it/\~brugnara/data/GraphDatabaseComparison_LissandriniBV_VLDB19.tar.gz
wget https://snap.stanford.edu/data/bigdata/communities/com-dblp.ungraph.txt.gz
wget https://snap.stanford.edu/data/cit-Patents.txt.gz
wget https://snap.stanford.edu/data/bigdata/communities/com-orkut.ungraph.txt.gz
################################################

echo "Unzip datasets ..."

# DBLP
tar -xvzf GraphDatabaseComparison_LissandriniBV_VLDB19.tar.gz -C ./
gunzip -c com-dblp.ungraph.txt.gz > com-dblp.ungraph.txt
sed -i 's/\t/ /g' com-dblp.ungraph.txt
tail -n +5 com-dblp.ungraph.txt > com-dblp.ungraph.json3
rm com-dblp.ungraph.txt

# cit-patents
gunzip -c cit-Patents.txt.gz > cit-Patents.txt
sed -i 's/\t/ /g' cit-Patents.txt
tail -n +5 cit-Patents.txt > cit-patents.txt
python3 ../../convert_graph.py --input=cit-patents.txt --output=cit-patents.json3
rm cit-Patents.txt
rm cit-patents.txt

# orkut
gunzip -c com-orkut.ungraph.txt.gz > com-orkut.ungraph.txt
sed -i 's/\t/ /g' com-orkut.ungraph.txt
tail -n +5 com-orkut.ungraph.txt > com-orkut.ungraph.txt.tmp
python3 ../../convert_graph.py --input=com-orkut.ungraph.txt.tmp --output=com-orkut.ungraph.json3
rm com-orkut.ungraph.txt
rm com-orkut.ungraph.txt.tmp

cd ../..