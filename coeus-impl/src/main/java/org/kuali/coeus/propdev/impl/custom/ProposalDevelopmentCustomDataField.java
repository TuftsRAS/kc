/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.custom;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.custom.arg.ArgValueLookup;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeDocValue;
import org.kuali.coeus.common.framework.person.KcPerson;
import org.kuali.coeus.common.framework.rolodex.Rolodex;
import org.kuali.coeus.common.framework.rolodex.RolodexConstants;
import org.kuali.coeus.common.impl.custom.arg.ArgValueLookupValuesFinder;
import org.kuali.kra.web.krad.KcBindingInfo;
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.MultiValueControlBase;
import org.kuali.rice.krad.uif.control.TextControlBase;
import org.kuali.rice.krad.uif.field.InputFieldBase;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.UifKeyValue;

import java.util.Arrays;


public class ProposalDevelopmentCustomDataField extends InputFieldBase {

    private static final long serialVersionUID = -3826980245026916902L;

    private static final String CUSTOM_DATA_DATE_TYPE = "3";
    private static final String CUSTOM_DATA_BOOLEAN_TYPE = "4";

    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        Object myBo = ObjectPropertyUtils.getPropertyValue(model, KcBindingInfo.getParentBindingInfo(getBindingInfo()));
        CustomAttributeDocValue customData = (CustomAttributeDocValue) myBo;
        this.setId(StringUtils.removePattern(customData.getCustomAttribute().getGroupName() + "_" + customData.getCustomAttribute().getLabel(), "([^0-9a-zA-Z\\-_])"));
        if (StringUtils.isNotBlank(customData.getCustomAttribute().getLookupClass())) {
            if (customData.getCustomAttribute().getLookupClass().equals(ArgValueLookup.class.getName())) {
                setControl((Control) ComponentFactory.getNewComponentInstance("Uif-DropdownControl"));
                ArgValueLookupValuesFinder valuesFinder = new ArgValueLookupValuesFinder();
                valuesFinder.setArgName(customData.getCustomAttribute().getLookupReturn());
                valuesFinder.setAddBlankOption(false);
                valuesFinder.setCurrentValue(customData.getValue());
                setOptionsFinder(valuesFinder);
                getInquiry().setRender(true);
            } else {
                if (customData.getCustomAttribute().getLookupClass().equals(KcPerson.class.getName())) {
                    if (customData.getCustomAttribute().getLookupReturn().equals("userName")) {
                        getSuggest().getSuggestQuery().setDataObjectClassName(PrincipalBo.class.getName());
                        getSuggest().setValuePropertyName("principalName");
                        getSuggest().setRender(true);
                    }
                } else {
                    getSuggest().setValuePropertyName(customData.getCustomAttribute().getLookupReturn());
                    getSuggest().getSuggestQuery().setDataObjectClassName(customData.getCustomAttribute().getLookupClass());
                    getSuggest().setRender(true);
                }

                getQuickfinder().setRender(true);
                getQuickfinder().setDataObjectClassName(customData.getCustomAttribute().getLookupClass());
                getQuickfinder().getFieldConversions().put(customData.getCustomAttribute().getLookupReturn(), getPropertyName());

                if (customData.getCustomAttribute().getLookupClass().equals(Rolodex.class.getName())) {
                    getQuickfinder().setViewName(RolodexConstants.EDITABLE_ROLODEX_QUICKFINDER);
                }
            }
        } else if (customData.getCustomAttribute().getDataTypeCode().equals(CUSTOM_DATA_DATE_TYPE)) {
            setControl((Control) ComponentFactory.getNewComponentInstance("Uif-DateControlOnFocus"));
            ((TextControlBase)getControl()).setWatermarkText("mm/dd/yyyy");
        } else if (customData.getCustomAttribute().getDataTypeCode().equals(CUSTOM_DATA_BOOLEAN_TYPE)) {
            MultiValueControlBase booleanControl = (MultiValueControlBase) ComponentFactory.getNewComponentInstance("Uif-HorizontalRadioControl");
            booleanControl.setOptions(Arrays.asList(new UifKeyValue("Yes", "Yes"), new UifKeyValue("No", "No")));
            setControl(booleanControl);
        }
        super.performApplyModel(model, parent);
    }
}
