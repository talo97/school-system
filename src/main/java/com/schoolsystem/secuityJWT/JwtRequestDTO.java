package com.schoolsystem.secuityJWT;

import java.io.Serializable;

public class JwtRequestDTO implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    private String login;
    private String password;

    //need default constructor for JSON Parsing
    public JwtRequestDTO()
    {

    }

    public JwtRequestDTO(String login, String password) {
        this.setLogin(login);
        this.setPassword(password);
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
