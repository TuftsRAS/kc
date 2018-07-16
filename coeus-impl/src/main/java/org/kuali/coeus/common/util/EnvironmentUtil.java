package org.kuali.coeus.common.util;

import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.FeatureFlagConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EnvironmentUtil {
    private static EnvironmentUtil environmentUtil;

    public static final String APPLICATION_URL = "application.url";
    public static final String APPLICATION_ENV_SBX_REGEX = "application.environment.sbx.regex";
    public static final String APPLICATION_ENV_STG_REGEX = "application.environment.stg.regex";
    public static final String APPLICATION_ENV_TST_REGEX = "application.environment.tst.regex";
    public static final String APPLICATION_ENV_DEMO_REGEX = "application.environment.demo.regex";

    public static final String SBX_TEXT = "Sandbox Environment";
    public static final String STG_TEXT = "Staging Environment";
    public static final String TST_TEXT = "Testing Environment";
    public static final String DEMO_TEXT = "Demo Environment";

    private String environmentText = "";

    private ConfigurationService configurationService;
    private ParameterService parameterService;

    public static EnvironmentUtil getInstance() {
        if (environmentUtil == null) {
            environmentUtil = new EnvironmentUtil();
        }

        return environmentUtil;
    }

    public EnvironmentUtil() {
        Map<String, String> environmentsMap = new HashMap<>();
        String applicationUrl = getConfigurationService().getPropertyValueAsString(APPLICATION_URL);

        environmentsMap.put(APPLICATION_ENV_SBX_REGEX, SBX_TEXT);
        environmentsMap.put(APPLICATION_ENV_STG_REGEX, STG_TEXT);
        environmentsMap.put(APPLICATION_ENV_TST_REGEX, TST_TEXT);
        environmentsMap.put(APPLICATION_ENV_DEMO_REGEX, DEMO_TEXT);

        environmentsMap.forEach((regexProperty, text) -> {
            if (Pattern.matches(getConfigurationService().getPropertyValueAsString(regexProperty), applicationUrl)) {
                setEnvironmentText(text);
            }
        });
    }

    public String getEnvironmentText() {
        return (isEnvironmentBannerEnabled()) ? environmentText : "";
    }

    public void setEnvironmentText(String environmentText) {
        this.environmentText = environmentText;
    }

    public ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = KcServiceLocator.getService(ConfigurationService.class);
        }

        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return this.parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected boolean isEnvironmentBannerEnabled() {
        return getParameterService().getParameterValueAsBoolean(
                Constants.MODULE_NAMESPACE_GEN, ParameterConstants.ALL_COMPONENT, FeatureFlagConstants.ENVIRONMENT_BANNER_ENABLED, true);
    }
}
