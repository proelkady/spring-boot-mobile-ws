package com.proelkady.app.ws.io.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proelkady.app.ws.io.entiry.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    UserEntity findUserByUserId(String userId);

    UserEntity findUserByEmailVerificationToken(String token);
}
