#!/bin/bash
mkdir -p runtime/data

cd runtime/data

################################################
echo "Downloading datasets ..."
wget --no-check-certificate https://disi.unitn.it/\~brugnara/data/GraphDatabaseComparison_LissandriniBV_VLDB19.tar.gz
wget https://snap.stanford.edu/data/bigdata/communities/com-dblp.ungraph.txt.gz
wget https://snap.stanford.edu/data/cit-Patents.txt.gz
wget https://snap.stanford.edu/data/bigdata/communities/com-orkut.ungraph.txt.gz
wget https://snap.stanford.edu/data/wiki-Talk.txt.gz
wget http://konect.cc/files/download.tsv.wikipedia_link_fr.tar.bz2
wget https://snap.stanford.edu/data/twitch_gamers.zip
wget -nc https://snap.stanford.edu/data/twitter-2010.txt.gz
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

# wikitalk
gunzip -c wiki-Talk.txt.gz > wiki-Talk.txt
sed -i 's/\t/ /g' wiki-Talk.txt
tail -n +5 wiki-Talk.txt > wiki-Talk.txt.tmp
python3 ../../convert_graph.py --input=wiki-Talk.txt.tmp --output=wikitalk.json3
rm wiki-Talk.txt
rm wiki-Talk.txt.tmp

# wikipedia
tar -xvjf download.tsv.wikipedia_link_fr.tar.bz2 -C ./
sed -i 's/\t/ /g' wikipedia_link_fr/out.wikipedia_link_fr
tail -n +2 wikipedia_link_fr/out.wikipedia_link_fr > wikipedia.txt
python3 ../../convert_graph.py --input=wikipedia.txt --output=wikipedia.json3
rm wikipedia.txt

# twitch
ZIP="twitch_gamers.zip"
EXTRACT_DIR="twitch_gamers"
OUT_TXT="twitch.json3"

rm -rf "${EXTRACT_DIR}"
mkdir -p "${EXTRACT_DIR}"
unzip -q "${ZIP}" -d "${EXTRACT_DIR}"
edges_file=""
while IFS= read -r -d '' f; do
  # Heuristic: prefer files containing "edge" in the name
  if [[ -z "${edges_file}" ]]; then
    edges_file="$f"
  fi
  case "$(basename "$f" | tr '[:upper:]' '[:lower:]')" in
    *edge* ) edges_file="$f"; break ;;
  esac
done < <(find "${EXTRACT_DIR}" -type f -print0)
if [[ -z "${edges_file}" ]]; then
  echo "[ERROR] Could not find an edges file in ${EXTRACT_DIR}." >&2
  echo "        Please inspect the unzipped contents and set edges_file manually." >&2
fi
awk '!/^[[:space:]]*#/' "${edges_file}" \
| sed -E 's/\t/ /g; s/,/ /g' \
| awk '{
    # trim and collapse spaces
    gsub(/^[[:space:]]+|[[:space:]]+$/, "", $0);
    gsub(/[[:space:]]+/, " ", $0);
    if (length($0)>0) print $0;
}' > "${OUT_TXT}"

#twitter
set -euo pipefail
clean_stream() {
  sed -E '/^[[:space:]]*[#%]/d; s/\t/ /g; s/^[[:space:]]+//; s/[[:space:]]+$//; s/[[:space:]]+/ /g'
}
gzip -cd twitter-2010.txt.gz | clean_stream > twitter-2010.json3

cd ../..