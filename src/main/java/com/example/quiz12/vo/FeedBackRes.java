package com.example.quiz12.vo;

import java.util.List;

public class FeedBackRes extends BasicRes{
	private List<FeedBackVo> feedBackVo;

	public List<FeedBackVo> getFeedBackVo() {
		return feedBackVo;
	}
	
	public FeedBackRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FeedBackRes(int code, String msg,List<FeedBackVo> feedBackVo) {
		super(code, msg);
		this.feedBackVo = feedBackVo;
	}


	public FeedBackRes(int code, String msg) {
		super(code, msg);
		// TODO Auto-generated constructor stub
	}
	
	
}
