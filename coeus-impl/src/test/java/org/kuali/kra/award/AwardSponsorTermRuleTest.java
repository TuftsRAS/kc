/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.framework.sponsor.term.SponsorTerm;
import org.kuali.coeus.sys.impl.validation.ErrorReporterImpl;
import org.kuali.kra.award.home.AwardSponsorTerm;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import java.util.ArrayList;
import java.util.List;

/**
 * This class tests the rule class of Award Sponsor Term.
 */
public class AwardSponsorTermRuleTest {
    
    private static final Long ONE = new Long(1);
    private static final Long TWO = new Long(2);
    private static final Long THREE = new Long(3);
    private static final String FOUR = "4";

    
    AwardSponsorTerm awardSponsorTerm1;
    AwardSponsorTerm awardSponsorTerm2;
    AwardSponsorTerm awardSponsorTerm3;
    AwardSponsorTermRuleImpl awardSponsorTermRule;
    List<AwardSponsorTerm> awardSponsorTerms;
    SponsorTerm sponsorTerm;


    
    /**
     * This method initializes fields and objects for testing
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        awardSponsorTerms = new ArrayList<AwardSponsorTerm>();
        awardSponsorTermRule = new AwardSponsorTermRuleImpl();
        awardSponsorTermRule.setErrorReporter(new ErrorReporterImpl());
        awardSponsorTerm1 = new AwardSponsorTerm();
        awardSponsorTerm2 = new AwardSponsorTerm();
        awardSponsorTerm3 = new AwardSponsorTerm();
        sponsorTerm = new SponsorTerm();
        sponsorTerm.setSponsorTermTypeCode(FOUR);
        awardSponsorTerm3.setSponsorTerm(sponsorTerm);
        
        awardSponsorTerm1.setSponsorTermId(ONE);
        awardSponsorTerm2.setSponsorTermId(TWO);
        awardSponsorTerm3.setSponsorTermId(THREE);
        awardSponsorTerms.add(awardSponsorTerm1);
        awardSponsorTerms.add(awardSponsorTerm2);
        GlobalVariables.setMessageMap(new MessageMap());
        
        
    }
    
    /**
     * This method sets objects to null.
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        awardSponsorTermRule = null;
        awardSponsorTerm1 = null;
        awardSponsorTerm2 = null;
        awardSponsorTerm3 = null;
        awardSponsorTerms = null;
    }
    
    /**
     * This method tests rule method for non-duplicate SponsorTerms in an award. It sets duplicate fields and retests to ensure
     * failure.
     */
    @Test
    public final void testValidateAwardSponsorTermNotDuplicate() {
        Assert.assertTrue(awardSponsorTermRule.validateAwardSponsorTermNotDuplicate(awardSponsorTerm3, awardSponsorTerms));
        awardSponsorTerm3.setSponsorTermId(TWO);
        Assert.assertFalse(awardSponsorTermRule.validateAwardSponsorTermNotDuplicate(awardSponsorTerm3, awardSponsorTerms));
        awardSponsorTerm3.setSponsorTermId(THREE);
    }

}
