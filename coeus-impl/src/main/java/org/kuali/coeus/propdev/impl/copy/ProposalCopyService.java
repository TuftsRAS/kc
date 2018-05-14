/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.copy;

import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;

/**
 * The Proposal Copy Service is responsible for copying proposals.
 *
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public interface ProposalCopyService {
    
    /**
     * Copy a proposal development document.  The criteria controls various
     * aspects of the copying process, e.g. whether or not to copy attachments.
     * 
     * @param doc the proposal development document to copy.
     * @param criteria the user-specified criteria that controls various copy operations.
     * @return the new document that is saved in the database;
     *         otherwise null if an error occurred, e.g. the user didn't have permission to copy the document
     */
    ProposalDevelopmentDocument copyProposal(ProposalDevelopmentDocument doc, ProposalCopyCriteria criteria);
}
