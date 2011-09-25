#!/bin/sh

while read line; do
    cat ../../ETFData/historical/"$line".csv >> vaneck.csv
done < ishares.txt