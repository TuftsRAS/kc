/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.home;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;

public class ValidBasisMethodPayment extends KcPersistableBusinessObjectBase {

    private static final Logger LOG = LogManager.getLogger(ValidBasisMethodPayment.class);

    private static final long serialVersionUID = 1L;

    private Integer validBasisMethodPaymentId;

    private String basisOfPaymentCode;

    private String methodOfPaymentCode;

    private String invInstructionsIndicator;

    private AwardMethodOfPayment methodOfPayment;

    private AwardBasisOfPayment basisOfPayment;

    public ValidBasisMethodPayment() {
    }

    public Integer getValidBasisMethodPaymentId() {
        return validBasisMethodPaymentId;
    }

    public void setValidBasisMethodPaymentId(Integer validBasisMethodPaymentId) {
        this.validBasisMethodPaymentId = validBasisMethodPaymentId;
    }

    public String getBasisOfPaymentCode() {
        return basisOfPaymentCode;
    }

    public void setBasisOfPaymentCode(String basisOfPaymentCode) {
        this.basisOfPaymentCode = basisOfPaymentCode;
    }

    public String getMethodOfPaymentCode() {
        return methodOfPaymentCode;
    }

    public void setMethodOfPaymentCode(String methodOfPaymentCode) {
        this.methodOfPaymentCode = methodOfPaymentCode;
    }

    public String getInvInstructionsIndicator() {
        return invInstructionsIndicator;
    }

    public InvInstructionsIndicatorConstants getInvInstructionsIndicatorConstant() {
        InvInstructionsIndicatorConstants result = InvInstructionsIndicatorConstants.getByCode(invInstructionsIndicator);
        if (result == null && invInstructionsIndicator != null) LOG.warn(String.format("ValidBasisMethodPayment with id = %s has invalid InvInstructionsIndicator value of %s", getValidBasisMethodPaymentId(), getInvInstructionsIndicator()));
        return result;
    }

    public void setInvInstructionsIndicator(String invInstructionsIndicator) {
        this.invInstructionsIndicator = invInstructionsIndicator;
    }

    public String getInvInstructionsIndicatorName() {
        InvInstructionsIndicatorConstants result = InvInstructionsIndicatorConstants.getByCode(invInstructionsIndicator);
        if (result == null && invInstructionsIndicator != null) {
            LOG.warn(String.format("ValidBasisMethodPayment with id = %s has invalid InvInstructionsIndicator value of %s", getValidBasisMethodPaymentId(), getInvInstructionsIndicator()));
            return null;
        }
        return result.name();
    }

    public AwardMethodOfPayment getMethodOfPayment() {
        return methodOfPayment;
    }

    public void setMethodOfPayment(AwardMethodOfPayment methodOfPayment) {
        this.methodOfPayment = methodOfPayment;
    }

    public AwardBasisOfPayment getBasisOfPayment() {
        return basisOfPayment;
    }

    public void setBasisOfPayment(AwardBasisOfPayment basisOfPayment) {
        this.basisOfPayment = basisOfPayment;
    }
}
