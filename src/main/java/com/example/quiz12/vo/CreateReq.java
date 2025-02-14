package com.example.quiz12.vo;

import java.time.LocalDate;
import java.util.List;

import com.example.quiz12.entity.Question;

import jakarta.persistence.Column;

public class CreateReq {

	private String name;

	private String description;

	private LocalDate start_date;

	private LocalDate end_date;

	private boolean published;

	// 問題
	private List<Question> questions;

	public CreateReq() {
		super();
	}

	public CreateReq(String name, String description, LocalDate start_date, LocalDate end_date, boolean published,
			List<Question> questions) {
		super();
		this.name = name;
		this.description = description;
		this.start_date = start_date;
		this.end_date = end_date;
		this.published = published;
		this.questions = questions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getStart_date() {
		return start_date;
	}

	public void setStart_date(LocalDate start_date) {
		this.start_date = start_date;
	}

	public LocalDate getEnd_date() {
		return end_date;
	}

	public void setEnd_date(LocalDate end_date) {
		this.end_date = end_date;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

}
