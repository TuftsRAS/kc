/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document.service;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ErrorMessage;

import java.util.List;


public interface CommonApiService {

    void validatePerson(String personId, Integer rolodexId);

    <T extends Object> T convertObject(Object input, Class<T> clazz);

    Document getDocumentFromDocId(Long documentNumber);

    void routeDocument(Document document);

    String getValidationErrors();

    Document saveDocument(Document document) throws WorkflowException;

    boolean isDocInModifiableState(WorkflowDocument workflowDocument);

    List<ErrorMessage> getAuditErrors(Document document);

    void clearErrors();

    void updateDataObjectFromDto(Object existingDataObject, Object input);
}
