/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.s2sgen.impl.generate.support;

import org.kuali.coeus.common.framework.type.ProposalType;
import org.kuali.coeus.common.questionnaire.framework.answer.Answer;
import org.kuali.coeus.common.questionnaire.framework.answer.AnswerHeader;
import org.kuali.coeus.common.questionnaire.framework.answer.ModuleQuestionnaireBean;
import org.kuali.coeus.common.questionnaire.framework.answer.QuestionnaireAnswerService;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.location.ProposalSite;
import org.kuali.coeus.propdev.impl.person.ProposalPerson;
import org.kuali.coeus.propdev.impl.questionnaire.ProposalDevelopmentModuleQuestionnaireBean;
import org.kuali.coeus.propdev.impl.s2s.S2sSubmissionType;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.ArrayList;
import java.util.List;

public abstract class SF424BaseGeneratorTest extends S2STestBase {

    @Override
    protected void prepareS2sData(ProposalDevelopmentDocument document) {
        super.prepareS2sData(document);

        document.getDevelopmentProposal().getS2sOpportunity().setOpportunityId("1234");

        S2sSubmissionType stype = new S2sSubmissionType();
        stype.setCode("2");

        document.getDevelopmentProposal().getS2sOpportunity().setS2sSubmissionType(stype);
        document.getDevelopmentProposal().getS2sOpportunity().setS2sSubmissionTypeCode("2");
    }

    @Override
    protected void prepareData(ProposalDevelopmentDocument document) throws Exception {

        ProposalType ptype = new ProposalType();
        ptype.setCode("1");

        document.getDevelopmentProposal().setProposalType(ptype);

        ProposalPerson proposalPerson = new ProposalPerson();
        proposalPerson.setProposalPersonRoleId("PI");
        proposalPerson.setFirstName("Philip");
        proposalPerson.setLastName("Berg");
        proposalPerson.setDirectoryTitle("Title");
        proposalPerson.setAddressLine1("Sanfransisco");
        proposalPerson.setAddressLine2("Newyork");
        proposalPerson.setCity("Los Angels");
        proposalPerson.setCounty("County");
        proposalPerson.setState("AL");
        proposalPerson.setPostalCode("58623415");
        proposalPerson.setCountryCode("USA");
        proposalPerson.setOfficePhone("63254152");
        proposalPerson.setFaxNumber("52374512");
        proposalPerson.setDegree("MS");
        proposalPerson.setProjectRole("Manager");
        proposalPerson.setYearGraduated("2006");
        proposalPerson.setEmailAddress("philip@hotmail.com");
        proposalPerson.setOptInCertificationStatus(true);
        proposalPerson.setOptInUnitStatus(true);
        proposalPerson.setProposalPersonNumber(1001);
        proposalPerson.setRolodexId(1);
        proposalPerson.setDevelopmentProposal(document.getDevelopmentProposal());

        List<ProposalPerson> proposalPersonList = new ArrayList<ProposalPerson>();
        proposalPersonList.add(proposalPerson);

        document.getDevelopmentProposal().setProposalPersons(proposalPersonList);

        ModuleQuestionnaireBean moduleQuestionnaireBean = new ProposalDevelopmentModuleQuestionnaireBean(document.getDevelopmentProposal());
        final List<AnswerHeader> answerHeaders = KcServiceLocator.getService(QuestionnaireAnswerService.class).getQuestionnaireAnswer(moduleQuestionnaireBean);

        for (AnswerHeader answerHeader : answerHeaders) {
            if (answerHeader != null) {
                List<Answer> answerDetails = answerHeader.getAnswers();
                for (Answer answers : answerDetails) {
                    if (Integer.valueOf(128).equals(answers.getQuestion().getQuestionSeqId())) {
                        answers.setAnswer("N");
                        answers.getQuestionnaireQuestion().setRuleId("");
                    } else if (Integer.valueOf(129).equals(answers.getQuestion().getQuestionSeqId())) {
                        answers.setAnswer("Y");
                        answers.getQuestionnaireQuestion().setRuleId("");
                    } else if (Integer.valueOf(131).equals(answers.getQuestion().getQuestionSeqId())) {
                        answers.setAnswer("Program not covered by EO 12372");
                    }
                    else if (Integer.valueOf(130).equals(answers.getQuestion().getQuestionSeqId())) {
                        answers.setAnswer("03/03/2003");
                        answers.getQuestionnaireQuestion().setRuleId("");
                    }
                }
            }
        }
        KcServiceLocator.getService(BusinessObjectService.class).save(answerHeaders);


        List<ProposalSite> proposalSites = new ArrayList<>();
        int siteNumber = 0;

        proposalSites.add(createProposalSite(document, ++siteNumber, ProposalSite.PROPOSAL_SITE_PERFORMING_ORGANIZATION));
        proposalSites.add(createProposalSite(document, ++siteNumber, ProposalSite.PROPOSAL_SITE_APPLICANT_ORGANIZATION));
        document.getDevelopmentProposal().setProposalSites(proposalSites);
    }
}
