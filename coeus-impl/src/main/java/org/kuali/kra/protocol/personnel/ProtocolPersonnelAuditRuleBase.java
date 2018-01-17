/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.personnel;

import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.util.AuditError;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.DocumentAuditRule;

import java.util.ArrayList;
import java.util.List;


/**
 * Rules that invoke audit mode for KeyPersonnel
 */
public abstract class ProtocolPersonnelAuditRuleBase extends KcTransactionalDocumentRuleBase implements DocumentAuditRule {
    private List<AuditError> auditErrors;
    private static final String PERSONNEL_AUDIT_ERRORS = "personnelAuditErrors";
    
    @Override
    public boolean processRunAuditBusinessRules(Document document) {
        boolean isValid = true;
        ProtocolDocumentBase protocolDocument = (ProtocolDocumentBase)document;
        auditErrors = new ArrayList<AuditError>();
        isValid = getProtocolPersonnelService().isValidStudentFacultyMatch(protocolDocument.getProtocol().getProtocolPersons());

        if (!isValid) {
            for (Integer errorIndex: getProtocolPersonnelService().getAffiliationStudentMap(protocolDocument.getProtocol().getProtocolPersons())) {
                addErrorToAuditErrors(errorIndex.intValue());
            }
        }
        reportAndCreateAuditCluster();
        
        return isValid;

    }
       
    /**
     * This method creates and adds the Audit Error to the <code>{@link List&lt;AuditError&gt;}</code> auditError.
     */
    protected void addErrorToAuditErrors(int personIndex) {
        String label = Constants.PROTOCOL_FROM_DOCUMENT + ".protocolPersons[" + personIndex + "].protocolPersonRoleId"; 
        String cat = Constants.PROTOCOL_PERSONNEL_PAGE + "." + Constants.PROTOCOL_PERSONNEL_PANEL_ANCHOR;
        auditErrors.add(new AuditError(label, KeyConstants.ERROR_PROTOCOL_INVESTIGATOR_INVALID, cat));   
    }

    /**
     * This method creates and adds the AuditCluster to the Global AuditErrorMap.
     */
    @SuppressWarnings("unchecked")
    protected void reportAndCreateAuditCluster() {
        if (auditErrors.size() > 0) {
            GlobalVariables.getAuditErrorMap().put(PERSONNEL_AUDIT_ERRORS, new AuditCluster(Constants.PROTOCOL_PERSONNEL_PANEL_NAME,
                                                                                          auditErrors, Constants.AUDIT_ERRORS));
        }
    }
    
    /**
     * This method is to get personnel sevice
     * @return ProtocolPersonnelService
     */
    protected abstract ProtocolPersonnelService getProtocolPersonnelService();
}
