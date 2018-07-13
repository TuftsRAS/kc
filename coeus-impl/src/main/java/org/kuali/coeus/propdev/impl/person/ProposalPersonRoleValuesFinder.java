/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.person;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.coeus.common.framework.person.PropAwardPersonRole;
import org.kuali.coeus.common.framework.person.PropAwardPersonRoleValuesFinder;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocumentForm;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.view.ViewModel;

public class ProposalPersonRoleValuesFinder extends PropAwardPersonRoleValuesFinder {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(PropAwardPersonRoleValuesFinder.class);

    @Override
	protected String getSponsorCodeFromModel(ViewModel model) {
		return ((ProposalDevelopmentDocumentForm) model).getProposalDevelopmentDocument().getDevelopmentProposal().getSponsorCode();
	}

    @Override
    protected boolean piAlreadyExists(ViewModel model, InputField field) {
        try {
            String roleId = (String) PropertyUtils.getProperty(model, field.getBindingInfo().getBindingPath());
            if (roleId != null && roleId.equals(PropAwardPersonRole.PRINCIPAL_INVESTIGATOR)) {
                return false;
            }
        } catch (Exception e) {
            LOG.info("could not retrieve role from the input field ");
        }

        for (ProposalPerson person : ((ProposalDevelopmentDocumentForm) model).getDevelopmentProposal().getProposalPersons()) {
            if (person.getRole().getCode().equals(PropAwardPersonRole.PRINCIPAL_INVESTIGATOR)) {
                return true;
            }
        }
        return false;
    }
}
