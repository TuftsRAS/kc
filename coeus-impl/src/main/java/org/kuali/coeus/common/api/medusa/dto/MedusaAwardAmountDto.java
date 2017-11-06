package org.kuali.coeus.common.api.medusa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.sys.framework.json.JsonFormats;

import java.sql.Date;

public class MedusaAwardAmountDto {

    public MedusaAwardAmountDto() {

    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonFormats.DESERIALIZED_SQL_DATE_FORMAT)
    private Date currentFundEffectiveDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonFormats.DESERIALIZED_SQL_DATE_FORMAT)
    private Date finalExpirationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonFormats.DESERIALIZED_SQL_DATE_FORMAT)
    private Date obligationExpirationDate;
    private ScaleTwoDecimal anticipatedTotalAmount;
    private ScaleTwoDecimal amountObligatedToDate;

    public Date getCurrentFundEffectiveDate() {
        return currentFundEffectiveDate;
    }

    public Date getFinalExpirationDate() {
        return finalExpirationDate;
    }

    public Date getObligationExpirationDate() {
        return obligationExpirationDate;
    }

    public ScaleTwoDecimal getAnticipatedTotalAmount() {
        return anticipatedTotalAmount;
    }

    public ScaleTwoDecimal getAmountObligatedToDate() {
        return amountObligatedToDate;
    }
}