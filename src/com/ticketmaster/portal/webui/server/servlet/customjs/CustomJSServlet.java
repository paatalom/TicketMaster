package com.ticketmaster.portal.webui.server.servlet.customjs;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ticketmaster.portal.webui.server.common.DMIUtils;

public class CustomJSServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6251149397142948543L;

	public CustomJSServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("text/javascript");
			List<Map<?, ?>> result = DMIUtils.findRecordsByCriteria(null, "CorpConfigDS", null, new TreeMap<>());
			String code=request.getParameter("code_type");
			Reader jscode_reader = (Reader) result.get(0).get(code);
			String jscode = DMIUtils.readAll(jscode_reader);
			response.getWriter().write(jscode.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
