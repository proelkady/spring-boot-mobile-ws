package com.proelkady.app.ws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.proelkady.app.ws.UserRepository;
import com.proelkady.app.ws.io.entiry.UserEntity;
import com.proelkady.app.ws.service.UserService;
import com.proelkady.app.ws.shared.UserDto;
import com.proelkady.app.ws.shared.Utils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private Utils utils;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDto createUser(UserDto user) {
		if (userRepository.findUserByEmail(user.getEmail()) != null) { // make sure that this user doesn't exists
			throw new RuntimeException("Record already exists");
		}
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setUserId(utils.generateUserId(30));

		UserEntity savedEntity = userRepository.save(userEntity);

		UserDto savedDto = new UserDto();
		BeanUtils.copyProperties(savedEntity, savedDto);

		return savedDto;

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}

}
