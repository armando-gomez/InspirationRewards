package com.armandogomez.inspirationrewards;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RewardHistoryViewHolder extends RecyclerView.ViewHolder {
	TextView rewardDate;
	TextView rewardName;
	TextView rewardAmount;
	TextView rewardMessage;

	RewardHistoryViewHolder(View view) {
		super(view);
		rewardDate = view.findViewById(R.id.rewardDate);
		rewardName = view.findViewById(R.id.rewardName);
		rewardAmount = view.findViewById(R.id.rewardAmount);
		rewardMessage = view.findViewById(R.id.rewardMessage);
	}
}
