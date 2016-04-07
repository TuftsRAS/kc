## Protocol Amend Renewals [/research-sys/api/v1/protocol-amend-renewals/]

### Get Protocol Amend Renewals by Key [GET /research-sys/api/v1/protocol-amend-renewals/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"}

### Get All Protocol Amend Renewals [GET /research-sys/api/v1/protocol-amend-renewals/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"}
            ]

### Get All Protocol Amend Renewals with Filtering [GET /research-sys/api/v1/protocol-amend-renewals/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + id
            + protoAmendRenNumber
            + dateCreated
            + summary
            + protocolId
            + protocolNumber
            + sequenceNumber
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Protocol Amend Renewals [GET /research-sys/api/v1/protocol-amend-renewals/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters

            + _schema (required) - will instruct the endpoint to return a schema data structure for the resource

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            ${sampleSchema}
		
### Get Blueprint API specification for Protocol Amend Renewals [GET /research-sys/api/v1/protocol-amend-renewals/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Protocol Amend Renewals.md"
            transfer-encoding:chunked


### Update Protocol Amend Renewals [PUT /research-sys/api/v1/protocol-amend-renewals/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Protocol Amend Renewals [PUT /research-sys/api/v1/protocol-amend-renewals/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Protocol Amend Renewals [POST /research-sys/api/v1/protocol-amend-renewals/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Protocol Amend Renewals [POST /research-sys/api/v1/protocol-amend-renewals/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","protoAmendRenNumber": "(val)","dateCreated": "(val)","summary": "(val)","protocolId": "(val)","protocolNumber": "(val)","sequenceNumber": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Protocol Amend Renewals by Key [DELETE /research-sys/api/v1/protocol-amend-renewals/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Protocol Amend Renewals [DELETE /research-sys/api/v1/protocol-amend-renewals/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Protocol Amend Renewals with Matching [DELETE /research-sys/api/v1/protocol-amend-renewals/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + id
            + protoAmendRenNumber
            + dateCreated
            + summary
            + protocolId
            + protocolNumber
            + sequenceNumber


+ Response 204