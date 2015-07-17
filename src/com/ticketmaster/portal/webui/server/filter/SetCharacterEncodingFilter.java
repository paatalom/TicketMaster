package com.ticketmaster.portal.webui.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SetCharacterEncodingFilter implements Filter {

	private String encoding;

	public void init(FilterConfig config) throws ServletException {
		encoding = config.getInitParameter("requestEncoding");

		if (encoding == null) {
			encoding = "UTF-8";
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException,
			ServletException {
		try {
			if (null == request.getCharacterEncoding()) {
				request.setCharacterEncoding(encoding);
			}
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");

			next.doFilter(request, response);
		} catch (Exception e) {
		}
	}

	public void destroy() {
	}

}
