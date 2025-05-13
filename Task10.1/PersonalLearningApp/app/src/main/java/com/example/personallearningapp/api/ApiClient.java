package com.example.personallearningapp.api;

import android.net.Uri;
import android.util.Log;

import com.example.personallearningapp.model.LlmQuestion;
import com.example.personallearningapp.model.LlmQuizResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClient {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)  // 设置连接超时
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)     // 设置读取超时
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)    // 设置写入超时
            .build();
    private static final String BASE_URL = "http://10.0.2.2:5000"; // 模拟器访问本地 Flask 用 10.0.2.2


    public interface OnQuizResponse {
        void onSuccess(List<LlmQuestion> questions);
        void onFailure(String errorMessage);
    }

    public static void fetchQuizFromLLM(String topic, OnQuizResponse callback) {
        String url = BASE_URL + "/getQuiz?topic=" + Uri.encode(topic);
        Log.d("------- API_REQUEST", "Request URL: " + url);
        Request request = new Request.Builder().url(url).get().build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Server error: " + response.code());
                    return;
                }

                String json = response.body().string();
                try {
                    LlmQuizResponse quiz = new Gson().fromJson(json, LlmQuizResponse.class);
                    callback.onSuccess(quiz.quiz);
                } catch (Exception e) {
                    callback.onFailure("Parse error: " + e.getMessage());
                }
            }
        });
    }
}

