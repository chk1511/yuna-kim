package com.tdd.study.admin;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/admin")
public class DashboardController {

    @GetMapping(value = "dashboard")
    public String index() {
        return "dashboard/dashboard";
    }

    @RequestMapping(value="search")
    public @ResponseBody String
    search(@RequestParam(value = "query") String query){

//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity("https://www.wanted.co.kr/api/v4/search?1560515032799&job_sort=job.latest_order&locations=all&years=-1&country=kr&query=%EA%B0%9C%EB%B0%9C", Object[].class);
//        Object[] objects = responseEntity.getBody();
//        MediaType contentType = responseEntity.getHeaders().getContentType();
//        HttpStatus statusCode = responseEntity.getStatusCode();
//        log.info("log::::::{}", objects);

        String returnStr = "";
        OkHttpClient client = new OkHttpClient();
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");

        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("username", username);
//        jsonObject.put("channel", channel);
//        jsonObject.put("text", "Test");

        Request request = new Request.Builder()
                .url("https://www.wanted.co.kr/api/v4/search?1560515032799&job_sort=job.latest_order&locations=all&years=-1&country=kr&query="+ query)
                .get()
                .addHeader("accept", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
//            log.info("log::::::{}", response.body().string());
            returnStr = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnStr;
    }
}
