/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.service;

import org.apache.commons.lang3.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.collect.CollectionUtils;
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl;
import org.springframework.util.Log4jConfigurer;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Properties;

public class KcServiceLocatorListener implements ServletContextListener {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(KcServiceLocatorListener.class);
    private static final String DEFAULT_SPRING_BEANS_REPLACEMENT_VALUE = "${bootstrap.spring.file}";
    private static final String WEB_BOOTSTRAP_SPRING_FILE = "web.bootstrap.spring.file";

    private XmlWebApplicationContext context;

    /**
     * ServletContextListener interface implementation that schedules the start of the lifecycle
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        long startInit = System.currentTimeMillis();
        LOG.info("Initializing Kuali Coeus Application...");

        // Stop Quartz from "phoning home" on every startup
        System.setProperty("org.terracotta.quartz.skipUpdateCheck", "true");

        String bootstrapSpringBeans = "";
        if (!StringUtils.isBlank(System.getProperty(WEB_BOOTSTRAP_SPRING_FILE))) {
            bootstrapSpringBeans = System.getProperty(WEB_BOOTSTRAP_SPRING_FILE);
        } else if (!StringUtils.isBlank(sce.getServletContext().getInitParameter(WEB_BOOTSTRAP_SPRING_FILE))) {
            String bootstrapSpringInitParam = sce.getServletContext().getInitParameter(WEB_BOOTSTRAP_SPRING_FILE);
            // if the value comes through as ${bootstrap.spring.beans}, we ignore it
            if (!DEFAULT_SPRING_BEANS_REPLACEMENT_VALUE.equals(bootstrapSpringInitParam)) {
                bootstrapSpringBeans = bootstrapSpringInitParam;
                LOG.info("Found bootstrap Spring Beans file defined in servlet context: " + bootstrapSpringBeans);
            }
        }

        Properties baseProps = new Properties();
        baseProps.putAll(getContextParameters(sce.getServletContext()));
        baseProps.putAll(System.getProperties());

        final String configFile = baseProps.getProperty("web.bootstrap.config.file");

        JAXBConfigImpl config = StringUtils.isNotBlank(configFile) ? new JAXBConfigImpl(configFile, baseProps) :  new JAXBConfigImpl(baseProps);

        try {
            config.parseConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //override all configured items with system properties
        config.putProperties(System.getProperties());
        ConfigContext.init(config);

        context = new XmlWebApplicationContext();
        if (!StringUtils.isEmpty(bootstrapSpringBeans)) {
            context.setConfigLocation(bootstrapSpringBeans);
        }
        context.setServletContext(sce.getServletContext());
        context.refresh();
        context.start();

        KcServiceLocator.setAppContext(getContext());

        long endInit = System.currentTimeMillis();
        LOG.info("...Kuali Coeus Application successfully initialized, startup took " + (endInit - startInit) + " ms.");
    }

    /**
     * Translates context parameters from the web.xml into entries in a Properties file.
     */
    protected Properties getContextParameters(ServletContext context) {
        Properties properties = new Properties();
        for (String paramName : CollectionUtils.toIterable(context.getInitParameterNames())) {
            properties.put(paramName, context.getInitParameter(paramName));
        }
        return properties;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("Shutting down Kuali Coeus...");
        if (context != null) {
            context.close();
        }
        LOG.info("...completed shutdown of Kuali Coeus.");
        Log4jConfigurer.shutdownLogging();
    }

    public XmlWebApplicationContext getContext() {
        return context;
    }
}
