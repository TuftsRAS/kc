/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.propdev.impl.s2s.override;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

final class XmlUtils {

    static final String ATTACHMENTS_NS = "http://apply.grants.gov/system/Attachments-V1.0";
    static final String FILE_LOCATION = "FileLocation";
    static final String HREF = "href";
    static final String HASH_VALUE = "HashValue";
    static final String GLOBAL_NS = "http://apply.grants.gov/system/Global-V1.0";
    static final String HEADER_NS = "http://apply.grants.gov/system/Header-V1.0";
    static final String GRANT_SUBMISSION_HEADER = "GrantSubmissionHeader";

    private XmlUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    static DocumentBuilder createDomBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory domParserFactory = DocumentBuilderFactory.newInstance();
        domParserFactory.setNamespaceAware(true);
        DocumentBuilder domParser = domParserFactory.newDocumentBuilder();
        domParserFactory.setIgnoringElementContentWhitespace(true);
        return domParser;
    }

    static String getHashValueFromParent(Node parent) {
        final NodeList childElements = parent.getChildNodes();
        for (int j = 0; j < childElements.getLength(); j++) {
            final Node childElement = childElements.item(j);
            if (HASH_VALUE.equals(childElement.getLocalName()) && GLOBAL_NS.equals(childElement.getNamespaceURI())) {
                return childElement.getTextContent();
            }
        }
        return null;
    }
}
