
package com.proelkady.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.proelkady.app.ws.ui.model.request.PasswordResetModel;
import com.proelkady.app.ws.ui.model.request.PasswordResetRequestModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
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
import com.proelkady.app.ws.ui.model.response.OperationStatusModel;
import com.proelkady.app.ws.ui.model.response.RequestOperation;
import com.proelkady.app.ws.ui.model.response.RequestStatus;
import com.proelkady.app.ws.ui.model.response.UserAddressRest;
import com.proelkady.app.ws.ui.model.response.UserRest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserAddressService userAddressService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
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
    @GetMapping(path = "/{userId}/addresses", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
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
    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public UserAddressRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        ModelMapper modelMapper = new ModelMapper();
        UserAddressDto addresses = userAddressService.getAddress(userId, addressId);
        UserAddressRest addressesRest = modelMapper.map(addresses, UserAddressRest.class);
        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
        addressesRest.add(addressLink);
        addressesRest.add(userLink);
        addressesRest.add(addressesLink);
        return addressesRest;
    }

    @GetMapping(path = "/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUser(@PathVariable String userId) {
        if (userId == null) {
            throw new RuntimeException("userId can't be null");
        }
        UserDto userDto = userService.findUserById(userId);
        ModelMapper modelMapper = new ModelMapper();
        UserRest userRest = modelMapper.map(userDto, UserRest.class);
        return userRest;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException {
        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMsg());

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        UserRest returnedValue = modelMapper.map(createdUser, UserRest.class);

        return returnedValue;
    }

    @PutMapping(path = "/{userId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
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

    @DeleteMapping(path = "/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable("userId") String userId) {
        if (userId == null)
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMsg());

        OperationStatusModel status = new OperationStatusModel();
        status.setOperationName(RequestOperation.DELETE.toString());
        status.setOperationResult(RequestStatus.SUCCESS.toString());
        userService.deleteUser(userId);

        return status;
    }

    /*
     * http://localhost:8080/mobile-app-ws/users/email-verification?token=sdfsdf
     * */
    @GetMapping(path = "/email-verification", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperation.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            returnValue.setOperationResult(RequestStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestStatus.ERROR.name());
        }

        return returnValue;
    }

    /*
     * http://localhost:8080/mobile-app-ws/users/password-reset-request
     * */
    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperation.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestStatus.ERROR.name());

        if (operationResult) {
            returnValue.setOperationResult(RequestStatus.SUCCESS.name());
        }

        return returnValue;
    }


    @PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperation.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestStatus.ERROR.name());

        if (operationResult) {
            returnValue.setOperationResult(RequestStatus.SUCCESS.name());
        }

        return returnValue;
    }
}
