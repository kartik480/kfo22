package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
        
        // Set user name
        holder.nameText.setText(user.getFullName() != null ? user.getFullName() : 
                              (user.getFirstName() + " " + user.getLastName()));
        
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
        TextView contactText, bankText, accountTypeText, locationText, rankText;
        
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
        }
    }
} 