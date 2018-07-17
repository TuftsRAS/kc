/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.attachment;


import org.kuali.coeus.sys.api.model.KcFile;
import org.kuali.rice.krad.file.FileMeta;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.impl.FileControllerServiceImpl;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

@Component("kcFileControllerService")
public class KcFileControllerServiceImpl extends FileControllerServiceImpl {

    @Override
    protected void sendFileFromLineResponse(UifFormBase form, HttpServletResponse response, List<FileMeta> collection,
                                            FileMeta fileLine) {
        KcFile attachmentDataSource = (KcFile) fileLine;
        try {
            byte[] data = attachmentDataSource.getData();
            long size = data.length;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            KRADUtils.addAttachmentToResponse(response,inputStream,attachmentDataSource.getType(),attachmentDataSource.getName(),size);
            response.flushBuffer();
        } catch (Exception e) {
            throw new RuntimeException("error occurred while downloading attachment", e);
        }
    }
}
