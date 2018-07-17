/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.test.infrastructure;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.kuali.kra.test.infrastructure.lifecycle.KcIntegrationTestMainLifecycle;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.MessageMap;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class serves as a base test class for all KC unit tests. It handles ensuring all of the necessary lifecycles are properly
 * launched, started and stopped.
 */
@RunWith(KcIntegrationTestRunner.class)
public class KcIntegrationTestBase implements KcIntegrationTestMethodAware {
    // non static Log to allow it to be named after the runtime class
    protected final Logger LOG = LogManager.getLogger(this.getClass());

    private static final KcIntegrationTestMainLifecycle LIFECYCLE = new KcIntegrationTestMainLifecycle();
    private static final RunListener RUN_LISTENER = new KcIntegrationTestRunListener(LIFECYCLE);
    private static final String DEFAULT_USER = "quickstart";
    private static final String MEM_STAT_FORMAT = "[%1$-7s] total: %2$10d, free: %3$10d";

    private long startTime;
    private long totalMem;
    private long freeMem;
    
    private String method;
    
    /**
     * This method executes before each unit test and ensures the necessary lifecycles have been started.
     */
    @Before
    public final void baseBeforeTest() {
        logBeforeRun();
        LIFECYCLE.startPerTest(true);
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setAuditErrorMap(new HashMap<>());
        final UserSession userSession = new UserSession(DEFAULT_USER);
        userSession.setKualiSessionId(UUID.randomUUID().toString());
        GlobalVariables.setUserSession(userSession);

    }

    /**
     * This method executes after each unit test and makes sure the necessary lifecycles have been stopped
     */
    @After
    public final void baseAfterTest() {
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setAuditErrorMap(new HashMap<>());
        GlobalVariables.setUserSession(null);
        LIFECYCLE.stopPerTest();
        logAfterRun();
    }
    
    @BeforeClass
    public static void baseBeforeClass() {
        if (!LIFECYCLE.isPerSuiteStarted()) {
            LIFECYCLE.startPerSuite();
        }
        LIFECYCLE.startPerClass();
    }
    
    @AfterClass
    public static void baseAfterClass() {
        LIFECYCLE.stopPerClass();
    }

    /**
     * This method returns the <code>RunListener</code> needed to ensure the KC persistent lifecycles shut down properly
     * @return the RunListener responsible for shutting down all KC persistent lifecycles
     */
    static RunListener getRunListener() {
        return RUN_LISTENER;
    }

    /**
     * This method is called by the <code>KcIntegrationTestRunner</code> and passes the method being called so the required lifecycles can
     * be determined.
     * 
     * @param method the <code>Method</code> being called by the current test
     * 
     * @see KcIntegrationTestMethodAware#setTestMethod(java.lang.String)
     */
    @Override
    public final void setTestMethod(String method) {
        this.method = method;
    }
    
    private void logBeforeRun() {
        ThreadContext.put("testName", getFullTestName());
        if (LOG.isInfoEnabled()) {
            statsBegin();
            LOG.info("##############################################################");
            LOG.info("# Starting test " + getFullTestName() + "...");
            LOG.info("##############################################################");
        }
    }

    private void logAfterRun() {
        if (LOG.isInfoEnabled()) {
            LOG.info("##############################################################");
            LOG.info("# ...finished test " + getFullTestName());
            for (String stat : statsEnd()) {
                LOG.info("# " + stat);
            }
            LOG.info("##############################################################");
        }
        ThreadContext.remove("testName");
    }
    
    private void statsBegin() {
        startTime = System.currentTimeMillis();
        totalMem = Runtime.getRuntime().totalMemory();
        freeMem = Runtime.getRuntime().freeMemory();
    }

    private String[] statsEnd() {
        long currentTime = System.currentTimeMillis();
        long currentTotalMem = Runtime.getRuntime().totalMemory();
        long currentFreeMem = Runtime.getRuntime().freeMemory();
        return new String[]{
                String.format(MEM_STAT_FORMAT, "MemPre", totalMem, freeMem),
                String.format(MEM_STAT_FORMAT, "MemPost", currentTotalMem, currentFreeMem),
                String.format(MEM_STAT_FORMAT, "MemDiff", totalMem-currentTotalMem, freeMem-currentFreeMem),
                String.format("[ElapsedTime] %1$d ms", currentTime-startTime)
        };
    }
    
    private String getFullTestName() {
        return getClass().getSimpleName() + "." + (method != null ? method : "UNKNOWN");
    }

    protected void updateParameterForTesting(Class componentClass, String parameterName, String newValue) {
        final ParameterService parameterService = CoreFrameworkServiceLocator.getParameterService();
        updateParameter(parameterService.getParameter(componentClass, parameterName), newValue);
    }

    protected void updateParameterForTesting(String namespaceCode, String componentCode, String parameterName, String newValue) {
        final ParameterService parameterService = CoreFrameworkServiceLocator.getParameterService();
        updateParameter(parameterService.getParameter(namespaceCode, componentCode, parameterName), newValue);
    }

    private void updateParameter(Parameter parameter, String newValue) {
        final ParameterService parameterService = CoreFrameworkServiceLocator.getParameterService();

        final Parameter.Builder parameterForUpdate = Parameter.Builder.create(parameter);
        parameterForUpdate.setValue(newValue);
        parameterService.updateParameter(parameterForUpdate.build());
    }
}
