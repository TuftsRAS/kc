/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.common.web.struts.form;

import org.kuali.coeus.common.framework.person.KcPerson;
import org.kuali.coeus.common.framework.person.KcPersonService;
import org.kuali.coeus.common.framework.print.CurrentReportBean;
import org.kuali.coeus.common.framework.print.PendingReportBean;
import org.kuali.coeus.common.proposal.framework.report.CurrentAndPendingReportService;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.kra.institutionalproposal.proposaladmindetails.ProposalAdminDetails;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  Helper to prepare Current and Pending Report
 */
public class ReportHelperBean implements Serializable {
    private KualiDocumentFormBase form;
    private String personId;
    private KcPerson targetPerson;
    private boolean institutionalProposalExists;
    private String proposalNumber;
    private static final String DEV_PROPOSAL_NUMBER_FIELD_NAME = "devProposalNumber";
    protected static final String PROP_NUMBER = "proposalNumber";
    public static final String PROP_SEQ_STATUS = "ACTIVE";
    public static final int PROP_PENDING_STATUS = 1;

    public ReportHelperBean(KualiDocumentFormBase form) {
        this.form = form;
        setTargetPerson(new KcPerson());
        if(form.getDocument() instanceof InstitutionalProposalDocument) {
            institutionalProposalExists = true;
            proposalNumber = findProposalNumberFromInstitutionalProposal();
        } else if(form.getDocument() instanceof ProposalDevelopmentDocument) {
            institutionalProposalExists = doesInstitutionalProposalExistForProposalNumber();
            proposalNumber = findProposalNumberFromDevelopmentProposal();
        }
    }
    public ReportHelperBean() {
        setTargetPerson(new KcPerson());
        
    }
    public String getPersonId() {
        return personId;
    }

    public String getProposalNumber() {
        return proposalNumber;
    }

    public KcPerson getTargetPerson() {
        return targetPerson;
    }

    public boolean isInstituteProposalAvailable() {
        return institutionalProposalExists;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
        if(personId != null) {
            targetPerson = getKcPersonService().getKcPersonByPersonId(personId);
        }
    }

    public void setTargetPerson(KcPerson targetPerson) {
        this.targetPerson = targetPerson;
    }

    public List<ResultRow> prepareCurrentReport() {
        return new CurrentReportHelperBean().prepareCurrentReport();
    }

    public List<ResultRow> preparePendingReport() {
        return new PendingReportHelperBean().preparePendingReport();
    }

    public String getTargetPersonName() {
        return targetPerson.getFullName();
    }

    protected boolean doesInstitutionalProposalExistForProposalNumber() {
        Map map = Collections.singletonMap(DEV_PROPOSAL_NUMBER_FIELD_NAME, findProposalNumberFromDevelopmentProposal());
        return ((Collection<ProposalAdminDetails>)getBusinessObjectService().findMatching(ProposalAdminDetails.class, map)).stream()
        	.anyMatch(adminDetails -> adminDetails.getInstProposalId() != null);
    }

    protected BusinessObjectService getBusinessObjectService() {
        return KcServiceLocator.getService(BusinessObjectService.class);
    }

    protected KcPersonService getKcPersonService() {
        return KcServiceLocator.getService(KcPersonService.class);
    }

    protected CurrentAndPendingReportService getCurrentAndPendingReportService() {
        return KcServiceLocator.getService(CurrentAndPendingReportService.class);
    }

    private String findProposalNumberFromDevelopmentProposal() {
        return ((ProposalDevelopmentDocument) form.getDocument()).getDevelopmentProposal().getProposalNumber();
    }

    private String findProposalNumberFromInstitutionalProposal() {
        return ((InstitutionalProposalDocument) form.getDocument()).getInstitutionalProposal().getProposalNumber();
    }

    private class PendingReportHelperBean implements Serializable {

        public List<ResultRow> preparePendingReport() {
            return loadReportData().stream().map(PendingReportBean::createResultRow).collect(Collectors.toList());
        }

        private List<PendingReportBean> loadReportData() {
            return getCurrentAndPendingReportService().loadPendingReportData(personId);
        }
    }

    private class CurrentReportHelperBean implements Serializable {
        public List<ResultRow> prepareCurrentReport() {
            List<ResultRow> resultRows = new ArrayList<ResultRow>();
            for(CurrentReportBean bean: loadReportData()) {
                resultRows.add(bean.createResultRow());
            }

            return resultRows;
        }

        private List<CurrentReportBean> loadReportData() {
            return getCurrentAndPendingReportService().loadCurrentReportData(personId);
        }
    }
}
