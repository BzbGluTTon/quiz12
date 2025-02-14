package com.example.quiz12.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.quiz12.constants.QuesType;
import com.example.quiz12.constants.ResMessage;
import com.example.quiz12.dao.QuestionDao;
import com.example.quiz12.dao.QuizDao;
import com.example.quiz12.entity.Question;
import com.example.quiz12.entity.Quiz;
import com.example.quiz12.service.ifs.QuizService;
import com.example.quiz12.vo.BasicRes;
import com.example.quiz12.vo.CreateReq;
import com.example.quiz12.vo.DeleatReq;
import com.example.quiz12.vo.GetQuestionRes;
import com.example.quiz12.vo.QuizSearchRes;
import com.example.quiz12.vo.SearchReq;
import com.example.quiz12.vo.SearchRes;
import com.example.quiz12.vo.UpdateReq;

import jakarta.transaction.Transactional;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private QuestionDao questionDao;

//================================================================================================
	// @Transactional: 因為同時新增問卷和問題，新增多筆資料都算是同一次的行為，所以要嘛全部成功，要嘛全部失敗
	// rollbackOn = Exception.class: 指定@Transactional 資料回朔有效的例外層級
	// 發生例外(Exception)是 RuntimeException 或其子類別時，@Transactional 才會讓資料回朔，
	// 藉由 rollbackOn 可以指定發生哪個例外時，就可以讓資料回朔
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes create(CreateReq req) {
		// 檢查參數
		BasicRes checkRes = checkParam(req);
		if (checkRes != null) {
			return checkRes;
		}
		// 新增問卷，問卷的 id 定是0，但 Rrq 沒有 quiz_id，所以不檢查

		// 因為 quiz 的 PK 是流水號，不會重複寫入，所以不用檢查資料料庫是否已存在相同的 PK
		// 新增問卷
		// 因為 Quiz 中的 id 是 AI 自動生成的流水號，要讓 quizDao 執行 save 後可以把該 id 的值回傳，
		// 必須要在 Quiz 此 Entity 中將資料型態為 int 的屬性 id
		// 加上 @GeneratedValue(strategy = GenerationType.IDENTITY)
		// JPA 的 save，PK 已存在於 DB，會執行 update，若PK不存在，則會執行 insert

		// 新增失敗: 用 try-catch，包含 @Transactional 資料回朔層級
		try {
			Quiz quiz = quizDao.save(new Quiz(req.getName(), req.getDescription(), req.getStart_date(),
					req.getEnd_date(), req.isPublished()));

			// quiz_id 塞至 question list 中 quizId
			for (Question item : req.getQuestions()) {
				item.setQuizId(quiz.getId());
				System.out.println(req.getQuestions());
			}

			// 新增問題: 把 question list 寫進 BD
			questionDao.saveAll(req.getQuestions());
			return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());

		} catch (Exception e) {
			return new BasicRes(ResMessage.DATA_SAVE_ERROR.getCode(), ResMessage.DATA_SAVE_ERROR.getMessage());
		}

	}

//=================================================================================================================================
	private BasicRes checkParam(CreateReq req) {

		if (!StringUtils.hasText(req.getName())) {
			return new BasicRes(ResMessage.PARAM_QUIZ_NAME_ERROR.getCode(), ResMessage.PARAM_QUIZ_NAME_ERROR.getMessage());
		}
		if (!StringUtils.hasText(req.getDescription())) {
			return new BasicRes(ResMessage.PARAM_DESCRIPTION_ERROR.getCode(),
					ResMessage.PARAM_DESCRIPTION_ERROR.getMessage());
		}
		if (req.getStart_date() == null) {
			return new BasicRes(ResMessage.PARAM_START_DATE_ERROR.getCode(),
					ResMessage.PARAM_START_DATE_ERROR.getMessage());
		}
		if (req.getEnd_date() == null) {
			return new BasicRes(ResMessage.PARAM_END_DATE_ERROR.getCode(), ResMessage.PARAM_END_DATE_ERROR.getMessage());
		}
		// 檢查 Start_date(開始日期) 不能超過 End_date(結束時間)
		// (開始日期).isAfter(結束時間): 判斷(結束時間)是否(開始日期)之後
		if (req.getStart_date().isAfter(req.getEnd_date())) {
			return new BasicRes(ResMessage.PARAM_DATE_ERROR.getCode(), ResMessage.PARAM_DATE_ERROR.getMessage());
		}
		// ====== 檢查問題內容
		List<Question> quesList = req.getQuestions();
		if (quesList.size() <= 0) {
			return new BasicRes(ResMessage.PARAM_QUES_LIST_ERROR.getCode(), //
					ResMessage.PARAM_QUES_LIST_ERROR.getMessage());
		}
		for (Question item : quesList) {
			// 問題編號一定是從1開始，但無法檢查是否有按照順序以及中間是否有空缺的編號
			if (item.getQuesId() <= 0) {
				return new BasicRes(ResMessage.PARAM_QUES_ID_ERROR.getCode(), //
						ResMessage.PARAM_QUES_ID_ERROR.getMessage());
			}
			if (!StringUtils.hasText(item.getQuesName())) {
				return new BasicRes(ResMessage.PARAM_QUES_NAME_ERROR.getCode(), //
						ResMessage.PARAM_QUES_NAME_ERROR.getMessage());
			}
			if (!StringUtils.hasText(item.getType())) {
				return new BasicRes(ResMessage.PARAM_TYPE_ERROR.getCode(), //
						ResMessage.PARAM_TYPE_ERROR.getMessage());
			}

			// 檢查 1.type 是否為 單選、多選、文字(簡答)
//			if (!item.getType().equalsIgnoreCase(QuesType.SINGLE.getType()) || //
//					!item.getType().equalsIgnoreCase(QuesType.MULTI.getType()) || //
//					!item.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
//
//				return new BasicRes(ResMessage.QUES_TYPE_MISMATCH.getCode(), //
//						ResMessage.QUES_TYPE_MISMATCH.getMsg());
//			}
			
			if (!QuesType.checkType(item.getType())) {

				return new BasicRes(ResMessage.QUES_TYPE_MISMATCH.getCode(), //
						ResMessage.QUES_TYPE_MISMATCH.getMessage());
			}

			// 檢查 2.文字(簡答)類型 時，Options不能有值
			if (item.getType().equalsIgnoreCase(QuesType.TEXT.getType()) //
					&& StringUtils.hasText(item.getOptions())) {
				return new BasicRes(ResMessage.PARAM_OPTIONS_ERROR.getCode(), //
						ResMessage.PARAM_OPTIONS_ERROR.getMessage());
			}
		}
		return null;
	}

//================================================================================================
	@Override
	public QuizSearchRes getAllQuiz() {
		List<Quiz> res = quizDao.getAllQuiz();
		return new QuizSearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), res);
	}

//================================================================================================
	@Override
	public QuizSearchRes getQuiz(SearchReq req) {
		// 若 name 沒條件，前端的值可能帶 null 或 空字串
		// 改條件值: 如果 name 是 null 或 空字串 或 全空字串，一律替換為空字串
		String name = req.getName();
		if (!StringUtils.hasText(name)) {
			// SQL 語法中， like %% (%中間為空字串)表示 忽略該欄位的條件值
			name = "";
		}
		LocalDate starData = req.getStartDate();
		if (starData == null) { // starData == null，表示前端沒帶值
			// 沒帶值，可把開始時間設置很早的時間
			starData = LocalDate.of(1970, 1, 1);
		}

		LocalDate endData = req.getEndDate();
		if (endData == null) { // starData == null，表示前端沒帶值
			// 沒帶值，可把結束時間設置很晚的時間
			endData = LocalDate.of(2999, 12, 31);
		}
		List<Quiz> res = quizDao.getQuiz(name, starData, endData);
		return new QuizSearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), res);
	}

//================================================================================================
	@Override
	public GetQuestionRes getQuesByQuizId(int quizId) {
		// 檢查
		if (quizId <= 0) {
			return new GetQuestionRes(ResMessage.PARAM_QUIZ_ID_ERROR.getCode(),
					ResMessage.PARAM_QUIZ_ID_ERROR.getMessage());
		}
		List<Question> res = questionDao.getByQuizId(quizId);
		return new GetQuestionRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), res);

	}

//================================================================================================
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes delete(DeleatReq req) {
		try {
			quizDao.deleteByQuizIdIn(req.getQuizList());
			questionDao.deleteByQuesIdIn(req.getQuizList());
		} catch (Exception e) {
			return new BasicRes(ResMessage.DATA_SAVE_ERROR.getCode(), ResMessage.DATA_SAVE_ERROR.getMessage());

		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());

	}
//================================================================================================
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes update(UpdateReq req) {
		BasicRes checkRes = checkParam(req);
		if (checkRes != null) {
			return checkRes;
		}

		// 檢查 (1) quizId 是否有資料存在
		int count = quizDao.selectCount(req.getQuizId());
		if (count != 0) {
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		// 檢查問題中的 quizId 是否與 req 中的 quizId 是否相同
		for (Question item : req.getQuestions()) {
			if (req.getQuizId() != item.getQuizId()) {
				return new BasicRes(ResMessage.QUIZ_ID_MISMATCH.getCode(), ResMessage.QUIZ_ID_MISMATCH.getMessage());
			}
		}

		try {
			// (1) 更新 quiz by quizId
			quizDao.updateById(req.getName(), req.getDescription(), req.getEnd_date(), req.getEnd_date(),
					req.isPublished(), req.getQuizId());

			// (2) 刪除 questions by quizId
			questionDao.deleteByQuesIdIn(List.of(req.getQuizId()));

			// (3) 新增 questions by quizId
			questionDao.saveAll(req.getQuestions());

		} catch (Exception e) {
			return new BasicRes(ResMessage.DATA_SAVE_ERROR.getCode(), ResMessage.DATA_SAVE_ERROR.getMessage());

		}

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

}
