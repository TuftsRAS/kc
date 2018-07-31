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
    private static final String ORACLE_CLASS_NAME = "org.eclipse.persistence.platform.database.oracle.Oracle12Platform";
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
    private static final String LOG4J_TOMCAT_HELP = "Tomcat 8.5 detected but log4j 2 is not configured for Tomcat. See https://logging.apache.org/log4j/2.0/log4j-appserver/index.html";
    private static final String LOG4J_JUL_HELP = "JUL logging is not configured with log4j 2. See https://logging.apache.org/log4j/2.0/log4j-jul/index.html";
    private static final String TOMCAT_LOGGER = "org.apache.logging.log4j.appserver.tomcat.TomcatLogger";
    private static final String JULI_LOG_FACTORY = "org.apache.juli.logging.LogFactory";
    private static final String GET_LOG = "getLog";
    private static final String JUL_LOG_MANAGER = "org.apache.logging.log4j.jul.LogManager";

    @Autowired
    @Qualifier("kualiConfigurationService")
    private ConfigurationService configurationService;

    @Override
    public void afterPropertiesSet() {
        verifyOracleConfiguration();
        verifyInstrumentationConfiguration();
        verifyTomcatLogging();
        verifyJulLogging();
    }

    protected void verifyTomcatLogging() {
        if (runningOnTomcat85() && !usingLog4jTomcatLogger()) {
            if (hardError()) {
                throw new RuntimeException(LOG4J_TOMCAT_HELP);
            } else {
                LOG.fatal(LOG4J_TOMCAT_HELP);
            }
        }
    }

    protected void verifyJulLogging() {
        final java.util.logging.LogManager logManager = java.util.logging.LogManager.getLogManager();
        //only checking on Tomcat 8.5 for now but eventually should check regardless of app server
        if (runningOnTomcat85() && logManager != null && !JUL_LOG_MANAGER.equals(logManager.getClass().getName())) {
            if (hardError()) {
                throw new RuntimeException(LOG4J_JUL_HELP);
            } else {
                LOG.fatal(LOG4J_JUL_HELP);
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

    private boolean usingLog4jTomcatLogger() {
        try {
            final Class<?> logFactoryClass = Class.forName(JULI_LOG_FACTORY);
            final Method getLog = logFactoryClass.getDeclaredMethod(GET_LOG, Class.class);
            return TOMCAT_LOGGER.equals(getLog.invoke(logFactoryClass, KcConfigVerifier.class).getClass().getName());
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return false;
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
        return runningOnTomcat80() || runningOnTomcat85();
    }

    protected boolean runningOnTomcat80() {
        return runningOnTomcatVersion(TOMCAT_8_VERSION_PREFIX);
    }

    protected boolean runningOnTomcat85() {
        return runningOnTomcatVersion(TOMCAT_85_VERSION_PREFIX);
    }

    protected boolean runningOnTomcatVersion(String prefix) {
        try {
            Class.forName(TOMCAT_SERVER_CLASS_NAME);
            final String serverNumber = getTomcatServerNumber();
            return serverNumber.startsWith(prefix);
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
