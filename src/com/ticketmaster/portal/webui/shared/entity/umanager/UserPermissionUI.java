package com.ticketmaster.portal.webui.shared.entity.umanager;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the USER_PERMISSION database table.
 * 
 * @author paatal
 */
@Entity()
@Table(name = "USER_PERMISSION", schema = "USER_MANAGER")
@NamedQueries( { 
	@NamedQuery(
				name = "UserPermissionUI.getByUserId", 
				query = "select e from UserPermissionUI e where e.userId = :userId") 
})
public class UserPermissionUI implements Serializable {
	
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Long userPermissionsId;

	private String remark;

	private Long userId;
	
	private Long permissionId;



	public UserPermissionUI() {
	}

	@Id()
	@SequenceGenerator(name = "idGenerator", schema="CCARE", sequenceName = "CCARE.USER_MANAGER.SEQ_USER_PERMISSIONS_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idGenerator")
	@Column(name = "USER_PERMISSIONS_ID", unique = true, nullable = false, precision = 22)
	public Long getUserPermissionsId() {
		return this.userPermissionsId;
	}

	public void setUserPermissionsId(Long userPermissionsId) {
		this.userPermissionsId = userPermissionsId;
	}

	@Basic()
	@Column(name = "REMARK", length = 255)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Basic()
	@Column(name = "USER_ID", precision = 22)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Basic()
	@Column(name = "PERMISSION_ID", precision = 22)
	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}
}
