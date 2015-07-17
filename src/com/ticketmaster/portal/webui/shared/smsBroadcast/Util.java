package com.ticketmaster.portal.webui.shared.smsBroadcast;

/**
 * Created by vano on 3/12/15.
 */
public class Util {

	public static boolean isValidNumber(String phone){
		return phone.matches("^[57][0-9]{8}$");
	}

	public static int countSMS(String v){
		if(v == null) v = "";

		int t = isASCII(v) ? 146 : 56;
		int sms_count = v.length() / t + (v.length() > 0 ? 1 : 0);
		return sms_count;
	}

	public static boolean isASCII(String str) {
		return str.matches("^[\\x00-\\x7F]*$");
	}

	public static boolean isValidFilter(Integer region, Integer district, Integer type, Integer phoneType, Integer charge, Integer gender){
		return true;
//		return (region != null && region != 0 ||
//				district != null && district != 0 ||
//				type != null && type != 0 ||
//				phoneType != null && phoneType != 0 ||
//				charge != null && charge != 0 ||
//				gender != null && gender != 0);
	}

}
