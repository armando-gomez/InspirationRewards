package com.armandogomez.inspirationrewards;

public class RewardHistory {
	private String date;
	private String name;
	private int value;
	private String notes;

	RewardHistory(String date, String name, int value, String notes) {
		this.date = date;
		this.name = name;
		this.value = value;
		this.notes = notes;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
