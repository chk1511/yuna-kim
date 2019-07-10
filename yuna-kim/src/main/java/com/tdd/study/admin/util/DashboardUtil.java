package com.tdd.study.admin.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class DashboardUtil {

    /**
     * Http 통신
     * @return
     * @throws Exception 
     */
    public String callData(String url) throws Exception{
    	
    	String returnStr = "";
    	OkHttpClient client = new OkHttpClient();
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .build();
        
        Response response = client.newCall(request).execute();
        returnStr = response.body().string();
        
        return returnStr;
    }
    
    /**
     * JSONObject 의 null 값이 있는 지 체크
     * @param jsonObj
     * @param key
     * @return
     * @throws JSONException
     */
    public String JsonObjNullChk(JSONObject jsonObj, String key) throws JSONException{
    	
    	String result = "";
    	
    	if(!jsonObj.isNull(key)){
    		result = jsonObj.getString(key); 
    	}
    	
    	return result;
    }
}