/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.krms;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.core.CostElement;
import org.kuali.coeus.common.budget.framework.distribution.BudgetCostShare;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItem;
import org.kuali.coeus.common.budget.framework.period.BudgetPeriod;
import org.kuali.coeus.common.budget.framework.personnel.AppointmentType;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPerson;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPersonnelDetails;
import org.kuali.coeus.common.framework.compliance.core.SpecialReviewType;
import org.kuali.coeus.common.framework.org.Organization;
import org.kuali.coeus.common.framework.person.KcPerson;
import org.kuali.coeus.common.framework.person.KcPersonService;
import org.kuali.coeus.common.framework.person.attr.PersonAppointment;
import org.kuali.coeus.common.framework.sponsor.Sponsor;
import org.kuali.coeus.common.framework.sponsor.hierarchy.SponsorHierarchy;
import org.kuali.coeus.common.framework.unit.Unit;
import org.kuali.coeus.common.framework.unit.UnitService;
import org.kuali.coeus.common.framework.unit.admin.UnitAdministrator;
import org.kuali.coeus.common.questionnaire.framework.answer.AnswerHeader;
import org.kuali.coeus.common.questionnaire.framework.answer.QuestionnaireAnswerService;
import org.kuali.coeus.common.questionnaire.framework.core.Questionnaire;
import org.kuali.coeus.propdev.impl.attachment.Narrative;
import org.kuali.coeus.propdev.impl.attachment.NarrativeType;
import org.kuali.coeus.propdev.impl.budget.ProposalDevelopmentBudgetExt;
import org.kuali.coeus.propdev.impl.core.*;
import org.kuali.coeus.propdev.impl.location.ProposalSite;
import org.kuali.coeus.propdev.impl.person.ProposalPerson;
import org.kuali.coeus.propdev.impl.person.ProposalPersonUnit;
import org.kuali.coeus.propdev.impl.person.attachment.PropPerDocType;
import org.kuali.coeus.propdev.impl.person.attachment.ProposalPersonBiography;
import org.kuali.coeus.propdev.impl.person.question.ProposalPersonModuleQuestionnaireBean;
import org.kuali.coeus.propdev.impl.s2s.S2sAppSubmission;
import org.kuali.coeus.propdev.impl.s2s.S2sOppForms;
import org.kuali.coeus.propdev.impl.s2s.S2sOpportunity;
import org.kuali.coeus.propdev.impl.specialreview.ProposalSpecialReview;
import org.kuali.coeus.propdev.impl.specialreview.ProposalSpecialReviewAttachment;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.sql.Date;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.*;

public class PropDevJavaFunctionKrmsTermServiceImplTest {

	private Mockery context;
	private BusinessObjectService businessObjectService;
	private ParameterService parameterService;
	private DateTimeService dateTimeService;
	private WorkflowDocument workflowDocument;
	private QuestionnaireAnswerService questionnaireAnswerService;
	private UnitService unitService;
	private String TRUE = "true";
	private String FALSE = "false";
	private PropDevJavaFunctionKrmsTermServiceImpl propDevJavaFunctionKrmsTermService;
	private ProposalTypeService proposalTypeService;

	@Before()
	public void setUpMockery() {
		context = new JUnit4Mockery() {{setThreadingPolicy(new Synchroniser());}};
		propDevJavaFunctionKrmsTermService = new PropDevJavaFunctionKrmsTermServiceImpl();
		businessObjectService = context.mock(BusinessObjectService.class);
		parameterService = context.mock(ParameterService.class);
		workflowDocument = context.mock(WorkflowDocument.class);
		dateTimeService = context.mock(DateTimeService.class);
		questionnaireAnswerService = context.mock(QuestionnaireAnswerService.class);
		unitService = context.mock(UnitService.class);
		proposalTypeService = context.mock(ProposalTypeService.class);
		propDevJavaFunctionKrmsTermService.setParameterService(parameterService);
		propDevJavaFunctionKrmsTermService.setBusinessObjectService(businessObjectService);
		propDevJavaFunctionKrmsTermService.setDateTimeService(dateTimeService);
		propDevJavaFunctionKrmsTermService.setQuestionnaireAnswerService(questionnaireAnswerService);
		propDevJavaFunctionKrmsTermService.setUnitService(unitService);
		propDevJavaFunctionKrmsTermService.setProposalTypeService(proposalTypeService);
	}

	@Test
	public void test_multiplePI() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.multiplePI(developmentProposal));
		developmentProposal.getProposalPerson(0).setProposalPersonRoleId("MPI");
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.multiplePI(developmentProposal));
	}

	@Test
	public void test_s2sBudgetRule() {
		String formName = "RR Budget V1-1";
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		createS2sOpportunity().getS2sOppForms().get(0).setFormName(formName);
		developmentProposal.setS2sOpportunity(createS2sOpportunity());
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2sBudgetRule(developmentProposal, formName));
	}

	@Test
	public void test_monitoredSponsorRule() {
		final String monitoredSponsorHirearchies = "Administering Activity";
		final Map<String, String> fieldValues = new HashMap<>();
		fieldValues.put(Constants.HIERARCHY_NAME, monitoredSponsorHirearchies);
		final ArrayList<SponsorHierarchy> hierarchies = new ArrayList<>();
		SponsorHierarchy sponsorHierarchy = new SponsorHierarchy();
		sponsorHierarchy.setSponsorCode("000399");
		sponsorHierarchy.setHierarchyName(monitoredSponsorHirearchies);
		hierarchies.add(sponsorHierarchy);
		final DevelopmentProposal developmentProposal = new DevelopmentProposal() {
			@Override
			public void refreshReferenceObject(String referenceObjectName) {
				// do nothing
			}
		};
		developmentProposal.setSponsor(createSponsor());
		context.checking(new Expectations() {
			{
				oneOf(businessObjectService).findMatching(SponsorHierarchy.class, fieldValues);
				will(returnValue(hierarchies));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.monitoredSponsorRule(developmentProposal, monitoredSponsorHirearchies));
	}

	@Test
	public void test_no_primeSponsor_monitoredSponsorRule() {
		final String monitoredSponsorHirearchies = "Administering Activity";
		final Map<String, String> fieldValues = new HashMap<>();
		fieldValues.put(Constants.HIERARCHY_NAME, monitoredSponsorHirearchies);
		final ArrayList<SponsorHierarchy> hierarchies = new ArrayList<>();
		SponsorHierarchy sponsorHierarchy = new SponsorHierarchy();
		sponsorHierarchy.setSponsorCode("001234"); // not a match to excersize checking prime sponsor 
		sponsorHierarchy.setHierarchyName(monitoredSponsorHirearchies);
		hierarchies.add(sponsorHierarchy);
		final DevelopmentProposal developmentProposal = new DevelopmentProposal() {
			@Override
			public void refreshReferenceObject(String referenceObjectName) {
				// do nothing
			}
		};
		developmentProposal.setSponsor(createSponsor());
		context.checking(new Expectations() {
			{
				oneOf(businessObjectService).findMatching(SponsorHierarchy.class, fieldValues);
				will(returnValue(hierarchies));
			}
		});
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.monitoredSponsorRule(developmentProposal, monitoredSponsorHirearchies));
	}
	
	@Test
	public void test_s2sResplanRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		String narativeTypes = "Performance_sites";
		developmentProposal.setProposalNumber("11");
		List<Narrative> narratives = new ArrayList<>();
		NarrativeType narrativeType = new NarrativeType();
		narrativeType.setCode("40");
		narrativeType.setDescription(narativeTypes);
		Narrative narrative = new Narrative();
		narrative.setModuleNumber(6);
		narrative.setNarrativeTypeCode("40");
		narrative.setNarrativeType(narrativeType);
		narrative.setDevelopmentProposal(developmentProposal);
		narratives.add(narrative);
		developmentProposal.setNarratives(narratives);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2sResplanRule(developmentProposal, narativeTypes, "0"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2sResplanRule(developmentProposal, narativeTypes, "1"));
	}

	@Test
	public void test_grantsFormRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		S2sOppForms s2sOppForms = new S2sOppForms();
		s2sOppForms.setFormName("RR Budget V1-1");
		s2sOppForms.setInclude(true);
		List<S2sOppForms> oppForms = new ArrayList<>();
		oppForms.add(s2sOppForms);
		S2sOpportunity s2sOpportunity = new S2sOpportunity();
		s2sOpportunity.setS2sOppForms(oppForms);
		developmentProposal.setS2sOpportunity(s2sOpportunity);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.grantsFormRule(developmentProposal, "RR Budget V1-1"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.grantsFormRule(developmentProposal, "PHS398 Modular Budget V1-1"));
	}

	@Test
	public void test_biosketchFileNameRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		List<ProposalPersonBiography> proposalPersonBiographies = new ArrayList<>();
		PropPerDocType propPerDocType = new PropPerDocType();
		propPerDocType.setCode("1");
		propPerDocType.setDescription("Biosketch");
		ProposalPersonBiography proposalPersonBiography = new ProposalPersonBiography();
		proposalPersonBiography.setDevelopmentProposal(developmentProposal);
		proposalPersonBiography.setProposalNumber("123");
		proposalPersonBiography.setProposalPersonNumber(1);
		proposalPersonBiography.setPropPerDocType(propPerDocType);
		proposalPersonBiographies.add(proposalPersonBiography);
		developmentProposal.setPropPersonBios(proposalPersonBiographies);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.biosketchFileNameRule(developmentProposal));
	}

	@Test
	public void test_ospAdminPropPersonRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		List<UnitAdministrator> unitAdministrators = new ArrayList<>();
		UnitAdministrator unitAdministrator = new UnitAdministrator();
		unitAdministrator.setPersonId("10000000018");
		unitAdministrators.add(unitAdministrator);
		Unit unit = new Unit();
		unit.setUnitNumber("000001");
		unit.setUnitAdministrators(unitAdministrators);
		developmentProposal.setOwnedByUnit(unit);
		assertEquals(TRUE,propDevJavaFunctionKrmsTermService.ospAdminPropPersonRule(developmentProposal));
	}

	@Test
	public void test_costElementVersionLimit() {
		ProposalDevelopmentBudgetExt budget = (ProposalDevelopmentBudgetExt) getBudget();
		List<ProposalDevelopmentBudgetExt> budgets = new ArrayList<>();
		budgets.add(budget);
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setBudgets(budgets);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.costElementVersionLimit(developmentProposal, "1", "Journals", "10"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.costElementVersionLimit(developmentProposal, "1", "Journals", "100"));
	}

	@Test
	public void test_divisionCodeRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.divisionCodeRule(developmentProposal));
	}

	@Test
	public void test_divisionCodeIsFellowship() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.divisionCodeIsFellowship(developmentProposal, "4"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.divisionCodeIsFellowship(developmentProposal, "5"));
	}

	@Test
	public void test_budgetSubawardOrganizationnameRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.budgetSubawardOrganizationnameRule(developmentProposal));
	}

	@Test
	public void test_checkProposalPerson() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.checkProposalPerson(developmentProposal, "10000000018"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.checkProposalPerson(developmentProposal, "10000000011"));
	}

	@Test
	public void test_agencyProgramCodeNullRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.agencyProgramCodeNullRule(developmentProposal));
	}

	@Test
	public void test_allProposalsRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.allProposalsRule(developmentProposal));
	}

	@Test
	public void test_proposalLeadUnitInHierarchy() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.proposalLeadUnitInHierarchy(developmentProposal, "000001"));
	}

	@Test
	public void test_s2sSubawardRule() {
		S2sOppForms s2sOppForms = new S2sOppForms();
		s2sOppForms.setFormName("RR SubAward Budget V1.2");
		s2sOppForms.setInclude(true);
		S2sOppForms s2sOppForms2 = new S2sOppForms();
		s2sOppForms2.setFormName("PHS398 Modular Budget V1-2");
		s2sOppForms2.setInclude(true);
		List<S2sOppForms> oppForms = new ArrayList<>();
		oppForms.add(s2sOppForms);
		oppForms.add(s2sOppForms2);
		S2sOpportunity s2sOpportunity = new S2sOpportunity();
		s2sOpportunity.setS2sOppForms(oppForms);
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setS2sOpportunity(s2sOpportunity);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2sSubawardRule(developmentProposal, "RR SubAward Budget V1.2", "PHS398 Modular Budget V1-2"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2sSubawardRule(developmentProposal, "RR SubAward Budget V1.2", "PHS398 Modular Budget V1-1"));
	}

	@Test
	public void test_proposalGrantsRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		S2sOpportunity s2sOpportunity = new S2sOpportunity();
		developmentProposal.setS2sOpportunity(s2sOpportunity);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.proposalGrantsRule(developmentProposal));
	}

	@Test
	public void test_narrativeTypeRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		List<Narrative> narratives = new ArrayList<>();
		Narrative narrative = new Narrative();
		narrative.setModuleNumber(6);
		narrative.setNarrativeTypeCode("40");
		narrative.setDevelopmentProposal(developmentProposal);
		narratives.add(narrative);
		developmentProposal.setNarratives(narratives);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.narrativeTypeRule(developmentProposal, "40"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.narrativeTypeRule(developmentProposal, "30"));
	}

	@Test
	public void test_s2s398CoverRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		List<Narrative> narratives = new ArrayList<>();
		Narrative narrative = new Narrative();
		narrative.setModuleNumber(6);
		narrative.setNarrativeTypeCode("40");
		narrative.setDevelopmentProposal(developmentProposal);
		narratives.add(narrative);
		developmentProposal.setNarratives(narratives);
		S2sAppSubmission s2sAppSubmission = new S2sAppSubmission();
		List<S2sAppSubmission> appSubmission = new ArrayList<>();
		appSubmission.add(s2sAppSubmission);
		developmentProposal.setS2sAppSubmission(appSubmission);
		S2sOppForms s2sOppForms = new S2sOppForms();
		s2sOppForms.setFormName("PHS398_CoverPageSupplement_2_0-V2.0");
		s2sOppForms.setInclude(true);
		List<S2sOppForms> oppForms = new ArrayList<>();
		oppForms.add(s2sOppForms);
		S2sOpportunity s2sOpportunity = new S2sOpportunity();
		s2sOpportunity.setS2sOppForms(oppForms);
		developmentProposal.setS2sOpportunity(s2sOpportunity);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2s398CoverRule(developmentProposal, "PHS398_CoverPageSupplement_2_0-V2.0", "40"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2s398CoverRule(developmentProposal, "RR SubAward Budget V1.2", "40"));
	}

	@Test
	public void test_narrativeFileName() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.narrativeFileName(developmentProposal));
	}

	@Test
	public void test_costElementInVersion() {
		ProposalDevelopmentBudgetExt budget = (ProposalDevelopmentBudgetExt) getBudget();
		List<ProposalDevelopmentBudgetExt> budgets = new ArrayList<>();
		budgets.add(budget);
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setBudgets(budgets);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.costElementInVersion(developmentProposal, "1", "10"));
	}

	@Test
	public void test_investigatorKeyPersonCertificationRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		final ProposalPerson proposalPerson = developmentProposal.getProposalPerson(1);
		proposalPerson.setDevelopmentProposal(developmentProposal);
		final Questionnaire questionnaire = new Questionnaire();
		questionnaire.setQuestionnaireSeqId("10001");
		final List<AnswerHeader> answerHeaders = new ArrayList<>();
		AnswerHeader answerHeader = new AnswerHeader();
		answerHeader.setModuleItemCode("3");
		answerHeader.setModuleItemKey("5");
		answerHeader.setModuleSubItemCode("0");
		answerHeader.setModuleSubItemKey("0");
		answerHeader.setQuestionnaire(questionnaire);
		answerHeaders.add(answerHeader);
		context.checking(new Expectations() {
			{
				atLeast(1).of(workflowDocument).isApproved();
				will(returnValue(true));
			}
		});
		context.checking(new Expectations() {
			{
				oneOf(questionnaireAnswerService).getQuestionnaireAnswer(new ProposalPersonModuleQuestionnaireBean(developmentProposal, proposalPerson));
				will(returnValue(answerHeaders));
			}
		});
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.investigatorKeyPersonCertificationRule(developmentProposal));
	}

	@Test
	public void test_specifiedGGForm() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		S2sOppForms s2sOppForms = new S2sOppForms();
		s2sOppForms.setFormName("PHS398 Modular Budget V1-2");
		s2sOppForms.setInclude(true);
		List<S2sOppForms> oppForms = new ArrayList<>();
		oppForms.add(s2sOppForms);
		S2sOpportunity s2sOpportunity = new S2sOpportunity();
		s2sOpportunity.setS2sOppForms(oppForms);
		developmentProposal.setS2sOpportunity(s2sOpportunity);
		assertTrue(propDevJavaFunctionKrmsTermService.specifiedGGForm(developmentProposal, "PHS398 Modular Budget V1-2"));
	}

	@Test
	public void test_leadUnitRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.leadUnitRule(developmentProposal, "000001"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.leadUnitRule(developmentProposal, "000000"));
	}

	@Test
	public void test_sponsorGroupRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		final Map<String, Object> values = new HashMap<>();
		values.put("hierarchyName", "Routing");
		values.put("sponsorCode", developmentProposal.getSponsorCode());
		final List<SponsorHierarchy> hierarchies = new ArrayList<>();
		SponsorHierarchy sponsorHierarchy = new SponsorHierarchy();
		sponsorHierarchy.setHierarchyName("Administering Activity");
		sponsorHierarchy.setLevel1("NIH");
		sponsorHierarchy.setSponsorCode("999999");
		hierarchies.add(sponsorHierarchy);
		context.checking(new Expectations() {
			{
				oneOf(businessObjectService).findMatching(SponsorHierarchy.class, values);
				will(returnValue(hierarchies));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.sponsorGroupRule(developmentProposal, "NIH"));
	}

	@Test
	public void test_proposalAwardTypeRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setAnticipatedAwardTypeCode(123);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.proposalAwardTypeRule(developmentProposal, 123));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.proposalAwardTypeRule(developmentProposal, 121));
	}

	@Test
	public void test_s2sLeadershipRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setS2sOpportunity(createS2sOpportunity());
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2sLeadershipRule(developmentProposal));
	}

	@Test
	public void test_checkProposalPiRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setS2sOpportunity(createS2sOpportunity());
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.checkProposalPiRule(developmentProposal, "10000000018"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.checkProposalPiRule(developmentProposal, "10000000011"));
	}

	@Test
	public void test_checkProposalCoiRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.checkProposalCoiRule(developmentProposal, "10000000018"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.checkProposalCoiRule(developmentProposal, "10000000011"));
	}

	@Test
	public void test_leadUnitBelowRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		final List<Unit> units = new ArrayList<>();
		Unit unit = new Unit();
		unit.setUnitNumber("000005");
		units.add(unit);
		context.checking(new Expectations() {
			{
				oneOf(unitService).getAllSubUnits("000005");
				will(returnValue(units));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.leadUnitBelowRule(developmentProposal, "000001"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.leadUnitBelowRule(developmentProposal, "000005"));
	}

	@Test
	public void test_specialReviewRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.getPropSpecialReviews().add(createProposalSpecialReview("1"));
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.specialReviewRule(developmentProposal, "1"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.specialReviewRule(developmentProposal, "12"));
	}

	@Test
	public void test_proposalUnitRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		List<ProposalPersonUnit> proposalPersonUnits = new ArrayList<>();
		ProposalPersonUnit proposalPersonUnit = new ProposalPersonUnit() {
			@Override
			public void refreshReferenceObject(String referenceObjectName) {
				// do nothing
			}
		};
		proposalPersonUnit.setUnitNumber("000001");
		proposalPersonUnits.add(proposalPersonUnit);
		developmentProposal.getProposalPerson(0).setUnits(proposalPersonUnits);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.proposalUnitRule(developmentProposal, "000001"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.proposalUnitRule(developmentProposal, "000003"));
	}

	@Test
	public void test_sponsorTypeRule() {
		final DevelopmentProposal developmentProposal = new DevelopmentProposal() {
			@Override
			public void refreshReferenceObject(String referenceObjectName) {
				// do nothing
			}
		};
		Sponsor sponsor = createSponsor();
		sponsor.setSponsorTypeCode("1001");
		developmentProposal.setSponsor(sponsor);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.sponsorTypeRule(developmentProposal, "1001"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.sponsorTypeRule(developmentProposal, "1008"));
	}

	@Test
	public void test_s2sAttachmentNarrativeRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setS2sOpportunity(createS2sOpportunity());
		developmentProposal.getNarratives().add(createNarrative());
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2sAttachmentNarrativeRule(developmentProposal));
		developmentProposal.setNarratives(new ArrayList<>());
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2sAttachmentNarrativeRule(developmentProposal));
		developmentProposal.setS2sOpportunity(null);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2sAttachmentNarrativeRule(developmentProposal));
	}

	@Test
	public void test_s2sModularBudgetRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setS2sOpportunity(createS2sOpportunity());
		ProposalDevelopmentBudgetExt budgetExt = (ProposalDevelopmentBudgetExt) getBudget();
		budgetExt.setModularBudgetFlag(true);
		developmentProposal.setFinalBudget(budgetExt);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2sModularBudgetRule(developmentProposal));
		developmentProposal.getS2sOppForms().get(0).setInclude(false);
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2sModularBudgetRule(developmentProposal));
		developmentProposal.setS2sOpportunity(null);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2sModularBudgetRule(developmentProposal));
	}

	@Test
	public void test_s2sFederalIdRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setProposalTypeCode("1");
		developmentProposal.setS2sOpportunity(createS2sOpportunity());
		final String renewalType1 = "1";
		final String renewalType2 = "0";
		context.checking(new Expectations() {
			{
				oneOf(parameterService).getParameterValueAsString(ProposalDevelopmentDocument.class, ProposalDevelopmentConstants.PropDevParameterConstants.PROPOSAL_TYPE_CODE_RENEWAL_PARM);
				will(returnValue(renewalType1));
				oneOf(parameterService).getParameterValueAsString(ProposalDevelopmentDocument.class, ProposalDevelopmentConstants.PropDevParameterConstants.PROPOSAL_TYPE_CODE_RENEWAL_PARM);
				will(returnValue(renewalType2));
				oneOf(parameterService).getParameterValueAsString(ProposalDevelopmentDocument.class, ProposalDevelopmentConstants.PropDevParameterConstants.PROPOSAL_TYPE_CODE_RENEWAL_PARM);
				will(returnValue(renewalType1));
				oneOf(parameterService).getParameterValueAsString(ProposalDevelopmentDocument.class, ProposalDevelopmentConstants.PropDevParameterConstants.PROPOSAL_TYPE_CODE_RENEWAL_PARM);
				will(returnValue(renewalType2));
			}
		});
		
		context.checking(new Expectations() {
			{
				oneOf(proposalTypeService).getRenewProposalTypeCode();
				will(returnValue(renewalType1));
				oneOf(proposalTypeService).getRenewProposalTypeCode();
				will(returnValue(renewalType2));
				oneOf(proposalTypeService).getRenewProposalTypeCode();
				will(returnValue(renewalType1));
				oneOf(proposalTypeService).getRenewProposalTypeCode();
				will(returnValue(renewalType2));

			}
		});
		String result = propDevJavaFunctionKrmsTermService.s2sFederalIdRule(developmentProposal);
		assertNotNull(result);
		assertEquals(FALSE, result);
		result = propDevJavaFunctionKrmsTermService.s2sFederalIdRule(developmentProposal);
		assertNotNull(result);
		assertEquals(TRUE, result);
		developmentProposal.setSponsorProposalNumber("101");
		result = propDevJavaFunctionKrmsTermService.s2sFederalIdRule(developmentProposal);
		assertNotNull(result);
		assertEquals(FALSE, result);
		developmentProposal.setS2sOpportunity(null);
		result = propDevJavaFunctionKrmsTermService.s2sFederalIdRule(developmentProposal);
		assertNotNull(result);
		assertEquals(TRUE, result);
	}

	@Test
	public void test_mtdcDeviation() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		ProposalDevelopmentBudgetExt budgetExt = (ProposalDevelopmentBudgetExt) getBudget();
		budgetExt.setOhRateClassCode("1");
		developmentProposal.setFinalBudget(budgetExt);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.mtdcDeviation(developmentProposal));
		budgetExt.setOhRateClassCode("0");
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.mtdcDeviation(developmentProposal));
		developmentProposal.setFinalBudget(null);
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.mtdcDeviation(developmentProposal));
	}

	@Test
	public void test_s2sExemptionRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.getPropSpecialReviews().add(createProposalSpecialReview("1"));
		final boolean irbLinkingEnabled1 = true;
		final boolean irbLinkingEnabled2 = false;
		context.checking(new Expectations() {
			{
				oneOf(parameterService).getParameterValueAsBoolean(ProposalDevelopmentDocument.class, Constants.ENABLE_PROTOCOL_TO_DEV_PROPOSAL_LINK);
				will(returnValue(irbLinkingEnabled1));
				oneOf(parameterService).getParameterValueAsBoolean(ProposalDevelopmentDocument.class, Constants.ENABLE_PROTOCOL_TO_DEV_PROPOSAL_LINK);
				will(returnValue(irbLinkingEnabled2));
				oneOf(parameterService).getParameterValueAsBoolean(ProposalDevelopmentDocument.class, Constants.ENABLE_PROTOCOL_TO_DEV_PROPOSAL_LINK);
				will(returnValue(irbLinkingEnabled2));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2sExemptionRule(developmentProposal));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2sExemptionRule(developmentProposal));
		developmentProposal.setPropSpecialReviews(new ArrayList<>());
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2sExemptionRule(developmentProposal));
	}

	@Test
	public void test_costElement() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		ProposalDevelopmentBudgetExt budgetExt = (ProposalDevelopmentBudgetExt) getBudget();
		budgetExt.setBudgetId(1L);
		developmentProposal.getBudgets().add(budgetExt);
		final String costElement = "1";
		final Map<String, Object> values = new HashMap<>();
		values.put("costElement", costElement);
		values.put("budgetId", budgetExt.getBudgetId());
		final List<BudgetLineItem> lineItems1 = budgetExt.getBudgetLineItems();
		final List<BudgetLineItem> lineItems2 = null;
		context.checking(new Expectations() {
			{
				oneOf(businessObjectService).findMatching(BudgetLineItem.class, values);
				will(returnValue(lineItems1));
				oneOf(businessObjectService).findMatching(BudgetLineItem.class, values);
				will(returnValue(lineItems2));
				oneOf(businessObjectService).findMatching(BudgetLineItem.class, values);
				will(returnValue(lineItems1));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.costElement(developmentProposal, costElement));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.costElement(developmentProposal, costElement));
		developmentProposal.setBudgets(new ArrayList<>());
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.costElement(developmentProposal, costElement));
	}

	@Test
	public void test_activityTypeRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.activityTypeRule(developmentProposal, "4"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.activityTypeRule(developmentProposal, "5"));
	}

	@Test
	public void test_sponsor() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.sponsor(developmentProposal, "999999"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.sponsor(developmentProposal, "99"));
	}

	@Test
	public void test_nonFacultyPi() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.nonFacultyPi(developmentProposal));
		developmentProposal.getProposalPerson(0).setFacultyFlag(false);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.nonFacultyPi(developmentProposal));
	}

	@Test
	public void test_attachmentFileNameRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.getNarratives().add(createNarrative());
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.attachmentFileNameRule(developmentProposal));
		developmentProposal.getNarrative(0).setName("TestNarrative");
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.attachmentFileNameRule(developmentProposal));
	}

	@Test
	public void test_mtdcDeviationInVersion() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		ProposalDevelopmentBudgetExt budgetExt = (ProposalDevelopmentBudgetExt) getBudget();
		budgetExt.setOhRateClassCode("1");
		developmentProposal.getBudgets().add(budgetExt);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.mtdcDeviationInVersion(developmentProposal, "1"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.mtdcDeviationInVersion(developmentProposal, "2"));
		developmentProposal.setBudgets(new ArrayList<>());
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.mtdcDeviationInVersion(developmentProposal, "1"));
	}

	@Test
	public void test_proposalTypeRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setProposalTypeCode("1");
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.proposalTypeRule(developmentProposal, "1"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.proposalTypeRule(developmentProposal, "2"));
	}

	@Test
	public void test_completeNarrativeRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.getNarratives().add(createNarrative());
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.completeNarrativeRule(developmentProposal));
		developmentProposal.getNarrative(0).setModuleStatusCode("C");
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.completeNarrativeRule(developmentProposal));
	}

	@Test
	public void test_investigatorCitizenshipTypeRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		final Collection<String> citizenshipTypeParams1 = new ArrayList<>();
		citizenshipTypeParams1.add("X");
		final Collection<String> citizenshipTypeParams2 = new ArrayList<>();
		citizenshipTypeParams2.add("Y");
		context.checking(new Expectations() {
			{
				oneOf(parameterService).getParameterValuesAsString(ProposalDevelopmentDocument.class, ProposalDevelopmentUtils.PROPOSAL_PI_CITIZENSHIP_TYPE_PARM);
				will(returnValue(citizenshipTypeParams1));
				oneOf(parameterService).getParameterValuesAsString(ProposalDevelopmentDocument.class, ProposalDevelopmentUtils.PROPOSAL_PI_CITIZENSHIP_TYPE_PARM);
				will(returnValue(citizenshipTypeParams2));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.investigatorCitizenshipTypeRule(developmentProposal, "A"));
		developmentProposal.getProposalPerson(0).setCitizenshipTypeCode(1);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.investigatorCitizenshipTypeRule(developmentProposal, "C"));
		developmentProposal.getProposalPerson(0).setCitizenshipTypeCode(2);
		assertEquals(TRUE,propDevJavaFunctionKrmsTermService.investigatorCitizenshipTypeRule(developmentProposal, "N"));
		developmentProposal.getProposalPerson(0).setCitizenshipTypeCode(4);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.investigatorCitizenshipTypeRule(developmentProposal, "P"));
		developmentProposal.getProposalPerson(0).setCitizenshipTypeCode(0);
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.investigatorCitizenshipTypeRule(developmentProposal, "P"));
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.investigatorCitizenshipTypeRule(developmentProposal, "X"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.investigatorCitizenshipTypeRule(developmentProposal, "X"));
	}

	@Test
	public void test_piAppointmentTypeRule() {
		final DevelopmentProposal developmentProposal1 = createDevelopmentProposal();
		final DevelopmentProposal developmentProposal2 = createDevelopmentProposal();
		final List<AppointmentType> appointmentTypes1 = new ArrayList<>();
		final List<AppointmentType> appointmentTypes2 = new ArrayList<>();
		final AppointmentType appointmentType = new AppointmentType();
		appointmentType.setDescription("Researcher");
		appointmentTypes1.add(appointmentType);
		final KcPersonService kcPersonService = context.mock(KcPersonService.class);
		developmentProposal1.getProposalPerson(0).setKcPersonService(kcPersonService);
		developmentProposal2.setProposalPersons(new ArrayList<>());
		final KcPerson kcPerson1 = new KcPerson();
		final KcPerson kcPerson2 = new KcPerson();
		final List<PersonAppointment> personAppointments = new ArrayList<>();
		final PersonAppointment personAppointment = new PersonAppointment();
		personAppointment.setJobTitle("Researcher");
		personAppointments.add(personAppointment);
		kcPerson1.getExtendedAttributes().setPersonAppointments(personAppointments);
		kcPerson2.getExtendedAttributes().setPersonAppointments(new ArrayList<>());
		context.checking(new Expectations() {
			{
				oneOf(businessObjectService).findAll(AppointmentType.class);
				will(returnValue(appointmentTypes1));
				oneOf(kcPersonService).getKcPersonByPersonId("10000000018");
				will(returnValue(kcPerson1));
				oneOf(businessObjectService).findAll(AppointmentType.class);
				will(returnValue(appointmentTypes2));
				oneOf(kcPersonService).getKcPersonByPersonId("10000000018");
				will(returnValue(kcPerson1));
				oneOf(businessObjectService).findAll(AppointmentType.class);
				will(returnValue(appointmentTypes2));
				oneOf(kcPersonService).getKcPersonByPersonId("10000000018");
				will(returnValue(kcPerson2));
				oneOf(businessObjectService).findAll(AppointmentType.class);
				will(returnValue(appointmentTypes2));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.piAppointmentTypeRule(developmentProposal1));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.piAppointmentTypeRule(developmentProposal1));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.piAppointmentTypeRule(developmentProposal1));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.piAppointmentTypeRule(developmentProposal2));
	}

	@Test
	public void test_proposalCampusRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		final List<ProposalPersonUnit> units = new ArrayList<>();
		units.add(createProposalPersonUnit("000001"));
		developmentProposal.getProposalPerson(0).setUnits(units);
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.proposalCampusRule(developmentProposal, "00"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.proposalCampusRule(developmentProposal, "11"));
		developmentProposal.getProposalPerson(0).setUnits(new ArrayList<>());
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.proposalCampusRule(developmentProposal, "00"));
		developmentProposal.setProposalPersons(new ArrayList<>());
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.proposalCampusRule(developmentProposal, "00"));
	}

	@Test
	public void test_routedToOSPRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		context.checking(new Expectations() {
			{
				oneOf(workflowDocument).isApproved();
				will(returnValue(true));
				oneOf(workflowDocument).isApproved();
				will(returnValue(false));
				oneOf(workflowDocument).isDisapproved();
				will(returnValue(true));
				oneOf(workflowDocument).isApproved();
				will(returnValue(false));
				oneOf(workflowDocument).isDisapproved();
				will(returnValue(false));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.routedToOSPRule(developmentProposal));
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.routedToOSPRule(developmentProposal));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.routedToOSPRule(developmentProposal));
	}

	@Test
	public void test_proposalUnitBelow() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		final List<ProposalPersonUnit> personUnits = new ArrayList<>();
		personUnits.add(createProposalPersonUnit("000001"));
		developmentProposal.getProposalPerson(0).setUnits(personUnits);
		final Map<String, Object> fieldValues = new HashMap<>();
		fieldValues.put("parentUnitNumber", "000001");
		final List<Unit> units1 = new ArrayList<>();
		units1.add(createUnit("000001"));
		final List<Unit> units2 = new ArrayList<>();
		units2.add(createUnit("100001"));
		final List<Unit> units3 = new ArrayList<>();
		context.checking(new Expectations() {
			{
				oneOf(unitService).getAllSubUnits("000001");
				will(returnValue(units1));
				oneOf(businessObjectService).findMatching(Unit.class, fieldValues);
				will(returnValue(units1));
				allowing(unitService).getAllSubUnits("100001");
				will(returnValue(units2));
				allowing(businessObjectService).findMatching(Unit.class, fieldValues);
				will(returnValue(units2));
				oneOf(unitService).getAllSubUnits("100001");
				will(returnValue(units2));
				oneOf(businessObjectService).findMatching(Unit.class, fieldValues);
				will(returnValue(units2));
				oneOf(unitService).getAllSubUnits("100001");
				will(returnValue(units3));
				oneOf(businessObjectService).findMatching(Unit.class, fieldValues);
				will(returnValue(units3));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.proposalUnitBelow(developmentProposal, "000001"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.proposalUnitBelow(developmentProposal, "100001"));
		developmentProposal.getProposalPerson(0).setUnits(new ArrayList<>());
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.proposalUnitBelow(developmentProposal, "100001"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.proposalUnitBelow(developmentProposal, "100001"));
	}

	@Test
	public void test_usesRolodex() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.usesRolodex(developmentProposal, 1));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.usesRolodex(developmentProposal, 2));
	}

	@Test
	public void test_competitionIdRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.setS2sOpportunity(createS2sOpportunity());
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.competitionIdRule(developmentProposal, "101"));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.competitionIdRule(developmentProposal, "111"));
	}

	@Test
	public void test_specialReviewDateRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		developmentProposal.getPropSpecialReviews().add(createProposalSpecialReview("1"));
		final Date currentDate = new Date(System.currentTimeMillis());
		context.checking(new Expectations() {
			{
				allowing(dateTimeService).getCurrentSqlDateMidnight();
				will(returnValue(currentDate));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.specialReviewDateRule(developmentProposal));
		developmentProposal.getPropSpecialReview(0).setApplicationDate(currentDate);
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.specialReviewDateRule(developmentProposal));
		Date applicationDate = new Date(System.currentTimeMillis());
		applicationDate.setDate(applicationDate.getDate() - 1);
		developmentProposal.getPropSpecialReview(0).setApplicationDate(applicationDate);
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.specialReviewDateRule(developmentProposal));
	}

	@Test
	public void test_deadlineDateRule() throws ParseException {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		final Date deadLineDate = new Date(System.currentTimeMillis());
		final Date currentDate = new Date(System.currentTimeMillis());
		currentDate.setDate(currentDate.getDate() - 1);
		developmentProposal.setDeadlineDate(currentDate);
		context.checking(new Expectations() {
			{
				allowing(dateTimeService).convertToSqlDate(deadLineDate.toString());
				will(returnValue(deadLineDate));
			}
		});
		assertEquals(TRUE, propDevJavaFunctionKrmsTermService.deadlineDateRule(developmentProposal, deadLineDate.toString()));
		currentDate.setDate(deadLineDate.getDate() + 1);
		developmentProposal.setDeadlineDate(currentDate);
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.deadlineDateRule(developmentProposal, deadLineDate.toString()));
		developmentProposal.setDeadlineDate(deadLineDate);
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.deadlineDateRule(developmentProposal, deadLineDate.toString()));
		assertEquals(FALSE, propDevJavaFunctionKrmsTermService.deadlineDateRule(null, deadLineDate.toString()));
	}

    @Test
    public void test_costShareUnitRule() {
        final DevelopmentProposal developmentProposal = createDevelopmentProposal();

        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareUnitRule(developmentProposal, "000001, IN-IN"));

        ProposalDevelopmentBudgetExt budget = (ProposalDevelopmentBudgetExt) getBudget();
        BudgetCostShare costShare1 = new BudgetCostShare();
        costShare1.setUnitNumber("000001");
        budget.getBudgetCostShares().add(costShare1);
        BudgetCostShare costShare2 = new BudgetCostShare();
        costShare2.setUnitNumber("IN-IN");
        budget.getBudgetCostShares().add(costShare2);
        developmentProposal.getBudgets().add(0, budget);
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareUnitRule(developmentProposal, "000001, IN-IN"));

        developmentProposal.setFinalBudget(budget);
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareUnitRule(developmentProposal, "000001, IN-IN"));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareUnitRule(developmentProposal, ""));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareUnitRule(developmentProposal, null));
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareUnitRule(developmentProposal, "IN-IN"));

        BudgetCostShare costShare3 = new BudgetCostShare();
        costShare3.setUnitNumber("BL-BL");
        developmentProposal.getFinalBudget().getBudgetCostShares().add(costShare3);
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareUnitRule(developmentProposal, "000001, IN-IN, BL-BL"));
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareUnitRule(developmentProposal, "000001,IN-IN,BL-BL"));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareUnitRule(developmentProposal, "000001;IN-IN;BL-BL"));

        developmentProposal.getFinalBudget().getBudgetCostShares().forEach(budgetCostShare -> budgetCostShare.setUnitNumber(null));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareUnitRule(developmentProposal, "000001, IN-IN, BL-BL"));
    }

    @Test
    public void test_costShareTypeInBudgetCostShareRule() {
        final DevelopmentProposal developmentProposal = createDevelopmentProposal();

        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, "3, 4, 5"));

        ProposalDevelopmentBudgetExt budget = (ProposalDevelopmentBudgetExt) getBudget();
        BudgetCostShare costShare1 = new BudgetCostShare();
        costShare1.setCostShareTypeCode(1);
        budget.getBudgetCostShares().add(costShare1);
        BudgetCostShare costShare2 = new BudgetCostShare();
        costShare2.setCostShareTypeCode(2);
        budget.getBudgetCostShares().add(costShare2);
        developmentProposal.getBudgets().add(0, budget);
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, "3, 4, 5"));

        costShare1 = new BudgetCostShare();
        costShare1.setCostShareTypeCode(1);
        budget.getBudgetCostShares().add(costShare1);
        costShare2 = new BudgetCostShare();
        costShare2.setCostShareTypeCode(2);
        developmentProposal.getBudgets().get(0).getBudgetCostShares().add(costShare2);
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, "1, 2"));

        developmentProposal.setFinalBudget(budget);
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, "1, 2"));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, ""));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, null));
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, "1"));

        BudgetCostShare costShare3 = new BudgetCostShare();
        costShare3.setCostShareTypeCode(3);
        developmentProposal.getFinalBudget().getBudgetCostShares().add(costShare3);
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, "3, 4, 5"));
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, "3,4,5"));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, "3;4;5"));

        developmentProposal.getFinalBudget().getBudgetCostShares().forEach(budgetCostShare -> budgetCostShare.setCostShareTypeCode(null));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareTypeInBudgetCostShareRule(developmentProposal, "3, 4, 5"));

    }

    @Test
    public void test_costShareSourceAccountInBudgetCostShareRule() {
        final DevelopmentProposal developmentProposal = createDevelopmentProposal();

        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, "2345, 3456, 6589"));

        ProposalDevelopmentBudgetExt budget = (ProposalDevelopmentBudgetExt) getBudget();
        BudgetCostShare costShare1 = new BudgetCostShare();
        costShare1.setSourceAccount("23456");
        budget.getBudgetCostShares().add(costShare1);
        BudgetCostShare costShare2 = new BudgetCostShare();
        costShare2.setSourceAccount("1234");
        budget.getBudgetCostShares().add(costShare2);
        developmentProposal.getBudgets().add(0, budget);
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, "2345, 3456, 6589"));

        costShare1 = new BudgetCostShare();
        costShare1.setSourceAccount("23456");
        budget.getBudgetCostShares().add(costShare1);
        costShare2 = new BudgetCostShare();
        costShare2.setSourceAccount("1234");
        developmentProposal.getBudgets().get(0).getBudgetCostShares().add(costShare2);
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, "2345, 3456, 6589"));

        developmentProposal.setFinalBudget(budget);
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, "23456, 2"));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, ""));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, null));
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, "1234"));

        BudgetCostShare costShare3 = new BudgetCostShare();
        costShare3.setSourceAccount("23456");
        developmentProposal.getFinalBudget().getBudgetCostShares().add(costShare3);
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, "23456, 3456, 6589"));
        Assert.assertTrue(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, "23456,3456,6589"));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, "23456; 3456; 6589"));

        developmentProposal.getFinalBudget().getBudgetCostShares().forEach(budgetCostShare -> budgetCostShare.setSourceAccount(null));
        Assert.assertFalse(propDevJavaFunctionKrmsTermService.costShareSourceAccountRule(developmentProposal, "23456, 3456, 6589"));

    }

    @Test
	public void test_humanSubjectsSpecialReviewContainsPropertyValue() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		final String studyTitle = "This is the title of the study";

		Assert.assertEquals(FALSE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "hiddenInHierarchy", "false"));

		ProposalSpecialReview otherSpecialReview = createProposalSpecialReview("4");
		otherSpecialReview.setApprovalTypeCode("3");
		otherSpecialReview.setDevelopmentProposal(developmentProposal);
		otherSpecialReview.setComments("this one does have comments");
		developmentProposal.getPropSpecialReviews().add(otherSpecialReview);

		Assert.assertEquals(FALSE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "specialReviewTypeCode", "4"));

		ProposalSpecialReview humanSubjectsSpecialReview = createProposalSpecialReview(SpecialReviewType.HUMAN_SUBJECTS);
		ProposalSpecialReviewAttachment attachment = new ProposalSpecialReviewAttachment();
		attachment.setClinicalTrial(false);
		attachment.setIsAttachmentDelayedOnset(true);
		attachment.setStudyTitle(studyTitle);
		humanSubjectsSpecialReview.setHiddenInHierarchy(true);
		humanSubjectsSpecialReview.setHierarchyProposalNumber("dummy");
		humanSubjectsSpecialReview.setProtocolNumber("dummy");
		humanSubjectsSpecialReview.setComments("");
		humanSubjectsSpecialReview.setSpecialReviewAttachment(attachment);
		humanSubjectsSpecialReview.setDevelopmentProposal(developmentProposal);
		developmentProposal.getPropSpecialReviews().add(humanSubjectsSpecialReview);

		// Can check simple property values
		Assert.assertEquals(FALSE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "approvalTypeCode", "3"));
		Assert.assertEquals(TRUE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "approvalTypeCode", "4"));
		Assert.assertEquals(TRUE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "hierarchyProposalNumber", "dummy"));
		Assert.assertEquals(TRUE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "hiddenInHierarchy", "true"));

		// Can check nested property values
		Assert.assertEquals(TRUE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "specialReviewAttachment.isAttachmentDelayedOnset", "true"));
		Assert.assertEquals(TRUE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "specialReviewAttachment.clinicalTrial", "false"));
		Assert.assertEquals(TRUE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "specialReviewAttachment.studyTitle", studyTitle));

		// Can check null / emptiness correctly
		Assert.assertEquals(TRUE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "specialReviewAttachment.fileDataId", "null"));
		Assert.assertEquals(TRUE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "specialReviewAttachment.fileDataId", "empty"));
		Assert.assertEquals(FALSE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "protocolNumber", "null"));
		Assert.assertEquals(TRUE, propDevJavaFunctionKrmsTermService.humanSubjectsSpecialReviewContainsPropertyValue(developmentProposal, "comments", "empty"));
	}

	@Test
	public void test_clinicalTrialQuestionnaireRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		final String studyTitle = "This is the title of the study";

		Assert.assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2sHumanSubjectExists(developmentProposal));

		ProposalSpecialReview otherSpecialReview = createProposalSpecialReview("4");
		otherSpecialReview.setApprovalTypeCode("3");
		otherSpecialReview.setDevelopmentProposal(developmentProposal);
		otherSpecialReview.setComments("this one does have comments");
		developmentProposal.getPropSpecialReviews().add(otherSpecialReview);

		Assert.assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2sHumanSubjectExists(developmentProposal));

		ProposalSpecialReview humanSubjectsSpecialReview = createProposalSpecialReview(SpecialReviewType.HUMAN_SUBJECTS);
		ProposalSpecialReviewAttachment attachment = new ProposalSpecialReviewAttachment();
		attachment.setClinicalTrial(false);
		attachment.setIsAttachmentDelayedOnset(true);
		attachment.setStudyTitle(studyTitle);
		humanSubjectsSpecialReview.setHiddenInHierarchy(true);
		humanSubjectsSpecialReview.setHierarchyProposalNumber("dummy");
		humanSubjectsSpecialReview.setProtocolNumber("dummy");
		humanSubjectsSpecialReview.setComments("");
		humanSubjectsSpecialReview.setSpecialReviewAttachment(attachment);
		humanSubjectsSpecialReview.setDevelopmentProposal(developmentProposal);
		developmentProposal.getPropSpecialReviews().add(humanSubjectsSpecialReview);

		Assert.assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2sHumanSubjectExists(developmentProposal));

		developmentProposal.getPropSpecialReviews().remove(humanSubjectsSpecialReview);
		developmentProposal.setS2sOpportunity(new S2sOpportunity());

		Assert.assertEquals(FALSE, propDevJavaFunctionKrmsTermService.s2sHumanSubjectExists(developmentProposal));


		developmentProposal.getPropSpecialReviews().add(humanSubjectsSpecialReview);

		Assert.assertEquals(TRUE, propDevJavaFunctionKrmsTermService.s2sHumanSubjectExists(developmentProposal));
	}

	@Test
	public void test_performanceSiteExistsRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertFalse(propDevJavaFunctionKrmsTermService.performanceSiteLocationExists(developmentProposal));
		developmentProposal.addPerformanceSite(new ProposalSite());
		assertTrue(propDevJavaFunctionKrmsTermService.performanceSiteLocationExists(developmentProposal));
	}
	
	@Test
	public void test_otherOrganizationExistsRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		assertFalse(propDevJavaFunctionKrmsTermService.otherOrganizationExists(developmentProposal));
		developmentProposal.addOtherOrganization(createOtherOrganization());
		assertTrue(propDevJavaFunctionKrmsTermService.otherOrganizationExists(developmentProposal));
	}
	
	@Test
	public void test_proposalPersonUnitBelowRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		String subUnitNumber = "000005";
		String personUnitNumber = "000005";
		String paramUnitNumber = "000001";
		String invalidUnitNumber = "000003";
		String projectRole = "PI";
		setProposalPersonUnit(developmentProposal, personUnitNumber);
		
		final List<Unit> units = new ArrayList<>();
		Unit unit = new Unit();
		unit.setUnitNumber(subUnitNumber);
		units.add(unit);
		context.checking(new Expectations() {
			{
				exactly(2).of(unitService).getAllSubUnits(paramUnitNumber);
				will(returnValue(units));
			}
		});
		assertTrue(propDevJavaFunctionKrmsTermService.proposalPersonUnitBelowRule(developmentProposal, projectRole, paramUnitNumber));
		setProposalPersonUnit(developmentProposal, invalidUnitNumber);
		assertFalse(propDevJavaFunctionKrmsTermService.proposalPersonUnitBelowRule(developmentProposal, projectRole, paramUnitNumber));
	} 

	@Test
	public void test_proposalPersonUnitRule() {
		final DevelopmentProposal developmentProposal = createDevelopmentProposal();
		String personUnitNumber = "000001";
		String invalidUnitNumber = "000003";
		String projectRole = "PI";
		setProposalPersonUnit(developmentProposal, personUnitNumber);
		assertTrue(propDevJavaFunctionKrmsTermService.proposalPersonUnitRule(developmentProposal, projectRole, personUnitNumber));
		assertFalse(propDevJavaFunctionKrmsTermService.proposalPersonUnitRule(developmentProposal, projectRole, invalidUnitNumber));
	}
	
	protected void setProposalPersonUnit(DevelopmentProposal developmentProposal, String personUnitNumber) {
		List<ProposalPersonUnit> proposalPersonUnits = new ArrayList<>();
		proposalPersonUnits.add(createProposalPersonUnit(personUnitNumber));
		developmentProposal.getProposalPerson(0).setUnits(proposalPersonUnits);
	}
	
	public ProposalSite createOtherOrganization() {
		String defaultCongressionalDistrict = "TEST";
		Organization organization = new Organization();
		organization.setCongressionalDistrict(defaultCongressionalDistrict);
		ProposalSite proposalSite = new ProposalSite();
		proposalSite.setOrganization(organization);
		return proposalSite;
	}
	
	public DevelopmentProposal createDevelopmentProposal() {
		final ProposalDevelopmentDocument proposalDevelopmentDocument = new ProposalDevelopmentDocument();
		proposalDevelopmentDocument.getDocumentHeader().setDocumentNumber("123");
		proposalDevelopmentDocument.getDocumentHeader().setWorkflowDocument(workflowDocument);
		DevelopmentProposal developmentProposal = proposalDevelopmentDocument.getDevelopmentProposal();
		developmentProposal.setProposalNumber("123");
		developmentProposal.setSponsorCode("999999");
		developmentProposal.setTitle("Project title");
		developmentProposal.setActivityTypeCode("4");
		developmentProposal.setOwnedByUnitNumber("000001");
		developmentProposal.setRequestedStartDateInitial(new Date(Calendar.getInstance().getTime().getTime()));
		developmentProposal.setRequestedEndDateInitial(new Date(Calendar.getInstance().getTime().getTime()));
		List<ProposalPerson> persons = new ArrayList<>();
		ProposalPerson proposalPerson = new ProposalPerson();
		proposalPerson.setFirstName("ALAN");
		proposalPerson.setLastName("MCAFEE");
		proposalPerson.setFullName("ALAN  MCAFEE");
		proposalPerson.setPersonId("10000000018");
		proposalPerson.setUserName("aemcafee");
		proposalPerson.setProposalPersonRoleId("PI");
		proposalPerson.setAddressLine1("1135 Kuali Drive");
		proposalPerson.setActive(true);
		proposalPerson.setFacultyFlag(true);
		proposalPerson.setCitizenshipTypeCode(3);
		proposalPerson.setRolodexId(1);
		proposalPerson.setDevelopmentProposal(proposalDevelopmentDocument.getDevelopmentProposal());
		persons.add(proposalPerson);
		developmentProposal.setProposalPersons(persons);
		return developmentProposal;
	}

	private Budget getBudget() {
		final  List<BudgetLineItem> lineItems = new ArrayList<>();
		ProposalDevelopmentBudgetExt budget = new ProposalDevelopmentBudgetExt() {
			@Override
			public List<BudgetLineItem> getBudgetLineItems() {
				return lineItems;
			}
		};

		List<BudgetPeriod> periods = new ArrayList<>();
		BudgetPeriod period = new BudgetPeriod();


		BudgetLineItem lineItem = budget.getNewBudgetLineItem();
		lineItem.setLineItemSequence(1);

		List<BudgetPersonnelDetails> lineItemDetails = new ArrayList<>();
		BudgetPersonnelDetails details = lineItem.getNewBudgetPersonnelLineItem();
		details.setLineItemSequence(1);
		details.setPersonSequenceNumber(1);

		lineItemDetails.add(details);

		lineItem.setBudgetPersonnelDetailsList(lineItemDetails);
		lineItem.setCostElement("Journals");
		CostElement costElementBO = new CostElement();
		costElementBO.setCostElement("420825");
		costElementBO.setDescription("Journals");
		lineItem.setCostElementBO(costElementBO);
		lineItem.setLineItemCost(ScaleTwoDecimal.ONE_HUNDRED);
		lineItems.add(lineItem);

		period.setBudgetLineItems(lineItems);
		periods.add(period);

		List<BudgetPerson> persons = new ArrayList<>();
		BudgetPerson person = new BudgetPerson() {
			private String jc;

			@Override
			public void setJobCode(String jobCode) {
				jc = jobCode;
			}

			@Override
			public String getJobCode() {
				return jc;
			}
		};

		person.setPersonSequenceNumber(1);
		person.setBudgetId(1L);
		persons.add(person);

		budget.setBudgetPeriods(periods);
		budget.setBudgetPersons(persons);
		budget.setBudgetVersionNumber(1);
		budget.setVersionNumber(1L);

		return budget;
	}

	public Sponsor createSponsor() {
		Sponsor sponsor = new Sponsor();
		sponsor.setSponsorCode("000399");
		return sponsor;
	}

	public S2sOpportunity createS2sOpportunity() {
		S2sOppForms s2sOppForms = new S2sOppForms();
		s2sOppForms.setFormName("PHS398 Modular Budget V1-2");
		s2sOppForms.setInclude(true);
		List<S2sOppForms> oppForms = new ArrayList<>();
		oppForms.add(s2sOppForms);
		S2sOpportunity s2sOpportunity = new S2sOpportunity();
		s2sOpportunity.setS2sOppForms(oppForms);
		s2sOpportunity.setCompetitionId("101");
		return s2sOpportunity;
	}

	public Narrative createNarrative() {
		Narrative narrative = new Narrative();
		narrative.setNarrativeTypeCode("61");
		narrative.setName("Test Narrative");
		narrative.setModuleStatusCode("I");
		return narrative;
	}

	public ProposalSpecialReview createProposalSpecialReview(
			String specialReviewTypeCode) {
		ProposalSpecialReview proposalSpecialReview = new ProposalSpecialReview();
		proposalSpecialReview.setSpecialReviewTypeCode(specialReviewTypeCode);
		proposalSpecialReview.setApprovalTypeCode("4");
		Date applicationDate = new Date(System.currentTimeMillis());
		applicationDate.setDate(applicationDate.getDate() + 1);
		proposalSpecialReview.setApplicationDate(applicationDate);
		return proposalSpecialReview;
	}

	public ProposalPersonUnit createProposalPersonUnit(String unitNumber) {
		ProposalPersonUnit personUnit = new ProposalPersonUnit() {
			@Override
			public void refreshReferenceObject(String referenceObjectName) {}
		};
		personUnit.setLeadUnit(true);
		personUnit.setUnitNumber(unitNumber);
		return personUnit;
	}

	public Unit createUnit(String unitNumber) {
		Unit unit = new Unit();
		unit.setUnitNumber(unitNumber);
		return unit;
	}
}
