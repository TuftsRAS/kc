/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.rolodex;

import java.util.ArrayList;
import java.util.List;

import org.kuali.coeus.common.framework.rolodex.Rolodex;
import org.kuali.coeus.sys.framework.lookup.KcKualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("rolodexLookupableHelperService")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RolodexLookupableHelperServiceImpl extends KcKualiLookupableHelperServiceImpl {

    private static final String IS_SPONSOR_ADDRESS = "isSponsorAddress";
    private static final String SPONSOR_NAME = "sponsor.sponsorName";

	@Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
		List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
		if(businessObject instanceof Rolodex) {
			Rolodex rolodex = (Rolodex)businessObject;
			if(rolodex.getSponsorAddressFlag()) {
		        if (allowsMaintenanceNewOrCopyAction()) {
		        	
		            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
		        }
			}
			else 
				htmlDataList = super.getCustomActionUrls(businessObject, pkNames);
			
		}
		return htmlDataList;
	}
}

