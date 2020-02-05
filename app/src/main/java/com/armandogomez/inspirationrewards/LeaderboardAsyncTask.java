package com.armandogomez.inspirationrewards;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeaderboardAsyncTask extends AsyncTask<String, Void, String> {
	private static final String TAG = "LeaderboardAsyncTask";
	private static final String API_URL = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
	private static final String STUDENT_ID = "1927073";

	private LeaderboardActivity leaderboardActivity;
	private int responseCode;
	private static List<Profile> leaderboardList;

	LeaderboardAsyncTask(LeaderboardActivity leaderboardActivity) {
		this.leaderboardActivity = leaderboardActivity;
	}

	@Override
	protected void onPostExecute(String s) {
		if(responseCode == 200) {
			this.leaderboardList = parseJson(s);
			leaderboardActivity.updateLeaderboardList(leaderboardList);
		} else {
			leaderboardActivity.leaderboardAsyncError(s);
		}
	}

	@Override
	protected String doInBackground(String... strings) {
		JSONObject dataJSON = new JSONObject();
		try {
			dataJSON.put("studentId", STUDENT_ID);
			dataJSON.put("username", strings[0]);
			dataJSON.put("password", strings[1]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		HttpURLConnection connection = null;
		BufferedReader reader = null;

		try {
			Uri baseUri = Uri.parse(API_URL);
			String urlString = baseUri.toString() + "/allprofiles";

			Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
			String urlToUse = buildURL.build().toString();
			URL url = new URL(urlToUse);

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.connect();

			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.write(dataJSON.toString());
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

	private List<Profile> parseJson(String s) {
		List<Profile> leaderboardList = new ArrayList<>();

		try {
			JSONArray jsonArray = new JSONArray(s);

			for(int i=0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				try {
					String firstName = jsonObject.getString("firstName");
					String lastName = jsonObject.getString("lastName");
					String username = jsonObject.getString("username");
					String password = jsonObject.getString("password");
					String department = jsonObject.getString("department");
					String position = jsonObject.getString("position");
					String story = jsonObject.getString("story");
					int points = jsonObject.getInt("pointsToAward");
					boolean admin = jsonObject.getBoolean("admin");
					String imageBytes = jsonObject.getString("imageBytes");
					String location = jsonObject.getString("location");
					JSONArray rewards;
					try {
						rewards = jsonObject.getJSONArray("rewards");
					} catch (Exception e) {
						try {
							rewards = jsonObject.getJSONArray("rewardRecords");
						} catch (Exception f) {
							rewards = null;
						}
					}

					Profile p = new Profile(firstName, lastName, username, password, department, position, story, points, admin, imageBytes, location, rewards);

					leaderboardList.add(p);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return leaderboardList;
		} catch (Exception e) {
			Log.d(TAG, "parseOuterJSON: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public static Profile getProfile(int index) {
		return leaderboardList.get(index);
	}

	public static void sortLeaderboard() {
		leaderboardList.sort(new Comparator<Profile>() {
			@Override
			public int compare(Profile a, Profile b) {
				return b.getPointsReceived() - a.getPointsReceived();
			}
		});
	}
}
