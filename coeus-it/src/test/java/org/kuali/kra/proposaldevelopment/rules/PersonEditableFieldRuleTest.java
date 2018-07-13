/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.proposaldevelopment.rules;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.framework.person.attr.PersonEditableField;
import org.kuali.coeus.propdev.impl.editable.PersonEditableFieldRule;
import org.kuali.kra.maintenance.MaintenanceRuleTestBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;

import static org.junit.Assert.*;

public class PersonEditableFieldRuleTest extends MaintenanceRuleTestBase {

    private static final Logger LOG = LogManager.getLogger(PersonEditableFieldRuleTest.class);

    private static final PersonEditableFieldFixture ADDRESS_LINE1_FIELD = PersonEditableFieldFixture.ADDRESS_LINE1_FIELD;
    private static final PersonEditableFieldFixture ADDRESS_LINE2_FIELD = PersonEditableFieldFixture.ADDRESS_LINE2_FIELD;
        

    @Before
    public void setUp() throws Exception {
        GlobalVariables.setUserSession(new UserSession("quickstart"));
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Case for if a <code>{@link PersonEditableField}</code> already exists.
     */
    @Test
    public void processCustomRouteDocumentBusinessRules_ExistingField() throws Exception {
        PersonEditableField editableField = ADDRESS_LINE1_FIELD.getInstance();
        MaintenanceDocument editableFieldDocument = newMaintDoc(editableField);
        PersonEditableFieldRule rule = setupMaintDocRule(editableFieldDocument, PersonEditableFieldRule.class);         
        
        assertTrue(rule.processCustomRouteDocumentBusinessRules(editableFieldDocument));
    }

    /**
     * Valid case where there are no persisted <code>{@link PersonEditableField}</code> already.
     */
    @Test
    public void processCustomRouteDocumentBusinessRules_Normal() throws Exception {
        PersonEditableField editableField = ADDRESS_LINE2_FIELD.getInstance();
        MaintenanceDocument editableFieldDocument = newMaintDoc(editableField);
        PersonEditableFieldRule rule = setupMaintDocRule(editableFieldDocument, PersonEditableFieldRule.class);
        
        LOG.info("pass " + rule.processCustomRouteDocumentBusinessRules(editableFieldDocument));
    }
}

/**
 * Fixtures for <code>{@link PersonEditableField}</code> business object. Used to wrap the <code>{@link PersonEditableField}</code> constructor and create
 * arbitrary <code>{@link PersonEditableField}</code> instances. This is useful for creating fixtures as they are arbitrary in nature.
 */
enum PersonEditableFieldFixture {
    ADDRESS_LINE1_FIELD("addressLine1"),
    ADDRESS_LINE2_FIELD("addressLine2");
    
    private PersonEditableField field;
    
    /**
     * Create a <code>{@link PersonEditableField}</code> instance, and set active status from a parameter.
     */
    PersonEditableFieldFixture(String fieldName, boolean active) {
        setInstance(new PersonEditableField());
        getInstance().setFieldName(fieldName);
        getInstance().setActive(active);
    }

    /**
     * Create a <code>{@link PersonEditableField}</code> instance and default to activated.
     */
    PersonEditableFieldFixture(String fieldName) {
        this(fieldName, true);
    }

    /**
     * Read access to the enclosed <code>{@link PersonEditableField}</code> instance.
     */
    public PersonEditableField getInstance() {
        return field;
    }

    /**
     * Write access to the enclosed <code>{@link PersonEditableField}</code> instance..
     */ 
    public void setInstance(PersonEditableField field) {
        this.field = field;
    }
}
