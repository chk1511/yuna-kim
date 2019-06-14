package com.tdd.study.wanted;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import net.minidev.json.parser.JSONParser;

@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WantedTest {

	private String url;
	private Document doc;
	private WantedUtil w;
	
	public void 문서가져오기() throws IOException{
		
		this.w.setSearchUrl(this.url);
		this.doc = w.getDocument();
		
		// html 태그가 있는지 확인??
		Elements el = doc.getElementsByTag("html");
		boolean result = false;
		
		if(el.size() > 0){
			result = true;
		}
		
		Assert.assertEquals(true, result);	
	}
	
	@Before
	public void URL생성() throws IOException {
		
		String param = "개발";
		this.w =  new WantedUtil();
		w.getUrl(param);
		this.url = w.getSearchUrl();
		
		Assert.assertEquals("https://www.wanted.co.kr/search?query="+param, this.url);
		
		문서가져오기();
	}	
	
	@Test
	public void A_제이슨데이터가져오기() throws Exception{
		
		if(this.doc == null){
			throw new Exception("문서생성 실패");
		}
		
		String jsonStr = this.w.getJsonData(this.doc);
		System.out.println(jsonStr);
		
		// 태그 리스트 가져오기
		String str = this.w.getTagList(jsonStr);
		
		System.out.println(str);
	}
}