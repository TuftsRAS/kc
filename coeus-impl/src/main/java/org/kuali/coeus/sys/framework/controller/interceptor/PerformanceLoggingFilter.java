/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.controller.interceptor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Part of KC's {@link FilterChain} that handles timing a {@link HttpServletRequest} and reporting on the state of
 * resource consumption. The following information gets logged from this {@link Filter} at the <b>INFO</b> log level.
 * <ul>
 *   <li>Request execution time</li>
 *   <li>Initial resource consumption</li>
 *   <li>Initial available resources</li>
 *   <li>Final resource consumption</li>
 *   <li>Final available resources</li>
 * </ul>
 */
public class PerformanceLoggingFilter implements Filter {
    private static final Logger LOG = LogManager.getLogger(PerformanceLoggingFilter.class);
    
    private static final String IGNORED_URLS_PARAM = "kc.monitoring.exclude.pattern";

	private Pattern urlExcludePattern;
    private ConfigurationService configurationService;

	@Override
	public void init(FilterConfig config) throws ServletException {
		if (getConfigurationService().getPropertyValueAsString(IGNORED_URLS_PARAM) != null) {
			urlExcludePattern = Pattern.compile(getConfigurationService().getPropertyValueAsString(IGNORED_URLS_PARAM));
		}
	}

    @Override
    public void destroy() {
		urlExcludePattern = null;
		configurationService = null;
	}

    /**
     * <p>Does the actual logging. The log4j.properties file already covers user and date/time logging for us, so the
     * only thing that gets logged in <b>INFO</b> here is the intended action.</p>
     * 
     * <p>Memory used for a request is calculated with {@link Runtime#freeMemory()} and 
     * {@link Runtime#totalMemory()}
     *
     * @see javax.servlet.Filter#doFilter(ServletRequest,ServletResponse,FilterChain)
     * @see Runtime#totalMemory()
     * @see Runtime#freeMemory()
     */
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
    	if (isRequestExcluded((HttpServletRequest) request)) {
			chain.doFilter(request, response);
    	} else {
	        final long start = System.currentTimeMillis();
	        chain.doFilter(request, response);
	
	        if (LOG.isInfoEnabled()) {
	            final long elapsed = System.currentTimeMillis() - start;
	            final long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

				LOG.info(((HttpServletRequest) request).getRequestURI() + " : " + elapsed + "ms, " + usedMemory + "B memory used");
	        }
    	}
    }

	private boolean isRequestExcluded(HttpServletRequest httpRequest) {
		return urlExcludePattern != null
				&& urlExcludePattern.matcher(
				httpRequest.getRequestURI()
						.substring(httpRequest.getContextPath().length())).matches();
	}

	public ConfigurationService getConfigurationService() {
		if (configurationService == null) {
			configurationService = KcServiceLocator.getService(ConfigurationService.class);
		}

		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
}
