package com.kfinone.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BusinessHeadActiveEmpListAdapter extends RecyclerView.Adapter<BusinessHeadActiveEmpListAdapter.ViewHolder> {
    private List<BusinessHeadUser> users;
    private Context context;

    public BusinessHeadActiveEmpListAdapter(Context context, List<BusinessHeadUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_head_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusinessHeadUser user = users.get(position);

        // Set basic user information
        holder.nameText.setText(getFullName(user));
        holder.usernameText.setText("Username: " + (user.getUsername() != null ? user.getUsername() : "N/A"));
        holder.emailText.setText("Email: " + (user.getEmailId() != null ? user.getEmailId() : "N/A"));
        holder.phoneText.setText("Phone: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A"));
        holder.statusText.setText("Status: " + (user.getStatus() != null ? user.getStatus() : "N/A"));

        // Set company and location info
        if (user.getCompanyName() != null && !user.getCompanyName().isEmpty()) {
            holder.companyText.setText("Company: " + user.getCompanyName());
            holder.companyText.setVisibility(View.VISIBLE);
        } else {
            holder.companyText.setVisibility(View.GONE);
        }

        if (user.getBranchStateNameId() != null && !user.getBranchStateNameId().isEmpty()) {
            holder.stateText.setText("State: " + user.getBranchStateNameId());
            holder.stateText.setVisibility(View.VISIBLE);
        } else {
            holder.stateText.setVisibility(View.GONE);
        }

        if (user.getBranchLocationId() != null && !user.getBranchLocationId().isEmpty()) {
            holder.locationText.setText("Location: " + user.getBranchLocationId());
            holder.locationText.setVisibility(View.VISIBLE);
        } else {
            holder.locationText.setVisibility(View.GONE);
        }

        if (user.getRank() != null && !user.getRank().isEmpty()) {
            holder.rankText.setText("Rank: " + user.getRank());
            holder.rankText.setVisibility(View.VISIBLE);
        } else {
            holder.rankText.setVisibility(View.GONE);
        }

        // Set View button click listener
        holder.viewButton.setOnClickListener(v -> showUserDetails(user));
    }

    private String getFullName(BusinessHeadUser user) {
        String firstName = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String lastName = user.getLastName() != null ? user.getLastName().trim() : "";
        String aliasName = user.getAliasName() != null ? user.getAliasName().trim() : "";

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        } else if (!firstName.isEmpty()) {
            return firstName;
        } else if (!lastName.isEmpty()) {
            return lastName;
        } else if (!aliasName.isEmpty()) {
            return aliasName;
        } else {
            return "Unknown User";
        }
    }

    private void showUserDetails(BusinessHeadUser user) {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(user.getId() != null ? user.getId() : "N/A").append("\n\n");
        details.append("Username: ").append(user.getUsername() != null ? user.getUsername() : "N/A").append("\n\n");
        details.append("Alias Name: ").append(user.getAliasName() != null ? user.getAliasName() : "N/A").append("\n\n");
        details.append("First Name: ").append(user.getFirstName() != null ? user.getFirstName() : "N/A").append("\n\n");
        details.append("Last Name: ").append(user.getLastName() != null ? user.getLastName() : "N/A").append("\n\n");
        details.append("Password: ").append(user.getPassword() != null ? user.getPassword() : "N/A").append("\n\n");
        details.append("Phone Number: ").append(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A").append("\n\n");
        details.append("Email ID: ").append(user.getEmailId() != null ? user.getEmailId() : "N/A").append("\n\n");
        details.append("Alternative Mobile: ").append(user.getAlternativeMobileNumber() != null ? user.getAlternativeMobileNumber() : "N/A").append("\n\n");
        details.append("Company Name: ").append(user.getCompanyName() != null ? user.getCompanyName() : "N/A").append("\n\n");
        details.append("Branch State: ").append(user.getBranchStateNameId() != null ? user.getBranchStateNameId() : "N/A").append("\n\n");
        details.append("Branch Location: ").append(user.getBranchLocationId() != null ? user.getBranchLocationId() : "N/A").append("\n\n");
        details.append("Bank ID: ").append(user.getBankId() != null ? user.getBankId() : "N/A").append("\n\n");
        details.append("Account Type ID: ").append(user.getAccountTypeId() != null ? user.getAccountTypeId() : "N/A").append("\n\n");
        details.append("Office Address: ").append(user.getOfficeAddress() != null ? user.getOfficeAddress() : "N/A").append("\n\n");
        details.append("Residential Address: ").append(user.getResidentialAddress() != null ? user.getResidentialAddress() : "N/A").append("\n\n");
        details.append("Aadhaar Number: ").append(user.getAadhaarNumber() != null ? user.getAadhaarNumber() : "N/A").append("\n\n");
        details.append("PAN Number: ").append(user.getPanNumber() != null ? user.getPanNumber() : "N/A").append("\n\n");
        details.append("Account Number: ").append(user.getAccountNumber() != null ? user.getAccountNumber() : "N/A").append("\n\n");
        details.append("IFSC Code: ").append(user.getIfscCode() != null ? user.getIfscCode() : "N/A").append("\n\n");
        details.append("Rank: ").append(user.getRank() != null ? user.getRank() : "N/A").append("\n\n");
        details.append("Status: ").append(user.getStatus() != null ? user.getStatus() : "N/A").append("\n\n");
        details.append("Reporting To: ").append(user.getReportingTo() != null ? user.getReportingTo() : "N/A").append("\n\n");
        details.append("PAN Image: ").append(user.getPanImg() != null ? user.getPanImg() : "N/A").append("\n\n");
        details.append("Aadhaar Image: ").append(user.getAadhaarImg() != null ? user.getAadhaarImg() : "N/A").append("\n\n");
        details.append("Photo Image: ").append(user.getPhotoImg() != null ? user.getPhotoImg() : "N/A").append("\n\n");
        details.append("Bank Proof Image: ").append(user.getBankproofImg() != null ? user.getBankproofImg() : "N/A").append("\n\n");
        details.append("Resume File: ").append(user.getResumeFile() != null ? user.getResumeFile() : "N/A").append("\n\n");
        details.append("Data Icons: ").append(user.getDataIcons() != null ? user.getDataIcons() : "N/A").append("\n\n");
        details.append("Work Icons: ").append(user.getWorkIcons() != null ? user.getWorkIcons() : "N/A").append("\n\n");
        details.append("User ID: ").append(user.getUserId() != null ? user.getUserId() : "N/A").append("\n\n");
        details.append("Created By: ").append(user.getCreatedBy() != null ? user.getCreatedBy() : "N/A").append("\n\n");
        details.append("Created At: ").append(user.getCreatedAt() != null ? user.getCreatedAt() : "N/A").append("\n\n");
        details.append("Updated At: ").append(user.getUpdatedAt() != null ? user.getUpdatedAt() : "N/A");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("User Details")
                .setMessage(details.toString())
                .setPositiveButton("Copy Details", (dialog, which) -> {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("User Details", details.toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "User details copied to clipboard", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Close", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<BusinessHeadUser> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, usernameText, emailText, phoneText, statusText;
        TextView companyText, stateText, locationText, rankText;
        Button viewButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            usernameText = itemView.findViewById(R.id.usernameText);
            emailText = itemView.findViewById(R.id.emailText);
            phoneText = itemView.findViewById(R.id.phoneText);
            statusText = itemView.findViewById(R.id.statusText);
            companyText = itemView.findViewById(R.id.companyText);
            stateText = itemView.findViewById(R.id.stateText);
            locationText = itemView.findViewById(R.id.locationText);
            rankText = itemView.findViewById(R.id.rankText);
            viewButton = itemView.findViewById(R.id.viewButton);
        }
    }
}
