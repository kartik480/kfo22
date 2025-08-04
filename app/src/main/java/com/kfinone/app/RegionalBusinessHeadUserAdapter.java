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
        private TextView nameText, employeeIdText, phoneText, emailText;
        private ImageView dashboardIcon, settingsIcon, analyticsIcon;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            
            nameText = itemView.findViewById(R.id.nameText);
            employeeIdText = itemView.findViewById(R.id.employeeIdText);
            phoneText = itemView.findViewById(R.id.phoneText);
            emailText = itemView.findViewById(R.id.emailText);
            
            dashboardIcon = itemView.findViewById(R.id.dashboardIcon);
            settingsIcon = itemView.findViewById(R.id.settingsIcon);
            analyticsIcon = itemView.findViewById(R.id.analyticsIcon);
        }

        public void bind(RegionalBusinessHeadUser user) {
            nameText.setText("Name: " + user.getFullName());
            employeeIdText.setText("Employee ID: " + user.getEmployeeId());
            phoneText.setText("Phone: " + user.getPhoneNumber());
            emailText.setText("Email: " + user.getEmailId());

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
                Toast.makeText(context, "Selected: " + user.getFullName(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to user details
            });
        }
    }
} 