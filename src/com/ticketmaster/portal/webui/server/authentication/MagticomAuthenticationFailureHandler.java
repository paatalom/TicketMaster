package com.ticketmaster.portal.webui.server.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MagticomAuthenticationFailureHandler extends MagticomMarkerResponseHandler implements
		AuthenticationFailureHandler {
	protected final Log logger = LogFactory.getLog(getClass());
	public MagticomAuthenticationFailureHandler() {
		setMarkerSnippet(LOGIN_REQUIRED_MARKER);
	}
	@Override
	public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			AuthenticationException e) throws IOException, ServletException {
		if (logger.isDebugEnabled()) {
			logger.debug("responded with LOGIN_REQUIRED_MARKER");
		}
		System.out.println("MagticomAuthenticationFailureHandler. onAuthenticationFailure ");
		handle(httpServletRequest, httpServletResponse, e);
	}
}