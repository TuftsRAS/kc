/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.contacts;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.type.InvestigatorCreditType;
import org.kuali.coeus.propdev.impl.person.creditsplit.CreditSplit;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.printing.service.InstitutionalProposalPersonService;
import org.kuali.kra.institutionalproposal.web.struts.form.InstitutionalProposalForm;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InstitutionalProposalCreditSplitBean implements Serializable {

    private static final long serialVersionUID = 1033802859817554693L;

    static final String PERSON_TOTALS_KEY = "personTotalsKey";
    
    private static Logger LOGGER = LogManager.getLogger(InstitutionalProposalCreditSplitBean.class);
    private static final String YES = "Y";
    private static final String PROPOSAL_CREDIT_SPLIT_PARM_NAME = "institutionalproposal.creditsplit.enabled";
    private static final ScaleTwoDecimal ZERO_VALUE = new ScaleTwoDecimal(0);
    private static final ScaleTwoDecimal MAX_VALUE = new ScaleTwoDecimal(100.00);
    
    private InstitutionalProposalForm institutionalProposalForm;
    private InstitutionalProposalDocument institutionalProposalDocument;
    
    private transient Collection<InvestigatorCreditType> investigatorCreditTypes;
    private transient ParameterService parameterService;
    private InstitutionalProposalPersonService institutionalProposalPersonService;

    public InstitutionalProposalCreditSplitBean(InstitutionalProposalForm institutionalProposalForm) {
        this.institutionalProposalForm = institutionalProposalForm;
    }

    /** 
     * This constructor should only be called when no InstitutionalProposalForm is not available
     * The InstitutionalProposalForm reference is stable during a session, but InstitutionalProposalDocument is not
     * However, in rule processing, a Form may not be available, especially if from a unit test.
     * 
     * In that case, this constructor should be used. The recalculateCreditSplit(InstitutionalProposalDocument) method should 
     * be called, passing in an InstitutionalProposalDocument
     */
    public InstitutionalProposalCreditSplitBean(InstitutionalProposalDocument institutionalProposalDocument) {
        this.institutionalProposalDocument = institutionalProposalDocument;
    }
    
    public Collection<InvestigatorCreditType> getInvestigatorCreditTypes() {
        if(investigatorCreditTypes == null || investigatorCreditTypes.size() == 0) {
            investigatorCreditTypes = loadInvestigatorCreditTypes();
        }
        return investigatorCreditTypes;
    }

    /**
     * This method returns the map of credit types to award personnel totals
     * @return
     */
    public Map<String, ScaleTwoDecimal> getPersonsTotalsMap() {
        return calculateCreditSplitTotals().get(PERSON_TOTALS_KEY);
    }
    
    public InstitutionalProposalPerson getProjectPerson(int index) {
        return getProjectPersons().get(index);
    }
    
    /**
     * This method prepares all project personnel, and their units, with empty credit splits
     * for any InvestigatorCreditTypes that aren't already represented.
     *  
     * @return
     */
    public List<InstitutionalProposalPerson> getProjectPersons() {
        Collection<InvestigatorCreditType> creditTypes = getInvestigatorCreditTypes();
        List<InstitutionalProposalPerson> projectPersons = getInstitutionalProposal().getProjectPersons();
        for(InstitutionalProposalPerson p: projectPersons) {
            createDefaultCreditSplitMapForProjectPerson(creditTypes, p);
            
            for(InstitutionalProposalPersonUnit apu: p.getUnits()) {
                createDefaultCreditSplitMapForPersonUnit(creditTypes, apu);
            }
        }
        return getInstitutionalProposal().getProjectPersons();
    }

    /**
     * @return The totals map which contains the unit totals 
     */
    public Map<String, Map<String, ScaleTwoDecimal>> getUnitTotalsMap() {
        return calculateCreditSplitTotals();
    }
    
    /**
     * Determines whether credit limits are applicable
     * @return
     */
    public boolean isInstitutionalProposalCreditsLimitApplicable() {
        try {
            String parmValue = fetchParameterValue(PROPOSAL_CREDIT_SPLIT_PARM_NAME);
            return parmValue.equalsIgnoreCase(YES);
        } catch(IllegalArgumentException e) {
            LOGGER.warn(e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Apply calculation total rules
     */
    public boolean recalculateCreditSplit() {
        boolean noErrors = true;
        if(isInstitutionalProposalCreditsLimitApplicable()) {
            Map<String, Map<String, ScaleTwoDecimal>> totalsMap = calculateCreditSplitTotals();
            noErrors = checkIfPersonTotalsAreValid(totalsMap);
            noErrors &= checkIfPersonUnitsTotalsAreValid(totalsMap);
        }
        
        return noErrors;
    }
    
    /**
     * This method fetches the InstitutionalProposal Credit Splits enabled system parm
     * @param parmName
     * @return
     */
    protected String fetchParameterValue(String parmName) {
        return this.getParameterService().getParameterValueAsString(InstitutionalProposalDocument.class, parmName);
    }
    
    /**
     * Looks up and returns the ParameterService.
     * @return the parameter service. 
     */
    protected ParameterService getParameterService() {
        if (this.parameterService == null) {
            this.parameterService = KcServiceLocator.getService(ParameterService.class);
        }
        return this.parameterService;
    }

    protected InstitutionalProposalPersonService getInstitutionalProposalPersonService() {
        if (this.institutionalProposalPersonService == null) {
            this.institutionalProposalPersonService = KcServiceLocator.getService(InstitutionalProposalPersonService.class);
        }
        return institutionalProposalPersonService;
    }
    

    protected BusinessObjectService getBusinessObjectService() {
        return KcServiceLocator.getService(BusinessObjectService.class);
    }

    Map<String, Map<String, ScaleTwoDecimal>> calculateCreditSplitTotals() {
        Map<String, Map<String, ScaleTwoDecimal>> allCreditSplitTotals = new HashMap<String, Map<String, ScaleTwoDecimal>>();
        calculatePersonTotals(allCreditSplitTotals);
        calculatePersonUnitTotals(allCreditSplitTotals);
        
        return allCreditSplitTotals;
    }
    
    /**
     * Find the Institutional Proposal, first by looking to a form bean, if any. If no form bean exists, check if the document exists.
     * If neither exists, returned Institutional Proposal is null 
     * @return
     */
    InstitutionalProposal getInstitutionalProposal() {
        InstitutionalProposal institutionalProposal;
        if(institutionalProposalForm != null) {
            institutionalProposal = institutionalProposalForm.getInstitutionalProposalDocument().getInstitutionalProposal();
        } else if(institutionalProposalDocument != null) {
            institutionalProposal = institutionalProposalDocument.getInstitutionalProposal();
        } else {
            institutionalProposal = null;
        }
        
        return institutionalProposal;
    }

    @SuppressWarnings("unchecked")
    Collection<InvestigatorCreditType> loadInvestigatorCreditTypes() {
        Map<String,String> valueMap = new HashMap<String, String>();
        valueMap.put("active", "true");
        return getBusinessObjectService().findMatching(InvestigatorCreditType.class, valueMap);
    }

    private void calculatePersonTotalForCreditSplitType(InstitutionalProposalPerson projectPerson, InvestigatorCreditType creditType, 
                                                            Map<String, ScaleTwoDecimal> personCreditSplitTotalMap) {
        String creditTypeCode = creditType.getCode();
        ScaleTwoDecimal personsTotalCredit = personCreditSplitTotalMap.get(creditTypeCode);

        if (personsTotalCredit == null) {
            personsTotalCredit = ZERO_VALUE;                    
        }
        
        for (CreditSplit personCreditSplit : projectPerson.getCreditSplits()) {
            if (personCreditSplit.getInvCreditTypeCode().equals(creditTypeCode)) {
                personCreditSplitTotalMap.put(creditTypeCode, personsTotalCredit.add(personCreditSplit.getCredit()));
            }
        }
    }

    private void calculatePersonTotals(Map<String, Map<String, ScaleTwoDecimal>> allCreditSplitTotals) {
        Collection<InvestigatorCreditType> creditTypes = getInvestigatorCreditTypes();
        Map<String, ScaleTwoDecimal> personCreditSplitTotalMap = initializePersonCreditSplitTotalMap(allCreditSplitTotals);
        for (InstitutionalProposalPerson projectPerson : getPersonsSelectedForCreditSplit()) {
                for (InvestigatorCreditType creditType : creditTypes) {
                    calculatePersonTotalForCreditSplitType(projectPerson, creditType, personCreditSplitTotalMap);
                }
            }
    }

    private void calculatePersonUnitTotals(Map<String, Map<String, ScaleTwoDecimal>> allCreditSplitTotals) {
        Collection<InvestigatorCreditType> creditTypes = getInvestigatorCreditTypes();
        for (InstitutionalProposalPerson projectPerson : getProjectPersons()) {
            String personKey = getPersonKey(projectPerson);
            Map<String, ScaleTwoDecimal> personUnitCreditTotals = allCreditSplitTotals.get(personKey);
            
            if (personUnitCreditTotals == null) {
                personUnitCreditTotals = new HashMap<>();
                allCreditSplitTotals.put(personKey, personUnitCreditTotals);
            }

            for (InvestigatorCreditType creditType : creditTypes) {                
                String creditTypeCode = creditType.getCode();
                ScaleTwoDecimal totalCredit = personUnitCreditTotals.get(creditTypeCode);
                personUnitCreditTotals.put(creditTypeCode, totalCredit != null ? totalCredit : ZERO_VALUE);
            }

            calculateUnitCreditSplitTotals(projectPerson, personUnitCreditTotals);
        }
    }

    private void calculateUnitCreditSplitTotals(InstitutionalProposalPerson projectPerson, Map<String, ScaleTwoDecimal> personUnitCreditTotals) {
        if(projectPerson.isKeyPerson() && projectPerson.getUnits().size() == 0) {
            handleKeyPersonWithNoUnits(personUnitCreditTotals);
        } else {
            for (InstitutionalProposalPersonUnit unit : projectPerson.getUnits()) {
                for (CreditSplit creditSplit : unit.getCreditSplits()) {
                    ScaleTwoDecimal totalCredit = personUnitCreditTotals.get(creditSplit.getInvCreditTypeCode());

                    if (totalCredit == null) {
                        totalCredit = ZERO_VALUE;
                    }
                    personUnitCreditTotals.put(creditSplit.getInvCreditTypeCode(), totalCredit.add(creditSplit.getCredit()));
                }
            }
        }
    }

    /**
     * A keyPerson may have no associated unit. To satisfy the validation checks, we apply this workaround to set the unit credit split type totals to 100.00 
     */
    private void handleKeyPersonWithNoUnits(Map<String, ScaleTwoDecimal> personUnitCreditTotals) {
        Collection<InvestigatorCreditType> creditTypes = getInvestigatorCreditTypes();
        for(InvestigatorCreditType creditType: creditTypes) {
            personUnitCreditTotals.put(creditType.getCode(), MAX_VALUE);
        }
    }

    private boolean  checkIfPersonTotalsAreValid(Map<String, Map<String, ScaleTwoDecimal>> totalsMap) {
        InstitutionalProposalPersonCreditSplitRule rule = new InstitutionalProposalPersonCreditSplitRuleImpl();
        InstitutionalProposalPersonCreditSplitRuleEvent event = new InstitutionalProposalPersonCreditSplitRuleEvent(institutionalProposalDocument, totalsMap.get(PERSON_TOTALS_KEY));
        return rule.checkInstitutionalProposalPersonCreditSplitTotals(event);
    }
    
    private boolean checkIfPersonUnitsTotalsAreValid(Map<String, Map<String, ScaleTwoDecimal>> totalsMap) {
        InstitutionalProposalPersonUnitCreditSplitRule rule = new InstitutionalProposalPersonUnitCreditSplitRuleImpl();
        
        boolean success = true;
        for(InstitutionalProposalPerson projectPerson: getInstitutionalProposal().getProjectPersons()) {
            InstitutionalProposalPersonUnitCreditSplitRuleEvent event = new InstitutionalProposalPersonUnitCreditSplitRuleEvent(institutionalProposalDocument, projectPerson, 
                                                                                                totalsMap.get(getPersonKey(projectPerson)));
            if (getInstitutionalProposalPersonService().generateCreditSplitForPerson(projectPerson)) {
                success &= rule.checkInstitutionalProposalPersonUnitCreditSplitTotals(event);
            }
        }
        return success;                                                                               
    }

    private void createDefaultCreditSplitMapForPersonUnit(Collection<InvestigatorCreditType> creditTypes, InstitutionalProposalPersonUnit apu) {
        Map<InvestigatorCreditType, InstitutionalProposalPersonUnitCreditSplit> personUnitCreditMap = new HashMap<InvestigatorCreditType, InstitutionalProposalPersonUnitCreditSplit>();
        for(InstitutionalProposalPersonUnitCreditSplit apuCreditSplit: apu.getCreditSplits()) {
            personUnitCreditMap.put(apuCreditSplit.getInvestigatorCreditType(), apuCreditSplit);
        }
         
        for(InvestigatorCreditType creditType: creditTypes) {
            if(personUnitCreditMap.get(creditType) == null) {
                apu.add(new InstitutionalProposalPersonUnitCreditSplit(creditType, ZERO_VALUE));
            }
        }
    }

    private void createDefaultCreditSplitMapForProjectPerson(Collection<InvestigatorCreditType> creditTypes, InstitutionalProposalPerson projectPerson) {
        Map<InvestigatorCreditType, InstitutionalProposalPersonCreditSplit> personCreditMap = new HashMap<InvestigatorCreditType, InstitutionalProposalPersonCreditSplit>();
        for(InstitutionalProposalPersonCreditSplit creditSplit: projectPerson.getCreditSplits()) {
            personCreditMap.put(creditSplit.getInvestigatorCreditType(), creditSplit);
        }
        
        for(InvestigatorCreditType creditType: creditTypes) {
            if(personCreditMap.get(creditType) == null) {
                projectPerson.add(new InstitutionalProposalPersonCreditSplit(creditType, ZERO_VALUE));
            }
        }
    }

    private String getPersonKey(InstitutionalProposalPerson projectPerson) {
        return projectPerson.getFullName();
    }

    private Map<String, ScaleTwoDecimal> initializePersonCreditSplitTotalMap(Map<String, Map<String, ScaleTwoDecimal>> allCreditSplitTotals) {
        Map<String, ScaleTwoDecimal> personCreditTypeTotals = allCreditSplitTotals.get(PERSON_TOTALS_KEY);
        
        if (personCreditTypeTotals == null) {
            personCreditTypeTotals = new HashMap<>();
            allCreditSplitTotals.put(PERSON_TOTALS_KEY, personCreditTypeTotals);
        }
        return personCreditTypeTotals;
    }

    public List<InstitutionalProposalPerson> getPersonsSelectedForCreditSplit() {
        return getInstitutionalProposalPersonService().getPersonsSelectedForCreditSplit(getProjectPersons());
    }

}
