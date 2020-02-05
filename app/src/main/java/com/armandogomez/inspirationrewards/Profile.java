package com.armandogomez.inspirationrewards;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Profile {
	private String firstName;
	private String lastName;
	private String department;
	private String story;
	private String position;
	private boolean admin;
	private int pointsToGive;
	private int pointsReceived = 0;
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
		this.pointsToGive = points;
		this.admin = admin;
		this.username = username;
		this.password = password;
		this.imageBytes = imageBytes;
		this.location = location;
		this.rewards = rewards;

		calculateTotalPointsReceived();
	}

	private void calculateTotalPointsReceived() {
		if(this.rewards == null) {
			pointsReceived = 0;
		} else {
			for(int i=0; i < rewards.length(); i++) {
				try {
					JSONObject reward = rewards.getJSONObject(i);
					pointsReceived += reward.getInt("value");
				} catch (JSONException e) {
					pointsReceived = 0;
					break;
				}
			}
		}
	}

	public String getFullName() {
		return getLastName() + ", " + getFirstName();
	}

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

	public int getPointsToGive() {
		return pointsToGive;
	}

	public void updatePoints(int points) {
		this.pointsToGive = this.pointsToGive + points;
	}

	public String getUsername() {
		return username;
	}

	public String getLocation() {
		return location;
	}

	public String getImageBytes() {
		return imageBytes;
	}

	public boolean isAdmin() {
		return admin;
	}

	public int getPointsReceived() {
		return pointsReceived;
	}

	public String getPassword() {
		return password;
	}

	public JSONArray getRewards() {
		return rewards;
	}
}
