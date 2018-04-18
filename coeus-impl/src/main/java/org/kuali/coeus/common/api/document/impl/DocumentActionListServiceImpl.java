/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document.impl;

import org.kuali.coeus.common.api.document.service.DocumentActionListService;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestStatus;
import org.kuali.rice.kew.api.action.RoutingReportCriteria;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.kew.api.document.node.RouteNodeInstanceState;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.engine.node.Branch;
import org.kuali.rice.kew.engine.node.NodeState;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.node.service.RouteNodeService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("documentActionListService")
public class DocumentActionListServiceImpl implements DocumentActionListService {

    @Autowired
    @Qualifier("actionRequestService")
    private ActionRequestService actionRequestService;

    @Autowired
    @Qualifier("workflowDocumentActionsService")
    protected WorkflowDocumentActionsService workflowDocumentActionsService;

    private static Comparator<ActionRequestValue> ROUTE_LOG_ACTION_REQUEST_SORTER = new Utilities.RouteLogActionRequestSorter();

    @Override
    public List<ActionRequestValue> populateRouteLogFormActionRequests(DocumentRouteHeaderValue routeHeader) {
        List<ActionRequestValue> rootRequests = actionRequestService.getRootRequests(routeHeader.getActionRequests());
        Collections.sort(rootRequests, ROUTE_LOG_ACTION_REQUEST_SORTER);
        rootRequests = switchActionRequestPositionsIfPrimaryDelegatesPresent(rootRequests);
        for ( ActionRequestValue actionRequest : rootRequests ) {
            if (actionRequest.isPending()) {
                if (ActionRequestStatus.INITIALIZED.getCode().equals(actionRequest.getStatus())) {
                    actionRequest.setDisplayStatus("PENDING");
                } else if (ActionRequestStatus.ACTIVATED.getCode().equals(actionRequest.getStatus())) {
                    actionRequest.setDisplayStatus("IN ACTION LIST");
                }
            }
        }
        return rootRequests;
    }

    @Override
    public List<ActionRequestValue> switchActionRequestPositionsIfPrimaryDelegatesPresent(Collection<ActionRequestValue> actionRequests) {
        List<ActionRequestValue> results = new ArrayList<ActionRequestValue>( actionRequests.size() );
        for ( ActionRequestValue actionRequest : actionRequests ) {
            results.add( switchActionRequestPositionIfPrimaryDelegatePresent(actionRequest) );
        }
        return results;
    }

    @Override
    public ActionRequestValue switchActionRequestPositionIfPrimaryDelegatePresent(ActionRequestValue actionRequest) {
        if (!actionRequest.isRoleRequest()) {
            List<ActionRequestValue> primaryDelegateRequests = actionRequest.getPrimaryDelegateRequests();
            // only display primary delegate request at top if there is only *one* primary delegate request
            if ( primaryDelegateRequests.size() != 1) {
                return actionRequest;
            }
            ActionRequestValue primaryDelegateRequest = primaryDelegateRequests.get(0);
            actionRequest.getChildrenRequests().remove(primaryDelegateRequest);
            primaryDelegateRequest.setChildrenRequests(actionRequest.getChildrenRequests());
            primaryDelegateRequest.setParentActionRequest(actionRequest.getParentActionRequest());

            actionRequest.setChildrenRequests( new ArrayList<ActionRequestValue>(0) );
            actionRequest.setParentActionRequest(primaryDelegateRequest);

            primaryDelegateRequest.getChildrenRequests().add(0, actionRequest);

            for (ActionRequestValue delegateRequest : primaryDelegateRequest.getChildrenRequests()) {
                delegateRequest.setParentActionRequest(primaryDelegateRequest);
            }
            return primaryDelegateRequest;
        }
        return actionRequest;
    }

    @Override
    public void fixActionRequestsPositions(DocumentRouteHeaderValue routeHeader) {
        for (ActionTakenValue actionTaken : routeHeader.getActionsTaken()) {
            Collections.sort((List<ActionRequestValue>) actionTaken.getActionRequests(), ROUTE_LOG_ACTION_REQUEST_SORTER);
            actionTaken.setActionRequests( actionTaken.getActionRequests() );
        }
    }

    @Override
    public List<ActionRequestValue> populateRouteLogFutureRequests(DocumentRouteHeaderValue document) throws Exception {
        RoutingReportCriteria reportCriteria = RoutingReportCriteria.Builder.createByDocumentId(document.getDocumentId()).build();
        // gather the IDs for action requests that predate the simulation
        Set<String> preexistingActionRequestIds = getActionRequestIds(document);

        // run the simulation
        DocumentDetail documentDetail = workflowDocumentActionsService.executeSimulation(reportCriteria);
        // fabricate our ActionRequestValueS from the results
        List<ActionRequestValue> futureActionRequests =
                reconstituteActionRequestValues(documentDetail, preexistingActionRequestIds);

        Collections.sort(futureActionRequests, ROUTE_LOG_ACTION_REQUEST_SORTER);
        futureActionRequests = switchActionRequestPositionsIfPrimaryDelegatesPresent(futureActionRequests);
        for (ActionRequestValue actionRequest: futureActionRequests) {
            if (actionRequest.isPending()) {
                if (ActionRequestStatus.INITIALIZED.getCode().equals(actionRequest.getStatus())) {
                    actionRequest.setDisplayStatus("PENDING");
                } else if (ActionRequestStatus.ACTIVATED.getCode().equals(actionRequest.getStatus())) {
                    actionRequest.setDisplayStatus("IN ACTION LIST");
                }
            }
        }

        return futureActionRequests;
    }

    private Set<String> getActionRequestIds(DocumentRouteHeaderValue document) {
        Set<String> actionRequestIds = new HashSet<String>();
        List<ActionRequestValue> actionRequests = actionRequestService.findAllActionRequestsByDocumentId(document.getDocumentId());
        if (actionRequests != null) {
            for (ActionRequestValue actionRequest : actionRequests) {
                if (actionRequest.getActionRequestId() != null) {
                    actionRequestIds.add(actionRequest.getActionRequestId());
                }
            }
        }
        return actionRequestIds;
    }

    private List<ActionRequestValue> reconstituteActionRequestValues(DocumentDetail documentDetail,
                                                                     Set<String> preexistingActionRequestIds) {
        RouteNodeInstanceFabricator routeNodeInstanceFabricator =
                new RouteNodeInstanceFabricator(KEWServiceLocator.getRouteNodeService());

        if (documentDetail.getRouteNodeInstances() != null && !documentDetail.getRouteNodeInstances().isEmpty()) {
            for (org.kuali.rice.kew.api.document.node.RouteNodeInstance routeNodeInstanceVO : documentDetail.getRouteNodeInstances()) {
                routeNodeInstanceFabricator.importRouteNodeInstanceDTO(routeNodeInstanceVO);
            }
        }

        List<ActionRequest> actionRequestVOs = documentDetail.getActionRequests();
        List<ActionRequestValue> futureActionRequests = new ArrayList<ActionRequestValue>();
        if (actionRequestVOs != null) {
            for (ActionRequest actionRequestVO : actionRequestVOs) {
                if (actionRequestVO != null) {
                    if (!preexistingActionRequestIds.contains(actionRequestVO.getId())) {
                        ActionRequestValue converted = ActionRequestValue.from(actionRequestVO,
                                routeNodeInstanceFabricator);
                        futureActionRequests.add(converted);
                    }
                }
            }
        }
        return futureActionRequests;
    }

    private static class RouteNodeInstanceFabricator implements DTOConverter.RouteNodeInstanceLoader {

        private Map<String,Branch> branches = new HashMap<>();
        private Map<String, RouteNodeInstance> routeNodeInstances = new HashMap<>();
        private Map<String,RouteNode> routeNodes = new HashMap<>();
        private Map<String,NodeState> nodeStates = new HashMap<>();

        private RouteNodeService routeNodeService;

        public RouteNodeInstanceFabricator(RouteNodeService routeNodeService) {
            this.routeNodeService = routeNodeService;
        }

        public void importRouteNodeInstanceDTO(org.kuali.rice.kew.api.document.node.RouteNodeInstance nodeInstanceDTO) {
            _importRouteNodeInstanceDTO(nodeInstanceDTO);
        }

        private RouteNodeInstance _importRouteNodeInstanceDTO(org.kuali.rice.kew.api.document.node.RouteNodeInstance nodeInstanceDTO) {
            if (nodeInstanceDTO == null) {
                return null;
            }
            RouteNodeInstance nodeInstance = new RouteNodeInstance();
            nodeInstance.setActive(nodeInstanceDTO.isActive());
            nodeInstance.setComplete(nodeInstanceDTO.isComplete());
            nodeInstance.setDocumentId(nodeInstanceDTO.getDocumentId());
            nodeInstance.setInitial(nodeInstanceDTO.isInitial());
            Branch branch = getBranch(nodeInstanceDTO.getBranchId());
            nodeInstance.setBranch(branch);
            if (nodeInstanceDTO.getRouteNodeId() != null) {
                RouteNode routeNode = routeNodeService.findRouteNodeById(nodeInstanceDTO.getRouteNodeId());
                if (routeNode == null) {
                    routeNode = getRouteNode(nodeInstanceDTO.getRouteNodeId());
                    routeNode.setNodeType(nodeInstanceDTO.getName());
                }
                nodeInstance.setRouteNode(routeNode);
                if (routeNode.getBranch() != null) {
                    branch.setName(routeNode.getBranch().getName());
                }
            }

            RouteNodeInstance process = getRouteNodeInstance(nodeInstanceDTO.getProcessId());
            nodeInstance.setProcess(process);
            nodeInstance.setRouteNodeInstanceId(nodeInstanceDTO.getId());
            List<NodeState> nodeState = new ArrayList<NodeState>();
            if (nodeInstanceDTO.getState() != null) {
                for (RouteNodeInstanceState stateDTO : nodeInstanceDTO.getState()) {
                    NodeState state = getNodeState(stateDTO.getId());
                    if (state != null) {
                        state.setKey(stateDTO.getKey());
                        state.setValue(stateDTO.getValue());
                        state.setStateId(stateDTO.getId());
                        state.setNodeInstance(nodeInstance);
                        nodeState.add(state);
                    }
                }
            }
            nodeInstance.setState(nodeState);
            List<RouteNodeInstance> nextNodeInstances = new ArrayList<RouteNodeInstance>();
            for (org.kuali.rice.kew.api.document.node.RouteNodeInstance nextNodeInstanceVO : nodeInstanceDTO.getNextNodeInstances()) {
                // recurse to populate nextNodeInstances
                nextNodeInstances.add(_importRouteNodeInstanceDTO(nextNodeInstanceVO));
            }
            nodeInstance.setNextNodeInstances(nextNodeInstances);
            routeNodeInstances.put(nodeInstance.getRouteNodeInstanceId(), nodeInstance);
            return nodeInstance;
        }

        @Override
        public RouteNodeInstance load(String routeNodeInstanceID) {
            return routeNodeInstances.get(routeNodeInstanceID);
        }

        private Branch getBranch(String branchId) {
            Branch result = null;

            if (branchId != null) {
                // if branch doesn't exist, create it
                if (!branches.containsKey(branchId)) {
                    result = new Branch();
                    result.setBranchId(branchId);
                    branches.put(branchId, result);
                } else {
                    result = branches.get(branchId);
                }
            }
            return result;
        }

        private RouteNode getRouteNode(String routeNodeId) {
            RouteNode result = null;
            if (routeNodeId != null) {
                // if RouteNode doesn't exist, create it
                if (!routeNodes.containsKey(routeNodeId)) {
                    result = new RouteNode();
                    result.setRouteNodeId(routeNodeId);
                    routeNodes.put(routeNodeId, result);
                } else {
                    result = routeNodes.get(routeNodeId);
                }
            }
            return result;
        }

        public RouteNodeInstance getRouteNodeInstance(String routeNodeInstanceId) {
            RouteNodeInstance result = null;

            if (routeNodeInstanceId != null) {
                // if RouteNodeInstance doesn't exist, create it
                if (!routeNodeInstances.containsKey(routeNodeInstanceId)) {
                    result = new RouteNodeInstance();
                    result.setRouteNodeInstanceId(routeNodeInstanceId);
                    routeNodeInstances.put(routeNodeInstanceId, result);
                } else {
                    result = routeNodeInstances.get(routeNodeInstanceId);
                }
            }
            return result;
        }

        private NodeState getNodeState(String nodeStateId) {
            NodeState result = null;
            if (nodeStateId != null) {
                // if NodeState doesn't exist, create it
                if (!nodeStates.containsKey(nodeStateId)) {
                    result = new NodeState();
                    result.setNodeStateId(nodeStateId);
                    nodeStates.put(nodeStateId, result);
                } else {
                    result = nodeStates.get(nodeStateId);
                }
            }
            return result;
        }
    }

}
