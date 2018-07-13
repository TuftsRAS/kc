/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.framework.nonpersonnel;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class BudgetJustificationWrapper implements Serializable {
    private String justificationText;        
    private String lastUpdateTime;
    private String lastUpdateUser;        
    private static final Logger LOG = LogManager.getLogger(BudgetJustificationWrapper.class);
    
    public BudgetJustificationWrapper(String budgetJustificationAsXML) {
        super();
        parse(budgetJustificationAsXML);
    }
    
    public BudgetJustificationWrapper(Date lastUpdateTime, String lastUpdateUser, String justificationText) {
        super();
        this.justificationText = justificationText;
        this.lastUpdateUser = lastUpdateUser;
        setLastUpdateTime(lastUpdateTime);
    }
    
    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getJustificationText() {
        return justificationText;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    public void setLastUpdateTime(Date lastUpdateTime) {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        this.lastUpdateTime = formatter.format(lastUpdateTime);
    }

    public void setJustificationText(String justificationText) {
        this.justificationText = justificationText;
    }
    
    @Override
    public String toString() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        try (Writer out = new StringWriter()) {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            Element rootElement = document.createElement("budgetJustification");
            rootElement.setAttribute("lastUpdateBy", lastUpdateUser);
            rootElement.setAttribute("lastUpdateOn", lastUpdateTime);
            document.appendChild(rootElement);

            CDATASection cdata = document.createCDATASection(justificationText == null ? "" : justificationText);
            rootElement.appendChild(cdata);

            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(new DOMSource(document), new StreamResult(out));
            return out.toString();
        } catch (ParserConfigurationException|TransformerException|IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
    * This method parses the raw budgetJustification String into the fields needed for this class
    * Expected format:
    * &lt;budgetJustification lastUpdateBy="" lastUpdateOn=""&gt;
    *   &lt;![CDATA[
    *     Justification text
    *   ]]&gt;
    * &lt;/justification&gt;
    */
    private void parse(String budgetJustificationAsXML) {
        if (budgetJustificationAsXML == null || budgetJustificationAsXML.trim().length() == 0) {
            return;
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try (InputStream is = new ByteArrayInputStream(budgetJustificationAsXML.getBytes("utf-8"))) {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new InputSource(is));
            Element node = document.getDocumentElement();
            lastUpdateUser = node.getAttribute("lastUpdateBy");
            lastUpdateTime = node.getAttribute("lastUpdateOn");
            justificationText = node.getTextContent();
        } catch (ParserConfigurationException|SAXException|IOException e) {
            LOG.warn("Unable to parse budget justification XML.", e);
        }
    }
}
