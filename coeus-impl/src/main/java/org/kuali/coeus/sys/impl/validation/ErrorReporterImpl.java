/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.impl.validation;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.validation.ErrorReporter;
import org.kuali.coeus.sys.framework.validation.SoftError;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.util.AuditError;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.UserSession;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("errorReporter")
public class ErrorReporterImpl implements ErrorReporter {

    private static final Logger LOG = LogManager.getLogger(ErrorReporterImpl.class);

    @Override
    public void reportError(String propertyName, String errorKey, String... errorParams) {
        GlobalVariables.getMessageMap().putError(propertyName, errorKey, errorParams);
        if (LOG.isDebugEnabled()) {
            LOG.debug("rule failure at " + ErrorReporterImpl.getMethodPath(1, 2));
        }
    }

    @Override
    public void reportAuditError(AuditError error, String errorKey, String clusterLabel, String clusterCategory) {
        if (error == null || StringUtils.isBlank(errorKey)
                || StringUtils.isBlank(clusterLabel) || StringUtils.isBlank(clusterCategory)) {
            throw new IllegalArgumentException(new StringBuilder("null argument error: ")
                    .append(error)
                    .append(" errorkey: ")
                    .append(errorKey)
                    .append(" clusterLabel: ")
                    .append(clusterLabel)
                    .append(" clusterCategory: ")
                    .append(clusterCategory).toString());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("rule failure at " + ErrorReporterImpl.getMethodPath(1, 2));
        }

        @SuppressWarnings("unchecked")
        final Map<String, AuditCluster> errorMap = GlobalVariables.getAuditErrorMap();

        AuditCluster cluster = errorMap.get(errorKey);

        if (cluster == null) {
            cluster = new AuditCluster(clusterLabel, new ArrayList<AuditError>(), clusterCategory);
            errorMap.put(errorKey, cluster);
        }

        @SuppressWarnings("unchecked")
        final Collection<AuditError> errors = cluster.getAuditErrorList();
        errors.add(error);
    }

    @Override
    public void reportSoftError(String propertyName, String errorKey, String... errorParams) {
        addSoftError(propertyName, errorKey, errorParams);
        if (LOG.isDebugEnabled()) {
            LOG.debug("rule failure at " + ErrorReporterImpl.getMethodPath(1, 2));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Collection<SoftError>> getSoftErrors() {
        UserSession session = GlobalVariables.getUserSession();
        Object o = session.retrieveObject(KeyConstants.SOFT_ERRORS_KEY);
        Map<String, Collection<SoftError>> softErrors =(Map<String, Collection<SoftError>>) o;
        if(softErrors == null) {
            softErrors = initializeSoftErrorMap();
        }
        return softErrors;
    }

    private void addSoftError(String propertyName, String errorKey, String[] errorParams) {
        Map<String, Collection<SoftError>> softMessageMap = getSoftErrors();
        Collection<SoftError> errorsForProperty = softMessageMap.get(propertyName);
        if(errorsForProperty == null) {
            errorsForProperty = new HashSet<SoftError>();
        }
        errorsForProperty.add(new SoftError(errorKey, errorParams));
        softMessageMap.put(propertyName, errorsForProperty);
    }

    private Map<String, Collection<SoftError>> initializeSoftErrorMap() {
        Map<String, Collection<SoftError>> softMessageMap = Collections.synchronizedMap(new HashMap<String, Collection<SoftError>>() {
            private static final long serialVersionUID = 709850431504932842L;

            @Override
            public Collection<SoftError> get(Object key) {
                return super.remove(key);
            }

        });
        GlobalVariables.getUserSession().addObject(KeyConstants.SOFT_ERRORS_KEY, softMessageMap);
        return softMessageMap;
    }

    @Override
    public void reportWarning(String propertyName, String errorKey, String... errorParams) {
        GlobalVariables.getMessageMap().putWarning(propertyName, errorKey, errorParams);
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("rule warning at %s", ErrorReporterImpl.getMethodPath(1, 2)));
        }
    }

    @Override
    public boolean propertyHasErrorReported(String propertyName) {
        boolean result = false;
        if( GlobalVariables.getMessageMap().getErrorMessagesForProperty(propertyName) != null) {
            result = GlobalVariables.getMessageMap().getErrorMessagesForProperty(propertyName).size() > 0;
        }
        return result;
    }

    @Override
    public void removeErrors(String propertyName) {
        if(GlobalVariables.getMessageMap().getErrorMessagesForProperty(propertyName)!=null) {
            GlobalVariables.getMessageMap().getErrorMessagesForProperty(propertyName).clear();
        }
    }

    public static String getMethodPath(int fromLevel, int toLevel) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        //increase the levels to avoid including the method that called this.
        fromLevel = fromLevel + 1;
        toLevel = toLevel + 1;

        if (fromLevel <= 0) {
            throw new IllegalArgumentException("invalid fromLevel (" + fromLevel + " < 0)");
        }
        if (fromLevel > toLevel) {
            throw new IllegalArgumentException("invalid levels (fromLevel " + fromLevel + " > toLevel " + toLevel + ")");
        }
        if (toLevel >= stackTraceElements.length) {
            throw new IllegalArgumentException("invalid toLevel (" + toLevel + " >= " + stackTraceElements.length + ")");
        }

        StringBuffer result = new StringBuffer();
        int elementIndex = 0;
        for (StackTraceElement element : stackTraceElements){
            if (elementIndex >= fromLevel && elementIndex >= toLevel) {
                if (result.length() > 0) {
                    result.append(" from ");
                }
                result.append(element.getClassName()).append(".");
                result.append(element.getMethodName()).append("(");
                result.append(element.getFileName()).append(":");
                result.append(element.getLineNumber()).append(")");
            }
            elementIndex++;
        }
        return result.toString();

    }
}
