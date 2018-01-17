/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.iacuc.actions.decision;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.committee.impl.meeting.ProtocolVoteAbstaineeBase;
import org.kuali.coeus.common.committee.impl.meeting.ProtocolVoteRecusedBase;
import org.kuali.kra.iacuc.IacucProtocol;
import org.kuali.kra.iacuc.actions.IacucProtocolAction;
import org.kuali.kra.iacuc.actions.IacucProtocolActionType;
import org.kuali.kra.iacuc.actions.submit.IacucProtocolSubmission;
import org.kuali.kra.iacuc.actions.submit.IacucProtocolSubmissionStatus;
import org.kuali.kra.iacuc.committee.meeting.IacucProtocolVoteAbstainee;
import org.kuali.kra.iacuc.committee.meeting.IacucProtocolVoteRecused;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.actions.ProtocolActionBase;
import org.kuali.kra.protocol.actions.decision.CommitteeDecisionServiceImplBase;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmissionBase;

import java.util.HashMap;
import java.util.Map;

public class IacucCommitteeDecisionServiceImpl extends CommitteeDecisionServiceImplBase<IacucCommitteeDecision> implements IacucCommitteeDecisionService {

    @Override
    protected String getProtocolActionTypeCodeForRecordCommitteeDecisionHook() {
        return IacucProtocolActionType.RECORD_COMMITTEE_DECISION;
    }

    @Override
    protected ProtocolActionBase getNewProtocolActionInstanceHook(ProtocolBase protocol, ProtocolSubmissionBase submission, String recordCommitteeDecisionActionCode) {
        return new IacucProtocolAction((IacucProtocol) protocol, (IacucProtocolSubmission) submission,
            recordCommitteeDecisionActionCode);
    }

    @Override
    protected ProtocolSubmissionBase getSubmission(ProtocolBase protocol) {
        // There are 'findCommission' in other classes. Consider to create a utility static method for this
        // need to loop thru to find the last submission.
        // it may have submit/Wd/notify irb/submit, and this will cause problem if don't loop thru.
        ProtocolSubmissionBase protocolSubmission = null;
        for (ProtocolSubmissionBase submission : protocol.getProtocolSubmissions()) {
            if (StringUtils.equals(submission.getSubmissionStatusCode(), IacucProtocolSubmissionStatus.IN_AGENDA)) {
                protocolSubmission = submission;
            }
        }
        return protocolSubmission;
    }

    @Override
    protected Map<String, Object> getFieldValuesMap(Long protocolId, Long scheduleIdFk, String personId, Integer rolodexId, Long submissionIdFk) {
      Map<String, Object> fieldValues = new HashMap<String, Object>();
      fieldValues.put("protocolIdFk", protocolId.toString());
      fieldValues.put("personId", personId);
      fieldValues.put("rolodexId", rolodexId);
      fieldValues.put("submissionIdFk", submissionIdFk.toString());
      return fieldValues;
  }

    @Override
    protected Class<? extends ProtocolVoteAbstaineeBase> getProtocolVoteAbstaineeBOClassHook() {
        return IacucProtocolVoteAbstainee.class;
    }

    @Override
    protected ProtocolVoteAbstaineeBase getNewProtocolVoteAbstaineeInstanceHook() {
        return new IacucProtocolVoteAbstainee();
    }

    @Override
    protected Class<? extends ProtocolVoteRecusedBase> getProtocolVoteRecusedBOClassHook() {
        return IacucProtocolVoteRecused.class;
    }

    @Override
    protected ProtocolVoteRecusedBase getNewProtocolVoteRecusedInstanceHook() {
        return new IacucProtocolVoteRecused();
    }


}
