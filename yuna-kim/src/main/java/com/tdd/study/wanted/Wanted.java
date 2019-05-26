package com.tdd.study.wanted;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Wanted {

	public Wanted() {
		
	}
	
	public Document getDocument(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		return doc;
	}
}
