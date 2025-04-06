package com.example.itubeapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.*;
import android.database.sqlite.SQLiteDatabase;

import com.example.itubeapp.utils.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnSignup;
    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Loading Login UI
        db = new DatabaseHelper(this);  // Initial Database

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        Button btnClearAll = findViewById(R.id.btnClearAll);

        // Login Click
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (db.checkUser(username, password)) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("username", username);
                // Login successfully, jump to Home page
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid login!", Toast.LENGTH_SHORT).show();
            }
        });

        // Register Click
        btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignupActivity.class));
        });

        // Clear Click
        btnClearAll.setOnClickListener(v -> {
            SQLiteDatabase dbHelper = db.getWritableDatabase();
            int deletedUsers = dbHelper.delete(DatabaseHelper.TABLE_USERS, null, null);
            int deletedVideos = dbHelper.delete(DatabaseHelper.TABLE_PLAYLIST, null, null);
            Toast.makeText(this,
                    "Clear successful: " + deletedUsers + " users, " + deletedVideos + " videos.",
                    Toast.LENGTH_SHORT).show();
        });

    }
}