package com.proelkady.app.ws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.proelkady.app.ws.shared.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user);

	UserDto loadUserByEmail(String email);

	UserDto findUserById(String userId);

	UserDto updateUser(String userId, UserDto userDto);

	void deleteUser(String userId);
}
