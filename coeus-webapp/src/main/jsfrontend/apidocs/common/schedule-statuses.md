## Schedule Statuses [/research-sys/api/v1/schedule-statuses/]

### Get Schedule Statuses by Key [GET /research-sys/api/v1/schedule-statuses/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"}

### Get All Schedule Statuses [GET /research-sys/api/v1/schedule-statuses/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]

### Get All Schedule Statuses with Filtering [GET /research-sys/api/v1/schedule-statuses/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + scheduleStatusCode
            + description
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Schedule Statuses [GET /research-sys/api/v1/schedule-statuses/]
	 
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
		
### Get Blueprint API specification for Schedule Statuses [GET /research-sys/api/v1/schedule-statuses/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Schedule Statuses.md"
            transfer-encoding:chunked


### Update Schedule Statuses [PUT /research-sys/api/v1/schedule-statuses/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Schedule Statuses [PUT /research-sys/api/v1/schedule-statuses/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Schedule Statuses [POST /research-sys/api/v1/schedule-statuses/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Schedule Statuses [POST /research-sys/api/v1/schedule-statuses/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"},
              {"scheduleStatusCode": "(val)","description": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Schedule Statuses by Key [DELETE /research-sys/api/v1/schedule-statuses/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Schedule Statuses [DELETE /research-sys/api/v1/schedule-statuses/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Schedule Statuses with Matching [DELETE /research-sys/api/v1/schedule-statuses/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + scheduleStatusCode
            + description


+ Response 204