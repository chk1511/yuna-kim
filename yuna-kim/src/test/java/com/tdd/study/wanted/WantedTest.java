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
	
	@Before
	public void URL생성() {
		
		String param = "개발";
		UrlContext ctx = new UrlContext(param);
		String url = ctx.getUrl();
		
		Assert.assertEquals("https://www.wanted.co.kr/search?query="+param, url);
		
		this.url = url;
	}
	
	@Test
	public void 웹크롤링() throws IOException {		
		Wanted w = new Wanted();
		Document doc = w.getDocument("https://www.wanted.co.kr/");
				
		Elements scriptElements = doc.getElementsByTag("script");

		for (Element element :scriptElements ){ 

	        for (DataNode node : element.dataNodes()) {
	        	
	            System.out.println(node.getWholeData());
	        }
	        System.out.println("-------------------");            
		}
	}
}
