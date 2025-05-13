package com.example.personallearningapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.personallearningapp.model.Question;
import com.example.personallearningapp.model.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnBack, ivEditImage;
    private ShapeableImageView profilePhoto;
    private TextView tvUsername, tvEmail, tvTotal, tvCorrect, tvIncorrect, tvNotifyText, btnUpgrade;
    private LinearLayout notificationLayout, btnShare;
    private DatabaseHelper dbHelper;
    private int userId;
    private String username, email, avatarUri;
    private int total, correct, incorrect;
    private String copiedImagePath = null;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    profilePhoto.setImageURI(uri);
                    copiedImagePath = copyImageToInternalStorage(uri);
                    dbHelper.updateAvatarUri(userId, copiedImagePath);
                    Toast.makeText(this, "Avatar updated!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Bind views
        btnBack = findViewById(R.id.btnBack);
        profilePhoto = findViewById(R.id.profilePhoto);
        ivEditImage = findViewById(R.id.ivEditImage);
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvTotal = findViewById(R.id.tv_total_number);
        tvCorrect = findViewById(R.id.tv_correct_number);
        tvIncorrect = findViewById(R.id.tv_incorrect_number);

        notificationLayout = findViewById(R.id.tv_notification);
        tvNotifyText = (TextView) notificationLayout.getChildAt(1); // 获取提示文字

        btnShare = findViewById(R.id.btn_share);
        btnUpgrade = findViewById(R.id.btn_upgrade);

        // Intent data
        userId = getIntent().getIntExtra("user_id", -1);
        dbHelper = new DatabaseHelper(this);
        User user = dbHelper.getUserById(userId);
        if (user != null) {
            username = user.username;
            email = user.email;
            avatarUri = user.avatarUri;
        }

        Log.d("PROFILE_STATS", "Id: " + userId);
        Log.d("PROFILE_STATS", "Name: " + username);
        Log.d("PROFILE_STATS", "Email: " + email);

        // Set user info
        tvUsername.setText(username);
        tvEmail.setText(email);
        if (avatarUri != null && !avatarUri.isEmpty()) {
            Glide.with(this).load(avatarUri).into(profilePhoto);
        }

        dbHelper = new DatabaseHelper(this);
        List<String[]> questionStats = dbHelper.getAllQuestionsByUser(userId);
        total = questionStats.size();
        correct = 0;
        for (String[] pair : questionStats) {
            if (pair[0] != null && pair[0].equals(pair[1])) correct++;
        }
        incorrect = total - correct;

        Log.d("PROFILE_STATS", "Total questions answered: " + total);
        Log.d("PROFILE_STATS", "Correct answers: " + correct);
        Log.d("PROFILE_STATS", "Incorrect answers: " + incorrect);

        // Show results
        tvTotal.setText(String.valueOf(total));
        tvCorrect.setText(String.valueOf(correct));
        tvIncorrect.setText(String.valueOf(incorrect));

        // Stats
        if (total == 0) {
            notificationLayout.setVisibility(View.VISIBLE);
            tvNotifyText.setText("You have not finished any questions yet!");
        } else {
            notificationLayout.setVisibility(View.GONE);
        }

        // Go back
        btnBack.setOnClickListener(v -> finish());

        // Share QR
        btnShare.setOnClickListener(v -> {
            String content = "Username: " + username +
                    "\nEmail: " + email +
                    "\n\nTotal Questions: " + total +
                    "\nCorrect Answers: " + correct +
                    "\nIncorrect Answers: " + incorrect;
            Bitmap qrBitmap = generateQRCode(content);
            if (qrBitmap != null) {
                showQrDialog(qrBitmap);
            } else {
                Toast.makeText(this, "Failed to generate QR code.", Toast.LENGTH_SHORT).show();
            }
        });

        // Go to upgrade page
        btnUpgrade.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, UpgradeActivity.class);
            startActivity(intent);
        });

        // Edit profile photo
        ivEditImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
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
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // ========== QR Code Generation ==========
    private Bitmap generateQRCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            Bitmap qr = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    qr.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return qr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showQrDialog(Bitmap qrBitmap) {
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.share_page, null);
        ImageView qrImageView = bottomSheetView.findViewById(R.id.qrImageView);
        qrImageView.setImageBitmap(qrBitmap);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(bottomSheetView);
        dialog.show();
    }

}
