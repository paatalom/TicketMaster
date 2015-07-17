package com.ticketmaster.portal.webui.server.dmi;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.magti.billing.ejb.beans.entity.common.ReportsGenerator;
import com.magti.billing.ejb.common.beans.UserContext;
import com.magti.billing.ejb.exception.CCareEJBException;
import com.ticketmaster.portal.webui.server.common.DMIUtils;
import com.ticketmaster.portal.webui.server.jndi.JNDIManager;
import com.ticketmaster.portal.webui.server.session.ServerSession;
import com.ticketmaster.portal.webui.shared.exception.PortalException;

public class DMIReport {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DSResponse createReportCriteria(DSRequest dsRequest, HttpServletRequest request) throws PortalException {
		try {
			ServerSession serverSession = (ServerSession) request.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}
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

	public DSResponse createReportsGenerator(DSRequest dsRequest, HttpServletRequest httpRequest)
			throws PortalException {
		try {
			Map<?, ?> values = dsRequest.getValues();
			ServerSession serverSession = (ServerSession) httpRequest.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}
			UserContext userContext = serverSession.getUserContext();

			Long paramId = DMIUtils.getRowValueLong(values.get("param_id").toString());
			Long userId = userContext.getUser().getUserId();
			Long contractId = DMIUtils.getRowValueLong(values.get("contract_id").toString());
			Long itemsCount = DMIUtils.getRowValueLong(values.get("items_count").toString());
			Long reportType = DMIUtils.getRowValueLong(values.get("report_type"));
			Long format = DMIUtils.getRowValueSt(values.get("format")).equals("XLS") ? 1L : 2L;
			String clientIp = userContext.getClientIP();
			SimpleDateFormat parser = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
			Timestamp startDate = new Timestamp(parser.parse(values.get("start_date").toString()).getTime());
			Timestamp endDate = new Timestamp(parser.parse(values.get("end_date").toString()).getTime());
			Long status = 0L;

			ReportsGenerator reportsGenerator = new ReportsGenerator();
			reportsGenerator.setParamId(paramId);
			reportsGenerator.setUserId(userId);
			reportsGenerator.setContractId(contractId);
			reportsGenerator.setItemsCount(itemsCount);
			reportsGenerator.setReportType(reportType);
			reportsGenerator.setClientIp(clientIp);
			reportsGenerator.setStartDate(startDate);
			reportsGenerator.setEndDate(endDate);
			reportsGenerator.setStatus(status);
			reportsGenerator.setExtType(format);
			JNDIManager.getInstance().getCommonFasade().createReportsGenerator(reportsGenerator);

			Map<String, Object> data = new TreeMap<String, Object>();
			data.put("id", 1);
			DSResponse dsResponse = new DSResponse();
			dsResponse.setData(data);
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

	public void downloadFile(final DSRequest dsRequest, final HttpServletRequest request) throws PortalException {
		try {
			ServerSession serverSession = (ServerSession) request.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}

			dsRequest.getRPCManager().doCustomResponse();
			final HttpServletResponse servletResponse = dsRequest.getRPCManager().getContext().response;

			Map<?, ?> values = dsRequest.getValues();
			String paramId = DMIUtils.getRowValueSt(values.get("param_id").toString());
			Long contractId = DMIUtils.getRowValueLong(values.get("contract_id").toString());
			String fullName = "mnt/report-box/External/" + contractId + "/" + paramId + ".zip";
			File file = new File(fullName);

			byte[] bytes = new byte[(int) file.length()];

			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytes);
			fileInputStream.close();

			servletResponse.setHeader("Pragma", "private");
			servletResponse.setHeader("Cache-Control", "private, must-revalidate");
			servletResponse.setContentType("application/zip");
			servletResponse.setHeader("Content-Disposition", "attachment; filename=" + contractId + ".zip");
			servletResponse.setContentLength(bytes.length);
			servletResponse.getOutputStream().write(bytes);
		} catch (final Exception e) {
			if (e instanceof CCareEJBException) {
				CCareEJBException ejbEx = (CCareEJBException) e;
				throw new PortalException(ejbEx.getErrorMessage(), ejbEx.getThrowable());
			}
			e.printStackTrace();
			throw new PortalException(e.getMessage());
		}
	}
}
