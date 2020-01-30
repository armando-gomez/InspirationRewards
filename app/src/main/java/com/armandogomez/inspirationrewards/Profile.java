package com.armandogomez.inspirationrewards;

import org.json.JSONArray;

import java.io.Serializable;

public class Profile implements Serializable {
	private String firstName;
	private String lastName;
	private String department;
	private String story;
	private String position;
	private boolean admin;
	private int points;
	private String username;
	private String password;
	private String imageBytes;
	private String location;
	private JSONArray rewards;

	Profile(String firstName, String lastName, String username, String password, String department, String position, String story, int points, boolean admin, String imageBytes, String location, JSONArray rewards) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.department = department;
		this.story = story;
		this.position = position;
		this.points = points;
		this.admin = admin;
		this.username = username;
		this.password = password;
		this.imageBytes = imageBytes;
		this.location = location;
		this.rewards = rewards;
	}

	Profile(){}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public int getPoints() {
		return points;
	}

	public void updatePoints(int points) {
		this.points = this.points + points;
	}
}
