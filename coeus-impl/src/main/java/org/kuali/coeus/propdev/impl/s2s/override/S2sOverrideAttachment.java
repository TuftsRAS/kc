/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.propdev.impl.s2s.override;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.coeus.common.framework.attachment.KcAttachmentDataDao;
import org.kuali.coeus.common.framework.attachment.KcAttachmentService;
import org.kuali.coeus.common.framework.print.KcAttachmentDataSource;
import org.kuali.coeus.propdev.api.s2s.override.S2sOverrideAttachmentContract;
import org.kuali.coeus.s2sgen.api.hash.GrantApplicationHashService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.file.FileMeta;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.persistence.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "S2S_OVERRIDE_ATT")
public class S2sOverrideAttachment extends KcAttachmentDataSource implements S2sOverrideAttachmentContract, FileMeta {

    private static final Log LOG = LogFactory.getLog(S2sOverrideAttachment.class);

    /*
      This cannot be named id because FileMeta has an id but KRAD sets it to a value that is not valid for this database column.
      See FileControllerServiceImpl.addFileUploadLine()
    */
    @PortableSequenceGenerator(name = "SEQ_S2S_OVERRIDE_ATT_ID")
    @GeneratedValue(generator = "SEQ_S2S_OVERRIDE_ATT_ID")
    @Id
    @Column(name = "ID")
    private String attachmentId;

    @Column(name = "CONTENT_ID")
    private String contentId;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "S2S_OVERRIDE_APPL_DATA_ID", referencedColumnName = "ID")
    private S2sOverrideApplicationData application;

    @Transient
    private String url;

    @Transient
    private String id;

    @Transient
    private transient DateTimeService dateTimeService;

    @Transient
    private transient KcAttachmentService kcAttachmentService;

    @Transient
    private transient KcAttachmentDataDao kcAttachmentDataDao;

    @Transient
    private transient GrantApplicationHashService grantApplicationHashService;

    public S2sOverrideApplicationData getApplication() {
        return application;
    }

    public void setApplication(S2sOverrideApplicationData application) {
        this.application = application;
    }

    @Override
    public void init(MultipartFile multipartFile) throws IOException {
        setData(multipartFile.getBytes());
        setName(multipartFile.getOriginalFilename());
        setType(multipartFile.getContentType());
    }

    @Override
    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    @Override
    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    @Override
    public String getContentType() {
        return getType();
    }

    @Override
    public void setContentType(String contentType) {
        setType(contentType);
    }

    @Override
    public Long getSize() {
        return (long) getData().length;
    }

    @Override
    public void setSize(Long size) {

    }

    @Override
    public String getSizeFormatted() {
        return getKcAttachmentService().formatFileSizeString(getSize());
    }

    @Override
    public Date getDateUploaded() {
        return getUploadTimestamp();
    }

    @Override
    public void setDateUploaded(Date dateUploaded) {
        setUploadTimestamp(new Timestamp(dateUploaded.getTime()));
    }

    @Override
    public String getDateUploadedFormatted() {
        if (this.getUpdateTimestamp() != null) {
            return getDateTimeService().toString(new Date(this.getUpdateTimestamp().getTime()), CoreConstants.TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE_DEFAULT);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getSha1Hash() {
        return getGrantApplicationHashService().computeAttachmentHash(getData());
    }

    public String getSha1HashInXml() {
        final String applicationXml = getApplication().getApplication();
        if (StringUtils.isNotBlank(applicationXml)) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(applicationXml.getBytes(StandardCharsets.UTF_8.name()))) {
                final Document appDoc = XmlUtils.createDomBuilder().parse(byteArrayInputStream);
                final String hashValue = getHashValueFromLocations(appDoc.getElementsByTagNameNS(XmlUtils.ATTACHMENTS_NS, XmlUtils.FILE_LOCATION));
                if (StringUtils.isNotBlank(hashValue)) {
                    return hashValue;
                }
                final String hashValueNoNs = getHashValueFromLocations(appDoc.getElementsByTagName(XmlUtils.FILE_LOCATION));
                if (StringUtils.isNotBlank(hashValueNoNs)) {
                    return hashValueNoNs;
                }
            } catch (RuntimeException|IOException|ParserConfigurationException|SAXException e) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(e);
                }
            }
        }

        return null;
    }

    private String getHashValueFromLocations(NodeList fileLocations) {
        for (int i = 0; i < fileLocations.getLength(); i++) {
            final Node location = fileLocations.item(i);
            final NamedNodeMap locationAttrs = location.getAttributes();
            if (locationAttrs != null) {
                final Node href = locationAttrs.getNamedItemNS(XmlUtils.ATTACHMENTS_NS, XmlUtils.HREF);
                if (href != null) {
                    if (href.getNodeValue().equals(contentId)) {
                        return XmlUtils.getHashValueFromParent(location.getParentNode());
                    }
                } else {
                    final Node hrefNoNS = locationAttrs.getNamedItem(XmlUtils.HREF);
                    if (hrefNoNS != null) {
                        if (hrefNoNS.getNodeValue().equals(contentId)) {
                            return XmlUtils.getHashValueFromParent(location.getParentNode());
                        }
                    }
                }
            }
        }
        return null;
    }

    @PostRemove
    public void removeData() {
        if (getFileDataId() != null) {
            getKcAttachmentDataDao().removeData(getFileDataId());
        }
    }

    public DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = KcServiceLocator.getService(DateTimeService.class);
        }
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public KcAttachmentService getKcAttachmentService() {
        if (kcAttachmentService == null) {
            kcAttachmentService = KcServiceLocator.getService(KcAttachmentService.class);
        }
        return kcAttachmentService;
    }

    public void setKcAttachmentService(KcAttachmentService kcAttachmentService) {
        this.kcAttachmentService = kcAttachmentService;
    }

    public KcAttachmentDataDao getKcAttachmentDataDao() {
        if (kcAttachmentDataDao == null) {
            kcAttachmentDataDao = KcServiceLocator.getService(KcAttachmentDataDao.class);
        }
        return kcAttachmentDataDao;
    }

    public void setKcAttachmentDataDao(KcAttachmentDataDao kcAttachmentDataDao) {
        this.kcAttachmentDataDao = kcAttachmentDataDao;
    }

    public GrantApplicationHashService getGrantApplicationHashService() {
        if (grantApplicationHashService == null) {
            grantApplicationHashService = KcServiceLocator.getService(GrantApplicationHashService.class);
        }

        return grantApplicationHashService;
    }

    public void setGrantApplicationHashService(GrantApplicationHashService grantApplicationHashService) {
        this.grantApplicationHashService = grantApplicationHashService;
    }
}
