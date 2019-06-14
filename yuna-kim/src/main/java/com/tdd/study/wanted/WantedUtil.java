package com.tdd.study.wanted;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WantedUtil {
	
	private String searchUrl;

	public String getSearchUrl() {
		return searchUrl;
	}

	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}

	public WantedUtil() {
		
	}
	
	/**
	 * 웹크롤링 url 가져오기
	 * @param param
	 */
	public void getUrl(String param){
		UrlContext ctx = new UrlContext(param);
		this.searchUrl = ctx.getUrl();
	}
	
	/**
	 * 웹크롤링으로 html 문서 가져오기
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Document getDocument() throws IOException {
		Document doc = Jsoup.connect(this.searchUrl).get();
		return doc;
	}
	
	/**
	 * json 데이터 가져오기
	 * @param doc
	 * @return
	 */
	public String getJsonData(Document doc){
		
		String jsonStr = null;
		
		Elements scriptElements = doc.getElementsByTag("script");
		
		for (Element element : scriptElements ){ 
	        for (DataNode node : element.dataNodes()) {
	        	String data = node.getWholeData();
	        	if(data.indexOf("{") != -1){
	        		jsonStr = data;
	        	}
	        }        
		}		
		return jsonStr;
	}
	
	/**
	 * tag 리스트 가져오기
	 * @param str
	 * @return
	 * @throws ParseException 
	 */
	public String getTagList(String str) throws Exception{
		
		JSONObject json = null;
		json = new JSONObject(str);
		
		String props = json.getString("props");
		json = new JSONObject(props);
		
		String serverState = json.getString("serverState");
		json = new JSONObject(serverState);
		
		String core = json.getString("core");
		json = new JSONObject(core);
		
		String tag = json.getString("tag");
		json = new JSONObject(tag);
		
		String industries = json.getString("industries");
		
		return industries;
	}
}