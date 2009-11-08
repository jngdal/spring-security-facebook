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

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class FacebookAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private Long uid;

	public FacebookAuthenticationToken() {
		this(null, null);
	}

	public FacebookAuthenticationToken(Long uid) {
		this(uid, null);
	}

	public FacebookAuthenticationToken(Long uid,
			List<GrantedAuthority> authorities) {
		super(authorities);
		this.uid = uid;
		super.setAuthenticated(true);
	}

	public void setAuthenticated(boolean isAuthenticated)
			throws IllegalArgumentException {
		throw new IllegalArgumentException(
				"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
	}

	public Object getCredentials() {
		return String.valueOf(uid);
	}

	public Object getPrincipal() {
		return null;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getUid() {
		return uid;
	}
}