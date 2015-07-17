package com.ticketmaster.portal.webui.server.dmi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.rpc.RPCManager;
import com.isomorphic.sql.SQLConnectionManager;
import com.isomorphic.velocity.Velocity;
import com.magti.billing.ejb.exception.CCareEJBException;
import com.ticketmaster.portal.webui.server.common.DMIUtils;
import com.ticketmaster.portal.webui.server.session.ServerSession;
import com.ticketmaster.portal.webui.shared.exception.PortalException;

public class DMICharts {
	private class MetaData {
		int columnIndex;
		String columnName;
		@SuppressWarnings("unused")
		int columnType;

		public MetaData(int columnIndex, String columnName, int columnType) {
			super();
			this.columnIndex = columnIndex;
			this.columnName = columnName;
			this.columnType = columnType;
		}	

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void concatMap(Map newCriteria, Map data) {
		Set keys = data.keySet();
		for (Object key : keys) {
			newCriteria.put(key, data.get(key));
		}
	}

	public DSResponse execChart(DSRequest dsRequest, HttpServletRequest httpRequest, RPCManager rpcManager)
			throws PortalException {
		try {
			ServerSession serverSession = (ServerSession) httpRequest.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}

			Map<?, ?> criteria = dsRequest.getCriteria();
			long id = Long.valueOf(criteria.get("id").toString());
			Map<?, ?> _tmp_data = DMIUtils.findRecordById(rpcManager, dsRequest.getDataSourceName(), null, id, "id");
			String sql = _tmp_data.get("sql_template").toString();

			Map<?, ?> newCriteria = Velocity.getStandardContextMap(dsRequest);
			concatMap(newCriteria, Velocity.getServletContextMap(httpRequest));
			concatMap(newCriteria, Velocity.getServletContextMap(rpcManager));
			sql = Velocity.evaluateAsString(sql, newCriteria, dsRequest.getOperationType(), dsRequest.getDataSource(),
					true);
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				conn = SQLConnectionManager.getConnection();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				List<MetaData> mdList = new ArrayList<MetaData>();

				for (int i = 0; i < columnCount; i++) {
					mdList.add(new MetaData(i + 1, rsmd.getColumnName(i + 1), rsmd.getColumnType(i + 1)));
				}
				while (rs.next()) {
					Map<String, Object> resultMap = new HashMap<String, Object>();
					result.add(resultMap);
					for (MetaData metaData : mdList) {
						Object obj = rs.getObject(metaData.columnIndex);
						if (obj != null)
							resultMap.put(metaData.columnName.toLowerCase(), obj);
					}

				}
			} finally {
				try {
					stmt.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					rs.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					SQLConnectionManager.freeConnection(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					conn.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			DSResponse dsResponse = new DSResponse();

			dsResponse.setData(result);
			return dsResponse;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof CCareEJBException) {
				CCareEJBException ejbEx = (CCareEJBException) e;
				throw new PortalException(ejbEx.getErrorMessage(), ejbEx.getThrowable());
			}
			if (e instanceof PortalException) {
				throw (PortalException) e;
			}
			throw new PortalException(e.getMessage());
		}
	}
}
