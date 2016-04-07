## Kim Attributes [/research-sys/api/v1/kim-attributes/]

### Get Kim Attributes by Key [GET /research-sys/api/v1/kim-attributes/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"}

### Get All Kim Attributes [GET /research-sys/api/v1/kim-attributes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]

### Get All Kim Attributes with Filtering [GET /research-sys/api/v1/kim-attributes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + id
            + componentName
            + attributeName
            + namespaceCode
            + attributeLabel
            + active
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Kim Attributes [GET /research-sys/api/v1/kim-attributes/]
	 
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
		
### Get Blueprint API specification for Kim Attributes [GET /research-sys/api/v1/kim-attributes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Kim Attributes.md"
            transfer-encoding:chunked


### Update Kim Attributes [PUT /research-sys/api/v1/kim-attributes/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Kim Attributes [PUT /research-sys/api/v1/kim-attributes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Kim Attributes [POST /research-sys/api/v1/kim-attributes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Kim Attributes [POST /research-sys/api/v1/kim-attributes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","componentName": "(val)","attributeName": "(val)","namespaceCode": "(val)","attributeLabel": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Kim Attributes by Key [DELETE /research-sys/api/v1/kim-attributes/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Kim Attributes [DELETE /research-sys/api/v1/kim-attributes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Kim Attributes with Matching [DELETE /research-sys/api/v1/kim-attributes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + id
            + componentName
            + attributeName
            + namespaceCode
            + attributeLabel
            + active


+ Response 204