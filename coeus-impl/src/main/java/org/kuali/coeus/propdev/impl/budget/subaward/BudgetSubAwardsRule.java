/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.budget.subaward;

import com.lowagie.text.pdf.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.kuali.coeus.common.framework.ruleengine.KcBusinessRule;
import org.kuali.coeus.common.framework.ruleengine.KcEventMethod;
import org.kuali.coeus.common.framework.ruleengine.KcEventResult;
import org.kuali.coeus.propdev.impl.s2s.FormUtilityService;
import org.kuali.coeus.sys.api.model.KcFile;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.kra.infrastructure.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.List;

@KcBusinessRule("budgetSubAwardsRule")
public class BudgetSubAwardsRule  {

    private static final Log LOG = LogFactory.getLog(BudgetSubAwardsRule.class);
    public static final String SUBAWARD_ORG_NAME_FIELD_NAME = "organizationId";

    @Autowired
    @Qualifier("formUtilityService")
    private FormUtilityService formUtilityService;

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
        String contentType = event.getBudgetSubAwards().getNewSubAwardFile().getContentType();
        if (ArrayUtils.isEmpty(subAwardData) || !contentType.equals(Constants.PDF_REPORT_CONTENT_TYPE)) {
          result.getMessageMap().putError(Constants.SUBAWARD_FILE_FIELD_NAME, Constants.SUBAWARD_FILE_REQUIRED);
          result.setSuccess(false);
        } else {
          if(isEncryptedFile(subAwardData)) {
            result.getMessageMap().putError(Constants.SUBAWARD_FILE_FIELD_NAME, Constants.SUBAWARD_FILE_ENCRYPTED);
            result.setSuccess(false);
          }
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
      List<KcFile> attachments = getFormUtilityService().extractAttachments(reader);
      for (KcFile file: attachments) {
        if (isEncryptedFile(file.getData())) {
            return true;
        }
      }
      return false;
    } catch(InvalidPasswordException ipe) {
      return true;
    }
  }

    public FormUtilityService getFormUtilityService() {
        return formUtilityService;
    }

    public void setFormUtilityService(FormUtilityService formUtilityService) {
        this.formUtilityService = formUtilityService;
    }

    public GlobalVariableService getGlobalVariableService() {
		return globalVariableService;
	}

	public void setGlobalVariableService(GlobalVariableService globalVariableService) {
		this.globalVariableService = globalVariableService;
	}
}
