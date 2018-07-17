/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.budget.subaward;


import com.lowagie.text.pdf.*;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.propdev.impl.budget.ProposalDevelopmentBudgetExt;
import org.kuali.coeus.propdev.impl.s2s.FormUtilityService;
import org.kuali.coeus.s2sgen.api.core.InfastructureConstants;
import org.kuali.coeus.s2sgen.api.hash.GrantApplicationHashService;
import org.kuali.coeus.sys.api.model.KcFile;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.common.api.sponsor.hierarchy.SponsorHierarchyService;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.core.BudgetService;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItem;
import org.kuali.coeus.common.budget.framework.period.BudgetPeriod;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.util.CollectionUtils;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.coeus.s2sgen.api.generate.FormMappingInfo;
import org.kuali.coeus.s2sgen.api.generate.FormMappingService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("propDevBudgetSubAwardService")
public class PropDevPropDevBudgetSubAwardServiceImpl implements PropDevBudgetSubAwardService {
    private static final String XFA_NS = "http://www.xfa.org/schema/xfa-data/1.0/";
    private static final Logger LOG = LogManager.getLogger(PropDevPropDevBudgetSubAwardServiceImpl.class);
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String BUDGET_YEAR_XPATH = "//*[local-name(.) = 'BudgetYear']";
    private static final String RR_FED_NON_FED_BUDGET = "RR_FedNonFedBudget";
    private static final String BUDGET_PERIOD = "BudgetPeriod";
    private static final List<String> FED_NON_FED_FORMS = Stream.of("http://apply.grants.gov/forms/RR_FedNonFedBudget10-V1.1",
                                                                            "http://apply.grants.gov/forms/RR_FedNonFedBudget_1_2-V1.2").collect(Collectors.toList());

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    @Autowired
    @Qualifier("proposalBudgetService")
    private BudgetService budgetService;

    @Autowired
    @Qualifier("dateTimeService")
    private DateTimeService dateTimeService;

    @Autowired
    @Qualifier("formMappingService")
    private FormMappingService formMappingService;

    @Autowired
    @Qualifier("grantApplicationHashService")
    private GrantApplicationHashService grantApplicationHashService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;
    
    @Autowired
    @Qualifier("sponsorHierarchyService")
    private SponsorHierarchyService sponsorHierarchyService;

    @Autowired
    @Qualifier("formUtilityService")
    private FormUtilityService formUtilityService;

    @Override
    public void populateBudgetSubAwardFiles(Budget budget, BudgetSubAwards subAward, String newFileName, byte[] newFileData) {
        subAward.setSubAwardStatusCode(1);
        BudgetSubAwardFiles newSubAwardFile = new BudgetSubAwardFiles();
        newSubAwardFile.setSubAwardXfdFileData(newFileData);
        subAward.getBudgetSubAwardAttachments().clear();
        subAward.getBudgetSubAwardFiles().clear();
        subAward.getBudgetSubAwardFiles().add(newSubAwardFile);
        
        boolean subawardBudgetExtracted;
        
        try {
            byte[] pdfFileContents = newSubAwardFile.getSubAwardXfdFileData();
            subAward.setSubAwardXfdFileData(pdfFileContents);
            PdfReader  reader = new PdfReader(pdfFileContents);
            byte[] xmlContents=getXMLFromPDF(reader);
            subawardBudgetExtracted = (xmlContents!=null && xmlContents.length>0);
            if(subawardBudgetExtracted){
                final List<KcFile> attachments = getFormUtilityService().extractAttachments(reader);
                final Collection<String> duplicates = CollectionUtils.findDuplicates(attachments, KcFile::getName);
                if (!duplicates.isEmpty()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Duplicate attachments detected " + duplicates);
                    }
                    subawardBudgetExtracted = false;
                } else {
                    updateXML(xmlContents, attachments, subAward);
                }
            }
        } catch (RuntimeException|IOException|XPathExpressionException|ParserConfigurationException|TransformerException|SAXException e) {
            LOG.error("Not able to extract xml from pdf",e);
            subawardBudgetExtracted = false;
        }
        
        newSubAwardFile.setSubAwardXfdFileData(subAward.getSubAwardXfdFileData());
        if (subawardBudgetExtracted) {
            newSubAwardFile.setSubAwardXmlFileData(subAward.getSubAwardXmlFileData());
        }
        newSubAwardFile.setSubAwardXfdFileName(newFileName);
        newSubAwardFile.setBudgetSubAward(subAward);
        subAward.setSubAwardXfdFileName(newFileName);
        subAward.setXfdUpdateUser(getLoggedInUserNetworkId());
        subAward.setXfdUpdateTimestamp(dateTimeService.getCurrentTimestamp());
        subAward.setXmlUpdateUser(getLoggedInUserNetworkId());
        subAward.setXmlUpdateTimestamp(dateTimeService.getCurrentTimestamp());
    }

    @Override
    public void removeSubAwardAttachment(BudgetSubAwards subAward) {
        subAward.setFormName(null);
        subAward.setNamespace(null);
        subAward.setSubAwardXfdFileData(null);
        subAward.setSubAwardXfdFileName(null);
        subAward.setSubAwardXmlFileData(null);
        subAward.setXfdUpdateUser(null);
        subAward.getBudgetSubAwardAttachments().clear();
        subAward.getBudgetSubAwardFiles().clear();
        subAward.setXfdUpdateUser(getLoggedInUserNetworkId());
        subAward.setXfdUpdateTimestamp(dateTimeService.getCurrentTimestamp());
        subAward.setXmlUpdateUser(getLoggedInUserNetworkId());
        subAward.setXmlUpdateTimestamp(dateTimeService.getCurrentTimestamp());
        resetSubAwardPeriodDetails(subAward);
    }
    
    protected void resetSubAwardPeriodDetails(BudgetSubAwards subAward) {
        for (BudgetSubAwardPeriodDetail budgetSubAwardPeriodDetail : subAward.getBudgetSubAwardPeriodDetails()) {
        	budgetSubAwardPeriodDetail.setDirectCost(ScaleTwoDecimal.ZERO);
        	budgetSubAwardPeriodDetail.setCostShare(ScaleTwoDecimal.ZERO);
        	budgetSubAwardPeriodDetail.setIndirectCost(ScaleTwoDecimal.ZERO);
        	budgetSubAwardPeriodDetail.setTotalCost(ScaleTwoDecimal.ZERO);
        }
    }

    @Override
    public void prepareBudgetSubAwards(Budget budget) {
        populateBudgetSubAwardAttachments(budget);
        for (BudgetSubAwards subAward : budget.getBudgetSubAwards()) {
            for (BudgetPeriod period : budget.getBudgetPeriods()) {
                BudgetSubAwardPeriodDetail detail = null;
                for (BudgetSubAwardPeriodDetail curDetail : subAward.getBudgetSubAwardPeriodDetails()) {
                    if (Objects.equals(curDetail.getBudgetPeriod(), period.getBudgetPeriod())) {
                        detail = curDetail;
                        break;
                    }
                }
                if (detail == null) {
                    subAward.getBudgetSubAwardPeriodDetails().add(new BudgetSubAwardPeriodDetail(subAward, period));
                }
            }
        }
    }

    @Override
    public void generateSubAwardLineItems(BudgetSubAwards subAward, ProposalDevelopmentBudgetExt budget) {
        ScaleTwoDecimal amountChargeFA = new ScaleTwoDecimal(25000);
        boolean isNihProposal = getSponsorHierarchyService().isSponsorNihMultiplePi(budget.getDevelopmentProposal().getSponsorCode());
        String directLtCostElement = getParameterService().getParameterValueAsString(Budget.class, Constants.SUBCONTRACTOR_DIRECT_LT_25K_PARAM);
        String directGtCostElement = getParameterService().getParameterValueAsString(Budget.class, Constants.SUBCONTRACTOR_DIRECT_GT_25K_PARAM);
        String inDirectLtCostElement = getParameterService().getParameterValueAsString(Budget.class, Constants.SUBCONTRACTOR_F_AND_A_LT_25K_PARAM);
        String inDirectGtCostElement = getParameterService().getParameterValueAsString(Budget.class, Constants.SUBCONTRACTOR_F_AND_A_GT_25K_PARAM);        	
        for (BudgetSubAwardPeriodDetail detail : subAward.getBudgetSubAwardPeriodDetails()) {
            BudgetPeriod budgetPeriod = findBudgetPeriod(detail, budget);

            List<BudgetLineItem> currentSubawardLineItems = zeroOutSubAwardLineItems(subAward, budgetPeriod);
            ScaleTwoDecimal directCost = ScaleTwoDecimal.returnZeroIfNull(detail.getDirectCost());
            //we only create separate line items for indirect if the proposal is nih
            if (!isNihProposal) {
            	directCost = directCost.add(ScaleTwoDecimal.returnZeroIfNull(detail.getIndirectCost()));
            }
            if (directCost.isNonZero()) {
                ScaleTwoDecimal ltValue = lesserValue(directCost, amountChargeFA);
                ScaleTwoDecimal gtValue = directCost.subtract(ltValue);
                if (ltValue.isNonZero()) {
                    BudgetLineItem lt = findOrCreateLineItem(currentSubawardLineItems, detail, budgetPeriod, directLtCostElement, subAward.getOrganizationName());
                    lt.setLineItemCost(ltValue);
                }
                if (gtValue.isNonZero()) {
                    BudgetLineItem gt = findOrCreateLineItem(currentSubawardLineItems, detail, budgetPeriod, directGtCostElement, subAward.getOrganizationName());
                    gt.setLineItemCost(gtValue);
                }
                amountChargeFA = amountChargeFA.subtract(ltValue);
            }

            if (ScaleTwoDecimal.returnZeroIfNull(detail.getIndirectCost()).isNonZero() && isNihProposal) {
                ScaleTwoDecimal ltValue = lesserValue(detail.getIndirectCost(), amountChargeFA);
                ScaleTwoDecimal gtValue = detail.getIndirectCost().subtract(ltValue);
                if (ltValue.isNonZero()) {
                    BudgetLineItem lt = findOrCreateLineItem(currentSubawardLineItems, detail, budgetPeriod, inDirectLtCostElement, subAward.getOrganizationName());
                    lt.setLineItemCost(ltValue);
                }
                if (gtValue.isNonZero()) {
                    BudgetLineItem gt = findOrCreateLineItem(currentSubawardLineItems, detail, budgetPeriod, inDirectGtCostElement, subAward.getOrganizationName());
                    gt.setLineItemCost(gtValue);
                }
                amountChargeFA = amountChargeFA.subtract(ltValue);
            }

            if (ScaleTwoDecimal.returnZeroIfNull(detail.getCostShare()).isNonZero()) {
                final String orgName = subAward.getOrganizationName() + " CostShare Amount(";
                String description = orgName + detail.getCostShare() + ")";
                BudgetLineItem costShareLineItem = findOrCreateCostshareLineItem(currentSubawardLineItems, detail, budgetPeriod, directGtCostElement, description, orgName);
                costShareLineItem.setLineItemCost(ScaleTwoDecimal.ZERO);
                costShareLineItem.setCostSharingAmount(detail.getCostShare());
                currentSubawardLineItems.add(costShareLineItem);
            }

            currentSubawardLineItems.sort(Comparator.comparing(BudgetLineItem::getLineItemNumber));

            addSubawardLineItemsToBudgetPeriod(budgetPeriod, currentSubawardLineItems);

        }
    }

    protected List<BudgetLineItem> zeroOutSubAwardLineItems(BudgetSubAwards subAward, BudgetPeriod budgetPeriod) {
        List<BudgetLineItem> currentSubawardLineItems = findSubAwardLineItems(budgetPeriod, subAward.getSubAwardNumber());
        for (BudgetLineItem item : currentSubawardLineItems) {
            item.setLineItemCost(ScaleTwoDecimal.ZERO);
            item.setDirectCost(ScaleTwoDecimal.ZERO);
            item.setCostSharingAmount(ScaleTwoDecimal.ZERO);
            item.setBudgetSubAward(subAward);
            item.setLineItemDescription(subAward.getOrganizationName());
        }
        return currentSubawardLineItems;
    }

    protected void addSubawardLineItemsToBudgetPeriod(BudgetPeriod budgetPeriod, List<BudgetLineItem> currentSubawardLineItems) {
        Iterator<BudgetLineItem> iter = currentSubawardLineItems.iterator();
        while (iter.hasNext()) {
            BudgetLineItem lineItem = iter.next();
            if (ScaleTwoDecimal.returnZeroIfNull(lineItem.getLineItemCost()).isZero() &&
                    ScaleTwoDecimal.returnZeroIfNull(lineItem.getCostSharingAmount()).isZero()) {
                budgetPeriod.getBudgetLineItems().remove(lineItem);
                iter.remove();
            } else {
                if (!budgetPeriod.getBudgetLineItems().contains(lineItem)) {
                    budgetPeriod.getBudgetLineItems().add(lineItem);
                }
            }
        }
    }

    protected BudgetPeriod findBudgetPeriod(BudgetSubAwardPeriodDetail detail, Budget budget) {
        for (BudgetPeriod period : budget.getBudgetPeriods()) {
            if (Objects.equals(detail.getBudgetPeriod(), period.getBudgetPeriod())) {
                return period;
            }
        }
        return null;
    }
    
    protected ScaleTwoDecimal lesserValue(ScaleTwoDecimal num1, ScaleTwoDecimal num2) {
        if (num1.isLessThan(num2)) {
            return num1;
        } else {
            return num2;
        }
    }
    
    protected BudgetLineItem findOrCreateLineItem(List<BudgetLineItem> lineItems, BudgetSubAwardPeriodDetail subAwardDetail,
                                                  BudgetPeriod budgetPeriod, String costElement, String description) {
        for (BudgetLineItem curLineItem : lineItems) {
            if (StringUtils.equals(curLineItem.getCostElement(), costElement)) {
                return curLineItem;
            }
        }
        return createLineItem(lineItems, subAwardDetail, budgetPeriod, costElement, description);


    }

    protected BudgetLineItem findOrCreateCostshareLineItem(List<BudgetLineItem> lineItems, BudgetSubAwardPeriodDetail subAwardDetail,
                                                  BudgetPeriod budgetPeriod, String costElement, String lineItemDescription, String orgName) {
        for (BudgetLineItem curLineItem : lineItems) {
            if (StringUtils.startsWith(curLineItem.getLineItemDescription(), orgName)) {
                return curLineItem;
            }
        }
        return createLineItem(lineItems, subAwardDetail, budgetPeriod, costElement, lineItemDescription);
    }

    private BudgetLineItem createLineItem(List<BudgetLineItem> lineItems, BudgetSubAwardPeriodDetail subAwardDetail, BudgetPeriod budgetPeriod, String costElement, String description) {
        BudgetLineItem newLineItem = new BudgetLineItem();
        newLineItem.setCostElement(costElement);
        newLineItem.setBudgetSubAward(subAwardDetail.getBudgetSubAward());
        newLineItem.setLineItemDescription(description);
        getBudgetService().populateNewBudgetLineItem(newLineItem, budgetPeriod);
        lineItems.add(newLineItem);
        return newLineItem;
    }

    protected List<BudgetLineItem> findSubAwardLineItems(BudgetPeriod budgetPeriod, Integer subAwardNumber) {
        List<BudgetLineItem> lineItems = new ArrayList<>();
        if (budgetPeriod.getBudgetLineItems() != null) {
            lineItems.addAll(budgetPeriod.getBudgetLineItems().stream()
                    .filter(item -> item.getBudgetSubAward() != null && Objects.equals(item.getBudgetSubAward().getSubAwardNumber(), subAwardNumber))
                    .collect(Collectors.toList()));
        }
        return lineItems;
    }
    
    /**
     * This method return loggedin user id
     */
    protected String getLoggedInUserNetworkId() {
        return globalVariableService.getUserSession().getPrincipalName();
    }
    
    /**
     * extracts XML from PDF
     */
    @Override
    public byte[] getXMLFromPDF(PdfReader reader) throws IOException, XPathExpressionException {
        XfaForm xfaForm = reader.getAcroFields().getXfa();
        Node domDocument = xfaForm.getDomDocument();
        if (domDocument==null) {
            return null;
        }
        Element documentElement = ((Document) domDocument).getDocumentElement();

        Element datasetsElement = (Element) documentElement.getElementsByTagNameNS(XFA_NS, "datasets").item(0);
        Element dataElement = (Element) datasetsElement.getElementsByTagNameNS(XFA_NS, "data").item(0);

        Element xmlElement = (Element) dataElement.getChildNodes().item(0);
        
        Node budgetElement = getBudgetElement(xmlElement);
        
        return XfaForm.serializeDoc(budgetElement);
    }

    private Node getBudgetElement(Element xmlElement) throws XPathExpressionException {
        Node budgetNode = xmlElement;
        NodeList budgetAttachments =  (NodeList) XPathFactory.newInstance().newXPath().evaluate("//*[local-name(.) = 'BudgetAttachments']", xmlElement, XPathConstants.NODESET);
        if(budgetAttachments!=null && budgetAttachments.getLength()>0){
            Element budgetAttachment = (Element)budgetAttachments.item(0);
            if(budgetAttachment.hasChildNodes()){
                budgetNode = budgetAttachment.getFirstChild();
            }
        }
        return budgetNode;
    }

    @Override
    public void updateSubAwardBudgetDetails(Budget budget, BudgetSubAwards budgetSubAward, List<String[]> errors) throws Exception {

        
        //extarct xml from the pdf because the stored xml has been modified
        if (budgetSubAward.getSubAwardXfdFileData() == null || budgetSubAward.getSubAwardXfdFileData().length == 0) {
            errors.add(new String[]{Constants.SUBAWARD_FILE_NOT_EXTRACTED});
        }
        PdfReader reader = new PdfReader(budgetSubAward.getSubAwardXfdFileData());
        byte[] xmlContents = getXMLFromPDF(reader);
        if (xmlContents == null) {
        	errors.add(new String[]{Constants.SUBAWARD_FILE_NOT_EXTRACTED});
        }
        javax.xml.parsers.DocumentBuilderFactory domParserFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder domParser = domParserFactory.newDocumentBuilder();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xmlContents);
        org.w3c.dom.Document document = domParser.parse(byteArrayInputStream);
        NodeList budgetYearList = (NodeList) XPathFactory.newInstance().newXPath().evaluate(BUDGET_YEAR_XPATH, document, XPathConstants.NODESET);

        boolean fnfForm = StringUtils.contains(budgetSubAward.getFormName(), RR_FED_NON_FED_BUDGET);
        
        //reset current line items if replacing with a new one.
        resetSubAwardPeriodDetails(budgetSubAward);
        
        for (int i = 0; i < budgetYearList.getLength(); i++) {
            Node budgetYear = budgetYearList.item(i);
            Node startDateNode = (Node) XPathFactory.newInstance().newXPath().evaluate("BudgetPeriodStartDate", budgetYear, XPathConstants.NODE);
            if (startDateNode == null) {
                startDateNode = (Node) XPathFactory.newInstance().newXPath().evaluate("PeriodStartDate", budgetYear, XPathConstants.NODE);
            }
            Node endDateNode = (Node) XPathFactory.newInstance().newXPath().evaluate("BudgetPeriodEndDate", budgetYear, XPathConstants.NODE);
            if(endDateNode == null) {
                endDateNode = (Node) XPathFactory.newInstance().newXPath().evaluate("PeriodEndDate", budgetYear, XPathConstants.NODE);
            }
            DateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
            Date startDate = dateFormat.parse(startDateNode.getTextContent());
            Date endDate = dateFormat.parse(endDateNode.getTextContent());
            //attempt to find a matching budget period
            BudgetSubAwardPeriodDetail periodDetail = findBudgetSubAwardPeriodDetail(budget, budgetSubAward, startDate, endDate);
            if (periodDetail != null) {
                Node directCostNode, indirectCostNode, costShareNode = null;
                if (fnfForm) {
                    directCostNode = (Node) XPathFactory.newInstance().newXPath().evaluate("DirectCosts/FederalSummary", budgetYear, XPathConstants.NODE);
                    indirectCostNode = (Node) XPathFactory.newInstance().newXPath().evaluate("IndirectCosts/TotalIndirectCosts/FederalSummary", budgetYear, XPathConstants.NODE);
                    costShareNode = (Node) XPathFactory.newInstance().newXPath().evaluate("TotalCosts/NonFederalSummary", budgetYear, XPathConstants.NODE);
                } else {
                    directCostNode = (Node) XPathFactory.newInstance().newXPath().evaluate("DirectCosts", budgetYear, XPathConstants.NODE);
                    if (directCostNode == null) {
                        directCostNode = (Node) XPathFactory.newInstance().newXPath().evaluate("TotalDirectCostsRequested", budgetYear, XPathConstants.NODE);
                    }

                    indirectCostNode = (Node) XPathFactory.newInstance().newXPath().evaluate("IndirectCosts/TotalIndirectCosts", budgetYear, XPathConstants.NODE);
                    if (indirectCostNode == null) {
                        indirectCostNode = (Node) XPathFactory.newInstance().newXPath().evaluate("TotalIndirectCostsRequested", budgetYear, XPathConstants.NODE);
                    }
                }
                if (directCostNode != null) {
                    periodDetail.setDirectCost(new ScaleTwoDecimal(Float.parseFloat(directCostNode.getTextContent())));
                }
                if (indirectCostNode != null) {
                    periodDetail.setIndirectCost(new ScaleTwoDecimal(Float.parseFloat(indirectCostNode.getTextContent())));
                }
                if (costShareNode != null) {
                    periodDetail.setCostShare(new ScaleTwoDecimal(Float.parseFloat(costShareNode.getTextContent())));
                } else {
                    periodDetail.setCostShare(ScaleTwoDecimal.ZERO);
                }
                periodDetail.computeTotal();
            } else {
                Node budgetPeriodNode = (Node) XPathFactory.newInstance().newXPath().evaluate(BUDGET_PERIOD, budgetYear, XPathConstants.NODE);
                String budgetPeriod = null;
                if (budgetPeriodNode != null) {
                    budgetPeriod = budgetPeriodNode.getTextContent();
                }
                LOG.debug("Unable to find matching period for uploaded period '" + budgetPeriod + "' -- " + startDateNode.getTextContent() + " - " + endDateNode.getTextContent());
                errors.add(new String[]{Constants.SUBAWARD_FILE_PERIOD_NOT_FOUND, (budgetPeriod == null ? "" : budgetPeriod), startDateNode.getTextContent(), endDateNode.getTextContent()});
            }
        }
    }
    
    /**
     * First find a budget period that matches the start and end date. If that is found, find a subaward period detail with the same
     * budget period number.
     */
    protected BudgetSubAwardPeriodDetail findBudgetSubAwardPeriodDetail(Budget budget, BudgetSubAwards budgetSubAward, Date startDate, Date endDate) {
        BudgetPeriod matchingPeriod = null;
        BudgetSubAwardPeriodDetail matchingDetail = null;
        for (BudgetPeriod period : budget.getBudgetPeriods()) {
            if (startDate.getTime() == period.getStartDate().getTime()
                    && endDate.getTime() == period.getEndDate().getTime()) {
                matchingPeriod = period;
                break;
            }
        }
        if (matchingPeriod != null) {

            for (BudgetSubAwardPeriodDetail detail : budgetSubAward.getBudgetSubAwardPeriodDetails()) {
                if (Objects.equals(detail.getBudgetPeriod(), matchingPeriod.getBudgetPeriod())) {
                    matchingDetail = detail;
                    break;
                }
            }
        }
        return matchingDetail;
    }  

    /**
     * updates the XMl with hashcode for the files
     */

  protected void updateXML(byte xmlContents[], List<KcFile> files, BudgetSubAwards budgetSubAwardBean) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException, TransformerException {

        javax.xml.parsers.DocumentBuilderFactory domParserFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder domParser = domParserFactory.newDocumentBuilder();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xmlContents);

        org.w3c.dom.Document document = domParser.parse(byteArrayInputStream);
        byteArrayInputStream.close();
        String namespace=null;
        String formName;
        if (document != null) {
            Node node;
            Element element = document.getDocumentElement();
            NamedNodeMap map = element.getAttributes();
            String namespaceHolder = element.getNodeName().substring(0, element.getNodeName().indexOf(':'));
            node = map.getNamedItem("xmlns:" + namespaceHolder);
            namespace = node.getNodeValue();
            FormMappingInfo formMappingInfo = formMappingService.getFormInfo(namespace);
            formName = formMappingInfo.getFormName();
            budgetSubAwardBean.setNamespace(namespace);
            budgetSubAwardBean.setFormName(formName);
        }

        String xpathEmptyNodes = "//*[not(node()) and local-name(.) != 'FileLocation' and local-name(.) != 'HashValue']";
        String xpathOtherPers = "//*[local-name(.)='ProjectRole' and local-name(../../.)='OtherPersonnel' and count(../NumberOfPersonnel)=0]";
        removeAllEmptyNodes(document,xpathEmptyNodes,0);
        removeAllEmptyNodes(document,xpathOtherPers,1);
        removeAllEmptyNodes(document,xpathEmptyNodes,0);
        changeDataTypeForNumberOfOtherPersons(document);
        
        NodeList budgetYearList =  (NodeList) XPathFactory.newInstance().newXPath().evaluate(BUDGET_YEAR_XPATH, document, XPathConstants.NODESET);
        for(int i=0;i<budgetYearList.getLength();i++){
            Node bgtYearNode = budgetYearList.item(i);
            String period = getValue((Node) XPathFactory.newInstance().newXPath().evaluate(BUDGET_PERIOD, bgtYearNode, XPathConstants.NODE));
            if(FED_NON_FED_FORMS.contains(namespace)){
                Element newBudgetYearElement = copyElementToName((Element)bgtYearNode,bgtYearNode.getNodeName());
                bgtYearNode.getParentNode().replaceChild(newBudgetYearElement,bgtYearNode);
            } else {
                Element newBudgetYearElement = copyElementToName((Element)bgtYearNode,bgtYearNode.getNodeName()+period);
                bgtYearNode.getParentNode().replaceChild(newBudgetYearElement,bgtYearNode);
            }
        }
        
        Node oldroot = document.removeChild(document.getDocumentElement());
        Node newroot = document.appendChild(document.createElement("Forms"));
        newroot.appendChild(oldroot);
        
        org.w3c.dom.NodeList lstFileName = document.getElementsByTagName("att:FileName");
        org.w3c.dom.NodeList lstFileLocation = document.getElementsByTagName("att:FileLocation");
        org.w3c.dom.NodeList lstMimeType = document.getElementsByTagName("att:MimeType");
        org.w3c.dom.NodeList lstHashValue = document.getElementsByTagName("glob:HashValue");

        org.w3c.dom.Node fileNode, hashNode, mimeTypeNode;
        org.w3c.dom.NamedNodeMap fileNodeMap, hashNodeMap;

        List<BudgetSubAwardAttachment> attachmentList = new ArrayList<>();

        for(int index = 0; index < lstFileName.getLength(); index++) {
            fileNode = lstFileName.item(index);
                
            Node fileNameNode = fileNode.getFirstChild();
            String fileName = fileNameNode.getNodeValue();
            final Optional<KcFile> file = files.stream().filter(f -> f.getName().equals(fileName)).findAny();
            final byte[] fileBytes;
            if(!file.isPresent() || file.get().getData() == null) {
                throw new RuntimeException("FileName mismatch in XML and PDF extracted file");
            } else {
                fileBytes = file.get().getData();
            }
            String hashVal = grantApplicationHashService.computeAttachmentHash(fileBytes);

            hashNode = lstHashValue.item(index);
            hashNodeMap = hashNode.getAttributes();

            Node temp = document.createTextNode(hashVal);
            hashNode.appendChild(temp);

            hashNode = hashNodeMap.getNamedItem("glob:hashAlgorithm");

            hashNode.setNodeValue(InfastructureConstants.HASH_ALGORITHM);

            fileNode = lstFileLocation.item(index);
            fileNodeMap = fileNode.getAttributes();
            fileNode = fileNodeMap.getNamedItem("att:href");

            String contentId = fileNode.getNodeValue();
            String encodedContentId = cleanContentId(contentId);
            fileNode.setNodeValue(encodedContentId);

            mimeTypeNode = lstMimeType.item(0);
            String contentType = mimeTypeNode.getFirstChild().getNodeValue();
                
            BudgetSubAwardAttachment budgetSubAwardAttachmentBean = new BudgetSubAwardAttachment();
            budgetSubAwardAttachmentBean.setData(fileBytes);
            budgetSubAwardAttachmentBean.setName(encodedContentId);

            budgetSubAwardAttachmentBean.setType(contentType);
            budgetSubAwardAttachmentBean.setBudgetSubAward(budgetSubAwardBean);

            attachmentList.add(budgetSubAwardAttachmentBean);
        }

        budgetSubAwardBean.setBudgetSubAwardAttachments(attachmentList);
        
        javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(bos);
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(document);
            transformer.transform(source, result);

            budgetSubAwardBean.setSubAwardXmlFileData(new String(bos.toByteArray()));
        }
    }


    protected String cleanContentId(String contentId) {
        return StringUtils.replaceChars(contentId, " .%-_", "");
    }

    @Override
    public void populateBudgetSubAwardAttachments(Budget budget) {
        List<BudgetSubAwards> subAwards = budget.getBudgetSubAwards();
        for (BudgetSubAwards budgetSubAwards : subAwards) {
            budgetSubAwards.refreshReferenceObject("budgetSubAwardAttachments");
        }
    }

    protected void removeAllEmptyNodes(Document document,String xpath,int parentLevel) throws XPathExpressionException {
        NodeList emptyElements =  (NodeList) XPathFactory.newInstance().newXPath().evaluate(xpath, document, XPathConstants.NODESET);
        
        for (int i = emptyElements.getLength()-1; i > -1; i--){
              Node nodeToBeRemoved = emptyElements.item(i);
              int hierLevel = parentLevel;
              while(hierLevel-- > 0){
                  nodeToBeRemoved = nodeToBeRemoved.getParentNode();
              }
              nodeToBeRemoved.getParentNode().removeChild(nodeToBeRemoved);
        }
        NodeList moreEmptyElements =  (NodeList) XPathFactory.newInstance().newXPath().evaluate(xpath, document, XPathConstants.NODESET);
        if(moreEmptyElements.getLength()>0){
            removeAllEmptyNodes(document,xpath,parentLevel);
        }
    }

    protected Element copyElementToName(Element element,String tagName) {
        Element newElement = element.getOwnerDocument().createElement(tagName);
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attribute = attrs.item(i);
            newElement.setAttribute(attribute.getNodeName(),attribute.getNodeValue());
        }
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            newElement.appendChild(element.getChildNodes().item(i).cloneNode(true));
        }
        return newElement;
    }

    private void changeDataTypeForNumberOfOtherPersons(Document document) throws XPathExpressionException {
        NodeList otherPesronsCountNodes =  (NodeList) XPathFactory.newInstance().newXPath().evaluate("//*[local-name(.)='OtherPersonnelTotalNumber']", document, XPathConstants.NODESET);
        for (int i = 0; i < otherPesronsCountNodes.getLength(); i++) {
            Node countNode = otherPesronsCountNodes.item(i);
            String value = getValue(countNode);

            if(value.length()>0 && value.indexOf('.')!=-1){
                int intVal = Double.valueOf(value).intValue();
                setValue(countNode,""+intVal);
            }
        }
    }
    private void setValue(Node node, String value) {
        Node child;
        for (child = node.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             if(child.getNodeType()==Node.TEXT_NODE){
                child.setNodeValue(value);
                break;
             }
        }
    }
    private static String getValue(Node node){
        String textValue = "";
        Node child;
        if(node!=null)
        for (child = node.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             if(child.getNodeType()==Node.TEXT_NODE){
                textValue = child.getNodeValue();
                break;
             }
        }
        return textValue.trim();
    }

    protected ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected BudgetService getBudgetService() {
        return budgetService;
    }

    public void setBudgetService(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public FormMappingService getFormMappingService() {
        return formMappingService;
    }

    public void setFormMappingService(FormMappingService formMappingService) {
        this.formMappingService = formMappingService;
    }

    public GrantApplicationHashService getGrantApplicationHashService() {
        return grantApplicationHashService;
    }

    public void setGrantApplicationHashService(GrantApplicationHashService grantApplicationHashService) {
        this.grantApplicationHashService = grantApplicationHashService;
    }

    public GlobalVariableService getGlobalVariableService() {
        return globalVariableService;
    }

    public void setGlobalVariableService(GlobalVariableService globalVariableService) {
        this.globalVariableService = globalVariableService;
    }

	public SponsorHierarchyService getSponsorHierarchyService() {
		return sponsorHierarchyService;
	}

	public void setSponsorHierarchyService(
			SponsorHierarchyService sponsorHierarchyService) {
		this.sponsorHierarchyService = sponsorHierarchyService;
	}

    public FormUtilityService getFormUtilityService() {
        return formUtilityService;
    }

    public void setFormUtilityService(FormUtilityService formUtilityService) {
        this.formUtilityService = formUtilityService;
    }
}
