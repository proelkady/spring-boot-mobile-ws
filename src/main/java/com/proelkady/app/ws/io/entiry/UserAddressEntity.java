package com.proelkady.app.ws.io.entiry;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "user_addresses")
public class UserAddressEntity implements Serializable {

	private static final long serialVersionUID = 7718643821901255862L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "address_id", nullable = false, length = 30)
	private String addressId;

	@Column(nullable = false, length = 15)
	private String city;

	@Column(nullable = false, length = 15)
	private String country;

	@Column(nullable = false, length = 10)
	private String type;

	@Column(name = "street_name", nullable = false, length = 100)
	private String streetName;

	@Column(name = "postal_code", length = 100)
	private String postalCode;

	@ManyToOne
	@JoinColumn(name = "users_id")
	private UserEntity userDetails;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public UserEntity getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserEntity userDetails) {
		this.userDetails = userDetails;
	}

}
