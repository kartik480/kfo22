package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RegionalBusinessHeadUserAdapter extends RecyclerView.Adapter<RegionalBusinessHeadUserAdapter.UserViewHolder> {
    
    private List<RegionalBusinessHeadUser> userList;
    private Context context;

    public RegionalBusinessHeadUserAdapter(List<RegionalBusinessHeadUser> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_regional_business_head_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        RegionalBusinessHeadUser user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText, employeeIdText, phoneText, emailText, companyText, stateText, locationText, rankText;
        private ImageView dashboardIcon, settingsIcon, analyticsIcon;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            
            nameText = itemView.findViewById(R.id.nameText);
            employeeIdText = itemView.findViewById(R.id.employeeIdText);
            phoneText = itemView.findViewById(R.id.phoneText);
            emailText = itemView.findViewById(R.id.emailText);
            
            // Add new text views for additional information
            companyText = itemView.findViewById(R.id.companyText);
            stateText = itemView.findViewById(R.id.stateText);
            locationText = itemView.findViewById(R.id.locationText);
            rankText = itemView.findViewById(R.id.rankText);
            
            dashboardIcon = itemView.findViewById(R.id.dashboardIcon);
            settingsIcon = itemView.findViewById(R.id.settingsIcon);
            analyticsIcon = itemView.findViewById(R.id.analyticsIcon);
        }

        public void bind(RegionalBusinessHeadUser user) {
            // Basic information
            nameText.setText("Name: " + user.getFullName());
            employeeIdText.setText("Employee ID: " + user.getEmployeeId());
            phoneText.setText("Phone: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A"));
            emailText.setText("Email: " + (user.getEmailId() != null ? user.getEmailId() : "N/A"));
            
            // Additional information from new fields (handle null values safely)
            if (companyText != null) {
                companyText.setText("Company: " + (user.getCompanyName() != null ? user.getCompanyName() : "N/A"));
            }
            
            if (stateText != null) {
                // Since we don't have resolved state name, show the ID or "N/A"
                String stateInfo = user.getBranchStateNameId() != null ? "ID: " + user.getBranchStateNameId() : "N/A";
                stateText.setText("State: " + stateInfo);
            }
            
            if (locationText != null) {
                // Since we don't have resolved location name, show the ID or "N/A"
                String locationInfo = user.getBranchLocationId() != null ? "ID: " + user.getBranchLocationId() : "N/A";
                locationText.setText("Location: " + locationInfo);
            }
            
            if (rankText != null) {
                rankText.setText("Rank: " + (user.getRank() != null ? user.getRank() : "N/A"));
            }

            // Set click listeners for manage icons
            dashboardIcon.setOnClickListener(v -> {
                Toast.makeText(context, "Dashboard for " + user.getFullName(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to user dashboard
            });

            settingsIcon.setOnClickListener(v -> {
                Toast.makeText(context, "Settings for " + user.getFullName(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to user settings
            });

            analyticsIcon.setOnClickListener(v -> {
                Toast.makeText(context, "Analytics for " + user.getFullName(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to user analytics
            });

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                String userInfo = String.format("User: %s\nUsername: %s\nCompany: %s\nState ID: %s\nLocation ID: %s\nRank: %s\nReporting To: %s",
                    user.getFullName(),
                    user.getUsername(),
                    user.getCompanyName() != null ? user.getCompanyName() : "N/A",
                    user.getBranchStateNameId() != null ? user.getBranchStateNameId() : "N/A",
                    user.getBranchLocationId() != null ? user.getBranchLocationId() : "N/A",
                    user.getRank() != null ? user.getRank() : "N/A",
                    user.getReportingTo() != null ? user.getReportingTo() : "N/A"
                );
                Toast.makeText(context, userInfo, Toast.LENGTH_LONG).show();
                // TODO: Navigate to user details
            });
        }
    }
} 