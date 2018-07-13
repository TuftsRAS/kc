/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.specialreview;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.XfaForm;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.propdev.impl.s2s.FormUtilityService;
import org.kuali.coeus.s2sgen.api.core.S2SException;
import org.kuali.coeus.sys.api.model.KcFile;
import org.kuali.coeus.sys.framework.util.CollectionUtils;
import org.kuali.kra.infrastructure.KeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("proposalSpecialReviewHumanSubjectsAttachmentService")
public class ProposalSpecialReviewHumanSubjectsAttachmentServiceImpl implements ProposalSpecialReviewHumanSubjectsAttachmentService {

    private static final String EMPTY_NODES = "//*[not(node()) and local-name(.) != 'FileLocation' and local-name(.) != 'HashValue' and local-name(.) != 'FileName']";
    private static final String OTHER_PERS = "//*[local-name(.)='ProjectRole' and local-name(../../.)='OtherPersonnel' and count(../NumberOfPersonnel)=0]";
    private static final String MESSAGE = "The pdf form does not contain any data.";
    private static final String UPLOADED_FILE_IS_EMPTY = "Uploaded file is empty";
    private static final String XFA_NS = "http://www.xfa.org/schema/xfa-data/1.0/";

    private static Logger LOG = LogManager.getLogger(ProposalSpecialReviewHumanSubjectsAttachmentServiceImpl.class);

    @Autowired
    @Qualifier("formUtilityService")
    private FormUtilityService formUtilityService;

    @Override
    public Map<String, Object> getSpecialReviewAttachmentXmlFileData(byte pdfFileContents[]) {
        String xml;
        List<KcFile> attachments;
        Map<String, Object> fileData = new HashMap<>();
        PdfReader reader = null;
        try {
            if (pdfFileContents == null || pdfFileContents.length == 0) {
                throw new S2SException(KeyConstants.S2S_USER_ATTACHED_FORM_EMPTY, UPLOADED_FILE_IS_EMPTY);
            } else {
                reader = new PdfReader(pdfFileContents);

                attachments = formUtilityService.extractAttachments(reader);
                final Collection<String> duplicates = CollectionUtils.findDuplicates(attachments, KcFile::getName);

                if (!duplicates.isEmpty()) {
                    S2SException s2sException = new S2SException();
                    s2sException.setErrorKey(KeyConstants.S2S_FORM_DUP_ATT);
                    s2sException.setParams(new String[] {"PHS_HumanSubjectsAndClinicalTrialsInfo_V1.0", duplicates.stream().collect(Collectors.joining(", "))});
                    throw s2sException;
                }

                fileData.put(FILES, attachments);
                XfaForm xfaForm = reader.getAcroFields().getXfa();
                Node domDocument = xfaForm.getDomDocument();
                if (domDocument == null) {
                    throw new S2SException(KeyConstants.S2S_USER_ATTACHED_FORM_NOT_FILLED, MESSAGE);
                }

                final Element documentElement = ((Document) domDocument).getDocumentElement();
                if (documentElement == null) {
                    throw new S2SException(KeyConstants.S2S_USER_ATTACHED_FORM_NOT_FILLED, MESSAGE);
                }

                final Element datasetsElement = (Element) documentElement.getElementsByTagNameNS(XFA_NS, "datasets").item(0);
                if (datasetsElement == null) {
                    throw new S2SException(KeyConstants.S2S_USER_ATTACHED_FORM_NOT_FILLED, MESSAGE);
                }
                final Element dataElement = (Element) datasetsElement.getElementsByTagNameNS(XFA_NS, "data").item(0);
                if (dataElement == null) {
                    throw new S2SException(KeyConstants.S2S_USER_ATTACHED_FORM_NOT_FILLED, MESSAGE);
                }
                final Element grantApplicationElement = (Element) dataElement.getChildNodes().item(0);

                if (grantApplicationElement == null) {
                    throw new S2SException(KeyConstants.S2S_USER_ATTACHED_FORM_NOT_FILLED, MESSAGE);
                }

                byte[] serializedXML = XfaForm.serializeDoc(grantApplicationElement);
                DocumentBuilderFactory domParserFactory = DocumentBuilderFactory.newInstance();
                domParserFactory.setNamespaceAware(true);
                javax.xml.parsers.DocumentBuilder domParser = domParserFactory.newDocumentBuilder();
                domParserFactory.setIgnoringElementContentWhitespace(true);

                final org.w3c.dom.Document document;
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedXML);
                document = domParser.parse(byteArrayInputStream);
                if (document != null) {
                    Element form;
                    form = document.getDocumentElement();
                    xml = processForm(form, attachments);
                    fileData.put(CONTENT, xml);
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException exception) {
            fileData = null;
            LOG.error("Cannot parse attachment." + exception.getMessage(), exception);
        } finally {
            if (reader != null) reader.close();
        }

        return fileData;
    }


    private String processForm(Element form, List<KcFile> attachments) throws TransformerException, XPathExpressionException {

        String formXML;
        Document doc = formUtilityService.node2Dom(form);
        formUtilityService.correctAttachmentXml(doc, attachments);
        formUtilityService.removeAllEmptyNodes(doc, EMPTY_NODES, 0);
        formUtilityService.removeAllEmptyNodes(doc, OTHER_PERS, 1);
        formUtilityService.removeAllEmptyNodes(doc, EMPTY_NODES, 0);
        formUtilityService.reorderXmlElements(doc);
        formXML = formUtilityService.docToString(doc);
        return formXML;
    }

    public FormUtilityService getFormUtilityService() {
        return formUtilityService;
    }

    public void setFormUtilityService(FormUtilityService formUtilityService) {
        this.formUtilityService = formUtilityService;
    }
}
