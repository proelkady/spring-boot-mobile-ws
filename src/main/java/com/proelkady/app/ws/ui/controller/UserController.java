
package com.proelkady.app.ws.ui.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.proelkady.app.ws.exception.UserServiceException;
import com.proelkady.app.ws.service.UserService;
import com.proelkady.app.ws.shared.UserDto;
import com.proelkady.app.ws.ui.model.request.UserDetailsRequestModel;
import com.proelkady.app.ws.ui.model.response.ErrorMessages;
import com.proelkady.app.ws.ui.model.response.OperationSatusModel;
import com.proelkady.app.ws.ui.model.response.RequestOperation;
import com.proelkady.app.ws.ui.model.response.RequestStatus;
import com.proelkady.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest getUser(@PathVariable String userId) {
		if (userId == null) {
			throw new RuntimeException("userId can't be null");
		}
		UserDto userDto = userService.findUserById(userId);
		UserRest userRest = new UserRest();
		BeanUtils.copyProperties(userDto, userRest);
		return userRest;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException {
		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMsg());
		UserRest returnedValue = new UserRest();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, returnedValue);

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
	public OperationSatusModel deleteUser(@PathVariable String userId) {
		if (userId == null)
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMsg());

		OperationSatusModel status = new OperationSatusModel();
		status.setOperationName(RequestOperation.DELETE.toString());
		status.setOperationResult(RequestStatus.SUCCESS.toString());
		userService.deleteUser(userId);

		return status;
	}
}
