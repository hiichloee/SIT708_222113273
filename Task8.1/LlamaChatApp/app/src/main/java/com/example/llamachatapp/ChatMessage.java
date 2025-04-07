package com.example.llamachatapp;

public class ChatMessage {
    public String text;
    public boolean isUser;
    public String senderName;

    public ChatMessage(String text, boolean isUser, String senderName) {
        this.text = text;
        this.isUser = isUser;
        this.senderName = senderName;
    }
}