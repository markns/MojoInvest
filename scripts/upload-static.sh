#!/bin/bash

	echo dummy_pass | appcfg.py upload_data \
	--email=marknuttallsmith@gmail.com \
	--passin \
	--config_file=config.yml \
	--filename="../data/etf-static-ishares-small.csv" \
	--url=http://localhost:8080/remote_api \
	--application=position-engine \
	--kind=Fund
