package com.armandogomez.inspirationrewards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
	private Profile profile;

	private Menu menu;
	private List<RewardHistory> rewardHistoryList = new ArrayList<>();
	private RecyclerView recyclerView;
	private RewardHistoryAdapter rewardHistoryAdapter;
	private String filename;

	private TextView profileFullName;
	private TextView profileUsername;
	private TextView profileLocation;
	private TextView profilePoints;
	private TextView profileDepartment;
	private TextView profilePosition;
	private TextView profilePointsToAward;
	private TextView profileStory;
	private TextView rewardHistoryCount;
	private ImageView profilePic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		recyclerView = findViewById(R.id.profileRecycler);
		rewardHistoryAdapter = new RewardHistoryAdapter(rewardHistoryList, this);

		profileFullName = (TextView) findViewById(R.id.profileFullName);
		profileUsername = (TextView) findViewById(R.id.profileUsername);
		profileLocation = (TextView) findViewById(R.id.profileLocation);
		profileLocation = (TextView) findViewById(R.id.profileLocation);
		profilePoints = (TextView) findViewById(R.id.profilePoints);
		profileDepartment = (TextView) findViewById(R.id.profileDepartment);
		profilePosition = (TextView) findViewById(R.id.profilePosition);
		profilePointsToAward = (TextView) findViewById(R.id.profilePointsToAward);
		profileStory = (TextView) findViewById(R.id.profileStory);
		rewardHistoryCount = (TextView) findViewById(R.id.profileRewardHistoryCount);
		profilePic = (ImageView) findViewById(R.id.profilePic);

		filename = getIntent().getStringExtra("JSON_FILE_NAME");
		loadJSON(filename);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Your Profile");
		toolbar.setNavigationIcon(R.drawable.icon);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 200 && resultCode == Activity.RESULT_OK) {
			finish();
			startActivity(getIntent());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.menu_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.editProfile:
				editProfile();
				return true;
			case R.id.leaderboardMenu:
				openLeaderboard();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
	}

	private void editProfile(){
		Intent intent = new Intent(this, EditProfileActivity.class);
		startActivityForResult(intent, 200);
	}

	private void openLeaderboard(){
		Intent intent = new Intent(this, LeaderboardActivity.class);
		intent.putExtra("USERNAME", profile.getUsername());
		intent.putExtra("PASSWORD", profile.getPassword());
		startActivity(intent);
	}

	private void loadProfile() {
		profileFullName.setText(profile.getFullName());
		profileUsername.setText("("+profile.getUsername()+")");
		profileLocation.setText(profile.getLocation());
		profilePoints.setText(String.valueOf(profile.getPointsReceived()));
		profileDepartment.setText(profile.getDepartment());
		profilePosition.setText(profile.getPosition());
		profilePointsToAward.setText(String.valueOf(profile.getPointsToGive()));
		profileStory.setText(profile.getStory());
		rewardHistoryCount.setText("Reward History (" + rewardHistoryList.size() + ")");

		textToImage(profile.getImageBytes());
	}

	private void textToImage(String imageString) {
		if(imageString == null) {
			profilePic.setImageDrawable(getResources().getDrawable(R.drawable.default_photo));
		}
		byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		profilePic.setImageBitmap(bitmap);
	}

	private void parseJSON(JSONObject jsonObject) {
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
				rewards = jsonObject.getJSONArray("rewardRecords");
			}
			profile = new Profile(firstName, lastName, username, password, department, position, story, points, admin, imageBytes, location, rewards);

			loadProfile();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadJSON(String filename) {
		try {
			InputStream is = getApplicationContext().openFileInput(filename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			JSONObject jsonObject = new JSONObject(sb.toString());
			parseJSON(jsonObject);
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
