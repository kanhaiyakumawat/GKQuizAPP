package com.kanhaiyakumawat.androidapps.sqlite.model;

public class QuestionType {
	private long id;
	private String questionType;
	private String typeName;
	private String questionFile;
	private String version;
	
	public QuestionType()
	{
		//empty construction
	}
	public QuestionType(int id, String questionType, String typeName, String questionFile)
	{
		this.id = id;
		this.questionType = questionType;
		this.typeName = typeName;
		this.questionFile = questionFile;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getQuestionFile() {
		return questionFile;
	}
	public void setQuestionFile(String questionFile) {
		this.questionFile = questionFile;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public boolean isValid() {
		if (questionType == null || typeName == null || questionFile == null || version == null) {
			return false;
		} else {
			return true;
		}
	}
}
