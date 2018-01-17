/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.api.medusa.dto;

import com.codiform.moo.annotation.Property;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.kuali.coeus.sys.framework.json.JsonFormats;

import java.sql.Date;

public class MedusaNegotiationDto {

    public MedusaNegotiationDto() {

    }

    private String documentNumber;
    private Long negotiationId;
    @Property(source = "mvel:?negotiationStatus.?description")
    private String negotiationStatus;
    @Property(source = "mvel:?negotiator.?userName")
    private String negotiator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonFormats.DESERIALIZED_SQL_DATE_FORMAT)
    private Date negotiationStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonFormats.DESERIALIZED_SQL_DATE_FORMAT)
    private Date negotiationEndDate;
    @Property(source = "mvel:?negotiationAgreementType.?description")
    private String agreementType;
    private int negotiationAge;

    public String getDocumentNumber() {
        return documentNumber;
    }

    public Long getNegotiationId() {
        return negotiationId;
    }

    public String getNegotiationStatus() {
        return negotiationStatus;
    }

    public String getNegotiator() {
        return negotiator;
    }

    public Date getNegotiationStartDate() {
        return negotiationStartDate;
    }

    public Date getNegotiationEndDate() {
        return negotiationEndDate;
    }

    public String getAgreementType() {
        return agreementType;
    }

    public int getNegotiationAge() {
        return negotiationAge;
    }
}
