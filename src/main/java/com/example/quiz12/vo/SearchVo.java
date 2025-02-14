package com.example.quiz12.vo;

import java.time.LocalDate;
import java.util.List;

import com.example.quiz12.entity.Question;

public class SearchVo extends CreateReq {
	private int quizId;

	public SearchVo() {
		super();
	}

	public SearchVo(int quizId, String name, String description, LocalDate start_date, LocalDate end_date,
			boolean published, List<Question> questions) {
		super(name, description, start_date, end_date, published, questions);
		this.quizId = quizId;
	}

	public int getQuizId() {
		return quizId;
	}
	
	
}
