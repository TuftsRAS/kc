/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.test.infrastructure.lifecycle;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.rice.core.api.config.property.ConfigContext;

/**
 * An abstract lifecycle.
 * 
 * This abstract lifecyle does the following:
 * <ul>
 * <li>wraps start and stop exceptions</li>
 * <li>logs lifecycle events</li>
 * <li>calculates and logs the amount of time a lifecycle stage takes</li>
 * <li>simplifies the implementation of a lifecycle through templating</li>
 * <li>does not allow lifecycle stages to be called at incorrect times (twice without the opposite in between)</li>
 * </ul>
 */
public abstract class KcIntegrationTestBaseLifecycle implements KcIntegrationTestLifecycle {
    
    public static final String BROWSER_PROTOCOL = "http";
    public static final String BROWSER_ADDRESS = "127.0.0.1";
    public static final String PORTAL_ADDRESS = "kc-dev";
    
    // non static Log to allow it to be named after the runtime class
    protected final Logger LOG = LogManager.getLogger(this.getClass());

    private boolean perTestStarted;
    private boolean perClassStarted;
    private boolean perSuiteStarted;

    private boolean failed;

    /**
     * Hook to execute "per test" start logic.
     * 
     * @throws Throwable just propagate any exceptions
     */
    protected abstract void doPerTestStart(boolean transactional) throws Throwable;

    /**
     * Hook to execute "per test" stop logic.
     * 
     * @throws Throwable just propagate any exceptions
     */
    protected abstract void doPerTestStop() throws Throwable;

    /**
     * Hook to execute "per class" start logic.
     * 
     * @throws Throwable just propagate any exceptions
     */
    protected abstract void doPerClassStart() throws Throwable;

    /**
     * Hook to execute "per class" stop logic.
     * 
     * @throws Throwable just propagate any exceptions
     */
    protected abstract void doPerClassStop() throws Throwable;

    /**
     * Hook to execute "per suite" start logic.
     * 
     * @throws Throwable just propagate any exceptions
     */
    protected abstract void doPerSuiteStart() throws Throwable;

    /**
     * Hook to execute "per suite" stop logic.
     * 
     * @throws Throwable just propagate any exceptions
     */
    protected abstract void doPerSuiteStop() throws Throwable;
    
    @Override
    public void startPerTest(boolean transactional) {
        if (this.failed) {
            throw new IllegalStateException("lifecycle previously failed");
        }

        if (this.perTestStarted) {
            throw new IllegalStateException("per test lifecycle already started");
        }

        final StopWatch watch = new StopWatch();

        if (LOG.isDebugEnabled()) {
            watch.start();
            LOG.debug("starting per test lifecycle");
        }

        try {
            doPerTestStart(transactional);
            perTestStarted = true;
        }
        catch (Throwable e) {
            perTestStarted = false;
            if (LOG.isErrorEnabled()) {
                LOG.error("per test lifecycle failed to start cleanly", e);
            }
            throw new KcLifecycleException(e);
        }

        if (LOG.isDebugEnabled()) {
            watch.stop();
            LOG.debug("per test lifecycle started in " + watch + " time");
        }
    }

    @Override
    public void stopPerTest() {
        if (this.failed) {
            throw new IllegalStateException("lifecycle previously failed");
        }

        if (!this.perTestStarted) {
            throw new IllegalStateException("per test lifecycle already stopped");
        }

        final StopWatch watch = new StopWatch();

        if (LOG.isDebugEnabled()) {
            watch.start();
            LOG.debug("stopping per test lifecycle");
        }

        try {
            doPerTestStop();
            perTestStarted = false;
        }
        catch (Throwable e) {
            perTestStarted = false;
            if (LOG.isErrorEnabled()) {
                LOG.error("per test lifecycle failed to stop cleanly", e);
            }
            throw new KcLifecycleException(e);
        }

        if (LOG.isDebugEnabled()) {
            watch.stop();
            LOG.debug("per test lifecycle stopped in " + watch + " time");
        }
    }

    @Override
    public final boolean isPerTestStarted() {
        return this.perTestStarted;
    }

    @Override
    public void startPerClass() {
        if (this.failed) {
            throw new IllegalStateException("lifecycle previously failed");
        }

        if (this.perClassStarted) {
            throw new IllegalStateException("per class lifecycle already started");
        }

        final StopWatch watch = new StopWatch();

        if (LOG.isDebugEnabled()) {
            watch.start();
            LOG.debug("starting per class lifecycle");
        }

        try {
            doPerClassStart();
            perClassStarted = true;
        }
        catch (Throwable e) {
            perClassStarted = false;
            if (LOG.isErrorEnabled()) {
                LOG.error("per class lifecycle failed to start cleanly", e);
            }
            throw new KcLifecycleException(e);
        }

        if (LOG.isDebugEnabled()) {
            watch.stop();
            LOG.debug("per class lifecycle started in " + watch + " time");
        }
    }

    @Override
    public void stopPerClass() {
        if (this.failed) {
            throw new IllegalStateException("lifecycle previously failed");
        }

        if (!this.perClassStarted) {
            throw new IllegalStateException("per class lifecycle already stopped");
        }

        final StopWatch watch = new StopWatch();

        if (LOG.isDebugEnabled()) {
            watch.start();
            LOG.debug("stopping per class lifecycle");
        }

        try {
            doPerClassStop();
            perClassStarted = false;
        }
        catch (Throwable e) {
            perClassStarted = false;
            if (LOG.isErrorEnabled()) {
                LOG.error("per class lifecycle failed to stop cleanly", e);
            }
            throw new KcLifecycleException(e);
        }

        if (LOG.isDebugEnabled()) {
            watch.stop();
            LOG.debug("per class lifecycle stopped in " + watch + " time");
        }
    }

    @Override
    public final boolean isPerClassStarted() {
        return this.perClassStarted;
    }

    @Override
    public void startPerSuite() {
        if (this.failed) {
            throw new IllegalStateException("lifecycle previously failed");
        }

        if (this.perSuiteStarted) {
            throw new IllegalStateException("per suite lifecycle already started");
        }

        final StopWatch watch = new StopWatch();

        if (LOG.isDebugEnabled()) {
            watch.start();
            LOG.debug("starting per suite lifecycle");
        }

        try {
            doPerSuiteStart();
            perSuiteStarted = true;
        }
        catch (Throwable e) {
            perSuiteStarted = false;
            failed = true;
            if (LOG.isErrorEnabled()) {
                LOG.error("per suite lifecycle failed to start cleanly", e);
            }
            throw new KcLifecycleException(e);
        }

        if (LOG.isDebugEnabled()) {
            watch.stop();
            LOG.debug("per suite lifecycle started in " + watch + " time");
        }
    }

    @Override
    public void stopPerSuite() {
        if (this.failed) {
            throw new IllegalStateException("lifecycle previously failed");
        }

        if (!this.perSuiteStarted) {
            throw new IllegalStateException("per suite lifecycle already stopped");
        }

        final StopWatch watch = new StopWatch();

        if (LOG.isDebugEnabled()) {
            watch.start();
            LOG.debug("stopping per suite lifecycle");
        }

        try {
            doPerSuiteStop();
            perSuiteStarted = false;
        }
        catch (Throwable e) {
            perSuiteStarted = false;
            failed = true;
            if (LOG.isErrorEnabled()) {
                LOG.error("per suite lifecycle failed to stop cleanly", e);
            }
            throw new KcLifecycleException(e);
        }

        if (LOG.isDebugEnabled()) {
            watch.stop();
            LOG.debug("per suite lifecycle stopped in " + watch + " time");
        }
    }

    @Override
    public final boolean isPerSuiteStarted() {
        return this.perSuiteStarted;
    }
    
    public static String getPort() {
        return ConfigContext.getCurrentContextConfig().getProperty("kns.test.port");
    }

    /** exception that wraps any lifecycle failures. */
    protected static class KcLifecycleException extends RuntimeException {

        private static final long serialVersionUID = 6680874845872733891L;

        public KcLifecycleException(Throwable t) {
            super(t);
        }
    }
}
