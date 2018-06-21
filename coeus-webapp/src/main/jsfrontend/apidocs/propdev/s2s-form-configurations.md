## S2s Form Configurations [/propdev/api/v1/s2s-form-configurations/]

### Get S2s Form Configurations by Key [GET /propdev/api/v1/s2s-form-configurations/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}

### Get All S2s Form Configurations [GET /propdev/api/v1/s2s-form-configurations/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}
            ]

### Get All S2s Form Configurations with Filtering [GET /propdev/api/v1/s2s-form-configurations/]
    
+ Parameters

    + id (optional) - S2s Error Id. Maximum length is 12.
    + formName (optional) - Form Name. Maximum length is 50.
    + active (optional) - Form Active. Maximum length is 1.
    + inactiveMessage (optional) - Inactive Message. Maximum length is 200.

            
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for S2s Form Configurations [GET /propdev/api/v1/s2s-form-configurations/]
	                                          
+ Parameters

      + _schema (required) - will instruct the endpoint to return a schema data structure for the resource
      
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"columns":["id","formName","active","inactiveMessage"],"primaryKey":"id"}
		
### Get Blueprint API specification for S2s Form Configurations [GET /propdev/api/v1/s2s-form-configurations/]
	 
+ Parameters

     + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource
                 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="S2s Form Configurations.md"
            transfer-encoding:chunked
### Update S2s Form Configurations [PUT /propdev/api/v1/s2s-form-configurations/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple S2s Form Configurations [PUT /propdev/api/v1/s2s-form-configurations/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204
### Update Specific Attributes S2s Form Configurations [PATCH /propdev/api/v1/s2s-form-configurations/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}
			
+ Response 204
    
    + Body
            
            {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}
### Insert S2s Form Configurations [POST /propdev/api/v1/s2s-form-configurations/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple S2s Form Configurations [POST /propdev/api/v1/s2s-form-configurations/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","formName": "(val)","active": "(val)","inactiveMessage": "(val)","_primaryKey": "(val)"}
            ]
### Delete S2s Form Configurations by Key [DELETE /propdev/api/v1/s2s-form-configurations/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All S2s Form Configurations [DELETE /propdev/api/v1/s2s-form-configurations/]

+ Parameters

      + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All S2s Form Configurations with Matching [DELETE /propdev/api/v1/s2s-form-configurations/]

+ Parameters

    + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
    + id (optional) - S2s Error Id. Maximum length is 12.
    + formName (optional) - Form Name. Maximum length is 50.
    + active (optional) - Form Active. Maximum length is 1.
    + inactiveMessage (optional) - Inactive Message. Maximum length is 200.

      
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204
