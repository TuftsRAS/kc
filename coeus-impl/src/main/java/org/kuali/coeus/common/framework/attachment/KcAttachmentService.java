/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.framework.attachment;

import org.apache.struts.upload.FormFile;
import org.kuali.coeus.sys.framework.validation.ErrorReporter;
import org.kuali.rice.krad.file.FileMeta;

/**
 * KC Attachment Service.
 */
public interface KcAttachmentService {

    /**
     * Based on the mime type provided determine and return a path to the icon for the file.
     * @param type
     * @return
     */
    String getFileTypeIcon(String type);
   
    
    /**
     * This method returns the invalid characters in the file name.
     * @return
     */
    String getInvalidCharacters(String text);
    
    /**
     * This method check the Special characters in the string.
     * @return
     */
    boolean getSpecialCharacter(String text);
   
        
    /**
     * This method checks for special characters in strings and replaces
     * them with underscores.
     * @param text
     * @return
     */
    String checkAndReplaceSpecialCharacters(String text);

    /**
     * This method formatted the attachment file size string
     * @param size
     * @return
     */
    public String formatFileSizeString(Long size);

    /**
     * This method checks to see if the attachment is of type PDF
     * @param fileInQuestion
     * @param errorReporterService
     * @param errorPrefix
     * @return boolean
     */
    public boolean validPDFFile(FileMeta fileInQuestion, ErrorReporter errorReporterService, String errorPrefix);

    boolean doesNewFileExist(FormFile file);
}
