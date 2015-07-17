package com.ticketmaster.portal.webui.server.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ServerUtils {
	private static ServerUtils instance = null;

	public static ServerUtils getInstance() {
		if (instance == null) {
			instance = new ServerUtils();
		}
		return instance;
	}

	public Timestamp truncDate(Timestamp date) {
		if (date == null) {
			return null;
		}
		return truncDate(new Date(date.getTime()));
	}

	public Timestamp truncDate(Date date) {
		if (date == null) {
			return null;
		}
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(date.getTime());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return new Timestamp(calendar.getTimeInMillis());
	}
}
