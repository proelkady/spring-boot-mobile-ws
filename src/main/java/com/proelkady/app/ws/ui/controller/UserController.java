
package com.proelkady.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.modelmapper.TypeToken;
import com.proelkady.app.ws.exception.UserServiceException;
import com.proelkady.app.ws.service.UserAddressService;
import com.proelkady.app.ws.service.UserService;
import com.proelkady.app.ws.shared.UserAddressDto;
import com.proelkady.app.ws.shared.UserDto;
import com.proelkady.app.ws.ui.model.request.UserDetailsRequestModel;
import com.proelkady.app.ws.ui.model.response.ErrorMessages;
import com.proelkady.app.ws.ui.model.response.OperationSatusModel;
import com.proelkady.app.ws.ui.model.response.RequestOperation;
import com.proelkady.app.ws.ui.model.response.RequestStatus;
import com.proelkady.app.ws.ui.model.response.UserAddressRest;
import com.proelkady.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserAddressService userAddressService;

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> returnValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);
		users.forEach(userDto -> {
			UserRest userRest = new UserRest();
			BeanUtils.copyProperties(userDto, userRest);
			returnValue.add(userRest);
		});
		return returnValue;
	}

	// http://localhost:8080/mobile-app-ws/users/asdfasdfsgasfdsadfasf/addresses
	@GetMapping(path = "/{userId}/addresses", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public List<UserAddressRest> getUserAddresses(@PathVariable String userId) {
		ModelMapper modelMapper = new ModelMapper();
		List<UserAddressRest> addressesRestList = new ArrayList<>();
		List<UserAddressDto> addresses = userAddressService.getAddresses(userId);
		if (addresses != null && !addresses.isEmpty()) {
			Type listType = new TypeToken<List<UserAddressRest>>() {
			}.getType();
			addressesRestList = modelMapper.map(addresses, listType);
		}

		return addressesRestList;
	}

	// http://localhost:8080/mobile-app-ws/users/asdfasdfsgasfdsadfasf/addresses
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public UserAddressRest getUserAddresse(@PathVariable String userId, @PathVariable String addressId) {
		ModelMapper modelMapper = new ModelMapper();
		UserAddressDto addresses = userAddressService.getAddress(userId, addressId);
		UserAddressRest addressesRestList = modelMapper.map(addresses, UserAddressRest.class);

		return addressesRestList;
	}

	@GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest getUser(@PathVariable String userId) {
		if (userId == null) {
			throw new RuntimeException("userId can't be null");
		}
		UserDto userDto = userService.findUserById(userId);
		ModelMapper modelMapper = new ModelMapper();
		UserRest userRest = modelMapper.map(userDto, UserRest.class);
		return userRest;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException {
		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMsg());

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
		UserRest returnedValue = modelMapper.map(createdUser, UserRest.class);

		return returnedValue;
	}

	@PutMapping(path = "/{userId}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public UserRest updateUser(@PathVariable String userId, @RequestBody UserDetailsRequestModel userDetails) {
		if (userId == null || userDetails == null)
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMsg());

		UserRest returnedValue = new UserRest();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto createdUser = userService.updateUser(userId, userDto);
		BeanUtils.copyProperties(createdUser, returnedValue);

		return returnedValue;
	}

	@DeleteMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationSatusModel deleteUser(@PathVariable("userId") String userId) {
		if (userId == null)
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMsg());

		OperationSatusModel status = new OperationSatusModel();
		status.setOperationName(RequestOperation.DELETE.toString());
		status.setOperationResult(RequestStatus.SUCCESS.toString());
		userService.deleteUser(userId);

		return status;
	}
}
