## Sub Award Template Infos [/subaward/api/v1/sub-award-template-infos/]

### Get Sub Award Template Infos by Key [GET /subaward/api/v1/sub-award-template-infos/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}

### Get All Sub Award Template Infos [GET /subaward/api/v1/sub-award-template-infos/]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"},
              {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}
            ]

### Get All Sub Award Template Infos with Filtering [GET /subaward/api/v1/sub-award-template-infos/]
    
+ Parameters

    + subAwardId (optional) - 
    + subAwardCode (optional) - 
    + sequenceNumber (optional) - 
    + sowOrSubProposalBudget (optional) - SOW/Budget specified in proposal. Maximum length is 1.
    + subProposalDate (optional) - SubProposal Date. Maximum length is 21.
    + invoiceOrPaymentContact (optional) - Invoice / Payment Contact. Maximum length is 10.
    + irbIacucContact (optional) - IRB / IACUC Contact. Maximum length is 10.
    + finalStmtOfCostscontact (optional) - Final Statement of Costs Contact. Maximum length is 10.
    + changeRequestsContact (optional) - Change Requests Contact. Maximum length is 10.
    + terminationContact (optional) - Termination Contact. Maximum length is 10.
    + noCostExtensionContact (optional) - No Cost Extension Contact. Maximum length is 10.
    + perfSiteDiffFromOrgAddr (optional) - Performance Site same as Org address?. Maximum length is 1.
    + perfSiteSameAsSubPiAddr (optional) - 
    + subRegisteredInCcr (optional) - Sub Registered in CCR?. Maximum length is 1.
    + subExemptFromReportingComp (optional) - 
    + parentDunsNumber (optional) - Parent DUNS (if applicable). Maximum length is 20.
    + parentCongressionalDistrict (optional) - Parent Congressional District. Maximum length is 20.
    + exemptFromRprtgExecComp (optional) - Exempt from reporting exec compensation. Maximum length is 1.
    + copyRightType (optional) - Copyrights. Maximum length is 10.
    + automaticCarryForward (optional) - Automatic Carry Forward. Maximum length is 1.
    + carryForwardRequestsSentTo (optional) - Carry Forward Requests Sent To. Maximum length is 10.
    + treatmentPrgmIncomeAdditive (optional) - Treatment of Program Income Additive. Maximum length is 1.
    + applicableProgramRegulations (optional) - Applicable Program Regulations. Maximum length is 50.
    + applicableProgramRegsDate (optional) - Applicable Program Regulations Date. Maximum length is 21.
    + mpiAward (optional) - Is an MPI award. Maximum length is 1.
    + mpiLeadershipPlan (optional) - MPI Leadership Plan. Maximum length is 3.
    + rAndD (optional) - Is R&D. Maximum length is 1.
    + includesCostSharing (optional) - Includes Cost Sharing. Maximum length is 1.
    + fcio (optional) - Prime Sponsor is PHS for FCOI regulation. Maximum length is 1.
    + invoicesEmailed (optional) - Invoices emailed. Maximum length is 1.
    + invoiceAddressDifferent (optional) - Invoice Address different from Financial contact's. Maximum length is 1.
    + invoiceEmailDifferent (optional) - Email different from Financial Contact's. Maximum length is 1.
    + fcioSubrecPolicyCd (optional) - Applicable FCOI policy for subrecipient. Maximum length is 3.
    + animalFlag (optional) - Animal Subjects Included. Maximum length is 1.
    + animalPteSendCd (optional) - Animal Subjects PTE requires verification to be sent. Maximum length is 3.
    + animalPteNrCd (optional) - Animal Subjects Not Required Reason. Maximum length is 3.
    + humanFlag (optional) - Human Subjects Included. Maximum length is 1.
    + humanPteSendCd (optional) - Human Subjects PTE requires verification to be sent. Maximum length is 3.
    + humanPteNrCd (optional) - Human Subjects Not Required Reason. Maximum length is 3.
    + humanDataExchangeAgreeCd (optional) - Human Subjects Data will be exchanged under this agreement. Maximum length is 3.
    + humanDataExchangeTermsCd (optional) - Human Subjects PTE will set forth the terms of exchange of human subjects data. Maximum length is 3.
    + additionalTerms (optional) - Additional Terms. Maximum length is 3500.
    + treatmentOfIncome (optional) - Treatment of Income. Maximum length is 60.
    + dataSharingAttachment (optional) - Data Sharing Attachment. Maximum length is 10.
    + dataSharingCd (optional) - Data Sharing. Maximum length is 4.
    + finalStatementDueCd (optional) - Final Statement Due. Maximum length is 4.

            
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json 

+ Response 200
    + Headers

            Content-Type: application/json;charset=UTF-8

    + Body
    
            [
              {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"},
              {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}
            ]
			
### Get Schema for Sub Award Template Infos [GET /subaward/api/v1/sub-award-template-infos/]
	                                          
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
    
            {"columns":["subAwardId","subAwardCode","sequenceNumber","sowOrSubProposalBudget","subProposalDate","invoiceOrPaymentContact","irbIacucContact","finalStmtOfCostscontact","changeRequestsContact","terminationContact","noCostExtensionContact","perfSiteDiffFromOrgAddr","perfSiteSameAsSubPiAddr","subRegisteredInCcr","subExemptFromReportingComp","parentDunsNumber","parentCongressionalDistrict","exemptFromRprtgExecComp","copyRightType","automaticCarryForward","carryForwardRequestsSentTo","treatmentPrgmIncomeAdditive","applicableProgramRegulations","applicableProgramRegsDate","mpiAward","mpiLeadershipPlan","rAndD","includesCostSharing","fcio","invoicesEmailed","invoiceAddressDifferent","invoiceEmailDifferent","fcioSubrecPolicyCd","animalFlag","animalPteSendCd","animalPteNrCd","humanFlag","humanPteSendCd","humanPteNrCd","humanDataExchangeAgreeCd","humanDataExchangeTermsCd","additionalTerms","treatmentOfIncome","dataSharingAttachment","dataSharingCd","finalStatementDueCd"],"primaryKey":"subAwardId"}
		
### Get Blueprint API specification for Sub Award Template Infos [GET /subaward/api/v1/sub-award-template-infos/]
	 
+ Parameters

     + _blueprint (required) - will instruct the endpoint to return an api blueprint markdown file for the resource
                 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: text/markdown

+ Response 200
    + Headers

            Content-Type: text/markdown;charset=UTF-8
            Content-Disposition:attachment; filename="Sub Award Template Infos.md"
            transfer-encoding:chunked
### Update Sub Award Template Infos [PUT /subaward/api/v1/sub-award-template-infos/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}
			
+ Response 204

### Update Multiple Sub Award Template Infos [PUT /subaward/api/v1/sub-award-template-infos/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"},
              {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 204
### Update Specific Attributes Sub Award Template Infos [PATCH /subaward/api/v1/sub-award-template-infos/(key)]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}
			
+ Response 204
    
    + Body
            
            {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}
### Insert Sub Award Template Infos [POST /subaward/api/v1/sub-award-template-infos/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}
			
+ Response 201
    
    + Body
            
            {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}
            
### Insert Multiple Sub Award Template Infos [POST /subaward/api/v1/sub-award-template-infos/]

+ Request

    + Headers

            Authorization: Bearer {api-key}   
            Content-Type: application/json

    + Body
    
            [
              {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"},
              {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}
            ]
			
+ Response 201
    
    + Body
            
            [
              {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"},
              {"subAwardId": "(val)","subAwardCode": "(val)","sequenceNumber": "(val)","sowOrSubProposalBudget": "(val)","subProposalDate": "(val)","invoiceOrPaymentContact": "(val)","irbIacucContact": "(val)","finalStmtOfCostscontact": "(val)","changeRequestsContact": "(val)","terminationContact": "(val)","noCostExtensionContact": "(val)","perfSiteDiffFromOrgAddr": "(val)","perfSiteSameAsSubPiAddr": "(val)","subRegisteredInCcr": "(val)","subExemptFromReportingComp": "(val)","parentDunsNumber": "(val)","parentCongressionalDistrict": "(val)","exemptFromRprtgExecComp": "(val)","copyRightType": "(val)","automaticCarryForward": "(val)","carryForwardRequestsSentTo": "(val)","treatmentPrgmIncomeAdditive": "(val)","applicableProgramRegulations": "(val)","applicableProgramRegsDate": "(val)","mpiAward": "(val)","mpiLeadershipPlan": "(val)","rAndD": "(val)","includesCostSharing": "(val)","fcio": "(val)","invoicesEmailed": "(val)","invoiceAddressDifferent": "(val)","invoiceEmailDifferent": "(val)","fcioSubrecPolicyCd": "(val)","animalFlag": "(val)","animalPteSendCd": "(val)","animalPteNrCd": "(val)","humanFlag": "(val)","humanPteSendCd": "(val)","humanPteNrCd": "(val)","humanDataExchangeAgreeCd": "(val)","humanDataExchangeTermsCd": "(val)","additionalTerms": "(val)","treatmentOfIncome": "(val)","dataSharingAttachment": "(val)","dataSharingCd": "(val)","finalStatementDueCd": "(val)","_primaryKey": "(val)"}
            ]
### Delete Sub Award Template Infos by Key [DELETE /subaward/api/v1/sub-award-template-infos/(key)]
	 
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Sub Award Template Infos [DELETE /subaward/api/v1/sub-award-template-infos/]

+ Parameters

      + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation

+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204

### Delete All Sub Award Template Infos with Matching [DELETE /subaward/api/v1/sub-award-template-infos/]

+ Parameters

    + _allowMulti (boolean, required) - flag to allow multiple resources to be deleted in one operation
    + subAwardId (optional) - 
    + subAwardCode (optional) - 
    + sequenceNumber (optional) - 
    + sowOrSubProposalBudget (optional) - SOW/Budget specified in proposal. Maximum length is 1.
    + subProposalDate (optional) - SubProposal Date. Maximum length is 21.
    + invoiceOrPaymentContact (optional) - Invoice / Payment Contact. Maximum length is 10.
    + irbIacucContact (optional) - IRB / IACUC Contact. Maximum length is 10.
    + finalStmtOfCostscontact (optional) - Final Statement of Costs Contact. Maximum length is 10.
    + changeRequestsContact (optional) - Change Requests Contact. Maximum length is 10.
    + terminationContact (optional) - Termination Contact. Maximum length is 10.
    + noCostExtensionContact (optional) - No Cost Extension Contact. Maximum length is 10.
    + perfSiteDiffFromOrgAddr (optional) - Performance Site same as Org address?. Maximum length is 1.
    + perfSiteSameAsSubPiAddr (optional) - 
    + subRegisteredInCcr (optional) - Sub Registered in CCR?. Maximum length is 1.
    + subExemptFromReportingComp (optional) - 
    + parentDunsNumber (optional) - Parent DUNS (if applicable). Maximum length is 20.
    + parentCongressionalDistrict (optional) - Parent Congressional District. Maximum length is 20.
    + exemptFromRprtgExecComp (optional) - Exempt from reporting exec compensation. Maximum length is 1.
    + copyRightType (optional) - Copyrights. Maximum length is 10.
    + automaticCarryForward (optional) - Automatic Carry Forward. Maximum length is 1.
    + carryForwardRequestsSentTo (optional) - Carry Forward Requests Sent To. Maximum length is 10.
    + treatmentPrgmIncomeAdditive (optional) - Treatment of Program Income Additive. Maximum length is 1.
    + applicableProgramRegulations (optional) - Applicable Program Regulations. Maximum length is 50.
    + applicableProgramRegsDate (optional) - Applicable Program Regulations Date. Maximum length is 21.
    + mpiAward (optional) - Is an MPI award. Maximum length is 1.
    + mpiLeadershipPlan (optional) - MPI Leadership Plan. Maximum length is 3.
    + rAndD (optional) - Is R&D. Maximum length is 1.
    + includesCostSharing (optional) - Includes Cost Sharing. Maximum length is 1.
    + fcio (optional) - Prime Sponsor is PHS for FCOI regulation. Maximum length is 1.
    + invoicesEmailed (optional) - Invoices emailed. Maximum length is 1.
    + invoiceAddressDifferent (optional) - Invoice Address different from Financial contact's. Maximum length is 1.
    + invoiceEmailDifferent (optional) - Email different from Financial Contact's. Maximum length is 1.
    + fcioSubrecPolicyCd (optional) - Applicable FCOI policy for subrecipient. Maximum length is 3.
    + animalFlag (optional) - Animal Subjects Included. Maximum length is 1.
    + animalPteSendCd (optional) - Animal Subjects PTE requires verification to be sent. Maximum length is 3.
    + animalPteNrCd (optional) - Animal Subjects Not Required Reason. Maximum length is 3.
    + humanFlag (optional) - Human Subjects Included. Maximum length is 1.
    + humanPteSendCd (optional) - Human Subjects PTE requires verification to be sent. Maximum length is 3.
    + humanPteNrCd (optional) - Human Subjects Not Required Reason. Maximum length is 3.
    + humanDataExchangeAgreeCd (optional) - Human Subjects Data will be exchanged under this agreement. Maximum length is 3.
    + humanDataExchangeTermsCd (optional) - Human Subjects PTE will set forth the terms of exchange of human subjects data. Maximum length is 3.
    + additionalTerms (optional) - Additional Terms. Maximum length is 3500.
    + treatmentOfIncome (optional) - Treatment of Income. Maximum length is 60.
    + dataSharingAttachment (optional) - Data Sharing Attachment. Maximum length is 10.
    + dataSharingCd (optional) - Data Sharing. Maximum length is 4.
    + finalStatementDueCd (optional) - Final Statement Due. Maximum length is 4.

      
+ Request

    + Headers

            Authorization: Bearer {api-key}
            Content-Type: application/json

+ Response 204
