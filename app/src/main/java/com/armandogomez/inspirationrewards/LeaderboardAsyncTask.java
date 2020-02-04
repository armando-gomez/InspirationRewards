package com.armandogomez.inspirationrewards;

import android.os.AsyncTask;

import org.json.JSONObject;

public class LeaderboardAsyncTask extends AsyncTask<String, void, String> {
	private static final String TAG = "LeaderboardAsyncTask";
	private static final String API_URL = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
	private static final String STUDENT_ID = "1927073";

	private LeaderboardActivity leaderboardActivity;
	private int responseCode;

	LeaderboardAsyncTask(LeaderboardActivity leaderboardActivity) {
		this.leaderboardActivity = leaderboardActivity;
	}

	@Override
	protected void onPostExecute(String s) {

	}

	@Override
	protected String doInBackground(String... strings) {
		JSONObject dataJSON = new JSONObject();
		try {
			dataJSON.put("studentId", STUDENT_ID);
			
		}
	}
}
