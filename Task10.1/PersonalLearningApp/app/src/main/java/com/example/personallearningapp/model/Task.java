//package com.example.personallearningapp.model;
//
//import java.io.Serializable;
//
//public class Task implements Serializable {
//    public String question;
//    public String[] options;
//    public int selectedOption;
//
//    public Task(String question, String[] options) {
//        this.question = question;
//        this.options = options;
//        this.selectedOption = -1;
//    }
//}
//
//// Task.java
package com.example.personallearningapp.model;

import java.io.Serializable;

public class Task implements Serializable {
    public String title;
    public String description;
    public String status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = "";
    }
}
