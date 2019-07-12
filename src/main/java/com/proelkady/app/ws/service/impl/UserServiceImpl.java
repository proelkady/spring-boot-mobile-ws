package com.proelkady.app.ws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proelkady.app.ws.UserRepository;
import com.proelkady.app.ws.io.entiry.UserEntity;
import com.proelkady.app.ws.service.UserService;
import com.proelkady.app.ws.shared.UserDto;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDto createUser(UserDto user) {
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		userEntity.setEncryptedPassword("s");
		userEntity.setUserId("testUserId");
		
		UserEntity savedEntity = userRepository.save(userEntity);
		
		UserDto savedDto = new UserDto();
		BeanUtils.copyProperties(savedEntity, savedDto);
		
		return savedDto;
	}

}
