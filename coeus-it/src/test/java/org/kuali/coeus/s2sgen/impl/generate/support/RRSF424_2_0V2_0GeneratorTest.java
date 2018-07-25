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
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.location.ProposalSite;
import org.kuali.coeus.propdev.impl.person.ProposalPerson;
import org.kuali.coeus.propdev.impl.questionnaire.ProposalDevelopmentModuleQuestionnaireBean;
import org.kuali.coeus.propdev.impl.s2s.S2sOpportunity;
import org.kuali.coeus.propdev.impl.s2s.S2sRevisionType;
import org.kuali.coeus.propdev.impl.s2s.S2sSubmissionType;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.List;

public class RRSF424_2_0V2_0GeneratorTest extends S2STestBase {

    @Override
    protected void prepareS2sData(ProposalDevelopmentDocument document) {
        super.prepareS2sData(document);

		document.getDevelopmentProposal().setSponsorProposalNumber("1234");

        S2sOpportunity s2sOpportunity = document.getDevelopmentProposal().getS2sOpportunity();
        S2sSubmissionType s2sSubmissionType = new S2sSubmissionType();
        s2sSubmissionType.setCode("1");
        s2sSubmissionType.setDescription("Preapplication");
        s2sOpportunity.setS2sSubmissionType(s2sSubmissionType);
        S2sRevisionType s2sRevisionType = new S2sRevisionType();
        s2sRevisionType.setCode("A");
        s2sOpportunity.setS2sRevisionType(s2sRevisionType);
        s2sOpportunity.setRevisionOtherDescription("revisionOtherDescription");
        document.getDevelopmentProposal().setS2sOpportunity(s2sOpportunity);
    }


    @Override
    protected void prepareData(ProposalDevelopmentDocument document)
            throws Exception {
        prepareS2sData(document);
        DevelopmentProposal developmentProposal = document
                .getDevelopmentProposal();

        ProposalType proposalType = new ProposalType();
        proposalType.setDescription("New");
        proposalType.setCode("1");
        developmentProposal.setProposalType(proposalType);

        developmentProposal
                .setProgramAnnouncementTitle("programAnnouncementTitle");

        List<ProposalSite> proposalSites;
        proposalSites = developmentProposal.getProposalSites();
        int siteNumber = 0;
        for (ProposalSite proposalSite : proposalSites) {
            if (proposalSite.getLocationTypeCode() == ProposalSite.PROPOSAL_SITE_APPLICANT_ORGANIZATION) {
                siteNumber = proposalSite.getSiteNumber();
            }
        }

        developmentProposal.setApplicantOrganization(createProposalSite(document, siteNumber, ProposalSite.PROPOSAL_SITE_APPLICANT_ORGANIZATION));

        ProposalPerson person = new ProposalPerson();
        person.setProposalPersonNumber(1);
        person.setProposalPersonRoleId("PI");
        person.setFirstName("firstname");
        person.setLastName("argLastName");
        person.setMiddleName("argMiddleName");
        person.setOfficePhone("321-321-1228");
        person.setEmailAddress("kcnotification@gmail.com");
        person.setFaxNumber("321-321-1289");
        person.setAddressLine1("argAddressLine1");
        person.setAddressLine2("argAddressLine2");
        person.setCity("Coeus");
        person.setPostalCode("53421");
        person.setCounty("UNITED STATES");
        person.setCountryCode("USA");
        person.setState("MA");
        person.setDirectoryTitle("argDirectoryTitle");
        person.setDivision("division");
        person.setDevelopmentProposal(developmentProposal);
        developmentProposal.getProposalPersons().add(person);

        ModuleQuestionnaireBean moduleQuestionnaireBean = new ProposalDevelopmentModuleQuestionnaireBean(document.getDevelopmentProposal(), false);
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
    }


    @Override
    protected String getFormGeneratorName() {
        return RRSF424_2_0V2_0Generator.class.getSimpleName();
    }
}
