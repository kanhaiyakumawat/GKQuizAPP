package com.kanhaiyakumawat.androidapps.latestgkquiz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.kanhaiyakumawat.androidapps.sqlite.helper.DatabaseHelper;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionDetails;
import com.kanhaiyakumawat.androidapps.sqlite.model.QuestionType;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	class SyncQuestionsTask {
		private Context myContext;
		private static final String LOG = "SyncQuestionsTask";
		private QuestionType questionType;

		public SyncQuestionsTask(Context context, QuestionType questionType) {
			myContext = context;
			this.questionType = questionType;
		}

		protected Boolean syncQuestions() {

			Log.v(LOG, "--------doInBackground-------START---");
			DatabaseHelper.getInstance(myContext);
			try {
				Log.v(LOG,
						"searching questions for topic "
								+ questionType.getQuestionType());
				QuestionType topic = questionType;
				URL url = new URL(topic.getQuestionFile());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(url.openStream()));
				String line = null;
				StringBuilder contents = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					contents.append(line);
				}
				Log.v(LOG, "contents " + contents.toString());
				HandleQuestionDetailsXML questionDetailsParser = new HandleQuestionDetailsXML(
						contents.toString());
				if (questionDetailsParser.isValidXML()) {
					DatabaseHelper dbHelper = DatabaseHelper
							.getInstance(myContext);
					List<QuestionDetails> questionDetailsList = questionDetailsParser
							.getQuestionDetailsList();
					Log.v(LOG, "got the question details total questions are "
							+ questionDetailsList.size());
					for (int i = 0; i < questionDetailsList.size(); i++) {
						questionDetailsList.get(i).setQuestionType(
								topic.getQuestionType());
						dbHelper.createQuestionDetails(questionDetailsList
								.get(i));
					}
				} else {
					Log.v(LOG, "invalid XML");
					return false;
				}

			} catch (Exception ex) {
				Log.v("EXCEPTION", ex.getMessage());
				return false;
			}
			return true;
		}

	}

	private class SyncQuestionTypeTask extends
			AsyncTask<Object, Object, Boolean> {
		private Context myContext;
		private static final String LOG = "SyncQuestionTypeTask";
		private boolean isCompleteSuccess = true;

		public SyncQuestionTypeTask(Context context) {
			myContext = context;
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			boolean isSuccess = false;
			Log.v(LOG, "--------doInBackground-------START---");
			DatabaseHelper databaseHelper = DatabaseHelper
					.getInstance(myContext);

			try {
				URL url = new URL(
						"https://dl.dropbox.com/s/hk9z77in3guoexj/question_types.xml");
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(url.openStream()));
				String line = null;
				StringBuilder contents = new StringBuilder();
				while ((line = reader.readLine()) != null && !isCancelled()) {
					contents.append(line);
				}
				Log.v(LOG, "contents " + contents.toString());
				long max_db_ques_id = databaseHelper.getQuestionTypeCount();
				HandleQuestionTypeXML questionTypeParser = new HandleQuestionTypeXML(
						contents.toString());
				List<QuestionType> questionTypes = questionTypeParser
						.getQuestionTypeList();
				Log.v(LOG, Long.toString(max_db_ques_id));
				if (max_db_ques_id < (questionTypes.size() - 1)) {
					Log.v(LOG, "max db id is less than new size");
					List<QuestionType> existing_types = databaseHelper
							.getAllQuestionTypes();
					HashMap<String, QuestionType> existing_sets = new HashMap<String, QuestionType>();

					for (int i = 0; i < existing_types.size(); i++) {
						Log.v(LOG, "existing type "
								+ existing_types.get(i).getQuestionType());
						existing_sets.put(existing_types.get(i)
								.getQuestionType(), existing_types.get(i));
					}
					for (int i = 0; i < questionTypes.size(); i++) {
						if (existing_sets.containsKey(questionTypes.get(i)
								.getQuestionType())) {
							Log.v(LOG, "this type already exist "
									+ questionTypes.get(i).getQuestionType());
							if (Float.parseFloat(existing_sets.get(
									questionTypes.get(i).getQuestionType())
									.getVersion()) < Float
									.parseFloat(questionTypes.get(i)
											.getVersion())) {
								Log.v(LOG,
										"this type already exist : but older version");
								isSuccess = new SyncQuestionsTask(myContext,
										questionTypes.get(i)).syncQuestions();
								if (isSuccess) {
									databaseHelper
											.updateQuestionType(questionTypes
													.get(i));
								} else {
									isCompleteSuccess = false;
								}
							}
						} else {
							Log.v(LOG, "inserting the record for "
									+ questionTypes.get(i).getQuestionType());
							isSuccess = new SyncQuestionsTask(myContext,
									questionTypes.get(i)).syncQuestions();
							if (isSuccess) {
								databaseHelper.createQuestionType(questionTypes
										.get(i));
							} else {
								isCompleteSuccess = false;
							}
							Log.v(LOG, "inserting of new type is done");
						}
					}
				}
			} catch (Exception ex) {
				Log.v("EXCEPTION", ex.getMessage());
				isCompleteSuccess = false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.v(LOG, "-----------------calling post execution---- ");
			// set flag done
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(myContext);
			String key = myContext.getResources().getString(
					R.string.first_time_data_load);
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if (isCompleteSuccess) {
				boolean first_time_loaded = preferences.getBoolean(key, false);
				if (first_time_loaded == false) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putBoolean(key, true);
					editor.commit();
					Toast.makeText(getApplicationContext(), "Success...",
							Toast.LENGTH_LONG).show();
				}
				String last_updated_key = getResources().getString(
						R.string.last_update_time);

				SharedPreferences.Editor editor = preferences.edit();
				editor.putLong(last_updated_key, new Date().getTime());
				editor.commit();
			} else {
				boolean first_time_loaded = preferences.getBoolean(key, false);
				if (first_time_loaded == false) {
					Toast.makeText(
							getApplicationContext(),
							"Data not loaded successfully, Check your internet connectivity.. ",
							Toast.LENGTH_LONG).show();
				}

			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.v(LOG, "--- starting pre-execute -- ");
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String key = getResources()
					.getString(R.string.first_time_data_load);
			boolean first_time_loaded = preferences.getBoolean(key, false);
			if (first_time_loaded == false) {
				Log.v(LOG, "Loading data for first time");
				progressDialog.setTitle("Processing...");
				progressDialog
						.setMessage("Please wait... Setting up for first Time. Make sure internet connectivity");
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
			}
		}
	}

	private static final String LOG = "MainActivity";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private AdView adView;

	ProgressDialog progressDialog;

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(LOG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonPlayQuiz) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String key = getResources()
					.getString(R.string.first_time_data_load);
			boolean first_time_loaded = preferences.getBoolean(key, false);
			if (first_time_loaded) {
				Intent intent = new Intent(this, QuizPlayActivity.class);
				this.startActivity(intent);
			} else {
				new SyncQuestionTypeTask(getApplicationContext()).execute(null,
						null, null);
			}
		}
        else if (v.getId() == R.id.Practice) {
            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            String key = getResources()
                    .getString(R.string.first_time_data_load);
            boolean first_time_loaded = preferences.getBoolean(key, false);
            if (first_time_loaded) {
                Intent intent = new Intent(this, PracticeActivity.class);
                this.startActivity(intent);
            } else {
                new SyncQuestionTypeTask(getApplicationContext()).execute(null,
                        null, null);
            }
        }else if (v.getId() == R.id.check_score_button_id) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String key = getResources()
					.getString(R.string.first_time_data_load);
			boolean first_time_loaded = preferences.getBoolean(key, false);
			if (first_time_loaded) {
				Intent intent = new Intent(this, PreviousScores.class);
				this.startActivity(intent);
			} else {
				new SyncQuestionTypeTask(getApplicationContext()).execute(null,
						null, null);
			}
		}else if(v.getId() == R.id.rapid_fire_round_button_id)
        {
            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            String key = getResources()
                    .getString(R.string.first_time_data_load);
            boolean first_time_loaded = preferences.getBoolean(key, false);
            if (first_time_loaded) {
                Intent intent = new Intent(this, RapidFireRoundActivity.class);
                this.startActivity(intent);
            } else {
                new SyncQuestionTypeTask(getApplicationContext()).execute(null,
                        null, null);
            }
        }

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progressDialog = new ProgressDialog(this);
		Log.v(LOG, "--- START ---");

		try {
			View v = findViewById(R.id.buttonPlayQuiz);
			v.setOnClickListener(this);
			v = findViewById(R.id.check_score_button_id);
			v.setOnClickListener(this);
			v = findViewById(R.id.settings_button);
			v.setOnClickListener(this);
            v = findViewById(R.id.Practice);
            v.setOnClickListener(this);
            v = findViewById(R.id.rapid_fire_round_button_id);
            v.setOnClickListener(this);
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String last_updated_key = getResources().getString(
					R.string.last_update_time);
			String update_frequency_key = getResources().getString(
					R.string.question_update_frequency);
			long last_updated_time = preferences.getLong(last_updated_key, 0);
			long update_frequency = preferences
					.getLong(update_frequency_key, 1);
			long time_now = new Date().getTime();

			if ((time_now - last_updated_time) >= (update_frequency * 86400)) {
				new SyncQuestionTypeTask(getApplicationContext()).execute(null,
						null, null);
			} else {
				Log.v(LOG,
						"not updating questions as frequency of update is not reached");
			}
			
			adView = (AdView) this.findViewById(R.id.adView);
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
		super.onResume();
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}
		checkPlayServices();
		if (adView != null) {
			adView.resume();
		}
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		//EasyTracker.getInstance(this).activityStart(this);
	}
}
