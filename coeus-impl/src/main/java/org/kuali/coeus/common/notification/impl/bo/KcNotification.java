/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.notification.impl.bo;

import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Defines a document-specific instance of a Notification Type.
 */
public class KcNotification extends KcPersistableBusinessObjectBase {

    private static final long serialVersionUID = 8649080269418978865L;

    private Long notificationId;

    private Long notificationTypeId;

    private String documentNumber;

    private String recipients;
    
    private String subject;

    private String message;

    private Long owningDocumentIdFk;

    private NotificationType notificationType;

    private String createUser;
    
    private Timestamp createTimestamp;
    
    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(Long notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getNotificationType() {
        if (notificationType == null) {
            this.refreshReferenceObject("notificationType");
        }
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Long getOwningDocumentIdFk() {
        return owningDocumentIdFk;
    }

    public void setOwningDocumentIdFk(Long owningDocumentIdFk) {
        this.owningDocumentIdFk = owningDocumentIdFk;
    }
    
    
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Timestamp getCreateTimestamp() {
        // fall back to update timestamp for backwards compatibility
        return createTimestamp != null ? createTimestamp : getUpdateTimestamp();
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getUpdateTimestampString() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        return (getCreateTimestamp() == null ? "" : dateFormat.format(getCreateTimestamp()));
    }

    public void persistOwningObject(KcPersistableBusinessObjectBase object) {
        KcServiceLocator.getService(BusinessObjectService.class).save(object);
    }

    public void resetPersistenceState() {
        setNotificationId(null);
        setOwningDocumentIdFk(null);
    }
    
    @Override
    protected void prePersist() {
        super.prePersist();
        if (StringUtils.isEmpty(createUser)) {
            createUser = GlobalVariables.getUserSession().getPrincipalName();
            createTimestamp = KcServiceLocator.getService(DateTimeService.class).getCurrentTimestamp();  
        }
    }

}
