## Modular Budget Idcs [/propdev/api/v1/modular-budget-idcs/]

### Get Modular Budget Idcs by Key [GET /propdev/api/v1/modular-budget-idcs/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}

### Get All Modular Budget Idcs [GET /propdev/api/v1/modular-budget-idcs/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"},
              {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}
            ]

### Get All Modular Budget Idcs with Filtering [GET /propdev/api/v1/modular-budget-idcs/]
    
+ Parameters

    + budgetPeriodId (optional) - Budget Period Id.
    + budgetPeriod (optional) - Budget Period. Maximum length is 3.
    + rateNumber (optional) - Rate Number. Maximum length is 3.
    + budgetId (optional) - Budget Id.
    + description (optional) - Description. Maximum length is 64.
    + idcRate (optional) - IDC Rate. Maximum length is 7.
    + idcBase (optional) - IDC Base. Maximum length is 15.
    + idcBaseUnrounded (optional) - Idc Base Unrounded.
    + fundsRequested (optional) - Funds Requested. Maximum length is 15.
    + startDate (optional) - Start Date.
    + endDate (optional) - End Date.
    + hierarchyProposalNumber (optional) - Hierarchy Proposal Number.

            
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"},
              {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Modular Budget Idcs [GET /propdev/api/v1/modular-budget-idcs/]
	                                          
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
    
            {"columns":["budgetPeriodId","budgetPeriod","rateNumber","budgetId","description","idcRate","idcBase","idcBaseUnrounded","fundsRequested","startDate","endDate","hierarchyProposalNumber"],"primaryKey":"budgetModular:budgetPeriodId:rateNumber"}
		
### Get Blueprint API specification for Modular Budget Idcs [GET /propdev/api/v1/modular-budget-idcs/]
	 
+ Parameters

     + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource
                 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Modular Budget Idcs.md"
            transfer-encoding:chunked
### Update Modular Budget Idcs [PUT /propdev/api/v1/modular-budget-idcs/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Modular Budget Idcs [PUT /propdev/api/v1/modular-budget-idcs/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"},
              {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204
### Update Specific Attributes Modular Budget Idcs [PATCH /propdev/api/v1/modular-budget-idcs/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}
			
+ Response 204
    
    + Body
            
            {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}
### Insert Modular Budget Idcs [POST /propdev/api/v1/modular-budget-idcs/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Modular Budget Idcs [POST /propdev/api/v1/modular-budget-idcs/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"},
              {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"},
              {"budgetPeriodId": "(val)","budgetPeriod": "(val)","rateNumber": "(val)","budgetId": "(val)","description": "(val)","idcRate": "(val)","idcBase": "(val)","idcBaseUnrounded": "(val)","fundsRequested": "(val)","startDate": "(val)","endDate": "(val)","hierarchyProposalNumber": "(val)","_primaryKey": "(val)"}
            ]
### Delete Modular Budget Idcs by Key [DELETE /propdev/api/v1/modular-budget-idcs/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Modular Budget Idcs [DELETE /propdev/api/v1/modular-budget-idcs/]

+ Parameters

      + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Modular Budget Idcs with Matching [DELETE /propdev/api/v1/modular-budget-idcs/]

+ Parameters

    + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
    + budgetPeriodId (optional) - Budget Period Id.
    + budgetPeriod (optional) - Budget Period. Maximum length is 3.
    + rateNumber (optional) - Rate Number. Maximum length is 3.
    + budgetId (optional) - Budget Id.
    + description (optional) - Description. Maximum length is 64.
    + idcRate (optional) - IDC Rate. Maximum length is 7.
    + idcBase (optional) - IDC Base. Maximum length is 15.
    + idcBaseUnrounded (optional) - Idc Base Unrounded.
    + fundsRequested (optional) - Funds Requested. Maximum length is 15.
    + startDate (optional) - Start Date.
    + endDate (optional) - End Date.
    + hierarchyProposalNumber (optional) - Hierarchy Proposal Number.

      
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204
