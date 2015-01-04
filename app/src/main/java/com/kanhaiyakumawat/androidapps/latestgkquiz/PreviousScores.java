package com.kanhaiyakumawat.androidapps.latestgkquiz;

import java.text.SimpleDateFormat;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PreviousScores extends ActionBarActivity implements
		OnClickListener {

	private static final int exit_score_button_id = 1000000;

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == exit_score_button_id) {
			super.finish();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_previous_scores);
		List<Result> results = new PreviousResults(getApplicationContext())
				.getAllPreviousResults();
		int max_results = getResources().getInteger(R.integer.maximum_results);
		TableLayout tableLayout = (TableLayout) findViewById(R.id.container);
		tableLayout.setShrinkAllColumns(true);
		tableLayout.setStretchAllColumns(true);
		TableRow row = new TableRow(this);
		// row.setPadding(2, 2, 2, 2);
		TextView date = new TextView(this);
		TextView totalQuestions = new TextView(this);
		TextView fullCorrect = new TextView(this);
		TextView partiallyCorrect = new TextView(this);
		TextView wrong = new TextView(this);

		date.setText("Play Time");
		date.setTextSize(12);
		date.setTextColor(getResources().getColor(R.color.black));

		totalQuestions.setText("Total Questions");
		totalQuestions.setTextSize(12);
		totalQuestions.setTextColor(getResources().getColor(R.color.black));

		fullCorrect.setText("Fully Correct");
		fullCorrect.setTextSize(12);
		fullCorrect.setTextColor(getResources().getColor(R.color.black));

		wrong.setText("Fully Wrong");
		wrong.setTextSize(12);
		wrong.setTextColor(getResources().getColor(R.color.black));

		partiallyCorrect.setText("Partially Correct");
		partiallyCorrect.setTextSize(12);
		partiallyCorrect.setTextColor(getResources().getColor(R.color.black));

		row.addView(date);
		row.addView(totalQuestions);
		row.addView(fullCorrect);
		row.addView(wrong);
		row.addView(partiallyCorrect);

		tableLayout.addView(row);
		for (int i = 0; i < results.size() && i < max_results; i++) {
			row = new TableRow(this);
			// row.setPadding(2, 2, 2, 2);
			date = new TextView(this);
			totalQuestions = new TextView(this);
			fullCorrect = new TextView(this);
			partiallyCorrect = new TextView(this);
			wrong = new TextView(this);

			date.setText(new SimpleDateFormat(getResources().getString(
					R.string.simple_date_format)).format(results.get(i)
					.getDate()));
			date.setTextSize(12);
			date.setTextColor(getResources().getColor(R.color.black));

			totalQuestions.setText(Long.toString(results.get(i)
					.getTotal_questions()));
			totalQuestions.setTextSize(12);
			totalQuestions.setTextColor(getResources().getColor(R.color.black));

			fullCorrect.setText(Long
					.toString(results.get(i).getFully_correct()));
			fullCorrect.setTextSize(12);
			fullCorrect.setTextColor(getResources().getColor(R.color.black));

			wrong.setText(Long.toString(results.get(i).getFully_wrong()));
			wrong.setTextSize(12);
			wrong.setTextColor(getResources().getColor(R.color.black));

			partiallyCorrect.setText(Long.toString(results.get(i)
					.getPartially_correct()));
			partiallyCorrect.setTextSize(12);
			partiallyCorrect.setTextColor(getResources()
					.getColor(R.color.black));

			row.addView(date);
			row.addView(totalQuestions);
			row.addView(fullCorrect);
			row.addView(wrong);
			row.addView(partiallyCorrect);

			tableLayout.addView(row);
		}
		Button exit = new Button(getApplicationContext());
		exit.setText(R.string.exit_quiz);
		exit.setId(exit_score_button_id);
		exit.setOnClickListener(this);
		exit.setBackgroundColor(getResources().getColor(R.color.Chocolate));
		// RelativeLayout.LayoutParams lpButton = new
		// LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		tableLayout.addView(exit);
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.setAdListener(new MyAdListener());
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build();
		
		adView.loadAd(adRequest);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.previous_scores, menu);
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
}
