package com.example.personallearningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personallearningapp.model.Question;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private ResultAdapter resultAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Log.d("----- DEBUG", "username: " + getIntent().getStringExtra("username"));
        Log.d("----- DEBUG", "avatar_uri: " + getIntent().getStringExtra("avatar_uri"));

        RecyclerView recyclerView = findViewById(R.id.newsRecycler);
        Button btnContinue = findViewById(R.id.btnContinue);

        // Receive a list of incoming Questions 接收传来的 Question 列表
        @SuppressWarnings("unchecked")
        List<Question> questions = (List<Question>) getIntent().getSerializableExtra("questions");

        if (questions == null) questions = new ArrayList<>();

        // Receive a list of incoming Questions 将数据转换为 [title, response] 数组用于 Adapter 显示
        List<String[]> results = new ArrayList<>();
        for (Question q : questions) {
            results.add(new String[]{q.questionTitle, q.response});
        }
        resultAdapter = new ResultAdapter(results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(resultAdapter);

        // Jump to the Dashboard
        // ResultActivity ➝ DashboardActivity (Continue)
        //     (putExtra "username", "avatar_uri", "taskTitle", "taskDesc")
        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("taskTitle", getIntent().getStringExtra("taskTitle"));
            intent.putExtra("taskDesc", getIntent().getStringExtra("taskDesc"));
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("avatar_uri", getIntent().getStringExtra("avatar_uri"));
            intent.putExtra("user_id", getIntent().getIntExtra("user_id", -1));
            startActivity(intent);
            finish();
        });

        // Back to task page, Redo the question
        // ResultActivity ➝ TaskActivity (Back Button)
        //     (putExtra "taskTitle", "taskDesc", "username", "avatar_uri")
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ResultActivity.this, TaskActivity.class);
                intent.putExtra("taskTitle", getIntent().getStringExtra("taskTitle"));
                intent.putExtra("taskDesc", getIntent().getStringExtra("taskDesc"));
                intent.putExtra("username", getIntent().getStringExtra("username"));
                intent.putExtra("avatar_uri", getIntent().getStringExtra("avatar_uri"));
                startActivity(intent);
                finish();
            }
        });
    }

}
