/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.location;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.kuali.coeus.common.framework.org.Organization;
import org.kuali.coeus.common.framework.rolodex.Rolodex;
import org.kuali.coeus.propdev.api.location.ProposalSiteContract;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.state.ProposalState;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a Proposal Site. It can either refer to an Organization, or to
 * a Rolodex entry.
 */
@Entity
@Table(name = "EPS_PROP_SITES")
@IdClass(ProposalSite.ProposalSiteId.class)
public class ProposalSite extends KcPersistableBusinessObjectBase implements ProposalSiteContract {

    private static final long serialVersionUID = -1657749549230077805L;

    // proposal site types, see LOCATION_TYPE table
    public static final int PROPOSAL_SITE_APPLICANT_ORGANIZATION = 1;

    public static final int PROPOSAL_SITE_PERFORMING_ORGANIZATION = 2;

    public static final int PROPOSAL_SITE_OTHER_ORGANIZATION = 3;

    public static final int PROPOSAL_SITE_PERFORMANCE_SITE = 4;

    private static final String ORGANIZATION = "organization";
    private static final String ROLODEX = "rolodex";

    @Id
    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "PROPOSAL_NUMBER")
    private DevelopmentProposal developmentProposal;

    @Id
    @Column(name = "SITE_NUMBER")
    private Integer siteNumber;

    @Column(name = "LOCATION_NAME", nullable = false)
    private String locationName;

    @Column(name = "LOCATION_TYPE_CODE", nullable = false)
    private Integer locationTypeCode;

    @Column(name = "ORGANIZATION_ID")
    private String organizationId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "ORGANIZATION_ID", referencedColumnName = "ORGANIZATION_ID", insertable = false, updatable = false)
    private Organization organization;

    @Column(name = "ROLODEX_ID")
    private Integer rolodexId;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "ROLODEX_ID", referencedColumnName = "ROLODEX_ID", insertable = false, updatable = false)
    private Rolodex rolodex;

    @Column(name = "ADDRESS_LINE_1")
    private String addressLine1;

    @Column(name = "ADDRESS_LINE_2")
    private String addressLine2;

    @Column(name = "ADDRESS_LINE_3")
    private String addressLine3;

    @Column(name = "CITY")
    private String city;

    @Column(name = "COUNTY")
    private String county;

    @Column(name = "STATE")
    private String state;

    @Column(name = "POSTAL_CODE")
    private String postalCode;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @OneToMany(mappedBy = "proposalSite", orphanRemoval = true, cascade = { CascadeType.ALL })
    @OrderBy("proposalSite")
    private List<CongressionalDistrict> congressionalDistricts;

    public ProposalSite() {
        congressionalDistricts = new ArrayList<CongressionalDistrict>();
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public String getLocationName() {
        return locationName;
    }

    public void setLocationTypeCode(Integer locationTypeCode) {
        this.locationTypeCode = locationTypeCode;
    }

    @Override
    public Integer getLocationTypeCode() {
        return locationTypeCode;
    }

    public void setOrganizationId(String organizationId) {
        if (!Objects.equals(getOrganizationId(), organizationId) || shouldDataBeAlwaysCopied()) {
            this.organizationId = organizationId;
            if (getOrganizationId() != null) {
                refreshReferenceObject(ORGANIZATION);
            }

            initializeOrganizationRelatedFields();
        }
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    private void initializeOrganizationRelatedFields() {
        setLocationName(getOrganization() == null ? null : getOrganization().getOrganizationName());
        setRolodexId(getOrganization() == null ? null : getOrganization().getContactAddressId());
    }

    @Override
    public Organization getOrganization() {
        return organization;
    }

    public void setRolodexId(Integer rolodexId) {
        if (!Objects.equals(getRolodexId(), rolodexId) || shouldDataBeAlwaysCopied()) {
            congressionalDistricts.clear();

            this.rolodexId = rolodexId;
            if (getRolodexId() != null) {
                refreshReferenceObject(ROLODEX);
            }

            initializeRolodexRelatedFields();
        }
    }

    public Integer getRolodexId() {
        return rolodexId;
    }

    public void setRolodex(Rolodex rolodex) {
        this.rolodex = rolodex;
    }

    @Override
    public Rolodex getRolodex() {
        return rolodex;
    }

    private void initializeRolodexRelatedFields() {
        if (getOrganizationId() == null) {
            setLocationName(getRolodex() == null ? null : getRolodex().getOrganization());
        }

        setAddressLine1((getRolodex() == null) ? null : getRolodex().getAddressLine1());
        setAddressLine2((getRolodex() == null) ? null : getRolodex().getAddressLine2());
        setAddressLine3((getRolodex() == null) ? null : getRolodex().getAddressLine3());
        setCity((getRolodex() == null) ? null : getRolodex().getCity());
        setCounty((getRolodex() == null) ? null : getRolodex().getCounty());
        setState((getRolodex() == null) ? null : getRolodex().getState());
        setPostalCode((getRolodex() == null) ? null : getRolodex().getPostalCode());
        setCountryCode((getRolodex() == null) ? null : getRolodex().getCountryCode());
    }

    public void setCongressionalDistricts(List<CongressionalDistrict> congressionalDistricts) {
        this.congressionalDistricts = congressionalDistricts;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public List<CongressionalDistrict> getCongressionalDistricts() {
        return congressionalDistricts;
    }

    public void addCongressionalDistrict(CongressionalDistrict congressionalDistrict) {
        congressionalDistricts.add(congressionalDistrict);
    }

    public void deleteCongressionalDistrict(int districtIndex) {
        congressionalDistricts.remove(districtIndex);
    }

    public void setDefaultCongressionalDistrict(CongressionalDistrict congressionalDistrict) {
        if (!contains(congressionalDistrict.getCongressionalDistrict())) {
            congressionalDistricts.add(0, congressionalDistrict);
        }
    }

    /**
     * This method tests whether the ProposalSite contains a ongressional district with a given congressionalDistrict value.
     * @param congressionalDistrictIdentifier
     * @return
     */
    private boolean contains(String congressionalDistrictIdentifier) {
        for (CongressionalDistrict district : congressionalDistricts) {
            if (StringUtils.equals(district.getCongressionalDistrict(), congressionalDistrictIdentifier)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns the first congressional district defined for the ProposalSite,
     * or null if there is none.
     * @return
     */
    public CongressionalDistrict getDefaultCongressionalDistrict() {
        if (congressionalDistricts == null || congressionalDistricts.isEmpty()) {
            return null;
        } else {
            return congressionalDistricts.get(0);
        }
    }

    /**
     * This method returns the name first congressional district defined for the ProposalSite,
     * or an empty string if there is none.
     * @return
     */
    @Override
    public String getFirstCongressionalDistrictName() {
        CongressionalDistrict firstDistrict = getDefaultCongressionalDistrict();
        if (firstDistrict == null) {
            return "";
        } else {
            return firstDistrict.getCongressionalDistrict();
        }
    }

    /**
     * This method deletes all existing congressional districts and adds one new congressional
     * district.
     * @param districtIdentifier The congressional district string, e.g. "AZ-5"
     */
    public void setDefaultCongressionalDistrictIdentifier(String districtIdentifier) {
        if (!StringUtils.isEmpty(districtIdentifier) && !contains(districtIdentifier)) {
            congressionalDistricts.clear();
            CongressionalDistrict defaultDistrict = new CongressionalDistrict();
            defaultDistrict.setCongressionalDistrict(districtIdentifier);
            defaultDistrict.setProposalSite(this);
            setDefaultCongressionalDistrict(defaultDistrict);
        }
    }

    /**
     * This method creates a CongressionalDistrict from the district defined in the Organization,
     * and adds it to the list of congressional districts if it doesn't exist yet.
     * The proposalNumber and siteNumber are set on the CongressionalDistrict, so they should
     * be initialized before calling this method.
     */
    public void initializeDefaultCongressionalDistrict() {
        Organization organization = getOrganization();
        if (organization != null) {
            String defaultDistrict = organization.getCongressionalDistrict();
            if (!StringUtils.isEmpty(defaultDistrict)) {
                setDefaultCongressionalDistrictIdentifier(defaultDistrict);
            } else {
                this.getCongressionalDistricts().clear();
            }
        }
    }

    @Embeddable
    public static final class ProposalSiteId implements Serializable, Comparable<ProposalSiteId> {

    	private String developmentProposal;
    	
        private Integer siteNumber;

        public Integer getSiteNumber() {
            return this.siteNumber;
        }

        public void setSiteNumber(Integer siteNumber) {
            this.siteNumber = siteNumber;
        }

        @Override
        public String toString() {
        	return new ToStringBuilder(this).append("developmentProposal", this.developmentProposal).append("siteNumber", this.siteNumber).toString();
        }

        @Override
        public boolean equals(Object other) {
            if (other == null)
                return false;
            if (other == this)
                return true;
            if (other.getClass() != this.getClass())
                return false;
            final ProposalSiteId rhs = (ProposalSiteId) other;
            return new EqualsBuilder().append(this.developmentProposal, rhs.developmentProposal).append(this.siteNumber, rhs.siteNumber).isEquals();
        }

        @Override
        public int hashCode() {
        	return new HashCodeBuilder(17, 37).append(this.developmentProposal).append(this.siteNumber).toHashCode();
        }

        @Override
        public int compareTo(ProposalSiteId other) {
        	return new CompareToBuilder().append(this.developmentProposal, other.developmentProposal).append(this.siteNumber, other.siteNumber).toComparison();
        }

		public String getDevelopmentProposal() {
			return developmentProposal;
		}

		public void setDevelopmentProposal(String developmentProposal) {
			this.developmentProposal = developmentProposal;
		}
    }

	public DevelopmentProposal getDevelopmentProposal() {
		return developmentProposal;
	}

	public void setDevelopmentProposal(DevelopmentProposal developmentProposal) {
		this.developmentProposal = developmentProposal;
	}

	@Override
    public Integer getSiteNumber() {
		return siteNumber;
	}
    
	public void setSiteNumber(Integer siteNumber) {
		this.siteNumber = siteNumber;
	}

    @Override
    public String getProposalNumber(){
        if (getDevelopmentProposal() != null){
            return getDevelopmentProposal().getProposalNumber();
        }
        return null;
    }

    public Boolean shouldDataBeAlwaysCopied() {
        String proposalStateTypeCode = getDevelopmentProposal().getProposalStateTypeCode();
        return proposalStateTypeCode.equals(ProposalState.IN_PROGRESS) || proposalStateTypeCode.equals(ProposalState.REVISIONS_REQUESTED);
    }
}
