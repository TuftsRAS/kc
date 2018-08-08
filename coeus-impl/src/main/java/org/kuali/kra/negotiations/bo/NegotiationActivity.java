/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.negotiations.bo;

import java.util.Objects;
import org.kuali.coeus.common.framework.person.KcPerson;
import org.kuali.coeus.common.framework.person.KcPersonService;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class NegotiationActivity extends KcPersistableBusinessObjectBase {


    private static final long serialVersionUID = 1927288853033781994L;

    public static final long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;

    private Long activityId;

    private Long negotiationId;

    private Negotiation negotiation;

    private Long locationId;

    private NegotiationLocation location;

    private Long activityTypeId;

    private NegotiationActivityType activityType;

    private Date startDate;

    private Date endDate;

    private Date createDate;

    private Date followupDate;

    private String lastModifiedUsername;

    private Date lastModifiedDate;

    private String description;

    private Boolean restricted;

    private List<NegotiationActivityAttachment> attachments;

    private transient NegotiationActivityAttachment newAttachment;

    private transient boolean updated;

    private transient KcPersonService kcPersonService;

    public NegotiationActivity() {
        restricted = Boolean.TRUE;
        attachments = new ArrayList<>();
        newAttachment = new NegotiationActivityAttachment();
    }

    /**
     * Calculates the number of days between the start date and either the end date when
     * available or the current date.

     */
    public String getNumberOfDays() {
        return getNumberOfDays(getStartDate(), getEndDate());
    }

    /**
     * 
     * This method leverages Negotiation's static age calculation and returns string value of age.
     */
    public static String getNumberOfDays(Date startDate, Date endDate) {
        return Objects.toString(Negotiation.getNumberOfDays(startDate, endDate), "");
    }

    /**
     * This method should be called during the execute or before save to
     * update this BOs last updated by fields when something has changed.
     */
    public void updateActivity() {
        if (updated) {
            this.refreshReferenceObject("location");
            this.refreshReferenceObject("activityType");
            this.setLastModifiedDate(new Date(new java.util.Date().getTime()));
            this.setLastModifiedUsername(GlobalVariables.getUserSession().getPrincipalName());
            this.updated = false;
        }
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getNegotiationId() {
        return negotiationId;
    }

    public void setNegotiationId(Long negotiationId) {
        this.negotiationId = negotiationId;
    }

    public Negotiation getNegotiation() {
        return negotiation;
    }

    public void setNegotiation(Negotiation negotiation) {
        this.negotiation = negotiation;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        if (!Objects.equals(this.locationId, locationId)) {
            updated = true;
        }
        this.locationId = locationId;
    }

    public NegotiationLocation getLocation() {
        return location;
    }

    public void setLocation(NegotiationLocation location) {
        if (!Objects.equals(this.location, location)) {
            updated = true;
        }
        this.location = location;
    }

    public Long getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(Long activityTypeId) {
        if (!Objects.equals(this.activityTypeId, activityTypeId)) {
            updated = true;
        }
        this.activityTypeId = activityTypeId;
    }

    public NegotiationActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(NegotiationActivityType activityType) {
        this.activityType = activityType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        if (!Objects.equals(this.startDate, startDate)) {
            updated = true;
        }
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        if (!Objects.equals(this.endDate, endDate)) {
            updated = true;
        }
        this.endDate = endDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getFollowupDate() {
        return followupDate;
    }

    public void setFollowupDate(Date followupDate) {
        if (!Objects.equals(this.followupDate, followupDate)) {
            updated = true;
        }
        this.followupDate = followupDate;
    }

    public String getLastModifiedUsername() {
        return lastModifiedUsername;
    }

    public void setLastModifiedUsername(String lastModifiedUsername) {
        this.lastModifiedUsername = lastModifiedUsername;
    }

    public KcPerson getLastModifiedUser() {
        if (this.getLastModifiedUsername() == null) {
            return null;
        } else {
            return getKcPersonService().getKcPersonByUserName(this.getLastModifiedUsername());
        }
    }
    
    public String getLastModifiedUserFullName() {
        KcPerson user = getLastModifiedUser();
        return user == null ? "" : user.getFullName();
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (!Objects.equals(this.description, description)) {
            updated = true;
        }
        this.description = description;
    }

    public Boolean getRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        if (!Objects.equals(this.restricted, restricted)) {
            updated = true;
        }
        this.restricted = restricted;
    }

    public List<NegotiationActivityAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<NegotiationActivityAttachment> attachments) {
        this.attachments = attachments;
    }

    /**
     * Add a new attachment to this activity.
     */
    public void add(NegotiationActivityAttachment attachment) {
        updated = true;
        this.attachments.add(attachment);
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public NegotiationActivityAttachment getNewAttachment() {
        return newAttachment;
    }

    public void setNewAttachment(NegotiationActivityAttachment newAttachment) {
        this.newAttachment = newAttachment;
    }

    protected KcPersonService getKcPersonService() {
        if (this.kcPersonService == null) {
            this.kcPersonService = KcServiceLocator.getService(KcPersonService.class);
        }
        return this.kcPersonService;
    }
}
