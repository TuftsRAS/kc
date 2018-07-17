/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.award.api;

import org.apache.commons.collections4.CollectionUtils;
import org.kuali.coeus.award.dto.*;
import org.kuali.coeus.common.api.document.service.CommonApiService;
import org.kuali.coeus.common.api.rolodex.RolodexContract;
import org.kuali.coeus.common.api.rolodex.RolodexService;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeDocument;
import org.kuali.coeus.common.framework.sponsor.term.SponsorTerm;
import org.kuali.coeus.sys.framework.controller.rest.RestController;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.rest.UnauthorizedAccessException;
import org.kuali.coeus.sys.framework.rest.UnprocessableEntityException;
import org.kuali.kra.award.budget.AwardBudgetExt;
import org.kuali.kra.award.contacts.AwardPerson;
import org.kuali.kra.award.contacts.AwardProjectPersonnelBean;
import org.kuali.kra.award.contacts.AwardSponsorContact;
import org.kuali.kra.award.customdata.AwardCustomData;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardSponsorTerm;
import org.kuali.kra.award.paymentreports.awardreports.AwardReportTerm;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.FeatureFlagConstants;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.stream.Collectors;

public class AwardControllerBase extends RestController {
    @Autowired
    @Qualifier("commonApiService")
    protected CommonApiService commonApiService;
    @Autowired
    @Qualifier("awardApiService")
    private AwardApiService awardApiService;
    @Autowired
    @Qualifier("businessObjectService")
    protected BusinessObjectService businessObjectService;
    @Autowired
    @Qualifier("globalVariableService")
    protected GlobalVariableService globalVariableService;
    @Autowired
    @Qualifier("identityService")
    private IdentityService identityService;
    @Autowired
    @Qualifier("rolodexService")
    private RolodexService rolodexService;
    @Autowired
    @Qualifier("permissionService")
    private PermissionService permissionService;
    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    protected void translateCollections(AwardDto awardDto, AwardDocument awardDocument) {

        final Award award = awardDocument.getAward();
        award.setProjectPersons(new ArrayList<>());
        final List<AwardPersonDto> projectPersons = awardDto.getProjectPersons();
        addPersons(projectPersons, awardDocument);
        addSponsorTerms(award, awardDto);
        addReportTerms(award, awardDto);
        addCustomData(awardDocument, award, awardDto);
        translateSponsorContacts(awardDto, award);
        if(!globalVariableService.getMessageMap().getErrorMessages().isEmpty()) {
            String errors = commonApiService.getValidationErrors();
            throw new UnprocessableEntityException(errors);
        }
    }

    protected void addPersons(List<AwardPersonDto> awardPersonsDto, AwardDocument awardDocument) {
        Award award = awardDocument.getAward();
        if (awardPersonsDto != null) {
            List<AwardPerson> awardPersons = awardPersonsDto.stream().map(awardPersonDto -> {
                AwardPerson awardPerson = commonApiService.convertObject(awardPersonDto, AwardPerson.class);
                validatePerson(awardPerson);
                AwardProjectPersonnelBean awardProjectPersonnelBean = new AwardProjectPersonnelBean(awardDocument);
                awardProjectPersonnelBean.addPersonUnits(awardPerson);
                awardPerson.setAward(award);
                return awardPerson;
            }).collect(Collectors.toList());
            award.setProjectPersons(awardPersons);
            awardDocument.setAward(award);
        }

    }

    public void validatePerson(AwardPerson person) {
        Entity personEntity = null;
        RolodexContract rolodex = null;
        if (person.getPersonId() != null) {
            personEntity = identityService.getEntityByPrincipalId(person.getPersonId());
        } else {
            rolodex = rolodexService.getRolodex(person.getRolodexId());
            if(rolodex != null) {
                person.setRolodexId(rolodex.getRolodexId());
                person.setPersonId(null);
            }
        }

        if (rolodex == null && personEntity == null) {
            throw new UnprocessableEntityException("Invalid person or rolodex for person " + getId(person));
        }
    }

    public void addCustomData(AwardDocument awardDocument, Award award, AwardDto awardDto) {
        List<AwardCustomDataDto> awardCustomDataList = awardDto.getAwardCustomDataList();
        award.setAwardCustomDataList(new ArrayList<>());
        if (awardDto.getAwardCustomDataList() != null) {
            awardCustomDataList.forEach(customDataDto -> {
                String customAttributeId = customDataDto.getCustomAttributeId().toString();
                String customDataValue = customDataDto.getValue();
                Map<String, CustomAttributeDocument> customAttributeDocuments = awardDocument.getCustomAttributeDocuments();
                List<AwardCustomData> customDataList = customAttributeDocuments.entrySet().stream().
                        filter(entry -> {
                                    CustomAttributeDocument customAttributeDoc = entry.getValue();
                                    return customAttributeId.equalsIgnoreCase(customAttributeDoc.getCustomAttribute().getId().toString());
                                }
                        ).map(entry -> {
                    CustomAttributeDocument customAttributeDoc = entry.getValue();
                    AwardCustomData customData = new AwardCustomData();
                    customData.setCustomAttributeId(customAttributeDoc.getId());
                    customData.setCustomAttribute(customAttributeDoc.getCustomAttribute());
                    customData.setValue(customDataValue);
                    customData.setAward(award);
                    return customData;
                }).collect(Collectors.toList());
                award.getAwardCustomDataList().addAll(customDataList);
            });
        }
    }

    protected void addSponsorTerms(Award award, AwardDto awardDto) {
        List<AwardSponsorTermDto> sponsorTermsDtos = awardDto.getAwardSponsorTerms();
        award.setAwardSponsorTerms(new ArrayList<>());
        if (sponsorTermsDtos != null) {
            List<AwardSponsorTerm> sponsorTerms = sponsorTermsDtos.stream().map(awardSponsorTermDto ->
                    getAwardSponsorTerm(awardSponsorTermDto.getSponsorTermId(), award)
            ).collect(Collectors.toList());
            award.getAwardSponsorTerms().addAll(sponsorTerms);
        }
    }

    protected AwardSponsorTerm getAwardSponsorTerm(Long sponsorTermId, Award award) {
        SponsorTerm sponsorTerm = getSponsorTerm(sponsorTermId);
        if (sponsorTerm == null) {
            throw new UnprocessableEntityException("Sponsor term " + sponsorTermId + " cannot be found.");
        }
        AwardSponsorTerm newAwardSponsorTerm = new AwardSponsorTerm(sponsorTermId, sponsorTerm);
        newAwardSponsorTerm.setAward(award);
        return newAwardSponsorTerm;
    }

    protected void addReportTerms(Award award, AwardDto awardDto) {
        List<AwardReportTermDto> awardReportTermDtos = awardDto.getAwardReportTerms();
        award.setAwardReportTermItems(new ArrayList<>());
        if(awardReportTermDtos != null) {
            List<AwardReportTerm> awardReportTerms = awardReportTermDtos.stream().map(awardReportTermDto -> {
                        AwardReportTerm awardReportTerm = commonApiService.convertObject(awardReportTermDto, AwardReportTerm.class);
                        awardReportTerm.setAward(award);
                        return awardReportTerm;
                    }
            ).collect(Collectors.toList());
            award.getAwardReportTermItems().addAll(awardReportTerms);
        }
    }

    protected SponsorTerm getSponsorTerm(Long sponsorTermId) {
        return businessObjectService.findBySinglePrimaryKey(SponsorTerm.class, sponsorTermId.toString());
    }

    public void translateSponsorContacts(AwardDto awardDto, Award award) {
        if(CollectionUtils.isNotEmpty(awardDto.getAwardSponsorContacts())) {
            award.setSponsorContacts(awardDto.getAwardSponsorContacts().stream().map(awardSponsorContactDto -> {
                        AwardSponsorContact awardSponsorContact = commonApiService.convertObject(awardSponsorContactDto, AwardSponsorContact.class);
                        awardSponsorContact.setAwardNumber(award.getAwardNumber());
                        awardSponsorContact.setSequenceNumber(award.getSequenceNumber());
                        return awardSponsorContact;
                    }
            ).collect(Collectors.toList()));
        }
    }

    protected String getId(AwardPerson person) {
        return person.getPersonId() != null ? person.getPersonId() : person.getRolodexId().toString();
    }

    protected void changeDates(Award award, AwardDto awardDto) {
        award.getAwardAmountInfo().setCurrentFundEffectiveDate(awardDto.getObligationStartDate());
        award.getAwardAmountInfo().setObligationExpirationDate(awardDto.getObligationEndDate());
        award.getAwardAmountInfo().setFinalExpirationDate(awardDto.getProjectEndDate());
        award.setAwardEffectiveDate(awardDto.getAwardEffectiveDate());
    }

    public boolean isApiAuthEnabled() {
        return parameterService.getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_GEN, ParameterConstants.DOCUMENT_COMPONENT,
                FeatureFlagConstants.ENABLE_API_AUTHORIZATION);
    }

    protected void assertUserHasBudgetReadAccess() {
        if (isApiAuthEnabled()) {
            if (globalVariableService.getUserSession() == null || !permissionService.hasPermissionByTemplate(globalVariableService.getUserSession().getPrincipalId(),
                    Constants.MODULE_NAMESPACE_SYSTEM, PermissionConstants.READ_CLASS, Collections.singletonMap(KcKimAttributes.CLASS_NAME, AwardBudgetExt.class.getName()))) {
                throw new UnauthorizedAccessException();
            }
        }
    }

    protected void assertUserHasAwardPersonReadAccess() {
        if (isApiAuthEnabled()) {
            if (globalVariableService.getUserSession() == null || !permissionService.hasPermissionByTemplate(globalVariableService.getUserSession().getPrincipalId(),
                    Constants.MODULE_NAMESPACE_SYSTEM, PermissionConstants.READ_CLASS, Collections.singletonMap(KcKimAttributes.CLASS_NAME, AwardPerson.class.getName()))) {
                throw new UnauthorizedAccessException();
            }
        }
    }

    protected void assertUserHasAwardPersonWriteAccess() {
        if (isApiAuthEnabled()) {
            if (globalVariableService.getUserSession() == null || !permissionService.hasPermissionByTemplate(globalVariableService.getUserSession().getPrincipalId(),
                    Constants.MODULE_NAMESPACE_SYSTEM, PermissionConstants.WRITE_CLASS, Collections.singletonMap(KcKimAttributes.CLASS_NAME, AwardPerson.class.getName()))) {
                throw new UnauthorizedAccessException();
            }
        }
    }

    protected void assertUserHasReadAccess() {
        if (isApiAuthEnabled()) {
            if (globalVariableService.getUserSession() == null || !permissionService.hasPermissionByTemplate(globalVariableService.getUserSession().getPrincipalId(),
                    Constants.MODULE_NAMESPACE_SYSTEM, PermissionConstants.READ_CLASS, Collections.singletonMap(KcKimAttributes.CLASS_NAME, Award.class.getName()))) {
                throw new UnauthorizedAccessException();
            }
        }
    }

    protected void assertUserHasWriteAccess() {
        if (isApiAuthEnabled()) {
            if (globalVariableService.getUserSession() == null || !permissionService.hasPermissionByTemplate(globalVariableService.getUserSession().getPrincipalId(),
                    Constants.MODULE_NAMESPACE_SYSTEM, PermissionConstants.WRITE_CLASS, Collections.singletonMap(KcKimAttributes.CLASS_NAME, Award.class.getName()))) {
                throw new UnauthorizedAccessException();
            }
        }
    }

    public CommonApiService getCommonApiService() {
        return commonApiService;
    }

    public void setCommonApiService(CommonApiService commonApiService) {
        this.commonApiService = commonApiService;
    }

    public AwardApiService getAwardApiService() {
        return awardApiService;
    }

    public void setAwardApiService(AwardApiService awardApiService) {
        this.awardApiService = awardApiService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public GlobalVariableService getGlobalVariableService() {
        return globalVariableService;
    }

    public void setGlobalVariableService(GlobalVariableService globalVariableService) {
        this.globalVariableService = globalVariableService;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public RolodexService getRolodexService() {
        return rolodexService;
    }

    public void setRolodexService(RolodexService rolodexService) {
        this.rolodexService = rolodexService;
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
