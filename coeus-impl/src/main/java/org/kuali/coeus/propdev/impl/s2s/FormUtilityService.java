/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;

import com.lowagie.text.pdf.PdfReader;
import org.kuali.coeus.sys.api.model.KcFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Map;

public interface FormUtilityService {
    Map<String, KcFile> extractAttachments(PdfReader reader) throws IOException;

    Document node2Dom(Node n) throws TransformerException;

    String docToString(Document node) throws TransformerException;

    void reorderXmlElements(Document doc);

    void removeAllEmptyNodes(Document document, String xpath, int parentLevel) throws XPathExpressionException;

    /**
     * For each AttachedFile element in the Document, this method makes sure that:
     * 1) the FileLocation element exists and the href attribute matches the FileName element's value
     * 2) the MimeType element exists with a value
     * 3) the HashValue element exists with a hashAlgorithm of SHA-1 and the HashValue element's value is not blank.
     *
     * @param document the Document representing the grants.gov form which could be altered by this method
     * @param attachments the attachment map to use for generating attachment data on the xml Document
     */
    void correctAttachmentXml(Document document, Map<String, KcFile> attachments);

    DocumentBuilder createDomBuilder() throws ParserConfigurationException;

    Node getHashValueFromParent(Node parent);
}
