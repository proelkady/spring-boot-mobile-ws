
package com.proelkady.app.ws.ui.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proelkady.app.ws.service.UserService;
import com.proelkady.app.ws.shared.UserDto;
import com.proelkady.app.ws.ui.model.request.UserDetailsRequestModel;
import com.proelkady.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping
	public String getUser() {
		return "get user was called";
	}
	
	@PostMapping
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnedValue = new UserRest();
		
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, returnedValue);
		
		return returnedValue;
	}
}
