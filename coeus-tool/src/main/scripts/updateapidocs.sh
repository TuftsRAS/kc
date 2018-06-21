#!/bin/bash

cd ../../../../coeus-webapp/src/main/jsfrontend/apidocs/

curl -X GET $1/award/api/v1/endpoints/?_blueprint= -o award.zip -H "Authorization: Bearer $2"
unzip -o -d award award.zip
rm award.zip

curl -X GET $1/research-common/api/v1/endpoints/?_blueprint= -o common.zip -H "Authorization: Bearer $2"
unzip -o -d common common.zip
rm common.zip

curl -X GET $1/iacuc/api/v1/endpoints/?_blueprint= -o iacuc.zip -H "Authorization: Bearer $2"
unzip -o -d iacuc iacuc.zip
rm iacuc.zip

curl -X GET $1/instprop/api/v1/endpoints/?_blueprint= -o instprop.zip -H "Authorization: Bearer $2"
unzip -o -d instprop instprop.zip
rm instprop.zip

curl -X GET $1/irb/api/v1/endpoints/?_blueprint= -o irb.zip -H "Authorization: Bearer $2"
unzip -o -d irb irb.zip
rm irb.zip

curl -X GET $1/negotiation/api/v1/endpoints/?_blueprint= -o negotiation.zip -H "Authorization: Bearer $2"
unzip -o -d negotiation negotiation.zip
rm negotiation.zip

curl -X GET $1/propdev/api/v1/endpoints/?_blueprint= -o propdev.zip -H "Authorization: Bearer $2"
unzip -o -d propdev propdev.zip
rm propdev.zip

curl -X GET $1/subaward/api/v1/endpoints/?_blueprint= -o subaward.zip -H "Authorization: Bearer $2"
unzip -o -d subaward subaward.zip
rm subaward.zip

curl -X GET $1/research-sys/api/v1/endpoints/?_blueprint= -o sys.zip -H "Authorization: Bearer $2"
unzip -o -d sys sys.zip
rm sys.zip
