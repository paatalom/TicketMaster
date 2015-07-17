package com.ticketmaster.portal.webui.server.servlet.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ticketmaster.portal.webui.server.session.ServerSession;
import com.ticketmaster.portal.webui.shared.utils.Constants;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.ticketmaster.portal.webui.server.common.DMIUtils;

public class ExcelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public ExcelServlet() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			ServerSession serverSession = (ServerSession) request.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				response.sendError(401, "Unauthorized");
				return;
			}

			String strReportUniqId = request.getParameter("reportuniqid");
			System.out.println(strReportUniqId);
			if (strReportUniqId == null || strReportUniqId.trim().equals("")) {
				throw new ServletException("Invalid Parameter");
			}
			Object pValue = request.getSession().getAttribute(strReportUniqId);
			request.getSession().removeAttribute(strReportUniqId);
			if (pValue == null) {
				throw new ServletException("Criteria Not Found For This Session");
			}
			Map criteria = null;
			try {
				criteria = (Map) pValue;
			} catch (Exception e) {
				throw new ServletException("Invalid Criteria Into Session");
			}
			System.out.println(pValue);

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Sample sheet");

			Map<String, String> mpHeaders = null;
			Map<String, String> mpCaptions = (Map<String, String>) criteria.get(Constants.REPORT_CAPTIONS);
			criteria.remove(Constants.REPORT_CAPTIONS);
			String _fields = criteria.get(Constants.REPORT_FIELDS).toString();
			criteria.remove(Constants.REPORT_FIELDS);
			String[] fields = _fields.trim().split(";");
			String ds_name = criteria.get(Constants.REPORT_DS).toString();
			criteria.remove(Constants.REPORT_DS);
			String ds_operation = criteria.get(Constants.REPORT_OPERATION).toString();
			criteria.remove(Constants.REPORT_OPERATION);
			if (criteria.get(Constants.REPORT_HEADERS) != null) {
				mpHeaders = (Map<String, String>) criteria.get(Constants.REPORT_HEADERS);
				criteria.remove(Constants.REPORT_HEADERS);
			}

			HSSFCellStyle style = workbook.createCellStyle();
			HSSFFont font = workbook.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			style.setFont(font);

			int rownum = 0;

			// Header
			if (mpHeaders != null) {
				rownum++;
				for (int i = 0; i < mpHeaders.size(); i++) {
					sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, fields.length - 1));
					HSSFRow h = sheet.createRow(rownum++);
					createHeaderCell(h.createCell(0), mpHeaders.get("" + i), style);
				}
				rownum++;
			}

			// Captions
			HSSFRow r = sheet.createRow(rownum++);
			for (int i = 0; i < fields.length; i++) {
				createCaptionCell(r.createCell(i), mpCaptions.get(fields[i]), style);
			}

			criteria.put("languageId", serverSession.getLanguageId());
			criteria.put("userContracts", serverSession.getUserContracts());

			List<Map<?, ?>> maps = DMIUtils.findRecordsByCriteria(null, ds_name, ds_operation, criteria);
			for (Map<?, ?> map : maps) {
				HSSFRow row = sheet.createRow(rownum++);
				int cellnum = 0;
				for (int i = 0; i < fields.length; i++) {
					Object obj = map.get(fields[i]);
					HSSFCell cell = row.createCell(cellnum++);
					generateCell(obj, cell);
				}
			}

			for (int colNum = 0; colNum < fields.length; colNum++)
				sheet.autoSizeColumn(colNum);

			OutputStream out = response.getOutputStream();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=Results.xls");
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateCell(Object obj, HSSFCell cell) {
		if (obj != null) {
			if (obj instanceof Date)
				cell.setCellValue((Date) obj);
			else if (obj instanceof Boolean)
				cell.setCellValue((Boolean) obj);
			else if (obj instanceof String)
				cell.setCellValue((String) obj);
			else if (obj instanceof Double)
				cell.setCellValue((Double) obj);
			else
				cell.setCellValue(obj.toString());
		}

	}

	private void createHeaderCell(HSSFCell c, String caption, HSSFCellStyle style) {
		c.setCellValue(caption);
		c.setCellStyle(style);
	}

	private void createCaptionCell(HSSFCell c, String caption, HSSFCellStyle style) {
		c.setCellValue(caption);
		c.setCellStyle(style);
	}
}
