package com.ticketmaster.portal.webui.server.session;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.magti.billing.ejb.beans.entity.umanager.User;
import com.magti.billing.ejb.common.beans.UserContext;
import com.ticketmaster.portal.webui.client.utils.ClientMapUtil;
import com.ticketmaster.portal.webui.shared.entity.umanager.UserUI;

public class ServerSession implements Serializable {

	public static final String SESSION_ATTRIBUTE_NAME = "MagticomPortalSessionObject";

	@SuppressWarnings("rawtypes")
	public void loadFromMap(Map dataMap) throws Exception {
		try {
			machineIP = ClientMapUtil.convertString(dataMap, "machineIP");
			hostname = ClientMapUtil.convertLong(dataMap, "hostname");
			phone = ClientMapUtil.convertString(dataMap, "phone");

			Object obj = null;
			obj = dataMap.get("user");
			user = new UserUI();
			if (obj != null && obj instanceof Map) {
				user.loadFromObjMap((Map) obj);
			}
			obj = dataMap.get("mapPerms");
			mapPerms = new TreeSet<Long>();
			if (obj != null) {
				List list = (List) obj;
				if (!list.isEmpty()) {
					for (Object object : list) {
						if (object != null) {
							mapPerms.add(ClientMapUtil.getLongValue(object));
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private static final long serialVersionUID = -1814238115292961188L;
	private String machineIP;
	private UserUI user;
	private TreeSet<Long> mapPerms;
	private Long hostname;
	private String sessionId;
	private String phone;
	private TreeSet<Long> userContracts;
	private Long languageId;

	public static Long CORP_CONTRACT_EDIT_BUNDLE_MANAGEMENT = 100000L;
	public static Long CORP_CONTRACT_EDIT_BUNDLE_SHAREABLE = 100001L;
	public static Long VIEW_TIMESTEN_BALANCES = 2214L;
	public static Long ADMIN_MENU = 100003L;
	public static Long PERMISSION_CAN_SYNCRONYZE_SUBSCRIBER = 276050L;
	public static Long SUBSCRIBER_TERMINATION = 2213L;
	public static Long VIEW_PARTY_INFO = 2202L;
	public static Long MODIFY_PARTY_INFO = 2203L;
	public static Long VIEW_DETAIL_REPORT = 2215L;
	public static Long RECREATE_SUBSCRIBER = 2218L;
	public static Long MANAGE_SERVICE = 2204L;
	public static Long MODIFY_PAYMENTS = 2210L;
	public static Long MODIFY_OFFICE_PAYMENTS = 296056L;
	public static Long MODIFY_OWN_PAYMENTS = 20050L;
	public static Long CORPORATE_BILLING_USER = 30000L;
	public static Long CORPORATE_BILLING_ADMIN = 30001L;
	public static Long CCARE_BATCH_OPERATIONS = 296050L;

	public ServerSession() {
	}

	public String getMachineIP() {
		return machineIP;
	}

	public void setMachineIP(String machineIP) {
		this.machineIP = machineIP;
	}

	public UserUI getUser() {
		return user;
	}

	public void setUser(UserUI user) {
		this.user = user;
	}

	public void setMapPerms(TreeSet<Long> mapPerms) {
		this.mapPerms = mapPerms;
	}

	public TreeSet<Long> getMapPerms() {
		return mapPerms;
	}

	public boolean hasPermission(Long permissionId) {
		if (mapPerms == null || mapPerms.isEmpty()) {
			return false;
		}
		return mapPerms.contains(permissionId);
	}

	public TreeMap<String, String> getMap() {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("machine_ip", machineIP);
		treeMap.put("user_name", user.getUserName());
		treeMap.put("office_id", user.getOfficeId().toString());
		treeMap.put("user_id", user.getUserId().toString());
		if (phone != null) {
			treeMap.put("phone", phone.toString());
		}
		return treeMap;
	}

	private UserContext userContext;

	public UserContext getUserContext() {
		if (user == null) {
			return null;
		}
		if (userContext != null) {
			return userContext;
		}
		UserContext userContext = new UserContext();
		userContext.setClientIP(machineIP);
		userContext.setLangiageId(languageId);
		userContext.setSessionId(sessionId);
		User userL = new User();
		userL.setOfficeId(user.getOfficeId());
		userL.setUserId(user.getUserId());
		userL.setUserName(user.getUserName());
		userL.setUserPwd(user.getUserPwd().getBytes());
		userL.setUserStatus(user.getUserStatus());
		userContext.setUser(userL);
		return userContext;
	}

	public Long getHostname() {
		return hostname;
	}

	public void setHostname(Long hostname) {
		this.hostname = hostname;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public TreeSet<Long> getUserContracts() {
		return userContracts;
	}

	public void setUserContracts(TreeSet<Long> userContracts) {
		this.userContracts = userContracts;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}
}
