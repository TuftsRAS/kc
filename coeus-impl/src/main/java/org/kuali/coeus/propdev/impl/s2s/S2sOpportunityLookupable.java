/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;

import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentConstants;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.krad.lookup.LookupForm;
import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.UrlFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Component("s2sOpportunityLookupable")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class S2sOpportunityLookupable extends LookupableImpl {

    @Autowired
    @Qualifier("documentTypeService")
    private DocumentTypeService documentTypeService;

    @Autowired
    @Qualifier("dateTimeService")
    private DateTimeService dateTimeService;

    @Autowired
    @Qualifier("s2sOpportunityLookupKradKnsHelperService")
    private S2sOpportunityLookupKradKnsHelperService s2sOpportunityLookupKradKnsHelperService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;

    @Override
    public List<?> performSearch(LookupForm form, Map<String, String> searchCriteria, boolean unbounded) {

        final String providerCode = searchCriteria.get(ProposalDevelopmentConstants.S2sConstants.PROVIDER_CODE);
        final String cfdaNumber = searchCriteria.get(ProposalDevelopmentConstants.S2sConstants.CFDA_NUMBER);
        final String opportunityId = searchCriteria.get(ProposalDevelopmentConstants.S2sConstants.OPPORTUNITY_ID);
        final String packageId = searchCriteria.get(ProposalDevelopmentConstants.S2sConstants.PACKAGE_ID);
        final String competitionId = searchCriteria.get(ProposalDevelopmentConstants.S2sConstants.COMPETITION_ID);

        List<S2sOpportunity> opportunities = s2sOpportunityLookupKradKnsHelperService.performSearch(providerCode, cfdaNumber, competitionId, opportunityId, packageId);
        if (CollectionUtils.isEmpty(opportunities)) {
            addNotFoundMessage();
        }

        return opportunities;
    }

    public String buildCreatePropActionHref(S2sOpportunity opportunity) {

        Properties parameters = new Properties();
        parameters.put(KRADConstants.PARAMETER_COMMAND, KewApiConstants.INITIATE_COMMAND);
        parameters.put(UifConstants.UrlParams.VIEW_ID, ProposalDevelopmentConstants.KradConstants.PROP_DEV_INITIATE_VIEW);
        parameters.put(ProposalDevelopmentConstants.S2sConstants.S2S_OPPORTUNITY_CFDAS_SERIALIZED, opportunity.getS2sOpportunityCfdasSerialized());
        parameters.put(ProposalDevelopmentConstants.S2sConstants.OPPORTUNITY_ID, opportunity.getOpportunityId() != null ? opportunity.getOpportunityId() : "");
        parameters.put(ProposalDevelopmentConstants.S2sConstants.OPPORTUNITY_TITLE, opportunity.getOpportunityTitle() != null ? opportunity.getOpportunityTitle() : "");
        parameters.put(ProposalDevelopmentConstants.S2sConstants.OPENING_DATE, opportunity.getOpeningDate() != null ? getDateTimeService().toDateTimeString(opportunity.getOpeningDate().getTime()) : "");
        parameters.put(ProposalDevelopmentConstants.S2sConstants.INSTRUCTION_URL, opportunity.getInstructionUrl() != null ? opportunity.getInstructionUrl() : "");
        parameters.put(ProposalDevelopmentConstants.S2sConstants.COMPETITION_ID, opportunity.getCompetitionId() != null ? opportunity.getCompetitionId() : "");
        parameters.put(ProposalDevelopmentConstants.S2sConstants.PACKAGE_ID, opportunity.getPackageId() != null ? opportunity.getPackageId() : "");
        parameters.put(ProposalDevelopmentConstants.S2sConstants.SCHEMA_URL, opportunity.getSchemaUrl() != null ? opportunity.getSchemaUrl() : "");
        parameters.put(ProposalDevelopmentConstants.S2sConstants.PROVIDER_CODE, opportunity.getProviderCode());
        return UrlFactory.parameterizeUrl(getDocumentTypeService().getDocumentTypeByName("ProposalDevelopmentDocument").getResolvedDocumentHandlerUrl(), parameters);
    }

    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


    public DocumentTypeService getDocumentTypeService() {
        return documentTypeService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    protected void addNotFoundMessage() {
        MessageMap messageMap = globalVariableService.getMessageMap();
        messageMap.putInfoForSectionId(UifConstants.MessageKeys.LOOKUP_RESULT_MESSAGES,
                RiceKeyConstants.INFO_LOOKUP_RESULTS_NONE_FOUND);
    }

    public S2sOpportunityLookupKradKnsHelperService getS2sOpportunityLookupKradKnsHelperService() {
        return s2sOpportunityLookupKradKnsHelperService;
    }

    public void setS2sOpportunityLookupKradKnsHelperService(S2sOpportunityLookupKradKnsHelperService s2sOpportunityLookupKradKnsHelperService) {
        this.s2sOpportunityLookupKradKnsHelperService = s2sOpportunityLookupKradKnsHelperService;
    }

    public GlobalVariableService getGlobalVariableService() {
        return globalVariableService;
    }

    public void setGlobalVariableService(GlobalVariableService globalVariableService) {
        this.globalVariableService = globalVariableService;
    }
}
