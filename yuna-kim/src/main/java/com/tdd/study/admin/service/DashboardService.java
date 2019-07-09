package com.tdd.study.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xmlunit.util.Nodes;

import com.tdd.study.admin.model.DashboardModel;
import com.tdd.study.admin.util.DashboardUtil;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@Service
public class DashboardService {
	
	@Autowired
	private DashboardUtil dashboardUtil;
	
	public static void main(String[] args) throws Exception{
		
		getProgrammers("개발");
	}

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
        	model.setLocation(this.dashboardUtil.JsonObjNullChk(json, "location"));
        	
        	json = obj.getJSONObject("company");
        	model.setCompanyName(this.dashboardUtil.JsonObjNullChk(json, "name"));
        	model.setIndustryName(this.dashboardUtil.JsonObjNullChk(json, "industry_name"));
        	
        	model.setPosition(this.dashboardUtil.JsonObjNullChk(obj, "position"));
        	model.setDueTime(this.dashboardUtil.JsonObjNullChk(obj, "due_time"));
        	
        	list.add(model);
        }

        return list;
	}
	
	/**
	 * 프로그래머스 데이터
	 * @return
	 * @throws Exception 
	 */
	public static List<DashboardModel> getProgrammers(String query) throws Exception{
		
		String url = "https://programmers.co.kr/job";
		
		Document doc = Jsoup.connect(url).get();
		Elements els = doc.getElementsByClass("item-body");
		
		DashboardModel model;
		int id;
		String location;
		String experience;
		
		for(Element el : els){
			
			model = new DashboardModel();
			
			Elements e = el.getElementsByTag("a");
			String href = e.get(0).attr("href");
			String[] hrefArr = href.split("/");
			id = Integer.parseInt(hrefArr[hrefArr.length -1]);
			model.setId(id);
						
			location = el.getElementsByClass("location").text();
			model.setLocation(location);
			
			experience = el.getElementsByClass("experience").text();
			model.setExperience(experience);
			
			System.out.println(id);
			System.out.println(location);
			System.out.println(experience);
		}
		
		
		return new ArrayList<>();
	}
}