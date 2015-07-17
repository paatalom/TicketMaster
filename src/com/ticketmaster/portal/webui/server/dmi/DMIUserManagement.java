package com.ticketmaster.portal.webui.server.dmi;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.rpc.RPCManager;
import com.magti.billing.ejb.common.beans.UserContext;
import com.magti.billing.ejb.exception.CCareEJBException;
import com.ticketmaster.portal.webui.server.jndi.JNDIManager;
import com.ticketmaster.portal.webui.server.session.ServerSession;
import com.ticketmaster.portal.webui.shared.exception.PortalException;

public class DMIUserManagement {

	public DSResponse getSession(DSRequest dsRequest, HttpSession httpSession, HttpServletRequest httpServletRequest,
			DSResponse dsResponse) throws PortalException {
		try {
			dsResponse.setDropExtraFields(false);
			dsResponse.setStatus(0);
			ServerSession serverSession = (ServerSession) httpSession
					.getAttribute(ServerSession.SESSION_ATTRIBUTE_NAME);
			TreeSet<Long> mapPerms = serverSession.getMapPerms();
			TreeMap<String, String> map = new TreeMap<String, String>();
			for (Long perm : mapPerms) {
				map.put(perm.toString(), perm.toString());
			}
			dsResponse.setData(map);
			return dsResponse;
		} catch (Exception e) {
			dsResponse.setStatus(-100005);
			e.printStackTrace();
			return dsResponse;
		}
	}

	public DSResponse invalidateSession(DSRequest dsRequest, HttpSession httpSession,
			HttpServletRequest httpServletRequest, DSResponse dsResponse) throws PortalException {
		try {
			dsResponse.setDropExtraFields(false);
			dsResponse.setStatus(0);
			httpSession.invalidate();
			return dsResponse;
		} catch (Exception e) {
			dsResponse.setStatus(-100005);
			e.printStackTrace();
			return dsResponse;
		}
	}

	public DSResponse changeUserPassword(DSRequest dsRequest, HttpServletRequest httpRequest, RPCManager rpcManager)
			throws PortalException {
		try {
			ServerSession serverSession = (ServerSession) httpRequest.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}

			UserContext userContext = serverSession.getUserContext();
			Map<?, ?> values = dsRequest.getValues();

			String currentPassword = values.get("current_password").toString();
			String newPassword = values.get("new_password").toString();

			JNDIManager.getInstance().getUserManagerBean()
					.changeUserPassword(userContext, currentPassword, newPassword);
			DSResponse dsResponse = new DSResponse();
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put("user_id", 1);
			dsResponse.setData(result);
			return dsResponse;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof CCareEJBException) {
				CCareEJBException ejbEx = (CCareEJBException) e;
				throw new PortalException(ejbEx.getErrorMessage(), ejbEx.getThrowable());
			}
			throw new PortalException(e.getMessage());
		}
	}
}
