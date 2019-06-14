package com.tdd.study.wanted;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
	
//	public String getJsonData(Document doc){
//		
//	}
}