package com.tdd.study.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tdd.study.admin.model.DashboardModel;
import com.tdd.study.admin.util.DashboardUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DashboardService {
	
	@Autowired
	private DashboardUtil dashboardUtil;
	
	/**
	 * 리스트
	 * @return
	 * @throws Exception
	 */
	public List<DashboardModel> search(String query, String productType) throws Exception{
		
		List<DashboardModel> list = new ArrayList<>();
		
		if(productType == null){
			list = getAll(list, query);
			return list;
		}
		
		// 1 : 원티드, 2 : 프로그래머스, 3 : 로켓펀치
		switch (productType) {
		case "ALL" :
				list = getAll(list, query);
			break;
		case "1" :
				list = getWanted(list, query);
			break;
		case "2" :
				list = getProgrammers(list, query);
			break;
		case "3" :
				list = getRocketPunch(list, query);
			break;
		default:
				list = getAll(list, query);
			break;
		}
		
		return list;
	}
	
	/**
	 * 모든 사이트의 데이터
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List<DashboardModel> getAll(List<DashboardModel> list, String query) throws Exception{
		
		list = getWanted(list, query);
		list = getProgrammers(list, query);
		list = getRocketPunch(list, query);
		
		return list;
	}

	/**
	 * 원티드 데이터
	 * @return
	 * @throws Exception 
	 */
	public List<DashboardModel> getWanted(List<DashboardModel> list, String query) throws Exception{
		
        String url = "https://www.wanted.co.kr/api/v4/search?1560515032799&job_sort=job.latest_order&locations=all&years=-1&country=kr&query="+ query;
        
        String returnStr = this.dashboardUtil.callData(url);
        
        JSONObject json = new JSONObject(returnStr);
        json = (JSONObject) json.get("data");
        
        JSONArray arr = new JSONArray();
        arr = (JSONArray) json.getJSONArray("jobs");
        
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
        	
        	model.setProductType("원티드");
        	
        	list.add(model);
        }

        return list;
	}
	
	/**
	 * 프로그래머스 데이터
	 * @return
	 * @throws Exception 
	 */
	public List<DashboardModel> getProgrammers(List<DashboardModel> list, String query) throws Exception{
		
		String url = "https://programmers.co.kr/job";
		
		Document doc = Jsoup.connect(url).get();
		Elements els = doc.getElementsByClass("item-body");
		
		DashboardModel model;
		int id;
		String position;
		String companyName;
		String location;
		String experience;
		
		for(Element el : els){
			
			model = new DashboardModel();
			
			Elements e = el.getElementsByTag("a");
			String href = e.get(0).attr("href");
			String[] hrefArr = href.split("/");
			
			id = Integer.parseInt(hrefArr[hrefArr.length -1]);
			position = e.text();
			model.setId(id);
			model.setPosition(position);
			
			companyName = el.getElementsByClass("company-name").text();
			model.setCompanyName(companyName);
									
			location = el.getElementsByClass("location").text();
			model.setLocation(location);
			
			experience = el.getElementsByClass("experience").text();
			model.setExperience(experience);
			
			model.setProductType("프로그래머스");
			
			list.add(model);
		}
		
		return list;
	}
	
	/**
	 * 프로그래머스 데이터
	 * @return
	 * @throws Exception 
	 */
	public List<DashboardModel> getRocketPunch(List<DashboardModel> list, String query) throws Exception{
		
		String url = "https://www.rocketpunch.com/api/jobs/template?keywords="+query;
				        
		String returnStr = this.dashboardUtil.callData(url);
        
        JSONObject json = new JSONObject(returnStr);
        json = (JSONObject) json.get("data");
        String template = json.getString("template");
        
        Document doc = Jsoup.parse(template);
        Elements els = doc.getElementsByClass("company active job-ad-group item");
		
        DashboardModel model;
		int id;
		String position;
		String companyName;
		String experience;
		String dueTime;
        
        for(Element el : els){
        	
        	model = new DashboardModel();
        	
        	Element e = el.getElementsByClass("company-name").get(0);
        	companyName = e.getElementsByTag("strong").get(0).text();
        	model.setCompanyName(companyName);
        	
        	experience = el.getElementsByClass("job-stat-info").get(0).text();
        	
        	// 경력 데이터가 일정한 규칙이 없음
        	if(experience.contains("/")){
        		experience = experience.split("/")[1].trim();
        		model.setExperience(experience);
        	}
        	
        	e = el.getElementsByClass("company-jobs-detail").get(0);
        	e = e.getElementsByTag("a").get(0);
        	id = Integer.parseInt(e.attr("data-job_id"));
        	model.setId(id);
        	
        	position = e.text();
        	model.setPosition(position);
        	
        	e = el.getElementsByClass("job-dates").get(0);
        	dueTime = e.getElementsByTag("span").get(0).text();
        	model.setDueTime(dueTime);
        	
        	model.setProductType("로켓펀치");
        	
        	list.add(model);
        }
        
		return list;
	}
}