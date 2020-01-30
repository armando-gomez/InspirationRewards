package com.armandogomez.inspirationrewards;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostProfileAsyncTask extends AsyncTask<String, Void, String> {
	private static final String TAG = "PostProfileAsyncTask";
	private static final String API_URL = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
	private static final String STUDENT_ID = "1927073";
	@SuppressLint("StaticFieldLeak")
	private CreateProfileActivity createProfileActivity;
	private int responseCode;

	PostProfileAsyncTask(CreateProfileActivity createProfileActivity) {
		this.createProfileActivity = createProfileActivity;
	}

	@Override
	protected void onPostExecute(String s) {
		if(responseCode == 200) {
			createProfileActivity.createProfileAsyncSuccess(s);
		} else {
			createProfileActivity.createProfileAsyncError(s);
		}
	}

	@Override
	protected String doInBackground(String... strings) {
		JSONObject profileJSON = new JSONObject();
		try {
			profileJSON.put("studentId", STUDENT_ID);
			profileJSON.put("username", strings[0]);
			profileJSON.put("password", strings[1]);
			profileJSON.put("firstName", strings[2]);
			profileJSON.put("lastName", strings[3]);
			profileJSON.put("pointsToAward", 1000);
			profileJSON.put("department", strings[4]);
			profileJSON.put("position", strings[5]);
			profileJSON.put("story", strings[6]);
			profileJSON.put("admin", String.valueOf(strings[7]));
			profileJSON.put("location", strings[8]);
			profileJSON.put("imageBytes", strings[9]);
			profileJSON.put("rewardRecords", new JSONArray());
			Log.i(TAG, profileJSON.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		HttpURLConnection connection = null;
		BufferedReader reader = null;

		try {
			Uri baseUri = Uri.parse(API_URL);
			String urlString = baseUri.toString() + "/profiles";

			Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
			String urlToUse = buildURL.build().toString();
			URL url = new URL(urlToUse);

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.connect();

			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.write(profileJSON.toString());
			out.close();

			responseCode = connection.getResponseCode();

			StringBuilder result = new StringBuilder();

			if (responseCode == 200) {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				String line;
				while (null != (line = reader.readLine())) {
					result.append(line).append("\n");
				}
				Log.i(TAG, "Result from JSON: " + result.toString());
				return result.toString();

			} else {
				reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

				String line;
				while (null != (line = reader.readLine())) {
					result.append(line).append("\n");
				}
				Log.i(TAG, "Result from JSON: " + result.toString());
				return result.toString();
			}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
				}
			}
		}
		return "";
	}
}
