package com.example.itubeapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.*;

import com.example.itubeapp.utils.DatabaseHelper;

public class SignupActivity extends AppCompatActivity {
    EditText etUsername, etPassword, etConfirmPassword, etFullname;
    Button btnCreate;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        db = new DatabaseHelper(this);

        etFullname = findViewById(R.id.etFullname);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreate = findViewById(R.id.btnCreateAccount);

        // Sign up Click
        btnCreate.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String p1 = etPassword.getText().toString();
            String p2 = etConfirmPassword.getText().toString();

            // Password type confirm
            if (!p1.equals(p2)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = db.addUser(username, p1);
            if (ok) {
                Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "User exists!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


