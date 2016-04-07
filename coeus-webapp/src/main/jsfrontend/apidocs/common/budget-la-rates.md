## Budget La Rates [/research-sys/api/v1/budget-la-rates/]

### Get Budget La Rates by Key [GET /research-sys/api/v1/budget-la-rates/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"}

### Get All Budget La Rates [GET /research-sys/api/v1/budget-la-rates/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"},
              {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"}
            ]

### Get All Budget La Rates with Filtering [GET /research-sys/api/v1/budget-la-rates/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + applicableRate
            + instituteRate
            + onOffCampusFlag
            + rateTypeCode
            + budgetId
            + rateClassCode
            + fiscalYear
            + startDate
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"},
              {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Budget La Rates [GET /research-sys/api/v1/budget-la-rates/]
	 
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
		
### Get Blueprint API specification for Budget La Rates [GET /research-sys/api/v1/budget-la-rates/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Budget La Rates.md"
            transfer-encoding:chunked


### Update Budget La Rates [PUT /research-sys/api/v1/budget-la-rates/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Budget La Rates [PUT /research-sys/api/v1/budget-la-rates/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"},
              {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Budget La Rates [POST /research-sys/api/v1/budget-la-rates/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Budget La Rates [POST /research-sys/api/v1/budget-la-rates/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"},
              {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"},
              {"applicableRate": "(val)","instituteRate": "(val)","onOffCampusFlag": "(val)","rateTypeCode": "(val)","budgetId": "(val)","rateClassCode": "(val)","fiscalYear": "(val)","startDate": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Budget La Rates by Key [DELETE /research-sys/api/v1/budget-la-rates/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Budget La Rates [DELETE /research-sys/api/v1/budget-la-rates/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Budget La Rates with Matching [DELETE /research-sys/api/v1/budget-la-rates/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + applicableRate
            + instituteRate
            + onOffCampusFlag
            + rateTypeCode
            + budgetId
            + rateClassCode
            + fiscalYear
            + startDate


+ Response 204