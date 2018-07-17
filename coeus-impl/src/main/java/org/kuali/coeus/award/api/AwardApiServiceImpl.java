/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.award.api;

import org.kuali.coeus.award.dto.AwardDto;
import org.kuali.coeus.common.api.document.service.CommonApiService;
import org.kuali.kra.award.contacts.AwardSponsorContact;
import org.kuali.kra.award.home.Award;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("awardApiService")
public class AwardApiServiceImpl implements AwardApiService {

    @Autowired
    @Qualifier("commonApiService")
    private CommonApiService commonApiService;

    @Override
    public AwardDto convertAwardToDto(Award award) {
        AwardDto awardDto = commonApiService.convertObject(award, AwardDto.class);
        awardDto.getAwardSponsorContacts().forEach(contact -> {
            AwardSponsorContact awardContactFound = award.getSponsorContacts().stream().filter(
                    awardcontact -> contact.getAwardContactId().compareTo(awardcontact.getAwardContactId()) == 0).findFirst().orElse(null);
            contact.setOrgName(awardContactFound != null ? awardContactFound.getContactOrganizationName() : "");
        });

        return awardDto;
    }

    public CommonApiService getCommonApiService() {
        return commonApiService;
    }

    public void setCommonApiService(CommonApiService commonApiService) {
        this.commonApiService = commonApiService;
    }
}
