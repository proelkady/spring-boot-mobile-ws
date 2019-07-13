package com.proelkady.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.proelkady.app.ws.exception.UserServiceException;
import com.proelkady.app.ws.io.entiry.UserEntity;
import com.proelkady.app.ws.io.repositories.UserRepository;
import com.proelkady.app.ws.service.UserService;
import com.proelkady.app.ws.shared.UserDto;
import com.proelkady.app.ws.shared.Utils;
import com.proelkady.app.ws.ui.model.response.ErrorMessages;

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
			throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMsg());
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
		UserEntity userEntity = userRepository.findUserByEmail(username);
		if (userEntity == null)
			throw new UsernameNotFoundException(username);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto loadUserByEmail(String email) {
		UserDto userDto = new UserDto();
		UserEntity userEntity = userRepository.findUserByEmail(email);
		BeanUtils.copyProperties(userEntity, userDto);
		return userDto;
	}

	@Override
	public UserDto findUserById(String userId) {
		UserEntity entity = userRepository.findUserByUserId(userId);
		if (entity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMsg());
		}
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(entity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto) {
		UserEntity entity = userRepository.findUserByUserId(userId);
		if (entity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMsg());
		}
		entity.setFirstName(userDto.getFirstName());
		entity.setLastName(userDto.getLastName());

		UserEntity updatedEntity = userRepository.save(entity);
		UserDto returnedValue = new UserDto();
		BeanUtils.copyProperties(updatedEntity, returnedValue);
		return returnedValue;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity entity = userRepository.findUserByUserId(userId);
		if (entity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMsg());
		}
		userRepository.delete(entity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValues = new ArrayList<>();
		Pageable pageableRequest = PageRequest.of(page, limit);
		
		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = usersPage.getContent();
		
        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValues.add(userDto);
        }
		
		return returnValues;
	}

}
