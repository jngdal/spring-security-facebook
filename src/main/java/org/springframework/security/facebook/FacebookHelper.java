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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookWebappHelper;
import com.google.code.facebookapi.FacebookXmlRestClient;
import com.google.code.facebookapi.IFacebookRestClient;
import com.google.code.facebookapi.ProfileField;

public class FacebookHelper {

	private String apiKey;
	private String secret;

	protected IFacebookRestClient<Document> getFacebookClient(
			HttpServletRequest request, HttpServletResponse response)
			throws FacebookUserNotConnected {

		String sessionKey = lookupSessionKey(request, response);
		return new FacebookXmlRestClient(apiKey, secret, sessionKey);
	}

	@SuppressWarnings("deprecation")
	public Long getLoggedInUserId(HttpServletRequest request,
			HttpServletResponse response) throws FacebookUserNotConnected {

		FacebookWebappHelper<Document> facebook = new FacebookWebappHelper<Document>(
				request, response, apiKey, secret);
		Long uid = facebook.getUser();

		if (uid == null)
			throw new FacebookUserNotConnected(
					"Facebook user id could not be obtained");

		return uid;
	}

	protected Document query(HttpServletRequest request,
			HttpServletResponse response, String fql)
			throws FacebookUserNotConnected {

		IFacebookRestClient<Document> client = getFacebookClient(request,
				response);

		Document document = null;
		try {
			document = client.fql_query(fql);
		} catch (FacebookException e) {
			handleFacebookException(request, response, e);
		}

		return document;
	}

	public Map<String, String> getLoggedInUserInfo(HttpServletRequest request,
			HttpServletResponse response) throws FacebookUserNotConnected {

		Long uid = getLoggedInUserId(request, response);

		Map<String, String> userInfo = new HashMap<String, String>();

		Document userInfoDoc = query(request, response,
				"select uid, first_name, last_name, proxied_email from user where uid in "
						+ uid);

		for (ProfileField pfield : ProfileField.values()) {
			NodeList nodes = userInfoDoc.getElementsByTagName(pfield
					.fieldName());
			if (nodes != null && nodes.getLength() > 0) {
				userInfo
						.put(pfield.fieldName(), nodes.item(0).getTextContent());
			}
		}

		return userInfo;
	}

	protected void handleFacebookException(HttpServletRequest request,
			HttpServletResponse response, FacebookException facebookException) {
		facebookException.printStackTrace();
		if (facebookException.getCode() == 102) {
			RequestDispatcher requestDispatcher = request
					.getRequestDispatcher("/j_spring_security_logout");
			try {
				requestDispatcher.forward(request, response);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected String lookupSessionKey(HttpServletRequest request,
			HttpServletResponse response) throws FacebookUserNotConnected {

		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(getApiKey() + "_session_key"))

					return cookie.getValue();
			}
		}

		throw new FacebookUserNotConnected(
				"Facebook session key could not be obtained");
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}