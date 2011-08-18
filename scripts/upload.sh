#!/bin/bash

echo ambersun | appcfg.py upload_data \
	--email=marknuttallsmith@gmail.com \
	--passin \
	--config_file=config.yml \
	--filename="../data/etf-historical-data-ishares-small.csv" \
	--url=http://mojointest.appspot.com/remote_api \
	--application=mojointest \
	--kind=Quote \
	--db_filename=bulkloader-progress-20110805.195103.sql3
