package com.kfinone.app;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.AgentViewHolder> {
    private List<AgentItem> agentList;

    public AgentAdapter(List<AgentItem> agentList) {
        this.agentList = agentList;
    }

    @NonNull
    @Override
    public AgentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agent_row, parent, false);
        return new AgentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgentViewHolder holder, int position) {
        AgentItem agent = agentList.get(position);
        holder.bind(agent);
    }

    @Override
    public int getItemCount() {
        return agentList.size();
    }

    public void updateData(List<AgentItem> newAgentList) {
        this.agentList = newAgentList;
        notifyDataSetChanged();
    }

    static class AgentViewHolder extends RecyclerView.ViewHolder {
        private TextView fullNameText, companyText, mobileText, agentTypeText, 
                       branchStateText, branchLocationText;
        private Button actionButton;

        public AgentViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameText = itemView.findViewById(R.id.fullNameText);
            companyText = itemView.findViewById(R.id.companyText);
            mobileText = itemView.findViewById(R.id.mobileText);
            agentTypeText = itemView.findViewById(R.id.agentTypeText);
            branchStateText = itemView.findViewById(R.id.branchStateText);
            branchLocationText = itemView.findViewById(R.id.branchLocationText);
            actionButton = itemView.findViewById(R.id.actionButton);
        }

        public void bind(AgentItem agent) {
            fullNameText.setText(agent.getFullName().isEmpty() ? "-" : agent.getFullName());
            companyText.setText(agent.getCompanyName().isEmpty() ? "-" : agent.getCompanyName());
            mobileText.setText(agent.getPhoneNumber().isEmpty() ? "-" : agent.getPhoneNumber());
            agentTypeText.setText(agent.getPartnerType().isEmpty() ? "-" : agent.getPartnerType());
            branchStateText.setText(agent.getState().isEmpty() ? "-" : agent.getState());
            branchLocationText.setText(agent.getLocation().isEmpty() ? "-" : agent.getLocation());

            actionButton.setOnClickListener(v -> showAgentDetails(agent));
        }

        private void showAgentDetails(AgentItem agent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Agent Details - " + agent.getFullName());
            
            String details = "Name: " + agent.getFullName() + "\n" +
                            "Company: " + agent.getCompanyName() + "\n" +
                            "Phone: " + agent.getPhoneNumber() + "\n" +
                            "Alternative Phone: " + agent.getAlternativePhoneNumber() + "\n" +
                            "Email: " + agent.getEmailId() + "\n" +
                            "Type: " + agent.getPartnerType() + "\n" +
                            "State: " + agent.getState() + "\n" +
                            "Location: " + agent.getLocation() + "\n" +
                            "Address: " + agent.getAddress() + "\n" +
                            "Created By: " + agent.getCreatedBy();
            
            builder.setMessage(details);
            builder.setPositiveButton("Close", null);
            builder.show();
        }
    }
}
