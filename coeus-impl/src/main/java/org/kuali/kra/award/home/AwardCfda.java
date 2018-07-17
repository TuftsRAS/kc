/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.kra.award.home;

import org.kuali.kra.award.AwardAssociate;

public class AwardCfda extends AwardAssociate {

    private Long awardCfdaId;
    private Long awardId;
    private String cfdaNumber;
    private String cfdaDescription;

    public Long getAwardCfdaId() {
        return awardCfdaId;
    }

    public void setAwardCfdaId(Long awardCfdaId) {
        this.awardCfdaId = awardCfdaId;
    }

    public Long getAwardId() {
        return awardId;
    }

    public void setAwardId(Long awardId) {
        this.awardId = awardId;
    }

    public String getCfdaNumber() {
        return cfdaNumber;
    }

    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

    public String getCfdaDescription() {
        return cfdaDescription;
    }

    public void setCfdaDescription(String cfdaDescription) {
        this.cfdaDescription = cfdaDescription;
    }

    @Override
    public void resetPersistenceState() {
        awardCfdaId = null;
        versionNumber = null;
    }
}
