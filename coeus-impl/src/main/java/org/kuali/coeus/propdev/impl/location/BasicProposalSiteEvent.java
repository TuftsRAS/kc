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

import java.util.ArrayList;
import java.util.List;

/**
 * Superclass for rule events related to Proposal Sites.
 */
public class BasicProposalSiteEvent extends KcDocumentEventBase {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(AddProposalCongressionalDistrictEvent.class);
    private List<ProposalSite> proposalSites;
    private String siteIndex;
    private List<CongressionalDistrict> congressionalDistricts;

    public BasicProposalSiteEvent(String errorPathPrefix, ProposalDevelopmentDocument proposalDevelopmentDocument) {
        this("Creating a rule event that only checks the proposal site index. Document Id = " + getDocumentId(proposalDevelopmentDocument), errorPathPrefix, proposalDevelopmentDocument);
    }
    
    /**
     * This constructor takes a single ProposalSite.
     * @param description
     * @param errorPathPrefix
     * @param proposalDevelopmentDocument
     * @param proposalSite
     */
    public BasicProposalSiteEvent(String description, String errorPathPrefix, ProposalDevelopmentDocument proposalDevelopmentDocument, ProposalSite proposalSite) {
        this(description, errorPathPrefix, proposalDevelopmentDocument);
        
        proposalSites = new ArrayList<ProposalSite>();
        proposalSites.add(proposalSite);
        
        siteIndex = "0";
    }

    /**
     * This constructor takes a List of ProposalSites and an index that points to one of them.
     * @param description
     * @param errorPathPrefix
     * @param proposalDevelopmentDocument
     * @param proposalSites
     * @param siteIndex
     */
    public BasicProposalSiteEvent(String description, String errorPathPrefix, ProposalDevelopmentDocument proposalDevelopmentDocument, List<ProposalSite> proposalSites, String siteIndex) {
        this(description, errorPathPrefix, proposalDevelopmentDocument);
        this.proposalSites = proposalSites;
        this.siteIndex = siteIndex;
    }

    private BasicProposalSiteEvent(String description, String errorPathPrefix, ProposalDevelopmentDocument proposalDevelopmentDocument) {
        super(description, errorPathPrefix, proposalDevelopmentDocument);
    }
    
    public BasicProposalSiteEvent(String description, ProposalDevelopmentDocument proposalDevelopmentDocument, List<CongressionalDistrict> congressionalDistricts) {
        this(description,proposalDevelopmentDocument);
        this.congressionalDistricts = congressionalDistricts;       
    }
    
    public BasicProposalSiteEvent(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        this("Creating a rule event that only checks the proposal site index. Document Id = " + getDocumentId(proposalDevelopmentDocument), proposalDevelopmentDocument);
    }
    
    public List<ProposalSite> getProposalSites() {
        return proposalSites;
    }

    public String getSiteIndex() {
        return siteIndex;
    }

    /**
     * This method returns the proposal site at the index pointed to by siteIndex.
     * No check is done to ensure siteIndex is a valid index, etc.
     * @return
     */
    public ProposalSite getProposalSite() {
        return proposalSites.get(new Integer(siteIndex));
    }
    
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));

        logMessage.append("with site index ");
        logMessage.append(siteIndex);
        logMessage.append(", ");
        logMessage.append(proposalSites==null?"null":proposalSites.size());
        logMessage.append(" proposal sites");

        LOG.debug(logMessage);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return ProposalSiteRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ProposalSiteRule)rule).processBasicProposalSiteRules(this);
    }
}
