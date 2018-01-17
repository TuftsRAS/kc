/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.auth;

import org.kuali.coeus.sys.framework.workflow.KcWorkflowService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.irb.Protocol;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.RoutingReportCriteria;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.document.DocumentDetail;

import java.util.Collections;

/**
 * Determine if a user can assign a protocol to a committee/schedule.
 */
public class ProtocolAssignReviewersCmtSelAuthorizer extends ProtocolAuthorizer {

    private KcWorkflowService kraWorkflowService;

    @Override
    public boolean isAuthorized(String username, ProtocolTask task) {
        Protocol protocol = task.getProtocol();
        return (isOnNode(protocol) || willBeOnNode(username, protocol)) && 
            hasPermission(username, protocol, PermissionConstants.PERFORM_IRB_ACTIONS_ON_PROTO);
    }

    public boolean isOnNode(Protocol protocol) {
        return kraWorkflowService.isDocumentOnNode(protocol.getProtocolDocument(), Constants.PROTOCOL_IRBREVIEW_ROUTE_NODE_NAME);
    }

    // look to insure our next node won't be "DepartmentReview", which means the protocol will require
    // departmental approval before being assigned reviewers
    public boolean willBeOnNode(String username, Protocol protocol) {
        boolean results = true;
        RoutingReportCriteria.Builder reportCriteriaBuilder = RoutingReportCriteria.Builder.createByDocumentId(protocol.getProtocolDocument().getDocumentNumber());
        reportCriteriaBuilder.setTargetPrincipalIds(Collections.singletonList(username));
        WorkflowDocumentActionsService info = GlobalResourceLoader.getService("rice.kew.workflowDocumentActionsService");
        
        try { 
            DocumentDetail results1 = info.executeSimulation(reportCriteriaBuilder.build());
            for(ActionRequest actionRequest : results1.getActionRequests() ){
                if (Constants.PROTOCOL_APPROVAL_NODE_NAME.equals(actionRequest.getNodeName())) {
                    results = false;
                }
            }
        } catch (Exception e) {}
        return results;
    }

    public KcWorkflowService getKraWorkflowService() {
        return kraWorkflowService;
    }

    public void setKraWorkflowService(KcWorkflowService kraWorkflowService) {
        this.kraWorkflowService = kraWorkflowService;
    }
}
