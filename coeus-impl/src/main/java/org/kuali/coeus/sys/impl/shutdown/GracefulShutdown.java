/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.sys.impl.shutdown;

import co.kuali.coeus.sys.impl.shutdown.GrmGracefulShutdown;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;


@Component("gracefulShutdown")
public class GracefulShutdown implements DisposableBean {

    private static final Logger LOG = LogManager.getLogger(GrmGracefulShutdown.class);

    @Override
    public void destroy() throws Exception {
        try {
            Class<?> abandonedConnectionCleanupThread = Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
            Method shutdown = abandonedConnectionCleanupThread.getDeclaredMethod("shutdown");
            shutdown.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.info(e);
        }

        Collections.list(java.sql.DriverManager.getDrivers()).forEach(driver -> {
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                LOG.info(e);
            }
        });
    }
}
