/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.subawardReporting.printing.xmlstream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.kuali.coeus.common.framework.module.CoeusModule;
import org.kuali.coeus.common.framework.org.Organization;
import org.kuali.coeus.common.framework.person.KcPerson;
import org.kuali.coeus.common.framework.print.stream.xml.XmlStream;
import org.kuali.coeus.common.framework.rolodex.NonOrganizationalRolodex;
import org.kuali.coeus.common.framework.rolodex.Rolodex;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.kra.award.contacts.AwardPerson;
import org.kuali.kra.award.home.ContactUsage;
import org.kuali.kra.award.printing.schema.AwardHeaderType;
import org.kuali.kra.award.printing.schema.AwardType;
import org.kuali.kra.award.printing.schema.AwardType.AwardDetails;
import org.kuali.kra.award.printing.schema.AwardType.AwardDetails.OtherHeaderDetails;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.subaward.bo.*;
import org.kuali.kra.subaward.lookup.keyvalue.SubawardTemplateContactFinder;
import org.kuali.kra.subaward.printing.schema.OrganizationType;
import org.kuali.kra.subaward.printing.schema.PersonDetailsType;
import org.kuali.kra.subaward.printing.schema.RolodexDetailsType;
import org.kuali.kra.subaward.printing.schema.SubContractDataDocument;
import org.kuali.kra.subaward.printing.schema.SubContractDataDocument.SubContractData;
import org.kuali.kra.subaward.printing.schema.SubContractDataDocument.SubContractData.*;
import org.kuali.kra.subaward.reporting.printing.SubAwardPrintType;
import org.kuali.kra.subaward.reporting.printing.service.SubAwardPrintingService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;

import java.math.BigDecimal;
import java.util.*;

public class SubAwardFDPPrintXmlStream implements XmlStream  {
    private static final String ORGANIZATION_ID = "organizationId";
    private static final String FDP_ORG_FROM_REQUISITIONER_UNIT = "FDP_ORG_FROM_REQUISITIONER_UNIT";
    public static final String CONTACT_TYPE_CODE = "contactTypeCode";
    public static final String AWARD_NUMBER = "awardNumber";
    public static final String ROLE_CODE = "roleCode";
    public static final String PRINCIPAL_INVESTIGATOR = "PI";
    public static final String SEQUENCE_NUMBER = "sequenceNumber";

    public static final String AWARD_TITLE = "awardTitle";
    public static final String SPONSOR_AWARD_NUMBER = "sponsorAwardNumber";
    public static final String SPONSOR_NAME = "sponsorName";
    public static final String CFDA_NUMBER = "cfdaNumber";
    public static final String MODIFICATION_TYPE = "modificationType";
    public static final String FCOI = "fcoi";
    public static final String FAIN = "fain";
    public static final String FFATA = "ffata";
    public static final String PRIME_SPONSOR_NAME = "primeSponsorName";
    public static final String NOTICE_DATE = "noticeDate";
    public static final String FED_AWARD_DATE = "fedAwardDate";
    public static final String OBLIGATED_TOTAL = "obligatedTotal";
    public static final String ANTICIPATED_TOTAL = "anticipatedTotal";
    public static final String INVOICES_EMAILED = "invoicesEmailed";
    public static final String INVOICE_ADDRESS_DIFFERENT = "invoiceAddressDifferent";
    public static final String INVOICE_EMAIL_DIFFERENT = "invoiceEmailDifferent";
    public static final String FDP_PTE_INVOICE_EMAIL = "FDP_PTE_Invoice_email";
    public static final String FDP_PTE_INVOICE_ADDRESS = "FDP_PTE_Invoice_address";
    private static final String FDP_NIH_POLICY = "FDP_NIH_Policy";
    private static final String FDP_NIH_CFR = "FDP_NIH_CFR";
    private static final String FDP_NIH_GRANTS_POLICY_STATEMENT = "FDP_NIH_Grants_Policy_Statement";
    private static final String FDP_NIH_INTERIM_RESEARCH_TERMS = "FDP_NIH_Interim_Research_Terms";
    private static final String FDP_NIH_AGENCY_REQUIREMENTS = "FDP_NIH_Agency_Requirements";
    private static final String FDP_NIH_FCOI_GUIDANCE = "FDP_NIH_FCOI_Guidance";
    private static final String FDP_NSF_POLICY = "FDP_NSF_Policy";
    private static final String FDP_NSF_CFR = "FDP_NSF_CFR";
    private static final String FDP_NSF_GRANTS_POLICY_STATEMENT = "FDP_NSF_Grants_Policy_Statement";
    private static final String FDP_NSF_INTERIM_RESEARCH_TERMS = "FDP_NSF_Interim_Research_Terms";
    private static final String FDP_NSF_AGENCY_REQUIREMENTS = "FDP_NSF_Agency_Requirements";
    private static final String FDP_NSF_FCOI_GUIDANCE = "FDP_NSF_FCOI_Guidance";
    private static final String FDP_NASA_POLICY = "FDP_NASA_Policy";
    private static final String FDP_NASA_CFR = "FDP_NASA_CFR";
    private static final String FDP_NASA_GRANTS_POLICY_STATEMENT = "FDP_NASA_Grants_Policy_Statement";
    private static final String FDP_NASA_INTERIM_RESEARCH_TERMS = "FDP_NASA_Interim_Research_Terms";
    private static final String FDP_NASA_AGENCY_REQUIREMENTS = "FDP_NASA_Agency_Requirements";
    private static final String FDP_NASA_FCOI_GUIDANCE = "FDP_NASA_FCOI_Guidance";
    private static final String FDP_ONR_POLICY = "FDP_ONR_Policy";
    private static final String FDP_ONR_CFR = "FDP_ONR_CFR";
    private static final String FDP_ONR_GRANTS_POLICY_STATEMENT = "FDP_ONR_Grants_Policy_Statement";
    private static final String FDP_ONR_INTERIM_RESEARCH_TERMS = "FDP_ONR_Interim_Research_Terms";
    private static final String FDP_ONR_AGENCY_REQUIREMENTS = "FDP_ONR_Agency_Requirements";
    private static final String FDP_ONR_FCOI_GUIDANCE = "FDP_ONR_FCOI_Guidance";
    private static final String FDP_ARO_POLICY = "FDP_ARO_Policy";
    private static final String FDP_ARO_CFR = "FDP_ARO_CFR";
    private static final String FDP_ARO_GRANTS_POLICY_STATEMENT = "FDP_ARO_Grants_Policy_Statement";
    private static final String FDP_ARO_INTERIM_RESEARCH_TERMS = "FDP_ARO_Interim_Research_Terms";
    private static final String FDP_ARO_AGENCY_REQUIREMENTS = "FDP_ARO_Agency_Requirements";
    private static final String FDP_ARO_FCOI_GUIDANCE = "FDP_ARO_FCOI_Guidance";
    private static final String FDP_AFOSR_POLICY = "FDP_AFOSR_Policy";
    private static final String FDP_AFOSR_CFR = "FDP_AFOSR_CFR";
    private static final String FDP_AFOSR_GRANTS_POLICY_STATEMENT = "FDP_AFOSR_Grants_Policy_Statement";
    private static final String FDP_AFOSR_INTERIM_RESEARCH_TERMS = "FDP_AFOSR_Interim_Research_Terms";
    private static final String FDP_AFOSR_AGENCY_REQUIREMENTS = "FDP_AFOSR_Agency_Requirements";
    private static final String FDP_AFOSR_FCOI_GUIDANCE = "FDP_AFOSR_FCOI_Guidance";
    private static final String FDP_EPA_POLICY = "FDP_EPA_Policy";
    private static final String FDP_EPA_CFR = "FDP_EPA_CFR";
    private static final String FDP_EPA_GRANTS_POLICY_STATEMENT = "FDP_EPA_Grants_Policy_Statement";
    private static final String FDP_EPA_INTERIM_RESEARCH_TERMS = "FDP_EPA_Interim_Research_Terms";
    private static final String FDP_EPA_AGENCY_REQUIREMENTS = "FDP_EPA_Agency_Requirements";
    private static final String FDP_EPA_FCOI_GUIDANCE = "FDP_EPA_FCOI_Guidance";
    private static final String FDP_AMRMC_POLICY = "FDP_AMRAA_Policy";
    private static final String FDP_AMRMC_CFR = "FDP_AMRAA_CFR";
    private static final String FDP_AMRMC_GRANTS_POLICY_STATEMENT = "FDP_AMRMC_Grants_Policy_Statement";
    private static final String FDP_AMRMC_INTERIM_RESEARCH_TERMS = "FDP_AMRMC_Interim_Research_Terms";
    private static final String FDP_AMRMC_AGENCY_REQUIREMENTS = "FDP_AMRMC_Agency_Requirements";
    private static final String FDP_AMRMC_FCOI_GUIDANCE = "FDP_AMRMC_FCOI_Guidance";
    private static final String FDP_AMRAA_POLICY = "FDP_AMRMC_Policy";
    private static final String FDP_AMRAA_CFR = "FDP_AMRMC_CFR";
    private static final String FDP_AMRAA_GRANTS_POLICY_STATEMENT = "FDP_AMRAA_Grants_Policy_Statement";
    private static final String FDP_AMRAA_INTERIM_RESEARCH_TERMS = "FDP_AMRAA_Interim_Research_Terms";
    private static final String FDP_AMRAA_AGENCY_REQUIREMENTS = "FDP_AMRAA_Agency_Requirements";
    private static final String FDP_AMRAA_FCOI_GUIDANCE = "FDP_AMRAA_FCOI_Guidance";
    private static final String FDP_USDA_POLICY = "FDP_USDA_Policy";
    private static final String FDP_USDA_CFR = "FDP_USDA_CFR";
    private static final String FDP_USDA_GRANTS_POLICY_STATEMENT = "FDP_USDA_Grants_Policy_Statement";
    private static final String FDP_USDA_INTERIM_RESEARCH_TERMS = "FDP_USDA_Interim_Research_Terms";
    private static final String FDP_USDA_AGENCY_REQUIREMENTS = "FDP_USDA_Agency_Requirements";
    private static final String FDP_USDA_FCOI_GUIDANCE = "FDP_USDA_FCOI_Guidance";

    private static final String FDP_SUBAWARD_BILATERAL_AGREEMENTS = "FDP_Subaward_Bilateral_Agreements";
    private static final String TYPE_CODE = "typeCode";

    private BusinessObjectService businessObjectService;
    private CountryService countryService;
    private StateService stateService;
    private ConfigurationService configurationService;

    private String awardNumber;
    private String awardTitle;
    private String sponsorAwardNumber;
    private String sponsorName;
    private String cfdaNumber;
    private String fain;
    private String modificationType;
    private Boolean fcoi;
    private Boolean ffata;
    private Boolean invoicesEmailed;
    private Boolean invoiceAddressDifferent;
    private Boolean invoiceEmailDifferent;
    private String primeSponsorName;
    private Calendar noticeDate;
    private Calendar fedAwardDate;
    private BigDecimal obligatedTotal;
    private BigDecimal anticipatedTotal;
    private List<SubAwardForms> sponsorTemplates;
    private ParameterService parameterService;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, XmlObject> generateXmlStream(KcPersistableBusinessObjectBase printableBusinessObject,
                                                    Map<String, Object> reportParameters) {

        this.awardNumber=(String) reportParameters.get(AWARD_NUMBER);
        this.awardTitle=(String) reportParameters.get(AWARD_TITLE);
        this.sponsorAwardNumber=(String) reportParameters.get(SPONSOR_AWARD_NUMBER);
        this.sponsorName=(String) reportParameters.get(SPONSOR_NAME);
        this.cfdaNumber=(String) reportParameters.get(CFDA_NUMBER);
        this.sponsorTemplates = (List<SubAwardForms>) reportParameters.get(SubAwardPrintingService.SELECTED_TEMPLATES);
        this.modificationType = (String) reportParameters.get(MODIFICATION_TYPE);
        this.fcoi = (Boolean) reportParameters.get(FCOI);
        this.fain = (String) reportParameters.get(FAIN);
        this.ffata = (Boolean) reportParameters.get(FFATA);
        this.invoicesEmailed = (Boolean) reportParameters.get(INVOICES_EMAILED);
        this.invoiceAddressDifferent = (Boolean) reportParameters.get(INVOICE_ADDRESS_DIFFERENT);
        this.invoiceEmailDifferent = (Boolean) reportParameters.get(INVOICE_EMAIL_DIFFERENT);
        this.primeSponsorName = (String) reportParameters.get(PRIME_SPONSOR_NAME);
        this.noticeDate = (Calendar) reportParameters.get(NOTICE_DATE);
        this.fedAwardDate = (Calendar) reportParameters.get(FED_AWARD_DATE);
        this.obligatedTotal = (BigDecimal) reportParameters.get(OBLIGATED_TOTAL);
        this.anticipatedTotal = (BigDecimal) reportParameters.get(ANTICIPATED_TOTAL);

        final SubAward subaward=(SubAward) printableBusinessObject;
        final SubContractData subContractData = SubContractData.Factory.newInstance();

        setOtherConfigInfo(subContractData);

        setSubcontractTemplateInfo(subContractData,subaward);
        setSubcontractDetail(subContractData,subaward);
        setSubcontractAmountInfo(subContractData,subaward);
        setAwardHeader(subContractData);
        setPrimeRecipientContacts(subContractData,subaward);
        setPrintRequirement(subContractData);
        setPrimeAdministrativeContact(subContractData,subaward);
        setPrimePrincipalInvestigator(subContractData);
        setPrimeFinancialContact(subContractData,subaward);
        setPrimeAuthorizedOfficial(subContractData,subaward);
        setAdministrativeContact(subContractData,subaward);
        setFinancialContact(subContractData,subaward);
        setAuthorizedOfficial(subContractData,subaward);
        setSubcontractReports(subContractData,subaward);

        final SubContractDataDocument subContractDataDoc = SubContractDataDocument.Factory.newInstance();
        subContractDataDoc.setSubContractData(subContractData);


        final Map<String, XmlObject> xmlObjectList = new LinkedHashMap<>();
        xmlObjectList.put(SubAwardPrintType.SUB_AWARD_FDP_TEMPLATE.getSubAwardPrintType(), subContractDataDoc);
        return xmlObjectList;
    }

    public void setOtherConfigInfo(SubContractData subContractData) {
        final OtherConfigInfo otherConfigInfo = OtherConfigInfo.Factory.newInstance();
        final List<OtherConfigInfo> otherConfigInfoList = new ArrayList<>();

        otherConfigInfo.setFdpNihPolicy(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NIH_POLICY));
        otherConfigInfo.setFdpNihCfr(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NIH_CFR));
        otherConfigInfo.setFdpNihGrantsPolicyStatement(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NIH_GRANTS_POLICY_STATEMENT));
        otherConfigInfo.setFdpNihInterimResearchTerms(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NIH_INTERIM_RESEARCH_TERMS));
        otherConfigInfo.setFdpNihAgencyRequirements(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NIH_AGENCY_REQUIREMENTS));
        otherConfigInfo.setFdpNihFCoiGuidance(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NIH_FCOI_GUIDANCE));

        otherConfigInfo.setFdpNsfPolicy(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NSF_POLICY));
        otherConfigInfo.setFdpNsfCfr(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NSF_CFR));
        otherConfigInfo.setFdpNsfGrantsPolicyStatement(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NSF_GRANTS_POLICY_STATEMENT));
        otherConfigInfo.setFdpNsfInterimResearchTerms(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NSF_INTERIM_RESEARCH_TERMS));
        otherConfigInfo.setFdpNsfAgencyRequirements(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NSF_AGENCY_REQUIREMENTS));
        otherConfigInfo.setFdpNsfFCoiGuidance(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NSF_FCOI_GUIDANCE));

        otherConfigInfo.setFdpNasaPolicy(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NASA_POLICY));
        otherConfigInfo.setFdpNasaCfr(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NASA_CFR));
        otherConfigInfo.setFdpNasaGrantsPolicyStatement(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NASA_GRANTS_POLICY_STATEMENT));
        otherConfigInfo.setFdpNasaInterimResearchTerms(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NASA_INTERIM_RESEARCH_TERMS));
        otherConfigInfo.setFdpNasaAgencyRequirements(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NASA_AGENCY_REQUIREMENTS));
        otherConfigInfo.setFdpNasaFCoiGuidance(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_NASA_FCOI_GUIDANCE));

        otherConfigInfo.setFdpOnrPolicy(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ONR_POLICY));
        otherConfigInfo.setFdpOnrCfr(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ONR_CFR));
        otherConfigInfo.setFdpOnrGrantsPolicyStatement(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ONR_GRANTS_POLICY_STATEMENT));
        otherConfigInfo.setFdpOnrInterimResearchTerms(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ONR_INTERIM_RESEARCH_TERMS));
        otherConfigInfo.setFdpOnrAgencyRequirements(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ONR_AGENCY_REQUIREMENTS));
        otherConfigInfo.setFdpOnrFCoiGuidance(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ONR_FCOI_GUIDANCE));

        otherConfigInfo.setFdpAroPolicy(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ARO_POLICY));
        otherConfigInfo.setFdpAroCfr(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ARO_CFR));
        otherConfigInfo.setFdpAroGrantsPolicyStatement(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ARO_GRANTS_POLICY_STATEMENT));
        otherConfigInfo.setFdpAroInterimResearchTerms(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ARO_INTERIM_RESEARCH_TERMS));
        otherConfigInfo.setFdpAroAgencyRequirements(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ARO_AGENCY_REQUIREMENTS));
        otherConfigInfo.setFdpAroFCoiGuidance(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_ARO_FCOI_GUIDANCE));

        otherConfigInfo.setFdpAfosrPolicy(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AFOSR_POLICY));
        otherConfigInfo.setFdpAfosrCfr(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AFOSR_CFR));
        otherConfigInfo.setFdpAfosrGrantsPolicyStatement(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AFOSR_GRANTS_POLICY_STATEMENT));
        otherConfigInfo.setFdpAfosrInterimResearchTerms(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AFOSR_INTERIM_RESEARCH_TERMS));
        otherConfigInfo.setFdpAfosrAgencyRequirements(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AFOSR_AGENCY_REQUIREMENTS));
        otherConfigInfo.setFdpAfosrFCoiGuidance(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AFOSR_FCOI_GUIDANCE));

        otherConfigInfo.setFdpEpaPolicy(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_EPA_POLICY));
        otherConfigInfo.setFdpEpaCfr(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_EPA_CFR));
        otherConfigInfo.setFdpEpaGrantsPolicyStatement(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_EPA_GRANTS_POLICY_STATEMENT));
        otherConfigInfo.setFdpEpaInterimResearchTerms(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_EPA_INTERIM_RESEARCH_TERMS));
        otherConfigInfo.setFdpEpaAgencyRequirements(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_EPA_AGENCY_REQUIREMENTS));
        otherConfigInfo.setFdpEpaFCoiGuidance(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_EPA_FCOI_GUIDANCE));

        otherConfigInfo.setFdpAmrmcPolicy(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRMC_POLICY));
        otherConfigInfo.setFdpAmrmcCfr(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRMC_CFR));
        otherConfigInfo.setFdpAmrmcGrantsPolicyStatement(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRMC_GRANTS_POLICY_STATEMENT));
        otherConfigInfo.setFdpAmrmcInterimResearchTerms(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRMC_INTERIM_RESEARCH_TERMS));
        otherConfigInfo.setFdpAmrmcAgencyRequirements(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRMC_AGENCY_REQUIREMENTS));
        otherConfigInfo.setFdpAmrmcFCoiGuidance(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRMC_FCOI_GUIDANCE));

        otherConfigInfo.setFdpAmraaPolicy(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRAA_POLICY));
        otherConfigInfo.setFdpAmraaCfr(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRAA_CFR));
        otherConfigInfo.setFdpAmraaGrantsPolicyStatement(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRAA_GRANTS_POLICY_STATEMENT));
        otherConfigInfo.setFdpAmraaInterimResearchTerms(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRAA_INTERIM_RESEARCH_TERMS));
        otherConfigInfo.setFdpAmraaAgencyRequirements(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRAA_AGENCY_REQUIREMENTS));
        otherConfigInfo.setFdpAmraaFCoiGuidance(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_AMRAA_FCOI_GUIDANCE));

        otherConfigInfo.setFdpUsdaPolicy(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_USDA_POLICY));
        otherConfigInfo.setFdpUsdaCfr(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_USDA_CFR));
        otherConfigInfo.setFdpUsdaGrantsPolicyStatement(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_USDA_GRANTS_POLICY_STATEMENT));
        otherConfigInfo.setFdpUsdaInterimResearchTerms(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_USDA_INTERIM_RESEARCH_TERMS));
        otherConfigInfo.setFdpUsdaAgencyRequirements(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_USDA_AGENCY_REQUIREMENTS));
        otherConfigInfo.setFdpUsdaFCoiGuidance(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_USDA_FCOI_GUIDANCE));

        otherConfigInfo.setFdpSubawardBilateralAgreements(getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_SUBAWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                FDP_SUBAWARD_BILATERAL_AGREEMENTS));

        otherConfigInfoList.add(otherConfigInfo);
        subContractData.setOtherConfigInfoArray(otherConfigInfoList.toArray(new OtherConfigInfo[0]));
    }

    public void setSubcontractTemplateInfo(SubContractData subContractData, SubAward subaward) {
        SubcontractTemplateInfo subContractTemplateInfo = SubcontractTemplateInfo.Factory.newInstance();
        List<SubcontractTemplateInfo> templateDataList = new ArrayList<>();

        SubAwardTemplateInfo subawardTemplate;
        if(subaward.getSubAwardTemplateInfo() != null && !subaward.getSubAwardTemplateInfo().isEmpty()) {
            subawardTemplate = subaward.getSubAwardTemplateInfo().get(0);

            if(subawardTemplate.getInvoiceOrPaymentContact()!= null) {
                subContractTemplateInfo.setInvoiceOrPaymentContactCd(subawardTemplate.getInvoiceOrPaymentContact().toString());
            }

            if (subawardTemplate.getIrbIacucContact() != null) {
                subContractTemplateInfo.setIrbIacucContactCd(subawardTemplate.getIrbIacucContact().toString());
            }

            if(subawardTemplate.getFinalStmtOfCostscontact()!=null) {
                subContractTemplateInfo.setFinalStmtOfCostsContactCd(subawardTemplate.getFinalStmtOfCostscontact().toString());
            }

            if(subawardTemplate.getChangeRequestsContact()!= null) {
                subContractTemplateInfo.setChangeRequestsContactCd(subawardTemplate.getChangeRequestsContact().toString());
            }

            if(subawardTemplate.getTerminationContact()!= null ) {
                subContractTemplateInfo.setTerminationContactCd(subawardTemplate.getTerminationContact().toString());
            }

            if(subawardTemplate.getNoCostExtensionContact() != null) {
                subContractTemplateInfo.setNoCostExtensionContactCd(subawardTemplate.getNoCostExtensionContact().toString());
            }

            if(subawardTemplate.getCopyRightType() != null) {
                subContractTemplateInfo.setCopyRights(subawardTemplate.getCopyRightType().toString());
            }
            if(subawardTemplate.getAutomaticCarryForward() != null) {
                subContractTemplateInfo.setAutomaticCarryForward(subawardTemplate.getAutomaticCarryForward());
            }
            if(subawardTemplate.getTreatmentPrgmIncomeAdditive() != null) {
                subContractTemplateInfo.setTreatmentPrgmIncomeAdditive(subawardTemplate.getTreatmentPrgmIncomeAdditive());
            }

            subContractTemplateInfo.setRAndD(toFlag(subawardTemplate.getrAndD()));

            if (subawardTemplate.getFcioSubrecPolicyCd() != null) {
                subContractTemplateInfo.setFcioSubrecPolicyCd(subawardTemplate.getFcioSubrecPolicyCd());
            }

            subContractTemplateInfo.setAnimalFlag(toFlag(subawardTemplate.getAnimalFlag()));

            if (subawardTemplate.getAnimalPteSendCd() != null) {
                subContractTemplateInfo.setAnimalPteSendCd(subawardTemplate.getAnimalPteSendCd());
            }

            if (subawardTemplate.getAnimalPteNrCd() != null) {
                subContractTemplateInfo.setAnimalPteNrCd(subawardTemplate.getAnimalPteNrCd());
            }

            subContractTemplateInfo.setHumanFlag(toFlag(subawardTemplate.getHumanFlag()));

            if (subawardTemplate.getHumanPteSendCd() != null) {
                subContractTemplateInfo.setHumanPteSendCd(subawardTemplate.getHumanPteSendCd());
            }

            if (subawardTemplate.getHumanPteNrCd() != null) {
                subContractTemplateInfo.setHumanPteNrCd(subawardTemplate.getHumanPteNrCd());
            }

            if (subawardTemplate.getHumanDataExchangeAgreeCd() != null) {
                subContractTemplateInfo.setHumanDataExchangeAgreeCd(subawardTemplate.getHumanDataExchangeAgreeCd());
            }

            if (subawardTemplate.getHumanDataExchangeTermsCd() != null) {
                subContractTemplateInfo.setHumanDataExchangeTermsCd(subawardTemplate.getHumanDataExchangeTermsCd());
            }

            subContractTemplateInfo.setMpiAward(toFlag(subawardTemplate.getMpiAward()));

            if (subawardTemplate.getMpiLeadershipPlan() != null) {
                subContractTemplateInfo.setMpiLeadershipPlan(subawardTemplate.getMpiLeadershipPlan());
            }

            if (subawardTemplate.getAdditionalTerms() != null) {
                subContractTemplateInfo.setAdditionalTerms(subawardTemplate.getAdditionalTerms());
            }

            if (subawardTemplate.getTreatmentOfIncome() != null) {
                subContractTemplateInfo.setTreatmentOfIncome(subawardTemplate.getTreatmentOfIncome());
            }

            if (subawardTemplate.getDataSharingAttachment() != null) {
                subContractTemplateInfo.setDataSharingAttachment(subawardTemplate.getDataSharingAttachment());
            }

            if (subawardTemplate.getDataSharingCd() != null) {
                subContractTemplateInfo.setDataSharingCd(subawardTemplate.getDataSharingCd());
            }

            if (subawardTemplate.getFinalStatementDueCd() != null) {
                subContractTemplateInfo.setFinalStatementDueCd(subawardTemplate.getFinalStatementDueCd());
            }

            templateDataList.add(subContractTemplateInfo);
        }
        subContractData.setSubcontractTemplateInfoArray(templateDataList.toArray(new SubcontractTemplateInfo[0]));
    }

    public void setSubcontractDetail(SubContractData subContractData, SubAward subaward) {
        SubcontractDetail subcontractDetail = SubcontractDetail.Factory.newInstance();
        RolodexDetailsType rolodexDetailsType = RolodexDetailsType.Factory.newInstance();
        if(subaward.getRolodex() != null ){
            subcontractDetail.setSiteInvestigator(subaward.getRolodex().getFullName());
            subcontractDetail.setSiteInvestigatorEmail(subaward.getRolodex().getEmailAddress());
        }
        subcontractDetail.setFsrsSubawardNumber(subaward.getFsrsSubawardNumber());
        if (subaward.getOrganization() != null){
            subcontractDetail.setSubcontractorName(subaward.getOrganization().getOrganizationName());
            rolodexDetailsType.setAddress1(subaward.getOrganization().getRolodex().getAddressLine1());
            rolodexDetailsType.setAddress2(subaward.getOrganization().getRolodex().getAddressLine2());
            rolodexDetailsType.setAddress3(subaward.getOrganization().getRolodex().getAddressLine3());
            rolodexDetailsType.setCity(subaward.getOrganization().getRolodex().getCity());
            String countryCode = subaward.getOrganization().getRolodex().getCountryCode();
            String stateName = subaward.getOrganization().getRolodex().getState();
            rolodexDetailsType.setStateDescription(getStateName(countryCode, stateName));
            rolodexDetailsType.setPincode(subaward.getOrganization().getRolodex().getPostalCode());
        }

        if (subaward.getStartDate() != null) {
            subcontractDetail.setStartDate(getDateTimeService().getCalendar(subaward.getStartDate()));
        }
        if (subaward.getEndDate() != null) {
            subcontractDetail.setEndDate(getDateTimeService().getCalendar(subaward.getEndDate()));
        }
        final List<SubAwardAmountInfo> allSubAwardAmountInfos = subaward.getAllSubAwardAmountInfos();
        if (allSubAwardAmountInfos != null && !allSubAwardAmountInfos.isEmpty()){
            final SubAwardAmountInfo lastSubAwardAmountInfo = allSubAwardAmountInfos.get(allSubAwardAmountInfos.size() - 1);
            subcontractDetail.setComments(lastSubAwardAmountInfo.getComments());
            subcontractDetail.setModificationType(lastSubAwardAmountInfo.getModificationTypeCode());
        }

        subcontractDetail.setSubcontractorOrgRolodexDetails(rolodexDetailsType);

        if (fcoi != null) {
            subcontractDetail.setPHSFCOI(toFlag(fcoi));
        }

        if (ffata != null) {
            subcontractDetail.setFFATA(toFlag(ffata));
        }

        subContractData.setSubcontractDetail(subcontractDetail);
    }

    private Optional<Date> getPeriodOfPerformanceEndDate(SubAward subaward) {
        final List<SubAwardAmountInfo> allSubAwardAmountInfos = subaward.getAllSubAwardAmountInfos();
        return allSubAwardAmountInfos.isEmpty() ? Optional.empty() : Optional.ofNullable(allSubAwardAmountInfos.get(allSubAwardAmountInfos.size() - 1).getPeriodofPerformanceEndDate());
    }

    private Optional<Date> getPeriodOfPerformanceStartDate(SubAward subaward) {
        final List<SubAwardAmountInfo> allSubAwardAmountInfos = subaward.getAllSubAwardAmountInfos();
        return allSubAwardAmountInfos.isEmpty() ? Optional.empty() : Optional.ofNullable(allSubAwardAmountInfos.get(allSubAwardAmountInfos.size() - 1).getPeriodofPerformanceStartDate());
    }

    private String toFlag(Boolean b) {
        return Boolean.TRUE.equals(b) ? "Y" : "N";
    }

    public void setSubcontractAmountInfo(SubContractData subContractData, SubAward subaward) {

        SubcontractAmountInfo subContractAmountInfo = SubcontractAmountInfo.Factory.newInstance();
        List<SubcontractAmountInfo> amountinfoList = new ArrayList<>();
        final List<SubAwardAmountInfo> allSubAwardAmountInfos = subaward.getAllSubAwardAmountInfos();

        if (allSubAwardAmountInfos != null && !allSubAwardAmountInfos.isEmpty()) {
            subContractAmountInfo.setObligatedAmount(subaward.getTotalObligatedAmount().bigDecimalValue());
            subContractAmountInfo.setAnticipatedAmount(subaward.getTotalAnticipatedAmount().bigDecimalValue());

            final SubAwardAmountInfo lastSubAwardAmountInfo = allSubAwardAmountInfos.get(allSubAwardAmountInfos.size() - 1);

            if (lastSubAwardAmountInfo.getObligatedChange() != null) {
                subContractAmountInfo.setObligatedChange(lastSubAwardAmountInfo.getObligatedChange().bigDecimalValue());
            }

            if (lastSubAwardAmountInfo.getObligatedChangeDirect() != null) {
                subContractAmountInfo.setObligatedChangeDirect(lastSubAwardAmountInfo.getObligatedChangeDirect().bigDecimalValue());
                subContractAmountInfo.setObligatedChange(lastSubAwardAmountInfo.getObligatedChangeDirect().bigDecimalValue());
            }
            if (lastSubAwardAmountInfo.getObligatedChangeIndirect() != null) {
                subContractAmountInfo.setObligatedChangeIndirect(lastSubAwardAmountInfo.getObligatedChangeIndirect().bigDecimalValue());
                subContractAmountInfo.setObligatedChange(subContractAmountInfo.getObligatedChange().add(lastSubAwardAmountInfo.getObligatedChangeIndirect().bigDecimalValue()));
            }

            getPeriodOfPerformanceStartDate(subaward).ifPresent(date -> subContractAmountInfo.setPerformanceStartDate(getDateTimeService().getCalendar(date)));
            getPeriodOfPerformanceEndDate(subaward).ifPresent(date -> subContractAmountInfo.setPerformanceEndDate(getDateTimeService().getCalendar(date)));

            if (lastSubAwardAmountInfo.getModificationEffectiveDate() != null){
                subContractAmountInfo.setModificationEffectiveDate(getDateTimeService().getCalendar(lastSubAwardAmountInfo.getModificationEffectiveDate()));
            }
            if (lastSubAwardAmountInfo.getModificationID() != null) {
                subContractAmountInfo.setModificationNumber(lastSubAwardAmountInfo.getModificationID());
            }
        }

        amountinfoList.add(subContractAmountInfo);
        subContractData.setSubcontractAmountInfoArray(amountinfoList.toArray(new SubcontractAmountInfo[0]));
    }

    public void setAwardHeader(SubContractData subContractData) {
        AwardType awardType= AwardType.Factory.newInstance();
        AwardHeaderType awardHeaderType =AwardHeaderType.Factory.newInstance();
        AwardDetails awardDetails= AwardDetails.Factory.newInstance();
        OtherHeaderDetails otherDetails = OtherHeaderDetails.Factory.newInstance();
        List<AwardType> awardTypeList = new ArrayList<>();

        if (StringUtils.isNotBlank(sponsorAwardNumber)) {
            awardHeaderType.setSponsorAwardNumber(sponsorAwardNumber);
        }

        if (StringUtils.isNotBlank(sponsorName)) {
            awardHeaderType.setSponsorDescription(sponsorName);
        }
        if(StringUtils.isNotBlank(awardTitle)) {
            awardHeaderType.setTitle(awardTitle);
        }

        if(StringUtils.isNotBlank(cfdaNumber)) {
            otherDetails.setCFDANumber(cfdaNumber);
        }

        if(StringUtils.isNotBlank(primeSponsorName)) {
            otherDetails.setPrimeSponsorDescription(primeSponsorName);
        }

        if(noticeDate != null) {
            otherDetails.setLastUpdate(noticeDate);
        }

        if(fedAwardDate != null) {
            otherDetails.setFAID(fedAwardDate);
        }

        if(obligatedTotal != null) {
            otherDetails.setObligatedAmt(obligatedTotal);
        }

        if(anticipatedTotal != null) {
            otherDetails.setAnticipatedAmt(anticipatedTotal);
        }

        if (StringUtils.isNotEmpty(fain)) {
            otherDetails.setFAIN(fain);
        }

        awardDetails.setAwardHeader(awardHeaderType);
        awardDetails.setOtherHeaderDetails(otherDetails);
        awardType.setAwardDetails(awardDetails);
        awardTypeList.add(awardType);
        subContractData.setAwardArray(awardTypeList.toArray(new AwardType[0]));
    }

    public void setPrimeRecipientContacts(SubContractData subContractData, SubAward subaward) {
        PrimeRecipientContacts primeReceipient = PrimeRecipientContacts.Factory.newInstance();
        OrganizationType organisation = OrganizationType.Factory.newInstance();
        RolodexDetailsType rolodexDetails = RolodexDetailsType.Factory.newInstance();

        final Organization univOrganisation = getBusinessObjectService().findByPrimaryKey(Organization.class, Collections.singletonMap(ORGANIZATION_ID, "000001"));
        if (univOrganisation != null) {
            organisation.setOrganizationName(univOrganisation.getOrganizationName());
        }

        boolean requisitionerOrg = getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_SUBAWARD,
                                                                                    Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE,
                                                                                    FDP_ORG_FROM_REQUISITIONER_UNIT);
        final Organization reqOrganisation = requisitionerOrg && subaward.getUnit() != null ? subaward.getUnit().getOrganization() : null;
        final Rolodex rolodex = requisitionerOrg && reqOrganisation != null && reqOrganisation.getRolodex() != null ? reqOrganisation.getRolodex() : univOrganisation != null ? univOrganisation.getRolodex() : null;

        if (rolodex != null) {
            rolodexDetails.setAddress1(rolodex.getAddressLine1());
            rolodexDetails.setAddress2(rolodex.getAddressLine2());
            rolodexDetails.setAddress3(rolodex.getAddressLine3());
            rolodexDetails.setCity(rolodex.getCity());
            String countryCode = rolodex.getCountryCode();
            String stateName = rolodex.getState();
            rolodexDetails.setStateDescription(getStateName(countryCode, stateName));
            rolodexDetails.setPincode(rolodex.getPostalCode());
        }

        primeReceipient.setOrgRolodexDetails(rolodexDetails);
        primeReceipient.setRequisitionerOrgDetails(organisation);
        subContractData.setPrimeRecipientContacts(primeReceipient);
    }

    public void setPrintRequirement(SubContractData subContractData) {
        PrintRequirement printrequirement =PrintRequirement.Factory.newInstance();

        String externalImageURL = Constants.KRA_EXTERNALIZABLE_IMAGES_URI_KEY;

        String imagePath=configurationService.getPropertyValueAsString(externalImageURL);
        printrequirement.setAttachment5Required("N");
        printrequirement.setAttachment3BRequired("N");
        printrequirement.setAttachment4Required("N");
        String subawardAttachment4 = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                                                                                        ParameterConstants.DOCUMENT_COMPONENT,
                                                                                        Constants.PARAMETER_FDP_SUBAWARD_ATTACHMENT_4);
        String subawardAttachment3B = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                                                                                        ParameterConstants.DOCUMENT_COMPONENT,
                                                                                        Constants.PARAMETER_FDP_SUBAWARD_ATTACHMENT_3B);

        for(SubAwardForms subAwardForms : sponsorTemplates){
            if(subAwardForms.getFormId().equals(subawardAttachment4)){
                printrequirement.setAttachment4Required("Y");
            }
            if(subAwardForms.getFormId().equals(subawardAttachment3B)){
                printrequirement.setAttachment3BRequired("Y");
            }
        }
        printrequirement.setImageCheckedPath(imagePath);
        printrequirement.setImageUncheckedPath(imagePath);
        subContractData.setPrintRequirement(printrequirement);
    }

    public void setPrimeAdministrativeContact(SubContractData subContractData, SubAward subaward) {
        PrimeAdministrativeContact primeAdministrativeContact= PrimeAdministrativeContact.Factory.newInstance();
        RolodexDetailsType rolodexdetails = RolodexDetailsType.Factory.newInstance();
        List<PrimeAdministrativeContact> rolodexDetailsList = new ArrayList<>();
        Map<String, String> administrativeMap = new HashMap<>();

        String administrativeContactCode = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                                                                                            ParameterConstants.DOCUMENT_COMPONENT,
                                                                                            Constants.PARAMETER_FDP_PRIME_ADMINISTRATIVE_CONTACT_CODE);
        administrativeMap.put(CONTACT_TYPE_CODE, administrativeContactCode);
        ContactUsage contactUsage =   getBusinessObjectService().findByPrimaryKey(ContactUsage.class, administrativeMap);
        if(contactUsage.getModuleCode().equals(CoeusModule.SUBCONTRACTS_MODULE_CODE)){
            if(subaward.getSubAwardContactsList() != null && !subaward.getSubAwardContactsList().isEmpty()){
                for(SubAwardContact subAwardContact : subaward.getSubAwardContactsList()){
                    if(subAwardContact.getContactTypeCode().equals(administrativeContactCode)) {
                        primeAdministrativeContact.setContactTypeCode(administrativeContactCode);
                        final Rolodex rolodex = subAwardContact.getRolodex();
                        if( rolodex.getFullName() == null ) {
                            rolodexdetails.setRolodexName(rolodex.getOrganization());
                        }
                        else {
                            rolodexdetails.setRolodexName(rolodex.getFullName());
                        }
                        rolodexdetails.setAddress1(rolodex.getAddressLine1());
                        rolodexdetails.setAddress2(rolodex.getAddressLine2());
                        rolodexdetails.setAddress3(rolodex.getAddressLine3());
                        rolodexdetails.setCity(rolodex.getCity());
                        String countryCode = rolodex.getCountryCode();
                        String stateName = rolodex.getState();
                        rolodexdetails.setStateDescription(getStateName(countryCode, stateName));
                        rolodexdetails.setPincode(rolodex.getPostalCode());
                        rolodexdetails.setPhoneNumber(rolodex.getPhoneNumber());
                        rolodexdetails.setFax(rolodex.getFaxNumber());
                        rolodexdetails.setEmail(rolodex.getEmailAddress());

                        primeAdministrativeContact.setCoiContactEmail(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD, ParameterConstants.DOCUMENT_COMPONENT, "FDP_COI_Contact_email"));
                    }
                }
            }
        }
        primeAdministrativeContact.setRolodexDetails(rolodexdetails);
        rolodexDetailsList.add(primeAdministrativeContact);
        subContractData.setPrimeAdministrativeContactArray(rolodexDetailsList.toArray(new PrimeAdministrativeContact[0]));
    }
    public void setPrimePrincipalInvestigator(SubContractData subContractData) {
        PrimePrincipalInvestigator primePrincipalInvestigator = PrimePrincipalInvestigator.Factory.newInstance();
        List<PrimePrincipalInvestigator> primePrincipalInvestigatorsList = new ArrayList<>();
        Map<String, String> awardNum = new HashMap<>();
        if(awardNumber != null){
            awardNum.put(AWARD_NUMBER, awardNumber);
            awardNum.put(ROLE_CODE, PRINCIPAL_INVESTIGATOR);
            List<AwardPerson> awardNumList = (List<AwardPerson>) getBusinessObjectService().findMatchingOrderBy(AwardPerson.class, awardNum, SEQUENCE_NUMBER, true);
            if (CollectionUtils.isNotEmpty(awardNumList)) {
                AwardPerson awardPerson = awardNumList.get(awardNumList.size() - 1);
                KcPerson awardPersons = awardPerson.getPerson();
                PersonDetailsType personDetails = PersonDetailsType.Factory.newInstance();
                if (awardPersons != null) {
                    personDetails.setFullName(awardPersons.getFullName());
                    personDetails.setAddressLine1(awardPersons.getAddressLine1());
                    personDetails.setAddressLine2(awardPersons.getAddressLine2());
                    personDetails.setAddressLine3(awardPersons.getAddressLine3());
                    personDetails.setCity(awardPersons.getCity());
                    String countryCode = awardPersons.getCountryCode();
                    String stateName = awardPersons.getState();
                    personDetails.setState(getStateName(countryCode, stateName));
                    personDetails.setPostalCode(awardPersons.getPostalCode());
                    personDetails.setMobilePhoneNumber(awardPersons.getOfficePhone());
                    personDetails.setFaxNumber(awardPersons.getFaxNumber());
                    personDetails.setEmailAddress(awardPersons.getEmailAddress());
                } else {
                    final NonOrganizationalRolodex rolodex = awardPerson.getRolodex();
                    if (rolodex != null) {
                        personDetails.setFullName(rolodex.getFullName());
                        personDetails.setAddressLine1(rolodex.getAddressLine1());
                        personDetails.setAddressLine2(rolodex.getAddressLine2());
                        personDetails.setAddressLine3(rolodex.getAddressLine3());
                        personDetails.setCity(rolodex.getCity());
                        String countryCode = rolodex.getCountryCode();
                        String stateName = rolodex.getState();
                        personDetails.setState(getStateName(countryCode, stateName));
                        personDetails.setPostalCode(rolodex.getPostalCode());
                        personDetails.setMobilePhoneNumber(rolodex.getPhoneNumber());
                        personDetails.setFaxNumber(rolodex.getFaxNumber());
                        personDetails.setEmailAddress(rolodex.getEmailAddress());
                    }
                }
                primePrincipalInvestigator.setContactTypeCode(SubawardTemplateContactFinder.PI.getKey());
                primePrincipalInvestigator.setContactTypeDesc(SubawardTemplateContactFinder.PI.getValue());
                primePrincipalInvestigator.setPersonDetailsType(personDetails);
            }
        }
        primePrincipalInvestigatorsList.add(primePrincipalInvestigator);
        subContractData.setPrimePrincipalInvestigatorArray(primePrincipalInvestigatorsList.toArray(new PrimePrincipalInvestigator [0]));
    }

    private String getStateName(String countryCode, String stateName) {
        if (countryCode != null && countryCode.length() > 0 && stateName != null && stateName.length() > 0) {
            final Country country = getCountryService().getCountryByAlternateCode(countryCode);
            final State state = getStateService().getState(country.getCode(), stateName);
            if (state != null) {
                return state.getName();
            }
        }
        return "";
    }


    public void setPrimeFinancialContact(SubContractData subContractData, SubAward subaward) {
        PrimeFinancialContact primeFinancialContact = PrimeFinancialContact.Factory.newInstance();
        RolodexDetailsType rolodexDetails = RolodexDetailsType.Factory.newInstance();
        List<PrimeFinancialContact> personDetailsList = new ArrayList<>();
        Map<String, String> administrativeMap = new HashMap<>();

        String administrativeFinancialCode = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                                                                                                ParameterConstants.DOCUMENT_COMPONENT,
                Constants.PARAMETER_FDP_PRIME_FINANCIAL_CONTACT_CODE);
        administrativeMap.put(CONTACT_TYPE_CODE, administrativeFinancialCode);
        ContactUsage contactUsage =   getBusinessObjectService().findByPrimaryKey(ContactUsage.class, administrativeMap);
        if(contactUsage.getModuleCode().equals(CoeusModule.SUBCONTRACTS_MODULE_CODE)){
            if(subaward.getSubAwardContactsList() != null && !subaward.getSubAwardContactsList().isEmpty()){
                for(SubAwardContact subAwardContact : subaward.getSubAwardContactsList()){
                    if(subAwardContact.getContactTypeCode().equals(administrativeFinancialCode)) {
                        primeFinancialContact.setContactTypeCode(administrativeFinancialCode);
                        Rolodex rolodex = subAwardContact.getRolodex();
                        if( rolodex.getFullName() == null) {
                            rolodexDetails.setRolodexName(rolodex.getOrganization());
                        }
                        else {
                            rolodexDetails.setRolodexName(rolodex.getFullName());
                        }
                        rolodexDetails.setAddress1(rolodex.getAddressLine1());
                        rolodexDetails.setAddress2(rolodex.getAddressLine2());
                        rolodexDetails.setAddress3(rolodex.getAddressLine3());
                        rolodexDetails.setCity(rolodex.getCity());
                        String countryCode = rolodex.getCountryCode();
                        String stateName = rolodex.getState();
                        rolodexDetails.setStateDescription(getStateName(countryCode, stateName));
                        rolodexDetails.setPincode(rolodex.getPostalCode());
                        rolodexDetails.setPhoneNumber(rolodex.getPhoneNumber());
                        rolodexDetails.setFax(rolodex.getFaxNumber());
                        rolodexDetails.setEmail(rolodex.getEmailAddress());

                        primeFinancialContact.setInvoicesEmailed(toFlag(invoicesEmailed));

                        if (invoiceEmailDifferent != null && invoiceEmailDifferent) {
                            primeFinancialContact.setInvoiceEmailDifferent(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD, ParameterConstants.DOCUMENT_COMPONENT, FDP_PTE_INVOICE_EMAIL));
                        }

                        if (invoiceAddressDifferent != null && invoiceAddressDifferent) {
                            primeFinancialContact.setInvoiceAddressDifferent(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD, ParameterConstants.DOCUMENT_COMPONENT, FDP_PTE_INVOICE_ADDRESS));
                        }
                    }
                }
            }
        }
        primeFinancialContact.setRolodexDetails(rolodexDetails);
        personDetailsList.add(primeFinancialContact);
        subContractData.setPrimeFinancialContactArray(personDetailsList.toArray(new PrimeFinancialContact [0]));
    }

    public void setPrimeAuthorizedOfficial(SubContractData subContractData, SubAward subaward) {
        PrimeAuthorizedOfficial primeAuthorisedOfficial = PrimeAuthorizedOfficial.Factory.newInstance();
        RolodexDetailsType rolodexDetails = RolodexDetailsType.Factory.newInstance();
        List<PrimeAuthorizedOfficial> primeAuthorisedList = new ArrayList<>();
        Map<String, String> administrativeMap = new HashMap<>();

        String auhtorisedOfficialCode = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                                                                                        ParameterConstants.DOCUMENT_COMPONENT,
                                                                                        Constants.PARAMETER_FDP_PRIME_AUTHORIZED_OFFICIAL_CODE);
        administrativeMap.put(CONTACT_TYPE_CODE, auhtorisedOfficialCode);
        ContactUsage contactUsage =   getBusinessObjectService().findByPrimaryKey(ContactUsage.class, administrativeMap);
        if(contactUsage.getModuleCode().equals(CoeusModule.SUBCONTRACTS_MODULE_CODE)) {
            if(subaward.getSubAwardContactsList() != null && !subaward.getSubAwardContactsList().isEmpty()){
                for(SubAwardContact subAwardContact : subaward.getSubAwardContactsList()){
                    if(subAwardContact.getContactTypeCode().equals(auhtorisedOfficialCode)) {
                        primeAuthorisedOfficial.setContactTypeCode(auhtorisedOfficialCode);
                        Rolodex rolodex = subAwardContact.getRolodex();
                        if( rolodex.getFullName()==null) {
                            rolodexDetails.setRolodexName(rolodex.getOrganization());
                        }
                        else {
                            rolodexDetails.setRolodexName(rolodex.getFullName());
                        }
                        rolodexDetails.setAddress1(rolodex.getAddressLine1());
                        rolodexDetails.setAddress2(rolodex.getAddressLine2());
                        rolodexDetails.setAddress3(rolodex.getAddressLine3());
                        rolodexDetails.setCity(rolodex.getCity());
                        String countryCode = rolodex.getCountryCode();
                        String stateName = rolodex.getState();
                        rolodexDetails.setStateDescription(getStateName(countryCode, stateName));
                        rolodexDetails.setPincode(rolodex.getPostalCode());
                        rolodexDetails.setPhoneNumber(rolodex.getPhoneNumber());
                        rolodexDetails.setFax(rolodex.getFaxNumber());
                        rolodexDetails.setEmail(rolodex.getEmailAddress());

                        primeAuthorisedOfficial.setCentralEmail(getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD, ParameterConstants.DOCUMENT_COMPONENT, "FDP_PTE_AOR_Central_email"));

                    }
                }
            }
        }
        primeAuthorisedOfficial.setRolodexDetails(rolodexDetails);
        primeAuthorisedList.add(primeAuthorisedOfficial);
        subContractData.setPrimeAuthorizedOfficialArray(primeAuthorisedList.toArray(new PrimeAuthorizedOfficial [0]));
    }
    public void setAdministrativeContact(SubContractData subContractData, SubAward subaward) {
        AdministrativeContact administrativeContact = AdministrativeContact.Factory.newInstance();
        RolodexDetailsType rolodexDetails = RolodexDetailsType.Factory.newInstance();
        List<AdministrativeContact> administrativeContactList = new ArrayList<>();
        Map<String, String> administrativeMap = new HashMap<>();

        String administrativeCode = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                                                                                    ParameterConstants.DOCUMENT_COMPONENT,
                                                                                    Constants.PARAMETER_FDP_SUB_ADMINISTRATIVE_CONTACT_CODE);
        administrativeMap.put(CONTACT_TYPE_CODE, administrativeCode);
        ContactUsage contactUsage =   getBusinessObjectService().findByPrimaryKey(ContactUsage.class, administrativeMap);
        if(contactUsage.getModuleCode().equals(CoeusModule.SUBCONTRACTS_MODULE_CODE)){
            if(subaward.getSubAwardContactsList() != null && !subaward.getSubAwardContactsList().isEmpty()){
                for(SubAwardContact subAwardContact : subaward.getSubAwardContactsList()){
                    if(subAwardContact.getContactTypeCode().equals(administrativeCode)) {
                        administrativeContact.setContactTypeCode(administrativeCode);
                        Rolodex rolodex = subAwardContact.getRolodex();
                        String organization = rolodex.getOrganization();
                        String fullName = rolodex.getFullName();
                        if (fullName != null && fullName.length() > 0){
                            rolodexDetails.setRolodexName(fullName);
                        } else {
                            rolodexDetails.setRolodexName(organization);
                        }
                        rolodexDetails.setAddress1(rolodex.getAddressLine1());
                        rolodexDetails.setAddress2(rolodex.getAddressLine2());
                        rolodexDetails.setAddress3(rolodex.getAddressLine3());
                        rolodexDetails.setCity(rolodex.getCity());
                        String countryCode = rolodex.getCountryCode();
                        String stateName = rolodex.getState();
                        rolodexDetails.setStateDescription(getStateName(countryCode, stateName));
                        rolodexDetails.setPincode(rolodex.getPostalCode());
                        rolodexDetails.setPhoneNumber(rolodex.getPhoneNumber());
                        rolodexDetails.setFax(rolodex.getFaxNumber());
                        rolodexDetails.setEmail(rolodex.getEmailAddress());
                    }
                }}
        }
        administrativeContact.setRolodexDetails(rolodexDetails);
        administrativeContactList.add(administrativeContact);
        subContractData.setAdministrativeContactArray(administrativeContactList.toArray(new AdministrativeContact [0]));
    }
    public void setFinancialContact(SubContractData subContractData, SubAward subaward) {
        FinancialContact financialContact =FinancialContact.Factory.newInstance();
        RolodexDetailsType rolodexDetails = RolodexDetailsType.Factory.newInstance();
        List<FinancialContact> financialContactList = new ArrayList<>();
        Map<String, String> administrativeMap = new HashMap<>();
        String financialContactCode = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                                                                                        ParameterConstants.DOCUMENT_COMPONENT,
                Constants.PARAMETER_FDP_SUB_FINANCIAL_CONTACT_CODE);
        administrativeMap.put(CONTACT_TYPE_CODE, financialContactCode);
        ContactUsage contactUsage =   getBusinessObjectService().findByPrimaryKey(ContactUsage.class, administrativeMap);
        if(contactUsage.getModuleCode().equals(CoeusModule.SUBCONTRACTS_MODULE_CODE)){
            if(subaward.getSubAwardContactsList() != null && !subaward.getSubAwardContactsList().isEmpty()) {
                for(SubAwardContact subAwardContact : subaward.getSubAwardContactsList()){
                    if(subAwardContact.getContactTypeCode().equals(financialContactCode)) {
                        financialContact.setContactTypeCode(financialContactCode);

                        Rolodex rolodex = subAwardContact.getRolodex();
                        String organization = rolodex.getOrganization();
                        String fullName = rolodex.getFullName();
                        if (fullName != null && fullName.length() > 0){
                            rolodexDetails.setRolodexName(fullName);
                        } else {
                            rolodexDetails.setRolodexName(organization);
                        }
                        rolodexDetails.setAddress1(rolodex.getAddressLine1());
                        rolodexDetails.setAddress2(rolodex.getAddressLine2());
                        rolodexDetails.setAddress3(rolodex.getAddressLine3());
                        rolodexDetails.setCity(rolodex.getCity());
                        String countryCode = rolodex.getCountryCode();
                        String stateName = rolodex.getState();
                        rolodexDetails.setStateDescription(getStateName(countryCode, stateName));
                        rolodexDetails.setPincode(rolodex.getPostalCode());
                        rolodexDetails.setPhoneNumber(rolodex.getPhoneNumber());
                        rolodexDetails.setFax(rolodex.getFaxNumber());
                        rolodexDetails.setEmail(rolodex.getEmailAddress());
                    }
                }
            }
        }
        financialContact.setRolodexDetails(rolodexDetails);
        financialContactList.add(financialContact);
        subContractData.setFinancialContactArray(financialContactList.toArray(new FinancialContact [0]));
    }

    public void setAuthorizedOfficial(SubContractData subContractData, SubAward subaward) {
        AuthorizedOfficial authorizedOfficial =AuthorizedOfficial.Factory.newInstance();
        RolodexDetailsType rolodexDetails = RolodexDetailsType.Factory.newInstance();
        List<AuthorizedOfficial> authorizedOfficialList = new ArrayList<>();
        Map<String, String> administrativeMap = new HashMap<>();
        String financialContactCode = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD,
                                                                                        ParameterConstants.DOCUMENT_COMPONENT,
                                                                                        Constants.PARAMETER_FDP_SUB_AUTHORIZED_CONTACT_CODE);
        administrativeMap.put(CONTACT_TYPE_CODE, financialContactCode);
        ContactUsage contactUsage = getBusinessObjectService().findByPrimaryKey(ContactUsage.class, administrativeMap);
        if(contactUsage.getModuleCode().equals(CoeusModule.SUBCONTRACTS_MODULE_CODE)){
            if(subaward.getSubAwardContactsList() != null && !subaward.getSubAwardContactsList().isEmpty()){
                for(SubAwardContact subAwardContact : subaward.getSubAwardContactsList()){
                    if(subAwardContact.getContactTypeCode().equals(financialContactCode)) {
                        authorizedOfficial.setContactTypeCode(financialContactCode);

                        Rolodex rolodex = subAwardContact.getRolodex();
                        String organization = rolodex.getOrganization();
                        String fullName = rolodex.getFullName();
                        if (fullName != null && fullName.length() > 0){
                            rolodexDetails.setRolodexName(fullName);
                        } else {
                            rolodexDetails.setRolodexName(organization);
                        }
                        rolodexDetails.setAddress1(rolodex.getAddressLine1());
                        rolodexDetails.setAddress2(rolodex.getAddressLine2());
                        rolodexDetails.setAddress3(rolodex.getAddressLine3());
                        rolodexDetails.setCity(rolodex.getCity());
                        String countryCode = rolodex.getCountryCode();
                        String stateName = rolodex.getState();
                        rolodexDetails.setStateDescription(getStateName(countryCode, stateName));
                        rolodexDetails.setPincode(rolodex.getPostalCode());
                        rolodexDetails.setPhoneNumber(rolodex.getPhoneNumber());
                        rolodexDetails.setFax(rolodex.getFaxNumber());
                        rolodexDetails.setEmail(rolodex.getEmailAddress());
                    }
                }
            }
        }
        authorizedOfficial.setRolodexDetails(rolodexDetails);
        authorizedOfficialList.add(authorizedOfficial);
        subContractData.setAuthorizedOfficialArray(authorizedOfficialList.toArray(new AuthorizedOfficial[0]));
    }

    public void setSubcontractReports(SubContractData subContractData, SubAward subaward) {
        List<SubcontractReports> subContractReportsList = new ArrayList<>();
        SubAwardReportType subAwardReportsType;
        if(subaward.getSubAwardReportList() != null && !subaward.getSubAwardReportList().isEmpty()){
            for(SubAwardReports subAwardReports : subaward.getSubAwardReportList()) {
                SubcontractReports subContractReports = SubcontractReports.Factory.newInstance();
                subAwardReports.refreshReferenceObject(TYPE_CODE);
                subAwardReportsType = subAwardReports.getTypeCode();
                subContractReports.setReportTypeDescription(subAwardReportsType.getDescription());
                subContractReportsList.add(subContractReports);
            }
        }
        subContractData.setSubcontractReportsArray(subContractReportsList.toArray(new SubcontractReports[0]));
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    private DateTimeService dateTimeService;

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public StateService getStateService() {
        return stateService;
    }

    public void setStateService(StateService stateService) {
        this.stateService = stateService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}