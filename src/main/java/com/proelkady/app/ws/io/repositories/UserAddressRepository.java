package com.proelkady.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.proelkady.app.ws.io.entiry.UserAddressEntity;
import com.proelkady.app.ws.io.entiry.UserEntity;

@Repository
public interface UserAddressRepository extends CrudRepository<UserAddressEntity, Long> {

	List<UserAddressEntity> findAllByUserDetails(UserEntity userEntity);

	UserAddressEntity findAllByUserDetailsAndAddressId(UserEntity userEntity, String addressId);

	UserAddressEntity findByAddressId(String addressId);

}
