/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.impl.session.ser;


import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SerializableSessionListener implements HttpSessionAttributeListener {

    private static final Logger LOG = LogManager.getLogger(SerializableSessionListener.class);

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (LOG.isWarnEnabled()) {
            final SerializableUtils.SerInfo info = SerializableUtils.getSerializationInfo(event.getValue());
            if (!info.isSerializable()) {
                LOG.warn("Serialization failed: Attribute added to session " + getMessage(event, info));
            }
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        //no op
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (LOG.isWarnEnabled()) {
            final SerializableUtils.SerInfo info = SerializableUtils.getSerializationInfo(event.getValue());
            if (!info.isSerializable()) {
                LOG.warn("Serialization failed: Attribute replaced in session " + getMessage(event, info));
            }
        }
    }

    private String getMessage(HttpSessionBindingEvent event, SerializableUtils.SerInfo info) {
        return "name: " + event.getName() + ", type: " + event.getValue().getClass().getName() +
                ", value: " + event.getValue().toString() + ", source: " + event.getSource().getClass().getName() +
                ", failure path: " + info.getFailurePath()
                + " is not Serializable. Location of violation:\n" + getStackTrace();
    }

    private static String getStackTrace() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .map(elem -> "\tat " + elem + SystemUtils.LINE_SEPARATOR)
                .collect(Collectors.joining());
    }
}
