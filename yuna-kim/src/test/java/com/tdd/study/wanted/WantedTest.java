package com.tdd.study.wanted;

import java.io.IOException;

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
public class WantedTest {

	private String url;
	private Document doc;
	
	@Before
	public void URL생성() {
		
		String param = "개발";
		WantedUtil w = new WantedUtil();
		w.getUrl(param);
		this.url = w.getSearchUrl();
		
		Assert.assertEquals("https://www.wanted.co.kr/search?query="+param, this.url);
	}
	
	@Test
	public void 문서가져오기() throws IOException{
		WantedUtil w = new WantedUtil();
		w.setSearchUrl(this.url);
		Document doc = w.getDocument();
		
		// html 태그가 있는지 확인??
		Elements el = doc.getElementsByTag("html");
		boolean result = false;
		
		if(el.size() > 0){
			result = true;
		}
		
		Assert.assertEquals(true, result);		
	}
	
	@Test
	public void 제이슨데이터가져오기(){
		WantedUtil w = new WantedUtil();
		w.setSearchUrl(this.url);
		
	}
	
//	@Test
//	public void 웹크롤링() throws IOException {		
//		WantedUtil w = new WantedUtil();
//		Document doc = w.getDocument(this.url);
//		
//		Elements scriptElements = doc.getElementsByTag("script");
//
//		for (Element element :scriptElements ){ 
//
//	        for (DataNode node : element.dataNodes()) {
//	        	
//	        	String data = node.getWholeData();
//	            System.out.println(data.getClass());
//	        }
//	        System.out.println("-------------------");            
//		}
//	}
}
