package com.ticketmaster.portal.webui.server.authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface InvalidSessionHandler {
	void sessionInvalidated(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException;
}