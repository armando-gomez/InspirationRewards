package com.armandogomez.inspirationrewards;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	TextView createAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setLogo(R.drawable.icon);
		getSupportActionBar().setDisplayUseLogoEnabled(true);

		createAccount = this.findViewById(R.id.loginCreateAccount);

		createAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openCreateProfileActivity();
			}
		});

	}

	public void openCreateProfileActivity() {
		Intent intent = new Intent(this, CreateProfileActivity.class);
		startActivity(intent);
	}
}
