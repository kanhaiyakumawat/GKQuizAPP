package com.kanhaiyakumawat.androidapps.sqlite.model;

import java.util.List;

import android.util.Log;

public class QuestionDetails {
	private static final String LOG = "QuestionDetails";
	private long questionId;
	private String questionText;
	private long userAttempts;
	private long successfulAttempts;
	private String questionType;

	private List<OptionDetails> optionDetails;
	
	public long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public long getUserAttempts() {
		return userAttempts;
	}
	public void setUserAttempts(long userAttempts) {
		this.userAttempts = userAttempts;
	}
	public long getSuccessfulAttempts() {
		return successfulAttempts;
	}
	public void setSuccessfulAttempts(long successfulAttempts) {
		this.successfulAttempts = successfulAttempts;
	}
	public List<OptionDetails> getOptionDetails() {
		return optionDetails;
	}
	public void setOptionDetails(List<OptionDetails> optionDetails) {
		this.optionDetails = optionDetails;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public boolean isValid()
	{
		if(questionId == 0 || questionText == null || userAttempts < 0 || successfulAttempts < 0)
		{
			Log.v(LOG, "Question Details invalid");
			return false;
		}
		
		return true;
	}
}
