#!/bin/sh

echo ambersun | appcfg.py upload_data ^
    --email=marknuttallsmith@gmail.com  ^
    --passin ^
    --application=mojointest ^
    --kind=Fund ^
    --filename=../data/funds.csv ^
    --url=http://localhost:8080/remote_api