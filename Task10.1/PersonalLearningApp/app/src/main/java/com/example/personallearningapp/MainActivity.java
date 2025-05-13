package com.example.personallearningapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personallearningapp.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnClearAll = findViewById(R.id.btnClearAll);
        TextView tvRegister = findViewById(R.id.tvRegister);

        // MainActivity ➝ DashboardActivity
        //     (putExtra "username", "avatar_uri")
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            Cursor cursor = dbHelper.loginUser(username, password);
            if (cursor.moveToFirst()) {

                String avatarUri = cursor.getString(cursor.getColumnIndexOrThrow("avatar_uri"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

                // 登录验证通过后
                int userId = getUserIdByUsername(username);
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("username", username);
                intent.putExtra("avatar_uri", avatarUri);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });

        btnClearAll.setOnClickListener(v -> {
            dbHelper.clearAllUsers();
            Toast.makeText(this, "All test data cleared", Toast.LENGTH_SHORT).show();
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private int getUserIdByUsername(String username) {
        int id = -1;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }
}
