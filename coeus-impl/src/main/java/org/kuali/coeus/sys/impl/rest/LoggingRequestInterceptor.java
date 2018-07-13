/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.impl.rest;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	private Logger LOG = LogManager.getLogger(LoggingRequestInterceptor.class);
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution) throws IOException {
		traceRequest(request, body);
		
		ClientHttpResponse response = execution.execute(request, body);
		
		traceResponse(request, response);
		
		return response;
		
	}
		
	protected void traceRequest(HttpRequest request, byte[] body) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("URI : " + request.getURI());
			LOG.debug("Method : " + request.getMethod());
			LOG.debug("Headers : " + request.getHeaders().toString());
			LOG.debug("Body : " + new String(body));
		}
	}
	
	protected void traceResponse(HttpRequest request, ClientHttpResponse response) {
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Response status code : " + response.getStatusCode());
				LOG.debug("Response status text : " + response.getStatusText());
			}
		} catch (IOException e) {
			LOG.warn("Error performing debug logging of response", e);
		}
	}

}
