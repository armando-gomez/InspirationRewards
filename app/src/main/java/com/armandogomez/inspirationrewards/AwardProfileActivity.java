package com.armandogomez.inspirationrewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AwardProfileActivity extends AppCompatActivity {

	private String username;
	private String password;
	private Profile targetProfile;
	private Menu menu;

	private TextView awardFullName;
	private TextView awardPoints;
	private TextView awardDepartment;
	private TextView awardPosition;
	private TextView awardStory;
	private ImageView awardProfilePic;

	private EditText awardPointEdit;
	private EditText awardComment;
	private TextView awardCommentCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_award_profile);

		username = getIntent().getStringExtra("USERNAME");
		password = getIntent().getStringExtra("PASSWORD");
		int index = getIntent().getIntExtra("INDEX", -1);
		if(index != -1) {
			targetProfile = LeaderboardAsyncTask.getProfile(index);
		}

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(targetProfile.getFirstName() + " " + targetProfile.getLastName());
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setLogo(R.drawable.icon);
		getSupportActionBar().setDisplayUseLogoEnabled(true);

		awardFullName = (TextView) findViewById(R.id.awardFullName);
		awardPoints = (TextView) findViewById(R.id.awardPoints);
		awardDepartment = (TextView) findViewById(R.id.awardDepartment);
		awardPosition = (TextView) findViewById(R.id.awardPosition);
		awardStory = (TextView) findViewById(R.id.awardStory);
		awardProfilePic = (ImageView) findViewById(R.id.awardProfilePic);
		awardComment = (EditText) findViewById(R.id.awardEditComment);
		awardPointEdit = (EditText) findViewById(R.id.awardPointEdit);
		awardCommentCount = (TextView) findViewById(R.id.awardCommentCount);

		awardComment.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				awardCommentCount.setText("(" + s.toString().length() + " of 80)");
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		loadProfile();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.menu_award, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.awardSave:
				awardSaveDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void awardSaveDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Rewards Points?");
		builder.setMessage("Add rewards for " + targetProfile.getFirstName() + " " + targetProfile.getLastName() + "?");
		builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				awardPoints();
			}
		});

		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
	}

	private void awardPoints() {
		String targetUser = targetProfile.getUsername();
		String commentFullName = getIntent().getStringExtra("FULLNAME");
		String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
		String notes = awardComment.getText().toString();
		String value = awardPointEdit.getText().toString();
		String srcUsername = username;
		String srcPassword= password;
		String[] postData = new String[]{targetUser, commentFullName, date, notes, value, srcUsername, srcPassword};
		new RewardAsyncTask(this).execute(postData);
	}

	private void loadProfile() {
		awardFullName.setText(targetProfile.getFullName());
		awardPoints.setText(String.valueOf(targetProfile.getPointsReceived()));
		awardDepartment.setText(targetProfile.getDepartment());
		awardPosition.setText(targetProfile.getPosition());
		awardStory.setText(targetProfile.getStory());

		textToImage(targetProfile.getImageBytes());
	}

	private void textToImage(String imageString) {
		if(imageString == null) {
			awardProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.default_photo));
		}
		byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		awardProfilePic.setImageBitmap(bitmap);
	}

	public void awardProfileAsyncSuccess(String s) {
		Toast toast = Toast.makeText(getApplicationContext(), "Add Reward succeeded", Toast.LENGTH_LONG);
		View toastView = toast.getView();
		toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
		TextView tv = toast.getView().findViewById(android.R.id.message);
		tv.setPadding(50, 25, 50, 25);
		tv.setTextColor(Color.WHITE);
		toast.show();
		setResult(RESULT_OK);
		finish();
	}

	public void awardProfileAsyncError(String result) {
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
