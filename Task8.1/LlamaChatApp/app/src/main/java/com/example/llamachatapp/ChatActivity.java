package com.example.llamachatapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
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
        if (username == null || username.trim().isEmpty()) {
            username = "User";
        }

        // Set RecyclerView to stack down (new messages are at the bottom) 设置 RecyclerView 向下堆叠（新消息在底部）
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChatAdapter(messages);
        recyclerView.setAdapter(adapter);

        // Welcome
        addMessage("Welcome " + username + "!", false);

        // Enable internal scrolling of EditText (for looking at the last line after the maxLines limit) 启用 EditText 的内部滚动（用于 maxLines 限制后看最后一行）
        inputEditText.setVerticalScrollBarEnabled(true);  // 不允许多行
        inputEditText.setMovementMethod(new ScrollingMovementMethod());
        inputEditText.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        inputEditText.setMaxLines(5); // 确保 Java 侧也限制 maxLines

        // Prevent RecyclerView from truncating EditText sliding 防止 RecyclerView 截断 EditText 滑动
        inputEditText.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.inputEditText) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
            return false;
        });

        // Send
        sendButton.setOnClickListener(v -> sendMessage());
    }

    // Adding a message to the chat box 添加消息到聊天框
    void addMessage(String text, boolean isUser) {
        String senderName = isUser ? username : "✨";
        messages.add(new ChatMessage(text, isUser, senderName));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    // Exe a Send
    private void sendMessage() {

        String userMsg = inputEditText.getText().toString().trim();
        if (!userMsg.isEmpty()) {
            addMessage(userMsg, true);
            inputEditText.setText("");
            hideKeyboard(); // Hide soft keyboard after sending
            getBotResponse(userMsg);
        }
    }

    // Hide soft keyboard after sending
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void getBotResponse(String userMessage) {
        // Add “Entering...” Placeholder Bubble
        ChatMessage typingMsg = new ChatMessage("Entering...", false, "✨");
        messages.add(typingMsg);
        int typingIndex = messages.size() - 1;
        adapter.notifyItemInserted(typingIndex);
        recyclerView.scrollToPosition(typingIndex);

        // Request the API using OkHttp
        ChatGPTApi.sendMessageToChatGPT(userMessage, new ChatGPTApi.Callback() {
            @Override
            public void onSuccess(String reply) {
                runOnUiThread(() -> {
                    // Simulate thinking, delayed by 1 second to show replies
                    new Handler().postDelayed(() -> {
                        typingMsg.text = reply.trim(); // replace
                        adapter.notifyItemChanged(typingIndex);
                    }, 1000);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    new Handler().postDelayed(() -> {
                        typingMsg.text = "Error: " + error;
                        adapter.notifyItemChanged(typingIndex);
                    }, 1000);
                });
            }
        });
    }
}