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
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.resourceloader.ResourceLoader;

import java.util.Collection;

/**
 * A lifecycle for running a Application web server.
 */
public class ApplicationServerLifecycle implements Lifecycle {
    private static final Logger LOG = LogManager.getLogger(ApplicationServerLifecycle.class);

    private boolean started;
	private final ApplicationServer server;

	public ApplicationServerLifecycle(int port, String contextName, String relativeWebappRoot, Collection<String> relativeExtraResourceDirs) {
        server = new ApplicationServer(port, contextName, relativeWebappRoot, relativeExtraResourceDirs);
	}

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void start() throws Exception {
        server.start();

	    final ClassLoader webappClassLoader = server.getContextClassLoader();
        final ResourceLoader rl = GlobalResourceLoader.getResourceLoader(webappClassLoader);
        if (rl == null) {
            throw new RuntimeException("Could not find resource loader for test harness web app for: " + webappClassLoader);
        }
        GlobalResourceLoader.addResourceLoader(rl);
	    final Config webappConfig = ConfigContext.getConfig(webappClassLoader);

        final Config curCtxConfig = ConfigContext.getCurrentContextConfig();
        if (webappConfig != null) {
            curCtxConfig.putProperties(webappConfig.getProperties());
            curCtxConfig.putObjects(webappConfig.getObjects());
        }

        started = true;
    }

    @Override
    public void stop() throws Exception {
	    LOG.info("Shutting down Application Server: " + server);
        try {
	    	if (server.isStarted()) {
                server.stop();
            }
        } catch (Exception e) {
	        LOG.error("Error shutting down Application Server " + server.getContextName(), e);
        }
        started = false;
    }
}
