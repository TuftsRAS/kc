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

public interface KcAttachmentService {

    /**
     * Based on the mime type provided determine and return a path to the icon for the file.
     */
    String getFileTypeIcon(String type);
   
    
    /**
     * This method returns the invalid characters in the file name.
     */
    String getInvalidCharacters(String text);
    
    /**
     * This method check the Special characters in the string.
     */
    boolean getSpecialCharacter(String text);
   
        
    /**
     * This method checks for special characters in strings and replaces
     * them with underscores.
     */
    String checkAndReplaceSpecialCharacters(String text);

    /**
     * This method formatted the attachment file size string
     */
    String formatFileSizeString(Long size);

    /**
     * This method checks to see if the attachment is of type PDF
     */
    boolean validPDFFile(FileMeta fileInQuestion, ErrorReporter errorReporterService, String errorPrefix);

    boolean doesNewFileExist(FormFile file);
}
