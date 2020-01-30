package com.armandogomez.inspirationrewards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RewardHistoryAdapter extends RecyclerView.Adapter<RewardHistoryViewHolder> {
	private List<RewardHistory> rewardHistoryList;
	private ProfileActivity profileActivity;

	RewardHistoryAdapter(List<RewardHistory> rewardHistoryList, ProfileActivity profileActivity) {
		this.rewardHistoryList = rewardHistoryList;
		this.profileActivity = profileActivity;
	}

	@NonNull
	@Override
	public RewardHistoryViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.reward_history_row, parent, false);
		itemView.setOnClickListener(profileActivity);
		return new RewardHistoryViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull RewardHistoryViewHolder holder, int pos) {
		RewardHistory rewardHistory = rewardHistoryList.get(pos);

		holder.rewardDate.setText(rewardHistory.getDate());
		holder.rewardName.setText(rewardHistory.getName());
		holder.rewardAmount.setText(rewardHistory.getAmount());
		holder.rewardMessage.setText(rewardHistory.getMessage());
	}

	@Override
	public int getItemCount() {return rewardHistoryList.size();}
}
