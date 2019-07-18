package com.proelkady.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.proelkady.app.ws.io.entiry.PasswordResetTokenEntity;
import com.proelkady.app.ws.io.repositories.PasswordResetTokenRepository;
import org.modelmapper.ModelMapper;
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
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public UserDto createUser(UserDto user) {
        if (userRepository.findByEmail(user.getEmail()) != null) { // make sure that this user doesn't exists
            throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMsg());
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(userEntity.getUserId()));
        userEntity.setUserId(utils.generateUserId(30));
        if (userEntity.getAddresses() != null) {
            userEntity.getAddresses().forEach(address -> {
                address.setAddressId(utils.generateUserAddressId(30));
                address.setUserDetails(userEntity);
            });
        }
        UserEntity savedEntity = userRepository.save(userEntity);

        UserDto savedDto = modelMapper.map(savedEntity, UserDto.class);
        // Send an email message to user to verify their email address
        return savedDto;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);
        if (userEntity == null)
            throw new UsernameNotFoundException(username);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

    @Override
    public UserDto loadUserByEmail(String email) {
        UserDto userDto = new UserDto();
        UserEntity userEntity = userRepository.findByEmail(email);
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

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;

        // Find user by token
        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null) {
            boolean hastokenExpired = Utils.hasTokenExpired(token);
            if (!hastokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {

        boolean returnValue = false;

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            return returnValue;
        }

        String token = new Utils().generatePasswordResetToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        passwordResetTokenRepository.save(passwordResetTokenEntity);

//		returnValue = new AmazonSES().sendPasswordResetRequest(
//				userEntity.getFirstName(),
//				userEntity.getEmail(),
//				token);

        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnValue = false;

        if (Utils.hasTokenExpired(token)) {
            return returnValue;
        }

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenEntity == null) {
            return returnValue;
        }

        // Prepare new password
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // Update User password in database
        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // Verify if password was saved successfully
        if (savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
            returnValue = true;
        }

        // Remove Password Reset token from database
        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnValue;
    }

}
