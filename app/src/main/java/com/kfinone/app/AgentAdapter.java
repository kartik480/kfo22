package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.ViewHolder> {
    private List<AgentData> agents;

    public AgentAdapter(List<AgentData> agents) {
        this.agents = agents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AgentData agent = agents.get(position);
        
        holder.fullNameText.setText(agent.getFullName() != null ? agent.getFullName() : "N/A");
        holder.companyNameText.setText(agent.getCompanyName() != null ? agent.getCompanyName() : "N/A");
        holder.phoneText.setText(agent.getPhoneNumber() != null ? agent.getPhoneNumber() : "N/A");
        holder.emailText.setText(agent.getEmailId() != null ? agent.getEmailId() : "N/A");
        holder.partnerTypeText.setText(agent.getPartnerType() != null ? agent.getPartnerType() : "N/A");
        holder.locationText.setText(agent.getLocation() != null ? agent.getLocation() : "N/A");
        holder.statusText.setText(agent.getStatus() != null ? agent.getStatus() : "N/A");
        holder.createdByText.setText(agent.getCreatorFullName() != null ? agent.getCreatorFullName() : "N/A");
    }

    @Override
    public int getItemCount() {
        return agents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameText;
        TextView companyNameText;
        TextView phoneText;
        TextView emailText;
        TextView partnerTypeText;
        TextView locationText;
        TextView statusText;
        TextView createdByText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameText = itemView.findViewById(R.id.fullNameText);
            companyNameText = itemView.findViewById(R.id.companyNameText);
            phoneText = itemView.findViewById(R.id.phoneText);
            emailText = itemView.findViewById(R.id.emailText);
            partnerTypeText = itemView.findViewById(R.id.partnerTypeText);
            locationText = itemView.findViewById(R.id.locationText);
            statusText = itemView.findViewById(R.id.statusText);
            createdByText = itemView.findViewById(R.id.createdByText);
        }
    }
}