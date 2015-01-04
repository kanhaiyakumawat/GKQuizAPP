package com.kanhaiyakumawat.androidapps.latestgkquiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

public class PreviousResults {
	class Results {
		private List<Result> result_list;// = new ArrayList<Result>();

		public List<Result> getResultList() {
			return result_list;
		}

		public void setResultList(List<Result> result_list) {
			this.result_list = result_list;
		}
	}

	private static final String LOG = "PreviousResults";
	Context context;

	long max_results;

	PreviousResults(Context context) {
		this.context = context;
	}

	public void addResult(Result result) {
		max_results = context.getResources().getInteger(
				R.integer.maximum_results);
		List<Result> results;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String key = context.getResources()
				.getString(R.string.previous_results);
		String previous_results_json = preferences.getString(key, null);
		Log.v(LOG, "previous results : " + previous_results_json);
		Gson gson = new Gson();
		if (previous_results_json == null) {
			results = new ArrayList<Result>();
			;
			Log.v(LOG, "first time putting score");
			results.add(result);
		} else {
			Log.v(LOG, "Appending result to existing score");
			Results prev_results = gson.fromJson(previous_results_json,
					Results.class);
			results = prev_results.getResultList();
			results.add(result);
			Collections.sort(results, new Comparator<Result>() {
				@Override
				public int compare(Result r1, Result r2) {
					return r2.getDate().compareTo(r1.getDate());
				}
			});
			if (results.size() > context.getResources().getInteger(
					R.integer.maximum_results)) {
				results.remove(results.size() - 1);
			}
		}
		Results pre_results = new Results();
		pre_results.setResultList(results);
		previous_results_json = gson.toJson(pre_results);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, previous_results_json);
		editor.commit();
		Log.v(LOG, "previous results : " + previous_results_json);
	}

	public List<Result> getAllPreviousResults() {
		Log.v(LOG, "getting all the previous scores");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String key = context.getResources()
				.getString(R.string.previous_results);
		String previous_results_json = preferences.getString(key, null);
		if (previous_results_json == null) {
			return new ArrayList<Result>();
		} else {
			Gson gson = new Gson();
			Results prev_results = gson.fromJson(previous_results_json,
					Results.class);
			return prev_results.getResultList();
		}
	}
}

class Result {
	private long date = 0;

	private long total_questions = 0;
	private long fully_correct = 0;
	private long fully_wrong = 0;
	private long partially_correct = 0;

	Result() {

	}

	public Date getDate() {
		return new Date(date);
	}

	public long getFully_correct() {
		return fully_correct;
	}

	public long getFully_wrong() {
		return fully_wrong;
	}

	public long getPartially_correct() {
		return partially_correct;
	}

	public long getTotal_questions() {
		return total_questions;
	}

	public void setDate(Date date) {
		this.date = date.getTime();
	}

	public void setFully_correct(long fully_correct) {
		this.fully_correct = fully_correct;
	}

	public void setFully_wrong(long fully_wrong) {
		this.fully_wrong = fully_wrong;
	}

	public void setPartially_correct(long partially_correct) {
		this.partially_correct = partially_correct;
	}

	public void setTotal_questions(long total_questions) {
		this.total_questions = total_questions;
	}
}
