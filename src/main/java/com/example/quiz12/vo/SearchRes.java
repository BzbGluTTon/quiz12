package com.example.quiz12.vo;

import java.time.LocalDate;
import java.util.List;

import com.example.quiz12.entity.Question;

public class SearchRes extends BasicRes {
	
	private List<SearchVo> searchList;

	public SearchRes() {
		super();
	}

	public SearchRes(int code, String msg) {
		super(code, msg);
	}
	public SearchRes(int code, String msg,List<SearchVo> searchList) {
		super(code, msg);
		this.searchList = searchList;
	}

	public List<SearchVo> getSearchList() {
		return searchList;
	}
	
	
}
