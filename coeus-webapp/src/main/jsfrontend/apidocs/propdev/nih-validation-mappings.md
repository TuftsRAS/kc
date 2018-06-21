## Nih Validation Mappings [/propdev/api/v1/nih-validation-mappings/]

### Get Nih Validation Mappings by Key [GET /propdev/api/v1/nih-validation-mappings/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}

### Get All Nih Validation Mappings [GET /propdev/api/v1/nih-validation-mappings/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}
            ]

### Get All Nih Validation Mappings with Filtering [GET /propdev/api/v1/nih-validation-mappings/]
    
+ Parameters

    + id (optional) - Rule Number. Maximum length is 22.
    + ruleNumber (optional) - Rule Number. Maximum length is 20.
    + customMessage (optional) - Custom Message. Maximum length is 400.
    + pageId (optional) - Page Id. Maximum length is 50.
    + sectionId (optional) - Section Id. Maximum length is 50.
    + active (optional) - Active. Maximum length is 1.
    + forceError (optional) - Force Error. Maximum length is 1.
    + appendToOriginal (optional) - Append to Original. Maximum length is 1.

            
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Nih Validation Mappings [GET /propdev/api/v1/nih-validation-mappings/]
	                                          
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
    
            {"columns":["id","ruleNumber","customMessage","pageId","sectionId","active","forceError","appendToOriginal"],"primaryKey":"id"}
		
### Get Blueprint API specification for Nih Validation Mappings [GET /propdev/api/v1/nih-validation-mappings/]
	 
+ Parameters

     + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource
                 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Nih Validation Mappings.md"
            transfer-encoding:chunked
### Update Nih Validation Mappings [PUT /propdev/api/v1/nih-validation-mappings/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Nih Validation Mappings [PUT /propdev/api/v1/nih-validation-mappings/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204
### Update Specific Attributes Nih Validation Mappings [PATCH /propdev/api/v1/nih-validation-mappings/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}
			
+ Response 204
    
    + Body
            
            {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}
### Insert Nih Validation Mappings [POST /propdev/api/v1/nih-validation-mappings/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Nih Validation Mappings [POST /propdev/api/v1/nih-validation-mappings/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","ruleNumber": "(val)","customMessage": "(val)","pageId": "(val)","sectionId": "(val)","active": "(val)","forceError": "(val)","appendToOriginal": "(val)","_primaryKey": "(val)"}
            ]
### Delete Nih Validation Mappings by Key [DELETE /propdev/api/v1/nih-validation-mappings/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Nih Validation Mappings [DELETE /propdev/api/v1/nih-validation-mappings/]

+ Parameters

      + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Nih Validation Mappings with Matching [DELETE /propdev/api/v1/nih-validation-mappings/]

+ Parameters

    + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
    + id (optional) - Rule Number. Maximum length is 22.
    + ruleNumber (optional) - Rule Number. Maximum length is 20.
    + customMessage (optional) - Custom Message. Maximum length is 400.
    + pageId (optional) - Page Id. Maximum length is 50.
    + sectionId (optional) - Section Id. Maximum length is 50.
    + active (optional) - Active. Maximum length is 1.
    + forceError (optional) - Force Error. Maximum length is 1.
    + appendToOriginal (optional) - Append to Original. Maximum length is 1.

      
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204
