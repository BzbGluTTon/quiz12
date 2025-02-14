package com.example.quiz12.vo;

public class StatisticsDto {
	private String quizName;
	private int quesId;
	private String quesName;
	private boolean required;
	private String options;
	private String type;
	private String answer;

	public StatisticsDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StatisticsDto(String quizName, int quesId, String quesName, boolean required, String options, String type,
			String answer) {
		super();
		this.quizName = quizName;
		this.quesId = quesId;
		this.quesName = quesName;
		this.required = required;
		this.options = options;
		this.type = type;
		this.answer = answer;
	}

	public String getQuizName() {
		return quizName;
	}

	public void setQuizName(String quizName) {
		this.quizName = quizName;
	}

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

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
