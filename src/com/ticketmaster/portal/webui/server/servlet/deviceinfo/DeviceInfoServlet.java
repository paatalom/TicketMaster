package com.ticketmaster.portal.webui.server.servlet.deviceinfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeviceInfoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static String DEVICE_INFO_URL = "http://192.168.19.242:8000/webcmd/getsub?msisdn=%s";
	private static String DEVICE_IMAGE_URL = "http://192.168.19.242:8000/webcmd/devicepic?brand=%s&model=%s";

	public DeviceInfoServlet() {
		super();
	}

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Enumeration<String> paramNames = request.getParameterNames();
			String msisdn = null;
			String brand = null;
			String model = null;
			while (paramNames.hasMoreElements()) {
				String paramName = (String) paramNames.nextElement();
				if (paramName.equals("msisdn")) {
					msisdn = request.getParameter(paramName);
				} else if (paramName.equals("brand")) {
					brand = request.getParameter(paramName);
				} else if (paramName.equals("model")) {
					model = request.getParameter(paramName);
				}
			}

			URL url = null;
			if (msisdn != null) {
				url = new URL(String.format(DEVICE_INFO_URL, msisdn));
			} else if (brand != null && model != null) {
				url = new URL(String.format(DEVICE_IMAGE_URL, brand, model));
				response.setContentType("image/JPEG");
			}

			URLConnection connection;
			connection = url.openConnection();
			InputStream in = connection.getInputStream();

			OutputStream out = response.getOutputStream();
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = in.read(buf)) >= 0) {
				out.write(buf, 0, count);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
