/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.version;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.version.sequence.Sequenceable;
import org.kuali.coeus.common.framework.version.sequence.associate.SeparatelySequenceableAssociate;
import org.kuali.coeus.common.framework.version.sequence.owner.SequenceOwner;
import org.kuali.coeus.common.framework.version.VersionException;
import org.kuali.coeus.common.framework.version.VersioningService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * This service implements generic versioning.
 */
@Component("versioningService")
public class VersioningServiceImpl implements VersioningService {
    
    private static final double NANOS_PER_SECOND = 1000000000.0;
    private static final String IN = " in ";
    private static final String SECONDS = " seconds";
    private static final String TO = " to ";
    private static final String VERSIONED_FROM = " versioned from ";
    private static final String PERIOD = ".";
    private static final Logger LOG = LogManager.getLogger(VersioningServiceImpl.class);
    
    @Override
    public <T extends SequenceOwner<?>> T createNewVersion(T oldVersion) throws VersionException {
        final long time = getCurrentTime();
        T newVersion = new SequenceUtils().sequence(oldVersion);
        logVersionOperation(time, oldVersion, newVersion);

        return newVersion;
    }

    @Override
    public <T extends SeparatelySequenceableAssociate> T versionAssociate(T oldAssociate) throws VersionException {
        final long time = getCurrentTime();
        SequenceUtils sequenceUtils = new SequenceUtils();
        T newAssociate = sequenceUtils.sequence(oldAssociate);
        logVersionOperation(time, oldAssociate, newAssociate);
        
        return newAssociate;
    }

    @Override
    public <T extends SeparatelySequenceableAssociate> List<T> versionAssociates(List<T> oldAssociates) throws VersionException {
        final long time = getCurrentTime();
        SequenceUtils sequenceUtils = new SequenceUtils();
        List<T> newAssociates = sequenceUtils.sequence(oldAssociates);
        
        logVersionOperation(time, oldAssociates, newAssociates);
        return newAssociates;
    }
    
    /**
     * Gets the current time in nanos.
     * @return the current time.
     */
    protected static long getCurrentTime() {
        return System.nanoTime();
    }
    
    /**
     * Gets an elapsed time in seconds based on a start and end time.
     * @param startTime the start time in nanos
     * @param endTime the end time in nanos
     * @return the time in seconds
     */
    protected static double calcElapsedTimeInSeconds(long startTime, long endTime) {
        return (endTime - startTime) / NANOS_PER_SECOND;
    }
    
    /**
     * Gets the non qualified class name of an object.
     * @param o the object
     * @return the non-qualified class name
     */
    protected static String getNonQualifiedClassName(Object o) {
        final String name = o.getClass().getName();
        return name.substring(name.lastIndexOf(PERIOD) + 1);
    }

    /**
     * This method logs the versioning operation
     * @param oldVersion
     * @param newVersion
     */
    protected void logVersionOperation(long startTime, Sequenceable oldVersion, Sequenceable newVersion) {
        this.logVersionOperation(startTime, Collections.singletonList(oldVersion), Collections.singletonList(newVersion));
    }
    
    /**
     * This method logs the versioning operation
     * @param oldVersion
     * @param newVersion
     */
    protected <T extends Sequenceable> void logVersionOperation(long startTime, List<T> oldVersions, List<T> newVersions) {
        if (LOG.isInfoEnabled()) {
            final double seconds = calcElapsedTimeInSeconds(startTime, getCurrentTime());
            
            for (int i = 0; i < oldVersions.size(); i++) {
                final StringBuilder versionLoggingMessage = new StringBuilder()
                    .append(getNonQualifiedClassName(oldVersions.get(i)))
                    .append(VERSIONED_FROM)
                    .append(oldVersions.get(i).getSequenceNumber())
                    .append(TO)
                    .append(newVersions.get(i).getSequenceNumber())
                    .append(IN)
                    .append(seconds)
                    .append(SECONDS);
                
                LOG.info(versionLoggingMessage);
            }
        }
    }
}
