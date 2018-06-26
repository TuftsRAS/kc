/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.award.service.AwardHierarchyUIService;
import org.kuali.kra.timeandmoney.AwardHierarchyNode;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AwardInquirable extends KualiInquirableImpl {
    
    private static final Logger LOG = LogManager.getLogger(AwardInquirable.class);

    @Override
    public List<Section> getSections(BusinessObject bo) {
        List<Section> sections = new ArrayList<Section>();
        Section section = new Section();
        
        section.setRows(new ArrayList<Row>());
        section.setDefaultOpen(true);
        section.setNumberOfColumns(2);
        
        AwardHierarchyUIService service = getAwardHierarchyUIService();
        Award award = (Award) bo;
        AwardHierarchyNode awardNode = null;
        try {
            awardNode = service.getRootAwardNode(award);
        }
        catch (ParseException e) {
            LOG.error("Error parsing award information" ,e);
        }
        
        // Adding the section title
        String sectionTitle = "";
        sectionTitle += awardNode.getAwardNumber();
        sectionTitle += KRADConstants.BLANK_SPACE;
        sectionTitle += Constants.COLON;
        sectionTitle += KRADConstants.BLANK_SPACE;
        
        if (ObjectUtils.isNotNull(award.getAccountNumber())) {
            sectionTitle += awardNode.getAccountNumber();
            sectionTitle += KRADConstants.BLANK_SPACE;
            sectionTitle += Constants.COLON;
            sectionTitle += KRADConstants.BLANK_SPACE;
        }
        if (ObjectUtils.isNotNull(awardNode.getPrincipalInvestigatorName())) {
            sectionTitle += awardNode.getPrincipalInvestigatorName();
            sectionTitle += KRADConstants.BLANK_SPACE;
            sectionTitle += Constants.COLON;
            sectionTitle += KRADConstants.BLANK_SPACE;
        } 
        if (ObjectUtils.isNotNull(awardNode.getLeadUnitName())) {
            sectionTitle += awardNode.getLeadUnitName();
        }
        
        section.setSectionTitle(sectionTitle);

        //Adding the rows to the sections
        section.setRows(new ArrayList<Row>());
        Row row1 = new Row();
        addField(awardNode.getProjectStartDate() + "", row1, "projectStartDate", "Project Start Date");
        addField(awardNode.getCurrentFundEffectiveDate() + "", row1, "obligationStartDate", "Obligation Start Date");
        section.getRows().add(row1);

        Row row2 = new Row();        
        addField(awardNode.getFinalExpirationDate() + "", row2, "projectEndDate", "Project End Date");
        addField(awardNode.getObligationExpirationDate() + "", row2, "obligationEndDate", "Obligation End Date");
        section.getRows().add(row2);

        Row row3 = new Row(); 
        addField(awardNode.getAnticipatedTotalAmount() + "", row3, "anticipatedAmount", "Anticipated Amount");
        addField(awardNode.getAmountObligatedToDate() + "", row3, "obligatedAmount", "Obligated Amount");
        section.getRows().add(row3);

        Row row4 = new Row();
        addField(awardNode.getTitle(), row4, "title", "Title");
        addField(award.getSponsorAwardNumber(), row4, "sponsorAwardNumber", "Sponsor Award Number");
        section.getRows().add(row4);

        Row row5 = new Row();
        addField(award.getAwardNumber(), row5, "awardNumber", "Award Number");
        addField(award.getAwardStatus().getStatusCode() + "", row5, "awardStatusCode", "Award Status Code");
        section.getRows().add(row5);

        Row row6 = new Row();
        addField(award.getOspAdministratorName(), row6, "ospAdminName", "OSP Administrator Name");
        addField(award.getCloseoutDate() + "", row6, "closeoutDate", "Closeout Date");
        section.getRows().add(row6);

        section.getRows().addAll(IntStream.range(0, award.getAwardCfdas().size())
                .mapToObj(i -> {
                    final Row row = new Row();
                    addField(award.getAwardCfdas().get(i).getCfdaNumber(), row, String.format("awardCfdas[%s].cfdaNumber", i), "CFDA Number " + i + 1);
                    addField(award.getAwardCfdas().get(i).getCfdaNumber(), row, String.format("awardCfdas[%s].cfdaDescription", i), "CFDA Program Title Name " + i + 1);

                    return row;
                }).collect(Collectors.toList()));

        sections.add(section);
        return sections;
    }

    private void addField(String text, Row row1, String propertyName, String fieldLabel) {
        Field field = new Field();
        field.setPropertyName(propertyName);        
        field.setFieldLabel(fieldLabel);        
        field.setFieldType(Field.TEXT);
        if(StringUtils.equalsIgnoreCase(text, " &nbsp; ")){
            text = "";
        }
        field.setPropertyValue(text);        
        row1.getFields().add(field);
    }

    private AwardHierarchyUIService getAwardHierarchyUIService() {
        return KcServiceLocator.getService(AwardHierarchyUIService.class);
    }
} 
