## Award Transferring Sponsors [/research-sys/api/v1/award-transferring-sponsors/]

### Get Award Transferring Sponsors by Key [GET /research-sys/api/v1/award-transferring-sponsors/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"}

### Get All Award Transferring Sponsors [GET /research-sys/api/v1/award-transferring-sponsors/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"},
              {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"}
            ]

### Get All Award Transferring Sponsors with Filtering [GET /research-sys/api/v1/award-transferring-sponsors/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + awardTransferringSponsorId
            + awardId
            + awardNumber
            + sequenceNumber
            + sponsorCode
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"},
              {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Award Transferring Sponsors [GET /research-sys/api/v1/award-transferring-sponsors/]
	 
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
		
### Get Blueprint API specification for Award Transferring Sponsors [GET /research-sys/api/v1/award-transferring-sponsors/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Award Transferring Sponsors.md"
            transfer-encoding:chunked


### Update Award Transferring Sponsors [PUT /research-sys/api/v1/award-transferring-sponsors/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Award Transferring Sponsors [PUT /research-sys/api/v1/award-transferring-sponsors/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"},
              {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Award Transferring Sponsors [POST /research-sys/api/v1/award-transferring-sponsors/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Award Transferring Sponsors [POST /research-sys/api/v1/award-transferring-sponsors/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"},
              {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"},
              {"awardTransferringSponsorId": "(val)","awardId": "(val)","awardNumber": "(val)","sequenceNumber": "(val)","sponsorCode": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Award Transferring Sponsors by Key [DELETE /research-sys/api/v1/award-transferring-sponsors/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Award Transferring Sponsors [DELETE /research-sys/api/v1/award-transferring-sponsors/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Award Transferring Sponsors with Matching [DELETE /research-sys/api/v1/award-transferring-sponsors/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + awardTransferringSponsorId
            + awardId
            + awardNumber
            + sequenceNumber
            + sponsorCode


+ Response 204