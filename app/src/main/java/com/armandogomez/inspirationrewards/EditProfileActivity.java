package com.armandogomez.inspirationrewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EditProfileActivity extends AppCompatActivity {
	private Profile profile;
	private Menu menu;
	private EditText editFirstName;
	private EditText editLastName;
	private TextView editUsername;
	private EditText editPassword;
	private EditText editDepartment;
	private EditText editPosition;
	private EditText editStory;
	private ImageView editProfilePic;
	private CheckBox editAdminCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Edit Profile");
		toolbar.setNavigationIcon(R.drawable.arrow_with_logo);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		editFirstName = (EditText) findViewById(R.id.editFirstName);
		editLastName = (EditText) findViewById(R.id.editLastName);
		editUsername = (TextView) findViewById(R.id.editUsername);
		editPassword = (EditText) findViewById(R.id.editPassword);
		editDepartment = (EditText) findViewById(R.id.editDepartment);
		editPosition = (EditText) findViewById(R.id.editPosition);
		editStory = (EditText) findViewById(R.id.editStory);
		editProfilePic = (ImageView) findViewById(R.id.editProfilePicture);
		editAdminCheck = (CheckBox) findViewById(R.id.editAdminCheck);
		loadJSON("profile.json");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.editSave:
				saveProfileDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void saveProfileDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Save Changes?");
		builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveProfile();
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

	public void saveProfile() {
		Bitmap bitmap = ((BitmapDrawable) editProfilePic.getBackground()).getBitmap();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		String imageBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);

		String firstName = ((EditText) findViewById(R.id.editFirstName)).getText().toString();
		String lastName = ((EditText) findViewById(R.id.editLastName)).getText().toString();
		String department = ((EditText) findViewById(R.id.editDepartment)).getText().toString();
		String position = ((EditText) findViewById(R.id.editPosition)).getText().toString();
		String story = ((EditText) findViewById(R.id.editStory)).getText().toString();
		String username = ((TextView) findViewById(R.id.editUsername)).getText().toString();
		String password = ((EditText) findViewById(R.id.editPassword)).getText().toString();
		boolean admin = ((CheckBox) findViewById(R.id.editAdminCheck)).isChecked();
		String location = profile.getLocation();

		JSONObject profileJSON = new JSONObject();
		try {
			profileJSON.put("username", username);
			profileJSON.put("password", password);
			profileJSON.put("firstName", firstName);
			profileJSON.put("lastName", lastName);
			profileJSON.put("pointsToAward", profile.getPointsToGive());
			profileJSON.put("department", department);
			profileJSON.put("position", position);
			profileJSON.put("story", story);
			profileJSON.put("admin", admin);
			profileJSON.put("location", location);
			profileJSON.put("imageBytes", profile.getImageBytes());
			profileJSON.put("rewardRecords", profile.getRewards());
		} catch (Exception e) {
			e.printStackTrace();
		}

		new SaveProfileAsyncTask(this).execute(profileJSON.toString());
	}

	private void loadProfile() {
		editFirstName.setText(profile.getFirstName());
		editLastName.setText(profile.getLastName());
		editUsername.setText(profile.getUsername());
		editPassword.setText(profile.getPassword());
		editDepartment.setText(profile.getDepartment());
		editPosition.setText(profile.getPosition());
		editStory.setText(profile.getStory());

		if(profile.isAdmin()) {
			editAdminCheck.setChecked(true);
		}

		textToImage(profile.getImageBytes());
	}

	private void textToImage(String imageString) {
		if(imageString == null) {
			editProfilePic.setBackground(getResources().getDrawable(R.drawable.default_photo));
		}
		byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		editProfilePic.setBackground(new BitmapDrawable(getResources(), bitmap));
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
			JSONArray rewards = jsonObject.getJSONArray("rewards");

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

	public void editProfileAsyncSuccess(String result) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
			FileOutputStream fos = getApplicationContext()
					.openFileOutput("profile.json", Context.MODE_PRIVATE);
			String jsonText = jsonObject.toString();
			fos.write(jsonText.getBytes());
			fos.close();
			setResult(RESULT_OK);
			finish();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void editProfileAsyncError(String result) {
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
