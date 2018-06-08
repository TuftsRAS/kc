package org.kuali.coeus.common.api.document.service;

import org.kuali.rice.kew.api.document.DocumentDetail;

public interface WorkflowDetailsService {

    void simulateWorkflowOnAllDocuments();
    void calculateAndPersistDetailsForUsersInRouteLog(String documentId, DocumentDetail documentDetail);
    void generateDetailsFromSimulation(String documentId);
    DocumentDetail getDocumentDetail(String documentId);

}
