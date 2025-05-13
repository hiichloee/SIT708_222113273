package com.example.personallearningapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivProfile;
    private EditText etUsername, etEmail, etConfirmEmail, etPassword, etConfirmPassword, etPhone;
    private DatabaseHelper dbHelper;
    private String copiedImagePath = null;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    ivProfile.setImageURI(uri);

                    copiedImagePath = copyImageToInternalStorage(uri); // Automatically copy and save the path
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        ivProfile = findViewById(R.id.ivProfile);
        ImageView ivAddImage = findViewById(R.id.ivAddImage);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etConfirmEmail = findViewById(R.id.etConfirmEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        Button btnRegister = findViewById(R.id.btnRegister);

        ivAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String confirmEmail = etConfirmEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            String phone = etPhone.getText().toString();

            // Check list
            if (etUsername == null) { Toast.makeText(this, "Please create a username", Toast.LENGTH_SHORT).show(); return; }
            if (etEmail == null) { Toast.makeText(this, "Please input your email", Toast.LENGTH_SHORT).show(); return; }
            if (etPassword == null) { Toast.makeText(this, "Please input your password", Toast.LENGTH_SHORT).show(); return; }
            if (etPhone == null) { Toast.makeText(this, "Please input your phone number", Toast.LENGTH_SHORT).show(); return; }
            if (copiedImagePath == null) { Toast.makeText(this, "Please select an avatar image", Toast.LENGTH_SHORT).show(); return; }
            if (!email.equals(confirmEmail) || !password.equals(confirmPassword)) {
                Toast.makeText(this, "Email or Password does not match", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.registerUser(username, email, password, phone, copiedImagePath);
            if (success) {
                int userId = dbHelper.getUserIdByUsername(username);  // 新建一个方法来根据 username 拿 userId

                Intent intent = new Intent(this, InterestsActivity.class);
                intent.putExtra("user_id", userId);  // 明确传入 userId

                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String copyImageToInternalStorage(Uri sourceUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            File file = new File(getFilesDir(), "avatar_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath(); // 用于数据库保存
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
