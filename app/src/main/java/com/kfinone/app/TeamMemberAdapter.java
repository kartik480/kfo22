package com.kfinone.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TeamMemberAdapter extends RecyclerView.Adapter<TeamMemberAdapter.TeamMemberViewHolder> {
    private List<TeamMember> teamMembers;
    private Context context;

    public TeamMemberAdapter(List<TeamMember> teamMembers, Context context) {
        this.teamMembers = teamMembers;
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
        TeamMember member = teamMembers.get(position);
        
        // Set member name
        holder.memberName.setText(member.getFullName());
        
        // Set designation
        holder.memberDesignation.setText(member.getDesignation());
        
        // Set avatar initials
        String initials = getInitials(member.getFullName());
        holder.avatarText.setText(initials);
        
        // Set status (assuming active for now)
        holder.memberStatus.setText("Active");
        
        // Set location (using manager name as location for demo)
        holder.memberLocation.setText(member.getManagerName() != null ? member.getManagerName() : "N/A");
        
        // Set click listeners for action buttons
        holder.emailButton.setOnClickListener(v -> {
            if (member.getEmail() != null && !member.getEmail().isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + member.getEmail()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Message from CBO");
                if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(emailIntent);
                }
            }
        });
        
        holder.phoneButton.setOnClickListener(v -> {
            if (member.getMobile() != null && !member.getMobile().isEmpty()) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + member.getMobile()));
                context.startActivity(phoneIntent);
            }
        });
        
        // Set card click listener
        holder.itemView.setOnClickListener(v -> {
            // Show member details or navigate to member profile
            // For now, just show a toast
            android.widget.Toast.makeText(context, 
                "Viewing " + member.getFullName() + "'s profile", 
                android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return teamMembers.size();
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "NA";
        }
        
        String[] names = fullName.trim().split("\\s+");
        if (names.length >= 2) {
            return (names[0].charAt(0) + "" + names[1].charAt(0)).toUpperCase();
        } else if (names.length == 1) {
            return names[0].substring(0, Math.min(2, names[0].length())).toUpperCase();
        }
        return "NA";
    }

    public static class TeamMemberViewHolder extends RecyclerView.ViewHolder {
        TextView memberName, memberDesignation, memberStatus, memberLocation, avatarText;
        ImageView emailButton, phoneButton;

        public TeamMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberName);
            memberDesignation = itemView.findViewById(R.id.memberDesignation);
            memberStatus = itemView.findViewById(R.id.memberStatus);
            memberLocation = itemView.findViewById(R.id.memberLocation);
            avatarText = itemView.findViewById(R.id.avatarText);
            emailButton = itemView.findViewById(R.id.emailButton);
            phoneButton = itemView.findViewById(R.id.phoneButton);
        }
    }
} 