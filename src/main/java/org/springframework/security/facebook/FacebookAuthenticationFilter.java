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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class FacebookAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter implements
		ApplicationContextAware {

	public static final String DEFAULT_FILTER_PROCESS_URL = "/j_spring_facebook_security_check";
	private ApplicationContext ctx;

	protected FacebookAuthenticationFilter() {
		super(DEFAULT_FILTER_PROCESS_URL);
	}

	public Authentication attemptAuthentication(HttpServletRequest req,
			HttpServletResponse res) throws AuthenticationException,
			IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		FacebookHelper facebook = (FacebookHelper) ctx
				.getBean("facebookHelper");

		Long uid = null;
		try {
			uid = facebook.getLoggedInUserId(request, response);
		} catch (FacebookUserNotConnected e) {
			throw new AuthenticationCredentialsNotFoundException(
					"Facebook user not connected", e);
		}

		FacebookAuthenticationToken token = new FacebookAuthenticationToken(uid);
		token.setDetails(authenticationDetailsSource.buildDetails(request));

		AuthenticationManager authenticationManager = getAuthenticationManager();
		Authentication authentication = authenticationManager
				.authenticate(token);

		return authentication;
	}

	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}
}