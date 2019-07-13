package com.proelkady.app.ws.security;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proelkady.app.ws.SpringApplicationContext;
import com.proelkady.app.ws.service.UserService;
import com.proelkady.app.ws.shared.UserDto;
import com.proelkady.app.ws.ui.model.request.UserLoginRequestModel;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private final AuthenticationManager authenticationManager;
	
	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		this.setFilterProcessesUrl("/users/login"); // change the login url
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			// get the UserLoginRequestModel object from HttpServletRequest
			UserLoginRequestModel creds = new ObjectMapper()
					.readValue(request.getInputStream(), UserLoginRequestModel.class);
			
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String userName = ((User) authResult.getPrincipal()).getUsername();
		
		// generate the token using Jwts library
		String token = Jwts.builder()
				.setSubject(userName)
				.setExpiration(new Date(Instant.now().toEpochMilli() + SecurityConstants.EXPIERATION_DATE))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.tokenSecret())
				.compact();
		
		UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
		UserDto userDto = userService.loadUserByEmail(userName);
		
		response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
		response.addHeader("UserId", userDto.getUserId());

	}
	
}
