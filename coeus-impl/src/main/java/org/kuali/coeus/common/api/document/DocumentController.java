/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.coeus.common.api.document.dto.DocumentDetailsDto;
import org.kuali.coeus.common.api.document.service.KewDocHeaderDao;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.rest.UnauthorizedAccessException;
import org.kuali.coeus.sys.framework.rest.UnprocessableEntityException;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
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

    private static final Log LOG = LogFactory.getLog(DocumentController.class);

    @RequestMapping(method= RequestMethod.GET, value="/enroute-documents", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<DocumentDetailsDto> documentsRoutingToUser(@RequestParam(value = "user") String user,
                                                           @RequestParam(value = "limit", required = false) Integer limit,
                                                           @RequestParam(value = "skip", required = false) Integer skip) {
        return getDocumentsRoutingForUser(user, limit, skip);
    }

    @RequestMapping(method= RequestMethod.GET, value="/saved-documents", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<DocumentDetailsDto> documentsSavedForUser(@RequestParam(value = "user") String user,
                                                          @RequestParam(value = "limit", required = false) Integer limit,
                                                          @RequestParam(value = "skip", required = false) Integer skip) {
        return documentSavedForUser(user, limit, skip);
    }

    private List<DocumentDetailsDto> getDocumentsRoutingForUser(String routingToUser, Integer limit, Integer skip) {
        checkAndRetrievePerson(routingToUser);
        List<DocumentDetailsDto> documentList;
        List<DocumentWorkflowUserDetails> documentDetails = kewDocHeaderDao.getWorkflowDetailsForEnrouteDocuments(routingToUser, limit, skip);

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

    protected List<DocumentDetailsDto> documentSavedForUser(String savedForUser, Integer limit, Integer skip) {
        checkAndRetrievePerson(savedForUser);
        final List<DocumentSearchResult> savedDocuments = kewDocHeaderDao.getSavedDocuments(savedForUser, limit, skip);

        return CollectionUtils.isNotEmpty(savedDocuments) ? savedDocuments.stream()
                .map(documentSearchResult -> getDocumentDetailsDto(documentSearchResult))
                .collect(Collectors.toList()) :  new ArrayList<>();
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
