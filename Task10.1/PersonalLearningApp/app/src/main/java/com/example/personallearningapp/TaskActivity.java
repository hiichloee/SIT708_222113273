package com.example.personallearningapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personallearningapp.api.ApiClient;
import com.example.personallearningapp.model.Question;
import com.example.personallearningapp.model.LlmQuestion;
import com.example.personallearningapp.model.LlmQuizResponse;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private String taskTitle, taskDesc;
    private String username, avatarUri;
    private TextView tvTaskTitle, tvTaskDesc;
    private RecyclerView recyclerView;
    private Button btnSubmit;
    private ProgressBar progressBar;

    private List<Question> questionList = new ArrayList<>();
    private QuestionAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // Bind views
        tvTaskTitle = findViewById(R.id.tvTask_Title);
        tvTaskDesc = findViewById(R.id.tvTask_Desc);
        recyclerView = findViewById(R.id.newsRecycler);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        // Get Intent data
        Intent intent = getIntent();
        taskTitle = intent.getStringExtra("taskTitle");
        taskDesc = intent.getStringExtra("taskDesc");
        username = intent.getStringExtra("username");
        avatarUri = intent.getStringExtra("avatar_uri");

        tvTaskTitle.setText(taskTitle);
        tvTaskDesc.setText(taskDesc);

        // Setup RecyclerView
//        questionList = getDummyQuestions(taskTitle);
        questionAdapter = new QuestionAdapter(questionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(questionAdapter);

        // Getting topics from the LLM API - Fetch questions
        fetchQuestionsFromLLM(taskTitle);

        Log.d("------- TaskActivity", "Topic for API request: " + taskTitle);


        btnSubmit.setOnClickListener(v -> {

            // 检查至少选择了一题
            boolean hasAnswered = false;

            for (Question q : questionList) {
                if (q.selectedOption != -1) {
                    hasAnswered = true;
                    int correctIndex = getCorrectIndex(q);
                    if (q.selectedOption == correctIndex) {
                        q.response = "✅ Correct! Well done!";
                    } else {
                        q.response = "❌ Incorrect. Correct answer is: " + q.correctAnswer;
                    }
                } else {
                    q.response = "❌ No option selected.";
                }

            }

            if (!hasAnswered) {
                Toast.makeText(this, "Please answer at least one question before submitting.", Toast.LENGTH_SHORT).show();
                return;
            }

            // TaskActivity ➝ ResultActivity
            //     (putExtra "questions", "username", "avatar_uri", "taskTitle", "taskDesc")
            Intent resultIntent  = new Intent(this, ResultActivity.class);
            resultIntent .putExtra("questions", new ArrayList<>(questionList)); // Questions implements Serializable
            resultIntent.putExtra("user_id", getIntent().getIntExtra("user_id", -1));
            resultIntent.putExtra("taskTitle", taskTitle);
            resultIntent.putExtra("taskDesc", taskDesc);
            resultIntent.putExtra("username", username);
            resultIntent.putExtra("avatar_uri", avatarUri);
            startActivity(resultIntent );
            finish();
        });
    }

    // 替换旧的 dummy data 调用
    private void fetchQuestionsFromLLM(String topic) {
        // Display ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        Log.d("------- API_REQUEST", "Request URL: http://10.0.2.2:5000/getQuiz?topic=" + Uri.encode(topic));

        ApiClient.fetchQuizFromLLM(topic, new ApiClient.OnQuizResponse() {
            @Override
            public void onSuccess(List<LlmQuestion> questions) {
                runOnUiThread(() -> {
                    Log.d("API_RESPONSE", "Received " + questions.size() + " questions");

                    // Hide ProgressBar
                    progressBar.setVisibility(View.GONE);

                    // Add a question to the list
                    for (LlmQuestion q : questions) {
                        String[] opts = q.options.toArray(new String[0]);
                        Question local = new Question(q.question, opts);

                        // 将 "A"/"B"/"C"/"D" 转为 index 0/1/2/3
                        int correctIndex = correctLetterToIndex(q.correct_answer);
                        if (correctIndex >= 0 && correctIndex < opts.length) {
                            local.correctAnswer = opts[correctIndex];  // 变为文本内容
                        } else {
                            local.correctAnswer = "";
                        }
                        questionList.add(local);
                    }

//                    for (LlmQuestion q : questions) {
//                        Question local = new Question(q.question, q.options.toArray(new String[0]));
//                        local.correctAnswer = q.correct_answer;
//                        questionList.add(local);
//                    }

                    // Update UI
                    questionAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    // Hide ProgressBar
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TaskActivity.this, "Failed to load title: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

        });

    }

    private int getCorrectIndex(Question q) {
        for (int i = 0; i < q.options.length; i++) {
            if (q.options[i].equals(q.correctAnswer)) {
                return i;
            }
        }
        return -1;
    }

    private int correctLetterToIndex(String letter) {
        switch (letter.toUpperCase()) {
            case "A": return 0;
            case "B": return 1;
            case "C": return 2;
            case "D": return 3;
            default: return -1;
        }
    }
}

