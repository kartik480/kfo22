package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AgentListAdapter extends RecyclerView.Adapter<AgentListAdapter.ViewHolder> {
    private List<AgentItem> agentList;

    public AgentListAdapter(List<AgentItem> agentList) {
        this.agentList = agentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agent_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AgentItem agent = agentList.get(position);

        holder.tvFullName.setText(agent.getFullName());
        holder.tvCompanyName.setText(agent.getCompanyName());
        holder.tvPhoneNumber.setText(agent.getPhoneNumber());
        holder.tvAlternativePhone.setText(agent.getAlternativePhoneNumber());
        holder.tvEmail.setText(agent.getEmailId());
        holder.tvPartnerType.setText(agent.getPartnerType());
        holder.tvState.setText(agent.getState());
        holder.tvLocation.setText(agent.getLocation());
        holder.tvAddress.setText(agent.getAddress());
        holder.tvVisitingCard.setText(agent.getVisitingCard());
        holder.tvCreatedUser.setText(agent.getCreatedUser());
        holder.tvCreatedBy.setText(agent.getCreatedBy());
        
        // Set status (you can customize this based on your data)
        holder.tvStatus.setText("Active");
    }

    @Override
    public int getItemCount() {
        return agentList.size();
    }

    public void updateData(List<AgentItem> newList) {
        this.agentList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvCompanyName, tvPhoneNumber, tvAlternativePhone, tvEmail, 
                tvPartnerType, tvState, tvLocation, tvAddress, tvVisitingCard, 
                tvCreatedUser, tvCreatedBy, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvAlternativePhone = itemView.findViewById(R.id.tvAlternativePhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPartnerType = itemView.findViewById(R.id.tvPartnerType);
            tvState = itemView.findViewById(R.id.tvState);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvVisitingCard = itemView.findViewById(R.id.tvVisitingCard);
            tvCreatedUser = itemView.findViewById(R.id.tvCreatedUser);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
} 
