package com.example.itubeapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class PlayerActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Get the incoming YouTube url
        String url = getIntent().getStringExtra("url");
        webView = findViewById(R.id.webView);

        // Enable JS
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        // Get the video ID and embed the iframe URL
        String videoId = extractVideoId(url);
        webView.loadUrl("https://www.youtube.com/embed/" + videoId);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.clearHistory();
            webView.clearCache(true);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    // Get YouTube ID
    private String extractVideoId(String url) {
        if (url.contains("v=")) {
            return url.substring(url.indexOf("v=") + 2);
        } else if (url.contains("youtu.be/")) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return url;
    }
}
