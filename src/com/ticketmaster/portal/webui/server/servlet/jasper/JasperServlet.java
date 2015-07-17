package com.ticketmaster.portal.webui.server.servlet.jasper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ticketmaster.portal.webui.server.session.ServerSession;

/**
 * Servlet implementation class JasperServlet
 */
public class JasperServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static String serverUrl = "http://192.9.200.104:8080/jasperserver";
	private final static String serverUser = "jasperadmin";
	private final static String serverPassword = "jasperadmin";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JasperServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			ServerSession serverSession = (ServerSession) request.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				response.sendError(401, "Unauthorized");
				return;
			}
			TreeSet<Long> userContracts = serverSession.getUserContracts();
			if (userContracts == null || userContracts.isEmpty()) {
				response.sendError(401, "Contracts is not defined for this user");
				return;
			}
			Enumeration<String> paramNames = request.getParameterNames();

			if (paramNames.hasMoreElements()) {
				Map<String, String> paramsMap = new HashMap<String, String>();
				while (paramNames.hasMoreElements()) {
					String paramName = paramNames.nextElement();
					paramsMap.put(paramName, request.getParameter(paramName));
					System.out.println("GET - name:" + paramName + " - value:" + request.getParameter(paramName));
				}

				Long paramContractId = Long.parseLong(paramsMap.get("contract_id"));

				Long contractId = null;
				for (Long contractIdItem : userContracts) {
					if (contractIdItem.equals(paramContractId)) {
						contractId = paramContractId;
						break;
					}
				}
				if (contractId == null) {
					response.sendError(401, "Contract is not defined for this user");
					return;
				}

				request.getSession().setAttribute("params_map", paramsMap);
				response.sendRedirect("JasperServlet");
			} else {
				Map<String, String> paramsMap = (Map<String, String>) request.getSession().getAttribute("params_map");
				request.getSession().removeAttribute("params_map");

				if (paramsMap != null) {
					Report report = new Report();
					report.setUrl("/reports/");
					String format = "PDF";

					for (String paramName : paramsMap.keySet()) {
						String paramValue = paramsMap.get(paramName);

						if (paramName.equals("ReportName")) {
							report.setUrl(report.getUrl() + paramValue);
						} else if (paramName.equals("format")) {
							format = paramValue;
							report.setFormat(format);
							report.addParameter(paramName, format);
						} else {
							report.addParameter(paramName, paramValue);
						}
						System.out.println("POST - name:" + paramName + " - value:" + paramValue);
					}

					JasperserverRestClient client = JasperserverRestClient.getInstance(serverUrl, serverUser,
							serverPassword);
					if (format.equals("XLS"))
						response.setContentType("application/vnd.ms-excel");
					else
						response.setContentType("application/pdf");
					response.getOutputStream().write(client.getReportAsByte(report));
				} else {
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					out.append("<br /><br /><br /><br /><br /><br /><br /><br /><br /><br />"
							+ "<center><span style=\"color: red;\">Wrong Jasper Parameters</span></center>");
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		//
		//
		// Enumeration<?> e = request.getSession().getAttributeNames();
		// while (e.hasMoreElements()) {
		// // String name = (String) e.nextElement();
		// // String value =
		// // request.getSession().getAttribute(name).toString();
		// // ServerSession ss = (ServerSession)
		// // request.getSession().getAttribute(name);
		// // System.out.println("user_id = " + ss.getUser().getUserId());
		// // System.out.println(name + " = " + value);
		// authorized = true;
		// break;
		// }
		// System.out.println(authorized);
		// if (authorized) {
		// } else {
		//
		// }
	}
}
