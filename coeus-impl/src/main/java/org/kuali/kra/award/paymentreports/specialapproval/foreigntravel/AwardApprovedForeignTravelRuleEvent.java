/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.paymentreports.specialapproval.foreigntravel;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.paymentreports.specialapproval.approvedequipment.AwardApprovedEquipmentRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * This the AwardApprovedEquipmentRuleEvent
 */
public class AwardApprovedForeignTravelRuleEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(AwardApprovedForeignTravelRuleEvent.class);
    
    private Award award;
    private AwardApprovedForeignTravel approvedForeignTravel;
    
    public AwardApprovedForeignTravelRuleEvent(String errorPathPrefix, 
                                            AwardDocument awardDocument,
                                            Award award,
                                            AwardApprovedForeignTravel approvedForeignTravel) {
        super("Approved equipment item", errorPathPrefix, awardDocument);
        this.award = award;
        this.approvedForeignTravel = approvedForeignTravel; 
    }

    /**
     * Convenience method to return an Award
     * @return
     */
    public Award getAward() {
        return award;
    }
    
    /**
     * Convenience method to return an AwardDocument
     * @return
     */
    public AwardDocument getAwardDocument() {
        return (AwardDocument) getDocument();
    }
    
    /**
     * This method returns the equipment item for validation
     * @return
     */
    public AwardApprovedForeignTravel getForeignTravelForValidation() {
        return approvedForeignTravel;
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging AwardApprovedForeignTravelRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AwardApprovedEquipmentRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardApprovedForeignTravelRule)rule).processAwardApprovedForeignTravelBusinessRules(this);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((approvedForeignTravel == null) ? 0 : approvedForeignTravel.hashCode());
        result = PRIME * result + ((award == null) ? 0 : award.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AwardApprovedForeignTravelRuleEvent)) {
            return false;
        }
        AwardApprovedForeignTravelRuleEvent other = (AwardApprovedForeignTravelRuleEvent) obj;
        if (approvedForeignTravel == null) {
            if (other.approvedForeignTravel != null) {
                return false;
            }
        } else if (!approvedForeignTravel.equals(other.approvedForeignTravel)) {
            return false;
        }
        if (award == null) {
            if (other.award != null) {
                return false;
            }
        } else if (!award.equals(other.award)) {
            return false;
        }
        return true;
    }
}
