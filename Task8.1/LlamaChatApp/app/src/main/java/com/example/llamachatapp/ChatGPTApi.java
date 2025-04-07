package com.example.llamachatapp;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class ChatGPTApi {
    private static final String API_URL = "https://xiaoai.plus/v1/chat/completions";  // 必须由Pixel 5手机来操作
//    private static final String API_URL = "https://api.openai.com/v1/chat/completions"; 
    private static final String API_KEY = "sk-M0Xvk7Cz2dLZZQGcNYg1tXUtpYT0PEAC8sWA4Qpss9E3Ej6F";

    public interface Callback {
        void onSuccess(String reply);
        void onFailure(String error);
    }

    public static void sendMessageToChatGPT(String userMessage, Callback callback) {
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "");
        System.setProperty("https.proxyHost", "");
        System.setProperty("https.proxyPort", "");

        OkHttpClient client = new OkHttpClient.Builder()
                .dns(Dns.SYSTEM)
                .build();

        try {
            JSONObject json = new JSONObject();
            json.put("model", "gpt-3.5-turbo");

            JSONArray messages = new JSONArray();
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.put(userMsg);

            json.put("messages", messages);

            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() == 429) {
                        callback.onFailure("Rate limit exceeded. Please wait and try again.");
                        return;
                    }
                    if (response.isSuccessful()) {
                        String resStr = response.body().string();
                        try {
                            JSONObject obj = new JSONObject(resStr);
                            JSONArray choices = obj.getJSONArray("choices");
                            String reply = choices.getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");
                            callback.onSuccess(reply.trim());
                        } catch (Exception e) {
                            callback.onFailure("Parse error");
                        }
                    } else {
                        callback.onFailure("API Error: " + response.code());
                    }
                }
            });

        } catch (Exception e) {
            callback.onFailure("Exception: " + e.getMessage());
        }
    }
}
