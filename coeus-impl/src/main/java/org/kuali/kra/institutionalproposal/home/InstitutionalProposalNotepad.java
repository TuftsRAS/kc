/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.home;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.version.sequence.associate.SequenceAssociate;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.institutionalproposal.InstitutionalProposalAssociate;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.NoteService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InstitutionalProposalNotepad extends InstitutionalProposalAssociate implements SequenceAssociate<InstitutionalProposal> {

    private static final long serialVersionUID = 1L;

    private Long proposalNotepadId;

    private String proposalNumber;

    private Integer entryNumber;

    private String comments;

    private String noteTopic;

    private boolean restrictedView;

    private Date createTimestamp;
    
    private String createUser;
    
    private Long noteId;
    
    private List<Note> attachments;

    public InstitutionalProposalNotepad() {
        attachments = new ArrayList<Note>();
    }

    public Long getProposalNotepadId() {
        return proposalNotepadId;
    }

    public void setProposalNotepadId(Long proposalNotepadId) {
        this.proposalNotepadId = proposalNotepadId;
    }

    @Override
    public String getProposalNumber() {
        return proposalNumber;
    }

    @Override
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public Integer getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(Integer entryNumber) {
        this.entryNumber = entryNumber;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean getRestrictedView() {
        return restrictedView;
    }

    public void setRestrictedView(boolean restrictedView) {
        this.restrictedView = restrictedView;
    }

    /**
     * Gets the noteTopic attribute. 
     * @return Returns the noteTopic.
     */
    public String getNoteTopic() {
        return noteTopic;
    }

    /**
     * Sets the noteTopic attribute value.
     * @param noteTopic The noteTopic to set.
     */
    public void setNoteTopic(String noteTopic) {
        this.noteTopic = noteTopic;
    }

    /**
     * Gets the createTimeStamp attribute. 
     * @return Returns the createTimeStamp.
     */
    public Date getCreateTimestamp() {
        return createTimestamp;
    }

    /**
     * Sets the createTimeStamp attribute value.
     * @param createTimestamp The createTimeStamp to set.
     */
    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    @Override
    public InstitutionalProposal getSequenceOwner() {
        return getInstitutionalProposal();
    }

    @Override
    public void setSequenceOwner(InstitutionalProposal newlyVersionedOwner) {
        setInstitutionalProposal(newlyVersionedOwner);
    }

    @Override
    public void resetPersistenceState() {
        this.proposalNotepadId = null;
    }

    @Override
    protected void prePersist() {
        super.prePersist();

        if (createUser == null) {
            setCreateUser(StringUtils.substring(getGlobalVariableService().getUserSession().getPrincipalName(), 0, UPDATE_USER_LENGTH));
        }

        if (createTimestamp == null) {
            Calendar cl = Calendar.getInstance();
            setCreateTimestamp(new Date(cl.getTime().getTime()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void postPersist() {
        NoteService noteService = KcServiceLocator.getService(NoteService.class);
        for (Note note : getAttachments()) {
            if (StringUtils.isBlank(note.getRemoteObjectIdentifier())) {
                note.setRemoteObjectIdentifier(this.getObjectId());
                noteService.save(note);
            }
        }
        //if we haven't saved the note id or the note id is different, save the note id.
        //This is done to allow for versioning of InstProp while still
        //maintaining the link to this object.
        if (!getAttachments().isEmpty() && (getNoteId() == null || getNoteId() != getAttachments().get(0).getNoteIdentifier())) {
            setNoteId(getAttachments().get(0).getNoteIdentifier());
            KcServiceLocator.getService(BusinessObjectService.class).save(this);
        }
    }

    public List<Note> getAttachments() {
        if (attachments.isEmpty() && getNoteId() != null) {
            Note note = KcServiceLocator.getService(NoteService.class).getNoteByNoteId(getNoteId());
            if (note != null) {
                attachments.add(note);
            }
        }
        if (attachments.isEmpty() && StringUtils.isNotEmpty(getObjectId())) {
            attachments = KcServiceLocator.getService(NoteService.class).getByRemoteObjectId(getObjectId());
            //if we didn't have a valid note id, but we have an attachment, set the note id here.
            if (!attachments.isEmpty()) {
                setNoteId(attachments.get(0).getNoteIdentifier());
            }
        }
        return attachments;
    }

    public void setAttachments(List<Note> attachments) {
        this.attachments = attachments;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public String getCreateUserName() {
        Person tempUser = KcServiceLocator.getService(PersonService.class).getPersonByPrincipalName(this.getCreateUser());
        return tempUser != null ? tempUser.getName() : this.getCreateUser();
    }
    
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    private GlobalVariableService getGlobalVariableService() {
        return KcServiceLocator.getService(GlobalVariableService.class);
    }
}
