package com.armandogomez.inspirationrewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardRecordAdapter extends RecyclerView.Adapter<LeaderboardRecordViewHolder> {
	private List<Profile> leaderboardRecordList;
	private LeaderboardActivity leaderboardActivity;

	LeaderboardRecordAdapter(List<Profile> leaderboardRecordList, LeaderboardActivity leaderboardActivity) {
		this.leaderboardRecordList = leaderboardRecordList;
		this.leaderboardActivity = leaderboardActivity;
	}

	@NonNull
	@Override
	public LeaderboardRecordViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.reward_record_row, parent, false);
		itemView.setOnClickListener(leaderboardActivity);
		return new LeaderboardRecordViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(LeaderboardRecordViewHolder holder, int pos) {
		Profile profile = leaderboardRecordList.get(pos);
		String imageBytes = profile.getImageBytes();
		setProfilePic(imageBytes, holder);
		holder.recordFullName.setText(profile.getFullName());
		holder.recordPoints.setText(profile.getPointsReceived());
		String departmentPosition = profile.getPosition() + ", " + profile.getDepartment();
		holder.recordDepartmentPosition.setText(departmentPosition);
	}

	private void setProfilePic(String imageString, LeaderboardRecordViewHolder holder) {
		if(imageString == null) {
			holder.recordPic.setImageDrawable(leaderboardActivity.getResources().getDrawable(R.drawable.default_photo));
		}
		byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		holder.recordPic.setImageBitmap(bitmap);
	}

	@Override
	public int getItemCount() {
		return leaderboardRecordList.size();
	}
}
