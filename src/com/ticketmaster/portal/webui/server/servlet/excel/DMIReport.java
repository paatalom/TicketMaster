package com.ticketmaster.portal.webui.server.servlet.excel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.ticketmaster.portal.webui.shared.exception.PortalException;

public class DMIReport {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DSResponse createReportCriteria(DSRequest dsRequest, HttpServletRequest request) throws PortalException {
		try {
			Map<?, ?> values = dsRequest.getCriteria();
			String u_id = UUID.randomUUID().toString();
			request.getSession().setAttribute(u_id, values);
			DSResponse resp = new DSResponse();
			Map mp = new HashMap();
			mp.put("id", u_id);
			resp.setData(mp);
			return resp;
		} catch (Exception e) {
			throw new PortalException("Error Excel Generation : " + e.toString());
		}
	}
}
