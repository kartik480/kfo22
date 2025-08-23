package com.kfinone.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SdsaUserAdapter extends RecyclerView.Adapter<SdsaUserAdapter.ViewHolder> {
    
    private List<SdsaUser> users;
    private Context context;
    
    public SdsaUserAdapter(List<SdsaUser> users, Context context) {
        this.users = users;
        this.context = context;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sdsa_user, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SdsaUser user = users.get(position);
        
        // Set user name - properly display first name and last name
        String fullName = "";
        if (user.getFirstName() != null && !user.getFirstName().isEmpty() && 
            user.getLastName() != null && !user.getLastName().isEmpty()) {
            fullName = user.getFirstName() + " " + user.getLastName();
        } else if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            fullName = user.getFirstName();
        } else if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            fullName = user.getLastName();
        } else if (user.getFullName() != null && !user.getFullName().isEmpty()) {
            fullName = user.getFullName();
        } else {
            fullName = "Name Not Available";
        }
        holder.nameText.setText(fullName);
        
        // Set alias name
        String aliasName = user.getAliasName() != null && !user.getAliasName().isEmpty() ? 
                          user.getAliasName() : "No Alias";
        holder.aliasNameText.setText("Alias: " + aliasName);
        
        // Set status
        holder.statusText.setText(user.getStatus() != null ? user.getStatus() : "Unknown");
        
        // Set employee number
        String employeeNo = user.getEmployeeNo() != null && !user.getEmployeeNo().isEmpty() ? 
                          user.getEmployeeNo() : "N/A";
        holder.employeeNoText.setText("Employee ID: " + employeeNo);
        
        // Set username
        holder.usernameText.setText(user.getUsername() != null ? user.getUsername() : "N/A");
        
        // Set designation
        holder.designationText.setText(user.getDesignation() != null ? user.getDesignation() : "Not Assigned");
        
        // Set department
        String department = user.getDepartment() != null && !user.getDepartment().isEmpty() ? 
                          user.getDepartment() : "Not Assigned";
        holder.departmentText.setText("Department: " + department);
        
        // Set contact information
        String contactInfo = "";
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            contactInfo += "📱 " + user.getPhoneNumber();
        }
        if (user.getEmailId() != null && !user.getEmailId().isEmpty()) {
            if (!contactInfo.isEmpty()) contactInfo += " | ";
            contactInfo += "✉ " + user.getEmailId();
        }
        if (contactInfo.isEmpty()) {
            contactInfo = "Contact info not available";
        }
        holder.contactText.setText(contactInfo);
        
        // Set bank information
        String bankInfo = "🏦 Bank: " + (user.getBankName() != null ? user.getBankName() : "Not Assigned");
        holder.bankText.setText(bankInfo);
        
        String accountType = user.getAccountType() != null ? user.getAccountType() : "Not Assigned";
        holder.accountTypeText.setText("Account: " + accountType);
        
        // Set location information
        String location = "";
        if (user.getBranchstate() != null && !user.getBranchstate().isEmpty()) {
            location += user.getBranchstate();
        }
        if (user.getBranchloaction() != null && !user.getBranchloaction().isEmpty()) {
            if (!location.isEmpty()) location += ", ";
            location += user.getBranchloaction();
        }
        if (location.isEmpty()) {
            location = "Location not available";
        }
        holder.locationText.setText("📍 Location: " + location);
        
        // Set rank
        String rank = user.getRank() != null ? user.getRank() : "N/A";
        holder.rankText.setText("Rank: " + rank);
        
        // Set view button click listener
        holder.viewButton.setOnClickListener(v -> {
            showUserDetails(user);
        });
    }
    
    @Override
    public int getItemCount() {
        return users.size();
    }
    
    public void updateData(List<SdsaUser> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, statusText, employeeNoText, usernameText, designationText, departmentText;
        TextView contactText, bankText, accountTypeText, locationText, rankText, aliasNameText;
        Button viewButton;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            statusText = itemView.findViewById(R.id.statusText);
            employeeNoText = itemView.findViewById(R.id.employeeNoText);
            usernameText = itemView.findViewById(R.id.usernameText);
            designationText = itemView.findViewById(R.id.designationText);
            departmentText = itemView.findViewById(R.id.departmentText);
            contactText = itemView.findViewById(R.id.contactText);
            bankText = itemView.findViewById(R.id.bankText);
            accountTypeText = itemView.findViewById(R.id.accountTypeText);
            locationText = itemView.findViewById(R.id.locationText);
            rankText = itemView.findViewById(R.id.rankText);
            aliasNameText = itemView.findViewById(R.id.aliasNameText);
            viewButton = itemView.findViewById(R.id.viewButton);
        }
    }
    
    private void showUserDetails(SdsaUser user) {
        // Create a detailed dialog showing all user information
        StringBuilder details = new StringBuilder();
        details.append("📋 SDSA User Details\n\n");
        details.append("🆔 ID: ").append(user.getId() != null ? user.getId() : "N/A").append("\n");
        details.append("👤 Username: ").append(user.getUsername() != null ? user.getUsername() : "N/A").append("\n");
        details.append("🏷️ Alias Name: ").append(user.getAliasName() != null ? user.getAliasName() : "N/A").append("\n");
        details.append("👨 First Name: ").append(user.getFirstName() != null ? user.getFirstName() : "N/A").append("\n");
        details.append("👩 Last Name: ").append(user.getLastName() != null ? user.getLastName() : "N/A").append("\n");
        details.append("📱 Phone: ").append(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A").append("\n");
        details.append("📧 Email: ").append(user.getEmailId() != null ? user.getEmailId() : "N/A").append("\n");
        details.append("📱 Alt Mobile: ").append(user.getAlternativeMobileNumber() != null ? user.getAlternativeMobileNumber() : "N/A").append("\n");
        details.append("🏢 Company: ").append(user.getCompanyName() != null ? user.getCompanyName() : "N/A").append("\n");
        details.append("🏦 Branch State ID: ").append(user.getBranchStateNameId() != null ? user.getBranchStateNameId() : "N/A").append("\n");
        details.append("📍 Branch Location ID: ").append(user.getBranchLocationId() != null ? user.getBranchLocationId() : "N/A").append("\n");
        details.append("🏦 Bank ID: ").append(user.getBankId() != null ? user.getBankId() : "N/A").append("\n");
        details.append("💳 Account Type ID: ").append(user.getAccountTypeId() != null ? user.getAccountTypeId() : "N/A").append("\n");
        details.append("🏢 Office Address: ").append(user.getOfficeAddress() != null ? user.getOfficeAddress() : "N/A").append("\n");
        details.append("🏠 Residential Address: ").append(user.getResidentialAddress() != null ? user.getResidentialAddress() : "N/A").append("\n");
        details.append("🆔 Aadhaar: ").append(user.getAadhaarNumber() != null ? user.getAadhaarNumber() : "N/A").append("\n");
        details.append("📄 PAN: ").append(user.getPanNumber() != null ? user.getPanNumber() : "N/A").append("\n");
        details.append("💳 Account Number: ").append(user.getAccountNumber() != null ? user.getAccountNumber() : "N/A").append("\n");
        details.append("🏦 IFSC Code: ").append(user.getIfscCode() != null ? user.getIfscCode() : "N/A").append("\n");
        details.append("⭐ Rank: ").append(user.getRank() != null ? user.getRank() : "N/A").append("\n");
        details.append("📊 Status: ").append(user.getStatus() != null ? user.getStatus() : "N/A").append("\n");
        details.append("👥 Reporting To: ").append(user.getReportingTo() != null ? user.getReportingTo() : "N/A").append("\n");
        details.append("🆔 Employee No: ").append(user.getEmployeeNo() != null ? user.getEmployeeNo() : "N/A").append("\n");
        details.append("🏢 Department: ").append(user.getDepartment() != null ? user.getDepartment() : "N/A").append("\n");
        details.append("👔 Designation: ").append(user.getDesignation() != null ? user.getDesignation() : "N/A").append("\n");
        details.append("🏛️ Branch State: ").append(user.getBranchstate() != null ? user.getBranchstate() : "N/A").append("\n");
        details.append("📍 Branch Location: ").append(user.getBranchloaction() != null ? user.getBranchloaction() : "N/A").append("\n");
        details.append("🏦 Bank Name: ").append(user.getBankName() != null ? user.getBankName() : "N/A").append("\n");
        details.append("💳 Account Type: ").append(user.getAccountType() != null ? user.getAccountType() : "N/A").append("\n");
        details.append("📷 PAN Image: ").append(user.getPanImg() != null ? user.getPanImg() : "N/A").append("\n");
        details.append("🆔 Aadhaar Image: ").append(user.getAadhaarImg() != null ? user.getAadhaarImg() : "N/A").append("\n");
        details.append("📸 Photo: ").append(user.getPhotoImg() != null ? user.getPhotoImg() : "N/A").append("\n");
        details.append("🏦 Bank Proof: ").append(user.getBankproofImg() != null ? user.getBankproofImg() : "N/A").append("\n");
        details.append("🆔 User ID: ").append(user.getUserId() != null ? user.getUserId() : "N/A").append("\n");
        details.append("👤 Created By: ").append(user.getCreatedBy() != null ? user.getCreatedBy() : "N/A").append("\n");
        details.append("📅 Created At: ").append(user.getCreatedAt() != null ? user.getCreatedAt() : "N/A").append("\n");
        details.append("🔄 Updated At: ").append(user.getUpdatedAt() != null ? user.getUpdatedAt() : "N/A").append("\n");
        
        // Show the details in a dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("SDSA User Complete Details");
        builder.setMessage(details.toString());
        builder.setPositiveButton("Close", null);
        builder.setNegativeButton("Copy Details", (dialog, which) -> {
            // Copy details to clipboard
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("SDSA User Details", details.toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Details copied to clipboard", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
} 