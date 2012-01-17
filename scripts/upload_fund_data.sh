#!/bin/sh

echo ambersun | appcfg.py upload_data \
    --email=marknuttallsmith@gmail.com  \
    --passin \
	--config_file=config.yml \
	--application=mojoinvest \
    --kind=Fund \
    --filename="../data/ishares_funds.csv" \
    --url=http://localhost:8080/remote_api
