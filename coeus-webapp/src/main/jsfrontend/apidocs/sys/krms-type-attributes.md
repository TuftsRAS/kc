## Krms Type Attributes [/research-sys/api/v1/krms-type-attributes/]

### Get Krms Type Attributes by Key [GET /research-sys/api/v1/krms-type-attributes/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"}

### Get All Krms Type Attributes [GET /research-sys/api/v1/krms-type-attributes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"}
            ]

### Get All Krms Type Attributes with Filtering [GET /research-sys/api/v1/krms-type-attributes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + id
            + sequenceNumber
            + active
            + attributeDefinitionId
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Krms Type Attributes [GET /research-sys/api/v1/krms-type-attributes/]
	 
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
		
### Get Blueprint API specification for Krms Type Attributes [GET /research-sys/api/v1/krms-type-attributes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Krms Type Attributes.md"
            transfer-encoding:chunked


### Update Krms Type Attributes [PUT /research-sys/api/v1/krms-type-attributes/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Krms Type Attributes [PUT /research-sys/api/v1/krms-type-attributes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Krms Type Attributes [POST /research-sys/api/v1/krms-type-attributes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Krms Type Attributes [POST /research-sys/api/v1/krms-type-attributes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","sequenceNumber": "(val)","active": "(val)","attributeDefinitionId": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Krms Type Attributes by Key [DELETE /research-sys/api/v1/krms-type-attributes/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Krms Type Attributes [DELETE /research-sys/api/v1/krms-type-attributes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Krms Type Attributes with Matching [DELETE /research-sys/api/v1/krms-type-attributes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + id
            + sequenceNumber
            + active
            + attributeDefinitionId


+ Response 204