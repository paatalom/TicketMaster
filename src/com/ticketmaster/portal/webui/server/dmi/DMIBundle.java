package com.ticketmaster.portal.webui.server.dmi;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.magti.billing.ejb.common.beans.UserContext;
import com.magti.billing.ejb.exception.CCareEJBException;
import com.ticketmaster.portal.webui.server.jndi.JNDIManager;
import com.ticketmaster.portal.webui.server.session.ServerSession;
import com.ticketmaster.portal.webui.shared.exception.PortalException;

public class DMIBundle {

	public DSResponse assignBundleToSubs(HttpServletRequest httpRequest, DSRequest dsRequest) throws PortalException {
		try {
			ServerSession serverSession = (ServerSession) httpRequest.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}
			UserContext userContext = serverSession.getUserContext();

			Map<?, ?> values = dsRequest.getValues();
			Long subscriber_id = new Long(values.get("subscriber_id").toString());
			Long bundle_id = new Long(values.get("bundle_id").toString());
			Long bundle_type = new Long(values.get("bundle_type").toString());

			JNDIManager.getInstance().getCorpContractFascade()
					.assignBundleToSubs(bundle_type, bundle_id, subscriber_id, userContext);

			DSResponse dsResponse = new DSResponse();
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put("id", 1);
			dsResponse.setData(result);
			return dsResponse;
		} catch (Exception e) {
			if (e instanceof CCareEJBException) {
				CCareEJBException ejbEx = (CCareEJBException) e;
				throw new PortalException(ejbEx.getErrorMessage(), ejbEx.getThrowable());
			}
			e.printStackTrace();
			throw new PortalException(e.getMessage());
		}
	}

	public DSResponse removeBundleFromSubs(HttpServletRequest httpRequest, DSRequest dsRequest) throws PortalException {
		try {
			Map<?, ?> values = dsRequest.getValues();

			ServerSession serverSession = (ServerSession) httpRequest.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}

			UserContext userContext = serverSession.getUserContext();

			Long subscriber_id = new Long(values.get("subscriber_id").toString());
			Long bundle_id = new Long(values.get("bundle_id").toString());

			JNDIManager.getInstance().getCorpContractFascade()
					.removeSubsBundleFromSubs(subscriber_id, bundle_id, userContext);

			DSResponse dsResponse = new DSResponse();
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put("id", 1);
			dsResponse.setData(result);
			return dsResponse;
		} catch (Exception e) {
			if (e instanceof CCareEJBException) {
				CCareEJBException ejbEx = (CCareEJBException) e;
				throw new PortalException(ejbEx.getErrorMessage(), ejbEx.getThrowable());
			}
			e.printStackTrace();
			throw new PortalException(e.getMessage());
		}
	}
}
