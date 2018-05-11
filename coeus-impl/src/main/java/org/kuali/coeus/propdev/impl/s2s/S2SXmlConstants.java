/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.propdev.impl.s2s;

public final class S2SXmlConstants {
    
    private S2SXmlConstants() {
        throw new UnsupportedOperationException("do not call");
    }

    public static final String ATTACHMENTS_NS = "http://apply.grants.gov/system/Attachments-V1.0";
    public static final String ATTACHED_FILE = "AttachedFile";
    public static final String FILE_NAME = "FileName";
    public static final String FILE_LOCATION = "FileLocation";
    public static final String HASH_VALUE = "HashValue";
    public static final String GLOBAL_NS = "http://apply.grants.gov/system/Global-V1.0";
    public static final String HREF = "href";
    public static final String HASH_ALGORITHM = "hashAlgorithm";
    public static final String MIME_TYPE = "MimeType";
    public static final String HEADER_NS = "http://apply.grants.gov/system/Header-V1.0";
    public static final String GRANT_SUBMISSION_HEADER = "GrantSubmissionHeader";
    public static final String META_GRANT_APPLICATION_NS = "http://apply.grants.gov/system/MetaGrantApplication";
    public static final String FORMS = "Forms";
    public static final String META_GRANT_APPLICATION_WRAPPER_NS = "http://apply.grants.gov/system/MetaGrantApplicationWrapper";
    public static final String SELECTED_OPTIONAL_FORMS = "SelectedOptionalForms";
    public static final String FORM_TAG_NAME = "FormTagName";
}
