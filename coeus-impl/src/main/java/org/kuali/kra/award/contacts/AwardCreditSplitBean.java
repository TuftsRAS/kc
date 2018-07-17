/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.contacts;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.type.InvestigatorCreditType;
import org.kuali.coeus.propdev.impl.person.creditsplit.CreditSplit;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.AwardForm;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides credit split support
 */
public class AwardCreditSplitBean implements Serializable {
    static final String PERSON_TOTALS_KEY = "personTotalsKey";
    
    private static final long serialVersionUID = 1330497293834315534L;
    private static Logger LOGGER = LogManager.getLogger(AwardCreditSplitBean.class);
    private static final String YES = "Y";
    private static final String AWARD_CREDIT_SPLIT_PARM_NAME = "award.creditsplit.enabled";
    private static final ScaleTwoDecimal ZERO_VALUE = new ScaleTwoDecimal(0);
    private static final ScaleTwoDecimal MAX_VALUE = new ScaleTwoDecimal(100.00);
    
    private AwardForm awardForm;
    private AwardDocument awardDocument;
    
    private transient Collection<InvestigatorCreditType> investigatorCreditTypes;
    private transient ParameterService parameterService;
    private transient AwardService awardService;

    public AwardCreditSplitBean(AwardForm awardForm) {
        this.awardForm = awardForm;
    }

    /** 
     * This constructor should only be called when no AwardForm is not available
     * The AwardForm reference is stable during a session, but AwardDocument is not
     * However, in rule processing, a Form may not be available, especially if from a unit test.
     * 
     * In that case, this constructor should be used. The recalculateCreditSplit(AwardDocument) method should 
     * be called, passing in an AwardDocument
     */
    public AwardCreditSplitBean(AwardDocument awardDocument) {
        this.awardDocument = awardDocument;
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
    
    public AwardPerson getProjectPerson(int index) {
        return getProjectPersons().get(index);
    }
    
    /**
     * This method prepares all project personnel, and their units, with empty credit splits
     * for any InvestigatorCreditTypes that aren't already represented.
     *  
     * @return
     */
    public List<AwardPerson> getProjectPersons() {
        Collection<InvestigatorCreditType> creditTypes = getInvestigatorCreditTypes();
        List<AwardPerson> projectPersons = getAward().getProjectPersons();
        for(AwardPerson p: projectPersons) {
            if (addToCreditSplit(p)) {
                createDefaultCreditSplitMapForProjectPerson(creditTypes, p);
                
                for(AwardPersonUnit apu: p.getUnits()) {
                    createDefaultCreditSplitMapForPersonUnit(creditTypes, apu);
                }
            }
        }
        return getAward().getProjectPersons();
    }

    /**
     * This method returns all Principal Investigators and Co-Investigators.
     * Just like getProjectPersons(), it also prepares all project personnel,
     * and their units, with empty credit splits for any InvestigatorCreditTypes
      * that aren't already represented.
     * @return
     * @see getProjectPersons()
     */
    public List<AwardPerson> getInvestigators() {
        Collection<InvestigatorCreditType> creditTypes = getInvestigatorCreditTypes();
        List<AwardPerson> investigators = getAward().getInvestigators();
        for(AwardPerson investigator: investigators) {
            if (addToCreditSplit(investigator)) {
                createDefaultCreditSplitMapForProjectPerson(creditTypes, investigator);
                for(AwardPersonUnit apu: investigator.getUnits()) {
                    createDefaultCreditSplitMapForPersonUnit(creditTypes, apu);
                }
            }
        }
        return investigators;
    }
    
    public boolean addToCreditSplit(AwardPerson person) {
        return !person.isKeyPerson() || person.isOptInUnitStatus();
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
    public boolean isAwardCreditsLimitApplicable() {
        try {
            String parmValue = fetchParameterValue(AWARD_CREDIT_SPLIT_PARM_NAME);
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
        if(isAwardCreditsLimitApplicable()) {
            Map<String, Map<String, ScaleTwoDecimal>> totalsMap = calculateCreditSplitTotals();
            noErrors = checkIfPersonTotalsAreValid(totalsMap);
            noErrors &= checkIfPersonUnitsTotalsAreValid(totalsMap);
        }
        
        return noErrors;
    }
    
    /**
     * This method fetches the Award Crddit Splits enabled system parm
     * @param parmName
     * @return
     */
    protected String fetchParameterValue(String parmName) {
        return this.getParameterService().getParameterValueAsString(AwardDocument.class, parmName);
    }

    protected ParameterService getParameterService() {
        if (this.parameterService == null) {
            this.parameterService = KcServiceLocator.getService(ParameterService.class);
        }
        return this.parameterService;
    }

    protected AwardService getAwardService() {
        if (this.awardService == null) {
            this.awardService = KcServiceLocator.getService(AwardService.class);
        }
        return awardService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return KcServiceLocator.getService(BusinessObjectService.class);
    }
        
    /**
     * This is  called to generate a map of the new credit split totals.
     *
     * @param document
     * @return Map
     * 
     */
    Map<String, Map<String, ScaleTwoDecimal>> calculateCreditSplitTotals() {
        Map<String, Map<String, ScaleTwoDecimal>> allCreditSplitTotals = new HashMap<String, Map<String, ScaleTwoDecimal>>();
        calculatePersonTotals(allCreditSplitTotals);
        calculatePersonUnitTotals(allCreditSplitTotals);
        
        return allCreditSplitTotals;
    }
    
    /**
     * Find the Award, first by looking to a form bean, if any. If no form bean exists, check if the document exists.
     * If neither exists, returned Award is null 
     * @return
     */
    Award getAward() {
        Award award;
        if(awardForm != null) {
            award = awardForm.getAwardDocument().getAward();
        } else if(awardDocument != null) {
            award = awardDocument.getAward();
        } else {
            award = null;
        }
        
        return award;
    }

    @SuppressWarnings("unchecked")
    Collection<InvestigatorCreditType> loadInvestigatorCreditTypes() {
        Map<String,String> valueMap = new HashMap<String, String>();
        valueMap.put("active", "true");
        return getBusinessObjectService().findMatching(InvestigatorCreditType.class, valueMap);
    }

    /**
     * @param projectPerson
     * @param creditType
     * @param personCreditSplitTotalMap
     */
    private void calculatePersonTotalForCreditSplitType(AwardPerson projectPerson, InvestigatorCreditType creditType, 
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
        for (AwardPerson projectPerson : getPersonsSelectedForCreditSplit()) {
            for (InvestigatorCreditType creditType : creditTypes) {                
                calculatePersonTotalForCreditSplitType(projectPerson, creditType, personCreditSplitTotalMap);
            }
        }
    }

    private void calculatePersonUnitTotals(Map<String, Map<String, ScaleTwoDecimal>> allCreditSplitTotals) {
        Collection<InvestigatorCreditType> creditTypes = getInvestigatorCreditTypes();
        for (AwardPerson projectPerson : getProjectPersons()) {
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

    private void calculateUnitCreditSplitTotals(AwardPerson projectPerson, Map<String, ScaleTwoDecimal> personUnitCreditTotals) {
        if(projectPerson.isKeyPerson() && projectPerson.getUnits().size() == 0) {
            handleKeyPersonWithNoUnits(personUnitCreditTotals);
        } else {
            for (AwardPersonUnit unit : projectPerson.getUnits()) {
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
     * @param personUnitCreditTotals
     */
    private void handleKeyPersonWithNoUnits(Map<String, ScaleTwoDecimal> personUnitCreditTotals) {
        Collection<InvestigatorCreditType> creditTypes = getInvestigatorCreditTypes();
        for(InvestigatorCreditType creditType: creditTypes) {
            personUnitCreditTotals.put(creditType.getCode(), MAX_VALUE);
        }
    }

    private boolean  checkIfPersonTotalsAreValid(Map<String, Map<String, ScaleTwoDecimal>> totalsMap) {
        AwardPersonCreditSplitRule rule = new AwardPersonCreditSplitRuleImpl();
        AwardPersonCreditSplitRuleEvent event = new AwardPersonCreditSplitRuleEvent(awardDocument, totalsMap.get(PERSON_TOTALS_KEY));
        return rule.checkAwardPersonCreditSplitTotals(event);
    }
    
    private boolean checkIfPersonUnitsTotalsAreValid(Map<String, Map<String, ScaleTwoDecimal>> totalsMap) {
        AwardPersonUnitCreditSplitRule rule = new AwardPersonUnitCreditSplitRuleImpl();
        
        boolean success = true;
        for(AwardPerson projectPerson: getAward().getProjectPersons()) {
            AwardPersonUnitCreditSplitRuleEvent event = new AwardPersonUnitCreditSplitRuleEvent(awardDocument, projectPerson, 
                                                                                                totalsMap.get(getPersonKey(projectPerson)));
            if (getAwardService().generateCreditSplitForPerson(projectPerson)) {
                success &= rule.checkAwardPersonUnitCreditSplitTotals(event);
            }
        }
        return success;
    }

    /*
     * @param creditTypes
     * @param apu
     */
    private void createDefaultCreditSplitMapForPersonUnit(Collection<InvestigatorCreditType> creditTypes, AwardPersonUnit apu) {
        Map<InvestigatorCreditType, AwardPersonUnitCreditSplit> personUnitCreditMap = new HashMap<InvestigatorCreditType, AwardPersonUnitCreditSplit>();
        for(AwardPersonUnitCreditSplit apuCreditSplit: apu.getCreditSplits()) {
            personUnitCreditMap.put(apuCreditSplit.getInvestigatorCreditType(), apuCreditSplit);
        }
         
        for(InvestigatorCreditType creditType: creditTypes) {
            if(personUnitCreditMap.get(creditType) == null) {
                apu.add(new AwardPersonUnitCreditSplit(creditType, ZERO_VALUE));
            }
        }
    }

    private void createDefaultCreditSplitMapForProjectPerson(Collection<InvestigatorCreditType> creditTypes, AwardPerson projectPerson) {
        Map<InvestigatorCreditType, AwardPersonCreditSplit> personCreditMap = new HashMap<InvestigatorCreditType, AwardPersonCreditSplit>();
        for(AwardPersonCreditSplit creditSplit: projectPerson.getCreditSplits()) {
            personCreditMap.put(creditSplit.getInvestigatorCreditType(), creditSplit);
        }
        
        for(InvestigatorCreditType creditType: creditTypes) {
            if(personCreditMap.get(creditType) == null) {
                projectPerson.add(new AwardPersonCreditSplit(creditType, ZERO_VALUE));
            }
        }
    }

    private String getPersonKey(AwardPerson projectPerson) {
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

    public List<AwardPerson> getPersonsSelectedForCreditSplit() {
        return getAwardService().getPersonsSelectedForCreditSplit(getProjectPersons());
    }
}
