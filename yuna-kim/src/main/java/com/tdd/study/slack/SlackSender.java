package com.tdd.study.slack;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
public class SlackSender {

    private String webHookUrl;
    private String channel;
    private String username;
  
    public SlackSender(String webHookUrl, String channel, String username) {
        this.webHookUrl = webHookUrl;
        this.channel = channel;
        this.username = username;
    }

    public void send() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("channel", channel);
        jsonObject.put("text", "Test");

        RequestBody body = RequestBody.create(
                mediaType,
                jsonObject.toJSONString());

        Request request = new Request.Builder()
                .url(webHookUrl)
                .post(body)
                .addHeader("accept", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
