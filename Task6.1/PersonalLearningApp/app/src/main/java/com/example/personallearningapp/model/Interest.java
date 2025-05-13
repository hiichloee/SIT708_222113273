// 🔹 Interest.java
package com.example.personallearningapp.model;

public class Interest {
    public int id;
    public int userId;
    public String interest;

    public Interest(int userId, String interest) {
        this.userId = userId;
        this.interest = interest;
    }
}