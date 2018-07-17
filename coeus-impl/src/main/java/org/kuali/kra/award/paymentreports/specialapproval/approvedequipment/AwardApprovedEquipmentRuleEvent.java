/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.paymentreports.specialapproval.approvedequipment;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * This the AwardApprovedEquipmentRuleEvent
 */
public class AwardApprovedEquipmentRuleEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(AwardApprovedEquipmentRuleEvent.class);
    
    private Award award;
    private AwardApprovedEquipment approvedEquipmentItem;
    private MinimumCapitalizationInfo minimumCapitalization;
    
    public AwardApprovedEquipmentRuleEvent(String errorPathPrefix, 
                                            AwardDocument awardDocument,
                                            Award award,
                                            AwardApprovedEquipment approvedEquipmentItem,
                                            MinimumCapitalizationInfo minimumCapitalization) {
        super("Approved equipment item", errorPathPrefix, awardDocument);
        this.award = award;
        this.approvedEquipmentItem = approvedEquipmentItem;
        this.minimumCapitalization = minimumCapitalization; 
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
    public AwardApprovedEquipment getEquipmentItemForValidation() {
        return approvedEquipmentItem;
    }

    /**
     * Gets the institutionMinimumCapitalization attribute. 
     * @return Returns the institutionMinimumCapitalization.
     */
    public MinimumCapitalizationInfo getMinimumCapitalization() {
        return minimumCapitalization;
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging AwardApprovedEquipmentRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AwardApprovedEquipmentRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardApprovedEquipmentRule)rule).processAwardApprovedEquipmentBusinessRules(this);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((approvedEquipmentItem == null) ? 0 : approvedEquipmentItem.hashCode());
        result = PRIME * result + ((minimumCapitalization == null) ? 0 : minimumCapitalization.hashCode());
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
        if (!(obj instanceof AwardApprovedEquipmentRuleEvent)) {
            return false;
        }
        AwardApprovedEquipmentRuleEvent other = (AwardApprovedEquipmentRuleEvent) obj;
        if (approvedEquipmentItem == null) {
            if (other.approvedEquipmentItem != null) {
                return false;
            }
        } else if (!approvedEquipmentItem.equals(other.approvedEquipmentItem)) {
            return false;
        }
        if (minimumCapitalization == null) {
            if (other.minimumCapitalization != null) {
                return false;
            }
        } else if (!minimumCapitalization.equals(other.minimumCapitalization)) {
            return false;
        }
        return true;
    }
}
