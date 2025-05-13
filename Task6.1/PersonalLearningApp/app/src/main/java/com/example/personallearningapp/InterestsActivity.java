package com.example.personallearningapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> interests;
    private List<String> selectedInterests = new ArrayList<>();
    private Button btnNext;
    private DatabaseHelper dbHelper;
    private InterestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        recyclerView = findViewById(R.id.recyclerViewInterests);
        btnNext = findViewById(R.id.btnNext);
        dbHelper = new DatabaseHelper(this);

        interests = Arrays.asList(
                "Algorithms", "Data Structures", "AI", "Android", "Security",
                "Web Dev", "Testing", "Cloud", "UI/UX", "Databases");

        // Dynamically Generated Interest Buttons 动态生成兴趣按钮
        // 设置 FlexboxLayoutManager  每个按钮宽度 = 根据文字长度变化
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP); // 允许换行
        layoutManager.setJustifyContent(JustifyContent.FLEX_START); // 左对齐
        layoutManager.setAlignItems(AlignItems.FLEX_START); // 顶部对齐

        recyclerView.setLayoutManager(layoutManager);

        adapter = new InterestAdapter(interests, selectedInterests);
        recyclerView.setAdapter(adapter);

        // Next button handling
        btnNext.setOnClickListener(v -> {
            // At least one interest
            if (selectedInterests.isEmpty()) {
                Toast.makeText(this, "Select at least 1 interest", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = getLastUserId();
            if (userId == -1) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Getting information on registration 获取注册时的信息
            String username = getUserFieldById(userId, "username");
            String email = getUserFieldById(userId, "email");
            String password = getUserFieldById(userId, "password");
            String phone = getUserFieldById(userId, "phone");
            String avatarUri = getUserFieldById(userId, "avatar_uri");


            // Insert Interest into user table
            for (String interest : selectedInterests) {
                dbHelper.getWritableDatabase().execSQL(
                        "INSERT INTO interests (user_id, interest) VALUES (?, ?)",
                        new Object[]{userId, interest});
            }

            // InterestsActivity ➝ DashboardActivity
            //     (putExtra "userId", "username", "avatar_uri", "interests (task title)")
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("username", username);
            intent.putExtra("avatar_uri", avatarUri);
            intent.putStringArrayListExtra("interests", new ArrayList<>(selectedInterests));

            startActivity(intent);
            finish();
        });

    }
    private int getSuggestedColumnCount(List<String> items) {
        int totalWidth = getResources().getDisplayMetrics().widthPixels;
        int paddingPerItem = 80; // padding + margin + space per item (approx)

        int longest = 0;
        for (String s : items) {
            if (s.length() > longest) longest = s.length();
        }

        // 根据最长项估算每个按钮的宽度
        int estimatedItemWidth = (int) (longest * 20) + paddingPerItem;
        return Math.max(2, Math.min(3, totalWidth / estimatedItemWidth));
    }

    // Get the latest registered user ID 获取最新注册的用户 ID
    private int getLastUserId() {
        int userId = -1;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT MAX(id) as id FROM users", null);
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            cursor.close();
        }
        return userId;
    }

    // Get a field (username, avatar URI, email, password, mobile number) based on userId 根据 userId 获取某个字段 （用户名、头像URI、邮箱、密码、手机号）
    private String getUserFieldById(int userId, String fieldName) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT " + fieldName + " FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            String value = c.getString(0);
            c.close();
            return value;
        }
        return "";
    }

}