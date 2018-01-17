/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.controller;

import org.apache.commons.lang3.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * A dispatcher servlet that only initializes if a specific config property exists with a
 * specific value.
 *
 * The config property to check is configured by setting an init property {@value #INIT_CONFIG_PROPERTY_NAME}.
 * The config property value to check is configured by an init property {@value #INIT_CONFIG_PROPERTY_NAME}.
 */
public class ConditionalInitDispatcherServlet extends DispatcherServlet {

    public static final String INIT_CONFIG_PROPERTY_NAME = "initConfigPropertyName";
    public static final String INIT_CONFIG_PROPERTY_VALUE = "initConfigPropertyValue";

    @Override
    public void init(ServletConfig config) throws ServletException {

        if (doInit(config)) {
            super.init(config);
        } else {
            //logging the same way the super class logs using context logger
            config.getServletContext().log("Not Initialized Spring FrameworkServlet '" + getServletName(config) + "'");
        }
    }

    private String getServletName(ServletConfig config) {
        return (config != null ? config.getServletName() : null);
    }

    private boolean doInit(ServletConfig config) {
        final String initConfigPropertyName = config.getInitParameter(INIT_CONFIG_PROPERTY_NAME);
        final String initConfigPropertyValue = config.getInitParameter(INIT_CONFIG_PROPERTY_VALUE);

        boolean init = false;
        if (StringUtils.isNotBlank(initConfigPropertyName)) {
            final String configProperty = getConfigPropertyValue(initConfigPropertyName);
            if (StringUtils.equals(initConfigPropertyValue, configProperty)) {
                init = true;
            }
        }
        return init;
    }

    private String getConfigPropertyValue(String name) {
        return ConfigContext.getCurrentContextConfig().getProperty(name);
    }
}
