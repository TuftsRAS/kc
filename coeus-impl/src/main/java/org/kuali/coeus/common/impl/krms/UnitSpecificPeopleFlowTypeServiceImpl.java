/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.krms;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.coeus.common.framework.unit.Unit;
import org.kuali.coeus.common.framework.workflow.KcAttributeCapablePeopleFlowTypeService;
import org.kuali.coeus.common.impl.workflow.KcPeopleFlowTypeServiceImpl;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.kra.krms.KcKrmsConstants;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.core.api.uif.RemotableAbstractWidget;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableAttributeLookupSettings;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("unitSpecificPeopleFlowTypeService")
public class UnitSpecificPeopleFlowTypeServiceImpl extends KcPeopleFlowTypeServiceImpl implements KcAttributeCapablePeopleFlowTypeService {
	
    public static final String UNIT_NUMBER_LOWER = "unit number";

    @Override
    public Map<String, String> resolveRoleQualifiers(PeopleFlowDefinition peopleflow, String roleId, Document document,
            DocumentContent documentContent) {
    	return Collections.singletonMap(KcKimAttributes.UNIT_NUMBER, peopleflow.getAttributes().get(KcKimAttributes.UNIT_NUMBER));
    }
    
    @Override
    public List<RemotableAttributeField> getAttributeFields(String kewTypeId) {
    	String unitClassName = Unit.class.getName();
    	String baseLookupUrl = LookupUtils.getBaseLookupUrl();
    
        RemotableQuickFinder.Builder quickFinderBuilder = RemotableQuickFinder.Builder.create(baseLookupUrl, unitClassName);
        quickFinderBuilder.setLookupParameters(Collections.singletonMap(KcKimAttributes.UNIT_NUMBER, KcKimAttributes.UNIT_NUMBER));
        quickFinderBuilder.setFieldConversions(Collections.singletonMap(KcKimAttributes.UNIT_NUMBER, KcKimAttributes.UNIT_NUMBER));

        RemotableTextInput.Builder controlBuilder = RemotableTextInput.Builder.create();
        controlBuilder.setSize(30);
        controlBuilder = RemotableTextInput.Builder.create();
        controlBuilder.setSize(Integer.valueOf(40));
        controlBuilder.setWatermark(UNIT_NUMBER_LOWER);
        
        RemotableAttributeLookupSettings.Builder lookupSettingsBuilder = RemotableAttributeLookupSettings.Builder.create();
        lookupSettingsBuilder.setCaseSensitive(Boolean.TRUE);
        lookupSettingsBuilder.setInCriteria(true);
        lookupSettingsBuilder.setInResults(true);
        lookupSettingsBuilder.setRanged(false);

        RemotableAttributeField.Builder builder = RemotableAttributeField.Builder.create(KcKimAttributes.UNIT_NUMBER);
        builder.setAttributeLookupSettings(lookupSettingsBuilder);
        builder.setName(KcKimAttributes.UNIT_NUMBER);
        builder.setRequired(true);
        builder.setDataType(DataType.STRING);
        builder.setControl(controlBuilder);
        builder.setLongLabel(KcKrmsConstants.UNIT_NUMBER);
        builder.setShortLabel(KcKrmsConstants.UNIT_NUMBER);
        builder.setMinLength(Integer.valueOf(1));
        builder.setMaxLength(Integer.valueOf(40));
        builder.setWidgets(Collections.<RemotableAbstractWidget.Builder> singletonList(quickFinderBuilder));

        return Collections.singletonList(builder.build());
    }
	
}
