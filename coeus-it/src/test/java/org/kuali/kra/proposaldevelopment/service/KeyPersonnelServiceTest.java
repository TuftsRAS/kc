/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.proposaldevelopment.service;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.person.*;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.test.infrastructure.KcIntegrationTestBase;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.Map;

import static org.junit.Assert.*;
import static org.kuali.kra.infrastructure.Constants.PRINCIPAL_INVESTIGATOR_ROLE;

/**
 * Class intended to exercise testing of units of functionality within the
 * <code>{@link KeyPersonnelService}</code>
 *
 * @author $Author: gmcgrego $
 * @version $Revision: 1.9 $
 */
public class KeyPersonnelServiceTest extends KcIntegrationTestBase {

    private static final Logger LOG = LogManager.getLogger(KeyPersonnelServiceTest.class);

    private ProposalDevelopmentDocument document;
    private ProposalDevelopmentDocument blankDocument;
    private DocumentService documentService = null;
    private ParameterService parameterService;
    
    protected static final String NIH_SPONSOR_CODE = "000340";
    protected static final String NON_NIH_SPONSOR_CODE = "000500";
    
    protected static final String COI_ROLE_ID = "COI";
    protected static final String NIH_COI_PARAM = "personrole.nih.coi";
    protected static final String NIH_MPI_PARAM = "personrole.nih.coi.mpi";
    
    @Before
    public void setUp() throws Exception {
        GlobalVariables.setUserSession(new UserSession("quickstart"));
        documentService = KRADServiceLocatorWeb.getDocumentService();
        parameterService = KcServiceLocator.getService(ParameterService.class);
        document = (ProposalDevelopmentDocument) documentService.getNewDocument("ProposalDevelopmentDocument");
        blankDocument =(ProposalDevelopmentDocument) documentService.getNewDocument("ProposalDevelopmentDocument");
    }
    
   
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void populateDocument() {
        getKeyPersonnelService().populateDocument(blankDocument);
        assertTrue(blankDocument.getDevelopmentProposal().getInvestigatorCreditTypes().size() > 0);
    }

    /**
     * Verify the proposal person is given the lead unit of the document if the person is an investigator, 
     * initial credit splits exist and are setup properly
     */
    @Test
    public void populateProposalPerson_Investigator() {
        ProposalPerson person = new ProposalPerson();
        document.getDevelopmentProposal().setOwnedByUnitNumber("000001");
        person.setProposalPersonRoleId(PRINCIPAL_INVESTIGATOR_ROLE);
        
        getKeyPersonnelService().populateProposalPerson(person, document);
        getKeyPersonnelService().assignLeadUnit(person, document.getDevelopmentProposal().getOwnedByUnitNumber());
        boolean personHasLeadUnit = false;
        for (ProposalPersonUnit unit : person.getUnits()) {
            personHasLeadUnit |= (unit.isLeadUnit() && unit.getUnitNumber().equals("000001")); 
        }
        assertTrue(personHasLeadUnit);
        assertTrue(person.isInvestigator());
    }

    /**
     * Verify the proposal person is given the lead unit of the document if the person is an investigator, 
     * initial credit splits exist and are setup properly
     * 
     */
    @Test
    public void populateProposalPerson_KeyPerson() {
        ProposalPerson person = new ProposalPerson();
        document.getDevelopmentProposal().setOwnedByUnitNumber("000001");
        person.setProposalPersonRoleId("KP");
        assertNull(person.getHomeUnit());
        assertFalse(person.isInvestigator());
    }
    
    /**
     * Test credit split totals are created properly initially
     */
    @Test
    public void calculateCreditSplitTotals_Default() {
        ProposalPerson person = new ProposalPerson();
        document.getDevelopmentProposal().setOwnedByUnitNumber("000001");
        person.setProposalPersonRoleId(PRINCIPAL_INVESTIGATOR_ROLE);
        
        getKeyPersonnelService().populateProposalPerson(person, document);
        document.getDevelopmentProposal().addProposalPerson(person);
        
        Map<String, Map<String,ScaleTwoDecimal>> totals = getKeyPersonnelService().calculateCreditSplitTotals(document);
        for(String key : totals.keySet()) {
            LOG.info("Key = " + key);
        }
    }

    private KeyPersonnelServiceImpl getKeyPersonnelService() {
        return (KeyPersonnelServiceImpl) KcServiceLocator.getService(KeyPersonnelService.class);
    }

}
