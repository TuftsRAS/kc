/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.location;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class SaveProposalSitesEvent extends KcDocumentEventBase {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(SaveProposalSitesEvent.class);

    public SaveProposalSitesEvent(String errorPathPrefix, ProposalDevelopmentDocument document) {
        super("Saving Proposal Sites of document " + getDocumentId(document), errorPathPrefix, document);
    }
    
    @Override
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return SaveProposalSitesRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((SaveProposalSitesRule) rule).processSaveProposalSiteBusinessRules(this);
    }

    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));

        LOG.debug(logMessage);
    }
}
