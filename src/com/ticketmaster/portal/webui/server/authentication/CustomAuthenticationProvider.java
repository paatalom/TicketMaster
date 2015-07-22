package com.ticketmaster.portal.webui.server.authentication;

import com.ticketmaster.portal.webui.server.common.DMIUtils;
import com.ticketmaster.portal.webui.shared.entity.umanager.UserUI;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		try {
			String username = authentication.getName();
			String password = authentication.getCredentials().toString();
			Map<String, String> criteria = new LinkedHashMap<String, String>();
			criteria.put("user_name", username);
			List<Map<?, ?>> data = DMIUtils.findRecordsByCriteria(null, "UserManagerDS", "getByUserId", criteria);
			if (data == null || data.isEmpty()) {
				throw new UsernameNotFoundException("Wrong username or password");
			}
			Map<?, ?> userInfo = data.get(0);
			if (userInfo == null || userInfo.isEmpty()) {
				throw new UsernameNotFoundException("Wrong username or password 1.");
			}
			UserUI user = new UserUI();
			user.loadFromMap(userInfo);
			System.out.println("user.getUserStatus() = " + user.getUserStatus());
			if (user.getUserStatus() == null || !user.getUserStatus().equals(1L)) {
				throw new UsernameNotFoundException("User Is Not Active");
			}

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			InputStream stream = (InputStream) userInfo.get("user_pwd");
			int next = stream.read();
			while (next > -1) {
				bos.write(next);
				next = stream.read();
			}
//			user.setUserPwd(bos.toByteArray());
//			byte dbPassword[] = user.getUserPwd();

			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(password.getBytes());
			byte[] passwd = md5.digest();
			boolean result = false;//MessageDigest.isEqual(dbPassword, passwd);
			if (!result) {
				throw new UsernameNotFoundException("Wrong username or password 3.");
			}

			criteria.clear();
			criteria.put("user_id", user.getUserId().toString());
			List<Map<?, ?>> dataPerms = DMIUtils.findRecordsByCriteria(null, "UserManagerDS", "getPermsByUserId",
					criteria);
			TreeSet<Long> mapPerms = new TreeSet<Long>();
			if (dataPerms != null && !dataPerms.isEmpty()) {
				for (Map<?, ?> map : dataPerms) {
					mapPerms.add(Long.parseLong(map.get("permission_id").toString()));
				}
			}
			if (mapPerms.isEmpty()) {
				throw new UsernameNotFoundException("Wrong username or password 25.");
			}

			List<Map<?, ?>> dataPartyId = DMIUtils.findRecordsByCriteria(null, "UserManagerDS", "getUserParties",
					criteria);
			if (dataPartyId == null || dataPartyId.isEmpty()) {
				throw new UsernameNotFoundException("Wrong username or password 26.");
			}

			List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
			grantedAuths.add(new GrantedAuthorityImpl("ROLE_USER", user));
			return new UsernamePasswordAuthenticationToken(username, password, grantedAuths);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UsernameNotFoundException("Wrong username or password 2.");
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		boolean result = authentication.equals(UsernamePasswordAuthenticationToken.class);
		System.out.println("supports. result = " + result);
		return result;
	}

}
