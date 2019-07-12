package com.proelkady.app.ws.ui.model.request;

public class UserLoginRequestModel{
  private String email;
  private String password;

  private void setEmail(String email){
    this.email = email;
  }

  private String getEmail(String email){
    return email;
  }

  private String getPassword(){
    return password;
  }

  private void setPassword(String password){
    this.password = password;
  }
}