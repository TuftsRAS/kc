## Sub Award Approval Types [/research-sys/api/v1/sub-award-approval-types/]

### Get Sub Award Approval Types by Key [GET /research-sys/api/v1/sub-award-approval-types/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}

### Get All Sub Award Approval Types [GET /research-sys/api/v1/sub-award-approval-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]

### Get All Sub Award Approval Types with Filtering [GET /research-sys/api/v1/sub-award-approval-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + subAwardApprovalTypeCode
            + description
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Sub Award Approval Types [GET /research-sys/api/v1/sub-award-approval-types/]
	 
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
		
### Get Blueprint API specification for Sub Award Approval Types [GET /research-sys/api/v1/sub-award-approval-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Sub Award Approval Types.md"
            transfer-encoding:chunked


### Update Sub Award Approval Types [PUT /research-sys/api/v1/sub-award-approval-types/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Sub Award Approval Types [PUT /research-sys/api/v1/sub-award-approval-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Sub Award Approval Types [POST /research-sys/api/v1/sub-award-approval-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Sub Award Approval Types [POST /research-sys/api/v1/sub-award-approval-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"subAwardApprovalTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Sub Award Approval Types by Key [DELETE /research-sys/api/v1/sub-award-approval-types/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Sub Award Approval Types [DELETE /research-sys/api/v1/sub-award-approval-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Sub Award Approval Types with Matching [DELETE /research-sys/api/v1/sub-award-approval-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + subAwardApprovalTypeCode
            + description


+ Response 204