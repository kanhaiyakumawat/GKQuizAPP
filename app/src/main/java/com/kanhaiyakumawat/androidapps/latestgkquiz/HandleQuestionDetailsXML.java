package com.kanhaiyakumawat.androidapps.latestgkquiz;

import android.util.Log;

import com.kanhaiyakumawat.androidapps.sqlite.model.OptionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionDetails;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class HandleQuestionDetailsXML {

	private static final String QUESTION_DETAILS_LIST_TAG = "QuestionDetailsList";
	private static final String QUESTION_DETAILS_TAG = "QuestionDetails";
	private static final String QUESTION_TAG = "question";
	private static final String OPTION_INFO_TAG = "OptionInfo";
	private static final String OPTION_TAG = "Option";;
	private static final String OPTION_TEXT_TAG = "text";
	private static final String IS_ANSWER_TAG = "isAnswer";
	private static final String LOG = "HandleQuestionDetailsXML";
	private String questionDetailsXMLContent;
	private List<QuestionDetails> questionDetailsList = new ArrayList<QuestionDetails>();
	private boolean isValidXML = false;

	public HandleQuestionDetailsXML(String questinDetailsXMLContent) {
		this.questionDetailsXMLContent = questinDetailsXMLContent;
		isValidXML = parseQuestionDetailsXML();
	}

	public List<QuestionDetails> getQuestionDetailsList() {
		return questionDetailsList;
	}

	public String getQuestionDetailsXMLContent() {
		return questionDetailsXMLContent;
	}

	public boolean isValidXML() {
		return isValidXML;
	}

	private boolean parseQuestionDetailsXML() {

		Log.v(LOG, "-- START --");
		int event;
		String question = null;
		String optionText = null;
		boolean isAnswer = false;
		String text = null;
		boolean hasQuestionDetailsListStarted = false;
		boolean hasQuestionDetailsStarted = false;
		boolean hasOptionInfoStarted = false;
		boolean hasOptionStarted = false;

		List<OptionDetails> optionList = new ArrayList<OptionDetails>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser myParser = factory.newPullParser();

			myParser.setInput(new StringReader(questionDetailsXMLContent));

			Log.v(LOG, "done with setting input");
			event = myParser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				String tag_name = myParser.getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (tag_name.equals(QUESTION_DETAILS_LIST_TAG)) {
						hasQuestionDetailsListStarted = true;
					} else if (tag_name.equals(QUESTION_DETAILS_TAG)) {
						hasQuestionDetailsStarted = true;
					} else if (tag_name.equals(OPTION_INFO_TAG)) {
						hasOptionInfoStarted = true;
					} else if (tag_name.equals(OPTION_TAG)) {
						hasOptionStarted = true;
					}
					break;
				case XmlPullParser.TEXT:
					text = myParser.getText();
					break;

				case XmlPullParser.END_TAG:
					Log.v(LOG, "parsing tag " + tag_name + " and text is "
							+ text);
					if (tag_name.equals(QUESTION_TAG)) {
						question = text;
					}else if (tag_name.equals(OPTION_TEXT_TAG)) {
						optionText = text;
					} else if (tag_name.equals(IS_ANSWER_TAG)) {
						isAnswer = text.equals("true") ? true : false;
					} else if (tag_name.equals(OPTION_TAG)) {
						if (hasOptionStarted) {
							Log.v(LOG, "Option tag"
									+ " is it answer " + isAnswer);
							OptionDetails option = new OptionDetails();
							option.setText(optionText);
							option.setAnswer(isAnswer);
							if (option.isValid()) {
								optionList.add(option);
							} else {
								Log.v(LOG, "option is invalid");
								return false;
							}
							isAnswer = false;
							optionText = null;
							hasOptionStarted = false;
						} else {
							Log.v(LOG, "missing Option start tag");
							return false;
						}
					} else if (tag_name.equals(OPTION_INFO_TAG)) {
						if (hasOptionInfoStarted) {
							hasOptionInfoStarted = false;
						} else {
							return false;
						}
					} else if (tag_name.equals(QUESTION_DETAILS_TAG)) {
						if (hasQuestionDetailsStarted) {
							Log.v(LOG, "Got Question Details for quextion");
							QuestionDetails questionDetails = new QuestionDetails();
							questionDetails.setQuestionText(question);
							questionDetails.setUserAttempts(0);
							questionDetails.setUserAttempts(0);
							questionDetails.setOptionDetails(optionList);
							questionDetailsList.add(questionDetails);
							hasQuestionDetailsStarted = false;
							optionList = new ArrayList<OptionDetails>();
						} else {
							Log.v(LOG, "Question Details tag was not started");
							return false;
						}
					} else if (tag_name.equals(QUESTION_DETAILS_LIST_TAG)) {
						if (hasQuestionDetailsListStarted) {
							// do nothing
							hasQuestionDetailsListStarted = false;
						} else {
							return false;
						}
					}else {
						Log.v(LOG, "unknown tag " + tag_name);
					}
					break;
				}
				event = myParser.next();

			}
			// parsingComplete = false;
		} catch (Exception e) {
			Log.v(LOG, "Exception : " + e.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void setQuestionDetailsList(List<QuestionDetails> questionDetailsList) {
		this.questionDetailsList = questionDetailsList;
	}

	public void setQuestionDetailsXMLContent(String questionDetailsXMLContent) {
		this.questionDetailsXMLContent = questionDetailsXMLContent;
	}

	public void setValidXML(boolean isValidXML) {
		this.isValidXML = isValidXML;
	}

}
