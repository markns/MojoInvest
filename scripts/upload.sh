#!/bin/bash

echo dummy_pass | appcfg.py upload_data \
	--email=marknuttallsmith@gmail.com \
	--passin \
	--config_file=config.yml \
	--filename="../data/etf-historical-data-ishares-small.csv" \
	--url=http://localhost:8080/remote_api \
	--application=position-engine \
	--kind=Quote \
	--batch_size=10

#	\
#	--db_filename=bulkloader-progress-20110717.153610.sql3
