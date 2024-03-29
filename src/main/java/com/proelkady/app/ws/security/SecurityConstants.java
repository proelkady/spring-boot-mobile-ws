package com.proelkady.app.ws.security;

import com.proelkady.app.ws.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIERATION_DATE = 864000000; // 10 days in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
    public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
    public static final String PASSWORD_RESET_URL = "/users/password-reset";

    public static String tokenSecret() {
        AppProperties props = (AppProperties) SpringApplicationContext.getBean("appProperties");
        return props.tokenSecret();
    }
}
