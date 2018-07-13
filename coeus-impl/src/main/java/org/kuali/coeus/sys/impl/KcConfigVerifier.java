/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.impl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component("kcConfigVerifier")
public class KcConfigVerifier implements InitializingBean {

    private static final Logger LOG = LogManager.getLogger(KcConfigVerifier.class);

    private static final String ORACLE_PLATFORM_NM = "Oracle";
    private static final String ORACLE_CLASS_NAME = "org.eclipse.persistence.platform.database.oracle.Oracle11Platform";
    private static final String KC_CONFIG_VERIFIER_HARD_ERROR_CFG_NM = "kc.config.verifier.hard.error";
    private static final String SERVER_DATASOURCE_PLATFORM_CFG_NM = "server.datasource.platform";
    private static final String DATASOURCE_PLATFORM_CFG_NM = "datasource.platform";
    private static final String MISSING_LIB_MESSAGE_FOR_ORACLE = "Oracle platform detected but org.eclipse.persistence:org.eclipse.persistence.oracle:jar is not found on the classpath";
    private static final String INCLUDED_LIB_MESSAGE_FOR_NON_ORACLE = "Non-Oracle platform detected but org.eclipse.persistence:org.eclipse.persistence.oracle:jar is found on the classpath";
    private static final String TOMCAT_SERVER_CLASS_NAME = "org.apache.catalina.Server";
    private static final String TOMCAT_INSTRUMENTATION_CLASS_LOADER_CLASS_NAME = "org.springframework.instrument.classloading.tomcat.TomcatInstrumentableClassLoader";
    private static final String INSTRUMENTATION_AGENT_CLASS_NAME = "org.springframework.instrument.InstrumentationSavingAgent";
    private static final String GET_INSTRUMENTATION_METHOD = "getInstrumentation";
    private static final String TOMCAT_SERVER_INFO_CLASS_NAME = "org.apache.catalina.util.ServerInfo";
    private static final String TOMCAT_7_VERSION_PREFIX = "7.0";
    private static final String TOMCAT_GET_SERVER_NUMBER_METHOD = "getServerNumber";
    private static final String TOMCAT_8_VERSION_PREFIX = "8.0";
    private static final String TOMCAT_85_VERSION_PREFIX = "8.5";
    private static final String INTEGRATION_TEST_CLASS = "org.kuali.kra.test.infrastructure.ApplicationServer";
    private static final String JAVA_UTIL_LOGGING_MANAGER = "java.util.logging.manager";
    private static final String ORG_APACHE_LOGGING_LOG4J_JUL_LOG_MANAGER = "org.apache.logging.log4j.jul.LogManager";
    private static final String JUL_MSG = "org.apache.logging.log4j:log4j-jul is included but not configured to send jul logging to Log4j.";
    private static final String LOG4J_HELP = "See https://logging.apache.org/log4j/2.0/log4j-jul/index.html";

    @Autowired
    @Qualifier("kualiConfigurationService")
    private ConfigurationService configurationService;

    @Override
    public void afterPropertiesSet() {
        verifyOracleConfiguration();
        verifyInstrumentationConfiguration();
        verifyJulLogging();
    }

    protected void verifyJulLogging() {
        final String logManagerClass = System.getProperty(JAVA_UTIL_LOGGING_MANAGER);
        final java.util.logging.LogManager julLogManager = java.util.logging.LogManager.getLogManager();

        final String julLogManagerName = julLogManager.getClass().getName();
        if (!julLogManagerName.equals(ORG_APACHE_LOGGING_LOG4J_JUL_LOG_MANAGER)) {
            if (ORG_APACHE_LOGGING_LOG4J_JUL_LOG_MANAGER.equals(logManagerClass)) {
                LOG.error(JUL_MSG + " The jul log manager is using " + julLogManagerName + ". The system property " + JAVA_UTIL_LOGGING_MANAGER + " was set to " + logManagerClass + " but was likely set too late or not available on the Application Server classpath. " + LOG4J_HELP);
            } else if (logManagerClass != null) {
                LOG.error(JUL_MSG + " The jul log manager is using " + julLogManagerName + ". The system property " + JAVA_UTIL_LOGGING_MANAGER + " was set to " + logManagerClass + ". " + LOG4J_HELP);
            } else {
                LOG.error(JUL_MSG + " The jul log manager is using " + julLogManagerName + ". The system property " + JAVA_UTIL_LOGGING_MANAGER + " was not set. " + LOG4J_HELP);
            }
        }
    }

    protected void verifyOracleConfiguration() {
        if (oraclePlatform() && !oracleJpaLibraryAvailable()) {
            if (hardError()) {
                throw new RuntimeException(MISSING_LIB_MESSAGE_FOR_ORACLE);
            } else {
                LOG.fatal(MISSING_LIB_MESSAGE_FOR_ORACLE);
            }
        } else if (!oraclePlatform() && oracleJpaLibraryAvailable()) {
            LOG.warn(INCLUDED_LIB_MESSAGE_FOR_NON_ORACLE);
        }
    }

    /**
     * Verifies that the instrumentation configuration is set up correctly.  EclipseLink requires
     * some form of instrumentation for features like lazy-loading.  Without proper instrumentation,
     * subtle hard-to-detect errors may occur.
     */
    protected void verifyInstrumentationConfiguration() {

        if (tomcatInstrumentingClassLoaderAvailable() && genericInstrumentationAgentAvailable()) {
            LOG.warn("Both the Spring Tomcat Instrumenting ClassLoader and the Spring Instrumentation Agent are on the classpath but only one is needed.");
        }

        if (runningOnTomcat8Or85() && !runningOnTomcat85FromJunit()) {
            if (tomcatInstrumentingClassLoaderAvailable()) {
                if (hardError()) {
                    throw new RuntimeException("The Spring Tomcat Instrumenting ClassLoader is on the classpath but the Tomcat Application Server 8.x is detected.");
                }
            }

            if (genericInstrumentationAgentAvailable()) {
                if (hardError()) {
                    throw new RuntimeException("The Spring Instrumentation Agent is on the but the Tomcat Application Server 8.x is detected.");
                }
            }
        } else if (runningOnTomcat85FromJunit()) {
            if (tomcatInstrumentingClassLoaderAvailable()) {
                if (hardError()) {
                    throw new RuntimeException("The Spring Tomcat Instrumenting ClassLoader is on the classpath but the Tomcat Application Server 8.5.x Embedded is detected.");
                }
            }

            if (!genericInstrumentationAgentAvailable()) {
                if (hardError()) {
                    throw new RuntimeException("The Spring Instrumentation Agent is not on the classpath but is needed.");
                }
            } else if (genericInstrumentationAgentAvailable() && !genericInstrumentationAgentConfigured()) {
                if (hardError()) {
                    throw new RuntimeException("The Spring Instrumentation Agent is on the classpath but is not properly configured as a jvm javaagent.");
                }
            }
        } else if (runningOnTomcat7()) {
            if (!tomcatInstrumentingClassLoaderAvailable() && !genericInstrumentationAgentAvailable()) {
                if (hardError()) {
                    throw new RuntimeException("Neither the Spring Tomcat Instrumenting ClassLoader or the Spring Instrumentation Agent are on the classpath and one is needed.");
                }
            } else if (tomcatInstrumentingClassLoaderAvailable() && !tomcatInstrumentingClassLoaderConfigured()) {
                if (hardError()) {
                    throw new RuntimeException("The Spring Tomcat Instrumenting ClassLoader is on the classpath but is not properly configured in the context.xml file.");
                }
            } else if (genericInstrumentationAgentAvailable() && !genericInstrumentationAgentConfigured()) {
                if (hardError()) {
                    throw new RuntimeException("The Spring Instrumentation Agent is on the classpath but is not properly configured as a jvm javaagent.");
                }
            }
        } else {
            if (tomcatInstrumentingClassLoaderAvailable()) {
                LOG.warn("The Spring Tomcat Instrumenting ClassLoader is on the classpath but the Tomcat Application Server is not detected.");
            }

            if (!genericInstrumentationAgentAvailable()) {
                if (hardError()) {
                    throw new RuntimeException("The Spring Instrumentation Agent is not on the classpath but is needed.");
                }
            } else if (genericInstrumentationAgentAvailable() && !genericInstrumentationAgentConfigured()) {
                if (hardError()) {
                    throw new RuntimeException("The Spring Instrumentation Agent is on the classpath but is not properly configured as a jvm javaagent.");
                }
            }
        }
    }

    private String getTomcatServerNumber() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> serverInfo = Class.forName(TOMCAT_SERVER_INFO_CLASS_NAME);
        final Method getServerNumber = serverInfo.getDeclaredMethod(TOMCAT_GET_SERVER_NUMBER_METHOD);
        return (String) getServerNumber.invoke(null);
    }

    /**
     * Checks for the tomcat server class which would only be available if running on tomcat along with the version number.
     */
    protected boolean runningOnTomcat7() {
        try {
            Class.forName(TOMCAT_SERVER_CLASS_NAME);
            return getTomcatServerNumber().startsWith(TOMCAT_7_VERSION_PREFIX);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    /**
     * Checks for the tomcat server class which would only be available if running on tomcat along with the version number.
     */
    protected boolean runningOnTomcat8Or85() {
        try {
            Class.forName(TOMCAT_SERVER_CLASS_NAME);
            final String serverNumber = getTomcatServerNumber();
            return serverNumber.startsWith(TOMCAT_8_VERSION_PREFIX) || serverNumber.startsWith(TOMCAT_85_VERSION_PREFIX);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    /**
     * Checks for the tomcat server class which would only be available if running on tomcat embedded along with the
     * version number.  This indicates the embedded server in our integration tests.
     */
    protected boolean runningOnTomcat85FromJunit() {
        try {
            Class.forName(TOMCAT_SERVER_CLASS_NAME);
            Class.forName(INTEGRATION_TEST_CLASS);
            final String serverNumber = getTomcatServerNumber();
            return serverNumber.startsWith(TOMCAT_85_VERSION_PREFIX);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    /**
     * Checks if the Spring Instrumenting ClassLoader for Tomcat is available on the classpath.
     */
    protected boolean tomcatInstrumentingClassLoaderAvailable() {
        try {
            Class.forName(TOMCAT_INSTRUMENTATION_CLASS_LOADER_CLASS_NAME);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks if the Spring Instrumenting ClassLoader is in the ClassLoader chain. If so, this means
     * that it is properly configured.
     */
    protected boolean tomcatInstrumentingClassLoaderConfigured() {
        //generally this means that the tomcat context.xml file has tomcat ClassLoader configured.  If not then
        //something really strange is going on
        ClassLoader cl = this.getClass().getClassLoader();
        while (cl != null) {
            if (TOMCAT_INSTRUMENTATION_CLASS_LOADER_CLASS_NAME.equals(cl.getClass().getName())) {
                return true;
            }
            cl = cl.getParent();
        }
        return false;
    }

    /**
     * Checks if the Spring Instrumentation Agent is available on the classpath.
     */
    protected boolean genericInstrumentationAgentAvailable() {
        try {
            Class.forName(INSTRUMENTATION_AGENT_CLASS_NAME);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks if the Spring Instrumentation Agent has a non-null Instrumentation instance.  If so, this means
     * that it is properly configured.
     */
    protected boolean genericInstrumentationAgentConfigured() {
        try {
            Class<?> instrumentationSavingAgentClass = Class.forName(INSTRUMENTATION_AGENT_CLASS_NAME);
            Method getInstrumentation = instrumentationSavingAgentClass.getMethod(GET_INSTRUMENTATION_METHOD);
            return getInstrumentation.invoke(instrumentationSavingAgentClass) != null;
        } catch (ClassNotFoundException|InvocationTargetException|NoSuchMethodException|IllegalAccessException e) {
            return false;
        }
    }

    protected boolean oracleJpaLibraryAvailable() {
        try {
            Class.forName(ORACLE_CLASS_NAME);
            return true;
        } catch (ClassNotFoundException e) {
           return false;
        } catch (NoClassDefFoundError e) {
            //class is available but it cannot be initialized because of missing oracle drivers
            return true;
        }

    }

    protected boolean oraclePlatform() {
        final String serverPlatform = configurationService.getPropertyValueAsString(SERVER_DATASOURCE_PLATFORM_CFG_NM);
        final String clientPlatform = configurationService.getPropertyValueAsString(DATASOURCE_PLATFORM_CFG_NM);

        return (serverPlatform != null && serverPlatform.contains(ORACLE_PLATFORM_NM)) || (clientPlatform != null && clientPlatform.contains(ORACLE_PLATFORM_NM));
    }

    protected boolean hardError() {
        return configurationService.getPropertyValueAsBoolean(KC_CONFIG_VERIFIER_HARD_ERROR_CFG_NM);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
