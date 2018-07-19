/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kuali.coeus.common.api.document.dto.ApproverDto;
import org.kuali.coeus.common.api.document.dto.DevelopmentProposalSummaryDto;
import org.kuali.coeus.common.api.document.dto.DocumentDetailsDto;
import org.kuali.coeus.common.api.document.service.KewDocHeaderDao;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentConstants;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.rest.NotImplementedException;
import org.kuali.coeus.sys.framework.rest.UnauthorizedAccessException;
import org.kuali.coeus.sys.framework.rest.UnprocessableEntityException;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequestMapping(value="/api/v1")
@Controller("documentController")
public class DocumentController {

    private static final String WORKLOAD_BALANCING_PRIORITY_STOP = "Workload_Balancing_Priority_Stop";
    private static final String SEPARATOR = ", ";
    @Autowired
    @Qualifier("documentService")
    private DocumentService documentService;

    @Autowired
    @Qualifier("kewDocHeaderDao")
    private KewDocHeaderDao kewDocHeaderDao;

    @Autowired
    @Qualifier("personService")
    private PersonService personService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    @Autowired
    @Qualifier("permissionService")
    private PermissionService permissionService;

    @Autowired
    @Qualifier("dataObjectService")
    private DataObjectService dataObjectService;

    @Autowired
    @Qualifier("actionRequestService")
    private ActionRequestService actionRequestService;

    private static final Logger LOG = LogManager.getLogger(DocumentController.class);

    @RequestMapping(method= RequestMethod.GET, value="/enroute-documents", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<DocumentDetailsDto> documentsRoutingToUser(@RequestParam(value = "user") String user,
                                                           @RequestParam(value = "limit", required = false) Integer limit,
                                                           @RequestParam(value = "skip", required = false) Integer skip) {
        return getDocumentsRoutingForUser(user, limit, skip);
    }

    @RequestMapping(method= RequestMethod.GET, value="/workload-balancing-documents", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<DevelopmentProposalSummaryDto> documentsRoutingToUser(@RequestParam(value = "limit", required = false) Integer limit,
                                                           @RequestParam(value = "skip", required = false) Integer skip) {
        return getDocumentsAtWorkloadBalancingStop(limit, skip);
    }

    @RequestMapping(method= RequestMethod.GET, value="/saved-documents", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<DocumentDetailsDto> documentsSavedForUser(@RequestParam(value = "user") String user,
                                                          @RequestParam(value = "limit", required = false) Integer limit,
                                                          @RequestParam(value = "skip", required = false) Integer skip) {
        return documentSavedForUser(user, limit, skip);
    }

    @RequestMapping(method= RequestMethod.GET, value="/progress-documents", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<DocumentDetailsDto> getDocumentsInProgressForUser(@RequestParam(value = "user") String user,
                                                          @RequestParam(value = "limit", required = false) Integer limit,
                                                          @RequestParam(value = "skip", required = false) Integer skip) {
        return documentsInProgressForUser(user, limit, skip);
    }

    @RequestMapping(method= RequestMethod.POST, value="/documents/{documentId}/approver",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    ApproverDto assignApprover(@RequestBody ApproverDto approverDto, @PathVariable String documentId) throws Exception {
        Document document = documentService.getByDocumentHeaderId(documentId);
        checkDocAndPerms(document);
        Person approver = getPerson(approverDto.getApproverId());
        final DevelopmentProposal developmentProposal = ((ProposalDevelopmentDocument) document).getDevelopmentProposal();
        developmentProposal.setAssignerId(globalVariableService.getUserSession().getPrincipalId());
        developmentProposal.setApproverId(approverDto.getApproverId());
        dataObjectService.save(developmentProposal);
        approverDto.setProposalNumber(developmentProposal.getProposalNumber());
        approverDto.setApproverName(approver.getName());
        return approverDto;
    }

    @RequestMapping(method= RequestMethod.DELETE, value="/documents/{documentId}/approver",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    void deleteApprover(@PathVariable String documentId) throws Exception {
        Document document = documentService.getByDocumentHeaderId(documentId);
        checkDocAndPerms(document);
        final DevelopmentProposal developmentProposal = ((ProposalDevelopmentDocument) document).getDevelopmentProposal();
        developmentProposal.setApproverId(null);
        developmentProposal.setAssignerId(globalVariableService.getUserSession().getPrincipalId());
        dataObjectService.save(developmentProposal);
    }

    @RequestMapping(method= RequestMethod.GET, value="/documents/{documentId}/approver",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    ApproverDto getApprover(@PathVariable String documentId) throws Exception {
        Document document = documentService.getByDocumentHeaderId(documentId);
        checkDocAndPerms(document);
        final DevelopmentProposal developmentProposal = ((ProposalDevelopmentDocument) document).getDevelopmentProposal();
        ApproverDto approverDto = new ApproverDto();
        Person approver = personService.getPerson(developmentProposal.getApproverId());
        if (approver != null) {
            approverDto.setApproverName(approver != null ? approver.getName() : "");
            approverDto.setApproverId(approver.getPrincipalId());
        }
        approverDto.setProposalNumber(developmentProposal.getProposalNumber());
        return approverDto;
    }

    protected void checkDocAndPerms(Document document) throws WorkflowException {
        checkIfPersonHasPerms(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, PermissionConstants.ASSIGN_PROPOSAL_DEVELOPMENT_WORKLOAD_APPROVER);

        if (Objects.isNull(document) ||
                !document.getDocumentHeader().getWorkflowDocument().
                        getDocumentTypeName().equalsIgnoreCase("ProposalDevelopmentDocument")) {
            throw new NotImplementedException("Approvers can only be assigned to Proposal Development Documents.");
        }
    }

    protected List<DevelopmentProposalSummaryDto> getDocumentsAtWorkloadBalancingStop(Integer limit, Integer skip) {
        List<DocumentWorkloadDetails> documentDetails = kewDocHeaderDao.getProposalsInWorkloadStop(getWorkloadBalancingStop(), limit, skip);
        List<DevelopmentProposalSummaryDto> documentList;
        try {
            List<Document> documents =  getAllDocuments(documentDetails.stream().map(DocumentWorkloadDetails::getDocumentNumber).collect(Collectors.toList()));
            documentList = documents.stream().map(document -> getDocumentSummary(document, documentDetails)).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (WorkflowException e) {
            LOG.error("An error occurred" + e);
            throw new UnprocessableEntityException("An error occurred " + e.getMessage());
        }
        return documentList;
    }

    protected String getWorkloadBalancingStop() {
        return parameterService.getParameterValueAsString(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT,
                ParameterConstants.DOCUMENT_COMPONENT, WORKLOAD_BALANCING_PRIORITY_STOP);
    }

    private DevelopmentProposalSummaryDto getDocumentSummary(Document document, List<DocumentWorkloadDetails> documentDetails) {
        final List<ActionRequestValue> allPendingRequests = actionRequestService.findAllPendingRequests(document.getDocumentNumber());
        if (CollectionUtils.isEmpty(allPendingRequests)) {
            return null;
        }
        final ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument) document;
        DevelopmentProposal proposal = proposalDevelopmentDocument.getDevelopmentProposal();
        DevelopmentProposalSummaryDto developmentProposalSummaryDto = new DevelopmentProposalSummaryDto();
        developmentProposalSummaryDto.setProposalNumber(proposal.getProposalNumber());
        developmentProposalSummaryDto.setTitle(proposal.getTitle());
        if (proposal.getApproverId() != null) {
            Person approver = personService.getPerson(proposal.getApproverId());
            developmentProposalSummaryDto.setAssignedApprover(createApprover(approver));
        }
        final String piName = Objects.isNull(proposal.getPrincipalInvestigator()) ? "" :
                proposal.getPrincipalInvestigator().getLastName() + SEPARATOR + proposal.getPrincipalInvestigator().getFirstName();
        developmentProposalSummaryDto.setPiName(piName);
        developmentProposalSummaryDto.setUnitNumber(proposal.getUnitNumber());
        developmentProposalSummaryDto.setSponsorName(proposal.getSponsorName());
        developmentProposalSummaryDto.setDocumentNumber(proposalDevelopmentDocument.getDocumentNumber());
        developmentProposalSummaryDto.setDueDate(proposal.getDeadlineDate()  == null ? null : proposal.getDeadlineDate().getTime());
        DocumentWorkloadDetails workloadDetails = documentDetails.stream().filter(documentWorkflowDetails ->
                documentWorkflowDetails.getDocumentNumber().equalsIgnoreCase(document.getDocumentNumber())).findFirst().get();
        developmentProposalSummaryDto.setStopNumber(workloadDetails.getCurrentPeopleFlowStop());
        developmentProposalSummaryDto.setLastActionTime(workloadDetails.getLastActionTime().getTime());
        setApprovers(allPendingRequests, developmentProposalSummaryDto);
        return developmentProposalSummaryDto;
    }

    private void setApprovers(List<ActionRequestValue> allPendingRequests, DevelopmentProposalSummaryDto developmentProposalSummaryDto) {
        developmentProposalSummaryDto.setAllApprovers(
                allPendingRequests.stream().
                        filter(request -> !request.isRoleRequest()).
                        map(request -> Objects.isNull(request.getPerson()) ? null : createApprover(request.getPerson())).
                        filter(Objects::nonNull).
                        collect(Collectors.toSet()));

        String primaryApprover = allPendingRequests.stream()
                .filter(actionRequestValue -> actionRequestValue.isRoleRequest())
                .findFirst()
                .map(ActionRequestValue::getQualifiedRoleNameLabel)
                .orElseGet(() -> getPersonName(allPendingRequests.stream().findFirst().get().getPerson()));

        developmentProposalSummaryDto.setPrimaryApproverName(primaryApprover);
    }

    protected ApproverDto createApprover(Person approver) {
        ApproverDto approverDto = new ApproverDto();
        approverDto.setApproverId(approver.getPrincipalId());
        approverDto.setApproverName(approver.getName());
        return approverDto;
    }

    private String getPersonName(Person person) {
        return person.getLastName() + SEPARATOR + person.getFirstName();
    }

    private List<DocumentDetailsDto> getDocumentsRoutingForUser(String routingToUser, Integer limit, Integer skip) {
        checkAndRetrievePerson(routingToUser);
        List<DocumentDetailsDto> documentList;
        List<DocumentWorkflowUserDetails> documentDetails = kewDocHeaderDao.getWorkflowDetailsOfEnrouteProposalsForUser(routingToUser, limit, skip);
        try {
            List<Document> documents =  getAllDocuments(documentDetails.stream().map(DocumentWorkflowUserDetails::getDocumentNumber).collect(Collectors.toList()));
            documentList = documents.stream().map(document -> getDocumentDetailsDto(document,
                                                                                    documentDetails.stream().
                                                                                            filter(detail -> detail.getDocumentNumber().equals(document.getDocumentNumber())).
                                                                                            findFirst().get().getSteps())).
                                                                                            collect(Collectors.toList());
        } catch (WorkflowException workflowException) {
            LOG.error("An error occurred" + workflowException);
            throw new UnprocessableEntityException("An error occurred " + workflowException.getMessage());
        }
        return documentList;
    }

    protected List<DocumentDetailsDto> documentsInProgressForUser(String inProgressForUser, Integer limit, Integer skip) {
        checkAndRetrievePerson(inProgressForUser);
        final List<DocumentSearchResult> inProgressDocuments = kewDocHeaderDao.getSavedDocuments(inProgressForUser,
                                                                                            ProposalDevelopmentConstants.KewConstants.PROPOSAL_DEVELOPMENT_DOCUMENT,
                                                                                            limit, skip);

        return CollectionUtils.isNotEmpty(inProgressDocuments) ? inProgressDocuments.stream()
                .map(documentSearchResult -> getDocumentDetailsDto(documentSearchResult))
                .collect(Collectors.toList()) :  new ArrayList<>();
    }

    protected List<DocumentDetailsDto> documentSavedForUser(String savedForUser, Integer limit, Integer skip) {
        checkAndRetrievePerson(savedForUser);
        final List<DocumentSearchResult> savedDocuments = kewDocHeaderDao.getSavedDocuments(savedForUser, null, limit, skip);

        return CollectionUtils.isNotEmpty(savedDocuments) ? savedDocuments.stream()
                .map(documentSearchResult -> getDocumentDetailsDto(documentSearchResult))
                .collect(Collectors.toList()) :  new ArrayList<>();
    }

    protected void checkIfPersonHasPerms(String namespaceCode, String permissionName) {
        final String currentUser = globalVariableService.getUserSession().getPrincipalId();
        if (!permissionService.hasPermission(currentUser, namespaceCode,permissionName)) {
            throw new UnauthorizedAccessException("User " + currentUser + " is not authorized to assign approvers");
        }
    }

    protected Person checkAndRetrievePerson(String user) {
        Person person = getPerson(user);
        final String currentUser = globalVariableService.getUserSession().getPrincipalId();
        if (!currentUser.equalsIgnoreCase(user)) {
            throw new UnauthorizedAccessException("User " + currentUser + " cannot view the documents of user " + user);
        }
        return person;
    }

    private Person getPerson(String user) {
        Person person = personService.getPerson(user);
        if (person == null) {
            throw new UnprocessableEntityException("Person with id " + user + " not found.");
        }
        return person;
    }

    private List<Document> getAllDocuments(List<String> documentNumbers) throws WorkflowException {
        return documentService.getDocumentsByListOfDocumentHeaderIds(ProposalDevelopmentDocument.class, documentNumbers);
    }

    private DocumentDetailsDto getDocumentDetailsDto(Document doc, Integer stepsAway) {
        DocumentDetailsDto documentDetailsDto = new DocumentDetailsDto();
        documentDetailsDto.setDocumentTitle(doc.getDocumentTitle());
        documentDetailsDto.setDocumentNumber(doc.getDocumentNumber());
        final WorkflowDocument workflowDocument = doc.getDocumentHeader().getWorkflowDocument();
        if (!Objects.isNull(workflowDocument)) {
            documentDetailsDto.setDocumentCreateDate(workflowDocument.getDateCreated().getMillis());
            documentDetailsDto.setDocHandlerUrl(workflowDocument.getDocumentHandlerUrl());
            documentDetailsDto.setDocumentType(workflowDocument.getDocumentTypeName());
        }
        documentDetailsDto.setStepsAway(stepsAway);
        return documentDetailsDto;
    }

    private DocumentDetailsDto getDocumentDetailsDto(DocumentSearchResult doc) {
        DocumentDetailsDto documentDetailsDto = new DocumentDetailsDto();
        documentDetailsDto.setDocumentTitle(doc.getDocument().getTitle());
        documentDetailsDto.setDocumentNumber(doc.getDocument().getDocumentId());
        long formattedDate = doc.getDocument().getDateCreated().getMillis();
        documentDetailsDto.setDocumentCreateDate(formattedDate);
        documentDetailsDto.setDocHandlerUrl(doc.getDocument().getDocumentHandlerUrl());
        documentDetailsDto.setDocumentType(doc.getDocument().getDocumentTypeName());
        return documentDetailsDto;
    }

}
