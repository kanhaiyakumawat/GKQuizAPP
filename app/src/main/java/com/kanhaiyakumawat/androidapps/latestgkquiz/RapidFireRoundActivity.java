package com.kanhaiyakumawat.androidapps.latestgkquiz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kanhaiyakumawat.androidapps.sqlite.helper.DatabaseHelper;
import com.kanhaiyakumawat.androidapps.sqlite.model.OptionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RapidFireRoundActivity extends Activity implements OnClickListener {

    private static final String LOG = "RapidFireRoundActivity";
    List<QuestionType> question_types;
    LinearLayout linear_layout;
    List<QuestionDetails> question_list;
    List<QuestionDetails> attempted_list = new LinkedList<QuestionDetails>();

    //private RelativeLayout.LayoutParams lpButton = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    private LayoutParams lpButton = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private static int curr_question_id = 0;

    private static int num_of_options = 0;
    private static int num_of_questions_attempted = 0;
    private static int num_of_questions_answered_fully_correct = 0;
    private static int num_of_questions_answered_partially_correct = 0;
    private static int num_of_questions_answered_wrong = 0;

    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    public TextView counter;
    private final long startTime = 7 * 1000;
    private final long interval = 1 * 1000;

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
            checkAnswerButton.setId(getResources().getInteger(R.integer.next_question_button_id));
            checkAnswerButton.setText(R.string.next_question);
            checkAnswerButton.setOnClickListener(this);
            checkAnswerButton.setBackgroundColor(getResources().getColor(
                    R.color.Chocolate));
            checkAnswerButton.setLayoutParams(lpButton);
            checkAnswerButton.setBackground(getResources().getDrawable(R.drawable.button_background));
            linear_layout.addView(checkAnswerButton);
        } else {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                if(counter != null)
                {
                    counter.setVisibility(View.INVISIBLE);
                }
            }
            curr_question_id = 0;

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
            total_attempted_question.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            total_answered_fully_correct.setText("Answered fully correct : "
                    + num_of_questions_answered_fully_correct);
            total_answered_fully_correct.setTextSize(12);
            total_answered_fully_correct.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            total_amswered_partially_correct
                    .setText("Answered partially correct : "
                            + num_of_questions_answered_partially_correct);
            total_amswered_partially_correct.setTextSize(12);
            total_amswered_partially_correct.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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
            exit.setBackground(getResources().getDrawable(R.drawable.button_background));
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
                Log.v(LOG, "checking each checkbox");
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
                countDownTimer.start();
            } else {
                Log.v(LOG, " no topics selected");
            }

        } else if (arg0.getId() == getResources().getInteger(R.integer.next_question_button_id)) {
            countDownTimer.cancel();
            countDownTimer = new MyCountDownTimer(startTime, interval);
            boolean isCorrect = false;
            boolean isWrong = false;
            QuestionDetails curr_question = question_list.get(curr_question_id);
            for (int i = 0; i < num_of_options; i++) {
                CheckBox cb = (CheckBox) findViewById(i + 1);
                if (cb.isChecked()) {
                    curr_question.getOptionDetails().get(i).setAttempted(true);
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
            attempted_list.add(curr_question);
            if (isCorrect && isWrong == false) {
                num_of_questions_answered_fully_correct++;

                question.setSuccessfulAttempts(question.getSuccessfulAttempts() + 1);
            } else if (isCorrect && isWrong == true) {
                num_of_questions_answered_partially_correct++;

            } else {
                num_of_questions_answered_wrong++;
            }
            DatabaseHelper db = DatabaseHelper
                    .getInstance(getApplicationContext());
            db.updateUserAttempts(question);

            Button check_answer_button = (Button) findViewById(getResources().getInteger(R.integer.next_question_button_id));
            linear_layout.removeView(check_answer_button);
            curr_question_id++;
            display_question();
            countDownTimer.start();
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
        this.setContentView(R.layout.activity_rapid_fire_round);
        linear_layout = (LinearLayout) findViewById(R.id.home_layout);
        counter = (TextView) this.findViewById(R.id.counter);
        countDownTimer = new MyCountDownTimer(startTime, interval);
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
            startQuizButton.setBackground(getResources().getDrawable(R.drawable.button_background));
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

    public class MyCountDownTimer extends CountDownTimer {
        MediaPlayer mp;

        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);

        }

        @Override
        public void onFinish() {
            counter.setText("Time's up!");
            Button next = (Button) findViewById(getResources().getInteger(R.integer.next_question_button_id));
            if(next != null) {
                next.performClick();
            }
            //mp.release();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(millisUntilFinished < 3*1000)
            {
                counter.setTextColor(getResources().getColor(R.color.red));
                counter.setSoundEffectsEnabled(true);
            }
            else
            {
                if(mp != null && mp.isPlaying())
                {
                    mp.stop();
                    mp.reset();
                    mp.release();
                }
                try {
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.clock_tick);
                    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                            Log.e(LOG, "error occurred");
                            return false;
                        }
                    });
                    mp.prepare();
                    mp.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(mp.isLooping() != true) {
                    Log.d(LOG, "Problem in Playing Audio");
                }
                counter.setTextColor(getResources().getColor(R.color.black));
            }
            counter.setText("" + millisUntilFinished / 1000);
        }
    }

}