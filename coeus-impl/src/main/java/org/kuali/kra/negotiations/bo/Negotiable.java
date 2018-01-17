/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.negotiations.bo;

import org.kuali.coeus.common.framework.type.ProposalType;

import java.util.List;

/**
 * 
 * This interface defines the methods a BO needs to implement in order to successfully associate with the negotiations module.
 */
public interface Negotiable {
    
    public String EMPTY_STRING = "";
    
    /**
     * Returns the negotiable's document id.
     * @return
     */
    String getAssociatedDocumentId();
    
    /**
     * 
     * This method returns the lead unit's number, if it exists, otherwise returns an empty string.
     * @return
     */
    String getLeadUnitNumber();
    
    /**
     * 
     * This method returns the lead unit's name, if it exists, otherwise returns an empty string.
     * @return
     */
    String getLeadUnitName();
    
    /**
     * 
     * This method returns the BO's title, if it exists, otherwise returns an empty string.
     * @return
     */
    String getTitle();
    
    /**
     * Returns the PI's name whether employee or non-employee.
     * @return
     */
    String getPiName();
    
    /**
     * 
     * This method returns the employee principle investigator's name, if it exists, otherwise returns an empty string.
     * @return
     */
    String getPiEmployeeName();
    
    /**
     * 
     * This method returns the the non-employee (Rolodex) Principle Ivestigator's name, if it exists, otherwise returns an empty string.
     * @return
     */
    String getPiNonEmployeeName();
    
    /**
     * 
     * This method returns the admin person's name, if it exists, otherwise returns an empty string.
     * @return
     */
    String getAdminPersonName();
    
    /**
     * 
     * This method returns the sponsor's code, if it exists, otherwise returns an empty string.
     * @return
     */
    String getSponsorCode();
    
    /**
     * 
     * This method returns the sponsor's name, if it exists, otherwise returns an empty string.
     * @return
     */
    String getSponsorName();
    
    /**
     * 
     * This method returns the prime sponsor's code, if it exists, otherwise returns an empty string.
     * @return
     */
    String getPrimeSponsorCode();
    
    /**
     * 
     * This method returns the prime sponsor's name, if it exists, otherwise returns an empty string.
     * @return
     */
    String getPrimeSponsorName();
    
    /**
     * 
     * This method returns the sponsor award number, if it exists, otherwise returns an empty string.
     * @return
     */
    String getSponsorAwardNumber();
    
    /**
     * 
     * This method returns the sub award organization name, if it exists, otherwise returns an empty string.
     * @return
     */
    String getSubAwardOrganizationName();
    
    /**
     * 
     * This method returns a list of KcPersons that include the PI, COI, and Key Personnel.
     * @return
     */
    List<NegotiationPersonDTO> getProjectPeople();
    
    /**
     * Get the proposal type code from this negotiable. Renamed to negotiableproposaltypecode as
     * PropertyUtils appears to get a descriptor for the interface instead of the BO itself in some
     * cases and this can cause the property to seem unwritable.
     * @return
     */
    String getNegotiableProposalTypeCode();
    
    /**
     * 
     * This method a proposal type if it is an institutional proposal or a proposal log.
     * @return
     */
    ProposalType getNegotiableProposalType();
    
    /**
     * 
     * This method returns the Requisitioner's Name if it is a subaward, otherwise returns an empty string.
     * @return
     */
    String getSubAwardRequisitionerName();
    
    /**
     * 
     * This method returns the Requisitioner's ID if it is a subaward, otherwise returns an empty string.
     * @return
     */
    String getSubAwardRequisitionerId();
    
    /**
     * 
     * This method returns the Requisitioner's Unit Number if it is a subaward, otherwise returns an empty string.
     * @return
     */
    String getSubAwardRequisitionerUnitNumber();
    
    /**
     * 
     * This method returns the Requisitioner's Unit Name if it is a subaward, otherwise returns an empty string.
     * @return
     */
    String getSubAwardRequisitionerUnitName();
    
}
