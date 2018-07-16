/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package co.kuali.coeus.web.jstl;

import java.util.Arrays;
import java.util.List;

import org.kuali.coeus.common.util.EnvironmentUtil;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationContract;
import org.kuali.rice.kim.impl.identity.PersonImpl;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PortalFunctions {

	//STAFF,FCLTY,AFLT, etc...
    /**
     * Determines if the given the currently logged in user has affiliations of the types specified in <code>types</code>
     * 
     * @param types comma separated list of affiliation types. It will get split.
     * @return true if the currently logged in user has ANY type in <code>types</code>.
     */
	public static boolean showByAffiliateType(String types){
		boolean success = false;
        if (GlobalVariables.getUserSession() == null) {
            return false;
        }
		final PersonImpl currentUser = (PersonImpl) GlobalVariables.getUserSession().getPerson();
		
		if (ObjectUtils.isNull(currentUser)){
			return false;
		}
		final List<String> typesList = Arrays.asList(types.split(","));

		for (int i = 0; currentUser.getAffiliations() != null 
                      && i < currentUser.getAffiliations().size(); i++){
			if (currentUser.getAffiliations().get(i) != null 
                && currentUser.getAffiliations().get(i).isDefaultValue() 
                && currentUser.getAffiliations().get(i).isActive()) {
                
				if (currentUser.getAffiliations().get(i).getAffiliationType() != null 
                    && typesList.contains(currentUser.getAffiliations().get(i).getAffiliationType().getCode())){
					success = true;
				}
				break;
			}
		}
		return success;
	}

    /**
     * This is not like {@link #showByAffiliateType(String)}. It is not checking if a user has a specific affiliation. Rather
     * it checks if the currently logged in user has any affiliation whatsoever. An affiliation is only valid if it is active.
     *
     * @return if the currently logged in user has an affiliation at all, or false otherwise.
     */
    public static boolean hasAffiliation() {
        if (GlobalVariables.getUserSession() == null) {
            return false;
        }

		final PersonImpl currentUser = (PersonImpl) GlobalVariables.getUserSession().getPerson();

		if (ObjectUtils.isNull(currentUser)){
			return false;
		}

        for (final EntityAffiliationContract affiliation : currentUser.getAffiliations()) {
            if (affiliation != null && affiliation.isActive()) {
                return true;
            }
        }
        return false;
    }

    public static String getEnvironmentText() {
    	return EnvironmentUtil.getInstance().getEnvironmentText();
	}
}
