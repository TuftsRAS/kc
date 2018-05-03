/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.sys.framework.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Utility class for working with Jaxb.
 */
public final class JaxbUtils {

    private JaxbUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    /**
     * Creates a nicely formatted String representation of a Jaxb generated object.
     * @param clazz the jaxb clazz of the object, if null will return an empty string.
     * @param o the object, if null will return an empty string
     * @throws JAXBException if unable to create a string
     */
    public static <T> String toString(Class<? extends T> clazz, T o) throws JAXBException {
        if (clazz == null || o == null) {
            return "";
        }

        final JAXBContext context = JAXBContext.newInstance(clazz);
        final Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final StringWriter sw = new StringWriter();
        m.marshal(o, sw);
        return sw.toString();
    }
}
