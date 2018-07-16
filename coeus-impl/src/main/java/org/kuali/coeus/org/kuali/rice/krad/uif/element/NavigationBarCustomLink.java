/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.org.kuali.rice.krad.uif.element;

import org.kuali.coeus.common.util.EnvironmentUtil;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.NavigationBar;

public class NavigationBarCustomLink extends NavigationBar {

	private static final long serialVersionUID = -3341001364063327193L;

	private Action brandImageLink;

	private String environmentText;

	public String getEnvironmentText() {
		if (environmentText == null) {
			setEnvironmentText(EnvironmentUtil.getInstance().getEnvironmentText());
		}

		return environmentText;
	}

	public void setEnvironmentText(String environmentText) {
		this.environmentText = environmentText;
	}

	public Action getBrandImageLink() {
		return brandImageLink;
	}

	public void setBrandImageLink(Action brandImageLink) {
		this.brandImageLink = brandImageLink;
	}
}
