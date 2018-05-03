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
import org.kuali.coeus.common.framework.attachment.KcAttachmentService;
import org.kuali.coeus.propdev.api.s2s.override.S2sOverrideApplicationDataContract;
import org.kuali.coeus.propdev.impl.s2s.FormUtilityService;
import org.kuali.coeus.s2sgen.api.hash.GrantApplicationHashService;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.file.FileMeta;
import org.kuali.rice.krad.web.bind.RequestAccessible;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.persistence.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "S2S_OVERRIDE_APPL_DATA")
public class S2sOverrideApplicationData extends KcPersistableBusinessObjectBase implements S2sOverrideApplicationDataContract, FileMeta, org.kuali.rice.core.api.mo.common.Identifiable {

    private static final Log LOG = LogFactory.getLog(S2sOverrideApplicationData.class);

    @PortableSequenceGenerator(name = "SEQ_S2S_OVERRIDE_APPL_DATA_ID")
    @GeneratedValue(generator = "SEQ_S2S_OVERRIDE_APPL_DATA_ID")
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "APPLICATION")
    @Lob
    private String application;

    @Column(name = "FILE_NAME")
    private String name;

    @OneToMany(mappedBy = "application", orphanRemoval = true, cascade = {CascadeType.ALL})
    @OrderBy("contentId ASC")
    private List<S2sOverrideAttachment> attachments;

    @RequestAccessible
    @Transient
    private MultipartFile multipartFile;

    @Transient
    private transient String url;

    @Transient
    private transient DateTimeService dateTimeService;

    @Transient
    private transient KcAttachmentService kcAttachmentService;

    @Transient
    private transient GrantApplicationHashService grantApplicationHashService;

    @Transient
    private transient FormUtilityService formUtilityService;

    @Override
    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    @Override
    public List<S2sOverrideAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<S2sOverrideAttachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public void init(MultipartFile multipartFile) throws IOException {
        application = new String(multipartFile.getBytes());
        name = multipartFile.getName();
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
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return getContentType();
    }

    @Override
    public byte[] getData() {
        return application != null ? application.getBytes() : new byte[0];
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }

    @Override
    public void setContentType(String contentType) {

    }

    @Override
    public Long getSize() {
        return application != null ? (long) application.length() : 0;
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

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
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
        if (StringUtils.isNotBlank(application)) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(application.getBytes(StandardCharsets.UTF_8.name()))) {
                final Document appDoc = XmlUtils.createDomBuilder().parse(byteArrayInputStream);
                final NodeList headers = appDoc.getElementsByTagNameNS(XmlUtils.HEADER_NS, XmlUtils.GRANT_SUBMISSION_HEADER);
                for (int i = 0; i < headers.getLength(); i++) {
                    final Node header = headers.item(i);
                    header.getParentNode().removeChild(header);
                }
                return getGrantApplicationHashService().computeGrantFormsHash(getFormUtilityService().docToString(appDoc));
            } catch (RuntimeException | ParserConfigurationException | IOException | SAXException | TransformerException e) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public String getSha1HashInXml() {
        if (StringUtils.isNotBlank(application)) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(application.getBytes(StandardCharsets.UTF_8.name()))) {
                final Document appDoc = XmlUtils.createDomBuilder().parse(byteArrayInputStream);
                final NodeList headers = appDoc.getElementsByTagNameNS(XmlUtils.HEADER_NS, XmlUtils.GRANT_SUBMISSION_HEADER);
                for (int i = 0; i < headers.getLength(); i++) {
                    final Node header = headers.item(i);
                    final String hashValue = XmlUtils.getHashValueFromParent(header);
                    if (StringUtils.isNotBlank(hashValue)) {
                        return hashValue;
                    }
                }
            } catch (RuntimeException | ParserConfigurationException | IOException | SAXException e) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(e.getMessage(), e);
                }
            }
        }
        return null;
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

    public GrantApplicationHashService getGrantApplicationHashService() {
        if (grantApplicationHashService == null) {
            grantApplicationHashService = KcServiceLocator.getService(GrantApplicationHashService.class);
        }

        return grantApplicationHashService;
    }

    public void setGrantApplicationHashService(GrantApplicationHashService grantApplicationHashService) {
        this.grantApplicationHashService = grantApplicationHashService;
    }

    public FormUtilityService getFormUtilityService() {
        if (formUtilityService == null) {
            formUtilityService = KcServiceLocator.getService(FormUtilityService.class);
        }

        return formUtilityService;
    }

    public void setFormUtilityService(FormUtilityService formUtilityService) {
        this.formUtilityService = formUtilityService;
    }
}
