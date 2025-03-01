package com.armandogomez.inspirationrewards;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
	private static int MY_LOCATION_REQUEST_CODE_ID = 329;

	private LocationManager locationManager;
	private Criteria criteria;
	private SharedPreferences sharedPreferences;

	private Address address;
	private boolean networkStatusOnline = false;
	View progressOverlay;

	private EditText loginUserInput;
	private EditText loginPasswordInput;
	private CheckBox loginCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loginUserInput = findViewById(R.id.loginUserInput);
		loginPasswordInput = findViewById(R.id.loginPasswordInput);
		loginCheckBox = findViewById(R.id.loginRememberLogin);

		sharedPreferences = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

		if(sharedPreferences.contains("USERNAME") && sharedPreferences.contains("PASSWORD") && sharedPreferences.contains("REMEMBER")) {
			loginUserInput.setText(sharedPreferences.getString("USERNAME", ""));
			loginPasswordInput.setText(sharedPreferences.getString("PASSWORD", ""));
			loginCheckBox.setChecked(true);
		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);

		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setLogo(R.drawable.icon);
		getSupportActionBar().setDisplayUseLogoEnabled(true);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
			String[] blah = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
			ActivityCompat.requestPermissions(this, blah, MY_LOCATION_REQUEST_CODE_ID);
		} else {
			setLocation();
		}

		progressOverlay = findViewById(R.id.progressOverlay);
	}

	private void doNetworkCheck() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			Toast.makeText(this, "Cannot Access ConnectivityManager", Toast.LENGTH_SHORT).show();
			return;
		}

		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			this.networkStatusOnline = true;
		} else {
			this.networkStatusOnline = false;
		}
	}

	public void openCreateProfileActivity(View v) {
		Intent intent = new Intent(this, CreateProfileActivity.class);
		String location = address.getLocality() + ", " + address.getAdminArea();
		intent.putExtra("LOCATION", location);
		startActivity(intent);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
			if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
					grantResults[0] == PERMISSION_GRANTED) {
				setLocation();
				return;
			}
		}
	}

	@SuppressLint("MissingPermission")
	private void setLocation() {
		doNetworkCheck();
		if(!networkStatusOnline) {
			showNoNetworkDialog();
			return;
		}
		Location bestLocation = locationManager.getLastKnownLocation("gps");
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			this.address = geocoder.getFromLocation(bestLocation.getLatitude(), bestLocation.getLongitude(), 1).get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showNoNetworkDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("No Network Connection");
		builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void onLoginClick(View v) {
		progressOverlay.setVisibility(View.VISIBLE);
		String username = ((EditText) findViewById(R.id.loginUserInput)).getText().toString();
		String password = ((EditText) findViewById(R.id.loginPasswordInput)).getText().toString();
		String[] postData = {username, password};
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		if(loginCheckBox.isChecked()) {
			SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
			prefsEditor.putString("USERNAME", username);
			prefsEditor.putString("PASSWORD", password);
			prefsEditor.putBoolean("REMEMBER", true);
			prefsEditor.apply();
		}
		new LoginAsyncTask(this).execute(postData);
	}

	public void loginAsyncResultSuccess(String result) {
		progressOverlay.setVisibility(View.INVISIBLE);
		try {
			String filename = saveProfileJSON(result);
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra("JSON_FILE_NAME", filename);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loginAsyncResultError(String result) {
		progressOverlay.setVisibility(View.INVISIBLE);
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

	private String saveProfileJSON(String s) throws IOException, JSONException {
		FileOutputStream fos = getApplicationContext()
				.openFileOutput("profile.json", Context.MODE_PRIVATE);

		JSONObject jsonObject = new JSONObject(s);
		String jsonText = jsonObject.toString();
		fos.write(jsonText.getBytes());
		fos.close();
		return "profile.json";
	}
}
