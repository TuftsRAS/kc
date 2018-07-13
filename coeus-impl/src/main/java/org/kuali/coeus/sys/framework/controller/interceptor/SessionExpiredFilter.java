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
import org.kuali.kra.infrastructure.KeyConstants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpiredFilter implements Filter {
    private static final Logger LOG = LogManager.getLogger(SessionExpiredFilter.class);
    
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hrequest = (HttpServletRequest) request;
        if (hrequest.getRequestedSessionId() != null
                && hrequest.isRequestedSessionIdValid() == false) {
            hrequest.getSession().setAttribute(KeyConstants.SESSION_EXPIRED_IND, new Boolean(true)); 
        } else {
        	if (hrequest.getSession() != null) {
        		hrequest.getSession().removeAttribute(KeyConstants.SESSION_EXPIRED_IND);
        	}
        }
          
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("Initialized");
    }

}
