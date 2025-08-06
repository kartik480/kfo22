package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RBHReportingUsersAdapter extends RecyclerView.Adapter<RBHReportingUsersAdapter.ViewHolder> {
    
    private List<RBHReportingUsersActivity.ReportingUser> users;
    private Context context;
    
    public RBHReportingUsersAdapter(List<RBHReportingUsersActivity.ReportingUser> users, Context context) {
        this.users = users;
        this.context = context;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reporting_user, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RBHReportingUsersActivity.ReportingUser user = users.get(position);
        holder.nameText.setText(user.getDisplayName());
        holder.employeeNoText.setText("Employee ID: " + (user.getEmployeeNo().isEmpty() ? "N/A" : user.getEmployeeNo()));
        holder.designationText.setText(user.getDesignationName());
        holder.departmentText.setText("Department: " + (user.getDepartmentName().isEmpty() ? "Not Assigned" : user.getDepartmentName()));
        
        // Set contact information
        String contactInfo = "";
        if (!user.getMobile().isEmpty()) {
            contactInfo += "📱 " + user.getMobile();
        }
        if (!user.getEmail().isEmpty()) {
            if (!contactInfo.isEmpty()) contactInfo += " | ";
            contactInfo += "✉ " + user.getEmail();
        }
        if (contactInfo.isEmpty()) {
            contactInfo = "Contact info not available";
        }
        holder.contactText.setText(contactInfo);
        
        // Set status
        holder.statusText.setText(user.getStatus());
        
        // Set manage icons if available
        if (user.getManageIcons() != null && !user.getManageIcons().isEmpty()) {
            holder.manageIcons.setText("Manage Icons: " + String.join(", ", user.getManageIcons()));
        } else {
            holder.manageIcons.setText("Manage Icons: None");
        }
    }
    
    @Override
    public int getItemCount() {
        return users.size();
    }
    
    public void updateData(List<RBHReportingUsersActivity.ReportingUser> newUsers) {
        this.users.clear();
        this.users.addAll(newUsers);
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, employeeNoText, designationText, departmentText, contactText, statusText, manageIcons;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            employeeNoText = itemView.findViewById(R.id.employeeNoText);
            designationText = itemView.findViewById(R.id.designationText);
            departmentText = itemView.findViewById(R.id.departmentText);
            contactText = itemView.findViewById(R.id.contactText);
            statusText = itemView.findViewById(R.id.statusText);
            manageIcons = itemView.findViewById(R.id.manageIcons);
        }
    }
} 