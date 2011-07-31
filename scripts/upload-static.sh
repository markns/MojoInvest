#!/bin/bash

	echo ambersun | appcfg.py upload_data \
	--email=marknuttallsmith@gmail.com \
	--passin \
	--config_file=config.yml \
	--filename="../data/etf-static-ishares-small.csv" \
	--url=http://alphaposition.appspot.com/remote_api \
	--application=s~alphaposition \
	--kind=Fund
