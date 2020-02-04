package com.armandogomez.inspirationrewards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
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
		getSupportActionBar().setTitle("Create Profile");
		toolbar.setNavigationIcon(R.drawable.arrow_with_logo);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		leaderboardRecycler = findViewById(R.id.leaderboardRecycler);
		leaderboardRecordAdapter = new LeaderboardRecordAdapter(leaderboardList, this);

		leaderboardRecycler.setAdapter(leaderboardRecordAdapter);
		leaderboardRecycler.setLayoutManager(new LinearLayoutManager(this));


	}

	@Override
	public void onClick(View v) {
		int pos = leaderboardRecycler.getChildLayoutPosition(v);
		Intent intent = new Intent(LeaderboardActivity.this, AwardProfileActivity.class);
		intent.putExtra("INDEX", pos);
		startActivity(intent);
	}
}
