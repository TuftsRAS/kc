/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.custom.arg;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.custom.arg.ArgValueLookup;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.coeus.sys.framework.util.ValuesFinderUtils;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgValueLookupValuesFinder extends UifKeyValuesFinderBase {

	private enum ArgValueConfig { VALUE, DESCRIPTION, BOTH}
	private static final String ARGUMENT_NAME = "argumentName";
	private static final String ARG_VALUE_VALUES_FINDER_DEFAULT_CONFIG = "ARG_VALUE_VALUES_FINDER_DEFAULT_CONFIG";
	private static final String ARG_VALUE_VALUES_FINDER_ARG_CONFIG ="ARG_VALUE_VALUES_FINDER_ARG_CONFIG";

	private transient ParameterService parameterService;
	private transient BusinessObjectService businessObjectService;
    private static final String INACTIVE_IND = "(inactive)";

    private String argName;
    private String currentValue;
    private boolean excludeInactive = true;

    @Override
    public List<KeyValue> getKeyValues() {
        final Collection<ArgValueLookup> argValueLookups = getBusinessObjectService().findMatching(ArgValueLookup.class, Collections.singletonMap(ARGUMENT_NAME, argName));

		final String defaultConfig = getParameterService().getParameterValueAsString(Constants.KC_GENERIC_PARAMETER_NAMESPACE,
				Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, ARG_VALUE_VALUES_FINDER_DEFAULT_CONFIG);
		final String argConfig = getParameterService().getSubParameterValueAsString(Constants.KC_GENERIC_PARAMETER_NAMESPACE,
				Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, ARG_VALUE_VALUES_FINDER_ARG_CONFIG, argName);
		final String cfg = StringUtils.isNotBlank(argConfig) ? argConfig : defaultConfig;

		return Stream.concat(Stream.of(ValuesFinderUtils.getSelectOption()), argValueLookups.stream()
                .filter(argValue -> !isExcludeInactive() || argValue.isActive() || StringUtils.equals(currentValue, argValue.getValue()))
                .sorted(Comparator.comparing((ArgValueLookup argValue) -> getKeyValueValue(argValue, cfg)).thenComparing(ArgValueLookup::isActive))
				.map(argValueLookup -> new ConcreteKeyValue(argValueLookup.getValue(), getKeyValueValue(argValueLookup, cfg))))
				.collect(Collectors.toList());
    }

	protected String getKeyValueValue(ArgValueLookup argValueLookup, String cfg) {
        String value = argValueLookup.getValue();
		if (ArgValueConfig.VALUE.toString().equals(cfg)) {
			value = argValueLookup.getValue();
		} else if (ArgValueConfig.DESCRIPTION.toString().equals(cfg))  {
			value = argValueLookup.getDescription();
		} else if (ArgValueConfig.BOTH.toString().equals(cfg)) {
			value = argValueLookup.getValue() + (StringUtils.isNotBlank(argValueLookup.getDescription()) ? ( " - " + argValueLookup.getDescription()) : "");
		}
		return argValueLookup.isActive() ? value : value + " " + INACTIVE_IND;
	}

    public String getArgName() {
        return argName;
    }

    public void setArgName(String argName) {
        this.argName = argName;
    }

	public ParameterService getParameterService() {
		if (parameterService == null) {
			parameterService = KcServiceLocator.getService(ParameterService.class);
		}
		return parameterService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	public BusinessObjectService getBusinessObjectService() {
		if (businessObjectService == null) {
			businessObjectService = KcServiceLocator.getService(BusinessObjectService.class);
		}
    	return businessObjectService;
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}
    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public boolean isExcludeInactive() {
        return excludeInactive;
    }

    public void setExcludeInactive(boolean excludeInactive) {
        this.excludeInactive = excludeInactive;
    }
}
