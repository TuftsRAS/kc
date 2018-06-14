/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.print;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.xmlbeans.XmlObject;
import org.kuali.coeus.common.framework.print.Printable;
import org.kuali.coeus.common.framework.print.PrintableAttachment;
import org.kuali.coeus.common.framework.print.PrintingException;
import org.kuali.coeus.common.framework.print.PrintingService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.coeus.common.framework.print.AttachmentDataSource;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

import static org.kuali.coeus.sys.framework.util.CollectionUtils.entriesToMap;

/**
 * This class provides the functionality for printing any {@link Printable} object. It uses the methods available in Printable
 * object to generate XML, fetch XSL style-sheets, then transforms the XML to a PDF after applying the style sheet.
 * 
 */
@Component("printingService")
public class PrintingServiceImpl implements PrintingService {

    private static final String FORCED_XSL_FORMS_PARAMETER = "FORCED_XSL_FORMS";

    private static final Log LOG = LogFactory.getLog(PrintingServiceImpl.class);

    @Autowired
    @Qualifier("dateTimeService")
    private DateTimeService dateTimeService;

    @Autowired
    @Qualifier("kualiConfigurationService")
    private ConfigurationService kualiConfigurationService;

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    /**
     * This method receives a {@link Printable} object, generates XML for it, transforms into PDF after applying style-sheet and
     * returns the PDF bytes as {@link AttachmentDataSource}
     * 
     * @param printableArtifact to be printed
     * @return {@link AttachmentDataSource} PDF bytes
     * @throws PrintingException in case of any errors occur during printing process
     */
    protected Map<String, byte[]> getPrintBytes(Printable printableArtifact) throws PrintingException {
        Map<String, byte[]> pdfByteMap = new LinkedHashMap<>();

        Map<String, XmlObject> streamMap = printableArtifact.renderXML();

        final boolean loggingEnable = kualiConfigurationService.getPropertyValueAsBoolean(Constants.PRINT_LOGGING_ENABLE);
        if (loggingEnable) {
            logPrintDetails(streamMap);
        }

        final Collection<String> forms = parameterService.getParameterValuesAsString(Constants.KC_GENERIC_PARAMETER_NAMESPACE, Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, FORCED_XSL_FORMS_PARAMETER);

        pdfByteMap.putAll(printableArtifact.fillPdfForms(printableArtifact.getPdfForms().entrySet().stream()
                .filter(e -> !forms.contains(e.getKey()))
                .collect(entriesToMap()), streamMap));

        final Set<String> ignored = pdfByteMap.keySet();
        try {
            // Apply all the style sheets to the xml document and generate the
            // PDF bytes
            if (printableArtifact.getXSLTemplates() != null) {
                int xslCount = 0;
                for (Source source : printableArtifact.getXSLTemplates()) {
                    xslCount++;
                    StreamSource xslt = (StreamSource) source;
                    if(xslt.getInputStream()==null || xslt.getInputStream().available()<=0){
                        LOG.error("Stylesheet is not available");
                    }else{
                        createPdfWithFOP(streamMap, pdfByteMap, xslCount, xslt);
                    }
                }
            } else if (printableArtifact.getXSLTemplateWithBookmarks() != null) {
                for (Map.Entry<String, Source> templatesWithBookmark : printableArtifact.getXSLTemplateWithBookmarks().entrySet()) {
                    if (!ignored.contains(templatesWithBookmark.getKey())) {
                        StreamSource xslt = (StreamSource) templatesWithBookmark.getValue();
                        createPdfWithFOP(streamMap, pdfByteMap, 0, xslt, templatesWithBookmark.getKey());
                    }
                }
            }
        } catch (FOPException|TransformerException|IOException e) {
            LOG.error(e.getMessage(), e);
            throw new PrintingException(e.getMessage(), e);
        }

        return pdfByteMap;

    }

    protected void createPdfWithFOP(Map<String, XmlObject> streamMap, Map<String, byte[]> pdfByteMap,
            int xslCount, StreamSource xslt) throws FOPException, TransformerException {
        createPdfWithFOP(streamMap, pdfByteMap, xslCount, xslt,null);
    }

    protected void createPdfWithFOP(Map<String, XmlObject> streamMap, Map<String, byte[]> pdfByteMap,
            int xslCount, StreamSource xslt, String bookmark) throws FOPException,
            TransformerException {
        FopFactory fopFactory = FopFactory.newInstance();
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xslt);
        String externalizableImagesUrl= getKualiConfigurationService().getPropertyValueAsString(Constants.KRA_EXTERNALIZABLE_IMAGES_URI_KEY);
        transformer.setParameter("externalImagesUrl",externalizableImagesUrl);
        String applicationUrl = getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        foUserAgent.setBaseURL(applicationUrl);      
        for (Map.Entry<String, XmlObject> xmlData : streamMap.entrySet()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlData.getValue().xmlText().getBytes());
            Source src = new StreamSource(inputStream);
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream);
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
            byte[] pdfBytes = outputStream.toByteArray();
            if (pdfBytes != null && pdfBytes.length > 0) {
                String pdfMapKey = bookmark == null ? createBookMark(xslCount, xmlData.getKey()) : bookmark;
                pdfByteMap.put(pdfMapKey, pdfBytes);
            }
        }
    }


    protected String createBookMark(int xslCount, String bookmarkKey) {
        return bookmarkKey + (xslCount == 1 ? "" : " " + xslCount);
    }

    /**
     * This method receives a {@link Printable} object, generates XML for it, transforms into PDF after applying style-sheet and
     * returns the PDF bytes as {@link AttachmentDataSource}
     * 
     * @param printableArtifacts to be printed
     * @return {@link AttachmentDataSource} PDF bytes
     * @throws PrintingException in case of any errors occur during printing process
     */
    @Override
    public AttachmentDataSource print(Printable printableArtifacts) throws PrintingException {
        List<Printable> printables = new ArrayList<>();
        printables.add(printableArtifacts);
        return print(printables);
    }

    /**
     * This method receives a {@link List} of {@link Printable} object, generates XML for it, transforms into PDF after applying
     * style-sheet and returns the PDF bytes as {@link AttachmentDataSource}
     * 
     * @param printableArtifactList List of printableArtifact to be printed
     * @return {@link AttachmentDataSource} PDF bytes
     * @throws PrintingException in case of any errors occur during printing process
     */
    @Override
    public AttachmentDataSource print(List<Printable> printableArtifactList) throws PrintingException {
        PrintableAttachment printablePdf;
        List<String> bookmarksList = new ArrayList<>();
        List<byte[]> pdfBaosList = new ArrayList<>();
        for (Printable printableArtifact : printableArtifactList) {
            final Map<String, byte[]> printBytes = new LinkedHashMap<>(printableArtifact.sortPdfForms(getPrintBytes(printableArtifact)));
            if (printableArtifact.getAttachments() != null) {
                printBytes.putAll(printableArtifact.getAttachments());
            }

            for (Map.Entry<String, byte[]> entry : printBytes.entrySet()) {
                if (isPdfGoodToMerge(entry.getValue())) {
                    bookmarksList.add(entry.getKey());
                    pdfBaosList.add(entry.getValue());
                }
            }
        }

        printablePdf = new PrintableAttachment();
        byte[] mergedPdfBytes = mergePdfBytes(pdfBaosList, bookmarksList);
        // If there is a stylesheet issue, the pdf bytes will be null. To avoid an exception
        // initialize to an empty array before sending the content back
        if (mergedPdfBytes == null) {
            mergedPdfBytes = new byte[0];
        }

        printablePdf.setData(mergedPdfBytes);
        StringBuilder fileName = new StringBuilder();
        fileName.append(getReportName());
        fileName.append(Constants.PDF_FILE_EXTENSION);
        printablePdf.setName(fileName.toString());
        printablePdf.setType(Constants.PDF_REPORT_CONTENT_TYPE);

        final boolean loggingEnable = kualiConfigurationService.getPropertyValueAsBoolean(Constants.PRINT_PDF_LOGGING_ENABLE);
        if (loggingEnable) {
            logPdfPrintDetails(printablePdf);
        }

        return printablePdf;
    }

    protected boolean isPdfGoodToMerge(byte[] pdfBytes) {
        try {
            new PdfReader(pdfBytes);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    protected String getReportName() {
        String dateString = getDateTimeService().getCurrentDate().toString();
        return StringUtils.deleteWhitespace(dateString);
    }

    protected byte[] mergePdfBytes(List<byte[]> pdfBytesList, List<String> bookmarksList)
            throws PrintingException {
        Document document = null;
        PdfWriter writer = null;
        ByteArrayOutputStream mergedPdfReport = new ByteArrayOutputStream();

        PdfReader[] pdfReaderArr = new PdfReader[pdfBytesList.size()];
        int pdfReaderCount = 0;
        for (byte[] fileBytes : pdfBytesList) {
            LOG.debug("File Size " + fileBytes.length + " For " + bookmarksList.get(pdfReaderCount));
            PdfReader reader;
            try {
                reader = new PdfReader(fileBytes);
                pdfReaderArr[pdfReaderCount] = reader;
                pdfReaderCount = pdfReaderCount + 1;

            }
            catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }


        for (int count = 0; count < pdfReaderArr.length; count++) {
            PdfReader reader = pdfReaderArr[count];
            int nop;
            if (reader == null) {
                LOG.debug("Empty PDF byetes found for " + bookmarksList.get(count));
                continue;
            }
            else {
                nop = reader.getNumberOfPages();
            }

            if (count == 0) {
                document = nop > 0 ? new com.lowagie.text.Document(reader.getPageSizeWithRotation(1))
                        : new com.lowagie.text.Document();
                try {
                    writer = PdfWriter.getInstance(document, mergedPdfReport);
                }
                catch (DocumentException e) {
                    LOG.error(e.getMessage(), e);
                    throw new PrintingException(e.getMessage(), e);
                }

                document.open();
            }

            PdfContentByte cb = writer.getDirectContent();
            int pageCount = 0;
            while (pageCount < nop) {
                document.setPageSize(reader.getPageSize(++pageCount));
                document.newPage();

                PdfImportedPage page = writer.getImportedPage(reader, pageCount);

                cb.addTemplate(page, 1, 0, 0, 1, 0, 0);


                PdfOutline root = cb.getRootOutline();
                if (pageCount == 1) {
                    String pageName = bookmarksList.get(count);
                    cb.addOutline(new PdfOutline(root, new PdfDestination(PdfDestination.FITH), pageName), pageName);
                }
            }
        }
        if (document != null) {
            try {
                document.close();
                return mergedPdfReport.toByteArray();
            }
            catch (Exception e) {
                LOG.error("Exception occurred because the generated PDF document has no pages", e);
            }
        }
        return null;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }


    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    protected void logPrintDetails(Map<String, XmlObject> xmlStreamMap) {
        XmlObject xmlBytes;
        String xmlString;
        String loggingDirectory = kualiConfigurationService.getPropertyValueAsString(Constants.PRINT_LOGGING_DIRECTORY);
        Iterator<String> it = xmlStreamMap.keySet().iterator();
        if (loggingDirectory != null) {
            BufferedWriter out=null;
            try {
                while (it.hasNext()) {
                    String key = it.next();
                    xmlBytes = xmlStreamMap.get(key);
                    xmlString = xmlBytes.xmlText();
                    String dateString = getDateTimeService().getCurrentTimestamp().toString();
                    String reportName = StringUtils.deleteWhitespace(key);
                    String createdTime = StringUtils.replaceChars(StringUtils.deleteWhitespace(dateString), ":", "_");
                    File dir = getLoggingDir(loggingDirectory);
                    File file = new File(dir , reportName + "_" + createdTime + ".xml");

                    out = new BufferedWriter(new FileWriter(file));
                    out.write(xmlString);
                }
            }
            catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }finally{
                try {
                    if(out!=null){
                        out.flush();
                        out.close();
                    }
                }
                catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    protected void logPdfPrintDetails(AttachmentDataSource pdf) {
        final String loggingDirectory = kualiConfigurationService.getPropertyValueAsString(Constants.PRINT_LOGGING_DIRECTORY);
        if (loggingDirectory != null && pdf != null && pdf.getData() != null) {
            final String dateString = new Timestamp(new Date().getTime()).toString();
            final String createdTime = StringUtils.replaceChars(StringUtils.deleteWhitespace(dateString), ":", "_");
            final String fileName = StringUtils.replaceChars(StringUtils.deleteWhitespace(pdf.getName().replace(".pdf", "")), ":", "_") + "_" + createdTime + ".pdf";

            try {
                FileUtils.writeByteArrayToFile(new File(getLoggingDir(loggingDirectory),fileName), pdf.getData());
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    protected File getLoggingDir(String loggingDirectory) {
        File dir = new File(loggingDirectory);
        if(!dir.exists() || !dir.isDirectory()){
            dir.mkdirs();
        }
        return dir;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
