package com.example.quiz12.service.ifs;

import com.example.quiz12.vo.BasicRes;
import com.example.quiz12.vo.FeedBackRes;
import com.example.quiz12.vo.FillinReq;
import com.example.quiz12.vo.StatisticsRes;

public interface FeedBackService {
	
	public BasicRes Fillin(FillinReq req);
	
	public FeedBackRes feedBack(int quizId);
	
	public StatisticsRes statistics(int quizId);
}
