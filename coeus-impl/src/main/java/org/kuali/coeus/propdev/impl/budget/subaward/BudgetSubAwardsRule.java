/*
 * Kuali Coeus, a comprehensive research administration system for higher education.
 * 
 * Copyright 2005-2016 Kuali, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.coeus.propdev.impl.budget.subaward;

import com.lowagie.text.pdf.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.kuali.coeus.common.framework.attachment.KcAttachmentService;
import org.kuali.coeus.common.framework.ruleengine.KcBusinessRule;
import org.kuali.coeus.common.framework.ruleengine.KcEventMethod;
import org.kuali.coeus.common.framework.ruleengine.KcEventResult;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.kra.infrastructure.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@KcBusinessRule("budgetSubAwardsRule")
public class BudgetSubAwardsRule  {

    private static final Log LOG = LogFactory.getLog(BudgetSubAwardsRule.class);
    public static final String SUBAWARD_ORG_NAME_FIELD_NAME = "organizationId";
    
    @Autowired
    @Qualifier("kcAttachmentService")
    private KcAttachmentService kcAttachmentService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;
    
    @KcEventMethod
    public KcEventResult processSubaward(BudgetSubAwardsEvent event) {
    	KcEventResult result = new KcEventResult();
    	result.getMessageMap().addToErrorPath(event.getErrorPath());
        verifyAttachment(event, result);
        event.getBudgetSubAwards().setNewSubAwardFileError(result.getSuccess());
        verifyOrganizationName(event, result);
        return result;
    }
    
    protected void verifyOrganizationName(BudgetSubAwardsEvent event, KcEventResult result){
        if (StringUtils.isBlank(event.getBudgetSubAwards().getOrganizationId())) {
        	result.getMessageMap().putError(SUBAWARD_ORG_NAME_FIELD_NAME, Constants.SUBAWARD_ORG_NAME_REQUIRED);
            result.setSuccess(false);
        } else {
        	if (event.getBudgetSubAwards().getOrganization() == null) {
            	result.getMessageMap().putError(SUBAWARD_ORG_NAME_FIELD_NAME, Constants.SUBAWARD_ORG_NAME_INVALID);
                result.setSuccess(false);
            }
        }
    }

  protected void verifyAttachment(BudgetSubAwardsEvent event, KcEventResult result) {
    if (event.getBudgetSubAwards().getNewSubAwardFile() != null) {
      try {
        byte[] subAwardData = event.getBudgetSubAwards().getNewSubAwardFile().getBytes();
        if (ArrayUtils.isEmpty(subAwardData)) {
          result.getMessageMap().putError(Constants.SUBAWARD_FILE_FIELD_NAME, Constants.SUBAWARD_FILE_REQUIRED);
          result.setSuccess(false);
        }
        String contentType = event.getBudgetSubAwards().getNewSubAwardFile().getContentType();

        if(ArrayUtils.isEmpty(subAwardData) || !contentType.equals(Constants.PDF_REPORT_CONTENT_TYPE)){
          result.getMessageMap().putError(Constants.SUBAWARD_FILE_FIELD_NAME, Constants.SUBAWARD_FILE_REQUIRED);
          result.setSuccess(false);
        }

        if(isEncryptedFile(subAwardData)) {
          result.getMessageMap().putError(Constants.SUBAWARD_FILE_FIELD_NAME, Constants.SUBAWARD_FILE_ENCRYPTED);
          result.setSuccess(false);
        }
      } catch(IOException e) {
        LOG.error(e.getMessage(), e);
        result.getMessageMap().putError(Constants.SUBAWARD_FILE_FIELD_NAME, Constants.SUBAWARD_FILE_REQUIRED);
        result.setSuccess(false);
      }
    }
  }

  private boolean isEncryptedFile(byte[] data) throws IOException {
    try {
      PDDocument pdd = PDDocument.load(data);
      if (pdd.isEncrypted()) return true;

      PdfReader reader = new PdfReader(data);
      Map<Object, Object> attachments = getKcAttachmentService().extractAttachments(reader);
      for (Map.Entry<Object, Object> pair : attachments.entrySet()) {
        if (isEncryptedFile((byte[]) pair.getValue())) return true;
      }
      return false;
    } catch(InvalidPasswordException ipe) {
      return true;
    }
  }

    protected KcAttachmentService getKcAttachmentService() {
        return kcAttachmentService;
    }

	public void setKcAttachmentService(KcAttachmentService kcAttachmentService) {
		this.kcAttachmentService = kcAttachmentService;
	}

	public GlobalVariableService getGlobalVariableService() {
		return globalVariableService;
	}

	public void setGlobalVariableService(GlobalVariableService globalVariableService) {
		this.globalVariableService = globalVariableService;
	}
}
