## Iacuc Protocol Attachment Type Groups [/research-sys/api/v1/iacuc-protocol-attachment-type-groups/]

### Get Iacuc Protocol Attachment Type Groups by Key [GET /research-sys/api/v1/iacuc-protocol-attachment-type-groups/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"}

### Get All Iacuc Protocol Attachment Type Groups [GET /research-sys/api/v1/iacuc-protocol-attachment-type-groups/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"}
            ]

### Get All Iacuc Protocol Attachment Type Groups with Filtering [GET /research-sys/api/v1/iacuc-protocol-attachment-type-groups/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + id
            + typeCode
            + groupCode
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Iacuc Protocol Attachment Type Groups [GET /research-sys/api/v1/iacuc-protocol-attachment-type-groups/]
	 
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
		
### Get Blueprint API specification for Iacuc Protocol Attachment Type Groups [GET /research-sys/api/v1/iacuc-protocol-attachment-type-groups/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Iacuc Protocol Attachment Type Groups.md"
            transfer-encoding:chunked


### Update Iacuc Protocol Attachment Type Groups [PUT /research-sys/api/v1/iacuc-protocol-attachment-type-groups/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Iacuc Protocol Attachment Type Groups [PUT /research-sys/api/v1/iacuc-protocol-attachment-type-groups/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Iacuc Protocol Attachment Type Groups [POST /research-sys/api/v1/iacuc-protocol-attachment-type-groups/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Iacuc Protocol Attachment Type Groups [POST /research-sys/api/v1/iacuc-protocol-attachment-type-groups/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","typeCode": "(val)","groupCode": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Iacuc Protocol Attachment Type Groups by Key [DELETE /research-sys/api/v1/iacuc-protocol-attachment-type-groups/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Iacuc Protocol Attachment Type Groups [DELETE /research-sys/api/v1/iacuc-protocol-attachment-type-groups/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Iacuc Protocol Attachment Type Groups with Matching [DELETE /research-sys/api/v1/iacuc-protocol-attachment-type-groups/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + id
            + typeCode
            + groupCode


+ Response 204