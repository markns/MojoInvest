#!/bin/sh

echo ambersun | appcfg.py upload_data \
    --email=marknuttallsmith@gmail.com  \
    --passin --application=mojoinvest \
    --kind=Fund --filename=../data/funds.bin \
    --url=http://localhost:8888/remote_api