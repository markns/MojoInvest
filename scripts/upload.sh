#!/bin/bash

echo ambersun | appcfg.py upload_data \
	--email=marknuttallsmith@gmail.com \
	--passin \
	--config_file=config.yml \
	--filename="../data/etf-historical-data-ishares-small.csv" \
	--url=http://alphaposition.appspot.com/remote_api \
	--application=s~alphaposition \
	--kind=Quote

#	--db_filename=bulkloader-progress-20110717.153610.sql3
