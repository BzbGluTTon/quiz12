package com.example.quiz12.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.quiz12.entity.Question;
import com.example.quiz12.entity.QuestionId;

import jakarta.transaction.Transactional;

public interface QuestionDao extends JpaRepository<Question, QuestionId>{
	
	@Query(value = "select * from question where quiz_id =?", nativeQuery = true)
	public List<Question>getByQuizId(int quizId);
	
	@Transactional
	@Modifying
	@Query(value = "delete from qestion where quiz_id in (?1)", nativeQuery = true)
	public int deleteByQuesIdIn (List<Integer> quizIds);
	
}
