/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.proposal.impl.report;

import org.apache.xmlbeans.XmlObject;
import org.kuali.coeus.common.framework.print.PendingReportBean;
import org.kuali.coeus.common.framework.print.PrintConstants;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.kra.institutionalproposal.customdata.InstitutionalProposalCustomData;
import org.kuali.kra.printing.schema.CurrentAndPendingSupportDocument;
import org.kuali.kra.printing.schema.CurrentAndPendingSupportDocument.CurrentAndPendingSupport;
import org.kuali.kra.printing.schema.CurrentAndPendingSupportDocument.CurrentAndPendingSupport.PendingReportCEColumnNames;
import org.kuali.kra.printing.schema.CurrentAndPendingSupportDocument.CurrentAndPendingSupport.PendingSupport;
import org.kuali.kra.printing.schema.CurrentAndPendingSupportDocument.CurrentAndPendingSupport.PendingSupport.PendingReportCEColomnValues;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class generates XML that confirms with the XSD related to Pending
 * Proposal Report. The data for XML is derived from
 * {@link org.kuali.coeus.sys.framework.model.KcTransactionalDocumentBase} and {@link Map} of details passed to the class.
 */
@Component("pendingProposalXmlStream")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PendingProposalXmlStream extends CurrentAndPendingBaseStream {

    /**
     * This method generates XML for Pending Proposal Report. It uses data
     * passed in {@link org.kuali.coeus.sys.framework.model.KcTransactionalDocumentBase} for populating the XML nodes. The
     * XMl once generated is returned as {@link XmlObject}
     *
     * @param printableBusinessObject using which XML is generated
     * @param reportParameters        parameters related to XML generation
     * @return {@link XmlObject} representing the XML
     */
    @Override
    public Map<String, XmlObject> generateXmlStream(
            KcPersistableBusinessObjectBase printableBusinessObject, Map<String, Object> reportParameters) {
        Map<String, XmlObject> xmlObjectList = new LinkedHashMap<>();
        CurrentAndPendingSupportDocument currentAndPendingSupportDocument = CurrentAndPendingSupportDocument.Factory.newInstance();
        CurrentAndPendingSupport currentAndPendingSupport = CurrentAndPendingSupport.Factory
                .newInstance();
        List<PendingReportBean> pendingReportBeans = (List<PendingReportBean>) reportParameters.get(PrintConstants.PENDING_REPORT_BEANS_KEY);

        List<String> columnsList = getColumnList(pendingReportBeans);

        PendingReportCEColumnNames pendingReportCEColumnNames = getPendingSupportCustomColumnName(columnsList);
        PendingSupport[] pendingSupports = getPendingSupportInformation(pendingReportBeans, columnsList);

        currentAndPendingSupport.setPersonName((String) reportParameters.get(PrintConstants.REPORT_PERSON_NAME_KEY));
        currentAndPendingSupport.setPendingSupportArray(pendingSupports);
        currentAndPendingSupport.setPendingReportCEColumnNames(pendingReportCEColumnNames);
        currentAndPendingSupportDocument.setCurrentAndPendingSupport(currentAndPendingSupport);
        xmlObjectList.put(PrintConstants.PENDING_REPORT_TYPE, currentAndPendingSupportDocument);
        return xmlObjectList;
    }

    private List<String> getColumnList(List<PendingReportBean> pendingReportBeans) {
        List<String> columsList = new ArrayList<>();
        String columnName = "";

        for (PendingReportBean bean : pendingReportBeans) {
            if (bean.getInstitutionalProposalCustomDataList() != null) {
                for (InstitutionalProposalCustomData institutionalProposalCustomData : bean.getInstitutionalProposalCustomDataList()) {
                    if (institutionalProposalCustomData.getCustomAttribute() != null) {
                        columnName = institutionalProposalCustomData.getCustomAttribute().getLabel();
                    }
                    if (!columsList.contains(columnName)) {
                        columsList.add(columnName);
                    }
                }
            }
        }

        return columsList;
    }

    private PendingReportCEColumnNames getPendingSupportCustomColumnName(List<String> columnsList) {
        PendingReportCEColumnNames pendingReportCEColumnNames = PendingReportCEColumnNames.Factory.newInstance();

        for (int columnLabelIndex = 0; columnLabelIndex < columnsList.size(); columnLabelIndex++) {
            if (columnLabelIndex == 0) {
                pendingReportCEColumnNames.setCEColumnName1(columnsList.get(columnLabelIndex));
            }
            if (columnLabelIndex == 1) {
                pendingReportCEColumnNames.setCEColumnName2(columnsList.get(columnLabelIndex));
            }
            if (columnLabelIndex == 2) {
                pendingReportCEColumnNames.setCEColumnName3(columnsList.get(columnLabelIndex));
            }
            if (columnLabelIndex == 3) {
                pendingReportCEColumnNames.setCEColumnName4(columnsList.get(columnLabelIndex));
            }
            if (columnLabelIndex == 4) {
                pendingReportCEColumnNames.setCEColumnName5(columnsList.get(columnLabelIndex));
            }
            if (columnLabelIndex == 5) {
                pendingReportCEColumnNames.setCEColumnName6(columnsList.get(columnLabelIndex));
            }
            if (columnLabelIndex == 6) {
                pendingReportCEColumnNames.setCEColumnName7(columnsList.get(columnLabelIndex));
            }
            if (columnLabelIndex == 7) {
                pendingReportCEColumnNames.setCEColumnName8(columnsList.get(columnLabelIndex));
            }
            if (columnLabelIndex == 8) {
                pendingReportCEColumnNames.setCEColumnName9(columnsList.get(columnLabelIndex));
            }
            if (columnLabelIndex == 9) {
                pendingReportCEColumnNames.setCEColumnName10(columnsList.get(columnLabelIndex));
            }
        }

        return pendingReportCEColumnNames;
    }

    private PendingSupport[] getPendingSupportInformation(List<PendingReportBean> pendingReportBeans, List<String> columnsList) {
        List<PendingSupport> pendingSupports = new ArrayList<>();

        for (PendingReportBean bean : pendingReportBeans) {
            Map<String, String> customDataValueMap = new HashMap<>();

            PendingSupport pendingSupport = PendingSupport.Factory.newInstance();
            pendingSupports.add(pendingSupport);

            if (bean.getProposalTitle() != null) {
                pendingSupport.setTitle(bean.getProposalTitle());
            }
            if (bean.getTotalDirectCostTotal() != null) {
                pendingSupport.setTotalDirectCost(bean.getTotalDirectCostTotal().bigDecimalValue());
            }
            if (bean.getTotalIndirectCostTotal() != null) {
                pendingSupport.setTotalIndirectCost(bean.getTotalIndirectCostTotal().bigDecimalValue());
            }
            if (bean.getTotalRequestedCost() != null) {
                pendingSupport.setTotalRequested(bean.getTotalRequestedCost().bigDecimalValue());
            }
            if (bean.getProposalNumber() != null) {
                pendingSupport.setProposalNumber(bean
                        .getProposalNumber());
            }
            if (bean.getRequestedEndDateTotal() != null) {
                pendingSupport
                        .setEndDate(dateTimeService.getCalendar(bean
                                .getRequestedEndDateTotal()));
            }
            if (bean.getRequestedStartDateInitial() != null) {
                pendingSupport.setEffectiveDate(dateTimeService
                        .getCalendar(bean
                                .getRequestedStartDateInitial()));
            }
            if (bean.getSponsorName() != null) {
                pendingSupport.setAgency(bean.getSponsorName());
            }
            if (bean.getRoleCode() != null) {
                pendingSupport.setPI(bean.getRoleCode());
            }
            if (bean.getTotalEffort() != null) {
                pendingSupport.setPercentageEffort(bean.getTotalEffort().bigDecimalValue());
            }
            if (bean.getAcademicYearEffort() != null) {
                pendingSupport.setAcademicYearEffort(bean.getAcademicYearEffort().bigDecimalValue());
            }
            if (bean.getCalendarYearEffort() != null) {
                pendingSupport.setCalendarYearEffort(bean.getCalendarYearEffort().bigDecimalValue());
            }
            if (bean.getSummerEffort() != null) {
                pendingSupport.setSummerYearEffort(bean.getSummerEffort().bigDecimalValue());
            }
            if (bean.getInstitutionalProposalCustomDataList() != null) {
                List<PendingReportCEColomnValues> pendingReportCEColomnValues = new ArrayList<>();
                for (InstitutionalProposalCustomData institutionalProposalCustomData : bean.getInstitutionalProposalCustomDataList()) {
                    if (institutionalProposalCustomData.getCustomAttribute() != null && institutionalProposalCustomData.getValue() != null && institutionalProposalCustomData.getCustomAttribute().getLabel() != null) {
                        customDataValueMap.put(institutionalProposalCustomData.getCustomAttribute().getLabel(), institutionalProposalCustomData.getValue());
                    }
                }
                for (int columnLabelIndex = 0; columnLabelIndex < columnsList.size(); columnLabelIndex++) {
                    PendingReportCEColomnValues pendingReportCEColomnValue = PendingReportCEColomnValues.Factory.newInstance();
                    if (customDataValueMap.get(columnsList.get(columnLabelIndex)) != null)
                        pendingReportCEColomnValue.setPendingReportCEColumnValue(customDataValueMap.get(columnsList.get(columnLabelIndex)));
                    else {
                        pendingReportCEColomnValue.setPendingReportCEColumnValue("");
                    }
                    pendingReportCEColomnValues.add(pendingReportCEColomnValue);
                }
                pendingSupport.setPendingReportCEColomnValuesArray(pendingReportCEColomnValues.toArray(new PendingReportCEColomnValues[0]));
            }
        }

        return pendingSupports.toArray(new PendingSupport[0]);
    }

}
