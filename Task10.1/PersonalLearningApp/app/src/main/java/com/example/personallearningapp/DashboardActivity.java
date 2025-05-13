package com.example.personallearningapp;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personallearningapp.model.Task;
import com.example.personallearningapp.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private ImageView relatedImage;
    private DatabaseHelper dbHelper;
    private int userId;
    private String username, email, avatarPath; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);

        TextView tvName = findViewById(R.id.tvName);
        relatedImage = findViewById(R.id.relatedImage);
        recyclerView = findViewById(R.id.newsRecycler);

        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);

        dbHelper = new DatabaseHelper(this);
        User user = dbHelper.getUserById(userId);
        if (user != null) {
            username = user.username;
            email = user.email;
            avatarPath = user.avatarUri;
        } else {
            Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (avatarPath != null) {
            File avatarFile = new File(avatarPath);
            Glide.with(this).load(avatarFile).into(relatedImage);
            relatedImage.setImageURI(Uri.parse(avatarPath));
        } else {
            // Set a default avatar to prevent crash
            relatedImage.setImageResource(R.drawable.default_avatar_test);
        }

        tvName.setText(username);

        // Back to Dashboard
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // 初始化 taskList
        taskList = new ArrayList<>();
        for (Task task : getDummyTasks(userId)) {
            if (dbHelper.isTaskCompleted(userId, task.title)) {
                task.status = "✔ Completed"; // 标记完成 mark complete
            }
            taskList.add(task);
        }

        Log.d("DASHBOARD_TASKS", "Generated taskList size: " + taskList.size());
        // 决定是否显示空页面提示
        TextView tvEmpty = findViewById(R.id.tvEmptyMessage);
        if (taskList == null || taskList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        taskAdapter = new TaskAdapter(taskList, task -> {
            Intent taskIntent = new Intent(this, TaskActivity.class);
            taskIntent.putExtra("user_id", userId);
            taskIntent.putExtra("taskTitle", task.title);
            taskIntent.putExtra("taskDesc", task.description);
            taskIntent.putExtra("username", username);
            taskIntent.putExtra("avatar_uri", avatarPath);
            startActivity(taskIntent);
        }, userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 40; // Task_card margin bottom setting
            }
        });

        relatedImage.setOnClickListener(v -> {
            Intent profileIntent = new Intent(DashboardActivity.this, ProfileActivity.class);
            profileIntent.putExtra("user_id", userId);
            startActivity(profileIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        User user = dbHelper.getUserById(userId);
        if (user != null) {
            username = user.username;
            email = user.email;
            avatarPath = user.avatarUri;

            TextView tvName = findViewById(R.id.tvName);
            tvName.setText(username);

            if (avatarPath != null && !avatarPath.isEmpty()) {
                File avatarFile = new File(avatarPath);
                Glide.with(this).load(avatarFile).into(relatedImage);
            } else {
                relatedImage.setImageResource(R.drawable.default_avatar_test);
            }
        }
    }
    // Dummy Task
    private List<Task> getDummyTasks(int userId) {
        List<Task> list = new ArrayList<>();
        Log.d("DASHBOARD", "Loaded user_id: " + userId);
        Log.d("DASHBOARD", "Loaded username: " + username);
        Log.d("DASHBOARD", "Loaded email: " + email);
        List<String> interests = dbHelper.getInterestsForUser(userId);
        Log.d("DEBUG_INTERESTS", "user_id = " + userId + ", interests = " + interests);

        for (String interest : interests) {
            // 根据兴趣生成
            switch (interest) {
                case "Algorithms":
                    list.add(new Task("Algorithms", "Understand the fundamentals of problem solving techniques."));
                    break;
                case "Data Structures":
                    list.add(new Task("Data Structures", "Learn how to organize and manage data efficiently."));
                    break;
                case "AI":
                    list.add(new Task("AI", "Explore the basics of machine learning and artificial intelligence."));
                    break;
                case "Android":
                    list.add(new Task("Android", "Learn how to build apps for Android devices."));
                    break;
                case "Security":
                    list.add(new Task("Security", "Understand cyber threats and data protection."));
                    break;
                case "Web Dev":
                    list.add(new Task("Web Dev", "Build responsive websites using HTML, CSS and JS."));
                    break;
                case "Testing":
                    list.add(new Task("Testing", "Learn software testing methods and frameworks."));
                    break;
                case "Cloud":
                    list.add(new Task("Cloud", "Understand cloud services and infrastructure models."));
                    break;
                case "UI/UX":
                    list.add(new Task("UI/UX", "Design smooth and intuitive user experiences."));
                    break;
                case "Databases":
                    list.add(new Task("Databases", "Learn SQL and manage data with relational databases."));
                    break;
            }
        }
        return list;
    }

}
