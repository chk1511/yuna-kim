package com.tdd.study.wanted;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlContext {

//	@Value("${wanted.search.url}")
//	private String searchUrl;
	private String searchUrl = "https://www.wanted.co.kr/search?query=";
	
	private String param;
	
	public UrlContext() {
		
	}
	
	public UrlContext(String param) {
		this.param = param;
	}
	
	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.searchUrl);
		sb.append(this.param);
		return sb.toString();
	}
}
