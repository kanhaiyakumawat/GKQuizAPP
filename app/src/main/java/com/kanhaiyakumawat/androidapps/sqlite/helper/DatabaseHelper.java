package com.kanhaiyakumawat.androidapps.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kanhaiyakumawat.androidapps.sqlite.model.OptionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionType;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper s_instance;
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Table Names
    private static final String TABLE_QUESTION_TYPE = "question_type";
    private static final String TABLE_QUESTION_DETAILS = "question_details";
    private static final String TABLE_OPTION_DETAILS = "option_details";

    // Table columns common
    private static final String TABLE_COLUMN_ID = "id";

    // Table columns for question_type
    private static final String TABLE_COLUMN_QUESTION_TYPE = "question_type";
    private static final String TABLE_COLUMN_TYPE_DESCRIPTION = "type_description";
    private static final String TABLE_COLUMN_FILE_NAME = "file_name";
    private static final String TABLE_COLUMN_VERSION = "version";

    // Table columns for question_details
    private static final String TABLE_COLUMN_QUESTION_ID = "question_id";
    private static final String TABLE_COLUMN_QUESTION_TEXT = "text";
    private static final String TABLE_COLUMN_USER_ATTEMPTS = "user_attempts";
    private static final String TABLE_COLUMN_USER_SUCCESSFUL_ATTEMPTS = "sucessfull_attempts";

    // Table columns for option_details
    private static final String TABLE_COLUMN_OPTION_ID = "option_id";
    private static final String TABLE_COLUMN_OPTION_TEXT = "text";
    private static final String TABLE_COLUMN_IS_ANSWER = "is_answer";

    private static final String CREATE_TABLE_QUESTION_TYPE = "CREATE TABLE "
            + TABLE_QUESTION_TYPE + "(" + TABLE_COLUMN_QUESTION_TYPE
            + " TEXT PRIMARY KEY, " + TABLE_COLUMN_TYPE_DESCRIPTION + " TEXT, "
            + TABLE_COLUMN_FILE_NAME + " TEXT, " + TABLE_COLUMN_VERSION
            + " TEXT)";

    private static final String CREATE_TABLE_QUESTION_DETAILS = "CREATE TABLE "
            + TABLE_QUESTION_DETAILS + "(" + TABLE_COLUMN_QUESTION_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + TABLE_COLUMN_QUESTION_TYPE + " TEXT, "
            + TABLE_COLUMN_QUESTION_TEXT + " TEXT, "
            + TABLE_COLUMN_USER_ATTEMPTS + " INTEGER, "
            + TABLE_COLUMN_USER_SUCCESSFUL_ATTEMPTS + " INTEGER)";

    private static final String CREATE_TABLE_OPTION_DETAILS = "CREATE TABLE "
            + TABLE_OPTION_DETAILS + "(" + TABLE_COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + TABLE_COLUMN_QUESTION_ID + " INTEGER," + TABLE_COLUMN_OPTION_ID
            + " INTEGER, " + TABLE_COLUMN_OPTION_TEXT + " TEXT, "
            + TABLE_COLUMN_IS_ANSWER + " INTEGER)";

    private static final String SELECT_ALL_QUESTION_TYPES = "SELECT * "
            + "FROM " + TABLE_QUESTION_TYPE;

    private static final String SELECT_QUESTION_TYPE_COUNT = "SELECT COUNT(*) AS CNT FROM "
            + TABLE_QUESTION_TYPE;

    private static final String SELECT_QUESTION_DETAILS_FOR_TOPICS = "SELECT * "
            + "FROM " + TABLE_QUESTION_DETAILS;

    private static final String SELECT_OPTIONS_FOR_QUESTION = "SELECT * "
            + "FROM " + TABLE_OPTION_DETAILS;

    public static DatabaseHelper getInstance(Context context) {
        if (s_instance == null) {
            s_instance = new DatabaseHelper(context);
        }
        return s_instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating tables
        Log.v(LOG, "onCreate Table creation");
        db.execSQL(CREATE_TABLE_QUESTION_TYPE);
        db.execSQL(CREATE_TABLE_QUESTION_DETAILS);
        db.execSQL(CREATE_TABLE_OPTION_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // On upgrade drop older tables
        Log.v(LOG, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION_TYPE);
        db.execSQL(CREATE_TABLE_QUESTION_TYPE);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION_DETAILS);
        db.execSQL(CREATE_TABLE_QUESTION_DETAILS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTION_DETAILS);
        db.execSQL(CREATE_TABLE_OPTION_DETAILS);
    }

    public long createQuestionType(QuestionType questionType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_COLUMN_QUESTION_TYPE, questionType.getQuestionType());
        values.put(TABLE_COLUMN_TYPE_DESCRIPTION, questionType.getTypeName());
        values.put(TABLE_COLUMN_FILE_NAME, questionType.getQuestionFile());
        values.put(TABLE_COLUMN_VERSION, questionType.getVersion());
        long type_id = db.insert(TABLE_QUESTION_TYPE, null, values);
        return type_id;
    }

    public List<QuestionType> getAllQuestionTypes() {
        List<QuestionType> question_types = new ArrayList<QuestionType>();
        Log.v(LOG, SELECT_ALL_QUESTION_TYPES);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(SELECT_ALL_QUESTION_TYPES, null);
        if (cur.moveToFirst()) {
            do {
                QuestionType questionType = new QuestionType();
                questionType.setQuestionType(cur.getString(cur
                        .getColumnIndex(TABLE_COLUMN_QUESTION_TYPE)));
                questionType.setTypeName(cur.getString(cur
                        .getColumnIndex(TABLE_COLUMN_TYPE_DESCRIPTION)));
                questionType.setQuestionFile(cur.getString(cur
                        .getColumnIndex(TABLE_COLUMN_FILE_NAME)));
                questionType.setVersion(cur.getString(cur
                        .getColumnIndex(TABLE_COLUMN_VERSION)));
                question_types.add(questionType);
            } while (cur.moveToNext());
        }
        return question_types;
    }

    public long getQuestionTypeCount() {
        Log.v(LOG, SELECT_QUESTION_TYPE_COUNT);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(SELECT_QUESTION_TYPE_COUNT, null);
        if (cur.moveToFirst()) {
            return cur.getLong(cur.getColumnIndex("CNT"));
        } else {
            return -1;
        }
    }

    public void updateQuestionType(QuestionType questionType) {
        Log.v(LOG, "UPDATE_QUESTION_TYPE");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_COLUMN_QUESTION_TYPE, questionType.getQuestionType());
        values.put(TABLE_COLUMN_TYPE_DESCRIPTION, questionType.getTypeName());
        values.put(TABLE_COLUMN_FILE_NAME, questionType.getQuestionFile());
        values.put(TABLE_COLUMN_VERSION, questionType.getVersion());
        String whereClause = TABLE_COLUMN_QUESTION_TYPE + " = "
                + questionType.getQuestionType();
        db.update(TABLE_QUESTION_TYPE, values, whereClause, null);
    }

    public boolean createQuestionDetails(QuestionDetails quesitonDetails) {
        Log.v(LOG, "createQuestionDetails");
        if (questionIdExists(quesitonDetails.getQuestionId())) {
            Log.v(LOG, "row already exists so returning");
            return true;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues question_values = new ContentValues();
            question_values.put(TABLE_COLUMN_QUESTION_ID,
                    quesitonDetails.getQuestionId());
            question_values.put(TABLE_COLUMN_QUESTION_TYPE,
                    quesitonDetails.getQuestionType());
            question_values.put(TABLE_COLUMN_QUESTION_TEXT,
                    quesitonDetails.getQuestionText());
            question_values.put(TABLE_COLUMN_USER_ATTEMPTS,
                    quesitonDetails.getUserAttempts());
            question_values.put(TABLE_COLUMN_USER_SUCCESSFUL_ATTEMPTS,
                    quesitonDetails.getSuccessfulAttempts());
            List<OptionDetails> options = quesitonDetails.getOptionDetails();
            ContentValues option_values = new ContentValues();
            for (int i = 0; i < options.size(); i++) {
                option_values.put(TABLE_COLUMN_QUESTION_ID,
                        quesitonDetails.getQuestionId());
                option_values.put(TABLE_COLUMN_OPTION_TEXT, options.get(i)
                        .getText());
                int answer = options.get(i).isAnswer() ? 1 : 0;
                Log.v(LOG, "is this option answer " + answer);
                option_values.put(TABLE_COLUMN_IS_ANSWER, answer);
                db.insert(TABLE_OPTION_DETAILS, null, option_values);
            }
            db.insert(TABLE_QUESTION_DETAILS, null, question_values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.endTransaction();
        return true;
    }

    public boolean createQuestionDetails(List<QuestionDetails> questionDetailsList, QuestionType questionType) {
        Log.v(LOG, "createQuestionDetails Updated");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            long deletedRows = db.delete(TABLE_QUESTION_DETAILS, TABLE_COLUMN_QUESTION_TYPE + " = ?", new String[]{questionType.getQuestionType()});
            Log.v(LOG, "deleted rows are " + deletedRows + " for question topic " + questionType.getQuestionType());
            for (int j = 0; j < questionDetailsList.size(); j++) {
                QuestionDetails questionDetails = questionDetailsList.get(j);
                ContentValues question_values = new ContentValues();
                question_values.put(TABLE_COLUMN_QUESTION_TYPE,
                        questionDetails.getQuestionType());
                question_values.put(TABLE_COLUMN_QUESTION_TEXT,
                        questionDetails.getQuestionText());
                question_values.put(TABLE_COLUMN_USER_ATTEMPTS,
                        questionDetails.getUserAttempts());
                question_values.put(TABLE_COLUMN_USER_SUCCESSFUL_ATTEMPTS,
                        questionDetails.getSuccessfulAttempts());
                List<OptionDetails> options = questionDetails.getOptionDetails();
                ContentValues option_values = new ContentValues();
                long questionId = db.insert(TABLE_QUESTION_DETAILS, null, question_values);
                Log.v(LOG, "question id " + questionId);
                for (int i = 0; i < options.size(); i++) {
                    option_values.put(TABLE_COLUMN_QUESTION_ID,
                            questionId);
                    option_values.put(TABLE_COLUMN_OPTION_TEXT, options.get(i)
                            .getText());
                    int answer = options.get(i).isAnswer() ? 1 : 0;
                    Log.v(LOG, "is this option answer " + answer);
                    option_values.put(TABLE_COLUMN_IS_ANSWER, answer);
                    long optionId = db.insert(TABLE_OPTION_DETAILS, null, option_values);
                    Log.v(LOG, "option id is " + optionId);
                }
            }
            Log.v(LOG, "committing all rows");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(LOG, "exception while inserting " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        db.endTransaction();
        return true;
    }

    public boolean updateUserAttempts(QuestionDetails quesitonDetails) {
        Log.v(LOG, "updateUserAttempts");
        if (!questionIdExists(quesitonDetails.getQuestionId())) {
            Log.v(LOG, "row doesn't exists so returning");
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues question_values = new ContentValues();

            question_values.put(TABLE_COLUMN_USER_ATTEMPTS,
                    quesitonDetails.getUserAttempts());
            question_values.put(TABLE_COLUMN_USER_SUCCESSFUL_ATTEMPTS,
                    quesitonDetails.getSuccessfulAttempts());
            String whereClause = TABLE_COLUMN_QUESTION_ID + " = " + quesitonDetails.getQuestionId();
            db.update(TABLE_QUESTION_DETAILS, question_values, whereClause, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.endTransaction();
        return true;
    }

    public List<QuestionDetails> getQuestionDetailsForTopics(List<String> topics, long no_of_question_in_quiz) {
        List<QuestionDetails> questionDetailsList = new ArrayList<QuestionDetails>();
        Log.v(LOG, SELECT_QUESTION_DETAILS_FOR_TOPICS);
        String topic_list = "";
        int i = 0;
        for (i = 0; i < topics.size() - 1; i++) {
            topic_list += "'" + topics.get(i) + "',";
        }
        topic_list += "'" + topics.get(i) + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        String sub_query = " WHERE " + TABLE_COLUMN_QUESTION_TYPE + " IN ("
                + topic_list + ") ORDER BY " + TABLE_COLUMN_USER_SUCCESSFUL_ATTEMPTS + ", " + TABLE_COLUMN_USER_ATTEMPTS + " ASC LIMIT " + no_of_question_in_quiz;
        Log.v(LOG, SELECT_QUESTION_DETAILS_FOR_TOPICS + sub_query);
        Cursor cur = db.rawQuery(
                SELECT_QUESTION_DETAILS_FOR_TOPICS + sub_query, null);
        if (cur.moveToFirst()) {
            do {
                Log.v(LOG,
                        "-- Fetching Question Details for Question ID "
                                + cur.getLong(cur
                                .getColumnIndex(TABLE_COLUMN_QUESTION_ID)));
                QuestionDetails questionDetails = new QuestionDetails();
                questionDetails.setQuestionType(cur.getString(cur
                        .getColumnIndex(TABLE_COLUMN_QUESTION_TYPE)));
                questionDetails.setQuestionId(cur.getLong(cur
                        .getColumnIndex(TABLE_COLUMN_QUESTION_ID)));
                questionDetails.setQuestionText(cur.getString(cur
                        .getColumnIndex(TABLE_COLUMN_QUESTION_TEXT)));
                questionDetails.setUserAttempts(cur.getLong(cur
                        .getColumnIndex(TABLE_COLUMN_USER_ATTEMPTS)));
                questionDetails
                        .setSuccessfulAttempts(cur.getLong(cur
                                .getColumnIndex(TABLE_COLUMN_USER_SUCCESSFUL_ATTEMPTS)));

                String sub_qery = " WHERE " + TABLE_COLUMN_QUESTION_ID + " = "
                        + questionDetails.getQuestionId();
                Cursor option_cur = db.rawQuery(SELECT_OPTIONS_FOR_QUESTION
                        + sub_qery, null);
                List<OptionDetails> options = new ArrayList<OptionDetails>();
                if (option_cur.moveToFirst()) {
                    do {
                        Log.v(LOG,
                                "-- Fetching Option Details for Question ID "
                                        + cur.getLong(cur
                                        .getColumnIndex(TABLE_COLUMN_QUESTION_ID)));
                        OptionDetails option = new OptionDetails();
                        option.setId(option_cur.getLong(option_cur
                                .getColumnIndex(TABLE_COLUMN_ID)));
                        option.setQuestionId(option_cur.getLong(option_cur
                                .getColumnIndex(TABLE_COLUMN_QUESTION_ID)));
                        option.setOptionId(option_cur.getLong(option_cur
                                .getColumnIndex(TABLE_COLUMN_OPTION_ID)));
                        option.setText(option_cur.getString(option_cur
                                .getColumnIndex(TABLE_COLUMN_OPTION_TEXT)));
                        option.setAnswer(option_cur.getInt(option_cur
                                .getColumnIndex(TABLE_COLUMN_IS_ANSWER)) == 0 ? false
                                : true);
                        options.add(option);
                    } while (option_cur.moveToNext());
                }
                questionDetails.setOptionDetails(options);
                questionDetailsList.add(questionDetails);
            } while (cur.moveToNext());
        }

        Log.v(LOG, "-- done with fetching quesiton details --");
        return questionDetailsList;
    }

    public boolean questionIdExists(long questionId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT QUESTION_ID FROM QUESTION_DETAILS WHERE QUESTION_ID = "
                    + questionId;
            Cursor cur = db.rawQuery(query, null);
            if (cur.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }
}
