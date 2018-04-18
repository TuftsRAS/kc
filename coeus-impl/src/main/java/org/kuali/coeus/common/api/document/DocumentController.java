/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kuali.coeus.common.api.document.dto.DocumentDetailsDto;
import org.kuali.coeus.common.api.document.service.DocumentActionListService;
import org.kuali.coeus.common.api.document.service.KewDocHeaderDao;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.rest.UnauthorizedAccessException;
import org.kuali.coeus.sys.framework.rest.UnprocessableEntityException;
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
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping(value="/api/v1")
@Controller("documentController")
public class DocumentController {

    private static final String MMMM_D_YYYY = "MMMM d , yyyy";
    @Autowired
    @Qualifier("documentService")
    private DocumentService documentService;

    @Autowired
    @Qualifier("documentDictionaryService")
    private DocumentDictionaryService documentDictionaryService;

    @Autowired
    @Qualifier("kewDocHeaderDao")
    private KewDocHeaderDao krewDocHeaderDao;

    @Autowired
    @Qualifier("personService")
    private PersonService personService;

    @Autowired
    @Qualifier("documentActionListService")
    private DocumentActionListService documentActionListService;

    @Autowired
    @Qualifier("workflowDocumentActionsService")
    private WorkflowDocumentActionsService workflowDocumentActionsService;

    @Autowired
    @Qualifier("groupService")
    private GroupService groupService;

    @Autowired
    @Qualifier("documentRouteHeaderService")
    private RouteHeaderService documentRouteHeaderService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;

    private static final Log LOG = LogFactory.getLog(DocumentController.class);

    @RequestMapping(method= RequestMethod.GET, value="/enroute-documents", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<DocumentDetailsDto> documentsRoutingToUser(@RequestParam(value = "user") String user) {
        return getDocumentsRoutingForUser(user);
    }

    @RequestMapping(method= RequestMethod.GET, value="/saved-documents", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<DocumentDetailsDto> documentsSavedForUser(@RequestParam(value = "user") String user) {
        return documentSavedForUser(user);
    }

    private List<DocumentDetailsDto> getDocumentsRoutingForUser(String routingToUser) {
        checkAndRetrievePerson(routingToUser);
        ArrayList<DocumentDetailsDto> documentList = new ArrayList<>();
        List<DocumentSearchResult> enrouteDocuments = krewDocHeaderDao.getEnrouteProposalDocs(routingToUser);
        for (DocumentSearchResult enrouteDocument : enrouteDocuments) {
            RoutingReportCriteria.Builder reportCriteriaBuilder = RoutingReportCriteria.Builder.createByDocumentId(enrouteDocument.getDocument().getDocumentId());
            reportCriteriaBuilder.setTargetPrincipalIds(Collections.singletonList(routingToUser));
            DocumentDetail documentDetail = workflowDocumentActionsService.executeSimulation(reportCriteriaBuilder.build());
            ActionRequest actionRequestUserIsIn = isUserInRouteLog(routingToUser, documentDetail);
            if (actionRequestUserIsIn != null) {
                DocumentDetailsDto documentDetailDto = getDocumentDetailsDto(enrouteDocument, null);
                Integer steps = null;
                try {
                    steps = getSteps(enrouteDocument.getDocument().getDocumentId(), routingToUser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                documentDetailDto.setStepsAway(steps);
                documentList.add(documentDetailDto);
            }
        }
        return documentList;
    }

    protected List<DocumentDetailsDto> documentSavedForUser(String savedForUser) {
        Person person = checkAndRetrievePerson(savedForUser);

        return krewDocHeaderDao.getSavedDocuments(savedForUser).stream()
                .filter(savedDoc -> canOpenDocument(person, savedDoc))
                .map(documentSearchResult -> getDocumentDetailsDto(documentSearchResult, null))
                .collect(Collectors.toList());
    }

    protected boolean canOpenDocument(Person person, DocumentSearchResult savedDoc) {
        try {
            Document document = documentService.getByDocumentHeaderId(savedDoc.getDocument().getDocumentId());
            return documentDictionaryService.getDocumentAuthorizer(document).canOpen(document, person);
        } catch (Exception e) {
            LOG.warn("User not authorized to open document");
        }
        return false;
    }

    protected Person checkAndRetrievePerson(String user) {
        Person person = personService.getPerson(user);
        final String currentUser = globalVariableService.getUserSession().getPrincipalId();
        if (person == null) {
            throw new UnprocessableEntityException("Person with id " + user + " not found.");
        }
        if (!currentUser.equalsIgnoreCase(user)) {
            throw new UnauthorizedAccessException("User " + currentUser + " cannot view the documents of user " + user);
        }
        return person;
    }

    private ActionRequest isUserInRouteLog(String routedToUser, DocumentDetail documentDetail) {
        ActionRequest actionRequestUserIsIn = null;
        for(ActionRequest actionRequest : documentDetail.getActionRequests() ) {
            if (actionRequest.isPending() && actionRequest.getActionRequested().getCode().equalsIgnoreCase(KewApiConstants.ACTION_REQUEST_APPROVE_REQ) &&
                    recipientMatchesUser(actionRequest, routedToUser)) {
                actionRequestUserIsIn = actionRequest;
            }
        }
        return actionRequestUserIsIn;
    }

    public Integer getSteps(String documentId, String userId) throws Exception {
        DocumentRouteHeaderValue routeHeader = documentRouteHeaderService.getRouteHeader(documentId);
        documentActionListService.fixActionRequestsPositions(routeHeader);
        List<ActionRequestValue> allRequests = Stream.concat(documentActionListService.populateRouteLogFormActionRequests(routeHeader).stream(),
                documentActionListService.populateRouteLogFutureRequests(routeHeader).stream()).filter(actionRequestValue ->
                !actionRequestValue.getStatus().equalsIgnoreCase("D")).collect(Collectors.toList());

        int activePosition = 0;
        for (int position = 0; position < allRequests.size(); position++) {
            final ActionRequestValue actionRequestValue = allRequests.get(position);
            if (actionRequestValue.getStatus().equalsIgnoreCase("A")) {
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

    private DocumentDetailsDto getDocumentDetailsDto(DocumentSearchResult doc, Integer stepsAway) {
        DocumentDetailsDto documentDetailsDto = new DocumentDetailsDto();
        documentDetailsDto.setDocumentTitle(doc.getDocument().getTitle());
        documentDetailsDto.setDocumentNumber(doc.getDocument().getDocumentId());
        DateTimeFormatter patternFormat = DateTimeFormat.forPattern(MMMM_D_YYYY);
        long formattedDate = doc.getDocument().getDateCreated().getMillis();
        documentDetailsDto.setDocumentCreateDate(formattedDate);
        documentDetailsDto.setDocHandlerUrl(doc.getDocument().getDocumentHandlerUrl());
        documentDetailsDto.setDocumentType(doc.getDocument().getDocumentTypeName());
        documentDetailsDto.setStepsAway(stepsAway);
        return documentDetailsDto;
    }

    protected boolean recipientMatchesUser(ActionRequest actionRequest, String loggedInPrincipalId) {
        if (actionRequest != null && loggedInPrincipalId != null ) {
            List<ActionRequest> actionRequests =  Collections.singletonList(actionRequest);
            if(actionRequest.isRoleRequest()) {
                actionRequests = actionRequest.getChildRequests();
            }
            for( ActionRequest cActionRequest : actionRequests ) {
                String recipientUser = cActionRequest.getPrincipalId();
                if( ( recipientUser != null && recipientUser.equals(loggedInPrincipalId) )
                        || (StringUtils.isNotBlank(cActionRequest.getGroupId())
                        && groupService.isMemberOfGroup(loggedInPrincipalId, cActionRequest.getGroupId() ))) {
                    return true;
                }
            }
        }

        return false;
    }
}
