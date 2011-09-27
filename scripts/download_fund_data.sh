#!/bin/sh

appcfg.py \
download_data --application=mojointest --kind=Fund \
--url=http://mojointest.appspot.com/remote_api --filename=../data/funds.bin