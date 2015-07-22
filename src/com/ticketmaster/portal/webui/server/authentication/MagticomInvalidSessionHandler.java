package com.ticketmaster.portal.webui.server.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MagticomInvalidSessionHandler extends MagticomMarkerResponseHandler implements InvalidSessionHandler {
	protected final Log logger = LogFactory.getLog(getClass());
	public MagticomInvalidSessionHandler() {
		setMarkerSnippet(LOGIN_REQUIRED_MARKER);
	}
	@Override
	public void sessionInvalidated(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		if (logger.isDebugEnabled()) {
			logger.debug("responded with LOGIN_REQUIRED_MARKER");
		}
		System.out.println("MagticomInvalidSessionHandler. sessionInvalidated.");
		handle(request, response);
	}
}