## Person Citi Training Records [/research-common/api/v1/person-citi-training-records/]

### Get Person Citi Training Records by Key [GET /research-common/api/v1/person-citi-training-records/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}

### Get All Person Citi Training Records [GET /research-common/api/v1/person-citi-training-records/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}
            ]

### Get All Person Citi Training Records with Filtering [GET /research-common/api/v1/person-citi-training-records/]
    
+ Parameters

    + id (optional) - Person Training CITI Record Id. Maximum length is 22.
    + firstName (optional) - First Name. Maximum length is 40.
    + lastName (optional) - Last Name. Maximum length is 80.
    + email (optional) - The email address of the CITI user. Maximum length is 200.
    + employeeNumber (optional) - Employee Number. Maximum length is 100.
    + crNumber (optional) - CR Number. Maximum length is 100.
    + curriculum (optional) - Curriculum. Maximum length is 200.
    + groupId (optional) - Group Id. Maximum length is 100.
    + group (optional) - Group. Maximum length is 200.
    + score (optional) - Score. Maximum length is 5.
    + passingScore (optional) - Passing Score. Maximum length is 5.
    + stageNumber (optional) - Stage Number. Maximum length is 100.
    + stage (optional) - Stage. Maximum length is 200.
    + learnerId (optional) - Learner Id. Maximum length is 100.
    + dateCompleted (optional) - Date Completed. Maximum length is 21.
    + expirationDate (optional) - Expiration Date. Maximum length is 21.
    + registrationDate (optional) - Registration Date. Maximum length is 21.
    + name (optional) - Name. Maximum length is 200.
    + username (optional) - User Name. Maximum length is 100.
    + institutionalUsername (optional) - Institutional User Name. Maximum length is 100.
    + institutionalLanguage (optional) - Institutional Language. Maximum length is 100.
    + institutionalEmail (optional) - The institutional email address of the CITI user. Maximum length is 200.
    + gender (optional) - Gender. Maximum length is 20.
    + highestDegree (optional) - Highest Degree. Maximum length is 200.
    + department (optional) - Department. Maximum length is 200.
    + addressField1 (optional) - Address Field 1. Maximum length is 128.
    + addressField2 (optional) - Address Field 2. Maximum length is 128.
    + addressField3 (optional) - Address Field 3. Maximum length is 128.
    + city (optional) - City. Maximum length is 30.
    + state (optional) - State. Maximum length is 100.
    + zipCode (optional) - Zip Code. Maximum length is 20.
    + country (optional) - Country. Maximum length is 100.
    + phone (optional) - Phone. Maximum length is 20.
    + customField1 (optional) - Custom Field 1. Maximum length is 200.
    + customField2 (optional) - Custom Field 2. Maximum length is 200.
    + customField3 (optional) - Custom Field 3. Maximum length is 200.
    + customField4 (optional) - Custom Field 4. Maximum length is 200.
    + customField5 (optional) - Custom Field 5. Maximum length is 200.
    + rawRecord (optional) - Raw Record.
    + statusCode (optional) - Status Code. Maximum length is 1.

            
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Person Citi Training Records [GET /research-common/api/v1/person-citi-training-records/]
	                                          
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
    
            {"columns":["id","firstName","lastName","email","employeeNumber","crNumber","curriculum","groupId","group","score","passingScore","stageNumber","stage","learnerId","dateCompleted","expirationDate","registrationDate","name","username","institutionalUsername","institutionalLanguage","institutionalEmail","gender","highestDegree","department","addressField1","addressField2","addressField3","city","state","zipCode","country","phone","customField1","customField2","customField3","customField4","customField5","rawRecord","statusCode"],"primaryKey":"id"}
		
### Get Blueprint API specification for Person Citi Training Records [GET /research-common/api/v1/person-citi-training-records/]
	 
+ Parameters

     + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource
                 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Person Citi Training Records.md"
            transfer-encoding:chunked
### Update Person Citi Training Records [PUT /research-common/api/v1/person-citi-training-records/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Person Citi Training Records [PUT /research-common/api/v1/person-citi-training-records/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204
### Update Specific Attributes Person Citi Training Records [PATCH /research-common/api/v1/person-citi-training-records/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}
			
+ Response 204
    
    + Body
            
            {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}
### Insert Person Citi Training Records [POST /research-common/api/v1/person-citi-training-records/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Person Citi Training Records [POST /research-common/api/v1/person-citi-training-records/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"},
              {"id": "(val)","firstName": "(val)","lastName": "(val)","email": "(val)","employeeNumber": "(val)","crNumber": "(val)","curriculum": "(val)","groupId": "(val)","group": "(val)","score": "(val)","passingScore": "(val)","stageNumber": "(val)","stage": "(val)","learnerId": "(val)","dateCompleted": "(val)","expirationDate": "(val)","registrationDate": "(val)","name": "(val)","username": "(val)","institutionalUsername": "(val)","institutionalLanguage": "(val)","institutionalEmail": "(val)","gender": "(val)","highestDegree": "(val)","department": "(val)","addressField1": "(val)","addressField2": "(val)","addressField3": "(val)","city": "(val)","state": "(val)","zipCode": "(val)","country": "(val)","phone": "(val)","customField1": "(val)","customField2": "(val)","customField3": "(val)","customField4": "(val)","customField5": "(val)","rawRecord": "(val)","statusCode": "(val)","_primaryKey": "(val)"}
            ]
### Delete Person Citi Training Records by Key [DELETE /research-common/api/v1/person-citi-training-records/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Person Citi Training Records [DELETE /research-common/api/v1/person-citi-training-records/]

+ Parameters

      + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Person Citi Training Records with Matching [DELETE /research-common/api/v1/person-citi-training-records/]

+ Parameters

    + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
    + id (optional) - Person Training CITI Record Id. Maximum length is 22.
    + firstName (optional) - First Name. Maximum length is 40.
    + lastName (optional) - Last Name. Maximum length is 80.
    + email (optional) - The email address of the CITI user. Maximum length is 200.
    + employeeNumber (optional) - Employee Number. Maximum length is 100.
    + crNumber (optional) - CR Number. Maximum length is 100.
    + curriculum (optional) - Curriculum. Maximum length is 200.
    + groupId (optional) - Group Id. Maximum length is 100.
    + group (optional) - Group. Maximum length is 200.
    + score (optional) - Score. Maximum length is 5.
    + passingScore (optional) - Passing Score. Maximum length is 5.
    + stageNumber (optional) - Stage Number. Maximum length is 100.
    + stage (optional) - Stage. Maximum length is 200.
    + learnerId (optional) - Learner Id. Maximum length is 100.
    + dateCompleted (optional) - Date Completed. Maximum length is 21.
    + expirationDate (optional) - Expiration Date. Maximum length is 21.
    + registrationDate (optional) - Registration Date. Maximum length is 21.
    + name (optional) - Name. Maximum length is 200.
    + username (optional) - User Name. Maximum length is 100.
    + institutionalUsername (optional) - Institutional User Name. Maximum length is 100.
    + institutionalLanguage (optional) - Institutional Language. Maximum length is 100.
    + institutionalEmail (optional) - The institutional email address of the CITI user. Maximum length is 200.
    + gender (optional) - Gender. Maximum length is 20.
    + highestDegree (optional) - Highest Degree. Maximum length is 200.
    + department (optional) - Department. Maximum length is 200.
    + addressField1 (optional) - Address Field 1. Maximum length is 128.
    + addressField2 (optional) - Address Field 2. Maximum length is 128.
    + addressField3 (optional) - Address Field 3. Maximum length is 128.
    + city (optional) - City. Maximum length is 30.
    + state (optional) - State. Maximum length is 100.
    + zipCode (optional) - Zip Code. Maximum length is 20.
    + country (optional) - Country. Maximum length is 100.
    + phone (optional) - Phone. Maximum length is 20.
    + customField1 (optional) - Custom Field 1. Maximum length is 200.
    + customField2 (optional) - Custom Field 2. Maximum length is 200.
    + customField3 (optional) - Custom Field 3. Maximum length is 200.
    + customField4 (optional) - Custom Field 4. Maximum length is 200.
    + customField5 (optional) - Custom Field 5. Maximum length is 200.
    + rawRecord (optional) - Raw Record.
    + statusCode (optional) - Status Code. Maximum length is 1.

      
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204
