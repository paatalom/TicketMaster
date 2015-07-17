package com.ticketmaster.portal.webui.client.utils;

import java.util.Date;
import java.util.Map;

public class CommonSingleton {

	private static CommonSingleton instance;
	private Long languageId;
	private String simPrefix = "8999597280";

	@SuppressWarnings("rawtypes")
	private Map mapPerms;

	@SuppressWarnings("rawtypes")
	public Map getMapPerms() {
		return mapPerms;
	}

	@SuppressWarnings("rawtypes")
	public void setMapPerms(Map mapPerms) {
		this.mapPerms = mapPerms;
	}

	public static CommonSingleton getInstance() {
		if (instance == null) {
			instance = new CommonSingleton();
		}
		return instance;
	}

	public CommonSingleton() {
	}

	public String getUnixTimeStamp() {
		Date date = new Date();
		return date.getTime() + "";
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public String getSimPrefix() {
		return simPrefix;
	}

	public boolean hasPermission(String permissionId) {
		if (mapPerms == null || mapPerms.isEmpty()) {
			return false;
		}
		return mapPerms.containsKey(permissionId);
	}

	public double round(double value, int decimalPlace) {
		double power_of_ten = 1;
		// floating point arithmetic can be very tricky.
		// that's why I introduce a "fudge factor"
		double fudge_factor = 0.05;
		while (decimalPlace-- > 0) {
			power_of_ten *= 10.0d;
			fudge_factor /= 10.0d;
		}
		double result = Math.round((value + fudge_factor) * power_of_ten) / power_of_ten;
		return result;
	}
}