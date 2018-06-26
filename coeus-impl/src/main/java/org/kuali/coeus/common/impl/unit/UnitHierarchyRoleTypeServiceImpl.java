/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.unit;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.unit.Unit;
import org.kuali.coeus.common.framework.unit.UnitService;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAbstractWidget;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("unitHierarchyRoleTypeService")
public class UnitHierarchyRoleTypeServiceImpl extends RoleTypeServiceBase {

    public static final String DESCENDS_HIERARCHY_N = "N";
    public static final String DESCENDS_HIERARCHY_Y = "Y";
    public static final String DESCENDS_HIERARCHY_YES = "Yes";
    public static final String UNIT_NUMBER_WILDCARD = "*";
    private static final String KIM_UI_CHECKBOX_DEFAULT_VALUE = "no";

    @Autowired
    @Qualifier("unitService")
    private UnitService unitService;

    @Autowired
    @Qualifier("kualiConfigurationService")
    private ConfigurationService kualiConfigurationService;
    
    @Override
    public boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {        
        if (roleQualifiedByUnitHierarchy(roleQualifier) && qualification.containsKey(KcKimAttributes.UNIT_NUMBER)) {
            return (performWildCardMatching(qualification, roleQualifier) || unitQualifierMatches(qualification, roleQualifier) || unitQualifierMatchesHierarchy(qualification, roleQualifier));
        }
        
        return false; 
    }
    
    /**
     * Rice does not currently handle situations where boolean role qualifiers like the descends flag are empty and treats them 
     * like strings causing stack traces. Overriding original method to handle this.
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#validateDataDictionaryAttribute(org.kuali.rice.kim.api.type.KimTypeAttribute, java.lang.String, java.lang.String)
     */
    @Override
    protected List<RemotableAttributeError> validateDataDictionaryAttribute(KimTypeAttribute attr, String key, String value) {
        if(StringUtils.equalsIgnoreCase(attr.getKimAttribute().getAttributeName(), KcKimAttributes.SUBUNITS) 
                && StringUtils.isBlank(value)) {
            return super.validateDataDictionaryAttribute(attr, key, KIM_UI_CHECKBOX_DEFAULT_VALUE);
        }

        return super.validateDataDictionaryAttribute(attr, key, value);
    }
    
    @Override
    public List<RemotableAttributeError> validateAttributes(String kimTypeId, Map<String, String> attributes) { 
        List<RemotableAttributeError> validationErrors = new ArrayList<RemotableAttributeError>();
        if(roleQualifiedByUnitHierarchy(attributes) && attributes.containsKey(KcKimAttributes.UNIT_NUMBER)) {
            validationErrors = super.validateAttributes(kimTypeId, attributes);
        }
        
        return validationErrors;
    }
    
    protected boolean roleQualifiedByUnitHierarchy(Map<String,String> roleQualifier) {
        return roleQualifier.containsKey(KcKimAttributes.UNIT_NUMBER) && roleQualifier.containsKey(KcKimAttributes.SUBUNITS);
    }
    
    protected boolean unitQualifierMatches(Map<String,String> qualification, Map<String,String> roleQualifier) {
        return StringUtils.equals(qualification.get(KcKimAttributes.UNIT_NUMBER), roleQualifier.get(KcKimAttributes.UNIT_NUMBER));
    }
    
    protected boolean unitQualifierMatchesHierarchy(Map<String,String> qualification, Map<String,String> roleQualifier) {
        boolean qualifierMatches = false;
        String unitNumber = qualification.get(KcKimAttributes.UNIT_NUMBER);
        
        while (!qualifierMatches) {
            final Unit unit = this.unitService.getUnit(unitNumber);
            if (unit == null) {
                break;
            }
            final String parentUnitNumber = unit.getParentUnitNumber();
            if (parentUnitNumber == null) {
                break;
            }
            qualifierMatches = ( StringUtils.equals(parentUnitNumber, roleQualifier.get(KcKimAttributes.UNIT_NUMBER)) && descendsSubunits(roleQualifier));
            unitNumber = parentUnitNumber;
        }
        
        return qualifierMatches;
    }
    
    
    protected boolean descendsSubunits(Map<String,String> roleQualifier) {
        return StringUtils.equalsIgnoreCase(DESCENDS_HIERARCHY_Y, roleQualifier.get(KcKimAttributes.SUBUNITS)) ||
                StringUtils.equalsIgnoreCase(DESCENDS_HIERARCHY_YES, roleQualifier.get(KcKimAttributes.SUBUNITS));
    }
    
    protected boolean performWildCardMatching(Map<String,String> qualification, Map<String,String> roleQualifier) {
        final String unitNumber = qualification.get(KcKimAttributes.UNIT_NUMBER);
        if (UNIT_NUMBER_WILDCARD.equalsIgnoreCase(unitNumber)) {
            return true;
        }
        //If necessary, we can include logic for other pattern matching later.
        //Not found necessary at this time.
        return false;
    }
    
    @Override
    public List<String> getUniqueAttributes(String kimTypeId){
        return new ArrayList<String>();
    }    
    
    @Override
    public List<KimAttributeField> getAttributeDefinitions(String kimTypeId) {
        if (StringUtils.isBlank(kimTypeId)) {
            throw new RiceIllegalArgumentException("kimTypeId was null or blank");
        }

        List<KimAttributeField> attributeList = new ArrayList<KimAttributeField>(super.getAttributeDefinitions(kimTypeId));

        for (int i = 0; i < attributeList.size(); i++) {
            final KimAttributeField definition = attributeList.get(i);
            if (KcKimAttributes.UNIT_NUMBER.equals(definition.getAttributeField().getName())) {
                KimAttributeField.Builder b = KimAttributeField.Builder.create(definition);

                String baseUrl = getKualiConfigurationService().getPropertyValueAsString("application.lookup.url");
                Collection<RemotableAbstractWidget.Builder> widgetsCopy = new ArrayList<RemotableAbstractWidget.Builder>();
                for (RemotableAbstractWidget.Builder widget : b.getAttributeField().getWidgets()) {
                    if(widget instanceof RemotableQuickFinder.Builder) {
                        RemotableQuickFinder.Builder orig = (RemotableQuickFinder.Builder) widget;
                        RemotableQuickFinder.Builder copy = RemotableQuickFinder.Builder.create(baseUrl, orig.getDataObjectClass());
                        copy.setLookupParameters(orig.getLookupParameters());
                        copy.setFieldConversions(orig.getFieldConversions());
                        widgetsCopy.add(copy);
                    } else {
                        widgetsCopy.add(widget);
                    }
                }
                b.getAttributeField().setWidgets(widgetsCopy);
                attributeList.set(i, b.build());
            }
        }
        return Collections.unmodifiableList(attributeList);
    }    

    /*
     * Should override if derivedRoles should not to be cached.  Currently defaults to system-wide default.
     */
    @Override
    public boolean dynamicRoleMembership(String namespaceCode, String roleName) {
        super.dynamicRoleMembership(namespaceCode, roleName);
        return true;
    }

    public UnitService getUnitService() {
        return unitService;
    }

    public void setUnitService(UnitService unitService) {
        this.unitService = unitService;
    }

    protected ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
