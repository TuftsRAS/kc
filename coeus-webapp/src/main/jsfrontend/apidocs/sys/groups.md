## Groups [/research-sys/api/v1/groups/]

### Get Groups by Key [GET /research-sys/api/v1/groups/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"}

### Get All Groups [GET /research-sys/api/v1/groups/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"}
            ]

### Get All Groups with Filtering [GET /research-sys/api/v1/groups/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + id
            + kimTypeId
            + name
            + active
            + description
            + namespaceCode
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Groups [GET /research-sys/api/v1/groups/]
	 
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
		
### Get Blueprint API specification for Groups [GET /research-sys/api/v1/groups/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Groups.md"
            transfer-encoding:chunked


### Update Groups [PUT /research-sys/api/v1/groups/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Groups [PUT /research-sys/api/v1/groups/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Groups [POST /research-sys/api/v1/groups/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Groups [POST /research-sys/api/v1/groups/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","kimTypeId": "(val)","name": "(val)","active": "(val)","description": "(val)","namespaceCode": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Groups by Key [DELETE /research-sys/api/v1/groups/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Groups [DELETE /research-sys/api/v1/groups/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Groups with Matching [DELETE /research-sys/api/v1/groups/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + id
            + kimTypeId
            + name
            + active
            + description
            + namespaceCode


+ Response 204