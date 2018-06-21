## Sent Report Notifications [/award/api/v1/sent-report-notifications/]

### Get Sent Report Notifications by Key [GET /award/api/v1/sent-report-notifications/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}

### Get All Sent Report Notifications [GET /award/api/v1/sent-report-notifications/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"},
              {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}
            ]

### Get All Sent Report Notifications with Filtering [GET /award/api/v1/sent-report-notifications/]
    
+ Parameters

    + awardReportNotifSentId (optional) - Report Tracking Sent Notification Id. Maximum length is 40.
    + awardNumber (optional) - Award ID. Maximum length is 12.
    + reportClassCode (optional) - Report Class. Maximum length is 22.
    + reportCode (optional) - Report. Maximum length is 22.
    + dueDate (optional) - Due Date. Maximum length is 10.
    + actionCode (optional) - Action Code. Maximum length is 3.
    + dateSent (optional) - Date Sent. Maximum length is 10.

            
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"},
              {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Sent Report Notifications [GET /award/api/v1/sent-report-notifications/]
	                                          
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
    
            {"columns":["awardReportNotifSentId","awardNumber","reportClassCode","reportCode","dueDate","actionCode","dateSent"],"primaryKey":"awardReportNotifSentId"}
		
### Get Blueprint API specification for Sent Report Notifications [GET /award/api/v1/sent-report-notifications/]
	 
+ Parameters

     + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource
                 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Sent Report Notifications.md"
            transfer-encoding:chunked
### Update Sent Report Notifications [PUT /award/api/v1/sent-report-notifications/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Sent Report Notifications [PUT /award/api/v1/sent-report-notifications/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"},
              {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204
### Update Specific Attributes Sent Report Notifications [PATCH /award/api/v1/sent-report-notifications/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}
			
+ Response 204
    
    + Body
            
            {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}
### Insert Sent Report Notifications [POST /award/api/v1/sent-report-notifications/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Sent Report Notifications [POST /award/api/v1/sent-report-notifications/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"},
              {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"},
              {"awardReportNotifSentId": "(val)","awardNumber": "(val)","reportClassCode": "(val)","reportCode": "(val)","dueDate": "(val)","actionCode": "(val)","dateSent": "(val)","_primaryKey": "(val)"}
            ]
### Delete Sent Report Notifications by Key [DELETE /award/api/v1/sent-report-notifications/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Sent Report Notifications [DELETE /award/api/v1/sent-report-notifications/]

+ Parameters

      + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Sent Report Notifications with Matching [DELETE /award/api/v1/sent-report-notifications/]

+ Parameters

    + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
    + awardReportNotifSentId (optional) - Report Tracking Sent Notification Id. Maximum length is 40.
    + awardNumber (optional) - Award ID. Maximum length is 12.
    + reportClassCode (optional) - Report Class. Maximum length is 22.
    + reportCode (optional) - Report. Maximum length is 22.
    + dueDate (optional) - Due Date. Maximum length is 10.
    + actionCode (optional) - Action Code. Maximum length is 3.
    + dateSent (optional) - Date Sent. Maximum length is 10.

      
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204
