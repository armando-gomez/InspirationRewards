package com.armandogomez.inspirationrewards;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardRecordViewHolder extends RecyclerView.ViewHolder {
	ImageView recordPic;
	TextView recordFullName;
	TextView recordPoints;
	TextView recordDepartmentPosition;

	LeaderboardRecordViewHolder(View view) {
		super(view);
		recordPic = view.findViewById(R.id.recordPic);
		recordFullName = view.findViewById(R.id.recordFullName);
		recordPoints = view.findViewById(R.id.recordPoints);
		recordDepartmentPosition = view.findViewById(R.id.recordDepartmentPosition);
	}
}
