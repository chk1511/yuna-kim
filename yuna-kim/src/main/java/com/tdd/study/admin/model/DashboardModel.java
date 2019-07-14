package com.tdd.study.admin.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("unused")
public class DashboardModel {

	private int id;
	private String location;
	private String companyName;
	private String industryName;
	private String position;
	private String dueTime;
	private String experience;
	private String productType;
}