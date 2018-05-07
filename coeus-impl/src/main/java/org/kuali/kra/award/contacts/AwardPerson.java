/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.contacts;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.person.KcPerson;
import org.kuali.coeus.common.framework.person.PropAwardPersonRole;
import org.kuali.coeus.common.framework.person.PropAwardPersonRoleService;
import org.kuali.coeus.common.framework.rolodex.NonOrganizationalRolodex;
import org.kuali.coeus.common.framework.rolodex.PersonRolodex;
import org.kuali.coeus.common.framework.sponsor.Sponsorable;
import org.kuali.coeus.propdev.impl.person.creditsplit.CreditSplitConstants;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.awardhierarchy.sync.AwardSyncableProperty;
import org.kuali.kra.award.home.ContactRole;
import org.kuali.kra.bo.AbstractProjectPerson;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import java.util.*;

public class AwardPerson extends AwardContact implements PersonRolodex, Comparable<AwardPerson>, AbstractProjectPerson {

    private static final long serialVersionUID = 7980027108784055721L;

    @AwardSyncableProperty
    private ScaleTwoDecimal academicYearEffort;

    @AwardSyncableProperty
    private ScaleTwoDecimal calendarYearEffort;

    @AwardSyncableProperty
    private boolean faculty;

    @AwardSyncableProperty
    private ScaleTwoDecimal summerEffort;

    @AwardSyncableProperty
    private ScaleTwoDecimal totalEffort;

    @AwardSyncableProperty
    private String keyPersonRole;
    
    @AwardSyncableProperty
    private boolean optInUnitStatus;

    @AwardSyncableProperty
    private List<AwardPersonUnit> units;

    private List<AwardPersonCreditSplit> creditSplits;
    private Boolean includeInCreditAllocation;
    private transient ParameterService parameterService;
    
    private transient PropAwardPersonRoleService propAwardPersonRoleService;

    public AwardPerson() {
        super();
        init();
    }

    public AwardPerson(NonOrganizationalRolodex rolodex, ContactRole contactRole) {
        super(rolodex, contactRole);
        init();
    }

    public AwardPerson(KcPerson person, ContactRole role) {
        super(person, role);
        init();
    }

    public void add(AwardPersonCreditSplit creditSplit) {
        creditSplits.add(creditSplit);
        creditSplit.setAwardPerson(this);
    }

    public void add(AwardPersonUnit awardPersonUnit) {
        units.add(awardPersonUnit);
        awardPersonUnit.setAwardPerson(this);
    }

    public void setIncludeInCreditAllocation(Boolean includeInCreditAllocation) {
        this.includeInCreditAllocation = includeInCreditAllocation;
    }

    public ScaleTwoDecimal getAcademicYearEffort() {
        return academicYearEffort;
    }

    public ScaleTwoDecimal getCalendarYearEffort() {
        return calendarYearEffort;
    }

    public AwardPersonCreditSplit getCreditSplit(int index) {
        return creditSplits.get(index);
    }


    public List<AwardPersonCreditSplit> getCreditSplits() {
        return creditSplits;
    }

    public ScaleTwoDecimal getSummerEffort() {
        return summerEffort;
    }

    public ScaleTwoDecimal getTotalEffort() {
        return totalEffort;
    }

    public AwardPersonUnit getUnit(int index) {
        return units.get(index);
    }

    public AwardPersonUnit getUnit(String unitNumber) {
        for (AwardPersonUnit awardPersonUnit : this.getUnits()) {
            if (awardPersonUnit.getUnitNumber().equals(unitNumber)) {
                return awardPersonUnit;
            }
        }
        return null;
    }

    public List<AwardPersonUnit> getUnits() {
        return units;
    }

    public boolean isCoInvestigator() {
        return StringUtils.equals(getContactRoleCode(), ContactRole.COI_CODE);
    }

    public boolean isFaculty() {
        return faculty;
    }

    public boolean isKeyPerson() {
        return StringUtils.equals(getContactRoleCode(), ContactRole.KEY_PERSON_CODE);
    }


    @Override
    public boolean isPrincipalInvestigator() {
        return StringUtils.equals(getContactRoleCode(), ContactRole.PI_CODE);
    }
    
    @Override
    public boolean isMultiplePi() {
    	return StringUtils.equals(getContactRoleCode(), PropAwardPersonRole.MULTI_PI);
    }

    public void setAcademicYearEffort(ScaleTwoDecimal academicYearEffort) {
        this.academicYearEffort = academicYearEffort;
    }

    public void setCalendarYearEffort(ScaleTwoDecimal calendarYearEffort) {
        this.calendarYearEffort = calendarYearEffort;
    }

    public void setCreditSplits(List<AwardPersonCreditSplit> creditSplits) {
        this.creditSplits = creditSplits;
    }

    public void setFaculty(boolean faculty) {
        this.faculty = faculty;
    }

    public void setSummerEffort(ScaleTwoDecimal summerEffort) {
        this.summerEffort = summerEffort;
    }

    public void setTotalEffort(ScaleTwoDecimal totalEffort) {
        this.totalEffort = totalEffort;
    }

    public void setUnits(List<AwardPersonUnit> units) {
        this.units = units;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class getContactRoleType() {
        return PropAwardPersonRole.class;
    }

    @Override
    protected Map<String, Object> getContactRoleIdentifierMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", getRoleCode());
        map.put("sponsorHierarchyName", getPropAwardPersonRoleService().getSponsorHierarchy(getAward().getSponsorCode()));
        return map;
     }

    protected void init() {
        units = new ArrayList<>();
        creditSplits = new ArrayList<>();
        optInUnitStatus = false;
    }

    @Override
    public String getProjectRole() {
        return getContactRole().getRoleDescription();
    }

    @Override
    public boolean isOtherSignificantContributorFlag() {
        return false;
    }

    public String getKeyPersonRole() {
        return keyPersonRole;
    }

    public void setKeyPersonRole(String keyPersonRole) {
        this.keyPersonRole = keyPersonRole;
    }

    @Override
    public int compareTo(AwardPerson o) {
        int lastNameComp = ObjectUtils.compare(this.getLastName(), o.getLastName());
        if (lastNameComp != 0) {
            return lastNameComp;
        } else {
            return ObjectUtils.compare(this.getFirstName(), o.getFirstName());
        }
    }

    @Override
    public Sponsorable getParent() {
        return getAward();
    }

    @Override
    public String getInvestigatorRoleDescription() {
    	return getContactRole().getRoleDescription();
    }

    public boolean isOptInUnitStatus() {
        return optInUnitStatus;
    }

    public void setOptInUnitStatus(boolean optInUnitStatus) {
        this.optInUnitStatus = optInUnitStatus;
    }
    
    @Override
    public String getLastName() {
        String lastName = null;
        if (getPerson() != null) {
            lastName = getPerson().getLastName();
        } else if (getRolodex() != null) {
            lastName = getRolodex().getLastName();
        }
        return lastName;
    }
    
    public String getFirstName() {
        String firstName = null;
        if (getPerson() != null) {
            firstName = getPerson().getFirstName();
        } else if (getRolodex() != null) {
            firstName = getRolodex().getFirstName();
        }
        return firstName;
    }
    
    public boolean getIsRolodexPerson() {
        return this.getRolodex() != null;
    }
    
    @Override
    public void setContactRoleCode(String roleCode) {
        Boolean isRoleChanged = !StringUtils.equals(roleCode, this.roleCode);
        super.setContactRoleCode(roleCode);

        if (isRoleChanged) {
            updateBasedOnRoleChange();
        }
    }
    
    private void updateBasedOnRoleChange() {
        if (PropAwardPersonRole.KEY_PERSON.equals(roleCode)) {
            this.setOptInUnitStatus(true);
        } else {
            if (this.getPerson() != null && this.getPerson().getUnit() != null && this.getUnits().isEmpty()) {
                this.add(new AwardPersonUnit(this, this.getPerson().getUnit(), true));
            }
        }

        getUnits().stream().forEach(awardPersonUnit -> {
            Boolean isLeadUnit = awardPersonUnit.getUnitNumber().equals(getPerson().getUnit().getUnitNumber()) && isPrincipalInvestigator();
            awardPersonUnit.setLeadUnit(isLeadUnit);
        });
    }

    @Override
    protected ContactRole refreshContactRole() {
    	if (StringUtils.isNotBlank(getRoleCode()) && getParent() != null && StringUtils.isNotBlank(getParent().getSponsorCode())) {
    		contactRole = getPropAwardPersonRoleService().getRole(getRoleCode(), getParent().getSponsorCode());
    	} else {
    		contactRole = null;
    	}
    	return contactRole;
    }

	@Override
    public PropAwardPersonRoleService getPropAwardPersonRoleService() {
		if (propAwardPersonRoleService == null) {
			propAwardPersonRoleService = KcServiceLocator.getService(PropAwardPersonRoleService.class);
		}
		return propAwardPersonRoleService;
	}

	@Override
    public void setPropAwardPersonRoleService(
			PropAwardPersonRoleService propAwardPersonRoleService) {
		this.propAwardPersonRoleService = propAwardPersonRoleService;
	}
	
	@Override
	public Integer getOrdinalPosition() {
		return 0;
	}
	
	@Override
	public boolean isInvestigator() {
		return isPrincipalInvestigator() || isMultiplePi() || isCoInvestigator() || (isKeyPerson() && isOptInUnitStatus());
	}

    public Boolean getIncludeInCreditAllocation() {
        if (includeInCreditAllocation == null) {
            includeInCreditAllocation = defaultIncludeInCreditAllocation(roleCode);
        }

        return includeInCreditAllocation;
    }

    public Boolean defaultIncludeInCreditAllocation(String proposalPersonRoleId) {
        final Collection<String> roles = getParameterService().getParameterValuesAsString(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT,
                Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, CreditSplitConstants.CREDIT_SPLIT_OPT_IN_DEFAULT_ROLES);

        return StringUtils.isNotBlank(proposalPersonRoleId)
                && roles.contains(proposalPersonRoleId);
    }

    public ParameterService getParameterService() {
        if(parameterService == null) {
            parameterService = KcServiceLocator.getService(ParameterService.class);
        }
        return parameterService;
    }

}
