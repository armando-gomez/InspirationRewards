package com.armandogomez.inspirationrewards;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class CreateProfileActivity extends AppCompatActivity {
	private int REQUEST_IMAGE_GALLERY = 1;
	private int REQUEST_IMAGE_CAPTURE = 2;
	private File currentImageFile;

	Menu menu;
	ImageView profilePic;

	String location;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_profile);

		location = getIntent().getStringExtra("LOCATION");

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

		profilePic = findViewById(R.id.createProfilePicture);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.menu_create_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.createSave:
				createProfileDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void createProfileDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Save Changes?");
		builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				createProfile();
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

	public void createProfile() {
		Bitmap bitmap = ((BitmapDrawable) profilePic.getBackground()).getBitmap();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		String imageBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);

		String firstName = ((EditText) findViewById(R.id.createFirstName)).getText().toString();
		String lastName = ((EditText) findViewById(R.id.createLastName)).getText().toString();
		String department = ((EditText) findViewById(R.id.createDepartment)).getText().toString();
		String position = ((EditText) findViewById(R.id.createPosition)).getText().toString();
		String story = ((EditText) findViewById(R.id.createStory)).getText().toString();
		String username = ((EditText) findViewById(R.id.createUsername)).getText().toString();
		String password = ((EditText) findViewById(R.id.createPassword)).getText().toString();
		boolean admin = ((CheckBox) findViewById(R.id.createAdminCheck)).isChecked();

		String[] postData = new String[]{username, password, firstName, lastName, department, position, story, Boolean.toString(admin), location, imageBytes};
		new PostProfileAsyncTask(this).execute(postData);
	}

	public void onProfilePictureClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Profile Picture");
		builder.setMessage("Take picture from:");
		builder.setIcon(R.drawable.logo);
		builder.setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentImageFile = new File(getExternalCacheDir(), "appimage_" + System.currentTimeMillis() + ".jpg");
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		});

		builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1 && resultCode == RESULT_OK) {
			try {
				processGallery(data);
			} catch (Exception e) {
				Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		} else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			try {
				processCamera();
			} catch (Exception e) {
				Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	private void processCamera() {
		Uri selectedImage = Uri.fromFile(currentImageFile);
		profilePic.setImageURI(selectedImage);
	}

	private void processGallery(Intent data) {
		Uri galleryImageUri = data.getData();
		if (galleryImageUri == null)
			return;

		InputStream imageStream = null;
		try {
			imageStream = getContentResolver().openInputStream(galleryImageUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
		profilePic.setBackground(new BitmapDrawable(getResources(), selectedImage));
		profilePic.setImageDrawable(null);
	}

	public void showResults(String s) {
		if(s.equals("SUCCESS")) {
			Toast toast = Toast.makeText(getApplicationContext(), "User Create Successful", Toast.LENGTH_LONG);
			View toastView = toast.getView();
			toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
			TextView tv = toast.getView().findViewById(android.R.id.message);
			tv.setPadding(50, 25, 50, 25);
			tv.setTextColor(Color.WHITE);
			toast.show();
			onBackPressed();
		} else {
			JSONObject jsonObject;
			String message = "";
			try{
				jsonObject = new JSONObject(s);
				message = jsonObject.getString("message");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
			View toastView = toast.getView();
			toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
			TextView tv = toast.getView().findViewById(android.R.id.message);
			tv.setPadding(50, 25, 50, 25);
			tv.setTextColor(Color.WHITE);
			toast.show();
		}
	}
}

