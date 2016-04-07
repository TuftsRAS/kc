## Sponsor Term Types [/research-sys/api/v1/sponsor-term-types/]

### Get Sponsor Term Types by Key [GET /research-sys/api/v1/sponsor-term-types/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}

### Get All Sponsor Term Types [GET /research-sys/api/v1/sponsor-term-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]

### Get All Sponsor Term Types with Filtering [GET /research-sys/api/v1/sponsor-term-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + sponsorTermTypeCode
            + description
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Sponsor Term Types [GET /research-sys/api/v1/sponsor-term-types/]
	 
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
		
### Get Blueprint API specification for Sponsor Term Types [GET /research-sys/api/v1/sponsor-term-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Sponsor Term Types.md"
            transfer-encoding:chunked


### Update Sponsor Term Types [PUT /research-sys/api/v1/sponsor-term-types/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Sponsor Term Types [PUT /research-sys/api/v1/sponsor-term-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Sponsor Term Types [POST /research-sys/api/v1/sponsor-term-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Sponsor Term Types [POST /research-sys/api/v1/sponsor-term-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"sponsorTermTypeCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Sponsor Term Types by Key [DELETE /research-sys/api/v1/sponsor-term-types/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Sponsor Term Types [DELETE /research-sys/api/v1/sponsor-term-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Sponsor Term Types with Matching [DELETE /research-sys/api/v1/sponsor-term-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + sponsorTermTypeCode
            + description


+ Response 204