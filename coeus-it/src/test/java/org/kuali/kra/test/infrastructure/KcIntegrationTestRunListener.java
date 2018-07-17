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
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.kuali.kra.test.infrastructure.lifecycle.KcIntegrationTestLifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the mechanism to shut down the persistent portions of lifecycles at the end of a full test run.
 */
public class KcIntegrationTestRunListener extends RunListener {
    private static final Logger LOG = LogManager.getLogger(KcIntegrationTestRunListener.class);
    
    KcIntegrationTestLifecycle lifecycle;
    List<Failure> assumptionFailures = new ArrayList<>();
    List<Failure> failures = new ArrayList<>();
    List<Description> ignoredTests = new ArrayList<>();
    
    public KcIntegrationTestRunListener(KcIntegrationTestLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        lifecycle.stopPerSuite();
        outputReport();
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        assumptionFailures.add(failure);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        failures.add(failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        ignoredTests.add(description);
    }
    
    private void outputReport() {
        if (LOG.isInfoEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append("Test Run Report:\n");
            builder.append("\nIgnored Tests:\n");
            if (ignoredTests.size() == 0) {
                builder.append("\tNo Ignored Tests\n");
            }
            else {
                for (Description description : ignoredTests) {
                    builder.append('\t').append(description.toString()).append('\n');
                }
            }
            builder.append("\nFailed Assumption Tests\n");
            if (assumptionFailures.size() == 0) {
                builder.append("\tNo Failed Assumption Tests\n");
            }
            else {
                builder.append("\nFailed Assumption Tests:\n");
                for (Failure failure : assumptionFailures) {
                    builder.append('\t').append(failure.toString()).append('\n');
                }
            }
            builder.append("\nFailed Tests:\n");
            if (failures.size() == 0) {
                builder.append("\tNo Failed Tests\n");
            }
            else {
                for (Failure failure : failures) {
                    builder.append('\t').append(failure.toString()).append('\n');
                }
            }
            LOG.info(builder.toString());
        }
    }
    
}
