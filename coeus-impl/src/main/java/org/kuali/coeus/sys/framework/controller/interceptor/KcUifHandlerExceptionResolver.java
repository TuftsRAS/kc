/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.controller.interceptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A KC specific exception handler that delegates to a rice krad exception handler.
 */
//not exactly sure how to do this with autowiring since exception handlers are special
public class KcUifHandlerExceptionResolver implements org.springframework.web.servlet.HandlerExceptionResolver, Ordered {
	private static final Logger LOG = LogManager.getLogger(KcUifHandlerExceptionResolver.class);
	HandlerExceptionResolver innerHandler;

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    	UserSession requestSession = (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
		if (System.identityHashCode(GlobalVariables.getUserSession()) != System.identityHashCode(requestSession)) {
    		LOG.error("User Session Difference Detected. GlobalVariables session = " + GlobalVariables.getUserSession() + ", request session = " + requestSession);
    	}
    	GlobalVariables.setUserSession(requestSession);
    	
        //Avoids NPE in rice incident handler
        if (handler == null) {
            return innerHandler.resolveException(request, response, NullHandler.INSTANCE, ex);
        }

        return innerHandler.resolveException(request, response, handler, ex);
    }
    
    public void setInnerHandler(HandlerExceptionResolver innerHandler) {
        this.innerHandler = innerHandler;
    }

    private static final class NullHandler{
        private static final NullHandler INSTANCE = new NullHandler();
    }
}
