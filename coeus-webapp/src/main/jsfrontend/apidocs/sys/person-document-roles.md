## Person Document Roles [/research-sys/api/v1/person-document-roles/]

### Get Person Document Roles by Key [GET /research-sys/api/v1/person-document-roles/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"}

### Get All Person Document Roles [GET /research-sys/api/v1/person-document-roles/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]

### Get All Person Document Roles with Filtering [GET /research-sys/api/v1/person-document-roles/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + roleId
            + kimTypeId
            + roleName
            + namespaceCode
            + edit
            + documentNumber
            + active
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Person Document Roles [GET /research-sys/api/v1/person-document-roles/]
	 
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
		
### Get Blueprint API specification for Person Document Roles [GET /research-sys/api/v1/person-document-roles/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Person Document Roles.md"
            transfer-encoding:chunked


### Update Person Document Roles [PUT /research-sys/api/v1/person-document-roles/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Person Document Roles [PUT /research-sys/api/v1/person-document-roles/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Person Document Roles [POST /research-sys/api/v1/person-document-roles/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Person Document Roles [POST /research-sys/api/v1/person-document-roles/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"roleId": "(val)","kimTypeId": "(val)","roleName": "(val)","namespaceCode": "(val)","edit": "(val)","documentNumber": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Person Document Roles by Key [DELETE /research-sys/api/v1/person-document-roles/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Person Document Roles [DELETE /research-sys/api/v1/person-document-roles/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Person Document Roles with Matching [DELETE /research-sys/api/v1/person-document-roles/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + roleId
            + kimTypeId
            + roleName
            + namespaceCode
            + edit
            + documentNumber
            + active


+ Response 204