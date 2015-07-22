package com.ticketmaster.portal.webui.server.authentication;


import com.ticketmaster.portal.webui.server.common.DMIUtils;
import com.ticketmaster.portal.webui.server.session.ServerSession;
import com.ticketmaster.portal.webui.shared.utils.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class MagticomAuthenticationSuccessHandler extends MagticomMarkerResponseHandler implements
		AuthenticationSuccessHandler {
	protected final Log logger = LogFactory.getLog(getClass());
	public MagticomAuthenticationSuccessHandler() {
		setMarkerSnippet(SUCCESS_MARKER);
	}
	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Authentication authentication) throws IOException, ServletException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("responded with SUCCESS_MARKER");
			}
			Long languageId = Long.parseLong(httpServletRequest.getParameter("languageId"));
			Object details = authentication.getDetails();
			String remoteAddress = null;
			if (details instanceof WebAuthenticationDetails) {
				remoteAddress = ((WebAuthenticationDetails) details).getRemoteAddress();
			}
			System.out.println("remoteAddress = " + remoteAddress);
			GrantedAuthorityImpl authority = (GrantedAuthorityImpl) authentication.getAuthorities().iterator().next();
			Map<String, String> criteria = new LinkedHashMap<String, String>();
			criteria.put("user_id", authority.getUser().getUserId().toString());
			List<Map<?, ?>> dataPerms = DMIUtils.findRecordsByCriteria(null, "UserManagerDS", "getPermsByUserId",
					criteria);
			TreeSet<Long> mapPerms = new TreeSet<Long>();
			if (dataPerms != null && !dataPerms.isEmpty()) {
				for (Map<?, ?> map : dataPerms) {
					mapPerms.add(Long.parseLong(map.get("permission_id").toString()));
				}
			}
			if (mapPerms.isEmpty()) {
				throw new UsernameNotFoundException("Wrong username or password 4.");
			}
			ServerSession serverSession = new ServerSession();
			String ipAddress = remoteAddress;
			serverSession.setMachineIP(ipAddress);
			serverSession.setUser(authority.getUser());
			serverSession.setMapPerms(mapPerms);
			serverSession.setLanguageId(languageId);
			Map<?, ?> hostMap = DMIUtils.findRecordById(null, "UserManagerDS", "getDBHostname", 1L, "user_id");
			serverSession.setHostname(0L);
			if (hostMap != null && !hostMap.isEmpty()) {
				String hostname = hostMap.get("hostname").toString();
				if (hostname.equalsIgnoreCase(Constants.DB_HOSTNAME_PREFIX_LIVE)) {
					serverSession.setHostname(1L);
				}
			}
			criteria.clear();
			criteria.put("user_id", authority.getUser().getUserId().toString());
			List<Map<?, ?>> dataPartyId = DMIUtils.findRecordsByCriteria(null, "UserManagerDS", "getUserParties",
					criteria);
			TreeSet<Long> userContracts = new TreeSet<Long>();
			userContracts.add(-8888888L);
			userContracts.add(-9999999L);
			if (dataPartyId != null && !dataPartyId.isEmpty()) {
				for (Map<?, ?> map : dataPartyId) {
					userContracts.add(Long.parseLong(map.get("contract_id").toString()));
				}
			}
			serverSession.setUserContracts(userContracts);
			HttpSession session = httpServletRequest.getSession(true);
			session.setAttribute(ServerSession.SESSION_ATTRIBUTE_NAME, serverSession);
			handle(httpServletRequest, httpServletResponse, authentication);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("Error While Initialize Session. Please Try Again Later");
		}
	}
}