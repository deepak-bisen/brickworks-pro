package com.brick.app.employeeservice.dto;

//This class models the JSON response we will send back after a successful login.
public class AuthResponse {
private final String jwt;

    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }

    //Getter
    public String getJwt() {
        return jwt;
    }
}
