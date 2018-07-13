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
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * This class is the event passed when a new projectPerson is being added
 */
public class AwardPersonUnitRuleAddEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(AwardPersonUnitRuleAddEvent.class);
    
    private AwardPersonUnit newPersonUnit;
    private AwardPerson projectPerson;
    private int addUnitPersonIndex;
    
    protected AwardPersonUnitRuleAddEvent(String description, String errorPathPrefix, Document document, 
                                                    AwardPerson projectPerson, AwardPersonUnit newPersonUnit,
                                                    int addUnitPersonIndex) {
        super(description, errorPathPrefix, document);
        this.newPersonUnit = newPersonUnit;
        this.projectPerson = projectPerson;
        this.addUnitPersonIndex = addUnitPersonIndex;
    }


    public AwardPersonUnit getNewPersonUnit() {
        return newPersonUnit;
    }

    public AwardPerson getProjectPerson() {
        return projectPerson;
    }
    
    @Override
    public Class<AwardPersonUnitAddRule> getRuleInterfaceClass() {
        return AwardPersonUnitAddRule.class;
    }

    public void setAddUnitPersonIndex(int addUnitPersonIndex) {
        this.addUnitPersonIndex = addUnitPersonIndex;
    }

    public int getAddUnitPersonIndex() {
        return addUnitPersonIndex;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardPersonUnitAddRule) rule).processAddAwardPersonUnitBusinessRules(this);
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging AddAwardProjectPersonRuleEvent");
    }
}
