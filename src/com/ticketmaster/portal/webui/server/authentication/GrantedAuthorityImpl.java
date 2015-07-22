package com.ticketmaster.portal.webui.server.authentication;

import com.ticketmaster.portal.webui.shared.entity.umanager.UserUI;
import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority {

	private static final long serialVersionUID = 1029928088340565343L;
	private String rolename;
	private UserUI user;

	public GrantedAuthorityImpl(String rolename, UserUI user) {
		this.rolename = rolename;
		this.user = user;
	}

	public String getAuthority() {
		return this.rolename;
	}

	public UserUI getUser() {
		return user;
	}
}