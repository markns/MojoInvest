#!/bin/sh

#while read line; do
#    cat ../../ETFData/historical/"$line".csv >> ../data/ishares_quotes.csv
#done < ../data/ishares.csv

while read line; do
    cat ../../ETFData/historical/"$line".csv >> ../data/ishares_quotes.csv
done < ../data/ishares.csv