/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.notification;

import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.onlinereview.ProtocolOnlineReviewBase;

import java.io.Serializable;

public abstract class ProtocolNotificationRequestBeanBase implements Serializable {
    

    private static final long serialVersionUID = -1481201763594063662L;
    
    private ProtocolBase protocol;
    private ProtocolOnlineReviewBase protocolOnlineReview;
    private String actionType;
    private String description;
    private String docNumber;
    private String olrEvent;
    private String committeeName;

    public ProtocolNotificationRequestBeanBase(ProtocolBase protocol, String actionType, String description) {
        this.protocol = protocol;
        this.actionType = actionType;
        this.description = description;
        
    }
    
    public ProtocolNotificationRequestBeanBase(ProtocolBase protocol, ProtocolOnlineReviewBase protocolOnlineReview, String actionType, String description, String docNumber, String olrEvent) {
        this(protocol, actionType, description);
        this.protocolOnlineReview = protocolOnlineReview;
        this.olrEvent = olrEvent;
        this.docNumber = docNumber;
        
    }
    
    public ProtocolBase getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolBase protocol) {
        this.protocol = protocol;
    }

    public ProtocolOnlineReviewBase getProtocolOnlineReview() {
        return protocolOnlineReview;
    }

    public void setProtocolOnlineReview(ProtocolOnlineReviewBase protocolOnlineReview) {
        this.protocolOnlineReview = protocolOnlineReview;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getOlrEvent() {
        return olrEvent;
    }

    public void setOlrEvent(String olrEvent) {
        this.olrEvent = olrEvent;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public void setCommitteeName(String committeeName) {
        this.committeeName = committeeName;
    }

        
}
