/*
 * Copyright [2009] [Kadir PEKEL]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.facebook;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

public class FacebookAuthenticationProvider implements AuthenticationProvider {

	private String[] roles;

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		FacebookAuthenticationToken facebookAuthentication = (FacebookAuthenticationToken) authentication;

		if (facebookAuthentication.getUid() == null)
			throw new BadCredentialsException(
					"User not authenticated through facebook");

		if (roles == null) {
			roles = new String[] {};
		}

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (String role : roles) {
			authorities.add(new GrantedAuthorityImpl(role));
		}

		FacebookAuthenticationToken succeedToken = new FacebookAuthenticationToken(
				facebookAuthentication.getUid(), authorities);
		succeedToken.setDetails(authentication.getDetails());

		return succeedToken;
	}

	public boolean supports(Class<? extends Object> authentication) {
		boolean supports = FacebookAuthenticationToken.class
				.isAssignableFrom(authentication);
		return supports;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String[] getRoles() {
		return roles;
	}

}