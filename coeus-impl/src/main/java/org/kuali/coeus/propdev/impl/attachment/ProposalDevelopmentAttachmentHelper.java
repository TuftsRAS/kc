/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.attachment;

import org.kuali.coeus.propdev.impl.abstrct.ProposalAbstract;
import org.kuali.coeus.propdev.impl.person.attachment.ProposalPersonBiography;
import org.kuali.rice.krad.bo.Note;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProposalDevelopmentAttachmentHelper implements Serializable {
    private Narrative narrative;
    private Narrative instituteAttachment;
    private Note note;
    private ProposalPersonBiography biography;
    private ProposalAbstract proposalAbstract;

    private String selectedLineIndex;
    private String proposalAttachmentModuleStatusCode;
    private String internalAttachmentModuleStatusCode;
    private String markAllBioStatus;
    private String previousNarrativeTypeValue;
    private String currentNarrativeTypeDescription;
    private String narrativeTypePropertyPath;

    private Map<String,List<String>> editableFileAttachments;

    public ProposalDevelopmentAttachmentHelper() {
        instituteAttachment = new Narrative();
        narrative = new Narrative();
        biography = new ProposalPersonBiography();
        note = new Note();
        proposalAbstract = new ProposalAbstract();
        editableFileAttachments = new HashMap<>();
    }

    public void reset() {
        instituteAttachment = new Narrative();
        narrative = new Narrative();
        biography = new ProposalPersonBiography();
        note = new Note();
        proposalAbstract = new ProposalAbstract();
        editableFileAttachments = new HashMap<>();
        selectedLineIndex = null;
        previousNarrativeTypeValue = "";
        currentNarrativeTypeDescription = "";
        narrativeTypePropertyPath = "";
    }

    public ProposalPersonBiography getBiography() {
        return biography;
    }

    public void setBiography(ProposalPersonBiography biography) {
        this.biography = biography;
    }

    public Narrative getNarrative() {
        return narrative;
    }

    public void setNarrative(Narrative narrative) {
        this.narrative = narrative;
    }

    public String getSelectedLineIndex() {
        return selectedLineIndex;
    }

    public void setSelectedLineIndex(String selectedLineIndex) {
        this.selectedLineIndex = selectedLineIndex;
    }

    public String getMarkAllBioStatus() {
        return markAllBioStatus;
    }

    public void setMarkAllBioStatus(String markAllBioStatus) {
        this.markAllBioStatus = markAllBioStatus;
    }

    public Narrative getInstituteAttachment() {
        return instituteAttachment;
    }

    public void setInstituteAttachment(Narrative instituteAttachment) {
        this.instituteAttachment = instituteAttachment;
    }

    public Map<String, List<String>> getEditableFileLineAttachments() {
        return editableFileAttachments;
    }

    public void setEditableFileAttachments(Map<String, List<String>> editableFileAttachments) {
        this.editableFileAttachments = editableFileAttachments;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public ProposalAbstract getProposalAbstract() {
        return proposalAbstract;
    }

    public void setProposalAbstract(ProposalAbstract proposalAbstract) {
        this.proposalAbstract = proposalAbstract;
    }

    public String getProposalAttachmentModuleStatusCode() {
        return proposalAttachmentModuleStatusCode;
    }

    public void setProposalAttachmentModuleStatusCode(String proposalAttachmentModuleStatusCode) {
        this.proposalAttachmentModuleStatusCode = proposalAttachmentModuleStatusCode;
    }

    public String getInternalAttachmentModuleStatusCode() {
        return internalAttachmentModuleStatusCode;
    }

    public void setInternalAttachmentModuleStatusCode(String internalAttachmentModuleStatusCode) {
        this.internalAttachmentModuleStatusCode = internalAttachmentModuleStatusCode;
    }

    public String getPreviousNarrativeTypeValue() {
        return previousNarrativeTypeValue;
    }

    public void setPreviousNarrativeTypeValue(String previousNarrativeTypeValue) {
        this.previousNarrativeTypeValue = previousNarrativeTypeValue;
    }

    public String getCurrentNarrativeTypeDescription() {
        return currentNarrativeTypeDescription;
    }

    public void setCurrentNarrativeTypeDescription(String currentNarrativeTypeDescription) {
        this.currentNarrativeTypeDescription = currentNarrativeTypeDescription;
    }

    public String getNarrativeTypePropertyPath() {
        return narrativeTypePropertyPath;
    }

    public void setNarrativeTypePropertyPath(String narrativeTypePropertyPath) {
        this.narrativeTypePropertyPath = narrativeTypePropertyPath;
    }
}
