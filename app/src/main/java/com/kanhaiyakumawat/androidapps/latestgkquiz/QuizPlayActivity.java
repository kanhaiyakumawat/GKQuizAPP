package com.kanhaiyakumawat.androidapps.latestgkquiz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kanhaiyakumawat.androidapps.sqlite.helper.DatabaseHelper;
import com.kanhaiyakumawat.androidapps.sqlite.model.OptionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuizPlayActivity extends Activity implements OnClickListener {

    private static final String LOG = "QuizPlayActivity";
    private static final String CORRECT = "Correct";
    private static final String WRONG = "Wrong";
    private static final String SKIPPED = "Skipped";

    List<QuestionType> question_types;
    LinearLayout linear_layout;
    List<QuestionDetails> question_list;
    ArrayList<QuestionDetails> attempted_list = new ArrayList<QuestionDetails>();

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
            checkAnswerButton.setId(getResources().getInteger(R.integer.next_question_button_id));
            checkAnswerButton.setText(R.string.next_question);
            checkAnswerButton.setOnClickListener(this);
            checkAnswerButton.setBackgroundColor(getResources().getColor(
                    R.color.Chocolate));
            checkAnswerButton.setLayoutParams(lpButton);
            checkAnswerButton.setBackground(getResources().getDrawable(R.drawable.button_background));
            linear_layout.addView(checkAnswerButton);
        } else {
            curr_question_id = 0;

            linear_layout.removeAllViews();
            ListView results = new ListView(getApplicationContext());
            ArrayList<ResultSummaryObject> objects = new ArrayList<ResultSummaryObject>();
            ResultSummaryObject object = new ResultSummaryObject("Total Questions", Integer.toString(num_of_questions_attempted));
            objects.add(object);
            object = new ResultSummaryObject("Correct Answers", Integer.toString(num_of_questions_answered_fully_correct));
            objects.add(object);
            object = new ResultSummaryObject("Partially Correct ", Integer.toString(num_of_questions_answered_partially_correct));
            objects.add(object);
            object = new ResultSummaryObject("Wrong Answers", Integer.toString(num_of_questions_answered_wrong));
            objects.add(object);
            ResultSummaryAdapter ResultSummaryAdapter = new ResultSummaryAdapter(this, objects);
            results.setAdapter(ResultSummaryAdapter);
            ResultSummaryAdapter.notifyDataSetChanged();
            linear_layout.addView(results);

            Button exit = new Button(getApplicationContext());
            exit.setText(R.string.exit_quiz);
            exit.setId(getResources().getInteger(R.integer.exit_quiz_button_id));
            exit.setOnClickListener(this);
            exit.setBackgroundColor(getResources().getColor(R.color.Chocolate));
            exit.setLayoutParams(lpButton);
            exit.setBackground(getResources().getDrawable(R.drawable.button_background));

            linear_layout.addView(exit);
            ListView detailResults = new ListView(getApplicationContext());
            ArrayList<DetailedResultObject> detailedResultObjects = new ArrayList<DetailedResultObject>();

            for (int i = 0; i < attempted_list.size(); i++) {
                List<String> options = new ArrayList<String>();
                for (int j = 0; j < attempted_list.get(i).getOptionDetails().size(); j++) {
                    options.add(attempted_list.get(i).getOptionDetails().get(j).getText());
                }
                DetailedResultObject detailedResultObject = new DetailedResultObject("[Right]", attempted_list.get(i).getQuestionText(), options);
                detailedResultObjects.add(detailedResultObject);
            }
            DetailedResultAdapter detailedResultAdapter = new DetailedResultAdapter(this, attempted_list);
            detailResults.setAdapter(detailedResultAdapter);
            detailedResultAdapter.notifyDataSetChanged();
            linear_layout.addView(detailResults);


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
            } else {
                Log.v(LOG, " no topics selected");
            }

        } else if (arg0.getId() == getResources().getInteger(R.integer.next_question_button_id)) {
            boolean isCorrect = false;
            boolean isWrong = false;
            boolean isSkipped = true;
            QuestionDetails curr_question = question_list.get(curr_question_id);
            for (int i = 0; i < num_of_options; i++) {
                CheckBox cb = (CheckBox) findViewById(i + 1);
                if (cb.isChecked()) {
                    isSkipped = false;
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
                    } else {
                        isWrong = true;
                    }

                } else {
                    if (question_list.get(curr_question_id).getOptionDetails()
                            .get(i).isAnswer()) {
                        isWrong = true;
                    } else {
                        // cb.setBackgroundColor();
                    }
                }
            }
            QuestionDetails question = question_list.get(curr_question_id);
            question.setUserAttempts(question.getUserAttempts() + 1);
            attempted_list.add(curr_question);
            if (isCorrect && isWrong == false) {
                question.setCurrentAttemptStatus(CORRECT);
                num_of_questions_answered_fully_correct++;

                question.setSuccessfulAttempts(question.getSuccessfulAttempts() + 1);
            } else if (isCorrect && isWrong == true) {
                num_of_questions_answered_partially_correct++;
                question.setCurrentAttemptStatus(WRONG);

            } else {
                num_of_questions_answered_wrong++;
                question.setCurrentAttemptStatus(WRONG);
            }
            if (isSkipped) {
                question.setCurrentAttemptStatus(SKIPPED);
            }

            DatabaseHelper db = DatabaseHelper
                    .getInstance(getApplicationContext());
            db.updateUserAttempts(question);

            Button check_answer_button = (Button) findViewById(getResources().getInteger(R.integer.next_question_button_id));
            linear_layout.removeView(check_answer_button);
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

    public class ResultSummaryObject {

        private String prop1;
        private String prop2;

        public ResultSummaryObject(String prop1, String prop2) {
            this.prop1 = prop1;
            this.prop2 = prop2;
        }

        public String getProp1() {
            return prop1;
        }

        public String getProp2() {
            return prop2;
        }
    }

    public class ResultSummaryAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ArrayList<ResultSummaryObject> objects;

        private class ViewHolder {
            TextView textView1;
            TextView textView2;
        }

        public ResultSummaryAdapter(Context context, ArrayList<ResultSummaryObject> objects) {
            inflater = LayoutInflater.from(context);
            this.objects = objects;
        }

        public int getCount() {
            return objects.size();
        }

        public ResultSummaryObject getItem(int position) {
            return objects.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Log.v(LOG, "getView for results");
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.result_list_layout, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.result_name);
                holder.textView2 = (TextView) convertView.findViewById(R.id.result_value);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView1.setText(objects.get(position).getProp1());
            holder.textView2.setText(objects.get(position).getProp2());
            return convertView;
        }
    }

    public class DetailedResultObject {

        private String result;
        private String question;
        private List<String> options;

        public DetailedResultObject(String result, String question, List<String> options) {
            this.result = result;
            this.question = question;
            this.options = options;
        }

        public String getResult() {
            return result;
        }

        public String getQuestion() {
            return question;
        }

        public List<String> getOptions() {
            return options;
        }
    }

    public class DetailedResultAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ArrayList<QuestionDetails> objects;


        private class ViewHolder {
            TextView resultView;
            TextView questionView;
            List<CheckBox> options;
        }

        public DetailedResultAdapter(Context context, ArrayList<QuestionDetails> objects) {
            inflater = LayoutInflater.from(context);
            this.objects = objects;

        }

        public int getCount() {
            return objects.size();
        }

        public QuestionDetails getItem(int position) {
            return objects.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Log.v(LOG, "getView for results");
            ViewHolder holder = null;

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detailed_result_list_layout, null);
            LinearLayout detailed_list_layout = (LinearLayout) convertView.findViewById(R.id.detailed_list_layout);
            TextView resultView = new TextView(getApplicationContext());
            resultView.setTextColor(getResources().getColor(R.color.black));
            resultView.setTextSize(24);
            resultView.setPadding(12, 12, 12, 5);
            TextView questionView = new TextView(getApplicationContext());
            questionView.setTextColor(getResources().getColor(R.color.black));
            questionView.setTextSize(24);
            questionView.setPadding(12, 12, 12, 12);
            holder.questionView = questionView;


            holder.resultView = resultView;
            holder.options = new ArrayList<CheckBox>(objects.get(position).getOptionDetails().size());
            detailed_list_layout.addView(resultView);
            detailed_list_layout.addView(questionView);

            for (int i = 0; i < objects.get(position).getOptionDetails().size(); i++) {

                CheckBox checkBox = new CheckBox(getApplicationContext());


                checkBox.setPadding(25, 5, 20, 5);
                checkBox.setTextColor(getResources().getColor(R.color.black));
                checkBox.setTextSize(18);

                detailed_list_layout.addView(checkBox);
                holder.options.add(checkBox);
            }
            //holder.optionImages = new ArrayList<ImageView>(objects.get(position).getOptions().size());
            //holder.options = new ArrayList<TextView>(objects.get(position).getOptions().size());
            convertView.setTag(holder);

            //holder.textView1.setText(objects.get(position).getProp1());
            //holder.textView2.setText(objects.get(position).getProp2());
            holder.resultView.setText("[" + objects.get(position).getCurrentAttemptStatus() + "]");
            if (objects.get(position).getCurrentAttemptStatus() == CORRECT) {
                holder.resultView.setTextColor(getResources().getColor(R.color.green));
            } else {
                holder.resultView.setTextColor(getResources().getColor(R.color.red));
            }
            holder.questionView.setText("Q" + (position + 1) + ". " + objects.get(position).getQuestionText());
            char op = 'A';
            for (int i = 0; i < objects.get(position).getOptionDetails().size(); i++) {
                Log.v(LOG, "holder options size " + holder.options.size() + " and options from object " + objects.get(position).getOptionDetails().size());
                holder.options.get(i).setText("(" + op + ") " + objects.get(position).getOptionDetails().get(i).getText());
                holder.options.get(i).setChecked(false);

                if (objects.get(position).getOptionDetails().get(i).isAnswer()) {
                    holder.options.get(i).setButtonDrawable(getResources().getDrawable(R.drawable.correct_symbol));
                } else if (objects.get(position).getOptionDetails().get(i).isAttempted()) {
                    holder.options.get(i).setButtonDrawable(getResources().getDrawable(R.drawable.wrong_symbol));
                } else {
                    holder.options.get(i).setButtonDrawable(getResources().getDrawable(R.drawable.empty_check_box_symbol));
                }
                holder.options.get(i).setEnabled(false);
                op++;
            }
            return convertView;
        }
    }
}
