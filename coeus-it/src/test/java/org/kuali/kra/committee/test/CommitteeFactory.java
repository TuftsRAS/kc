/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.committee.test;

import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.bo.DocumentNextvalue;
import org.kuali.kra.committee.bo.Committee;
import org.kuali.kra.committee.document.CommitteeDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

import java.util.ArrayList;

public class CommitteeFactory {

    protected static final String DEFAULT_DOCUMENT_DESCRIPTION = "Committee Web Test";
    protected static final String DEFAULT_TYPE_CODE = "1"; // IRB
    protected static final Integer DEFAULT_MAX_PROTOCOLS = 10;
    protected static final String DEFAULT_HOME_UNIT_NUMBER = "000001";
    protected static final Integer DEFAULT_MIN_MEMBERS_REQUIRED = 3;
    protected static final String DEFAULT_NAME = "Committee Test ";
    protected static final Integer DEFAULT_ADV_SUBMISSION_DAYS_REQUIRED = 1;
    protected static final String DEFAULT_REVIEW_TYPE_CODE = "1"; // FULL
  
    public static CommitteeDocument createCommitteeDocument(String committeeId) throws WorkflowException {
        DocumentService documentService = KcServiceLocator.getService(DocumentService.class);
        CommitteeDocument committeeDocument = (CommitteeDocument) documentService.getNewDocument("CommitteeDocument");
        setRequiredFields(committeeDocument, committeeId);
        documentService.saveDocument(committeeDocument);
        return committeeDocument;
    }

    private static void setRequiredFields(CommitteeDocument document, String committeeId) {
        Committee committee = document.getCommittee();
        document.getDocumentHeader().setDocumentDescription(DEFAULT_DOCUMENT_DESCRIPTION);
        document.setDocumentNextvalues(new ArrayList<DocumentNextvalue>());
        document.setCommitteeId(committeeId);
        committee.setCommitteeDocument(document);
        committee.setCommitteeId(committeeId);
        committee.setCommitteeTypeCode(DEFAULT_TYPE_CODE);
        committee.setMaxProtocols(DEFAULT_MAX_PROTOCOLS);
        committee.setHomeUnitNumber(DEFAULT_HOME_UNIT_NUMBER);
        committee.setMinimumMembersRequired(DEFAULT_MIN_MEMBERS_REQUIRED);
        committee.setCommitteeName(DEFAULT_NAME + committeeId);
        committee.setAdvancedSubmissionDaysRequired(DEFAULT_ADV_SUBMISSION_DAYS_REQUIRED);
        committee.setReviewTypeCode(DEFAULT_REVIEW_TYPE_CODE);
    }
}
