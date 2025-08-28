package com.kfinone.app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
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
        String rankText = "Rank: ";
        if (currentUser.getRank() != null && !currentUser.getRank().isEmpty()) {
            rankText += currentUser.getRank();
        } else {
            rankText += "N/A";
        }
        userRankText.setText(rankText);

        // Set company
        TextView userCompanyText = listItem.findViewById(R.id.userCompanyText);
        String companyText = "Company: ";
        if (currentUser.getCompanyName() != null && !currentUser.getCompanyName().isEmpty()) {
            companyText += currentUser.getCompanyName();
        } else {
            companyText += "N/A";
        }
        userCompanyText.setText(companyText);

        // Set up View Details button
        Button viewUserButton = listItem.findViewById(R.id.viewUserButton);
        viewUserButton.setOnClickListener(v -> {
            showUserDetailsDialog(currentUser);
        });

        return listItem;
    }

    private void showUserDetailsDialog(ReportingUser user) {
        // Create a custom dialog to show all user details
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Complete User Details");

        // Create a scrollable view for the content
        ScrollView scrollView = new ScrollView(context);
        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(24, 16, 24, 16);
        contentLayout.setBackgroundColor(0xFFFFFFFF);

        // Add user details in organized sections
        addDetailSection(contentLayout, "📋 BASIC INFORMATION", new String[][]{
            {"Full Name", user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : "N/A"},
            {"Username", user.getUsername() != null && !user.getUsername().isEmpty() ? user.getUsername() : "N/A"},
            {"Employee No", user.getEmployeeNo() != null && !user.getEmployeeNo().isEmpty() ? user.getEmployeeNo() : "N/A"},
            {"Status", user.getStatus() != null && !user.getStatus().isEmpty() ? user.getStatus() : "N/A"},
            {"Rank", user.getRank() != null && !user.getRank().isEmpty() ? user.getRank() : "N/A"}
        });

        addDetailSection(contentLayout, "📞 CONTACT INFORMATION", new String[][]{
            {"Email", user.getEmailId() != null && !user.getEmailId().isEmpty() ? user.getEmailId() : "N/A"},
            {"Phone", user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() ? user.getPhoneNumber() : "N/A"},
            {"Alternative Mobile", user.getAlternativeMobileNumber() != null && !user.getAlternativeMobileNumber().isEmpty() ? user.getAlternativeMobileNumber() : "N/A"}
        });

        addDetailSection(contentLayout, "💼 PROFESSIONAL DETAILS", new String[][]{
            {"Department", user.getDepartment() != null && !user.getDepartment().isEmpty() ? user.getDepartment() : "N/A"},
            {"Designation", user.getDesignation() != null && !user.getDesignation().isEmpty() ? user.getDesignation() : "N/A"},
            {"Company", user.getCompanyName() != null && !user.getCompanyName().isEmpty() ? user.getCompanyName() : "N/A"},
            {"Reporting To", user.getReportingTo() != null && !user.getReportingTo().isEmpty() ? user.getReportingTo() : "N/A"}
        });

        addDetailSection(contentLayout, "📍 LOCATION DETAILS", new String[][]{
            {"Branch State", user.getBranchState() != null && !user.getBranchState().isEmpty() ? user.getBranchState() : "N/A"},
            {"Branch Location", user.getBranchLocation() != null && !user.getBranchLocation().isEmpty() ? user.getBranchLocation() : "N/A"},
            {"Office Address", user.getOfficeAddress() != null && !user.getOfficeAddress().isEmpty() ? user.getOfficeAddress() : "N/A"},
            {"Residential Address", user.getResidentialAddress() != null && !user.getResidentialAddress().isEmpty() ? user.getResidentialAddress() : "N/A"}
        });

        addDetailSection(contentLayout, "🏦 BANKING DETAILS", new String[][]{
            {"Bank Name", user.getBankName() != null && !user.getBankName().isEmpty() ? user.getBankName() : "N/A"},
            {"Account Type", user.getAccountType() != null && !user.getAccountType().isEmpty() ? user.getAccountType() : "N/A"},
            {"Account Number", user.getAccountNumber() != null && !user.getAccountNumber().isEmpty() ? user.getAccountNumber() : "N/A"},
            {"IFSC Code", user.getIfscCode() != null && !user.getIfscCode().isEmpty() ? user.getIfscCode() : "N/A"}
        });

        addDetailSection(contentLayout, "🆔 IDENTITY DETAILS", new String[][]{
            {"Aadhaar Number", user.getAadhaarNumber() != null && !user.getAadhaarNumber().isEmpty() ? user.getAadhaarNumber() : "N/A"},
            {"PAN Number", user.getPanNumber() != null && !user.getPanNumber().isEmpty() ? user.getPanNumber() : "N/A"},
            {"User ID", user.getUserId() != null && !user.getUserId().isEmpty() ? user.getUserId() : "N/A"}
        });

        addDetailSection(contentLayout, "⚙️ SYSTEM DETAILS", new String[][]{
            {"Created By", user.getCreatedBy() != null && !user.getCreatedBy().isEmpty() ? user.getCreatedBy() : "N/A"},
            {"Created At", user.getCreatedAt() != null && !user.getCreatedAt().isEmpty() ? user.getCreatedAt() : "N/A"},
            {"Updated At", user.getUpdatedAt() != null && !user.getUpdatedAt().isEmpty() ? user.getUpdatedAt() : "N/A"}
        });

        scrollView.addView(contentLayout);
        builder.setView(scrollView);

        // Add close button
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addDetailSection(LinearLayout parent, String sectionTitle, String[][] details) {
        // Add section title
        TextView titleText = new TextView(context);
        titleText.setText(sectionTitle);
        titleText.setTextSize(16);
        titleText.setTextColor(0xFF2C3E50);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setPadding(0, 24, 0, 12);
        parent.addView(titleText);

        // Add details
        for (String[] detail : details) {
            LinearLayout detailRow = new LinearLayout(context);
            detailRow.setOrientation(LinearLayout.HORIZONTAL);
            detailRow.setPadding(0, 4, 0, 4);

            TextView labelText = new TextView(context);
            labelText.setText(detail[0] + ": ");
            labelText.setTextSize(14);
            labelText.setTextColor(0xFF7F8C8D);
            labelText.setTypeface(null, Typeface.BOLD);
            labelText.setMinWidth(120);
            detailRow.addView(labelText);

            TextView valueText = new TextView(context);
            valueText.setText(detail[1] != null ? detail[1] : "N/A");
            valueText.setTextSize(14);
            valueText.setTextColor(0xFF2C3E50);
            detailRow.addView(valueText);

            parent.addView(detailRow);
        }

        // Add separator
        View separator = new View(context);
        separator.setBackgroundColor(0xFFE0E0E0);
        separator.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1));
        separator.setPadding(0, 16, 0, 0);
        parent.addView(separator);
    }
}
