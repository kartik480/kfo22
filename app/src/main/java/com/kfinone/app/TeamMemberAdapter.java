package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TeamMemberAdapter extends RecyclerView.Adapter<TeamMemberAdapter.TeamMemberViewHolder> {

    private List<InsuranceTeamActivity.TeamMember> teamList;
    private Context context;

    public TeamMemberAdapter(List<InsuranceTeamActivity.TeamMember> teamList, Context context) {
        this.teamList = teamList;
        this.context = context;
    }

    @NonNull
    @Override
    public TeamMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_member, parent, false);
        return new TeamMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamMemberViewHolder holder, int position) {
        InsuranceTeamActivity.TeamMember member = teamList.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public class TeamMemberViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText, userTypeText, emailText, phoneText, locationText, statusChip;
        private Button viewButton, editButton, deleteButton;

        public TeamMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            
            nameText = itemView.findViewById(R.id.nameText);
            userTypeText = itemView.findViewById(R.id.userTypeText);
            emailText = itemView.findViewById(R.id.emailText);
            phoneText = itemView.findViewById(R.id.phoneText);
            locationText = itemView.findViewById(R.id.locationText);
            statusChip = itemView.findViewById(R.id.statusChip);
            viewButton = itemView.findViewById(R.id.viewButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(InsuranceTeamActivity.TeamMember member) {
            nameText.setText(member.getName());
            userTypeText.setText(member.getUserType());
            emailText.setText(member.getEmail());
            phoneText.setText(member.getPhone());
            locationText.setText(member.getLocation());
            statusChip.setText(member.getStatus());

            // Set status chip background color based on status
            if (member.getStatus().equals("Active")) {
                statusChip.setBackgroundResource(R.drawable.status_background);
            } else {
                statusChip.setBackgroundResource(R.drawable.status_inactive_background);
            }

            // Setup button click listeners
            viewButton.setOnClickListener(v -> viewMember(member));
            editButton.setOnClickListener(v -> editMember(member));
            deleteButton.setOnClickListener(v -> deleteMember(member));

            // Setup item click listener
            itemView.setOnClickListener(v -> viewMember(member));
        }

        private void viewMember(InsuranceTeamActivity.TeamMember member) {
            Toast.makeText(context, "Viewing member: " + member.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to member details activity
        }

        private void editMember(InsuranceTeamActivity.TeamMember member) {
            Toast.makeText(context, "Editing member: " + member.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to edit member activity
        }

        private void deleteMember(InsuranceTeamActivity.TeamMember member) {
            Toast.makeText(context, "Deleting member: " + member.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Show confirmation dialog and delete member
        }
    }
} 