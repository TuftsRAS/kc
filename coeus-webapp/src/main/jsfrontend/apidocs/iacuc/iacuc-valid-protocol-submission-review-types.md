## Iacuc Valid Protocol Submission Review Types [/research-sys/api/v1/iacuc-valid-protocol-submission-review-types/]

### Get Iacuc Valid Protocol Submission Review Types by Key [GET /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"}

### Get All Iacuc Valid Protocol Submission Review Types [GET /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"},
              {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"}
            ]

### Get All Iacuc Valid Protocol Submission Review Types with Filtering [GET /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + validProtoSubRevTypeId
            + submissionTypeCode
            + protocolReviewTypeCode
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"},
              {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Iacuc Valid Protocol Submission Review Types [GET /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/]
	 
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
		
### Get Blueprint API specification for Iacuc Valid Protocol Submission Review Types [GET /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Iacuc Valid Protocol Submission Review Types.md"
            transfer-encoding:chunked


### Update Iacuc Valid Protocol Submission Review Types [PUT /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Iacuc Valid Protocol Submission Review Types [PUT /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"},
              {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Iacuc Valid Protocol Submission Review Types [POST /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Iacuc Valid Protocol Submission Review Types [POST /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"},
              {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"},
              {"validProtoSubRevTypeId": "(val)","submissionTypeCode": "(val)","protocolReviewTypeCode": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Iacuc Valid Protocol Submission Review Types by Key [DELETE /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Iacuc Valid Protocol Submission Review Types [DELETE /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Iacuc Valid Protocol Submission Review Types with Matching [DELETE /research-sys/api/v1/iacuc-valid-protocol-submission-review-types/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + validProtoSubRevTypeId
            + submissionTypeCode
            + protocolReviewTypeCode


+ Response 204