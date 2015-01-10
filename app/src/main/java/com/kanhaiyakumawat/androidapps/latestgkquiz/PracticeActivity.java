package com.kanhaiyakumawat.androidapps.latestgkquiz;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kanhaiyakumawat.androidapps.sqlite.helper.DatabaseHelper;
import com.kanhaiyakumawat.androidapps.sqlite.model.OptionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionType;

import java.util.ArrayList;
import java.util.List;


public class PracticeActivity extends ActionBarActivity implements OnClickListener {
    private static final String LOG = "PracticeActivity";
    List<QuestionType> question_types;
    LinearLayout linear_layout;
    List<QuestionDetails> question_list;
    //private RelativeLayout.LayoutParams lpButton = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams lpButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
            checkAnswerButton.setBackground(getResources().getDrawable(R.drawable.button_background));
            linear_layout.addView(checkAnswerButton);
        } else {
            curr_question_id = 0;
            Toast.makeText(
                    getApplicationContext(),
                    "Your are done with all the questions!! check your answers",
                    Toast.LENGTH_LONG).show();
            linear_layout.removeAllViews();
            super.finish();

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

            Button check_answer_button = (Button) findViewById(getResources().getInteger(R.integer.check_answer_button_id));
            linear_layout.removeView(check_answer_button);
            Button next_question_button = new Button(getApplicationContext());
            next_question_button.setText("Next Question");
            next_question_button.setId(getResources().getInteger(R.integer.next_question_button_id));
            next_question_button.setOnClickListener(this);
            next_question_button.setBackgroundColor(getResources().getColor(
                    R.color.Chocolate));
            next_question_button.setLayoutParams(lpButton);
            next_question_button.setBackground(getResources().getDrawable(R.drawable.button_background));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_practice, container, false);
            return rootView;
        }
    }
}
