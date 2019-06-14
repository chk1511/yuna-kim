package com.tdd.study.schedule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tdd.study.slack.SlackSender;

@Component
public class Schedule {
	
	@Value("${slack.webHook.url}")
	private String url;
	
	@Value("${slack.webHook.channel}")
	private String channel;
	
	@Value("${slack.webHook.username}")
	private String username;
	
	@Scheduled(cron = "*/10 * * * * *")
    public void start() {
		//parsing
		
		//true, false
		
		//send
		new SlackSender(url, channel, username).send();
		System.out.println(url);
        System.out.println(System.currentTimeMillis());
        
    }
}
