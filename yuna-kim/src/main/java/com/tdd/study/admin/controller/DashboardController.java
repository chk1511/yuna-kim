package com.tdd.study.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tdd.study.admin.model.DashboardModel;
import com.tdd.study.admin.service.DashboardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
public class DashboardController {
	
	@Autowired
	DashboardService dashboardService;

    @GetMapping(value = "dashboard")
    public String index() {
        return "dashboard/dashboard";
    }

	@RequestMapping(value="search")
    public @ResponseBody List<DashboardModel>
    search(@RequestParam(value = "query") String query) throws Exception{
    	return this.dashboardService.getWanted(query);
    }
	
//	RequestMapping(value="search")
//    public @ResponseBody List<DashboardModel>
//    search(@RequestParam(value = "query") String query, @RequestParam(value = "productType") String productType) throws Exception{
//    	return this.dashboardService.search(query, productType);
//    }
}