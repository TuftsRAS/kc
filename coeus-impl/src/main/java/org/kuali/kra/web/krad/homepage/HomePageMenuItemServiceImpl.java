/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.web.krad.homepage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static java.util.Collections.singletonMap;

@Service("homePageMenuItemService")
public class HomePageMenuItemServiceImpl implements HomePageMenuItemService {

	protected static final String APP_URL_TOKEN = "<<APPLICATION_URL>>";

	@Autowired
	@Qualifier("dataObjectService")
	private DataObjectService dataObjectService;

	@Autowired
	@Qualifier("kualiConfigurationService")
	private ConfigurationService configurationService;

	@Override
	public List<HomePageItemSuggestion> getActiveMenuItems() {

		final QueryResults<HomePageMenuItem> menuItems = this.getDataObjectService().findMatching(HomePageMenuItem.class,
				QueryByCriteria.Builder.andAttributes(singletonMap("active", "Y")).build());

		return toSuggestions(menuItems.getResults());
	}

	@Override
	public List<HomePageItemSuggestion> getAllMenuItems() {
		
		final QueryResults<HomePageMenuItem> menuItems = this.getDataObjectService().findAll(HomePageMenuItem.class);

		return toSuggestions(menuItems.getResults());
	}

	protected List<HomePageItemSuggestion> toSuggestions(List<HomePageMenuItem> menuItems) {
		final String appUrl = configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);

		return menuItems.stream()
				.map(menuItem -> new HomePageItemSuggestion(menuItem, appUrl))
				.collect(Collectors.toList());
	}

	public class HomePageItemSuggestion {
		private String label;
		private String value;
		private String href;

		public HomePageItemSuggestion(HomePageMenuItem item, String appUrl) {
			this.setLabel(item.getMenuItemFormatted());
			this.setValue(item.getMenuItem());

			String href;
			try {
				href = appUrl + item.getMenuAction().replace(APP_URL_TOKEN, URLEncoder.encode(appUrl, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			this.setHref(href);
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			HomePageItemSuggestion that = (HomePageItemSuggestion) o;

			if (label != null ? !label.equals(that.label) : that.label != null) return false;
			if (value != null ? !value.equals(that.value) : that.value != null) return false;
			return href != null ? href.equals(that.href) : that.href == null;
		}

		@Override
		public int hashCode() {
			int result = label != null ? label.hashCode() : 0;
			result = 31 * result + (value != null ? value.hashCode() : 0);
			result = 31 * result + (href != null ? href.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "HomePageItemSuggestion{" +
					"label='" + label + '\'' +
					", value='" + value + '\'' +
					", href='" + href + '\'' +
					'}';
		}
	}

	public DataObjectService getDataObjectService() {
		return dataObjectService;
	}

	public void setDataObjectService(DataObjectService dataObjectService) {
		this.dataObjectService = dataObjectService;
	}
}
