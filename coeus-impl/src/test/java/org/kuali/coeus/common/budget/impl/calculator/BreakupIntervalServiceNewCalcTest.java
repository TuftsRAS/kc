/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.impl.calculator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.budget.impl.calculator.BreakUpInterval;
import org.kuali.coeus.common.budget.impl.calculator.RateAndCost;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class BreakupIntervalServiceNewCalcTest extends BreakupIntervalServiceTest {

	@Override
    @Before
	public void setup() {
		super.setup();
		ParameterService parameterService = mock(ParameterService.class);
		when(parameterService.parameterExists(Constants.MODULE_NAMESPACE_BUDGET, Constants.PARAMETER_COMPONENT_DOCUMENT, "breakupServiceUseNewCalculation")).thenReturn(true);
		when(parameterService.getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_BUDGET, Constants.PARAMETER_COMPONENT_DOCUMENT, "breakupServiceUseNewCalculation")).thenReturn(true);
		breakupIntervalService.setParameterService(parameterService);
	}
	
    @Test
    public void testCalculateWithTwoEbRates() {
        BreakUpInterval bi  = createBreakupInterval();
        bi.getBudgetProposalRates().add(getBudgetRate("1",0,1L,"2010",0,true,"5","6","E","07/01/2009","000001"));
        bi.getRateAndCosts().add(getRateCost("5", "6", "E", 10));
        breakupIntervalService.calculate(bi);
        List<RateAndCost> rateAndCosts = bi.getRateAndCosts();
        validateResults(rateAndCosts.get(0), 3375, 675, 6750, 1350);
        validateResults(rateAndCosts.get(1),1250,250,5000,1000);
        validateResults(rateAndCosts.get(2),500,100,5000,1000);
        validateResults(rateAndCosts.get(3),250,50,5000,1000);
        validateResults(rateAndCosts.get(6),7.50,1.50,250,50);
        validateResults(rateAndCosts.get(7),5,1,250,50);
    }
    
    @Test
    public void testCalculateWithTwoEbRatesSimple() {
        BreakUpInterval bi  = createBreakupInterval();
        bi.getBudgetProposalRates().add(getBudgetRate("1",10,1L,"2010",10,true,"5","6","E","07/01/2009","000001"));
        bi.getRateAndCosts().clear();
        bi.getRateAndCosts().add(getRateCost("1", "1", "O", 50));
        bi.getRateAndCosts().add(getRateCost("5", "1", "E", 25));
        bi.getRateAndCosts().add(getRateCost("8", "1", "V", 10));
        bi.getRateAndCosts().add(getRateCost("5", "6", "E", 10));
        breakupIntervalService.calculate(bi);
        List<RateAndCost> rateAndCosts = bi.getRateAndCosts();
        RateAndCost rateAndCost1 = rateAndCosts.get(0);
        validateResults(rateAndCost1,3625,725,7250,1450);
    }
}
