package com.tdd.study.admin.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class DashboardUtil {
    
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