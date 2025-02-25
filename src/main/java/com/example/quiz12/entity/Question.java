package com.example.quiz12.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
@IdClass(value = QuestionId.class)
public class Question {
	@Id
	@Column(name = "quiz_id")
	private int quizId;
	@Id
	@Column(name = "ques_id")
	private int quesId;
	@Column(name = "ques_name")
	private String quesName;
	@Column(name = "type")
	private String type;
	@Column(name = "required")
	private boolean required;
	@Column(name = "options")
	private String options;

	public Question() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Question(int quizId, int ques_id, String ques_name, String type, boolean required, String options) {
		super();
		this.quizId = quizId;
		this.quesId = ques_id;
		this.quesName = ques_name;
		this.type = type;
		this.required = required;
		this.options = options;
	}

	

	public int getQuizId() {
		return quizId;
	}

	public int getQuesId() {
		return quesId;
	}

	public String getQuesName() {
		return quesName;
	}

	public String getType() {
		return type;
	}

	public boolean isRequired() {
		return required;
	}

	public String getOptions() {
		return options;
	}
	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public void setQuesId(int quesId) {
		this.quesId = quesId;
	}

	

}
