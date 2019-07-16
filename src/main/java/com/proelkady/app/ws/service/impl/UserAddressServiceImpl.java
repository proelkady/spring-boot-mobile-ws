package com.proelkady.app.ws.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proelkady.app.ws.exception.UserServiceException;
import com.proelkady.app.ws.io.entiry.UserAddressEntity;
import com.proelkady.app.ws.io.entiry.UserEntity;
import com.proelkady.app.ws.io.repositories.UserAddressRepository;
import com.proelkady.app.ws.io.repositories.UserRepository;
import com.proelkady.app.ws.service.UserAddressService;
import com.proelkady.app.ws.shared.UserAddressDto;
import com.proelkady.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserAddressServiceImpl implements UserAddressService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserAddressRepository userAddressRepository;

	@Override
	public List<UserAddressDto> getAddresses(String userId) {
		List<UserAddressDto> returnedValue = new ArrayList<>();
		UserEntity userEntity = userRepository.findUserByUserId(userId);
		if (userEntity == null)
			return returnedValue;

		List<UserAddressEntity> userAddresses = userAddressRepository.findAllByUserDetails(userEntity);
		ModelMapper modelMapper = new ModelMapper();
		Type listType = new TypeToken<List<UserAddressDto>>() {
		}.getType();
		returnedValue = modelMapper.map(userAddresses, listType);
		return returnedValue;
	}

	@Override
	public UserAddressDto getAddress(String userId, String addressId) {
		
		UserEntity userEntity = userRepository.findUserByUserId(userId);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMsg());

		UserAddressEntity userAddresses = userAddressRepository.findAllByUserDetailsAndAddressId(userEntity, addressId);
		ModelMapper modelMapper = new ModelMapper();
		UserAddressDto returnedValue  = modelMapper.map(userAddresses, UserAddressDto.class);
		return returnedValue;
	}

}
