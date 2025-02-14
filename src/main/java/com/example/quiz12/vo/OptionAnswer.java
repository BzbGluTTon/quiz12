package com.example.quiz12.vo;

import java.util.List;

public class OptionAnswer {
	private int quesId;
	
	private String quesName;
	
	private List<String> quesAnswer;

	public int getQuesId() {
		return quesId;
	}

	public void setQuesId(int quesId) {
		this.quesId = quesId;
	}

	public String getQuesName() {
		return quesName;
	}

	public void setQuesName(String quesName) {
		this.quesName = quesName;
	}

	public List<String> getQuesAnswer() {
		return quesAnswer;
	}

	public void setQuesAnswer(List<String> quesAnswer) {
		this.quesAnswer = quesAnswer;
	}
	
	
	
	
}
