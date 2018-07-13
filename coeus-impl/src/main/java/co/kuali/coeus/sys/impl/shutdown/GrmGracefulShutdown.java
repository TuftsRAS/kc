/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package co.kuali.coeus.sys.impl.shutdown;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.DisposableBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class GrmGracefulShutdown implements DisposableBean {

    private static final Logger LOG = LogManager.getLogger(GrmGracefulShutdown.class);

    @Override
    public void destroy() {
        LOG.info("Shutting down AWS IdleConnectionReaper thread ");
        try {
            Class<?> idleConnectionReaper = Class.forName("com.amazonaws.http.IdleConnectionReaper");
            Method shutdown = idleConnectionReaper.getDeclaredMethod("shutdown");
            shutdown.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
