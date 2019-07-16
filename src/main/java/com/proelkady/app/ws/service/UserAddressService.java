package com.proelkady.app.ws.service;

import java.util.List;

import com.proelkady.app.ws.shared.UserAddressDto;

public interface UserAddressService {

	List<UserAddressDto> getAddresses(String userId);

	UserAddressDto getAddress(String userId, String addressId);

}
