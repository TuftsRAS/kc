/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.cgb;

import org.kuali.coeus.sys.framework.util.ValuesFinderUtils;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.EnumValuesFinder;

import java.util.List;

public class InvoicingOptionsValuesFinder extends EnumValuesFinder {

	public InvoicingOptionsValuesFinder() {
		super(AwardCgbConstants.InvoicingOptions.Types.class);
	}
	
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> labels = super.getKeyValues();
        labels.add(0, ValuesFinderUtils.getSelectOption());
        return labels;
    }
	
    @Override
    protected String getEnumKey(Enum enm) {
        return ((AwardCgbConstants.InvoicingOptions.Types)enm).getCode();
    }

    @Override
    protected String getEnumLabel(Enum enm) {
        return ((AwardCgbConstants.InvoicingOptions.Types)enm).getName();
    }

}
