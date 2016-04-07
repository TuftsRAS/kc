## Person Mass Changes [/research-sys/api/v1/person-mass-changes/]

### Get Person Mass Changes by Key [GET /research-sys/api/v1/person-mass-changes/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"}

### Get All Person Mass Changes [GET /research-sys/api/v1/person-mass-changes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"},
              {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"}
            ]

### Get All Person Mass Changes with Filtering [GET /research-sys/api/v1/person-mass-changes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
    
    + Parameters
    
            + personMassChangeId
            + documentNumber
            + replaceePersonId
            + replaceeRolodexId
            + replacerPersonId
            + replacerRolodexId
            + changeAllSequences
 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"},
              {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Person Mass Changes [GET /research-sys/api/v1/person-mass-changes/]
	 
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
		
### Get Blueprint API specification for Person Mass Changes [GET /research-sys/api/v1/person-mass-changes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown
    
    + Parameters
    
            + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Person Mass Changes.md"
            transfer-encoding:chunked


### Update Person Mass Changes [PUT /research-sys/api/v1/person-mass-changes/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Person Mass Changes [PUT /research-sys/api/v1/person-mass-changes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"},
              {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204

### Insert Person Mass Changes [POST /research-sys/api/v1/person-mass-changes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Person Mass Changes [POST /research-sys/api/v1/person-mass-changes/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"},
              {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"},
              {"personMassChangeId": "(val)","documentNumber": "(val)","replaceePersonId": "(val)","replaceeRolodexId": "(val)","replacerPersonId": "(val)","replacerRolodexId": "(val)","changeAllSequences": "(val)","_primaryKey": "(val)"}
            ]
            
### Delete Person Mass Changes by Key [DELETE /research-sys/api/v1/person-mass-changes/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Person Mass Changes [DELETE /research-sys/api/v1/person-mass-changes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Response 204

### Delete All Person Mass Changes with Matching [DELETE /research-sys/api/v1/person-mass-changes/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json
            
    + Parameters
    
            + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
            + personMassChangeId
            + documentNumber
            + replaceePersonId
            + replaceeRolodexId
            + replacerPersonId
            + replacerRolodexId
            + changeAllSequences


+ Response 204