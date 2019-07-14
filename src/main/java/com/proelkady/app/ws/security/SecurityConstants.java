package com.proelkady.app.ws.security;

import com.proelkady.app.ws.SpringApplicationContext;

public class SecurityConstants {
	public static final long EXPIERATION_DATE = 864000000; // 10 days in milliseconds
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	
	public static String tokenSecret() {
		AppProperties props = (AppProperties) SpringApplicationContext.getBean("appProperties");
		return props.tokenSecret();
	}
}
