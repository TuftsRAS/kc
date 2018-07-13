/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.protocol.funding;

import org.apache.commons.lang3.StringUtils;
import org.kuali.kra.protocol.ProtocolEventBase;
import org.kuali.rice.krad.document.Document;

import java.util.List;

/**
 * 
 * This class implements the tightly coupled Event-Rule approach to Kuali Rule processing for Adding a ProtocolBase Funding Source.
 */
public abstract class AddProtocolFundingSourceEventBase extends ProtocolEventBase<ProtocolFundingSourceRuleBase> {
    
    private static final org.apache.logging.log4j.Logger LOG =
        org.apache.logging.log4j.LogManager.getLogger(AddProtocolFundingSourceEventBase.class);
    
    private static final String MSG = "adding a funding source to a ProtocolBase document ";
    private ProtocolFundingSourceBase fundingSource;
    private List<ProtocolFundingSourceBase> protocolFundingSources;



    protected AddProtocolFundingSourceEventBase(String description, String errorPathPrefix, Document document) {
        super(MSG + getDocumentId(document), errorPathPrefix, document, ErrorType.HARDERROR);
    }
    
    public AddProtocolFundingSourceEventBase(String description,  Document document, ProtocolFundingSourceBase fundingSource, 
            List<ProtocolFundingSourceBase> protocolFundingSources) {
        super(description+": "+MSG + getDocumentId(document), "", document, ErrorType.HARDERROR);

        this.protocolFundingSources = protocolFundingSources;
        this.fundingSource = fundingSource;
    }


    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (document == null) {
            logMessage.append("null protocolDocument");
        }
        else {
            logMessage.append(document.toString());
        }

        LOG.debug(logMessage);   
    }

    public ProtocolFundingSourceBase getFundingSource() {
        return fundingSource;
    }

    @Override
    public abstract ProtocolFundingSourceRuleBase getRule();

    public List<ProtocolFundingSourceBase> getProtocolFundingSources() {
        return protocolFundingSources;
    }

    public void setProtocolFundingSources(List<ProtocolFundingSourceBase> protocolFundingSources) {
        this.protocolFundingSources = protocolFundingSources;
    }
    
}
