## Negotiation Activity Attachments [/research-sys/api/v1/negotiation-activity-attachments/]

### Get Negotiation Activity Attachments by Key [GET /research-sys/api/v1/negotiation-activity-attachments/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"}

### Get All Negotiation Activity Attachments [GET /research-sys/api/v1/negotiation-activity-attachments/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"},
              {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"}
            ]

### Get All Negotiation Activity Attachments with Filtering [GET /research-sys/api/v1/negotiation-activity-attachments/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + attachmentId
            + activityId
            + fileId
            + description
            + restricted
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"},
              {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Negotiation Activity Attachments [GET /research-sys/api/v1/negotiation-activity-attachments/]
	 
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
		
### Get Blueprint API specification for Negotiation Activity Attachments [GET /research-sys/api/v1/negotiation-activity-attachments/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Negotiation Activity Attachments.md"
            transfer-encoding:chunked


### Update Negotiation Activity Attachments [PUT /research-sys/api/v1/negotiation-activity-attachments/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Negotiation Activity Attachments [PUT /research-sys/api/v1/negotiation-activity-attachments/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"},
              {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Negotiation Activity Attachments [POST /research-sys/api/v1/negotiation-activity-attachments/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Negotiation Activity Attachments [POST /research-sys/api/v1/negotiation-activity-attachments/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"},
              {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"},
              {"attachmentId": "(val)","activityId": "(val)","fileId": "(val)","description": "(val)","restricted": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Negotiation Activity Attachments by Key [DELETE /research-sys/api/v1/negotiation-activity-attachments/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Negotiation Activity Attachments [DELETE /research-sys/api/v1/negotiation-activity-attachments/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Negotiation Activity Attachments with Matching [DELETE /research-sys/api/v1/negotiation-activity-attachments/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + attachmentId
            + activityId
            + fileId
            + description
            + restricted


+ Response 204