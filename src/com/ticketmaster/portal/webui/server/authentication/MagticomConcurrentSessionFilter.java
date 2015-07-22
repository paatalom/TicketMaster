package com.ticketmaster.portal.webui.server.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class MagticomConcurrentSessionFilter extends GenericFilterBean {
	private SessionRegistry sessionRegistry;
	private InvalidSessionHandler invalidSessionHandler;
	private LogoutHandler[] handlers = new LogoutHandler[] { new SecurityContextLogoutHandler() };
	public void afterPropertiesSet() {
		Assert.notNull(sessionRegistry, "SessionRegistry required");
		Assert.notNull(invalidSessionHandler, "InvalidSessionHandler required");
	}
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		try {
			System.out.println("MagticomConcurrentSessionFilter. doFilter ");
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
//			RPCManager rpcManager = null;
//			try {
//				rpcManager = new RPCManager(request, response);
//			} catch (Exception e) {
//				System.out.println("Can't instantiate rpcManager ");
//			}
//			if (rpcManager != null) {
//				System.out.println("rpcManager = " + rpcManager.getStartedTimestamp());
//			}
			HttpSession session = request.getSession(false);
			if (session != null) {
				SessionInformation info = sessionRegistry.getSessionInformation(session.getId());
				if (info != null) {
					if (info.isExpired()) {
						doLogout(request, response);
						if (invalidSessionHandler != null)
							invalidSessionHandler.sessionInvalidated(request, response);
						else {
							response.getWriter().print(
									"This session has been expired (possibly due to multiple concurrent "
											+ "logins being attempted as the same user).");
							response.flushBuffer();
						}
						return;
					} else {
						info.refreshLastRequest();
					}
				}
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void doLogout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].logout(request, response, auth);
		}
	}
	public void setInvalidSessionHandler(InvalidSessionHandler invalidSessionHandler) {
		this.invalidSessionHandler = invalidSessionHandler;
	}
	public void setLogoutHandlers(LogoutHandler[] handlers) {
		Assert.notNull(handlers);
		this.handlers = handlers;
	}
	public void setSessionRegistry(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
}