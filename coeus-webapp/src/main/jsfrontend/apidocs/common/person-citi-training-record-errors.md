## Person Citi Training Record Errors [/research-common/api/v1/person-citi-training-record-errors/]

### Get Person Citi Training Record Errors by Key [GET /research-common/api/v1/person-citi-training-record-errors/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}

### Get All Person Citi Training Record Errors [GET /research-common/api/v1/person-citi-training-record-errors/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}
            ]

### Get All Person Citi Training Record Errors with Filtering [GET /research-common/api/v1/person-citi-training-record-errors/]
    
+ Parameters

    + id (optional) - Person Training CITI Record Error Id. Maximum length is 22.
    + citiRecordId (optional) - Person Training CITI Record Id. Maximum length is 22.
    + message (optional) - Message. Maximum length is 2000.

            
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Person Citi Training Record Errors [GET /research-common/api/v1/person-citi-training-record-errors/]
	                                          
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
    
            {"columns":["id","citiRecordId","message"],"primaryKey":"id"}
		
### Get Blueprint API specification for Person Citi Training Record Errors [GET /research-common/api/v1/person-citi-training-record-errors/]
	 
+ Parameters

     + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource
                 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Person Citi Training Record Errors.md"
            transfer-encoding:chunked
### Update Person Citi Training Record Errors [PUT /research-common/api/v1/person-citi-training-record-errors/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Person Citi Training Record Errors [PUT /research-common/api/v1/person-citi-training-record-errors/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204
### Update Specific Attributes Person Citi Training Record Errors [PATCH /research-common/api/v1/person-citi-training-record-errors/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}
			
+ Response 204
    
    + Body
            
            {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}
### Insert Person Citi Training Record Errors [POST /research-common/api/v1/person-citi-training-record-errors/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Person Citi Training Record Errors [POST /research-common/api/v1/person-citi-training-record-errors/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","citiRecordId": "(val)","message": "(val)","_primaryKey": "(val)"}
            ]
### Delete Person Citi Training Record Errors by Key [DELETE /research-common/api/v1/person-citi-training-record-errors/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Person Citi Training Record Errors [DELETE /research-common/api/v1/person-citi-training-record-errors/]

+ Parameters

      + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Person Citi Training Record Errors with Matching [DELETE /research-common/api/v1/person-citi-training-record-errors/]

+ Parameters

    + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
    + id (optional) - Person Training CITI Record Error Id. Maximum length is 22.
    + citiRecordId (optional) - Person Training CITI Record Id. Maximum length is 22.
    + message (optional) - Message. Maximum length is 2000.

      
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204
