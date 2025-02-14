package com.example.quiz12.vo;

import java.util.List;

import com.example.quiz12.entity.Question;

public class GetQuestionRes extends BasicRes{
	
	private List<Question> listQuws;

	public GetQuestionRes() {
		super();
		
	}

	public GetQuestionRes(int code, String msg) {
		super(code, msg);
		
	}
	
	public GetQuestionRes(int code, String msg,List<Question> listQuws) {
		super(code, msg);
		this.listQuws = listQuws;
	}
	
	
}
