/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.custom.attr;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeDocument;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.maintenance.MaintenanceRuleTestBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.List;
import static org.junit.Assert.*;
public class CustomAttributeDocumentMaintenanceDocumentRuleTest extends MaintenanceRuleTestBase {
    private CustomAttributeDocumentMaintenanceDocumentRule rule = null;

    @Before
    public void setUp() throws Exception {
        rule = new CustomAttributeDocumentMaintenanceDocumentRule();
        GlobalVariables.setUserSession(new UserSession("quickstart"));
    }

    @After
    public void tearDown() throws Exception {
        rule = null;
    }

    /**
     * 
     * This method test both custom attribute that has not been referenced by any doc
     * @throws Exception
     */
    @Test
    public void testOK() throws Exception {

        CustomAttributeDocument customAttributeDocument = new CustomAttributeDocument();
        customAttributeDocument.setId(1L);
        customAttributeDocument.setActive(true);
        MaintenanceDocument customAttributeDocumentMaintDoc = newMaintDoc(customAttributeDocument);
        assertTrue(rule.processCustomRouteDocumentBusinessRules(customAttributeDocumentMaintDoc));
        assertTrue(rule.processCustomApproveDocumentBusinessRules(customAttributeDocumentMaintDoc));
    }

    /**
     * 
     * This method to test to check if it ok to inactivate a customattribute doc.
     * @throws Exception
     */
    @Test
    public void testOkToInactivate() throws Exception {

        CustomAttributeDocument customAttributeDocument = new CustomAttributeDocument();
        customAttributeDocument.setId(8L);
        MaintenanceDocument customAttributeDocumentMaintDoc = newMaintDoc(customAttributeDocument);
        assertTrue(rule.processCustomRouteDocumentBusinessRules(customAttributeDocumentMaintDoc));

        // approve will have the same error too.
        assertTrue(rule.processCustomApproveDocumentBusinessRules(customAttributeDocumentMaintDoc));

    }

    @Test
    public void testInvalidCustomAttributeId() throws Exception {

        CustomAttributeDocument customAttributeDocument = new CustomAttributeDocument();
        customAttributeDocument.setId(99L);
        MaintenanceDocument customAttributeDocumentMaintDoc = newMaintDoc(customAttributeDocument);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(customAttributeDocumentMaintDoc));
        List errors = GlobalVariables.getMessageMap().getMessages(Constants.DOCUMENT_NEWMAINTAINABLEOBJECT_CUSTOM_ATTRIBUTE_ID);
        assertTrue(errors.size() == 1);
        ErrorMessage message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), KeyConstants.ERROR_INVALID_CUSTOM_ATT_ID);

        // approve will have the same error too.
        assertFalse(rule.processCustomApproveDocumentBusinessRules(customAttributeDocumentMaintDoc));
        errors = GlobalVariables.getMessageMap().getMessages(Constants.DOCUMENT_NEWMAINTAINABLEOBJECT_CUSTOM_ATTRIBUTE_ID);
        assertTrue(errors.size() == 1);
        message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), KeyConstants.ERROR_INVALID_CUSTOM_ATT_ID);

    }

}
