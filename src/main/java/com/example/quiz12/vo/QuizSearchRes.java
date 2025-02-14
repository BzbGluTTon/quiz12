package com.example.quiz12.vo;

import java.time.LocalDate;
import java.util.List;

import com.example.quiz12.entity.Quiz;

import jakarta.persistence.Column;

public class QuizSearchRes extends BasicRes {
	private List<Quiz> quizList;

	public QuizSearchRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QuizSearchRes(int code, String msg) {
		super(code, msg);
		// TODO Auto-generated constructor stub
	}

	public QuizSearchRes(int code, String msg,List<Quiz> quizList) {
		super(code, msg);
		this.quizList = quizList;
	}

	public List<Quiz> getQuizList() {
		return quizList;
	}
	
}
