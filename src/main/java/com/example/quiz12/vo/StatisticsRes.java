package com.example.quiz12.vo;

import java.util.List;

public class StatisticsRes extends BasicRes {
	private List<StatisticsVo> statisticsVoList;

	public StatisticsRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StatisticsRes(int code, String msg) {
		super(code, msg);
		// TODO Auto-generated constructor stub
	}

	public StatisticsRes(int code, String msg, List<StatisticsVo> statisticsVoList) {
		super(code, msg);
		this.statisticsVoList = statisticsVoList;
	}

	public List<StatisticsVo> getStatisticsVoList() {
		return statisticsVoList;
	}

	public void setStatisticsVoList(List<StatisticsVo> statisticsVoList) {
		this.statisticsVoList = statisticsVoList;
	}

}
