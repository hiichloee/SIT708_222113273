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

        // Initial
        tvTaskTitle = findViewById(R.id.tvTask_Title);
        tvTaskDesc = findViewById(R.id.tvTask_Desc);
        recyclerView = findViewById(R.id.newsRecycler);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        taskTitle = intent.getStringExtra("taskTitle");
        taskDesc = intent.getStringExtra("taskDesc");
        username = intent.getStringExtra("username");
        avatarUri = intent.getStringExtra("avatar_uri");

        tvTaskTitle.setText(taskTitle);
        tvTaskDesc.setText(taskDesc);

//        questionList = getDummyQuestions(taskTitle);
        questionAdapter = new QuestionAdapter(questionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(questionAdapter);

        // Getting topics from the LLM API
        fetchQuestionsFromLLM(taskTitle);

        Log.d("------- TaskActivity", "Topic for API request: " + taskTitle);

        // TaskActivity ➝ ResultActivity
        //     (putExtra "questions", "username", "avatar_uri", "taskTitle", "taskDesc")
        btnSubmit.setOnClickListener(v -> {

            for (Question q : questionList) {
                if (q.selectedOption != -1 && q.correctAnswer != null) {
                    String selected = q.options[q.selectedOption];
                    if (selected.equals(q.correctAnswer)) {
                        q.response = "✅ Correct! Well done!";
                    } else {
                        q.response = "❌ Incorrect. Correct answer is: " + q.correctAnswer;
                    }
                } else {
                    q.response = "❌ No option selected.";
                }

                // 判断有选就给予正向反馈，否则给予随机其他反馈
//                q.response = (q.selectedOption != -1)
//                        ? getRandomResponse()
//                        : "❌ Please review this concept.";
            }

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

//    // Dummy Question
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
                        Question local = new Question(q.question, q.options.toArray(new String[0]));
                        local.correctAnswer = q.correct_answer;
                        questionList.add(local);
                    }
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


}



// Dummy Question
//    private List<Question> getDummyQuestions(String taskTitle) {
//        List<Question> questions = new ArrayList<>();
//
//        // 根据task生成
//        switch (taskTitle) {
//            case "Algorithms":
//                questions.add(new Question("What is dynamic programming?", new String[]{"Divide and conquer", "Memoization", "Recursion"}));
//                questions.add(new Question("Which algorithm finds shortest paths?", new String[]{"Dijkstra", "DFS", "BFS"}));
//                break;
//
//            case "Data Structures":
//                questions.add(new Question("What is a Linked List?", new String[]{"Sequential memory", "Nodes with pointers", "Stack structure"}));
//                questions.add(new Question("Which is LIFO?", new String[]{"Queue", "Array", "Stack"}));
//                questions.add(new Question("What data structure uses priority?", new String[]{"Heap", "Graph", "HashMap"}));
//                break;
//
//            case "AI":
//                questions.add(new Question("What is supervised learning?", new String[]{"With labeled data", "No data", "Only rules"}));
//                questions.add(new Question("What is a neural network?", new String[]{"Human brain", "Mathematical model", "Flowchart"}));
//                questions.add(new Question("Which algorithm is used in classification?", new String[]{"K-Means", "SVM", "PCA"}));
//                questions.add(new Question("What is reinforcement learning?", new String[]{"Self-play", "Dataset", "Random"}));
//                break;
//
//            case "Android":
//                questions.add(new Question("What is an Activity in Android?", new String[]{"Main screen component", "Database", "Style"}));
//                questions.add(new Question("Which language is used for Android?", new String[]{"Swift", "Java/Kotlin", "PHP"}));
//                break;
//
//            case "Security":
//                questions.add(new Question("What does HTTPS stand for?", new String[]{"Secure Protocol", "HyperText Secure", "Hyper Transfer Secure"}));
//                questions.add(new Question("Which is a hashing algorithm?", new String[]{"SHA-256", "AES", "RSA"}));
//                questions.add(new Question("What's SQL Injection?", new String[]{"Attack vector", "Database column", "Server hardware"}));
//                break;
//
//            case "Web Dev":
//                questions.add(new Question("What is HTML used for?", new String[]{"Page content", "Styling", "Backend"}));
//                questions.add(new Question("CSS handles what?", new String[]{"Structure", "Design", "Data logic"}));
//                questions.add(new Question("Which language runs in browser?", new String[]{"Java", "Python", "JavaScript"}));
//                break;
//
//            case "Testing":
//                questions.add(new Question("What is unit testing?", new String[]{"Tests individual parts", "Tests server", "Tests database"}));
//                questions.add(new Question("Which is automated testing tool?", new String[]{"JUnit", "Postman", "Notepad"}));
//                questions.add(new Question("Integration testing checks?", new String[]{"Interaction between modules", "GUI", "RAM"}));
//                questions.add(new Question("What is TDD?", new String[]{"Test Driven Dev", "Typed Debug Dev", "Template Data Design"}));
//                break;
//
//            case "Cloud":
//                questions.add(new Question("What is IaaS?", new String[]{"Infrastructure as Service", "Internet at School", "Internet as App"}));
//                questions.add(new Question("Which is cloud provider?", new String[]{"AWS", "Android", "Photoshop"}));
//                questions.add(new Question("What is scalability?", new String[]{"App gets bigger", "Test faster", "Code refactor"}));
//                break;
//
//            case "UI/UX":
//                questions.add(new Question("What does UX mean?", new String[]{"User Experience", "User Extend", "Unified Extension"}));
//                questions.add(new Question("Which tool is for UI design?", new String[]{"Figma", "Excel", "MySQL"}));
//                break;
//
//            case "Databases":
//                questions.add(new Question("What is SQL?", new String[]{"Structured Query Lang", "Server Quality Logic", "Stack Query Lang"}));
//                questions.add(new Question("Which is a relational DB?", new String[]{"MySQL", "MongoDB", "Redis"}));
//                questions.add(new Question("What is normalization?", new String[]{"Reduce redundancy", "Encrypt rows", "Duplicate data"}));
//                break;
//
//            default:
//                questions.add(new Question("Default question", new String[]{"A", "B", "C"}));
//        }
//
//        // 为每题随机附加回答
//        for (Question q : questions) {
//            q.response = getRandomResponse();
//        }
//
//        return questions;
//    }
//
//    private String getRandomResponse() {
//        String[] responses = {
//                "✅ Well done! You clearly understand this.",
//                "🤔 Not bad, but review this one again.",
//                "❌ Incorrect. Take another look at this topic.",
//                "🧠 You're getting there!",
//                "✨ Nice! You're on track."
//        };
//        return responses[new Random().nextInt(responses.length)];
//    }