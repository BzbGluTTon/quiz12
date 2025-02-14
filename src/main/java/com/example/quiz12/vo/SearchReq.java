package com.example.quiz12.vo;

import java.time.LocalDate;

public class SearchReq {

	private String name;
	
	private LocalDate startDate;
	
	private LocalDate endDate;

	public SearchReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SearchReq(String name, LocalDate startDate, LocalDate endDate) {
		super();
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getName() {
		return name;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}
	
	
}
