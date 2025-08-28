package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class ReportingUserAdapter extends ArrayAdapter<ReportingUser> {
    
    private Context context;
    private List<ReportingUser> users;

    public ReportingUserAdapter(Context context, List<ReportingUser> users) {
        super(context, 0, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(
                R.layout.reporting_user_item, parent, false);
        }

        ReportingUser currentUser = users.get(position);

        // Set user name
        TextView userNameText = listItem.findViewById(R.id.userNameText);
        userNameText.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "N/A");

        // Set status
        TextView userStatusText = listItem.findViewById(R.id.userStatusText);
        String status = currentUser.getStatus();
        userStatusText.setText(status != null ? status : "Unknown");
        
        // Set status color based on status value
        if (status != null && "active".equalsIgnoreCase(status)) {
            userStatusText.setTextColor(0xFF27AE60); // Green
            userStatusText.setBackgroundResource(R.drawable.status_background);
        } else if (status != null && "inactive".equalsIgnoreCase(status)) {
            userStatusText.setTextColor(0xFFE74C3C); // Red
            userStatusText.setBackgroundResource(R.drawable.status_background);
        } else {
            userStatusText.setTextColor(0xFF7F8C8D); // Gray
            userStatusText.setBackground(null);
        }

        // Set designation
        TextView userDesignationText = listItem.findViewById(R.id.userDesignationText);
        userDesignationText.setText(currentUser.getDesignation() != null ? currentUser.getDesignation() : "N/A");

        // Set email
        TextView userEmailText = listItem.findViewById(R.id.userEmailText);
        userEmailText.setText(currentUser.getEmailId() != null ? currentUser.getEmailId() : "N/A");

        // Set phone
        TextView userPhoneText = listItem.findViewById(R.id.userPhoneText);
        userPhoneText.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "N/A");

        // Set department
        TextView userDepartmentText = listItem.findViewById(R.id.userDepartmentText);
        userDepartmentText.setText(currentUser.getDepartment() != null ? currentUser.getDepartment() : "N/A");

        // Set employee number
        TextView userEmployeeNoText = listItem.findViewById(R.id.userEmployeeNoText);
        userEmployeeNoText.setText(currentUser.getEmployeeNo() != null ? currentUser.getEmployeeNo() : "N/A");

        // Set rank
        TextView userRankText = listItem.findViewById(R.id.userRankText);
        userRankText.setText(currentUser.getRank() != null ? "Rank: " + currentUser.getRank() : "Rank: N/A");

        // Set company
        TextView userCompanyText = listItem.findViewById(R.id.userCompanyText);
        userCompanyText.setText(currentUser.getCompanyName() != null ? "Company: " + currentUser.getCompanyName() : "Company: N/A");

        return listItem;
    }
}
