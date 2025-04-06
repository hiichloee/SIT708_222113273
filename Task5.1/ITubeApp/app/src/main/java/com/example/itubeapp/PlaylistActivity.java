package com.example.itubeapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Paint;
import android.content.Intent;

import com.example.itubeapp.utils.DatabaseHelper;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    LinearLayout playlistLayout;
    DatabaseHelper db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlistLayout = findViewById(R.id.playlistLayout);
        username = getIntent().getStringExtra("username");

        db = new DatabaseHelper(this);

        // Get the user’s all video links
        List<String> videos = db.getVideos(username);

        // Display each video link
        for (String link : videos) {
            TextView tv = new TextView(this);
            tv.setText(link);
            tv.setPadding(16, 16, 16, 16);
            tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 15, 0, 15);
            tv.setLayoutParams(params);

            // Jump to the player page
            tv.setOnClickListener(v -> {
                Intent intent = new Intent(PlaylistActivity.this, PlayerActivity.class);
                intent.putExtra("url", link);
                startActivity(intent);
            });

            playlistLayout.addView(tv);
        }


    }
}
