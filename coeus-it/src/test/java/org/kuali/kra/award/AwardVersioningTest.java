/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.framework.attachment.AttachmentDocumentStatus;
import org.kuali.coeus.common.framework.attachment.AttachmentFile;
import org.kuali.coeus.common.framework.version.sequence.associate.SequenceAssociate;
import org.kuali.coeus.common.framework.version.VersionException;
import org.kuali.coeus.common.framework.version.VersioningService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.commitments.AwardCostShare;
import org.kuali.kra.award.commitments.AwardFandaRate;
import org.kuali.kra.award.contacts.AwardPerson;
import org.kuali.kra.award.contacts.AwardPersonUnit;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.*;
import org.kuali.kra.award.home.approvedsubawards.AwardApprovedSubaward;
import org.kuali.kra.award.notesandattachments.attachments.AwardAttachment;
import org.kuali.kra.award.notesandattachments.attachments.AwardAttachmentType;
import org.kuali.kra.award.paymentreports.awardreports.AwardReportTerm;
import org.kuali.kra.award.paymentreports.specialapproval.approvedequipment.AwardApprovedEquipment;
import org.kuali.kra.award.specialreview.AwardSpecialReview;
import org.kuali.kra.award.version.service.AwardVersionService;
import org.kuali.kra.bo.CommentType;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.test.infrastructure.KcIntegrationTestBase;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import java.sql.Date;
import java.util.*;

import static org.junit.Assert.*;

public class AwardVersioningTest extends KcIntegrationTestBase {
    private static final Log LOG = LogFactory.getLog(AwardVersioningTest.class); 
    private static final double COST_SHARE_COMMIT_AMT = 1000.00;
    private static final String COST_SHARE_DEST1 = "576434";
    private static final String COST_SHARE_DEST2 = "777777";
    private static final String COST_SHARE_SOURCE = "7568657";
    private static final String FISCAL_YEAR = "2009";
    private static final String VENDOR_A = "VendorA";
    private static final String MODEL_A = "ModelA";
    private static final String ITEM_A = "ItemA";
    private static final String ITEM_B = "ItemB";
    private static final double AMOUNT = 1000.00;
    private static final String AWARD_TITLE = "Award Title";
    private static final String GOOGLE_SPONSOR_CODE = "005979";
    private static final String SPONSOR_AWARD_NUMBER = "1R01CA123456";
    private static final String DOCUMENT_DESCRIPTION = "Award Versioning Test Document";    
    
    private BusinessObjectService bos;
    private DocumentService documentService;
    private VersioningService versioningService;
    private AwardVersionService awardVersionService;
    private List<AwardDocument> savedDocuments;
    private List<Award> awards;

    private Integer awardAttachmentDocumentId = 0;

    @Before
    public void setUp() throws Exception {
       GlobalVariables.setUserSession(new UserSession("quickstart"));
       savedDocuments = new ArrayList<>();
       awards = new ArrayList<>();
       locateServices();
       initializeAward();
       AwardDocument originalDocument = initializeNewDocument(awards.get(0));       
       saveDocument(originalDocument);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testVersioningAward_Level1() throws VersionException, WorkflowException {
        Award awardVersion2 = versionAward(savedDocuments.get(0));

        addSomeAwardAssociates(awardVersion2);
        
        Award awardVersion3 = versioningService.createNewVersion(awardVersion2);
        assertEquals(2, awardVersion2.getApprovedEquipmentItemCount());
        assertEquals(2, awardVersion3.getApprovedEquipmentItemCount());
        
        AwardDocument document = getAwardDocumentFromNewAwardVersion(awardVersion3);
        awardVersion3 = document.getAward();
        
        verifySequenceAssociatesAfterVersioning(awardVersion2.getApprovedEquipmentItems(), awardVersion3.getApprovedEquipmentItems());
        verifySequenceAssociatesAfterVersioning(awardVersion2.getAwardComments(), awardVersion3.getAwardComments());
        verifySequenceAssociatesAfterVersioning(awardVersion2.getAwardCostShares(), awardVersion3.getAwardCostShares());
        verifySequenceAssociatesAfterVersioning(awardVersion2.getAwardFandaRate(), awardVersion3.getAwardFandaRate());
        verifySequenceAssociatesAfterVersioning(awardVersion2.getAwardReportTermItems(), awardVersion3.getAwardReportTermItems());
        verifySequenceAssociatesAfterVersioning(awardVersion2.getAwardSponsorTerms(), awardVersion3.getAwardSponsorTerms());
        verifySequenceAssociatesAfterVersioning(awardVersion2.getAwardApprovedSubawards(), awardVersion3.getAwardApprovedSubawards());
        verifySequenceAssociatesAfterVersioning(awardVersion2.getSpecialReviews(), awardVersion3.getSpecialReviews());
        verifySequenceAssociatesAfterVersioning(awardVersion2.getProjectPersons(), awardVersion3.getProjectPersons());
    }

    private void addSomeAwardAssociates(Award awardVersion) {
        addSomeApprovedEquipmentAndVerifyBaseline(awardVersion);
        addSomeAwardCommentsAndVerifyBaseline(awardVersion);
        addSomeAwardCostSharesAndVerifyBaseline(awardVersion);
        addSomeAwardFandaRatesAndVerifyBaseline(awardVersion);
        addSomeAwardReportTermsAndVerifyBaseline(awardVersion);
        addSomeAwardSponsorTermsAndVerifyBaseline(awardVersion);
        addSomeAwardSubawardsAndVerifyBaseline(awardVersion);
        addSomeAwardSpecialReviewsAndVerifyBaseline(awardVersion);
        addSomeAwardPersonsAndVerifyBaseLine(awardVersion);
        addSomeAwardAttachmentsAndVerifyBaseLine(awardVersion);
    }

    @Test
    public void testVersioningAwardAfterCancellingIt() throws Exception {
        Award awardVersion2 = getAwardVersionService().createAndSaveNewAwardVersion(savedDocuments.get(0)).getAward();
        addSomeAwardAssociates(awardVersion2);
        AwardDocument document2 = (AwardDocument)documentService.getByDocumentHeaderId(awardVersion2.getAwardDocument().getDocumentNumber());

        AwardDocument document3 = getAwardVersionService().createAndSaveNewAwardVersion(document2);
        document3 = (AwardDocument)documentService.saveDocument(documentService.cancelDocument(document3,""));
        Award awardVersion3 = document3.getAward();

        Award awardVersion4 = getAwardVersionService().createAndSaveNewAwardVersion(document3).getAward();

        verifySequenceAssociatesAfterVersioning(awardVersion3.getApprovedEquipmentItems(), awardVersion4.getApprovedEquipmentItems());
        verifySequenceAssociatesAfterVersioning(awardVersion3.getAwardComments(), awardVersion4.getAwardComments());
        verifySequenceAssociatesAfterVersioning(awardVersion3.getAwardCostShares(), awardVersion4.getAwardCostShares());
        verifySequenceAssociatesAfterVersioning(awardVersion3.getAwardFandaRate(), awardVersion4.getAwardFandaRate());
        verifySequenceAssociatesAfterVersioning(awardVersion3.getAwardReportTermItems(), awardVersion4.getAwardReportTermItems());
        verifySequenceAssociatesAfterVersioning(awardVersion3.getAwardSponsorTerms(), awardVersion4.getAwardSponsorTerms());
        verifySequenceAssociatesAfterVersioning(awardVersion3.getAwardApprovedSubawards(), awardVersion4.getAwardApprovedSubawards());
        verifySequenceAssociatesAfterVersioning(awardVersion3.getSpecialReviews(), awardVersion4.getSpecialReviews());
        verifySequenceAssociatesAfterVersioning(awardVersion3.getProjectPersons(), awardVersion4.getProjectPersons());
    }

    private AwardDocument getAwardDocumentFromNewAwardVersion(Award awardVersion) throws WorkflowException {
        return (AwardDocument)documentService.saveDocument(initializeNewDocument(awardVersion));
    }

    private void addSomeApprovedEquipmentAndVerifyBaseline(Award awardVersion) {
        awardVersion.add(new AwardApprovedEquipment(VENDOR_A, MODEL_A, ITEM_A, AMOUNT));
        awardVersion.add(new AwardApprovedEquipment(VENDOR_A, MODEL_A, ITEM_B, AMOUNT));
        saveAndVerifySequenceAssociateValues(awardVersion, awardVersion.getApprovedEquipmentItems());
    }

    private void addSomeAwardCommentsAndVerifyBaseline(Award awardVersion) {
        awardVersion.add(createComment("Comment 1"));
        awardVersion.add(createComment("Comment 2"));
        awardVersion.add(createComment("Comment 3"));
        saveAndVerifySequenceAssociateValues(awardVersion, awardVersion.getAwardComments());
    }
    
    private void addSomeAwardCostSharesAndVerifyBaseline(Award awardVersion) {
        awardVersion.add(createCostShare(0.75, COST_SHARE_DEST1));
        awardVersion.add(createCostShare(0.25, COST_SHARE_DEST2));
        saveAndVerifySequenceAssociateValues(awardVersion, awardVersion.getAwardCostShares());
    }
    
    private void addSomeAwardFandaRatesAndVerifyBaseline(Award awardVersion) {
        awardVersion.add(createAwardFandaRate("N"));
        awardVersion.add(createAwardFandaRate("F"));
        saveAndVerifySequenceAssociateValues(awardVersion, awardVersion.getAwardFandaRate());
    } 

    private void addSomeAwardReportTermsAndVerifyBaseline(Award awardVersion) {
        awardVersion.add(createAwardReportTerm("2", "39", "14", "2"));
        awardVersion.add(createAwardReportTerm("1", "5", "14", "2"));
    }
    
    private void addSomeAwardSponsorTermsAndVerifyBaseline(Award awardVersion) {
        awardVersion.add(createAwardSponsorTerm());
        saveAndVerifySequenceAssociateValues(awardVersion, awardVersion.getAwardSponsorTerms());
    }
    
    private void addSomeAwardSubawardsAndVerifyBaseline(Award awardVersion) {
        awardVersion.add(createApprovedSubaward("Org A"));
        awardVersion.add(createApprovedSubaward("Org B"));
        saveAndVerifySequenceAssociateValues(awardVersion, awardVersion.getAwardApprovedSubawards());
    }
    
    private void addSomeAwardSpecialReviewsAndVerifyBaseline(Award awardVersion) {
        awardVersion.add(createSpecialReview(1, "2", "1"));
        awardVersion.add(createSpecialReview(2, "2", "2"));
        saveAndVerifySequenceAssociateValues(awardVersion, awardVersion.getSpecialReviews());
    }

    private void addSomeAwardPersonsAndVerifyBaseLine(Award awardVersion) {
        awardVersion.add(createAwardPerson(awardVersion, ContactRole.PI_CODE, "10000000001", "IN-CARD"));
        awardVersion.add(createAwardPerson(awardVersion, ContactRole.COI_CODE, "10000000006", "IN-CARD"));
        saveAndVerifySequenceAssociateValues(awardVersion, awardVersion.getProjectPersons());
    }

    private void addSomeAwardAttachmentsAndVerifyBaseLine(Award awardVersion) {
        awardVersion.addAttachment(createAwardAttachment(awardVersion));
        saveAndVerifySequenceAssociateValues(awardVersion, awardVersion.getAwardAttachments());
    }
    
    private AwardApprovedSubaward createApprovedSubaward(String organizationName) {
        AwardApprovedSubaward subaward = new AwardApprovedSubaward();
        subaward.setAmount(new ScaleTwoDecimal(100.00));
        subaward.setOrganizationName(organizationName);
        return subaward;
    }
    
    private AwardReportTerm createAwardReportTerm(String reportClassCode, String reportCode, String frequencyCode, String frequencyBaseCode) {
        AwardReportTerm term = new AwardReportTerm();
        term.setReportClassCode(reportClassCode);
        term.setReportCode(reportCode);
        term.setFrequencyBaseCode(frequencyBaseCode);
        term.setFrequencyCode(frequencyCode);
        term.setDueDate(new Date(10000332));
        term.setOspDistributionCode("1");
        
        return term;
    }
    
    private AwardSponsorTerm createAwardSponsorTerm() {
        AwardSponsorTerm term = new AwardSponsorTerm();
        term.setSponsorTermId(null);
        return term;
    }
    

    private AwardComment createComment(String commentText) {
        CommentType commentType = new CommentType();
        AwardComment comment = new AwardComment();
        commentType.setCommentTypeCode(Constants.GENERAL_COMMENT_TYPE_CODE);
        comment.setComments(commentText);
        comment.setChecklistPrintFlag(Boolean.TRUE);
        comment.setCommentTypeCode(commentType.getCommentTypeCode());
        comment.setCommentType(commentType);
        return comment;
    }
    
    private AwardCostShare createCostShare(double costSharePct, String destination) {
        AwardCostShare costShare = new AwardCostShare();
        costShare.setCostSharePercentage(new ScaleTwoDecimal(costSharePct));
        costShare.setCostShareMet(costShare.getCostSharePercentage());
        costShare.setCostShareTypeCode(1);
        costShare.setProjectPeriod(FISCAL_YEAR);
        costShare.setSource(COST_SHARE_SOURCE);
        costShare.setDestination(destination);
        costShare.setCommitmentAmount(new ScaleTwoDecimal(COST_SHARE_COMMIT_AMT));
        costShare.setUnitNumber("BL-BL");
        return costShare;
    }

    private AwardFandaRate createAwardFandaRate(String onOffCampusFlag) {
        AwardFandaRate rate = new AwardFandaRate();
        rate.setApplicableFandaRate(new ScaleTwoDecimal(5.0));
        rate.setFandaRateTypeCode("1");
        rate.setFiscalYear("2008");
        rate.setStartDate(new Date(new GregorianCalendar(2007, Calendar.JULY, 1).getTimeInMillis()));
        rate.setStartDate(new Date(new GregorianCalendar(2008, Calendar.JUNE, 30).getTimeInMillis()));
        rate.setOnCampusFlag(onOffCampusFlag);
        return rate;
    }
    
    private AwardSpecialReview createSpecialReview(Integer specialReviewNo, String specialReviewCode, String approvalTypeCode) {
        AwardSpecialReview review = new AwardSpecialReview();
        review.setSpecialReviewNumber(specialReviewNo);
        review.setSpecialReviewTypeCode(specialReviewCode);
        review.setApprovalTypeCode(approvalTypeCode);
        return review;
    }

    private AwardPerson createAwardPerson(Award award, String contactRole, String personId, String unitNumber) {
        AwardPerson person = new AwardPerson();
        person.setAward(award);
        person.setAcademicYearEffort(new ScaleTwoDecimal(100.00));
        person.setCalendarYearEffort(new ScaleTwoDecimal(100.00));
        person.setRoleCode(contactRole);
        person.setFaculty(false);
        person.setPersonId(personId);

        AwardPersonUnit unit = new AwardPersonUnit();
        unit.setAwardPerson(person);
        unit.setLeadUnit(true);
        unit.setUnitNumber(unitNumber);
        person.add(unit);
        unit.setAwardPerson(person);

        return person;
    }

    private AwardAttachment createAwardAttachment(Award award) {
        AwardAttachment attachment = new AwardAttachment(award);
        AwardAttachmentType attachmentType = new AwardAttachmentType("7", "Other Document");
        AttachmentFile attachmentFile = new AttachmentFile("Test File", attachmentType.getTypeCode(), "test contents".getBytes());

        attachment.setDocumentId(++awardAttachmentDocumentId);
        attachment.setAwardId(award.getAwardId());
        attachment.setDocumentStatusCode(AttachmentDocumentStatus.ACTIVE.getCode());
        attachment.setFile(attachmentFile);
        attachment.setType(attachmentType);
        attachment.setTypeCode(attachmentType.getTypeCode());

        return attachment;
    }
    
    private void initializeAward() {
        Award awardVersion1 = new Award();
        awardVersion1.setAwardTypeCode(1);
        awardVersion1.setTitle(AWARD_TITLE);
        awardVersion1.setActivityTypeCode("1");
        awardVersion1.setAwardTransactionTypeCode(1);
        awardVersion1.setUnitNumber("IN-CARD");
        awardVersion1.setPrimeSponsorCode(GOOGLE_SPONSOR_CODE);
        awardVersion1.setSponsorCode(GOOGLE_SPONSOR_CODE);
        awardVersion1.setStatusCode(1);
        awardVersion1.setModificationNumber("1");
        awardVersion1.setSponsorAwardNumber(SPONSOR_AWARD_NUMBER);
           
        Date date = new Date(System.currentTimeMillis());
           
        awardVersion1.setAwardEffectiveDate(date);
        awardVersion1.setAwardExecutionDate(date);
        awardVersion1.setBeginDate(date);
        awardVersion1.setProjectEndDate(date);
        awards.add(awardVersion1);
    }

    private AwardDocument initializeNewDocument(Award award) throws WorkflowException {
        AwardDocument document = (AwardDocument) documentService.getNewDocument(AwardDocument.class);
        document.getDocumentHeader().setDocumentDescription(DOCUMENT_DESCRIPTION);
        document.setAward(award);
        return document;
    }

    private void locateServices() {
        documentService = KcServiceLocator.getService(DocumentService.class);
        bos = KcServiceLocator.getService(BusinessObjectService.class);
        versioningService = KcServiceLocator.getService(VersioningService.class);
    }

    private AwardVersionService getAwardVersionService() {
        if (awardVersionService == null) {
            awardVersionService = KcServiceLocator.getService(AwardVersionService.class);;
        }
        return awardVersionService;
    }

    private void saveAndVerifySequenceAssociateValues(Award award, List<? extends SequenceAssociate> items) {
        bos.save(award);
        Map<String,Object> keys = new HashMap<>();
        keys.put("awardId", award.getAwardId());
        award = bos.findByPrimaryKey(Award.class, keys);
        for(SequenceAssociate sequenceAssociate: items) {
            assertEquals(award.getSequenceNumber(), sequenceAssociate.getSequenceNumber());
            assertEquals(award.getAwardId(), ((Award) sequenceAssociate.getSequenceOwner()).getAwardId());
        }
    }

    private AwardDocument saveDocument(AwardDocument document) throws WorkflowException {
        try {
            document = (AwardDocument) documentService.saveDocument(document);
            savedDocuments.add(document);
            return document;
        } catch(WorkflowException e) {
            MessageMap errorMap = GlobalVariables.getMessageMap();
            if(errorMap.getErrorCount() > 0) {
                   for(String errorProperty : errorMap.getPropertiesWithErrors()) {
                       LOG.error("-------\nProperty in error " + errorProperty);
                       for(Object error: errorMap.getErrorMessagesForProperty(errorProperty)) {
                           LOG.error(error);
                       }
                   }    
            }
            throw e;
       }
    }
    
    /**
     * This method compares the sequence numbers of SequenceAssociates in one Award version with those in the subsequent Award version
     */
    private void verifySequenceAssociatesAfterVersioning(List<? extends SequenceAssociate> sequenceAssociatesBeforeVersioning, List<? extends SequenceAssociate> sequenceAssociatesAfterVersioning) {
        assertEquals(sequenceAssociatesBeforeVersioning.size(), sequenceAssociatesAfterVersioning.size());
        for(int index = 0; index < sequenceAssociatesBeforeVersioning.size(); index++) {
            SequenceAssociate associateBeforeVersioning = sequenceAssociatesBeforeVersioning.get(index);
            SequenceAssociate associateAfterVersioning = sequenceAssociatesAfterVersioning.get(index);
            assertEquals(associateBeforeVersioning.getSequenceNumber() + 1, associateAfterVersioning.getSequenceNumber().intValue());
        }
    }

    private Award versionAward(AwardDocument oldVersionDocument) throws VersionException, WorkflowException {
        Award oldVersion = oldVersionDocument.getAward();
        Award newVersion = versioningService.createNewVersion(oldVersion);
        assertEquals(oldVersion.getSequenceNumber() + 1, newVersion.getSequenceNumber().intValue());
        assertNull(newVersion.getAwardId());
        AwardDocument newDocument = initializeNewDocument(newVersion);
        newDocument = saveDocument(newDocument);
        
        AwardDocument fetchedOldVersionDocument = (AwardDocument) documentService.getByDocumentHeaderId(oldVersionDocument.getDocumentNumber());
        Award fetchedOldVersionAward = fetchedOldVersionDocument.getAward();
        assertEquals(oldVersion.getAwardId(), fetchedOldVersionAward.getAwardId());
        assertEquals(oldVersion.getSequenceNumber(), fetchedOldVersionAward.getSequenceNumber());
        
        AwardDocument fetchedNewVersionDocument = (AwardDocument) documentService.getByDocumentHeaderId(newDocument.getDocumentNumber());
        Award fetchedNewVersion = fetchedNewVersionDocument.getAward();
        assertNotSame(oldVersion.getAwardId(), fetchedNewVersion.getAwardId());
        assertEquals(oldVersion.getSequenceNumber() + 1, fetchedNewVersion.getSequenceNumber().intValue());
        return fetchedNewVersion;
    }    
}
