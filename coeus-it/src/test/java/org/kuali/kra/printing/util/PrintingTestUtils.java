/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.printing.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.print.AttachmentDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class PrintingTestUtils {

    private static final Logger LOG = LogManager.getLogger(PrintingTestUtils.class);

	public static String FILE_DIR = System.getProperty("java.io.tmpdir");

	public static void writePdftoDisk(AttachmentDataSource pdfBytes,
			String reportType) {
		if (pdfBytes == null || pdfBytes.getData() == null) {
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(new File(
					PrintingTestUtils.FILE_DIR
					        + File.separator
							+ reportType
							+ "_"
							+ new SimpleDateFormat("ddMMyyyy_HHmmss")
									.format(new java.util.Date()) + ".pdf"));
			fos.write(pdfBytes.getData());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
