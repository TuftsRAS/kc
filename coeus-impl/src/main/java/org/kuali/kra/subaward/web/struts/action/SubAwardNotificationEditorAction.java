/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.subaward.web.struts.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.coeus.common.notification.impl.bo.KcNotification;
import org.kuali.coeus.common.notification.impl.bo.NotificationTypeRecipient;
import org.kuali.coeus.common.notification.impl.rule.event.AddNotificationRecipientEvent;
import org.kuali.coeus.common.notification.impl.rule.event.SendNotificationEvent;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.subaward.SubAwardForm;
import org.kuali.kra.subaward.document.SubAwardDocument;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SubAwardNotificationEditorAction extends SubAwardAction {
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.execute(mapping, form, request, response);
        SubAwardForm subAwardForm = (SubAwardForm) form;
        subAwardForm.getNotificationHelper().prepareView();
        return actionForward;
    }
    
    /**
     * Adds a Notification Recipient.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addNotificationRecipient(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
        throws Exception {
        
        SubAwardForm subAwardForm = (SubAwardForm) form;
        SubAwardDocument document = subAwardForm.getSubAwardDocument();
        NotificationTypeRecipient notificationRecipient = subAwardForm.getNotificationHelper().getNewNotificationRecipient();
        List<NotificationTypeRecipient> notificationRecipients = subAwardForm.getNotificationHelper().getNotificationRecipients();
        
        if (applyRules(new AddNotificationRecipientEvent(document, notificationRecipient, notificationRecipients))) {
            subAwardForm.getNotificationHelper().getNotificationRecipients().add(notificationRecipient);
            subAwardForm.getNotificationHelper().setNewNotificationRecipient(new NotificationTypeRecipient());
            subAwardForm.getNotificationHelper().setNewRoleId(null);
            subAwardForm.getNotificationHelper().setNewPersonId(null);
            subAwardForm.getNotificationHelper().setNewRolodexId(null);
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Deletes a Notification Recipient.
     * 
     * @param mapping the action mapping
     * @param form the action form
     * @param request the request
     * @param response the response
     * @return the action forward
     * @throws Exception
     */
    public ActionForward deleteNotificationRecipient(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
        throws Exception {

        SubAwardForm subAwardForm = (SubAwardForm) form;
        
        subAwardForm.getNotificationHelper().getNotificationRecipients().remove(getLineToDelete(request));
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Sends a Notification.
     * 
     * @param mapping the action mapping
     * @param form the action form
     * @param request the request
     * @param response the response
     * @return the action forward
     * @throws Exception
     */
    public ActionForward sendNotification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = mapping.findForward(Constants.MAPPING_BASIC);
        
        SubAwardForm subAwardForm = (SubAwardForm) form;
        SubAwardDocument document = subAwardForm.getSubAwardDocument();
        KcNotification notification = subAwardForm.getNotificationHelper().getNotification();
        List<NotificationTypeRecipient> notificationRecipients = subAwardForm.getNotificationHelper().getNotificationRecipients();
        
        if (applyRules(new SendNotificationEvent(document, notification, notificationRecipients))) {
            subAwardForm.getNotificationHelper().sendNotification();
            
            String forwardName = subAwardForm.getNotificationHelper().getNotificationContext().getForwardName();
            if (StringUtils.isNotBlank(forwardName)) {
                actionForward = mapping.findForward(forwardName);
            }
            
        }

        return actionForward;
    }

    /**
     * Cancels a Notification.
     * 
     * @param mapping the action mapping
     * @param form the action form
     * @param request the request
     * @param response the response
     * @return the action forward
     * @throws Exception
     */
    public ActionForward cancelNotification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SubAwardForm subAwardForm = (SubAwardForm) form;
        
        subAwardForm.getNotificationHelper().setNotificationContext(null);
        
        return mapping.findForward(Constants.MAPPING_AWARD_ACTIONS_PAGE);
    }
    
}
