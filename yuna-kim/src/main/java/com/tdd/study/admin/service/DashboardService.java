package com.tdd.study.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.tdd.study.admin.model.DashboardModel;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@Service
public class DashboardService {

	/**
	 * 원티드 데이터
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unused")
	public List<DashboardModel> getWanted(String query) throws Exception{
		
		String returnStr = "";
        OkHttpClient client = new OkHttpClient();
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");

        Request request = new Request.Builder()
                .url("https://www.wanted.co.kr/api/v4/search?1560515032799&job_sort=job.latest_order&locations=all&years=-1&country=kr&query="+ query)
                .get()
                .addHeader("accept", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        returnStr = response.body().string();
        log.debug(returnStr);
        
        JSONObject json = new JSONObject(returnStr);
        json = (JSONObject) json.get("data");
        
        JSONArray arr = new JSONArray();
        arr = (JSONArray) json.getJSONArray("jobs");
        
        List<DashboardModel> list = new ArrayList<>();
        
        for(int i=0; i<arr.length(); i++){
        	
        	DashboardModel model = new DashboardModel();      
        	JSONObject obj = arr.getJSONObject(i);
        	
        	model.setId(obj.getInt("id"));
        	
        	json = obj.getJSONObject("address");    
        	model.setLocation(nullCheck(json, "location"));
        	
        	json = obj.getJSONObject("company");
        	model.setCompanyName(nullCheck(json, "name"));
        	model.setIndustryName(nullCheck(json, "industry_name"));
        	
        	model.setPosition(nullCheck(obj, "position"));
        	model.setDueTime(nullCheck(obj, "due_time"));
        	
        	list.add(model);
        }

        return list;
	}
    
    /**
     * JSONObject 의 null 값이 있는 지 체크
     * @param jsonObj
     * @param key
     * @return
     * @throws JSONException
     */
    public String nullCheck(JSONObject jsonObj, String key) throws JSONException{
    	
    	String result = "";
    	
    	if(!jsonObj.isNull(key)){
    		result = jsonObj.getString(key); 
    	}
    	
    	return result;
    }
}