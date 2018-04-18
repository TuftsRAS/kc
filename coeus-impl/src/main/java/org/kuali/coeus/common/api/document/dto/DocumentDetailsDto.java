/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document.dto;

public class DocumentDetailsDto {

    String documentNumber;
    Long documentCreateDate;
    String documentType;
    String documentTitle;
    Integer stepsAway;
    String docHandlerUrl;

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Long getDocumentCreateDate() {
        return documentCreateDate;
    }

    public void setDocumentCreateDate(Long documentCreateDate) {
        this.documentCreateDate = documentCreateDate;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Integer getStepsAway() {
        return stepsAway;
    }

    public void setStepsAway(Integer stepsAway) {
        this.stepsAway = stepsAway;
    }

    public String getDocHandlerUrl() {
        return docHandlerUrl;
    }

    public void setDocHandlerUrl(String docHandlerUrl) {
        this.docHandlerUrl = docHandlerUrl;
    }

}
