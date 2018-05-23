/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.proposal.impl.report;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.coeus.common.framework.print.PendingReportBean;
import org.kuali.coeus.common.framework.version.VersionStatus;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalPerson;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * OJB implementation of PendingReportDao using OJB Report Query (see http://db.apache.org/ojb/docu/guides/query.html#Report+Queries)
 */
@Component("pendingReportDao")
public class PendingReportDaoOjb extends BaseReportDaoOjb implements PendingReportDao {

    @Override
    public List<PendingReportBean> queryForPendingSupport(String personId, Collection<String> excludedProposalTypes) throws WorkflowException {
        List<PendingReportBean> data = new ArrayList<>();
        for(InstitutionalProposalPerson ipPerson : executePendingSupportQuery(personId, excludedProposalTypes)) {
            PendingReportBean bean = buildPendingReportBean(ipPerson);
            if(bean != null)  {
                data.add(bean);
            }
        }
        return data;
    }

    private PendingReportBean buildPendingReportBean(InstitutionalProposalPerson ipPerson) throws WorkflowException {
        InstitutionalProposal proposal = ipPerson.getInstitutionalProposal();
        PendingReportBean bean = null;
        if(proposal != null && shouldDataBeIncluded(proposal.getInstitutionalProposalDocument())) {
            bean = new PendingReportBean(ipPerson);
        }
        return bean;
    }

    private Collection<InstitutionalProposalPerson> executePendingSupportQuery(String personId, Collection<String> excludedProposalTypes) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("personId", personId);
        criteria.addEqualTo("institutionalProposal.proposalSequenceStatus", VersionStatus.ACTIVE.toString());

        if (!excludedProposalTypes.isEmpty()) {
            criteria.addNotIn("institutionalProposal.proposalTypeCode", excludedProposalTypes);
        }

        QueryByCriteria queryByCriteria = QueryFactory.newQuery(InstitutionalProposalPerson.class, criteria);

        return getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
    }
}
