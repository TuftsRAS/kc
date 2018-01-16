/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.budget.rules;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.budget.framework.rate.InstituteRate;
import org.kuali.coeus.common.budget.impl.rate.InstituteRateMaintenanceDocumentRule;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.maintenance.MaintenanceRuleTestBase;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.List;
import static org.junit.Assert.*;
public class InstituteRateMaintenanceDocumentRuleTest extends MaintenanceRuleTestBase {
    private InstituteRateMaintenanceDocumentRule rule = null;

    @Before
    public void setUp() throws Exception {
        rule = new InstituteRateMaintenanceDocumentRule();
        GlobalVariables.setUserSession(new UserSession("quickstart"));
    }

    @After
    public void tearDown() throws Exception {
        rule = null;
    }

    @Test
    public void testOK() throws Exception {

        InstituteRate instituteRate = new InstituteRate();
        
        instituteRate.setRateClassCode("1");
        instituteRate.setRateTypeCode("1");
        instituteRate.setUnitNumber("000001");
        instituteRate.setActivityTypeCode("1");
        MaintenanceDocument instituteRateDocument = newMaintDoc(instituteRate);
        assertTrue(rule.processCustomRouteDocumentBusinessRules(instituteRateDocument));
        assertTrue(rule.processCustomApproveDocumentBusinessRules(instituteRateDocument));
    }

    /**
     * 
     * This method to test rate type does not exist.
     * @throws Exception
     */
    @Test
    public void testRateTypeNotExist() throws Exception {

        InstituteRate instituteRate = new InstituteRate();
        
        instituteRate.setRateClassCode("10");
        instituteRate.setRateTypeCode("2");
        instituteRate.setUnitNumber("000001");
        instituteRate.setActivityTypeCode("1");
        MaintenanceDocument instituteRateDocument = newMaintDoc(instituteRate);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(instituteRateDocument));
        List errors = GlobalVariables.getMessageMap().getMessages("document.newMaintainableObject.rateTypeCode");
        assertTrue(errors.size() == 1);
        ErrorMessage message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), KeyConstants.ERROR_RATE_TYPE_NOT_EXIST);

        // approve will have the same error too.
        assertFalse(rule.processCustomApproveDocumentBusinessRules(instituteRateDocument));
        errors = GlobalVariables.getMessageMap().getMessages("document.newMaintainableObject.rateTypeCode");
        assertTrue(errors.size() == 1);
        message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), KeyConstants.ERROR_RATE_TYPE_NOT_EXIST);

    }

    @Test
    public void testUnitNumberNotExist() throws Exception {

        InstituteRate instituteRate = new InstituteRate();
        
        instituteRate.setRateClassCode("10");
        instituteRate.setRateTypeCode("1");
        instituteRate.setUnitNumber("00000x");
        instituteRate.setActivityTypeCode("1");
        MaintenanceDocument instituteRateDocument = newMaintDoc(instituteRate);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(instituteRateDocument));
        List errors = GlobalVariables.getMessageMap().getMessages("document.newMaintainableObject.unitNumber");
        assertTrue(errors.size() == 1);
        ErrorMessage message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), RiceKeyConstants.ERROR_EXISTENCE);

        // approve will have the same error too.
        assertFalse(rule.processCustomApproveDocumentBusinessRules(instituteRateDocument));
        errors = GlobalVariables.getMessageMap().getMessages("document.newMaintainableObject.unitNumber");
        assertTrue(errors.size() == 1);
        message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), RiceKeyConstants.ERROR_EXISTENCE);

    }

    @Test
    public void testActivityTypeNotExist() throws Exception {

        InstituteRate instituteRate = new InstituteRate();
        
        instituteRate.setRateClassCode("10");
        instituteRate.setRateTypeCode("1");
        instituteRate.setUnitNumber("000001");
        instituteRate.setActivityTypeCode("x");
        MaintenanceDocument instituteRateDocument = newMaintDoc(instituteRate);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(instituteRateDocument));
        List errors = GlobalVariables.getMessageMap().getMessages("document.newMaintainableObject.activityTypeCode");
        assertTrue(errors.size() == 1);
        ErrorMessage message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), RiceKeyConstants.ERROR_EXISTENCE);

        // approve will have the same error too.
        assertFalse(rule.processCustomApproveDocumentBusinessRules(instituteRateDocument));
        errors = GlobalVariables.getMessageMap().getMessages("document.newMaintainableObject.activityTypeCode");
        assertTrue(errors.size() == 1);
        message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), RiceKeyConstants.ERROR_EXISTENCE);

    }

}
