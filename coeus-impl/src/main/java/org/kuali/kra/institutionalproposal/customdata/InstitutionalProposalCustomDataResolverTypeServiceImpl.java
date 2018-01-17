/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */package org.kuali.kra.institutionalproposal.customdata;


import org.kuali.coeus.common.impl.custom.CustomDataResolverTypeServiceImpl;
import org.kuali.kra.krms.KcKrmsConstants;
import org.springframework.stereotype.Component;

@Component("institutionalProposalCustomDataResolverTypeService")
public class InstitutionalProposalCustomDataResolverTypeServiceImpl extends CustomDataResolverTypeServiceImpl {
    @Override
    protected String getModuleNamePrereq() {return KcKrmsConstants.InstitutionalProposal.INSTITUTIONAL_PROPOSAL;}
}
