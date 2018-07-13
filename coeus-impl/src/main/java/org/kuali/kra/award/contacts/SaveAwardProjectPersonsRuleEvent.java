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
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

import java.util.List;

/**
 * This class is the event passed when a new projectPerson is being added
 */
public class SaveAwardProjectPersonsRuleEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(SaveAwardProjectPersonsRuleEvent.class);
    
    private List<AwardPerson> projectPersons;
    
    public SaveAwardProjectPersonsRuleEvent(String description, String errorPathPrefix, Document document) {
        super(description, errorPathPrefix, document);
        this.projectPersons = ((AwardDocument) document).getAward().getProjectPersons();
    }

    /**
     * Gets the Award project persons. 
     * @return Returns the Award project persons
     */
    public List<AwardPerson> getProjectPersons() {
        return projectPersons;
    }

    @Override
    public Class<AwardProjectPersonAddRule> getRuleInterfaceClass() {
        return AwardProjectPersonAddRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardProjectPersonsSaveRule) rule).processSaveAwardProjectPersonsBusinessRules(this);
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging SaveAwardProjectPersonsRuleEvent");
    }

}
