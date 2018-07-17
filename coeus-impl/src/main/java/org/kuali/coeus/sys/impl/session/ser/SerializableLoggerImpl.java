/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.impl.session.ser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("serializableLogger")
public class SerializableLoggerImpl implements SerializableLogger {

    private Logger LOG = LogManager.getLogger(SerializableLoggerImpl.class);

    @Autowired
    @Qualifier("serializableKradFormService")
    private SerializableSessionAttributeService serializableKradFormService;

    @Autowired
    @Qualifier("serializableSessionAttributeService")
    private SerializableSessionAttributeService serializableSessionAttributeService;

    @Autowired
    @Qualifier("serializableUserSessionAttributeService")
    private SerializableSessionAttributeService serializableUserSessionAttributeService;

    @Override
    public void log() {
        if (LOG.isWarnEnabled()) {
            Stream.concat(Stream.concat(serializableSessionAttributeService.getAttributes().stream(),
                    serializableUserSessionAttributeService.getAttributes().stream()),
                    serializableKradFormService.getAttributes().stream())
                    .filter(attr -> !attr.isSerializable())
                    .forEach(attr ->  LOG.warn("Serialization failed: " + attr));
        }
    }

    public SerializableSessionAttributeService getSerializableKradFormService() {
        return serializableKradFormService;
    }

    public void setSerializableKradFormService(SerializableSessionAttributeService serializableKradFormService) {
        this.serializableKradFormService = serializableKradFormService;
    }

    public SerializableSessionAttributeService getSerializableSessionAttributeService() {
        return serializableSessionAttributeService;
    }

    public void setSerializableSessionAttributeService(SerializableSessionAttributeService serializableSessionAttributeService) {
        this.serializableSessionAttributeService = serializableSessionAttributeService;
    }

    public SerializableSessionAttributeService getSerializableUserSessionAttributeService() {
        return serializableUserSessionAttributeService;
    }

    public void setSerializableUserSessionAttributeService(SerializableSessionAttributeService serializableUserSessionAttributeService) {
        this.serializableUserSessionAttributeService = serializableUserSessionAttributeService;
    }
}
