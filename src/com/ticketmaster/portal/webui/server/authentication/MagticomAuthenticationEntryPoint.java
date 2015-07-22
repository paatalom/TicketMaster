package com.ticketmaster.portal.webui.server.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MagticomAuthenticationEntryPoint extends MagticomMarkerResponseHandler implements AuthenticationEntryPoint {
	public MagticomAuthenticationEntryPoint() {
		setMarkerSnippet(LOGIN_REQUIRED_MARKER);
	}
	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			AuthenticationException e) throws IOException, ServletException {
		handle(httpServletRequest, httpServletResponse, e);		
	}
}