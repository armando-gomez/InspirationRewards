package com.armandogomez.inspirationrewards;

public class Profile {
	private String firstName;
	private String lastName;
	private String department;
	private String story;
	private String position;
	private int points;

	Profile(String firstName, String lastName, String department, String story, String position) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.department = department;
		this.story = story;
		this.position = position;
		this.points = 1000;
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

	public int getPoints() {
		return points;
	}

	public void updatePoints(int points) {
		this.points = this.points + points;
	}
}
