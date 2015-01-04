package com.kanhaiyakumawat.androidapps.latestgkquiz;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionType;

public class HandleQuestionTypeXML {
	private static final String QUESTION_TYPE_LIST_TAG = "QuestionTypeList";
	private static final String QUESTION_TYPE_TAG = "QuestionType";
	private static final String TYPE_TAG = "type";
	private static final String NAME_TAG = "name";
	private static final String QUESTION_FILE_TAG = "question_file";
	private static final String VERSION_TAG = "version";
	private static final String LOG = "HandleQuestionTypeXML";
	private String QuestionTypeFileContent;
	private List<QuestionType> questionTypes = new ArrayList<QuestionType>();

	HandleQuestionTypeXML(final String QuestionTypeFileContent) {
		this.QuestionTypeFileContent = QuestionTypeFileContent;
		parseQuestionTypeFile();
	}

	public String getQuestionTypeFileContent() {
		return QuestionTypeFileContent;
	}

	public List<QuestionType> getQuestionTypeList() {
		return questionTypes;
	}

	private void parseQuestionTypeFile() {
		Log.v(LOG, "-- START --");
		int event;
		String type = null;
		String name = null;
		String questionFile = null;
		String text = null;
		String version = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser myParser = factory.newPullParser();

			myParser.setInput(new StringReader(QuestionTypeFileContent));

			Log.v(LOG, "done with setting input");
			event = myParser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				String tag_name = myParser.getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					break;
				case XmlPullParser.TEXT:
					text = myParser.getText();
					break;

				case XmlPullParser.END_TAG:
					if (tag_name.equals(TYPE_TAG)) {
						type = text;
					} else if (tag_name.equals(NAME_TAG)) {
						name = text;
					} else if (tag_name.equals(QUESTION_FILE_TAG)) {
						questionFile = text;
					} else if (tag_name.equals(VERSION_TAG)) {
						version = text;
					} else if (tag_name.equals(QUESTION_TYPE_TAG)) {
						set_question_type(type, name, questionFile, version);
						type = null;
						name = null;
						questionFile = null;
						version = null;
					} else if (tag_name.equals(QUESTION_TYPE_LIST_TAG)) {
						break;
					} else {
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
		}

	}

	private void set_question_type(String type, String name,
			String questionFile, String version) {
		Log.v("set_question_type", "type " + type + "name " + name
				+ " file name " + questionFile);
		QuestionType questionType = new QuestionType();
		questionType.setQuestionType(type);
		questionType.setTypeName(name);
		questionType.setQuestionFile(questionFile);
		questionType.setVersion(version);
		if (!questionType.isValid()) {
			System.out.println("invalid Question Type XML");
		} else {
			Log.v("setQuestinType",
					"Setting QuestinType " + questionType.toString());
			questionTypes.add(questionType);
		}
	}

	public void setQuestionTypeFileContent(String QuestionTypeFileContent) {
		this.QuestionTypeFileContent = QuestionTypeFileContent;
	}
}
