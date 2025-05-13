package com.example.personallearningapp.model;

public class User {
    public int id;
    public String username;
    public String email;
    public String password;
    public String phone;
    public String avatarUri;

    // 全参构造函数（用于注册、插入数据库）
    public User(String username, String email, String password, String phone, String avatarUri) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.avatarUri = avatarUri;
    }

    // 无参构造函数（用于数据库读取时临时赋值）
    public User() {}
}