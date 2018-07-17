/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.paymentreports.awardreports.reporting.service;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.xmlbeans.XmlObject;
import org.kuali.coeus.common.framework.print.Printable;
import org.kuali.coeus.common.framework.print.PrintingException;
import org.kuali.coeus.common.framework.print.stream.xml.XmlStream;
import org.kuali.coeus.common.framework.print.watermark.Watermarkable;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.springframework.core.io.Resource;

import javax.xml.transform.Source;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class provides all the objects required for printing reports. It
 * provides methods for fetching XML generator {@link XmlStream},{@link org.kuali.coeus.sys.framework.model.KcTransactionalDocumentBase},
 * {@link Map} of parameters required for printing.
 **
 */
public class AwardReportTracking implements Printable,Cloneable {

    private XmlStream xmlStream;
    private KcPersistableBusinessObjectBase printableBusinessObject;
    private Map<String, Object> reportParameters;
    private Map<String, byte[]> attachments;
    private static final Logger LOG = LogManager.getLogger(AwardReportTracking.class);
    /**
     * @return the xmlStream
     */
    public XmlStream getXmlStream() {
        return xmlStream;
    }

    /**
     * @param xmlStream
     *            the xmlStream to set
     */
    public void setXmlStream(XmlStream xmlStream) {
        this.xmlStream = xmlStream;
    }

    /**
     * Fetches the {@link org.kuali.coeus.sys.framework.model.KcTransactionalDocumentBase}
     * 
     * @return {@link org.kuali.coeus.sys.framework.model.KcTransactionalDocumentBase} document
     */
    @Override
    public KcPersistableBusinessObjectBase getPrintableBusinessObject() {
        return printableBusinessObject;
    }

    /**
     * @param printableBusinessObject
     *            the document to set
     */
    public void setPrintableBusinessObject(KcPersistableBusinessObjectBase printableBusinessObject) {
        this.printableBusinessObject = printableBusinessObject;
    }

    /**
     * @return the reportParameters
     */
    public Map<String, Object> getReportParameters() {
        return reportParameters;
    }

    /**
     * @param reportParameters
     *            the reportParameters to set
     */
    public void setReportParameters(Map<String, Object> reportParameters) {
        this.reportParameters = reportParameters;
    }
    @Override
    public Map<String, byte[]> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, byte[]> attachments) {
        this.attachments = attachments;
    }

    protected byte[] getBytes(XmlObject xmlObject) {
        String xmlString = xmlObject.xmlText();
        return xmlString.getBytes();
    }

    /**
     * This method generates the XML that conforms to Delta Report XSD returns
     * it as {@link InputStream}
     * 
     * @return {@link InputStream} of generated XML
     * @throws PrintingException
     *             in case of any errors occur during XML generation
     */
    @Override
    public Map<String, XmlObject> renderXML() throws PrintingException {
        return getXmlStream().generateXmlStream(
                getPrintableBusinessObject(), getReportParameters());
    }
    
    /**
     * This method should be overridden if any printable artifacts wants to send Templates with separate bookmarks.
     */
    @Override
    public Map<String,Source> getXSLTemplateWithBookmarks(){
        return null;
    }

    @Override
    public Map<String, Resource> getPdfForms() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, byte[]> fillPdfForms(Map<String, Resource> pdfForms, Map<String, XmlObject> xml) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, byte[]> sortPdfForms(Map<String, byte[]> forms) {
        return forms;
    }

    @Override
    public List<Source> getXSLTemplates(){
        return null;
    }
    /**
     * This method for checking watermark is enable or disable
     * @see org.kuali.coeus.common.framework.print.Printable#isWatermarkEnabled()
     */
    @Override
    public boolean isWatermarkEnabled(){
        return false;
    }
    /**
     * 
     *This method for getting the watermark object 
     *with respect to the appropriate document.
     */
    @Override
    public Watermarkable getWatermarkable(){
        if(isWatermarkEnabled()){
            throw new RuntimeException("Watermarkable not implemented");
        }else{
            return null;
        }
    }
    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
