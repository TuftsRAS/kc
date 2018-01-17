/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.attachment;

import com.lowagie.text.pdf.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.coeus.common.framework.attachment.KcAttachmentService;
import org.kuali.coeus.sys.framework.validation.ErrorReporter;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.krad.file.FileMeta;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

/**
 * KC Attachment Service Implementation.
 */
@Component("kcAttachmentService")
public class KcAttachmentServiceImpl implements KcAttachmentService {
    
	private static final String DEFAULT_ICON = "default";
    private static final String DUPLICATE_FILE_NAMES =  "Duplicate PDF Attachment File Names";

	@Resource(name="KcFileMimeTypeIcons")
    private Map<String, String> KcFileMimeTypeIcons;

    private static final String REPLACEMENT_CHARACTER = "_";
    //Exclude everything but numbers, alphabets, dots, hyphens and underscores
    private static final String REGEX_TITLE_FILENAME_PATTERN = "([^0-9a-zA-Z\\.\\-_])";
    private static final String REGEX_TITLE_SPECIAL_CHARACTER_PATTERN = "([^\\x00-\\x7F])";
    
    /**
     * Currently determining the icon based only on the mime type and using the default icon
     * if a mime type is not mapped in mimeTypeIcons. The full attachment is being passed here
     * so more advanced file type detection can be implemented if necessary.
     */
    @Override
    public String getFileTypeIcon(String type) {
        String iconPath = getMimeTypeIcons().get(type);
        if (iconPath == null) {
            return KcFileMimeTypeIcons.get(DEFAULT_ICON);
        } else {
            return iconPath;
        }
    }

    protected Map<String, String> getMimeTypeIcons() {
        return KcFileMimeTypeIcons;
    }

    public void setMimeTypeIcons(Map<String, String> mimeTypeIcons) {
        this.KcFileMimeTypeIcons = mimeTypeIcons;
    }

    protected String getDefaultIcon() {
        return KcFileMimeTypeIcons.get(DEFAULT_ICON);
    }

    @Override
    public String getInvalidCharacters(String text) {
        if (ObjectUtils.isNotNull(text)) {
            
            Pattern pattern = Pattern.compile(REGEX_TITLE_FILENAME_PATTERN);
            Matcher matcher = pattern.matcher(text);
            // Not null and invalid chars found 
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        // if text is null, return false. Null checks
        //for file names are done in other places and description
        // text can be null.
        return null;    
    }

    @Override
    public boolean getSpecialCharacter(String text) {
        if (ObjectUtils.isNotNull(text)) {
            Pattern pattern = Pattern.compile(REGEX_TITLE_SPECIAL_CHARACTER_PATTERN);
            Matcher matcher = pattern.matcher(text);            
            if (matcher.find()) {                
                return true;
            }
        }        
        return false;    
    }

    @Override
    public String checkAndReplaceSpecialCharacters(String text) {     
        String cleanText = text;
        if (ObjectUtils.isNotNull(text)) {
            Pattern pattern = Pattern.compile(REGEX_TITLE_SPECIAL_CHARACTER_PATTERN);
            Matcher matcher = pattern.matcher(text);
            cleanText = matcher.replaceAll(REPLACEMENT_CHARACTER);
        }
        return cleanText;
    }

    @Override
    public String formatFileSizeString(Long size) {
        DecimalFormat format = new DecimalFormat("0.#");

        if (size >= 1000000000) {
            return format.format((((double)size) / 1000000000)) + " GB";
        } else if (size >= 1000000) {
            return format.format((((double)size) / 1000000)) + " MB";
        } else {
            return format.format((((double)size) / 1000)) + " KB";
        }
    }

    @Override
    public boolean validPDFFile(FileMeta fileInQuestion, ErrorReporter errorReporter, String errorPrefix) {
        if (fileInQuestion.getName() == null) {
        	errorReporter.reportError(errorPrefix, KeyConstants.ERROR_ATTACHMENT_FILE_REQURIED);
        } else if (!Constants.PDF_REPORT_CONTENT_TYPE.equals(fileInQuestion.getContentType())) {
           errorReporter.reportWarning(errorPrefix, KeyConstants.INVALID_FILE_TYPE,
                    fileInQuestion.getName(), Constants.PDF_REPORT_CONTENT_TYPE);
        }
        return true;
    }

    @Override
    public boolean doesNewFileExist(FormFile file){
        return file != null && StringUtils.isNotBlank(file.getFileName());
    }

    @Override
    public Map<Object, Object> extractAttachments(PdfReader reader) throws IOException {
        Map<Object, Object> fileMap = new HashMap<>();
        PdfDictionary catalog = reader.getCatalog();
        PdfDictionary names = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.NAMES));
        if (names != null) {
            PdfDictionary embFiles = (PdfDictionary) PdfReader.getPdfObject(names.get(new PdfName("EmbeddedFiles")));
            if (embFiles != null) {
                HashMap embMap = PdfNameTree.readTree(embFiles);
                for (Object o : embMap.values()) {
                    PdfDictionary filespec = (PdfDictionary) PdfReader.getPdfObject((PdfObject) o);
                    Object fileInfo[] = unpackFile(filespec);
                    if (fileMap.containsKey(fileInfo[0])) {
                        throw new RuntimeException(DUPLICATE_FILE_NAMES);
                    }
                    fileMap.put(fileInfo[0], fileInfo[1]);
                }
            }
        }
        for (int k = 1; k <= reader.getNumberOfPages(); ++k) {
            PdfArray annots = (PdfArray) PdfReader.getPdfObject(reader.getPageN(k).get(PdfName.ANNOTS));
            if (annots == null) {
                continue;
            }
            for (Object o : annots.getArrayList()) {
                PdfDictionary annot = (PdfDictionary) PdfReader.getPdfObject((PdfObject) o);
                PdfName subType = (PdfName) PdfReader.getPdfObject(annot.get(PdfName.SUBTYPE));
                if (!PdfName.FILEATTACHMENT.equals(subType)) {
                    continue;
                }
                PdfDictionary filespec = (PdfDictionary)
                        PdfReader.getPdfObject(annot.get(PdfName.FS));
                Object fileInfo[] = unpackFile(filespec);
                if (fileMap.containsKey(fileInfo[0])) {
                    throw new RuntimeException(DUPLICATE_FILE_NAMES);
                }

                fileMap.put(fileInfo[0], fileInfo[1]);
            }
        }

        return fileMap;
    }

    /**
     * Unpacks a file attachment.
     * @param filespec The dictionary containing the file specifications
     * @throws IOException
     */
    private static Object[] unpackFile(PdfDictionary filespec)throws IOException  {
        Object arr[] = new Object[2]; //use to store name and file bytes
        if (filespec == null) {
            return null;
        }

        PdfName type = (PdfName) PdfReader.getPdfObject(filespec.get(PdfName.TYPE));
        if (!PdfName.F.equals(type) && !PdfName.FILESPEC.equals(type)) {
            return null;
        }

        PdfDictionary ef = (PdfDictionary) PdfReader.getPdfObject(filespec.get(PdfName.EF));
        if (ef == null) {
            return null;
        }

        PdfString fn = (PdfString) PdfReader.getPdfObject(filespec.get(PdfName.F));
        if (fn == null) {
            return null;
        }

        File fLast = new File(fn.toUnicodeString());
        PRStream prs = (PRStream) PdfReader.getPdfObject(ef.get(PdfName.F));
        if (prs == null) {
            return null;
        }

        byte attachmentByte[] = PdfReader.getStreamBytes(prs);
        arr[0] = fLast.getName();
        arr[1] = attachmentByte;

        return arr;
    }
}
