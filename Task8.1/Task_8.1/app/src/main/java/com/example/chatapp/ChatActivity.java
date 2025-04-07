package com.example.chatapp;

import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText inputEditText;
    ImageButton sendButton;
    ChatAdapter adapter;
    ArrayList<ChatMessage> messages = new ArrayList<>();
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);

        username = getIntent().getStringExtra("USERNAME");

        adapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addMessage("Welcome " + username + "!", false);

        sendButton.setOnClickListener(v -> {
            String userMsg = inputEditText.getText().toString().trim();
            if (!userMsg.isEmpty()) {
                addMessage(userMsg, true);
                inputEditText.setText("");
                getBotResponse(userMsg);
            }
        });
    }

    void addMessage(String text, boolean isUser) {
        messages.add(new ChatMessage(text, isUser));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    void getBotResponse(String userMessage) {
        new Handler().postDelayed(() -> addMessage("Echo: " + userMessage, false), 1000);
    }
}