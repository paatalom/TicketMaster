package com.ticketmaster.portal.webui.server.utils;

import java.util.Map;

import com.magti.billing.ejb.beans.entity.umanager.User;
import com.magti.billing.ejb.common.beans.UserContext;

public class ServerSingleton {
	private static ServerSingleton instance;

	public static ServerSingleton getInstance() {
		if (instance == null) {
			instance = new ServerSingleton();
		}
		return instance;
	}

	public UserContext getUserContext(Map<?, ?> map) {
		Object oMachineIP = map.get("machine_ip");
		Object oUserId = map.get("user_id");
		Object oOfficeId = map.get("office_id");
		Object oUserName = map.get("user_name");

		UserContext userContext = new UserContext();
		userContext.setClientIP(oMachineIP == null ? "NONE" : oMachineIP.toString());
		userContext.setSessionId("123123123123");
		userContext.setLangiageId(1L);

		User user = new User();
		user.setUserId(oUserId == null ? 2567L : Long.parseLong(oUserId.toString()));
		user.setOfficeId(oOfficeId == null ? 0L : Long.parseLong(oOfficeId.toString()));
		user.setUserName(oUserName == null ? "JBOSS" : oUserName.toString());
		userContext.setUser(user);

		return userContext;
	}
}
