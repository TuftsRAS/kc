/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.noteattachment;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.ProtocolDao;
import org.kuali.kra.protocol.ProtocolSpecialVersion;
import org.kuali.kra.protocol.personnel.ProtocolPersonBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.*;


/** Implementation of {@link ProtocolAttachmentService ProtocolNoteAndAttachmentService}. */
public abstract class ProtocolAttachmentServiceImplBase implements ProtocolAttachmentService {

    protected final BusinessObjectService boService;
    protected final ProtocolDao protocolDao;
    protected PersonService personService;
    
    protected final Logger LOG = LogManager.getLogger(getClass());
    private static final String PERSON_NOT_FOUND_FORMAT_STRING = "%s (not found)";
    
    
    /**
     * Constructor than sets the dependencies.
     * 
     * @param boService the {@link BusinessObjectService BusinessObjectService}
     * @throws IllegalArgumentException if the boService or protocolDao is null
     */
    public ProtocolAttachmentServiceImplBase(final BusinessObjectService boService, final ProtocolDao protocolDao) {
        if (boService == null) {
            throw new IllegalArgumentException("the boService was null");
        }
        
        if (protocolDao == null) {
            throw new IllegalArgumentException("the protocolDao was null");
        }
        
        this.boService = boService;
        this.protocolDao = protocolDao;
    }
    
    public abstract Class<? extends ProtocolBase> getProtocolClassHook();
    public abstract Class<? extends ProtocolAttachmentStatusBase> getProtocolAttachmentStatusClassHook();
    public abstract Class<? extends ProtocolPersonBase> getProtocolPersonClassHook();
    public abstract Class<? extends ProtocolAttachmentTypeBase> getProtocolAttachmentTypeClassHook();
    public abstract Class<? extends ProtocolAttachmentTypeGroupBase> getProtocolAttachmentTypeGroupClassHook();
    public abstract Class<? extends ProtocolAttachmentProtocolBase> getProtocolAttachmentProtocolClassHook();
    public abstract Class<? extends ProtocolAttachmentPersonnelBase> getProtocolAttachmentPersonnelClassHook();
    
    @Override
    public ProtocolAttachmentStatusBase getStatusFromCode(final String code) {
        return this.getCodeType(getProtocolAttachmentStatusClassHook(), code);
    }

    @Override
    public ProtocolAttachmentTypeBase getTypeFromCode(final String code) {
        return this.getCodeType(getProtocolAttachmentTypeClassHook(), code);
    }

    @Override
    public abstract Collection<ProtocolAttachmentTypeBase> getTypesForGroup(String code);
    
    @Override
    public void saveAttatchment(ProtocolAttachmentBase attachment) {
        
        if (attachment == null) {
            throw new IllegalArgumentException("the attachment is null");
        }
        
        this.boService.save(attachment);
    }
    
    @Override
    public void deleteAttatchment(ProtocolAttachmentBase attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("the attachment is null");
        }
        this.boService.delete(attachment);
    }
    
    @Override
    public ProtocolPersonBase getPerson(Integer personId) {
        if (personId == null) {
            throw new IllegalArgumentException("the personId is null");
        }
        
        return (ProtocolPersonBase) this.boService.findByPrimaryKey(getProtocolPersonClassHook(), Collections.singletonMap("protocolPersonId", personId));
    }
    
    @Override
    public <T extends ProtocolAttachmentBase> T getAttachment(Class<T> type, Long id) {
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        }

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        
        @SuppressWarnings("unchecked")
        final T attachment = (T) this.boService.findByPrimaryKey(type, Collections.singletonMap("id", id));
        return attachment;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean isNewAttachmentVersion(ProtocolAttachmentProtocolBase attachment) {
        Map keyMap = new HashMap();
        // the initial version of amendment & renewal need to do this
        if (!attachment.getProtocol().isNew() && attachment.getProtocol().getSequenceNumber() == 0) {
            ProtocolBase protocol = getActiveProtocol(attachment.getProtocol().getProtocolNumber().substring(0, 
                    attachment.getProtocol().getProtocolNumber().indexOf(attachment.getProtocol().isAmendment() ? ProtocolSpecialVersion.AMENDMENT.getCode() : attachment.getProtocol().isRenewal() ? ProtocolSpecialVersion.RENEWAL.getCode() : ProtocolSpecialVersion.FYI.getCode())));
            keyMap.put("protocolNumber", protocol.getProtocolNumber());
            keyMap.put("sequenceNumber", protocol.getSequenceNumber());
        } else {
           keyMap.put("protocolNumber", attachment.getProtocolNumber());
           keyMap.put("sequenceNumber", attachment.getSequenceNumber() - 1);
        }
        keyMap.put("attachmentVersion", attachment.getAttachmentVersion());
        keyMap.put("documentId", attachment.getDocumentId());
   
        return this.boService.findMatching(getProtocolAttachmentProtocolClassHook(), keyMap).isEmpty();
    }

    

    @Override
    public boolean isAttachmentActive(ProtocolAttachmentProtocolBase attachment) {
        boolean retValue;
        // first get the active version of the protocol with the number given in the attachment
        String protocolNumber = attachment.getProtocol().getProtocolNumber();
        if (!attachment.getProtocol().isNew()) {
            protocolNumber = attachment.getProtocol().getAmendedProtocolNumber(); 
        }
        ProtocolBase activeProtocol = getActiveProtocol(protocolNumber);
        // now check if the current attachment is in the list of the active, 'non-deleted' attachments for this protocol
        // note: see equals methods for protocol attachment for the appropriate semantics for "contains" below
        if(activeProtocol.getActiveAttachmentProtocolsNoDelete().contains(attachment)) {
            retValue = true;
        }
        else {
            retValue = false;
        }
        return retValue;
    }
    
    /*
     * This method is to get the current protocol.  The protocol with the highest sequence number.
     */
    @SuppressWarnings("unchecked")
    protected ProtocolBase getActiveProtocol(String protocolNumber) {
        Map keyMap = new HashMap();
        keyMap.put("protocolNumber", protocolNumber);
        List<ProtocolBase> protocols = (List <ProtocolBase>)this.boService.findMatchingOrderBy(getProtocolClassHook(), keyMap, "sequenceNumber", false);
        return protocols.get(0);
    }
    
    @Override
    public <T extends ProtocolAttachmentBase & TypedAttachment> Collection<T> getAttachmentsWithOlderFileVersions(final T attachment, final Class<T> type) {
        
        final Collection<T> olderAttachments = new ArrayList<T>();
        for (final T version : (List<T>)this.protocolDao.retrieveAttachmentVersions(attachment, type)) {
            if (version.getFile().getSequenceNumber().intValue() < attachment.getFile().getSequenceNumber().intValue()) {
                olderAttachments.add(version);
            }
        }
        return olderAttachments;
    }
    
    /**
     * Gets a "code" BO from a code.  This method will only work for a BO that has a property of "code" that is the
     * primary key.
     *   
     * @param <T> the BO type
     * @param type the type
     * @param code the code value
     * @return the BO
     * @throws IllegalArgumentException if the code or type is null.
     */
    protected <T extends PersistableBusinessObject> T getCodeType(final Class<T> type, final String code) {
        if (type == null) {
            throw new IllegalArgumentException("the type is null");
        }
        
        if (code == null) {
            throw new IllegalArgumentException("the code is null");
        }
        
        @SuppressWarnings("unchecked")
        final T bo = (T) this.boService.findByPrimaryKey(type, Collections.singletonMap("code", code));
        
        return bo;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean isSharedFile(ProtocolAttachmentPersonnelBase attachment) {
        Map keyMap = new HashMap();
        keyMap.put("fileId", attachment.getFileId());   
        return this.boService.findMatching(getProtocolAttachmentPersonnelClassHook(), keyMap).size() > 1;
    }
    
    @Override
    public void setProtocolAttachmentUpdateUsersName(List<? extends ProtocolAttachmentBase> protocolAttachmentBases) {
        
        for (ProtocolAttachmentBase pab : protocolAttachmentBases) {
            if (LOG.isDebugEnabled()) { 
                LOG.debug(String.format("Looking up person for update user %s.", pab.getUpdateUser()));
            }
            Person person = personService.getPersonByPrincipalName(pab.getUpdateUser());
            pab.setUpdateUserFullName(person==null?String.format(PERSON_NOT_FOUND_FORMAT_STRING, pab.getUpdateUser()):person.getName());
        }
    }
 
    /**
     * Sets the personService attribute value.
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }   

}
