package org.kuali.coeus.common.api.document.impl;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.api.document.DocumentWorkflowUserDetails;
import org.kuali.coeus.common.api.document.service.DocumentActionListService;
import org.kuali.coeus.common.api.document.service.KewDocHeaderDao;
import org.kuali.coeus.common.api.document.service.WorkflowDetailsService;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.RoutingReportCriteria;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("workflowDetailsService")
public class WorkflowDetailsServiceImpl implements WorkflowDetailsService {

    private static final String DOCUMENT_NUMBER = "documentNumber";
    private static final String PRINCIPAL_ID = "principalId";
    @Autowired
    @Qualifier("documentActionListService")
    private DocumentActionListService documentActionListService;

    @Autowired
    @Qualifier("workflowDocumentActionsService")
    private WorkflowDocumentActionsService workflowDocumentActionsService;

    @Autowired
    @Qualifier("documentRouteHeaderService")
    private RouteHeaderService documentRouteHeaderService;

    @Autowired
    @Qualifier("groupService")
    private GroupService groupService;

    @Autowired
    @Qualifier("kewDocHeaderDao")
    private KewDocHeaderDao kewDocHeaderDao;

    @Autowired
    @Qualifier("dataObjectService")
    private DataObjectService dataObjectService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;

    public void simulateWorkflowOnAllDocuments() {
        String principalId = globalVariableService.getUserSession().getPrincipalId();
        List<DocumentSearchResult> enrouteDocuments = kewDocHeaderDao.getEnrouteProposalDocs(principalId, null, null);
        for (DocumentSearchResult enrouteDocument : enrouteDocuments) {
            generateDetailsFromSimulation(enrouteDocument.getDocument().getDocumentId());
        }
    }

    public void generateDetailsFromSimulation(String documentId) {
        DocumentDetail documentDetail = getDocumentDetail(documentId);
        calculateAndPersistDetailsForUsersInRouteLog(documentId, documentDetail);
    }

    public DocumentDetail getDocumentDetail(String documentId) {
        RoutingReportCriteria.Builder reportCriteriaBuilder = RoutingReportCriteria.Builder.createByDocumentId(documentId);
        return workflowDocumentActionsService.executeSimulation(reportCriteriaBuilder.build());
    }

    public void calculateAndPersistDetailsForUsersInRouteLog(String documentId, DocumentDetail documentDetail) {
        Set<String> usersInRouteLog = getUsersInActionRequest(documentDetail);
        clearWorkflowDetails(documentId);
        for (String userId : usersInRouteLog) {
            Integer steps = null;
            try {
                steps = getSteps(documentId, userId);
                if (Objects.nonNull(steps)) {
                    createWorkflowDetails(userId, documentId, steps);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Integer getSteps(String documentId, String userId) throws Exception {
        DocumentRouteHeaderValue routeHeader = documentRouteHeaderService.getRouteHeader(documentId);
        documentActionListService.fixActionRequestsPositions(routeHeader);
        List<ActionRequestValue> allRequests = Stream.concat(documentActionListService.populateRouteLogFormActionRequests(routeHeader).stream(),
                documentActionListService.populateRouteLogFutureRequests(routeHeader).stream()).filter(actionRequestValue ->
                !actionRequestValue.getStatus().equalsIgnoreCase(KewApiConstants.ACTION_REQUEST_DEFAULT_CD)).collect(Collectors.toList());

        int activePosition = 0;
        for (int position = 0; position < allRequests.size(); position++) {
            final ActionRequestValue actionRequestValue = allRequests.get(position);
            if (actionRequestValue.getStatus().equalsIgnoreCase(KewApiConstants.ACTION_REQUEST_APPROVE_REQ)) {
                activePosition = position;
            }
            if (actionRequestValue.getPrincipalId() != null && actionRequestValue.getPrincipalId().equalsIgnoreCase(userId)) {
                return position - activePosition;
            }
            if (actionRequestValue.getPrincipalId() == null) {
                List<ActionRequestValue> childRequests = actionRequestValue.getChildrenRequests();
                boolean found = childRequests.stream().anyMatch(childRequest -> userId.equalsIgnoreCase(childRequest.getPrincipalId()));
                if (found) {
                    return position - activePosition;
                }
            }
        }
        return null;
    }

    private Set<String> getUsersInActionRequest(DocumentDetail documentDetail) {
        Set<String> principalIds = documentDetail.getActionRequests().stream().filter(actionRequest -> isPendingApproval(actionRequest))
                .map(actionRequest -> getUsersInActionRequest(actionRequest)).flatMap(List::stream)
                .collect(Collectors.toSet());
        return principalIds;
    }

    private boolean isPendingApproval(ActionRequest actionRequest) {
        return actionRequest.isPending() && actionRequest.getActionRequested().getCode().equalsIgnoreCase(KewApiConstants.ACTION_REQUEST_APPROVE_REQ);
    }

    protected List<String> getUsersInActionRequest(ActionRequest actionRequest) {
        List<String> principalIds = new ArrayList<>();
        if (actionRequest != null) {
            List<ActionRequest> actionRequests =  Collections.singletonList(actionRequest);
            if (actionRequest.isRoleRequest()) {
                actionRequests = actionRequest.getChildRequests();
            }
            for(ActionRequest cActionRequest : actionRequests ) {
                String recipientUser = cActionRequest.getPrincipalId();
                if(StringUtils.isNotBlank(cActionRequest.getGroupId())) {
                    principalIds.addAll(groupService.getMemberPrincipalIds(cActionRequest.getGroupId()));
                }
                principalIds.add(recipientUser);
            }
        }

        return principalIds;
    }

    private void clearWorkflowDetails(String documentNumber) {
        Map<String, String> keys = new HashMap<>();
        keys.put(DOCUMENT_NUMBER, documentNumber);
        dataObjectService.deleteMatching(DocumentWorkflowUserDetails.class, QueryByCriteria.Builder.andAttributes(keys).build());
    }

    private void createWorkflowDetails(String principalId, String documentNumber, Integer steps) {
        Map<String, String> keys = new HashMap<>();
        keys.put(DOCUMENT_NUMBER, documentNumber);
        keys.put(PRINCIPAL_ID, principalId);
        DocumentWorkflowUserDetails detailsRecord = dataObjectService.findUnique(DocumentWorkflowUserDetails.class, QueryByCriteria.Builder.andAttributes(keys).build());
        if (Objects.isNull(detailsRecord)) {
            detailsRecord = new DocumentWorkflowUserDetails(principalId, steps, documentNumber);
        } else {
            detailsRecord.setSteps(steps);
        }
        dataObjectService.save(detailsRecord);
    }
}
