/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.web.krad.homepage;

import java.util.List;

import org.kuali.kra.web.krad.homepage.HomePageMenuItemServiceImpl.HomePageItemSuggestion;

public interface HomePageMenuItemService {

	List<HomePageItemSuggestion> getActiveMenuItems();
	List<HomePageItemSuggestion> getAllMenuItems();
}