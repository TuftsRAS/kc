/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.subaward.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.version.VersionException;
import org.kuali.coeus.common.framework.version.VersionStatus;
import org.kuali.coeus.common.framework.version.VersioningService;
import org.kuali.coeus.common.framework.version.history.VersionHistory;
import org.kuali.coeus.common.framework.version.history.VersionHistoryService;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.bo.SubAwardAmountInfo;
import org.kuali.kra.subaward.bo.SubAwardAmountReleased;
import org.kuali.kra.subaward.bo.SubAwardFundingSource;
import org.kuali.kra.subaward.dao.SubAwardDao;
import org.kuali.kra.subaward.document.SubAwardDocument;
import org.kuali.kra.subaward.service.SubAwardService;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.SequenceAccessorService;

import java.sql.Date;
import java.util.*;

public class SubAwardServiceImpl implements SubAwardService {

    private static final Logger LOG = LogManager.getLogger(SubAwardServiceImpl.class);
    public static final String SUB_AWARD_CODE = "subAwardCode";
    public static final String SUB_AWARD_SEQUENCE_STATUS = "subAwardSequenceStatus";
    public static final String SUBAWARD_FOLLOW_UP = "Subaward Follow Up";
    public static final String AWARD_AWARD_NUMBER = "award.awardNumber";

    private BusinessObjectService businessObjectService;
    private VersioningService versioningService;
    private VersionHistoryService versionHistoryService;
    private DocumentService documentService;
    private SequenceAccessorService sequenceAccessorService;
    private ParameterService parameterService;
    private SubAwardDao subAwardDao;

    @Override
    public SubAwardDocument createNewSubAwardVersion(SubAwardDocument subAwardDocument) throws VersionException, WorkflowException {

        SubAward newVersion = getVersioningService().createNewVersion(subAwardDocument.getSubAward());
        incrementVersionNumberIfCanceledVersionsExist(newVersion);//Canceled versions retain their own version number.
        newVersion.getSubAwardAmountInfoList().clear();
        SubAwardDocument newSubAwardDocument = (SubAwardDocument) getDocumentService().getNewDocument(SubAwardDocument.class);
        newSubAwardDocument.getDocumentHeader().setDocumentDescription(subAwardDocument.getDocumentHeader().getDocumentDescription());
        newSubAwardDocument.setSubAward(newVersion);
        newVersion.setSubAwardDocument(newSubAwardDocument);
        return newSubAwardDocument;
    }

    protected void incrementVersionNumberIfCanceledVersionsExist(SubAward subAward) {
        int sequenceNumber = subAwardDao.getNextSequenceNumber(subAward.getSubAwardCode());
        subAward.setSequenceNumber(sequenceNumber);
    }

	@Override
    public void updateSubAwardSequenceStatus(SubAward subAward, VersionStatus status) {
        if (status.equals(VersionStatus.ACTIVE)) {
            archiveCurrentActiveSubAward(subAward.getSubAwardCode());
        }
        subAward.setSubAwardSequenceStatus(status.toString());
        getBusinessObjectService().save(subAward);
    }

    protected void archiveCurrentActiveSubAward(String subAwardCode) {
        Map<String, Object> values = new HashMap<>();
        values.put(SUB_AWARD_CODE, subAwardCode);
        values.put(SUB_AWARD_SEQUENCE_STATUS, VersionStatus.ACTIVE.name());
        Collection<SubAward> subAwards = getBusinessObjectService().
        findMatching(SubAward.class, values);
        for (SubAward subAward : subAwards) {
            subAward.setSubAwardSequenceStatus(VersionStatus.ARCHIVED.name());
            getBusinessObjectService().save(subAward);
        }
    }

	public BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

    @Override
    public String getNextSubAwardCode() {
        Long nextAwardNumber = sequenceAccessorService.
        getNextAvailableSequenceNumber(Constants.SUBAWARD_SEQUENCE_SUBAWARD_CODE, SubAward.class);

        return nextAwardNumber.toString();
    }

    public void setSequenceAccessorService(
    SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    protected boolean isCostSplitEnabled() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_SUBAWARD, Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, Constants.ENABLE_SUBAWARD_DC_IDC);
    }

    @Override
    public SubAward calculateAmountInfo(SubAward subAward) {

        List<SubAwardAmountInfo> subAwardAmountInfoList = subAward.getAllSubAwardAmountInfos();
        List<SubAwardAmountReleased> subAwardAmountReleasedList = subAward.getSubAwardAmountReleasedList();

        ScaleTwoDecimal totalAmountReleased = ScaleTwoDecimal.ZERO;
        initializeAmounts(subAward);

        if (subAwardAmountInfoList != null && subAwardAmountInfoList.size() > 0) {
            for (SubAwardAmountInfo subAwardAmountInfo: subAwardAmountInfoList) {
                if (!isCostSplitEnabled()) {
                    if (subAwardAmountInfo.getObligatedChange() != null) {
                        subAward.setTotalObligatedAmount(subAward.getTotalObligatedAmount().add(subAwardAmountInfo.getObligatedChange()));
                    }
                    if (subAwardAmountInfo.getAnticipatedChange() != null) {
                        subAward.setTotalAnticipatedAmount(subAward.getTotalAnticipatedAmount().add(subAwardAmountInfo.getAnticipatedChange()));
                    }
                } else {
                    if (subAwardAmountInfo.getObligatedChangeDirect() != null) {
                        subAward.setTotalObligatedAmount(subAward.getTotalObligatedAmount().add(subAwardAmountInfo.getObligatedChangeDirect()));
                        subAward.setTotalObligatedDirectAmount(subAward.getTotalObligatedDirectAmount().add(subAwardAmountInfo.getObligatedChangeDirect()));
                    }
                    if (subAwardAmountInfo.getObligatedChangeIndirect() != null) {
                        subAward.setTotalObligatedAmount(subAward.getTotalObligatedAmount().add(subAwardAmountInfo.getObligatedChangeIndirect()));
                        subAward.setTotalObligatedIndirectAmount(subAward.getTotalObligatedIndirectAmount().add(subAwardAmountInfo.getObligatedChangeIndirect()));

                    }
                    if (subAwardAmountInfo.getAnticipatedChangeDirect() != null) {
                        subAward.setTotalAnticipatedAmount(subAward.getTotalAnticipatedAmount().add(subAwardAmountInfo.getAnticipatedChangeDirect()));
                        subAward.setTotalAnticipatedDirectAmount(subAward.getTotalAnticipatedDirectAmount().add(subAwardAmountInfo.getAnticipatedChangeDirect()));

                    }
                    if (subAwardAmountInfo.getAnticipatedChangeIndirect() != null) {
                        subAward.setTotalAnticipatedAmount(subAward.getTotalAnticipatedAmount().add(subAwardAmountInfo.getAnticipatedChangeIndirect()));
                        subAward.setTotalAnticipatedIndirectAmount(subAward.getTotalAnticipatedIndirectAmount().add(subAwardAmountInfo.getAnticipatedChangeIndirect()));
                    }
                }

                if (subAwardAmountInfo.getModificationEffectiveDate() != null) {
                    subAward.setModificationEffectiveDate(subAwardAmountInfo.getModificationEffectiveDate());
                }
                if (subAwardAmountInfo.getModificationID() != null) {
                    subAward.setModificationId(subAwardAmountInfo.getModificationID());
                }
                if (subAwardAmountInfo.getPeriodofPerformanceStartDate() != null) {
                    subAward.setPerformanceStartDate(subAwardAmountInfo.getPeriodofPerformanceStartDate());
                }
                if (subAwardAmountInfo.getPeriodofPerformanceEndDate() != null) {
                    subAward.setPerformanceEnddate(subAwardAmountInfo.getPeriodofPerformanceEndDate());
                }
            }
            for (SubAwardAmountReleased subAwardAmountReleased: subAwardAmountReleasedList) {

                if (subAwardAmountReleased.getAmountReleased() != null
                        && !(StringUtils.equals(subAwardAmountReleased.getInvoiceStatus(), DocumentStatus.DISAPPROVED.getCode())
                        || StringUtils.equals(subAwardAmountReleased.getInvoiceStatus(), DocumentStatus.CANCELED.getCode())
                        || StringUtils.equals(subAwardAmountReleased.getInvoiceStatus(), DocumentStatus.RECALLED.getCode()))) {
                    subAward.setTotalAmountReleased(totalAmountReleased.add(subAwardAmountReleased.getAmountReleased()));
                    totalAmountReleased = subAward.getTotalAmountReleased();
                }
            }
            SubAwardAmountInfo amountInfo = subAward.getAllSubAwardAmountInfos().get(subAward.getAllSubAwardAmountInfos().size()-1);
            amountInfo.setAnticipatedAmount(subAward.getTotalAnticipatedAmount());
            amountInfo.setObligatedAmount(subAward.getTotalObligatedAmount());
        }

        subAward.setTotalAmountReleased(totalAmountReleased);
        subAward.setTotalAvailableAmount(subAward.getTotalObligatedAmount().subtract(totalAmountReleased));

        return subAward;
    }

    public void initializeAmounts(SubAward subAward) {
        subAward.setTotalObligatedAmount(ScaleTwoDecimal.ZERO);
        subAward.setTotalObligatedDirectAmount(ScaleTwoDecimal.ZERO);
        subAward.setTotalObligatedIndirectAmount(ScaleTwoDecimal.ZERO);
        subAward.setTotalAnticipatedAmount(ScaleTwoDecimal.ZERO);
        subAward.setTotalAnticipatedDirectAmount(ScaleTwoDecimal.ZERO);
        subAward.setTotalAnticipatedIndirectAmount(ScaleTwoDecimal.ZERO);
    }

    @Override
    public String getFollowupDateDefaultLength() {
        return getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                Constants.PARAMETER_COMPONENT_DOCUMENT, SUBAWARD_FOLLOW_UP);
    }

    @Override
    public Date getCalculatedFollowupDate(Date baseDate) {
        return new Date(DateUtils.addDays(baseDate, getFollowupDateDefaultLengthInDays()).getTime());
    }
    
    @Override
    public String getCalculatedFollowupDateForAjaxCall(String baseDate) {
        final String empty = "";
        String[] elements = baseDate.split("/");
        if (elements.length == 3) {
            try {
                int month = Integer.parseInt(elements[0]);
                int day = Integer.parseInt(elements[1]);
                int year = Integer.parseInt(elements[2]);
                if (year < 100) {
                    year = year + 2000;
                }
                Date requestedDate = new Date(year, month-1, day-1);
                Date followUpDate = getCalculatedFollowupDate(requestedDate);
                return (followUpDate.getMonth()+1) + "/" + (followUpDate.getDate()+1) + "/" + followUpDate.getYear();
                
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return empty;
    }

    @Override
    public int getFollowupDateDefaultLengthInDays() {
        String followupDateRange = getFollowupDateDefaultLength();
        String rangeUnit = followupDateRange.substring(
        followupDateRange.length() - 1, followupDateRange.length());
        int rangeAmount =
        Integer.parseInt(followupDateRange.substring(
        0, followupDateRange.length() - 1));
        final int returnAmount;
        if (StringUtils.equalsIgnoreCase(rangeUnit, "D")) {
            returnAmount = rangeAmount;
        } else if (StringUtils.equalsIgnoreCase(rangeUnit, "W")) {
            returnAmount = rangeAmount * 7;
        } else {
            throw new IllegalArgumentException(
            "An invalid range unit was set in the "
            + "'Subaward Follow Up' parameter: " + rangeUnit);
        }
        return returnAmount;
    }

    @Override
    public SubAward getActiveSubAward(Long subAwardId) {

        SubAward subAward = getBusinessObjectService().findBySinglePrimaryKey(SubAward.class, subAwardId);
        if (subAward != null) {
            calculateAmountInfo(subAward);
        }
        return subAward;
    }

    @Override
    public List<SubAward> getLinkedSubAwards(Award award) {
        Map<String, Object> values = new HashMap<>();
        values.put(AWARD_AWARD_NUMBER, award.getAwardNumber());
        Collection<SubAwardFundingSource> subAwardFundingSources = businessObjectService.findMatching(SubAwardFundingSource.class, values);
        Set<String> subAwardSet = new TreeSet<>();
        for(SubAwardFundingSource subAwardFundingSource : subAwardFundingSources){
            subAwardSet.add(subAwardFundingSource.getSubAward().getSubAwardCode());
        }
        List<SubAward> subAwards = new ArrayList<>();
        for (String subAwardCode : subAwardSet) {
            VersionHistory activeVersion = getVersionHistoryService().findActiveVersion(SubAward.class, subAwardCode);
            if (activeVersion == null) {
                VersionHistory pendingVersion = getVersionHistoryService().findPendingVersion(SubAward.class, subAwardCode);
                if (pendingVersion != null) {
                    subAwards.add((SubAward) pendingVersion.getSequenceOwner());
                }
            } else {
                subAwards.add((SubAward) activeVersion.getSequenceOwner());
            }
        }
        return subAwards;
    }

    public VersionHistoryService getVersionHistoryService() {
        return versionHistoryService;
    }

    public void setVersionHistoryService(VersionHistoryService versionHistoryService) {
        this.versionHistoryService = versionHistoryService;
    }

    protected VersioningService getVersioningService() {
        return versioningService;
    }

    public void setVersioningService(VersioningService versioningService) {
        this.versioningService = versioningService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public SubAwardDao getSubAwardDao() {
        return this.subAwardDao;
    }

    public void setSubAwardDao(SubAwardDao subAwardDao) {
        this.subAwardDao = subAwardDao;
    }
}
