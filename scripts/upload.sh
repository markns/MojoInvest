#!/bin/bash

echo ambersun | appcfg.py upload_data \
	--email=marknuttallsmith@gmail.com \
	--passin \
	--config_file=config.yml \
	--filename="../data/wisdomtree_quotes.csv" \
	--url=http://localhost:8888/remote_api \
	--application=mojoinvest \
	--kind=Quote

