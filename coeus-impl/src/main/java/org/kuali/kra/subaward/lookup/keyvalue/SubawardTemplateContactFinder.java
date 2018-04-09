/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.kra.subaward.lookup.keyvalue;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.keyvalue.FormViewAwareUifKeyValuesFinderBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;

import org.kuali.coeus.sys.framework.util.ValuesFinderUtils;
import org.kuali.kra.award.home.ContactType;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubawardTemplateContactFinder extends FormViewAwareUifKeyValuesFinderBase {

    private static final Set<String> CONTACT_PARAMETERS = Stream.of(Constants.PARAMETER_FDP_SUB_FINANCIAL_CONTACT_CODE, Constants.PARAMETER_FDP_PRIME_AUTHORIZED_OFFICIAL_CODE, Constants.PARAMETER_FDP_SUB_ADMINISTRATIVE_CONTACT_CODE,
            Constants.PARAMETER_FDP_PRIME_FINANCIAL_CONTACT_CODE, Constants.PARAMETER_FDP_PRIME_AUTHORIZED_OFFICIAL_CODE, Constants.PARAMETER_FDP_PRIME_ADMINISTRATIVE_CONTACT_CODE).collect(Collectors.toSet());

    public static final ConcreteKeyValue PI = new ConcreteKeyValue("-99", "Principal Investigator");

    @Override
    public List<KeyValue> getKeyValues() {
        final Set<String> contactCodes = CONTACT_PARAMETERS.stream()
                .map(paramName -> getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_SUBAWARD, ParameterConstants.DOCUMENT_COMPONENT, paramName))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toSet());

        final Collection<ContactType> contactTypes = getBusinessObjectService().findAll(org.kuali.kra.award.home.ContactType.class);
        return Stream.concat(Stream.of(ValuesFinderUtils.getSelectOption()),
                Stream.concat(contactTypes.stream()
                        .filter(contactType -> contactCodes.contains(contactType.getContactTypeCode()))
                        .map(contactType -> new ConcreteKeyValue(contactType.getContactTypeCode(), contactType.getDescription())), Stream.of(PI))
                        .sorted(Comparator.comparing(KeyValue::getValue)))
                .collect(Collectors.toList());
    }

    protected BusinessObjectService getBusinessObjectService() {
        return KcServiceLocator.getService(BusinessObjectService.class);
    }

    protected ParameterService getParameterService() {
        return KcServiceLocator.getService(ParameterService.class);
    }
}
