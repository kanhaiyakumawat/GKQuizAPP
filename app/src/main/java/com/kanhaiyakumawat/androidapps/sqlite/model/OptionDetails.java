package com.kanhaiyakumawat.androidapps.sqlite.model;

import android.util.Log;

public class OptionDetails {

	private static final String LOG = "OptionDetails";
	private long id;
	private long optionId;
	private long questionId;
	private String text;
	private boolean isAnswer;
    private boolean isAttempted;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOptionId() {
		return optionId;
	}
	public void setOptionId(long optionId) {
		this.optionId = optionId;
	}
	public long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isAnswer() {
		return isAnswer;
	}
	public void setAnswer(boolean isAnswer) {
		this.isAnswer = isAnswer;
	}
	public OptionDetails() {
		// TODO Auto-generated constructor stub
	}
	public boolean isValid()
	{
		if(optionId <= 0 || questionId <= 0 || text == null)
		{
			Log.v(LOG, "questionId " + questionId + " and text is "+ text);
			return false;
		}
		return true;
	}

    public boolean isAttempted() {
        return isAttempted;
    }

    public void setAttempted(boolean isAttempted) {
        this.isAttempted = isAttempted;
    }

}
