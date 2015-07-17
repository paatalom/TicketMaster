package com.ticketmaster.portal.webui.shared.entity.umanager;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ticketmaster.portal.webui.client.utils.ClientMapUtil;


@Entity()
@Table(name = "USERS", schema = "TICKETS")
@NamedQueries({
		@NamedQuery(name = "UserUI.findByUserId", query = "SELECT u FROM UserUI u WHERE u.userId = :userId"),
		@NamedQuery(name = "UserUI.findByUserName", query = "SELECT u FROM UserUI u WHERE UPPER(u.userName) = UPPER(:userName)") })
public class UserUI implements Serializable {

	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Long userId;

	private String userName;

	private String userPwd;

	private Long userStatus;

	private Long reseted;

	private Long officeId;

	private Long isExternalUser;

	@Basic()
	@Column(name = "OFFICE_ID", length = 7)
	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public UserUI() {
	}

	@SuppressWarnings("rawtypes")
	public void loadFromMap(Map dataMap) throws Exception {
		try {
			officeId = ClientMapUtil.convertLong(dataMap, "office_id");
			userId = ClientMapUtil.convertLong(dataMap, "user_id");
			userName = ClientMapUtil.convertString(dataMap, "user_name");
			userStatus = ClientMapUtil.convertLong(dataMap, "user_status");
			reseted = ClientMapUtil.convertLong(dataMap, "reseted");
			isExternalUser = ClientMapUtil.convertLong(dataMap, "is_external_user");
			
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings("rawtypes")
	public void loadFromObjMap(Map dataMap) throws Exception {
		try {
			officeId = ClientMapUtil.convertLong(dataMap, "officeId");
			userId = ClientMapUtil.convertLong(dataMap, "userId");
			userName = ClientMapUtil.convertString(dataMap, "userName");
			userStatus = ClientMapUtil.convertLong(dataMap, "userStatus");
			reseted = ClientMapUtil.convertLong(dataMap, "reseted");
			isExternalUser = ClientMapUtil.convertLong(dataMap, "isExternalUser");
		} catch (Exception e) {
			throw e;
		}
	}

	@Id()
	@SequenceGenerator(name = "idGenerator", schema = "CCARE", sequenceName = "CCARE.USER_MANAGER.SEQ_USER_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idGenerator")
	@Column(name = "USER_ID", unique = true, nullable = false, precision = 22)
	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Basic()
	@Column(name = "USER_NAME", length = 256)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Lob
	@Column(name = "USER_PWD")
	public String getUserPwd() {
		return this.userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	@Basic()
	@Column(name = "USER_STATUS", precision = 22)
	public Long getUserStatus() {
		return this.userStatus;
	}

	public void setUserStatus(Long userStatus) {
		this.userStatus = userStatus;
	}

	@Basic()
	@Column(name = "RESETED", precision = 22)
	public Long getReseted() {
		return reseted;
	}

	public void setReseted(Long reseted) {
		this.reseted = reseted;
	}
	
	@Basic()
	@Column(name = "IS_EXTERNAL_USER", precision = 22)
	public Long getIsExternalUser() {
		return isExternalUser;
	}

	public void setIsExternalUser(Long isExternalUser) {
		this.isExternalUser = isExternalUser;
	}
}
