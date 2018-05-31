/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.test.infrastructure;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.kuali.rice.core.api.lifecycle.Lifecycle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class ApplicationServer implements Lifecycle {

    private static final String BASEDIR = "basedir";
    private static final String COEUS_IT = "coeus-it";
    private static final String USER_DIR = "user.dir";
    private static final String TARGET_TOMCAT_BASEDIR = "target/tomcat-basedir";

    private final int port;
	private final String contextName;
    private final String relativeWebappRoot;
	private final Collection<String> relativeExtraResourceDirs;
    private Tomcat server;
    private Context context;

    public ApplicationServer(int port, String contextName, String relativeWebappRoot, Collection<String> relativeExtraResourceDirs) {
        this.port = port;
        this.contextName = contextName;
        this.relativeWebappRoot = relativeWebappRoot;
        this.relativeExtraResourceDirs = new ArrayList<>(relativeExtraResourceDirs);
    }

    //need to expose the webapp's classloader
    //so rice resource loaders can be configured load
    //classes from the running webapp
	public ClassLoader getContextClassLoader() {
	    return context.getLoader().getClassLoader();
	}

    @Override
	public void start() throws Exception {
        setBaseDirSystemProperty();

        server = new Tomcat();
        server.setPort(port);
        server.setBaseDir(System.getProperty(BASEDIR) + TARGET_TOMCAT_BASEDIR);

        final String webappRoot = System.getProperty(BASEDIR) + File.separator + relativeWebappRoot;
        context = server.addWebapp(contextName, webappRoot);
        final StandardRoot resources = new StandardRoot(context);
        relativeExtraResourceDirs.stream()
                .map(root -> System.getProperty(BASEDIR) + File.separator + root)
                .forEach(dir -> resources.createWebResourceSet(WebResourceRoot.ResourceSetType.POST, "/", dir, null, "/"));
        context.setResources(resources);

        StandardJarScanner jarScanner = new StandardJarScanner();
        jarScanner.setScanManifest(true);
        context.setJarScanner(jarScanner);
        context.setAddWebinfClassesResources(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.stop();
            } catch (LifecycleException e) {
                throw new RuntimeException("Unable to stop tomcat", e);
            }
        }));

        server.start();
	}

    @Override
	public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Override
	public boolean isStarted() {
		return server != null && server.getServer().getState().equals(LifecycleState.STARTED);
	}

    private void setBaseDirSystemProperty() {
        if (System.getProperty(BASEDIR) == null) {
            System.setProperty(BASEDIR, System.getProperty(USER_DIR));
        }

        //hack for multi-module structure
        String baseDir = System.getProperty(BASEDIR);
        if (baseDir != null && baseDir.contains(COEUS_IT)) {
            System.setProperty(BASEDIR, baseDir.replace(COEUS_IT, ""));
        }
    }

    public int getPort() {
        return port;
    }

    public String getContextName() {
        return contextName;
    }

    public String getRelativeWebappRoot() {
        return relativeWebappRoot;
    }

    public Collection<String> getRelativeExtraResourceDirs() {
        return new ArrayList<>(relativeExtraResourceDirs);
    }

    @Override
    public String toString() {
	    return new ToStringBuilder(this).append("port", port)
	                                    .append("contextName", contextName)
                                        .append("relativeWebappRoot", relativeWebappRoot)
                                        .append("relativeExtraResourceDirs", relativeExtraResourceDirs)
	                                    .toString();
	}
}
