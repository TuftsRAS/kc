/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.proposal.impl.report;

import org.kuali.coeus.common.framework.print.*;
import org.kuali.coeus.common.proposal.framework.report.CurrentAndPendingReportService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Transactional
@Component("currentAndPendingReportService")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CurrentAndPendingReportServiceImpl implements CurrentAndPendingReportService {

    @Autowired
    @Qualifier("currentReportDao")
    private CurrentReportDao currentReportDao;

    @Autowired
    @Qualifier("pendingReportDao")
    private PendingReportDao pendingReportDao;

    @Autowired
    @Qualifier("printingService")
    private PrintingService printingService;

    @Autowired
    @Qualifier("currentProposalPrint")
    private CurrentProposalPrint currentProposalPrint;

    @Autowired
    @Qualifier("pendingProposalPrint")
    private PendingProposalPrint pendingProposalPrint;

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    // setters for dependency injection
    public void setCurrentReportDao(CurrentReportDao currentReportDao) {
        this.currentReportDao = currentReportDao;
    }
    public void setPendingReportDao(PendingReportDao pendingReportDao) {
        this.pendingReportDao = pendingReportDao;
    }
    public void setCurrentProposalPrint(CurrentProposalPrint currentProposalPrint) {
        this.currentProposalPrint = currentProposalPrint;
    }
    public void setPendingProposalPrint(PendingProposalPrint pendingProposalPrint) {
        this.pendingProposalPrint = pendingProposalPrint;
    }
    public void setPrintingService(PrintingService printingService) {
        this.printingService = printingService;
    }

    @Override
    public List<CurrentReportBean> loadCurrentReportData(String personId) {
        List<CurrentReportBean> data;
        try {
            data = currentReportDao.queryForCurrentSupport(personId);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public List<PendingReportBean> loadPendingReportData(String personId) {
        List<PendingReportBean> data;
        try {
            Collection<String> excludedProposalTypes = getParameterService().getParameterValuesAsString(InstitutionalProposalDocument.class, Constants.EXCLUDED_CP_PROPOSAL_TYPE_CODES_PARAM);

            data = pendingReportDao.queryForPendingSupport(personId, excludedProposalTypes);
        } catch(WorkflowException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public AttachmentDataSource printCurrentReport(Map<String, Object> reportParameters) throws PrintingException {
        reportParameters.put(PrintConstants.CURRENT_REPORT_BEANS_KEY, loadCurrentReportData((String)reportParameters.get(PrintConstants.PERSON_ID_KEY)));
        AbstractPrint printable = currentProposalPrint;

        printable.setPrintableBusinessObject(null);
        printable.setReportParameters(reportParameters);
        AttachmentDataSource source = printingService.print(printable);
        source.setName(PrintConstants.CURRENT_REPORT_TYPE.replace(' ', '_')+Constants.PDF_FILE_EXTENSION);
        return source;
    }

    @Override
    public AttachmentDataSource printPendingReport(Map<String, Object> reportParameters) throws PrintingException {
        reportParameters.put(PrintConstants.PENDING_REPORT_BEANS_KEY, loadPendingReportData((String)reportParameters.get(PrintConstants.PERSON_ID_KEY)));
        AbstractPrint printable = pendingProposalPrint;

        printable.setPrintableBusinessObject(null);
        printable.setReportParameters(reportParameters);
        AttachmentDataSource source= printingService.print(printable);
        source.setName(PrintConstants.PENDING_REPORT_TYPE.replace(' ', '_')+Constants.PDF_FILE_EXTENSION);
        return source;
    }

    protected ParameterService getParameterService() {
        return parameterService;
    }
}
