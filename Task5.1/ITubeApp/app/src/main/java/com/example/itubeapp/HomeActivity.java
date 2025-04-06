package com.example.itubeapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.*;

import com.example.itubeapp.utils.DatabaseHelper;

public class HomeActivity extends AppCompatActivity {
    EditText etUrl;
    Button btnPlay, btnAdd, btnPlaylist;
    String username;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        etUrl = findViewById(R.id.etUrl);
        btnPlay = findViewById(R.id.btnPlay);
        btnAdd = findViewById(R.id.btnAddToPlaylist);
        btnPlaylist = findViewById(R.id.btnMyPlaylist);
        username = getIntent().getStringExtra("username");

        db = new DatabaseHelper(this);

        // Play Click
        btnPlay.setOnClickListener(v -> {
            String url = etUrl.getText().toString();
            startActivity(new Intent(this, PlayerActivity.class).putExtra("url", url));
        });

        // Adding a url to a playlist
        btnAdd.setOnClickListener(v -> {
            String url = etUrl.getText().toString();
            db.addVideo(username, url);
            Toast.makeText(this, "Added to playlist", Toast.LENGTH_SHORT).show();
        });

        // Check playlist
        btnPlaylist.setOnClickListener(v -> {
            startActivity(new Intent(this, PlaylistActivity.class).putExtra("username", username));
        });
    }

}
