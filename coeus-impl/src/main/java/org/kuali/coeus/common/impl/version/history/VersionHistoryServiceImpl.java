/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.version.history;

import org.apache.commons.collections4.CollectionUtils;
import org.kuali.coeus.common.framework.version.VersionStatus;
import org.kuali.coeus.common.framework.version.history.VersionHistory;
import org.kuali.coeus.common.framework.version.history.VersionHistoryService;
import org.kuali.coeus.common.framework.version.sequence.owner.SequenceOwner;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.document.authorization.PessimisticLock;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PessimisticLockService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.*;

@Component("versionHistoryService")
public class VersionHistoryServiceImpl implements VersionHistoryService {
	
    public static final String VERSION_STATUS_FIELD = "statusForOjb";
    public static final String SEQUENCE_OWNER_CLASS_NAME_FIELD = "sequenceOwnerClassName";
    public static final String SEQUENCE_OWNER_REFERENCE_VERSION_NAME = "sequenceOwnerVersionNameValue";
    public static final String SEQUENCE_OWNER_REFERENCE_SEQ_NUMBER = "sequenceOwnerSequenceNumber";
    public static final String SEQUENCE_OWNER_SEQUENCE_NUMBER_FIELD = "sequenceOwnerSequenceNumber";

    @Autowired
    @Qualifier("businessObjectService")
    private BusinessObjectService bos;

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;

    @Autowired
    @Qualifier("pessimisticLockService")
    private PessimisticLockService pessimisticLockService;
    
    protected VersionHistory createVersionHistory(SequenceOwner<? extends SequenceOwner<?>> sequenceOwner, VersionStatus versionStatus, String userId) {
        VersionHistory versionHistory = new VersionHistory(sequenceOwner, versionStatus, userId, new Date(new java.util.Date().getTime()));
        
        List<VersionHistory> list = new ArrayList<VersionHistory>();
        list.add(versionHistory);
        bos.save(list);
        return versionHistory;
    }

    @Override
    public VersionHistory findPendingVersion(Award award) {
        List<VersionHistory> histories = loadVersionHistory(Award.class, award.getAwardNumber());
        VersionHistory foundPending = null;
        for (VersionHistory history : histories) {
            if (history.getStatus() == VersionStatus.PENDING && award.getSequenceNumber() < history.getSequenceOwnerSequenceNumber()) {
                foundPending = history;
                break;
            }
        }
        return foundPending;
    }
    
    @Override
    public VersionHistory updateVersionHistory(SequenceOwner<? extends SequenceOwner<?>> sequenceOwner, VersionStatus versionStatus, String userId) {
        VersionHistory currentVersion = getVersionHistory(sequenceOwner.getClass(), getVersionName(sequenceOwner), sequenceOwner.getSequenceNumber());
        if (currentVersion == null) {
            currentVersion = createVersionHistory(sequenceOwner,versionStatus, userId);
        }
        currentVersion.setStatus(versionStatus);
        
        //if newly active, clear any other active version histories
        if (versionStatus == VersionStatus.ACTIVE) {
            archiveActiveVersions(sequenceOwner.getClass(), getVersionName(sequenceOwner), currentVersion);
        }
        bos.save(currentVersion);

        return currentVersion;
    }
    protected void archiveActiveVersions(Class klass, String versionName, VersionHistory currentVersion) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(SEQUENCE_OWNER_CLASS_NAME_FIELD, klass.getName());
        fieldValues.put(SEQUENCE_OWNER_REFERENCE_VERSION_NAME, versionName);  
        fieldValues.put(VERSION_STATUS_FIELD, VersionStatus.ACTIVE.toString());
        for (VersionHistory version : bos.findMatching(VersionHistory.class, fieldValues)) {
            if (!version.equals(currentVersion)) {
                version.setStatus(VersionStatus.ARCHIVED);
                bos.save(version);
            }
        }
    }

    protected VersionHistory getVersionHistory(Class klass, String versionName, Integer sequenceNumber) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(SEQUENCE_OWNER_CLASS_NAME_FIELD, klass.getName());
        fieldValues.put(SEQUENCE_OWNER_REFERENCE_VERSION_NAME, versionName);     
        fieldValues.put(SEQUENCE_OWNER_SEQUENCE_NUMBER_FIELD, sequenceNumber);
        List<VersionHistory> history = (List<VersionHistory>) getBusinessObjectService().findMatching(VersionHistory.class, fieldValues);
        if (history != null && !history.isEmpty()) {
            return history.get(0);
        } else {
            return null;
        }
    }

    @Override
    public boolean isVersionLockOn() {
        return parameterService.getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_GEN, Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.ENABLE_LOCK_ON_DOCUMENT_VERSION);
    }

    @Override
    public boolean isAnotherUserEditingDocument(String documentNumber) {
        return isVersionLockOn() ? pessimisticLockService.getPessimisticLocksForDocument(documentNumber).stream().
                anyMatch(lock -> lock.getLockDescriptor().contains(Constants.VERSION_LOCK)) : false;
    }

    @Override
    public String getVersionLockDescriptor(String documentTypeCode, String documentNumber) {
        return documentTypeCode + Constants.VERSION_LOCK + "-" + documentNumber + "-" + globalVariableService.getUserSession().getPrincipalId();
    }

    @Override
    @SuppressWarnings("unchecked")
    public VersionHistory findActiveVersion(Class<? extends SequenceOwner> klass, String versionName) {
        List<VersionHistory> histories = new ArrayList<VersionHistory>(bos.findMatching(VersionHistory.class, buildFieldValueMapForActiveVersionHistory(klass, versionName)));       
        VersionHistory activeVersionHistory = findActiveVersionHistory(histories);        
        return activeVersionHistory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<VersionHistory> loadVersionHistory(Class<? extends SequenceOwner> klass, String versionName) {
        List<VersionHistory> histories = findVersionHistory(klass, versionName);
        if(histories.size() > 0) {
            String versionFieldName = histories.get(0).getSequenceOwnerVersionNameField();
            Map<Integer, SequenceOwner<? extends SequenceOwner<?>>> map = findSequenceOwners(klass, versionFieldName, versionName);
            for(VersionHistory vh: histories) {
                SequenceOwner owner = map.get(vh.getSequenceOwnerSequenceNumber());
                if(owner != null) {
                    vh.setSequenceOwner(owner);
                }
            }
        }
        return histories;
    }

    /**
     * @param bos
     */
    public void setBusinessObjectService(BusinessObjectService bos) {
        this.bos = bos;
    }
    
    protected BusinessObjectService getBusinessObjectService() {
        return bos;
    }

    @Override
    @SuppressWarnings("unchecked")
    public VersionHistory findPendingVersion(Class<? extends SequenceOwner> klass, String versionName, String sequenceNumber) {
        VersionHistory pendingVersionHistory = null;
        
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(SEQUENCE_OWNER_CLASS_NAME_FIELD, klass.getName());
        fieldValues.put(SEQUENCE_OWNER_REFERENCE_VERSION_NAME, versionName);
        fieldValues.put(SEQUENCE_OWNER_REFERENCE_SEQ_NUMBER, sequenceNumber);
        fieldValues.put(VERSION_STATUS_FIELD, VersionStatus.PENDING.name());
        
        List<VersionHistory> histories = new ArrayList<VersionHistory>(bos.findMatching(VersionHistory.class, fieldValues));
        if(CollectionUtils.isNotEmpty(histories)) {
            pendingVersionHistory = histories.get(0);
        }
        
        return pendingVersionHistory;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public VersionHistory findPendingVersion(Class<? extends SequenceOwner> klass, String versionName) {
        VersionHistory pendingVersionHistory = null;
        
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(SEQUENCE_OWNER_CLASS_NAME_FIELD, klass.getName());
        fieldValues.put(SEQUENCE_OWNER_REFERENCE_VERSION_NAME, versionName);
        fieldValues.put(VERSION_STATUS_FIELD, VersionStatus.PENDING.name());
        
        List<VersionHistory> histories = new ArrayList<VersionHistory>(bos.findMatching(VersionHistory.class, fieldValues));
        if(CollectionUtils.isNotEmpty(histories)) {
            pendingVersionHistory = histories.get(0);
        }
        
        return pendingVersionHistory;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> buildFieldValueMapForActiveVersionHistory(Class<? extends SequenceOwner> klass, String versionName) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(SEQUENCE_OWNER_CLASS_NAME_FIELD, klass.getName());
        fieldValues.put(SEQUENCE_OWNER_REFERENCE_VERSION_NAME, versionName);
        fieldValues.put(VERSION_STATUS_FIELD, VersionStatus.ACTIVE.name());
        return fieldValues;
    }


    protected VersionHistory findActiveVersionHistory(List<VersionHistory> histories) {
        VersionHistory activeVersionHistory = null;
        for(VersionHistory vh: histories) {
            if(vh.getStatus() == VersionStatus.ACTIVE) {
                activeVersionHistory = vh;
                break;
            }
        }
        return activeVersionHistory;
    }
    
    @Deprecated
    @SuppressWarnings("unchecked")
    protected Map<Integer, SequenceOwner<? extends SequenceOwner<?>>> findSequenceOwners(Class klass, String versionField, String versionName) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(versionField, versionName);
        Collection<SequenceOwner<? extends SequenceOwner<?>>> c = bos.findMatching(klass, fieldValues);
        Map<Integer, SequenceOwner<? extends SequenceOwner<?>>> map = new TreeMap<Integer, SequenceOwner<? extends SequenceOwner<?>>>();
        for(SequenceOwner<?> owner: c) {
            map.put(owner.getSequenceNumber(), owner);
        }
        return map;
    }
    
    protected String getVersionName(SequenceOwner<? extends SequenceOwner<?>> sequenceOwner) {
        return ObjectUtils.getPropertyValue(sequenceOwner, sequenceOwner.getVersionNameField()).toString();
    }

    @Override
    public List<VersionHistory> findVersionHistory(Class<? extends SequenceOwner> klass, String versionName) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(SEQUENCE_OWNER_CLASS_NAME_FIELD, klass.getName());
        fieldValues.put(SEQUENCE_OWNER_REFERENCE_VERSION_NAME, versionName);        
        return new ArrayList<VersionHistory>(bos.findMatching(VersionHistory.class, fieldValues));
        
    }

    @Override
    public void loadSequenceOwner(Class klass,VersionHistory versionHistory) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(versionHistory.getSequenceOwnerVersionNameField(), versionHistory.getSequenceOwnerVersionNameValue());
        fieldValues.put("sequenceNumber", versionHistory.getSequenceOwnerSequenceNumber());
        Collection<SequenceOwner<? extends SequenceOwner<?>>> c = bos.findMatching(klass, fieldValues);
        
        for (SequenceOwner<? extends SequenceOwner<?>> sequenceOwner : c) {
            versionHistory.setSequenceOwner(sequenceOwner);
        }
    }
    
    @Override
    public VersionHistory getActiveOrNewestVersion(Class<? extends SequenceOwner> klass, String versionName) {
        List<VersionHistory> versions = findVersionHistory(klass, versionName);
        VersionHistory history = null;
        for (VersionHistory version : versions) {
            if (history == null) {
                history = version;
            } else if (version.isActiveVersion()) {
                history = version;
            } else if (!history.isActiveVersion() 
                    && version.getSequenceOwnerSequenceNumber() > history.getSequenceOwnerSequenceNumber()
                    && version.getStatus() != VersionStatus.CANCELED) {
                history = version;
            }
        }
        return history;
    }
}
