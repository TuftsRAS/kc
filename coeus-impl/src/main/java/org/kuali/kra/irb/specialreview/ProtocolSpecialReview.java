/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.specialreview;

import org.kuali.kra.irb.Protocol;
import org.kuali.kra.protocol.specialreview.ProtocolSpecialReviewBase;
import org.kuali.kra.protocol.specialreview.ProtocolSpecialReviewExemption;

/**
 * Defines a Special Review for a Protocol.
 */
public class ProtocolSpecialReview extends ProtocolSpecialReviewBase  {

    private static final long serialVersionUID = -9010537404528653558L;

    @Override
    public ProtocolSpecialReviewExemption createSpecialReviewExemption(String exemptionTypeCode) {
        org.kuali.kra.irb.specialreview.ProtocolSpecialReviewExemption protocolSpecialReviewExemption = new org.kuali.kra.irb.specialreview.ProtocolSpecialReviewExemption();
        protocolSpecialReviewExemption.setExemptionTypeCode(exemptionTypeCode);
        protocolSpecialReviewExemption.setProtocolSpecialReview(this);
        return protocolSpecialReviewExemption;
    }

    @Override
    public Protocol getSequenceOwner() {
        return (Protocol) super.getSequenceOwner();
    }

    @Override
    public void resetPersistenceState() {
        super.resetPersistenceState();
        for (ProtocolSpecialReviewExemption exemption : getSpecialReviewExemptions()) {
            exemption.setProtocolSpecialReviewExemptionId(null);
            exemption.setProtocolSpecialReviewId(null);
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((getProtocolId() == null) ? 0 : getProtocolId().hashCode());
        result = prime * result + ((getProtocolSpecialReviewId() == null) ? 0 : getProtocolSpecialReviewId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ProtocolSpecialReview other = (ProtocolSpecialReview) obj;
        if (getProtocolId() == null) {
            if (other.getProtocolId() != null) {
                return false;
            }
        } else if (!getProtocolId().equals(other.getProtocolId())) {
            return false;
        }
        if (getProtocolSpecialReviewId() == null) {
            if (other.getProtocolSpecialReviewId() != null) {
                return false;
            }
        } else if (!getProtocolSpecialReviewId().equals(other.getProtocolSpecialReviewId())) {
            return false;
        }
        return true;
    }
}
