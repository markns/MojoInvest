#!/bin/bash
echo "Bash version ${BASH_VERSION}..."
for i in {2011..1990}
do
   head -1 ../data/etf-historical-data.csv > ../data/etf-historical-data-$i.csv
   grep "$i-" ../data/etf-historical-data.csv >> ../data/etf-historical-data-$i.csv
done
