## Nsf Codes [/research-sys/api/v1/nsf-codes/]

### Get Nsf Codes by Key [GET /research-sys/api/v1/nsf-codes/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"}

### Get All Nsf Codes [GET /research-sys/api/v1/nsf-codes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]

### Get All Nsf Codes with Filtering [GET /research-sys/api/v1/nsf-codes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + nsfSequenceNumber
            + nsfCode
            + description
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Nsf Codes [GET /research-sys/api/v1/nsf-codes/]
	 
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
		
### Get Blueprint API specification for Nsf Codes [GET /research-sys/api/v1/nsf-codes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Nsf Codes.md"
            transfer-encoding:chunked


### Update Nsf Codes [PUT /research-sys/api/v1/nsf-codes/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Nsf Codes [PUT /research-sys/api/v1/nsf-codes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Nsf Codes [POST /research-sys/api/v1/nsf-codes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Nsf Codes [POST /research-sys/api/v1/nsf-codes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"nsfSequenceNumber": "(val)","nsfCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Nsf Codes by Key [DELETE /research-sys/api/v1/nsf-codes/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Nsf Codes [DELETE /research-sys/api/v1/nsf-codes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Nsf Codes with Matching [DELETE /research-sys/api/v1/nsf-codes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + nsfSequenceNumber
            + nsfCode
            + description


+ Response 204