package com.example.personallearningapp.model;

public class User {
    public int id;
    public String username;
    public String email;
    public String password;
    public String phone;
    public String avatarUri;

    public User(String username, String email, String password, String phone, String avatarUri) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.avatarUri = avatarUri;
    }
}