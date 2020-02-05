package com.armandogomez.inspirationrewards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

	private RecyclerView leaderboardRecycler;
	private List<Profile> leaderboardList = new ArrayList<>();
	private LeaderboardRecordAdapter leaderboardRecordAdapter;
	private String username;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);

		username = getIntent().getStringExtra("USERNAME");
		password = getIntent().getStringExtra("PASSWORD");

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Inspiration Leaderboard");
		toolbar.setNavigationIcon(R.drawable.arrow_with_logo);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});

		leaderboardRecycler = findViewById(R.id.leaderboardRecycler);
		leaderboardRecordAdapter = new LeaderboardRecordAdapter(leaderboardList, this);

		leaderboardRecycler.setAdapter(leaderboardRecordAdapter);
		leaderboardRecycler.setLayoutManager(new LinearLayoutManager(this));

		String[] data = new String[]{username, password};
		new LeaderboardAsyncTask(LeaderboardActivity.this).execute(data);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 300 && resultCode == Activity.RESULT_OK) {
			String[] params = new String[]{username, password};
			new LeaderboardAsyncTask(LeaderboardActivity.this).execute(params);
		}
	}

	@Override
	public void onClick(View v) {
		int pos = leaderboardRecycler.getChildLayoutPosition(v);
		Intent intent = new Intent(LeaderboardActivity.this, AwardProfileActivity.class);
		intent.putExtra("INDEX", pos);
		intent.putExtra("USERNAME", username);
		intent.putExtra("PASSWORD", password);
		intent.putExtra("FULLNAME", getIntent().getStringExtra("FULLNAME"));
		startActivityForResult(intent, 300);
	}

	public void updateLeaderboardList(List<Profile> leaderboardList){
		if(!(this.leaderboardList.isEmpty())) {
			this.leaderboardList.clear();
		}
		if(leaderboardList != null) {
			for(Profile p: leaderboardList) {
				this.leaderboardList.add(p);
			}
		}
		updateAdapter();
	}

	private void updateAdapter(){
		leaderboardList.sort(new Comparator<Profile>() {
			@Override
			public int compare(Profile a, Profile b) {
				return b.getPointsReceived() - a.getPointsReceived();
			}
		});
		LeaderboardAsyncTask.sortLeaderboard();
		leaderboardRecordAdapter.notifyDataSetChanged();
	}

	public void leaderboardAsyncError(String result){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
			result = jsonObject.getString("errordetails");
			jsonObject = new JSONObject(result);
			String message = jsonObject.getString("message");
			Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
			View toastView = toast.getView();
			toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
			TextView tv = toast.getView().findViewById(android.R.id.message);
			tv.setPadding(50, 25, 50, 25);
			tv.setTextColor(Color.WHITE);
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
