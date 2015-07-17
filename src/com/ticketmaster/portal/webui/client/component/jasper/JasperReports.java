package com.ticketmaster.portal.webui.client.component.jasper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;

public class JasperReports {

	private String reportName;

	public JasperReports(String reportName) {
		this.reportName = reportName;
	}

	public void openReport(final Record record) {
		try {
			DataSource dataSource = DataSource.get("UserManagerDS");
			DSRequest dsRequest = new DSRequest();
			dsRequest.setOperationId("pingDB");
			dsRequest.setWillHandleError(true);
			Criteria criteria = new Criteria();
			criteria.setAttribute("uID", "UID_" + HTMLPanel.createUniqueId());
			criteria.setAttribute("USER_NAME", "test_user");
			dataSource.invalidateCache();
			dataSource.fetchData(criteria, new DSCallback() {
				@Override
				public void execute(DSResponse response, Object rawData, DSRequest request) {

					Map<String, Object> params = getParametersMap(record);
					if (params != null) {
						StringBuilder sb = new StringBuilder();
						for (String k : params.keySet()) {
							if (params.get(k) != null) {
								String vx = URL.encodeQueryString(params.get(k).toString());
								if (sb.length() > 0) {
									sb.append("&");
								}
								sb.append(k).append("=").append(vx);
							}
						}
						Window.open(
								URL.encode(GWT.getHostPageBaseURL() + "JasperServlet?" + sb.toString()),
								"_blank",
								"height=700,width=900,location=no,titlebar=no,resizable=yes,toolbar=no,scrollbars=yes,menubar=no,directories=no,status=no");
					} else {
						SC.warn("Wrong Jasper Parameters");
					}
				}
			}, dsRequest);
		} catch (Exception e) {
			SC.say(e.toString());
		}

	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getParametersMap(Record rec) {
		Map<String, Object> params = null;

		try {
			if (reportName.equals("allContract")) {
				int contractTypeId = rec.getAttributeAsInt("contract_type_id");
				String partyId = rec.getAttribute("party_id");

				params = new HashMap<String, Object>();
				params.put("party_id", partyId);
				switch (contractTypeId) {
				case 1: // Magti MONO+
				case 2: { // Bali
					params.put("ReportName", "allContract");
					params.put("sim", rec.getAttribute("sim"));
					break;
				}
				case 6: { // Magti-Fix
					params.put("ReportName", "MagtifixContract");
					params.put("esn", rec.getAttribute("esn"));
					break;
				}
				case 12: { // CDMA EvDO
					params.put("ReportName", "ContractEvdo");
					params.put("sim", rec.getAttribute("sim"));
					params.put("esn", rec.getAttribute("esn"));
					break;
				}
				case 14: { // Bani
					params.put("ReportName", "ContractBaniImei");
					params.put("sim", rec.getAttribute("sim"));
					break;
				}
				// case 31: { // MagtiSat Contract Type
				// params.put("ReportName", "allContract");
				// break;
				// }
				// case 32: { // Post Paid MagtiFix
				// params.put("ReportName", "allContract");
				// break;
				// }
				default:
					params = null;
					break;
				}
			} else if (reportName.equals("SubscriberBill")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
				// params.put("subscriber_id", "273546025");
				// params.put("party_id", "159840097");
			} else if (reportName.equals("DetailLimited")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
				// params.put("subscriber_id", "273546025");
				// params.put("party_id", "159840097");
				params.put("start_date",
						DateTimeFormat.getFormat("yyMMdd00000000").format((Date) params.get("start_date")));
				params.put("end_date", DateTimeFormat.getFormat("yyMMdd99999999").format((Date) params.get("end_date")));
			} else if (reportName.equals("DetailFull")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
				// params.put("subscriber_id", "273870221");
				// params.put("party_id", "141932700");
				if (params.get("immediate") != null)
					if (params.get("immediate").equals(true))
						params.put("immediate", 1);
					else
						params.put("immediate", 0);
				params.put("start_date",
						DateTimeFormat.getFormat("yyMMdd00000000").format((Date) params.get("start_date")));
				params.put("end_date", DateTimeFormat.getFormat("yyMMdd99999999").format((Date) params.get("end_date")));
			} else if (reportName.equals("AccountDetail")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
				params.put("date_range", DateTimeFormat.getFormat("dd.MM.yyyy").format((Date) params.get("start_date"))
						+ " - " + DateTimeFormat.getFormat("dd.MM.yyyy").format((Date) params.get("end_date")));
				params.put("start_tran_id",
						DateTimeFormat.getFormat("yyMMdd00000000").format((Date) params.get("start_date")));
				params.remove("start_date");
				params.put("end_tran_id",
						DateTimeFormat.getFormat("yyMMdd99999999").format((Date) params.get("end_date")));
				params.remove("end_date");
			} else if (reportName.equals("AccountServiceCharges")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
				params.put("date_range", DateTimeFormat.getFormat("dd.MM.yyyy").format((Date) params.get("start_date"))
						+ " - " + DateTimeFormat.getFormat("dd.MM.yyyy").format((Date) params.get("end_date")));
				params.put("start_tran_id", DateTimeFormat.getFormat("yyMMdd").format((Date) params.get("start_date")));
				params.remove("start_date");
				params.put("end_tran_id", DateTimeFormat.getFormat("yyMMdd").format((Date) params.get("end_date")));
				params.remove("end_date");
			} else if (reportName.equals("ContractBill")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
				// params.put("contract_id", 23515291L);
			} else if (reportName.equals("SubscribersBill")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
				// params.put("party_id", "159840097");
			} else if (reportName.equals("ChargeTablePivot")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
				// params.put("contract_id", 23515291L);
			} else if (reportName.equals("ChargeTableAccPivot")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
				// params.put("contract_id", 23515291L);
			} else if (reportName.equals("SubscriberStop")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
			} else if (reportName.equals("TransactionsDbl")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
			} else if (reportName.equals("ChangeOwnership")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
			} else if (reportName.equals("HSPAModemContract")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
			} else if (reportName.equals("LimitExceeded")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
			} else if (reportName.equals("CheckList")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
			} else if (reportName.equals("CorpAccountsBalance")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
			} else if (reportName.equals("PrivAccountsBalance")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
			} else if (reportName.equals("CorpTotals")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
			}
			// ოთარისთვის სატესტოდ >>>>>>>>>>>>>>>>>>>
			else if (reportName.equals("ContractBill_1")) {
				params = rec.toMap();
				params.put("ReportName", reportName);
			}
			// ოთარისთვის სატესტოდ <<<<<<<<<<<<<<<<<<<
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}

		return params;
	}
}
