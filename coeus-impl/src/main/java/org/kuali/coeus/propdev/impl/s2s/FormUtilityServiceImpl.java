/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;

import com.lowagie.text.pdf.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.coeus.s2sgen.api.core.InfastructureConstants;
import org.kuali.coeus.s2sgen.api.hash.GrantApplicationHashService;
import org.kuali.coeus.sys.api.model.KcFile;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;

import static org.kuali.coeus.propdev.impl.s2s.S2SXmlConstants.*;

@Component("formUtilityService")
public class FormUtilityServiceImpl implements FormUtilityService {

    private static final Log LOG = LogFactory.getLog(FormUtilityServiceImpl.class);

    private static final String DUPLICATE_FILE_NAMES = "Attachments contain duplicate file names";

    @Autowired
    @Qualifier("businessObjectService")
    private BusinessObjectService businessObjectService;

    @Autowired
    @Qualifier("grantApplicationHashService")
    private GrantApplicationHashService grantApplicationHashService;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, KcFile> extractAttachments(PdfReader reader) throws IOException {
        Map<String, KcFile> fileMap = new HashMap<>();

        PdfDictionary catalog = reader.getCatalog();
        PdfDictionary names = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.NAMES));
        if (names != null) {
            PdfDictionary embFiles = (PdfDictionary) PdfReader.getPdfObject(names.get(PdfName.EMBEDDEDFILES));
            if (embFiles != null) {
                HashMap<String, PdfObject> embMap = PdfNameTree.readTree(embFiles);

                for (PdfObject o : embMap.values()) {
                    PdfDictionary filespec = (PdfDictionary) PdfReader.getPdfObject(o);
                    final KcFile fileInfo = unpackFile(filespec);
                    if (fileInfo != null) {
                        if (!fileMap.containsKey(fileInfo.getName())) {
                            fileMap.put(fileInfo.getName(), fileInfo);
                        }
                    }
                }
            }
        }

        for (int k = 1; k <= reader.getNumberOfPages(); ++k) {
            PdfArray annots = (PdfArray) PdfReader.getPdfObject(reader.getPageN(k).get(PdfName.ANNOTS));
            if (annots == null) {
                continue;
            }
            for (Iterator i = annots.listIterator(); i.hasNext(); ) {
                PdfDictionary annot = (PdfDictionary) PdfReader.getPdfObject((PdfObject) i.next());
                PdfName subType = (PdfName) PdfReader.getPdfObject(annot.get(PdfName.SUBTYPE));
                if (!PdfName.FILEATTACHMENT.equals(subType)) {
                    continue;
                }
                PdfDictionary filespec = (PdfDictionary) PdfReader.getPdfObject(annot.get(PdfName.FS));
                final KcFile fileInfo = unpackFile(filespec);
                if (fileInfo != null) {
                    if (!fileMap.containsKey(fileInfo.getName())) {
                        fileMap.put(fileInfo.getName(), fileInfo);
                    } else {
                        throw new RuntimeException(DUPLICATE_FILE_NAMES);
                    }
                }
            }
        }

        return fileMap;
    }

    /**
     * Unpacks a file attachment.
     *
     * @param filespec The dictionary containing the file specifications
     */
    private KcFile unpackFile(PdfDictionary filespec) throws IOException {

        if (filespec == null)
            return null;

        PdfName type = (PdfName) PdfReader.getPdfObject(filespec.get(PdfName.TYPE));

        if (!PdfName.F.equals(type) && !PdfName.FILESPEC.equals(type))
            return null;

        PdfDictionary ef = (PdfDictionary) PdfReader.getPdfObject(filespec.get(PdfName.EF));
        if (ef == null)
            return null;

        PdfString fn = (PdfString) PdfReader.getPdfObject(filespec.get(PdfName.F));
        if (fn == null)
            return null;

        PdfString st = (PdfString) PdfReader.getPdfObject(filespec.get(PdfName.SUBTYPE));

        File fLast = new File(fn.toUnicodeString());

        PRStream prs = (PRStream) PdfReader.getPdfObject(ef.get(PdfName.F));
        if (prs == null)
            return null;

        byte attachmentByte[] = PdfReader.getStreamBytes(prs);
        return new S2sFile(fLast.getName(), st != null ? st.toUnicodeString() : InfastructureConstants.CONTENT_TYPE_OCTET_STREAM, attachmentByte);
    }

    @Override
    public Document node2Dom(org.w3c.dom.Node n) throws TransformerException {
        javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer xf = tf.newTransformer();
        javax.xml.transform.dom.DOMResult dr = new javax.xml.transform.dom.DOMResult();
        xf.transform(new javax.xml.transform.dom.DOMSource(n), dr);
        return (Document) dr.getNode();
    }

    @Override
    public void reorderXmlElements(Document doc) {
        Collection<UserAttachedFormsXMLReorder> userAttachedFormsXmlReorders = getBusinessObjectService().findAll(UserAttachedFormsXMLReorder.class);
        if (userAttachedFormsXmlReorders != null) {
            userAttachedFormsXmlReorders.forEach(userAttachedFormsXMLReorder -> {
                try {
                    reorderXmlElement(doc, userAttachedFormsXMLReorder.getNodeToMove(), userAttachedFormsXMLReorder.getTargetNode(), userAttachedFormsXMLReorder.isMoveAfter());
                } catch (Exception e) {
                    LOG.error("Could not move XML node " + userAttachedFormsXMLReorder.getNodeToMove() + "next to " + userAttachedFormsXMLReorder.getTargetNode(), e);
                }
            });
        }
    }

    protected void reorderXmlElement(Document doc, String original, String target, boolean insertAfter) {
        final NodeList originalNodes = doc.getElementsByTagName(original);
        if (originalNodes != null && originalNodes.getLength() > 0) {
            final Node originalNode = originalNodes.item(0);
            final NodeList targetNodes = doc.getElementsByTagName(target);
            if (targetNodes != null && targetNodes.getLength() > 0) {
                if (insertAfter) {
                    moveNode(originalNode, targetNodes.item(0).getNextSibling());
                } else {
                    moveNode(originalNode, targetNodes.item(0));
                }
            }
        }
    }

    private void moveNode(Node original, Node target) {
        final Node copy = original.cloneNode(true);
        final Node parent = target.getParentNode();
        parent.insertBefore(copy, target);
        parent.removeChild(original);
    }

    @Override
    public void removeAllEmptyNodes(Document document, String xpath, int parentLevel) throws XPathExpressionException {
        NodeList emptyElements = (NodeList) XPathFactory.newInstance().newXPath().evaluate(xpath, document, XPathConstants.NODESET);
        for (int i = emptyElements.getLength() - 1; i > -1; i--) {
            Node nodeToBeRemoved = emptyElements.item(i);
            int hierLevel = parentLevel;
            while (hierLevel-- > 0) {
                nodeToBeRemoved = nodeToBeRemoved.getParentNode();
            }
            nodeToBeRemoved.getParentNode().removeChild(nodeToBeRemoved);
        }
        NodeList moreEmptyElements = (NodeList) XPathFactory.newInstance().newXPath().evaluate(xpath, document, XPathConstants.NODESET);
        if (moreEmptyElements.getLength() > 0) {
            removeAllEmptyNodes(document, xpath, parentLevel);
        }
    }

    @Override
    public void correctAttachmentXml(Document document, Map<String, KcFile> atts) {
        final NodeList attachments = document.getElementsByTagNameNS(ATTACHMENTS_NS, ATTACHED_FILE);
        if (attachments != null) {
           for (int i = 0; i < attachments.getLength(); i++) {
               final Node attachment = attachments.item(i);
               correctAttachmentXml(document, attachment, atts);
           }
        }

        //not all AttachedFile elements use the Attachments Namespace.  In some cases they inherit from the namespace.
        //In these cases, start from the FileName and go to the parent to get the attachment.
        final NodeList fileNames = document.getElementsByTagNameNS(ATTACHMENTS_NS, FILE_NAME);
        if (fileNames != null) {
            for (int i = 0; i < fileNames.getLength(); i++) {
                final Node fileName = fileNames.item(i);
                final Node attachment = fileName.getParentNode();
                correctAttachmentXml(document, attachment, atts);
            }
        }
    }

    protected void correctAttachmentXml(Document document, Node attachment, Map<String, KcFile> atts) {
        if (attachment != null) {
            final NodeList attachmentElements = attachment.getChildNodes();
            if (attachmentElements != null) {
                Node fileName = null;
                Node fileLocation = null;
                Node hashValue = null;
                Node mimeType = null;
                for (int j = 0; j < attachmentElements.getLength(); j++) {
                    final Node node = attachmentElements.item(j);
                    if (FILE_NAME.equals(node.getLocalName()) && ATTACHMENTS_NS.equals(node.getNamespaceURI())) {
                        fileName = node;
                    } else if (FILE_LOCATION.equals(node.getLocalName()) && ATTACHMENTS_NS.equals(node.getNamespaceURI())) {
                        fileLocation = node;
                    } else if (MIME_TYPE.equals(node.getLocalName()) && ATTACHMENTS_NS.equals(node.getNamespaceURI())) {
                        mimeType = node;
                    } else if (HASH_VALUE.equals(node.getLocalName()) && GLOBAL_NS.equals(node.getNamespaceURI())) {
                        hashValue = node;
                    }
                }

                if (fileLocation == null) {
                    fileLocation = document.createElementNS(ATTACHMENTS_NS, FILE_LOCATION);
                    attachment.appendChild(fileLocation);
                }

                if (fileName != null) {
                    final Node location = fileLocation.getAttributes().getNamedItemNS(ATTACHMENTS_NS, HREF);
                    if (location == null || !StringUtils.equals(fileName.getTextContent(), location.getTextContent())) {
                        final Attr href = document.createAttributeNS(ATTACHMENTS_NS, HREF);
                        href.setValue(fileName.getTextContent());
                        ((Element) fileLocation).setAttributeNode(href);
                    }
                }

                if (mimeType == null) {
                    mimeType = document.createElementNS(ATTACHMENTS_NS, MIME_TYPE);
                    attachment.appendChild(mimeType);
                }

                final String mime = mimeType.getTextContent();
                if (StringUtils.isBlank(mime) && fileName != null && StringUtils.isNotBlank(fileName.getTextContent()) && atts.containsKey(fileName.getTextContent())) {
                    mimeType.setTextContent(atts.get(fileName.getTextContent()).getType());
                }

                if (hashValue == null) {
                    hashValue = document.createElementNS(GLOBAL_NS, HASH_VALUE);
                    attachment.appendChild(hashValue);
                }

                final Attr hashAlgorithm = (Attr) hashValue.getAttributes().getNamedItemNS(GLOBAL_NS, HASH_ALGORITHM);
                if (hashAlgorithm == null || StringUtils.isBlank(hashAlgorithm.getValue())) {
                    final Attr newHashAlgorithm = document.createAttributeNS(GLOBAL_NS, HASH_ALGORITHM);
                    newHashAlgorithm.setValue(InfastructureConstants.HASH_ALGORITHM);
                    ((Element) hashValue).setAttributeNode(newHashAlgorithm);
                }

                final String hash = hashValue.getTextContent();
                if (StringUtils.isBlank(hash) && fileName != null && StringUtils.isNotBlank(fileName.getTextContent()) && atts.containsKey(fileName.getTextContent())) {
                    hashValue.setTextContent(getGrantApplicationHashService().computeAttachmentHash(atts.get(fileName.getTextContent()).getData()));
                }
            }
        }
    }

    /**
     * This method convert Document to a String
     *
     * @param node {Document} node entry.
     * @return String containing doc information
     */
    @Override
    public String docToString(Document node) throws TransformerException {
        DOMSource domSource = new DOMSource(node);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    @Override
    public DocumentBuilder createDomBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory domParserFactory = DocumentBuilderFactory.newInstance();
        domParserFactory.setNamespaceAware(true);
        DocumentBuilder domParser = domParserFactory.newDocumentBuilder();
        domParserFactory.setIgnoringElementContentWhitespace(true);
        return domParser;
    }

    @Override
    public Node getHashValueFromParent(Node parent) {
        final NodeList childElements = parent.getChildNodes();
        for (int j = 0; j < childElements.getLength(); j++) {
            final Node childElement = childElements.item(j);
            if (HASH_VALUE.equals(childElement.getLocalName()) && GLOBAL_NS.equals(childElement.getNamespaceURI())) {
                return childElement;
            }
        }
        return null;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public GrantApplicationHashService getGrantApplicationHashService() {
        return grantApplicationHashService;
    }

    public void setGrantApplicationHashService(GrantApplicationHashService grantApplicationHashService) {
        this.grantApplicationHashService = grantApplicationHashService;
    }

    private static class S2sFile implements KcFile, Serializable {

        private String name;

        private String type;

        private byte[] data;

        public S2sFile() {
            super();
        }

        public S2sFile(String name, String type, byte[] data) {
            this.name = name;
            this.type = type;
            this.data = data;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            S2sFile s2sFile = (S2sFile) o;
            return Objects.equals(name, s2sFile.name) &&
                    Objects.equals(type, s2sFile.type) &&
                    Arrays.equals(data, s2sFile.data);
        }

        @Override
        public int hashCode() {

            int result = Objects.hash(name, type);
            result = 31 * result + Arrays.hashCode(data);
            return result;
        }

        @Override
        public String toString() {
            return "S2sFile{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", data=" + Arrays.toString(data) +
                    '}';
        }
    }
}
