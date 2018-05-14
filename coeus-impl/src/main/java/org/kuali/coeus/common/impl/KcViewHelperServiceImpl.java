/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.impl.validation.DataValidationItem;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;
import org.kuali.rice.krad.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class KcViewHelperServiceImpl extends ViewHelperServiceImpl {

    private static final long serialVersionUID = 3335951799627228900L;
    private static final Logger LOG = Logger.getLogger(KcViewHelperServiceImpl.class);
    private static final String FEEDBACK_LINK_PARAMETER_NAME = "feedback.link.url";

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;

    public List<DataValidationItem> populateDataValidation() {
        List<DataValidationItem> dataValidationItems = new ArrayList<>();
        for (Map.Entry<String,AuditCluster> entry : getGlobalVariableService().getAuditErrorMap().entrySet()) {
            AuditCluster auditCluster = entry.getValue();
            List<AuditError> auditErrors = auditCluster.getAuditErrorList();
            String areaName = StringUtils.substringBefore(auditCluster.getLabel(),".");
            String sectionName = StringUtils.substringAfter(auditCluster.getLabel(),".");
            for (AuditError auditError : auditErrors) {
            	DataValidationItem dataValidationItem = new DataValidationItem();
                String pageId = StringUtils.substringBefore(auditError.getLink(),".");
                String sectionId = StringUtils.substringAfter(auditError.getLink(),".");
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setErrorKey(auditError.getMessageKey());
                errorMessage.setMessageParameters(auditError.getParams());

                dataValidationItem.setArea(areaName);
                dataValidationItem.setSection(sectionName);
                dataValidationItem.setDescription(KRADUtils.getMessageText(errorMessage, false));
                dataValidationItem.setSeverity(auditCluster.getCategory());
                dataValidationItem.setNavigateToPageId(pageId);
                dataValidationItem.setNavigateToSectionId(sectionId);

                dataValidationItems.add(dataValidationItem);
            }
        }

        dataValidationItems.sort(Comparator.comparing(DataValidationItem::getArea));
        return dataValidationItems;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected ParameterService getParameterService () {
        return parameterService;
    }

    public String getErrorCssClass(String severity) {
        if (severity.endsWith(Constants.AUDIT_ERRORS)) {
            return "label-danger";
        } else if (severity.endsWith(Constants.AUDIT_WARNINGS)) {
            return "label-warning";
        }
        return "label-info";
    }

    public String urlEncode(String s) throws UnsupportedEncodingException {
        if (s == null) {
            return "";
        }
        return URLEncoder.encode(s, "UTF-8");
    }
   
	public GlobalVariableService getGlobalVariableService() {
		return globalVariableService;
	}

	public void setGlobalVariableService(GlobalVariableService globalVariableService) {
		this.globalVariableService = globalVariableService;
	}

    public boolean isDataValidationSectionEnabled() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE,
                Constants.SHOW_SECTION_IN_DATA_VALIDATION);
    }

    public String getHelpLinkFromParameter(String parameterName) {
        String link = getParameterService().getParameterValueAsString(getParameterClass(), parameterName);
        String relativeLink = String.format("%s/%s", getConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY), link);
        if (new UrlValidator().isValid(link)) {
            return link;
        } else if (new UrlValidator(UrlValidator.ALLOW_2_SLASHES).isValid(relativeLink)) {
            return  relativeLink;
        } else {
            LOG.warn(String.format("Help link \"%s\" for parameter \"%s\" does not exist or is not a valid URL", link, parameterName));
            String feedbackLink = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_GEN, Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, FEEDBACK_LINK_PARAMETER_NAME);
            return StringUtils.isNotBlank(feedbackLink) ? feedbackLink : getConfigurationService().getPropertyValueAsString(FEEDBACK_LINK_PARAMETER_NAME);
        }
    }

    protected abstract Class<?> getParameterClass();
}
