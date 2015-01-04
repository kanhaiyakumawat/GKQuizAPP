package com.kanhaiyakumawat.androidapps.latestgkquiz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kanhaiyakumawat.androidapps.sqlite.helper.DatabaseHelper;
import com.kanhaiyakumawat.androidapps.sqlite.model.OptionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionType;

public class QuizPlayActivity extends Activity implements OnClickListener {

    private static final String LOG = "QuizPlayActivity";
    List<QuestionType> question_types;
    LinearLayout linear_layout;
    List<QuestionDetails> question_list;
    //private RelativeLayout.LayoutParams lpButton = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    private LayoutParams lpButton = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private static int curr_question_id = 0;

    private static int num_of_options = 0;
    private static int num_of_questions_attempted = 0;
    private static int num_of_questions_answered_fully_correct = 0;
    private static int num_of_questions_answered_partially_correct = 0;
    private static int num_of_questions_answered_wrong = 0;

    private void display_question() {
        Log.v(LOG, "displaying question " + Integer.toString(curr_question_id)
                + " question size " + Integer.toString(question_list.size()));
        num_of_options = 0;
        linear_layout.removeAllViews();
        TextView question_text = new TextView(getApplicationContext());
        if (curr_question_id < question_list.size()) {
            num_of_questions_attempted++;
            QuestionDetails question = question_list.get(curr_question_id);
            question_text.setText("Q" + (curr_question_id + 1) + ". "
                    + question.getQuestionText()
                    + " [Choose All Correct Options] ");
            question_text.setTextColor(getResources().getColor(R.color.black));
            question_text.setPadding(8, 8, 8, 8);
            question_text.setTextSize(18);
            question_text.setTypeface(null, Typeface.BOLD_ITALIC);
            linear_layout.addView(question_text);
            List<OptionDetails> options = question.getOptionDetails();
            num_of_options = options.size();
            char op = 'A';
            for (int i = 0; i < options.size(); i++) {
                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setId(i + 1);
                cb.setText("(" + op + ") " + options.get(i).getText());
                cb.setTextColor(getResources().getColor(R.color.black));
                cb.setTextSize(16);
                cb.setTypeface(null, Typeface.ITALIC);
                linear_layout.addView(cb);
                op++;
            }
            Button checkAnswerButton = new Button(getApplicationContext());
            checkAnswerButton.setId(getResources().getInteger(R.integer.check_answer_button_id));
            checkAnswerButton.setText(R.string.check_answer);
            checkAnswerButton.setOnClickListener(this);
            checkAnswerButton.setBackgroundColor(getResources().getColor(
                    R.color.Chocolate));
            checkAnswerButton.setLayoutParams(lpButton);
            linear_layout.addView(checkAnswerButton);
        } else {
            curr_question_id = 0;
            Toast.makeText(
                    getApplicationContext(),
                    "Your are done with all the questions!! check your answers",
                    Toast.LENGTH_LONG).show();
            linear_layout.removeAllViews();
            TextView total_attempted_question = new TextView(
                    getApplicationContext());
            TextView total_answered_fully_correct = new TextView(
                    getApplicationContext());
            TextView total_amswered_partially_correct = new TextView(
                    getApplicationContext());
            TextView total_answered_wrong = new TextView(
                    getApplicationContext());
            TextView score = new TextView(getApplicationContext());

            score.setText("----- Score Analysis ----");
            score.setTextSize(16);
            total_attempted_question.setText("Questions Attempted : "
                    + num_of_questions_attempted);
            total_attempted_question.setTextSize(12);
            total_answered_fully_correct.setText("Answered fully correct : "
                    + num_of_questions_answered_fully_correct);
            total_answered_fully_correct.setTextSize(12);
            total_amswered_partially_correct
                    .setText("Answered partially correct : "
                            + num_of_questions_answered_partially_correct);
            total_amswered_partially_correct.setTextSize(12);
            total_answered_wrong.setText("Answered Wrong : "
                    + num_of_questions_answered_wrong);
            total_answered_wrong.setTextSize(12);
            score.setTextColor(getResources().getColor(R.color.black));
            total_attempted_question.setTextColor(getResources().getColor(
                    R.color.black));
            total_answered_fully_correct.setTextColor(getResources().getColor(
                    R.color.black));
            total_amswered_partially_correct.setTextColor(getResources()
                    .getColor(R.color.black));
            total_answered_wrong.setTextColor(getResources().getColor(
                    R.color.black));
            Button exit = new Button(getApplicationContext());
            exit.setText(R.string.exit_quiz);
            exit.setId(getResources().getInteger(R.integer.exit_quiz_button_id));
            exit.setOnClickListener(this);
            exit.setBackgroundColor(getResources().getColor(R.color.Chocolate));
            exit.setLayoutParams(lpButton);
            linear_layout.addView(score);
            linear_layout.addView(total_attempted_question);
            linear_layout.addView(total_answered_fully_correct);
            linear_layout.addView(total_amswered_partially_correct);
            linear_layout.addView(total_answered_wrong);
            linear_layout.addView(exit);
            Result result = new Result();
            result.setDate(new Date());
            result.setFully_correct(num_of_questions_answered_fully_correct);
            result.setFully_wrong(num_of_questions_answered_wrong);
            result.setPartially_correct(num_of_questions_answered_partially_correct);
            result.setTotal_questions(num_of_questions_attempted);
            PreviousResults previousResult = new PreviousResults(
                    getApplicationContext());
            previousResult.addResult(result);
        }

    }

    @Override
    public void onClick(View arg0) {
        Log.v(LOG, "view id " + Integer.toString(arg0.getId())
                + " start button id " + Integer.toString(getResources().getInteger(R.integer.start_quiz_button_id)));
        if (arg0.getId() == getResources().getInteger(R.integer.start_quiz_button_id)) {
            List<String> topics = new ArrayList<String>();
            for (int i = 0; i < question_types.size(); i++) {
                Log.v(LOG, "checkin each checkbox");
                CheckBox cb = (CheckBox) findViewById(i + 1);
                if (cb.isChecked()) {
                    Log.v(LOG, " CheckBox is selected ");
                    topics.add(question_types.get(i).getQuestionType());
                }
            }
            if (topics.size() > 0) {
                Log.v(LOG, "going for db search for topices");
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                String key = getResources().getString(
                        R.string.number_of_questions_in_quiz);
                long no_of_question_in_quiz = preferences.getLong(
                        key,
                        getResources().getInteger(
                                R.integer.max_default_questions_in_quiz));

                DatabaseHelper db = DatabaseHelper
                        .getInstance(getApplicationContext());
                question_list = db.getQuestionDetailsForTopics(topics,
                        no_of_question_in_quiz);
                Log.v(LOG,
                        "total questions "
                                + Integer.toString(question_list.size()));
                linear_layout.removeAllViews();
                display_question();
            } else {
                Log.v(LOG, " no topics selected");
            }

        } else if (arg0.getId() == getResources().getInteger(R.integer.check_answer_button_id)) {
            boolean isCorrect = false;
            boolean isWrong = false;
            for (int i = 0; i < num_of_options; i++) {
                CheckBox cb = (CheckBox) findViewById(i + 1);
                if (cb.isChecked()) {
                    Log.v(LOG,
                            " CheckBox is checked "
                                    + cb.getText()
                                    + " and its answer "
                                    + question_list.get(curr_question_id)
                                    .getOptionDetails().get(i)
                                    .isAnswer());
                    if (question_list.get(curr_question_id).getOptionDetails()
                            .get(i).isAnswer()) {
                        isCorrect = true;
                        cb.setBackgroundColor(getResources().getColor(
                                R.color.DarkGreen));
                    } else {
                        isWrong = true;
                        cb.setBackgroundColor(getResources().getColor(
                                R.color.red));
                    }

                } else {
                    if (question_list.get(curr_question_id).getOptionDetails()
                            .get(i).isAnswer()) {
                        isWrong = true;
                        cb.setBackgroundColor(getResources().getColor(
                                R.color.DarkOrange));
                    } else {
                        // cb.setBackgroundColor();
                    }
                }
            }
            QuestionDetails question = question_list.get(curr_question_id);
            question.setUserAttempts(question.getUserAttempts() + 1);
            if (isCorrect && isWrong == false) {
                num_of_questions_answered_fully_correct++;

                question.setSuccessfulAttempts(question.getSuccessfulAttempts() + 1);
                Toast.makeText(getApplicationContext(),
                        "Awesome!!! You got it right!!", Toast.LENGTH_LONG)
                        .show();
            } else if (isCorrect && isWrong == true) {
                num_of_questions_answered_partially_correct++;
                Toast.makeText(getApplicationContext(),
                        "You Partially answered correctly!!", Toast.LENGTH_LONG)
                        .show();
            } else {
                num_of_questions_answered_wrong++;
                Toast.makeText(getApplicationContext(), "Its Incorrect!!",
                        Toast.LENGTH_LONG).show();
            }
            DatabaseHelper db = DatabaseHelper
                    .getInstance(getApplicationContext());
            db.updateUserAttempts(question);

            Button check_answer_button = (Button) findViewById(getResources().getInteger(R.integer.check_answer_button_id));
            linear_layout.removeView(check_answer_button);
            Button next_question_button = new Button(getApplicationContext());
            next_question_button.setText("Next Question");
            next_question_button.setId(getResources().getInteger(R.integer.next_question_button_id));
            next_question_button.setOnClickListener(this);
            next_question_button.setBackgroundColor(getResources().getColor(
                    R.color.Chocolate));
            next_question_button.setLayoutParams(lpButton);
            linear_layout.addView(next_question_button);
        } else if (arg0.getId() == getResources().getInteger(R.integer.next_question_button_id)) {
            curr_question_id++;
            display_question();
        } else if (arg0.getId() == getResources().getInteger(R.integer.exit_quiz_button_id)) {
            num_of_questions_attempted = 0;
            num_of_questions_answered_fully_correct = 0;
            num_of_questions_answered_partially_correct = 0;
            num_of_questions_answered_wrong = 0;

            linear_layout.removeAllViews();
            super.finish();
        }

    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.quizplay_layout);
        linear_layout = (LinearLayout) findViewById(R.id.home_layout);
        try {
            lpButton.setMargins(30, 20, 20, 20);
            Log.v(LOG, "--- START ---");
            num_of_questions_attempted = 0;
            num_of_questions_answered_fully_correct = 0;
            num_of_questions_answered_partially_correct = 0;
            num_of_questions_answered_wrong = 0;

            DatabaseHelper dbhelper = DatabaseHelper
                    .getInstance(getApplicationContext());
            question_types = dbhelper.getAllQuestionTypes();
            for (int i = 0; i < question_types.size(); i++) {

                CheckBox cb = new CheckBox(this);
                cb.setId(i + 1);
                cb.setText(question_types.get(i).getTypeName().toCharArray(),
                        0, question_types.get(i).getTypeName().length());
                linear_layout.addView(cb);
            }
            Button startQuizButton = new Button(this);
            startQuizButton.setId(getResources().getInteger(R.integer.start_quiz_button_id));
            startQuizButton.setText(R.string.start_quiz);
            startQuizButton.setOnClickListener(this);
            startQuizButton.setBackgroundColor(getResources().getColor(
                    R.color.Chocolate));
            startQuizButton.setLayoutParams(lpButton);
            linear_layout.addView(startQuizButton);
            AdView adView = (AdView) this.findViewById(R.id.adView);
            adView.setAdListener(new MyAdListener());
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build();

            adView.loadAd(adRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
