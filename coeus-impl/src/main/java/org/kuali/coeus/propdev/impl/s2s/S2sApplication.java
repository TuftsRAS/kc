/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.attachment.KcAttachmentService;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.coeus.propdev.api.s2s.S2sApplicationContract;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.file.FileMeta;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "S2S_APPLICATION")
public class S2sApplication extends KcPersistableBusinessObjectBase implements S2sApplicationContract, FileMeta {

    @Id
    @Column(name = "PROPOSAL_NUMBER")
    private String proposalNumber;

    @Column(name = "APPLICATION")
    @Lob
    private String application;

    @Transient
    private transient String url;

    @Transient
    private transient DateTimeService dateTimeService;

    @Transient
    private transient KcAttachmentService kcAttachmentService;

    @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "PROPOSAL_NUMBER", referencedColumnName = "PROPOSAL_NUMBER")
    private List<S2sAppAttachments> s2sAppAttachmentList;

    @Override
    public String getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    @Override
    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    @Override
    public List<S2sAppAttachments> getS2sAppAttachmentList() {
        return s2sAppAttachmentList;
    }

    public void setS2sAppAttachmentList(List<S2sAppAttachments> s2sAppAttachmentList) {
        this.s2sAppAttachmentList = s2sAppAttachmentList;
    }

    @Override
    public void init(MultipartFile multipartFile) {
        //no op
    }

    @Override
    public String getId() {
        return getProposalNumber();
    }

    @Override
    public void setId(String id) {
        setProposalNumber(id);
    }

    @Override
    public String getName() {
        return "Grant Application.xml";
    }

    @Override
    public void setName(String name) {
        //no op
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }

    @Override
    public void setContentType(String contentType) {
        //no op
    }

    @Override
    public Long getSize() {
        return (long) application.length();
    }

    @Override
    public void setSize(Long size) {
        //no op
    }

    @Override
    public String getSizeFormatted() {
        return getKcAttachmentService().formatFileSizeString(getSize());
    }

    @Override
    public Date getDateUploaded() {
        return this.getUpdateTimestamp();
    }

    @Override
    public void setDateUploaded(Date dateUploaded) {
        this.setUpdateTimestamp(new Timestamp(dateUploaded.getTime()));
    }

    @Override
    public String getDateUploadedFormatted() {
        if (this.getUpdateTimestamp() != null) {
            return getDateTimeService().toString(new Date(this.getUpdateTimestamp().getTime()), CoreConstants.TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE_DEFAULT);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    public DateTimeService getDateTimeService() {
        if (dateTimeService == null)
            dateTimeService = KcServiceLocator.getService(DateTimeService.class);
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public KcAttachmentService getKcAttachmentService() {
        if (kcAttachmentService == null)
            kcAttachmentService = KcServiceLocator.getService(KcAttachmentService.class);
        return kcAttachmentService;
    }

    public void setKcAttachmentService(KcAttachmentService kcAttachmentService) {
        this.kcAttachmentService = kcAttachmentService;
    }
}
