## Mail By Entries [/research-sys/api/v1/mail-by-entries/]

### Get Mail By Entries by Key [GET /research-sys/api/v1/mail-by-entries/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"}

### Get All Mail By Entries [GET /research-sys/api/v1/mail-by-entries/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]

### Get All Mail By Entries with Filtering [GET /research-sys/api/v1/mail-by-entries/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + mailByCode
            + description
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Mail By Entries [GET /research-sys/api/v1/mail-by-entries/]
	 
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
		
### Get Blueprint API specification for Mail By Entries [GET /research-sys/api/v1/mail-by-entries/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Mail By Entries.md"
            transfer-encoding:chunked


### Update Mail By Entries [PUT /research-sys/api/v1/mail-by-entries/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Mail By Entries [PUT /research-sys/api/v1/mail-by-entries/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Mail By Entries [POST /research-sys/api/v1/mail-by-entries/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Mail By Entries [POST /research-sys/api/v1/mail-by-entries/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"mailByCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Mail By Entries by Key [DELETE /research-sys/api/v1/mail-by-entries/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Mail By Entries [DELETE /research-sys/api/v1/mail-by-entries/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Mail By Entries with Matching [DELETE /research-sys/api/v1/mail-by-entries/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + mailByCode
            + description


+ Response 204