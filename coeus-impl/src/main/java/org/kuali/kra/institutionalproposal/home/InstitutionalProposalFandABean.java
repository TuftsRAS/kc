/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.home;

import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.kra.institutionalproposal.web.struts.form.InstitutionalProposalForm;

import java.io.Serializable;


public class InstitutionalProposalFandABean implements Serializable {

    private static final long serialVersionUID = -4162806132355841770L;

    private InstitutionalProposalForm parent;

    private InstitutionalProposalFandA newInstitutionalProposalFandA;

    public InstitutionalProposalFandABean() {
        super();
        newInstitutionalProposalFandA = new InstitutionalProposalFandA();
    }

    public InstitutionalProposalFandABean(InstitutionalProposalForm parent) {
        this.parent = parent;
        newInstitutionalProposalFandA = new InstitutionalProposalFandA();
    }

    public void init() {
        newInstitutionalProposalFandA = new InstitutionalProposalFandA();
    }

    public InstitutionalProposalFandA getNewInstitutionalProposalFandA() {
        return newInstitutionalProposalFandA;
    }

    public void setnewInstitutionalProposalFandA(InstitutionalProposalFandA newInstitutionalProposalFandA) {
        this.newInstitutionalProposalFandA = newInstitutionalProposalFandA;
    }

    public InstitutionalProposalDocument getInstitutionalProposalDocument() {
        return parent.getInstitutionalProposalDocument();
    }

    public Object getData() {
        return getNewInstitutionalProposalFandA();
    }

    public boolean addFandA(InstitutionalProposalFandABean formBean) {
        formBean.getInstitutionalProposalDocument().getInstitutionalProposal().add(formBean.getNewInstitutionalProposalFandA());
        formBean.init();
        return true;
    }
}
