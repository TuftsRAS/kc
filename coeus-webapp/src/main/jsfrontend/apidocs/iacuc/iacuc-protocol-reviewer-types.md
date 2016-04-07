## Iacuc Protocol Reviewer Types [/research-sys/api/v1/iacuc-protocol-reviewer-types/]

### Get Iacuc Protocol Reviewer Types by Key [GET /research-sys/api/v1/iacuc-protocol-reviewer-types/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}

### Get All Iacuc Protocol Reviewer Types [GET /research-sys/api/v1/iacuc-protocol-reviewer-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]

### Get All Iacuc Protocol Reviewer Types with Filtering [GET /research-sys/api/v1/iacuc-protocol-reviewer-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + reviewerTypeCode
            + description
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Iacuc Protocol Reviewer Types [GET /research-sys/api/v1/iacuc-protocol-reviewer-types/]
	 
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
		
### Get Blueprint API specification for Iacuc Protocol Reviewer Types [GET /research-sys/api/v1/iacuc-protocol-reviewer-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Iacuc Protocol Reviewer Types.md"
            transfer-encoding:chunked


### Update Iacuc Protocol Reviewer Types [PUT /research-sys/api/v1/iacuc-protocol-reviewer-types/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Iacuc Protocol Reviewer Types [PUT /research-sys/api/v1/iacuc-protocol-reviewer-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Iacuc Protocol Reviewer Types [POST /research-sys/api/v1/iacuc-protocol-reviewer-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Iacuc Protocol Reviewer Types [POST /research-sys/api/v1/iacuc-protocol-reviewer-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"reviewerTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Iacuc Protocol Reviewer Types by Key [DELETE /research-sys/api/v1/iacuc-protocol-reviewer-types/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Iacuc Protocol Reviewer Types [DELETE /research-sys/api/v1/iacuc-protocol-reviewer-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Iacuc Protocol Reviewer Types with Matching [DELETE /research-sys/api/v1/iacuc-protocol-reviewer-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + reviewerTypeCode
            + description


+ Response 204