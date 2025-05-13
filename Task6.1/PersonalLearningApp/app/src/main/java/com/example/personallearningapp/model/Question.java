package com.example.personallearningapp.model;

import java.io.Serializable;

public class Question implements Serializable {
    public String questionTitle; // e.g. "What is recursion?"
    public String[] options;
    public int selectedOption = -1;
    public String correctAnswer;
    public String response; // For result page display
    public boolean isExpanded = false; // Control expansion status

    public Question(String questionTitle, String[] options) {
        this.questionTitle = questionTitle;
        this.options = options;
    }
}
