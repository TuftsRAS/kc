## Iacuc Committee Schedule Attendances [/research-sys/api/v1/iacuc-committee-schedule-attendances/]

### Get Iacuc Committee Schedule Attendances by Key [GET /research-sys/api/v1/iacuc-committee-schedule-attendances/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"}

### Get All Iacuc Committee Schedule Attendances [GET /research-sys/api/v1/iacuc-committee-schedule-attendances/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"},
              {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"}
            ]

### Get All Iacuc Committee Schedule Attendances with Filtering [GET /research-sys/api/v1/iacuc-committee-schedule-attendances/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + commScheduleAttendanceId
            + scheduleIdFk
            + personId
            + personName
            + guestFlag
            + alternateFlag
            + alternateFor
            + nonEmployeeFlag
            + comments
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"},
              {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Iacuc Committee Schedule Attendances [GET /research-sys/api/v1/iacuc-committee-schedule-attendances/]
	 
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
		
### Get Blueprint API specification for Iacuc Committee Schedule Attendances [GET /research-sys/api/v1/iacuc-committee-schedule-attendances/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Iacuc Committee Schedule Attendances.md"
            transfer-encoding:chunked


### Update Iacuc Committee Schedule Attendances [PUT /research-sys/api/v1/iacuc-committee-schedule-attendances/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Iacuc Committee Schedule Attendances [PUT /research-sys/api/v1/iacuc-committee-schedule-attendances/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"},
              {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Iacuc Committee Schedule Attendances [POST /research-sys/api/v1/iacuc-committee-schedule-attendances/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Iacuc Committee Schedule Attendances [POST /research-sys/api/v1/iacuc-committee-schedule-attendances/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"},
              {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"},
              {"commScheduleAttendanceId": "(val)","scheduleIdFk": "(val)","personId": "(val)","personName": "(val)","guestFlag": "(val)","alternateFlag": "(val)","alternateFor": "(val)","nonEmployeeFlag": "(val)","comments": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Iacuc Committee Schedule Attendances by Key [DELETE /research-sys/api/v1/iacuc-committee-schedule-attendances/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Iacuc Committee Schedule Attendances [DELETE /research-sys/api/v1/iacuc-committee-schedule-attendances/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Iacuc Committee Schedule Attendances with Matching [DELETE /research-sys/api/v1/iacuc-committee-schedule-attendances/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + commScheduleAttendanceId
            + scheduleIdFk
            + personId
            + personName
            + guestFlag
            + alternateFlag
            + alternateFor
            + nonEmployeeFlag
            + comments


+ Response 204