/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.dataovveride;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.*;
import org.kuali.coeus.propdev.impl.dataovveride.common.CommonDataOverrideService;
import org.kuali.coeus.propdev.impl.editable.ProposalChangedData;
import org.kuali.coeus.propdev.impl.editable.ProposalColumnsToAlter;
import org.kuali.coeus.propdev.impl.editable.ProposalDataOverrideEvent;
import org.kuali.coeus.propdev.impl.notification.NotificationControllerService;
import org.kuali.coeus.propdev.impl.notification.ProposalDevelopmentNotificationContext;
import org.kuali.coeus.propdev.impl.notification.ProposalDevelopmentNotificationRenderer;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.krad.uif.UifConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class ProposalDevelopmentDataOverrideController extends ProposalDevelopmentControllerBase {

    @Autowired
    @Qualifier("notificationControllerService")
    private NotificationControllerService notificationControllerService;

    @Autowired
    @Qualifier("commonDataOverrideService")
    private CommonDataOverrideService commonDataOverrideService;

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=prepareDataOverride")
    public ModelAndView prepareDataOverride(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form)
            throws Exception {

        String columnName = form.getNewProposalChangedData().getColumnName();
        if (StringUtils.isNotEmpty(columnName)){
        form.getNewProposalChangedData().setEditableColumn(getDataObjectService().find(ProposalColumnsToAlter.class,
                columnName));

        String propertyValue = getCommonDataOverrideService().getChangedValue(form.getDevelopmentProposal(),form.getNewProposalChangedData().getAttributeName());

        form.getNewProposalChangedData().setDisplayValue(propertyValue);
        form.getNewProposalChangedData().setOldDisplayValue(propertyValue);
        } else {
            form.setNewProposalChangedData(new ProposalChangedData());
        }
        form.setUpdateComponentId("PropDev-DataOverride-Dialog");
        form.setAjaxReturnType(UifConstants.AjaxReturnTypes.UPDATECOMPONENT.getKey());
        return getRefreshControllerService().refresh(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=createOverride")
    public ModelAndView createOverride(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form)
            throws Exception {
        ProposalDevelopmentDocument pdDocument = form.getProposalDevelopmentDocument();
        ProposalChangedData newProposalChangedData = form.getNewProposalChangedData();


        int changeNumber = pdDocument.getDocumentNextValue("proposalDevelopment.proposalChangedDataList.changeNumber");
        newProposalChangedData.setProposalNumber(pdDocument.getDevelopmentProposal().getProposalNumber());
        newProposalChangedData.setChangeNumber(changeNumber);
        newProposalChangedData.setDisplayValue(newProposalChangedData.getChangedValue());

        if(getKualiRuleService().applyRules(new ProposalDataOverrideEvent(pdDocument, newProposalChangedData))){
            getCommonDataOverrideService().setChangedValue(pdDocument.getDevelopmentProposal(),newProposalChangedData.getAttributeName(), newProposalChangedData.getChangedValue());
            growProposalChangedHistory(pdDocument, newProposalChangedData);
            List<ProposalChangedData> proposalChangedDataList= new ArrayList<>();
            proposalChangedDataList.add(newProposalChangedData);
            proposalChangedDataList.addAll(form.getDevelopmentProposal().getProposalChangedDataList());
            form.getDevelopmentProposal().setProposalChangedDataList(proposalChangedDataList);

            super.save(form);
            String value = getCommonDataOverrideService().getDisplayReferenceValue(form.getDevelopmentProposal(), newProposalChangedData.getAttributeName(), DevelopmentProposal.class);
            if (value != null) {
                newProposalChangedData.setDisplayValue(value);
            }
            form.setNewProposalChangedData(new ProposalChangedData());

            ProposalDevelopmentNotificationContext context =
                    new ProposalDevelopmentNotificationContext(pdDocument.getDevelopmentProposal(), Constants.PROPOSAL_DATA_OVVERRIDE_ACTION_TYPE_CODE, Constants.DATA_OVERRIDE_CONTEXT);
            ((ProposalDevelopmentNotificationRenderer) context.getRenderer()).setProposalChangedData(newProposalChangedData);
            ((ProposalDevelopmentNotificationRenderer) context.getRenderer()).setDevelopmentProposal(pdDocument.getDevelopmentProposal());

            getNotificationControllerService().sendNotificationIfNoErrors(form, context);

        }

       return getRefreshControllerService().refresh(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=sendOverrideNotification")
    public ModelAndView sendOverrideNotification(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        return getNotificationControllerService().sendNotification(form);
    }

    private void growProposalChangedHistory(ProposalDevelopmentDocument pdDocument, ProposalChangedData newProposalChangedData) {
        Map<String, List<ProposalChangedData>> changeHistory = pdDocument.getDevelopmentProposal().getProposalChangeHistory();
        changeHistory.computeIfAbsent(newProposalChangedData.getEditableColumn().getColumnLabel(), k -> new ArrayList<>());
        changeHistory.get(newProposalChangedData.getEditableColumn().getColumnLabel()).add(0, newProposalChangedData);
    }

    public NotificationControllerService getNotificationControllerService() {
        return notificationControllerService;
    }

    public void setNotificationControllerService(NotificationControllerService notificationControllerService) {
        this.notificationControllerService = notificationControllerService;
    }

    public CommonDataOverrideService getCommonDataOverrideService() {
        return commonDataOverrideService;
    }

    public void setCommonDataOverrideService(CommonDataOverrideService commonDataOverrideService) {
        this.commonDataOverrideService = commonDataOverrideService;
    }
}
