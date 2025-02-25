package com.example.quiz12.service.impl;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.quiz12.constants.QuesType;
import com.example.quiz12.constants.ResMessage;
import com.example.quiz12.dao.FeedbackDao;
import com.example.quiz12.dao.QuestionDao;
import com.example.quiz12.dao.QuizDao;
import com.example.quiz12.entity.FeedBack;
import com.example.quiz12.entity.Question;
import com.example.quiz12.entity.Quiz;
import com.example.quiz12.service.ifs.FeedBackService;
import com.example.quiz12.vo.BasicRes;
import com.example.quiz12.vo.FeedBackDto;
import com.example.quiz12.vo.FeedBackRes;
import com.example.quiz12.vo.FeedBackVo;
import com.example.quiz12.vo.FillinReq;
import com.example.quiz12.vo.OptionAnswer;
import com.example.quiz12.vo.OptionCount;
import com.example.quiz12.vo.StatisticsDto;
import com.example.quiz12.vo.StatisticsRes;
import com.example.quiz12.vo.StatisticsVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FeedBackServiceImpl implements FeedBackService {
	
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private QuestionDao questionDao;

	@Autowired
	private FeedbackDao feedbackDao;

	@Override
	public BasicRes Fillin(FillinReq req) {
		// 1. 檢查參數
		BasicRes checkRes = checkParam(req);
		if (checkRes != null) {
			return checkRes;
		}
		// 2. 檢查問卷是否存在以及已發佈
		if (quizDao.selectCountIsPublished(req.getQuizId()) != 1) {
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), //
					ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		// 3. 檢查同一個 email 是否有填過同一張問卷
		if (feedbackDao.selectCount(req.getQuizId(), req.getEmail()) != 0) {
			return new BasicRes(ResMessage.EMAIL_DUPLICATED.getCode(), //
					ResMessage.EMAIL_DUPLICATED.getMessage());
		}
		// 4. 檢查問題
		// 利用 quizId 找出問卷(使用 JPA 方法): 被 Optional 包起來主要用來提醒要判斷內容物是否有值
		Optional<Quiz> op = quizDao.findById(req.getQuizId());
		// 判斷被 Optional 包起來的 Quiz 物件是否有值
		if (op.isEmpty()) { // op.isEmpty() == true 時，表示從資料庫取回的 Quiz 沒有資料
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), //
					ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		// 將 Quiz 從 Optional 中取出
		Quiz quiz = op.get();
		// 4.1 檢查填寫的日期是否在問卷可填寫的範圍內
		LocalDate startDate = quiz.getStartDate();
		LocalDate endDate = quiz.getEndDate();
		LocalDate fillinDate = req.getFillinDate();
		// 判斷填寫時間是否在開始時間之前或者結束時間之後
		if (fillinDate.isBefore(startDate) || fillinDate.isAfter(endDate)) {
			return new BasicRes(ResMessage.OUT_OF_FILLIN_DATE_RANGE.getCode(), //
					ResMessage.OUT_OF_FILLIN_DATE_RANGE.getMessage());
		}
		// 4.2 比對相同題號中填寫的答案(來自 req)與選項(來自資料庫)是否一樣(除了簡答之外)
		List<Question> quesList = questionDao.getByQuizId(req.getQuizId());
		// 題號, 答案(1~多個)
		Map<Integer, List<String>> quesIdAnswerMap = req.getQuesIdAnswerMap();
		
		for (Question item : quesList) {
			// 比對題號
			int quesNumber = item.getQuesId();
			List<String> answerList = quesIdAnswerMap.get(quesNumber);
			// 排除若該題是必填，但沒有答案
			if (item.isRequired() && CollectionUtils.isEmpty(answerList)) {
				return new BasicRes(ResMessage.ANSWERI_IS_REQUIRED.getCode(), //
						ResMessage.ANSWERI_IS_REQUIRED.getMessage());
			}
			// 排除題目類型是 text
			String quesType = item.getType();
			if (quesType.equalsIgnoreCase(QuesType.TEXT.getType())) {
				// 跳過當次
				continue;
			}
			// 題目是單選或簡答(文字)時			
			if (quesType.equalsIgnoreCase(QuesType.SINGLE.getType()) //
					|| quesType.equalsIgnoreCase(QuesType.TEXT.getType())) {
				// 答案不能有多個
				if (answerList.size() > 1) {
					return new BasicRes(ResMessage.ONE_OPTION_IS_ALLOWED.getCode(), //
							ResMessage.ONE_OPTION_IS_ALLOWED.getMessage());
				}
			}
			// 將選項字串轉成 List<String>: 要先確定當初創建問卷時，前端的多個選項是陣列，且使用 Stringify 轉成字串型態
			// 前端選項原本格式(陣列): ["aa","bb", "cc"]			
			try {
				List<String> options = mapper.readValue(item.getOptions(), new TypeReference<>() {
				});
				// 比對相同題號中的選項與答案
				for (String answer : answerList) {
					if (!options.contains(answer)) {
						return new BasicRes(ResMessage.OPTIONS_ANSWER_MISMATCH.getCode(), //
								ResMessage.OPTIONS_ANSWER_MISMATCH.getMessage());
					}
				}
			} catch (Exception e) {
				return new BasicRes(ResMessage.OPTIONS_PARSER_ERROE.getCode(), //
						ResMessage.OPTIONS_PARSER_ERROE.getMessage());
			}
		}
		// 存資料
		List<FeedBack> feedbackList = new ArrayList<>();
		for(Entry<Integer, List<String>> map : req.getQuesIdAnswerMap().entrySet()) {
			FeedBack feeback = new FeedBack();
			feeback.setQuizId(req.getQuizId());
			feeback.setUserName(req.getUsername());
			feeback.setEmail(req.getEmail());
			feeback.setAge(req.getAge());
			feeback.setQuesId(map.getKey());
			// 將 List<String> 轉成 String
			try {
				String answerStr = mapper.writeValueAsString(map.getValue());
				feeback.setAnswer(answerStr);
			} catch (JsonProcessingException e) {
				return new BasicRes(ResMessage.OPTIONS_PARSER_ERROE.getCode(), //
						ResMessage.OPTIONS_PARSER_ERROE.getMessage());
			}
			feeback.setFillinDate(req.getFillinDate());
			feedbackList.add(feeback);
		}
		feedbackDao.saveAll(feedbackList);
		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	private BasicRes checkParam(FillinReq req) {
		// 排除法
		if (req.getQuizId() <= 0) {
			return new BasicRes(ResMessage.PARAM_QUIZ_ID_ERROR.getCode(), //
					ResMessage.PARAM_QUIZ_ID_ERROR.getMessage());
		}
		if (!StringUtils.hasText(req.getUsername())) {
			return new BasicRes(ResMessage.PARAM_USER_NAME_ERROR.getCode(), //
					ResMessage.PARAM_USER_NAME_ERROR.getMessage());
		}
		if (!StringUtils.hasText(req.getEmail())) {
			return new BasicRes(ResMessage.PARAM_EMAIL_ERROR.getCode(), //
					ResMessage.PARAM_EMAIL_ERROR.getMessage());
		}
		if (req.getAge() <= 0) {
			return new BasicRes(ResMessage.PARAM_AGE_ERROR.getCode(), //
					ResMessage.PARAM_AGE_ERROR.getMessage());
		}
		return null;
	}

	@Override
	public FeedBackRes feedBack(int quizId) {
		if(quizId <= 0) {
			return new FeedBackRes(ResMessage.PARAM_QUIZ_ID_ERROR.getCode(), //
					ResMessage.PARAM_QUIZ_ID_ERROR.getMessage());
		}
		List<FeedBackDto> feedbackList = feedbackDao.selectFeedBackByQuizId(quizId);
		// 整理資料
		List<FeedBackVo> feedbackVoList = new ArrayList<>();
		for(FeedBackDto item : feedbackList) {
			// 查看 feedbackVoList 中是否已有相同 email 存在
			FeedBackVo resVo = getEmail(feedbackVoList, item.getEmail());
			if(resVo != null) { // 表示 feedbackVoList 中的 FeedbackVo 已經存在相同的 email
				// 取出 optionAnswerList，此 optionAnswerList 已經有包含之前新增的 optionAnswer
				List<OptionAnswer> optionAnswerList = resVo.getOptionAnswersList();
				// 新增並設定同一張問卷不同問題以及答案				
				OptionAnswer optionAnswer = new OptionAnswer();
				optionAnswer.setQuesId(item.getQuesId());
				optionAnswer.setQuesName(item.getQuesName());
				// 把答案字串轉成 List<String>
				List<String> answerList = new ArrayList<>();
				try {
					answerList = mapper.readValue(item.getAnswer(), new TypeReference<>() {});				
				} catch (Exception e) {
					return new FeedBackRes(ResMessage.ANSWER_PARSER_ERROR.getCode(), //
							ResMessage.ANSWER_PARSER_ERROR.getMessage());
				}
				optionAnswer.setQuesAnswer(answerList);				
				optionAnswerList.add(optionAnswer);				
				resVo.setOptionAnswersList(optionAnswerList);
				// 取出的 FeedbackVo 早已經存在於 feedbackVoList 中，所以最後不需要再把 resVo 新增回去
			} else { // 表示 feedbackVoList 中的 FeedbackVo 沒有相同的 email
				FeedBackVo vo = new FeedBackVo();
				// 設定同一張問卷和同一位填寫者的資料
				vo.setQuizId(quizId);
				vo.setQuizName(item.getQuizName());
				vo.setDescription(item.getDescription());
				vo.setUserName(item.getUserName());
				vo.setEmail(item.getEmail());
				vo.setAge(item.getAge());
				vo.setFillinDate(item.getFillinDate());
				// 設定同一張問卷不同問題以及答案
				List<OptionAnswer> optionAnswerList = new ArrayList<>();
				OptionAnswer optionAnswer = new OptionAnswer();
				optionAnswer.setQuesId(item.getQuesId());
				optionAnswer.setQuesName(item.getQuesName());
				// 把答案字串轉成 List<String>
				List<String> answerList = new ArrayList<>();
				try {
					answerList = mapper.readValue(item.getAnswer(), new TypeReference<>() {});				
				} catch (Exception e) {
					return new FeedBackRes(ResMessage.ANSWER_PARSER_ERROR.getCode(), //
							ResMessage.ANSWER_PARSER_ERROR.getMessage());
				}
				optionAnswer.setQuesAnswer(answerList);
				optionAnswerList.add(optionAnswer);
				vo.setOptionAnswersList(optionAnswerList);
				
				feedbackVoList.add(vo);
			}			
		}
		return new FeedBackRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), feedbackVoList);
	}
	
	private FeedBackVo getEmail(List<FeedBackVo> feedbackVoList, String targetEmail) {
		for(FeedBackVo vo : feedbackVoList) {
			if(vo.getEmail().equalsIgnoreCase(targetEmail)) {
				return vo;
			}
		}
		return null;
	}

	@Override
	public StatisticsRes statistics(int quizId) {
		if(quizId <= 0) {
			return new StatisticsRes(ResMessage.PARAM_QUIZ_ID_ERROR.getCode(), //
					ResMessage.PARAM_QUIZ_ID_ERROR.getMessage());
		}
		List<StatisticsDto> dtoList = feedbackDao.statistics(quizId);
		// 1. 集合每一題各自的所有答案: Map<題號，答案>
		Map<Integer, List<String>> quesIdAnswerMap = gatherAnswer(dtoList);
		if(quesIdAnswerMap == null) {
			return new StatisticsRes(ResMessage.ANSWER_PARSER_ERROR.getCode(), //
					ResMessage.ANSWER_PARSER_ERROR.getMessage());
		}
		// 2. 蒐集每一題的選項(不直接從答案計算次數，是因為可能有極端的情況: 就是某個選項沒人選)
		List<OptionCount> optionCountList = gatherOptions(dtoList);
		if (optionCountList == null) {
			return new StatisticsRes(ResMessage.OPTIONS_PARSER_ERROE.getCode(), //
					ResMessage.OPTIONS_PARSER_ERROE.getMessage());
		}
		// 3.蒐集每一題每個選項的次數
		optionCountList = computeCount(quesIdAnswerMap,optionCountList);
		if (optionCountList == null) {
			return new StatisticsRes(ResMessage.OPTIONS_COUNT_ERROR.getCode(), //
					ResMessage.OPTIONS_COUNT_ERROR.getMessage());		
		}
		//設定結束
		List<StatisticsVo> statisticsVoList =new ArrayList<StatisticsVo>();
		for (StatisticsDto dto : dtoList) {
			StatisticsVo vo = new StatisticsVo();
			vo.setQuizName(dto.getQuizName());
			vo.setQuesId(dto.getQuesId());
			vo.setQuesName(dto.getQuesName());
			vo.setRequired(dto.isRequired());
			//把相同題號的 OptionCount 放一起
			List<OptionCount> ocList = new ArrayList<OptionCount>();
			for (OptionCount oc : optionCountList) {
				if (oc.getQuesId() == dto.getQuesId()) {
					//相同題號的話，就把當初蒐集的 OptionCount 放一起
					ocList.add(oc);
				}
			}
			vo.setOptionCountList(ocList);
			statisticsVoList.add(vo);
			
		}
		return new StatisticsRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(),statisticsVoList);		
	
	}
	
	private Map<Integer, List<String>> gatherAnswer(List<StatisticsDto> dtoList) {
		// 把每一題的答案放到 quesIdAnswerMap 中
		// 1. 若 quesIdAnswerMap 中已存在相同編號的 quesId //
		//--> 從 quesIdAnswerMap 中取出相同 quesId 對應的答案 List<String>, 
		//並把轉化後的答案加再一起後並放回到 quesIdAnswerMap 中 //
		// 2. 若 quesIdAnswerMap 中不存在相同編號的 quesId //
		//--> 把轉化後的答案 List<String> 放到 quesIdAnswerMap 中
		
		Map<Integer, List<String>> quesIdAnswerMap = new HashMap<>();
		for(StatisticsDto item : dtoList) {
			//如果題型是 text(簡答) 就跳過不蒐集
//			if (item.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
//				continue;
//			}
			// 將 Answer String 轉成 List<String>
			List<String> answerList = new ArrayList<>();
			try {
				answerList = mapper.readValue(item.getAnswer(), new TypeReference<>() {});
			} catch (Exception e) {
				return null;
			}
			// 若 quesIdAnswerMap 中已經存在相同編號的 List<String> answerList，就從 map 中取出
			if(quesIdAnswerMap.containsKey(item.getQuesId())) {
				List<String> answerListInMap = quesIdAnswerMap.get(item.getQuesId());
				// 把新的答案案已經存在的 answerList 加在一起
				answerList.addAll(answerListInMap);
				quesIdAnswerMap.put(item.getQuesId(), answerList);
			}else {
			quesIdAnswerMap.put(item.getQuesId(), answerList);
			}
		}
		return quesIdAnswerMap;
	}
	private List<OptionCount> gatherOptions(List<StatisticsDto> dtoList) {
		List<OptionCount> optionCountList = new ArrayList<OptionCount>();
		Map<Integer, Boolean> map = new HashMap<Integer, Boolean>(); 
		for (StatisticsDto dto : dtoList) {
			// 跳過題型是 text ，因為沒有選項畫面可蒐集
			if (dto.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
				continue;
			}
			// targeQuesId == dto 為 true 的話，表示同一提的選項已經蒐集過了。
			Boolean boo = map.get(dto.getQuesId());
			// 若尚未收集過，boo 會是 null，需要排除
			if ( boo != null && boo == true) {
				continue;
			}
			
			
			//轉換每一題的選項 String 為List<String> 
			List<String> optionList = new ArrayList<String>();
			try {
				optionList = mapper.readValue(dto.getOptions(),new TypeReference<>() {});
			} catch (Exception e) {
				return null;
			}
			//蒐集題號和選項
			for (String str : optionList) {
				// 相同的題號下，每個不同的選項就會有一個 OptionCount
				OptionCount oc = new OptionCount();	
				oc.setQuesId(dto.getQuesId());
				oc.setOption(str);
				optionCountList.add(oc);
			}
			//表示已蒐集過該題的選項
			map.put(dto.getQuesId(), true);
		}
		return optionCountList;
	}
	//此方法只計算有選項的題型(單選、多選)
	private List<OptionCount> computeCount(Map<Integer, List<String>> quesIdAnswerMap, //
					List<OptionCount> optionCountList) {
		//因為是以選項為主，所以外層的迴圈是 optionCountList
		for (OptionCount item : optionCountList) {
			int quesId = item.getQuesId();
			String option = item.getOption();
			// 透過 quesId 從 quesIdAnswerMap 取的對應答案 List
			List<String> ansList = quesIdAnswerMap.get(quesId);
			if (ansList ==null) {
				return null;
			}
			// 把 List<String> 串成單字一串
			String ansStr =String.join("-",ansList);
			//計算每個選項的次數
			int ansStrLength = ansStr.length();//原本長度
			 String newAnsStr = ansStr.replace(option, "");//把某個選項用空字串替換
			 int newStrLength = newAnsStr.length();//扣掉某個選空字串的長度
			 //判斷該選項都沒人選
			 if (ansStrLength == newStrLength) {
				//將次數設定回到 OptionCount 中
				 item.setCount(0);
			}else {
				//選項可能會有多個字，所以要計算次數應是要除以選項長度
				 int count = (ansStrLength - newStrLength)/option.length();
				 //將次數設定回到 OptionCount 中
				 item.setCount(count);							
			}
		}
	return optionCountList;
	}
	//=====================================================
}