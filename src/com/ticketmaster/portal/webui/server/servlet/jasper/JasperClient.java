package com.ticketmaster.portal.webui.server.servlet.jasper;

import java.io.File;

public class JasperClient {
	private final static String serverUrl = "http://192.9.200.104:8080/jasperserver";
	private final static String serverUser = "jasperadmin";
	private final static String serverPassword = "jasperadmin";
	private static File outPutDir;

	public static void main(String[] argv) {
		outPutDir = new File("D:/SyStem/Desktop/jasperTests");
		try {
			Report report = new Report();
			report.setUrl("/reports/allBill");
			report.setOutputFolder(outPutDir.getAbsolutePath());
			report.addParameter("language", "ge");
			report.addParameter("bill_id", "1212");
			report.addParameter("subscriber_id", "273223820");
			report.addParameter("party_id", "141932700");
			JasperserverRestClient client = JasperserverRestClient.getInstance(serverUrl, serverUser, serverPassword);
			// File reportFile = client.getReportAsFile(report);
			client.getReportAsFile(report);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
