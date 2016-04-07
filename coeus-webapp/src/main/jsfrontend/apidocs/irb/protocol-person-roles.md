## Protocol Person Roles [/research-sys/api/v1/protocol-person-roles/]

### Get Protocol Person Roles by Key [GET /research-sys/api/v1/protocol-person-roles/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"}

### Get All Protocol Person Roles [GET /research-sys/api/v1/protocol-person-roles/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]

### Get All Protocol Person Roles with Filtering [GET /research-sys/api/v1/protocol-person-roles/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + protocolPersonRoleId
            + description
            + unitDetailsRequired
            + affiliationDetailsRequired
            + trainingDetailsRequired
            + commentsDetailsRequired
            + active
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Protocol Person Roles [GET /research-sys/api/v1/protocol-person-roles/]
	 
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
		
### Get Blueprint API specification for Protocol Person Roles [GET /research-sys/api/v1/protocol-person-roles/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Protocol Person Roles.md"
            transfer-encoding:chunked


### Update Protocol Person Roles [PUT /research-sys/api/v1/protocol-person-roles/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Protocol Person Roles [PUT /research-sys/api/v1/protocol-person-roles/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Protocol Person Roles [POST /research-sys/api/v1/protocol-person-roles/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Protocol Person Roles [POST /research-sys/api/v1/protocol-person-roles/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"},
              {"protocolPersonRoleId": "(val)","description": "(val)","unitDetailsRequired": "(val)","affiliationDetailsRequired": "(val)","trainingDetailsRequired": "(val)","commentsDetailsRequired": "(val)","active": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Protocol Person Roles by Key [DELETE /research-sys/api/v1/protocol-person-roles/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Protocol Person Roles [DELETE /research-sys/api/v1/protocol-person-roles/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Protocol Person Roles with Matching [DELETE /research-sys/api/v1/protocol-person-roles/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + protocolPersonRoleId
            + description
            + unitDetailsRequired
            + affiliationDetailsRequired
            + trainingDetailsRequired
            + commentsDetailsRequired
            + active


+ Response 204