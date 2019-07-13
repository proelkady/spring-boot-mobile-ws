package com.proelkady.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.proelkady.app.ws.io.entiry.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>{

	UserEntity findUserByEmail(String email);

	UserEntity findUserByUserId(String userId);
}
