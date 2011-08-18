#!/bin/bash

	echo ambersun | appcfg.py upload_data \
	--email=marknuttallsmith@gmail.com \
	--passin \
	--config_file=config.yml \
	--filename="../data/etf-static-ishares-small.csv" \
	--url=http://mojointest.appspot.com/remote_api \
	--application=mojointest \
	--kind=Fund
