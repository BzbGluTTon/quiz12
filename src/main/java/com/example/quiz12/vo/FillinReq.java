package com.example.quiz12.vo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.example.quiz12.entity.FeedBack;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class FillinReq {
	
	private int quizId;
	
	
	private String username;
	
	private String email;
	
	
	private int age;
	
	//			編號        答案            名稱
	private Map<Integer, List<String>> quesIdAnswerMap;
	
	//給定預設值(當前日期)，前端送的req，此欄位若沒值(null)，這欄位及使用預設值
	private LocalDate fillinDate =LocalDate.now();

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Map<Integer, List<String>> getQuesIdAnswerMap() {
		return quesIdAnswerMap;
	}

	public void setQuesIdAnswerMap(Map<Integer, List<String>> quesIdAnswerMap) {
		this.quesIdAnswerMap = quesIdAnswerMap;
	}

	public LocalDate getFillinDate() {
		return fillinDate;
	}

	public void setFillinDate(LocalDate fillinDate) {
		this.fillinDate = fillinDate;
	}
	

}
