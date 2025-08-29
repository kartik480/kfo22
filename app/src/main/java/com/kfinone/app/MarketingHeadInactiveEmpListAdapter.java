package com.kfinone.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MarketingHeadInactiveEmpListAdapter extends RecyclerView.Adapter<MarketingHeadInactiveEmpListAdapter.ViewHolder> {
    
    private List<MarketingHeadUser> employees;
    private Context context;
    
    public MarketingHeadInactiveEmpListAdapter(List<MarketingHeadUser> employees, Context context) {
        this.employees = employees;
        this.context = context;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_marketing_head_user, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketingHeadUser employee = employees.get(position);
        
        // Set employee name
        holder.nameText.setText(employee.getFullName());
        
        // Set employee ID
        String employeeId = employee.getEmployeeNo() != null && !employee.getEmployeeNo().isEmpty() ? 
                          employee.getEmployeeNo() : "N/A";
        holder.employeeIdText.setText("Employee ID: " + employeeId);
        
        // Set username
        holder.usernameText.setText(employee.getUsername() != null ? employee.getUsername() : "N/A");
        
        // Set designation
        holder.designationText.setText(employee.getDesignation() != null ? employee.getDesignation() : "Not Assigned");
        
        // Set department
        String department = employee.getDepartment() != null && !employee.getDepartment().isEmpty() ? 
                          employee.getDepartment() : "Not Assigned";
        holder.departmentText.setText("Department: " + department);
        
        // Set contact information
        String contactInfo = "";
        if (employee.getPhoneNumber() != null && !employee.getPhoneNumber().isEmpty()) {
            contactInfo += "📱 " + employee.getPhoneNumber();
        }
        if (employee.getEmailId() != null && !employee.getEmailId().isEmpty()) {
            if (!contactInfo.isEmpty()) contactInfo += " | ";
            contactInfo += "✉ " + employee.getEmailId();
        }
        if (contactInfo.isEmpty()) {
            contactInfo = "Contact info not available";
        }
        holder.contactText.setText(contactInfo);
        
        // Set location information
        String location = "";
        if (employee.getWorkState() != null && !employee.getWorkState().isEmpty()) {
            location += employee.getWorkState();
        }
        if (employee.getWorkLocation() != null && !employee.getWorkLocation().isEmpty()) {
            if (!location.isEmpty()) location += " - ";
            location += employee.getWorkLocation();
        }
        if (location.isEmpty()) {
            location = "Location not assigned";
        }
        holder.locationText.setText("📍 " + location);
        
        // Set status
        holder.statusText.setText(employee.getStatus() != null ? employee.getStatus() : "Unknown");
        
        // Set up view button
        holder.viewButton.setOnClickListener(v -> {
            // Launch user detail activity
            Intent intent = new Intent(context, MarketingHeadUserDetailActivity.class);
            intent.putExtra("USER_ID", employee.getId());
            intent.putExtra("USERNAME", employee.getUsername());
            intent.putExtra("FIRST_NAME", employee.getFirstName());
            intent.putExtra("LAST_NAME", employee.getLastName());
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return employees.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView employeeIdText;
        TextView usernameText;
        TextView designationText;
        TextView departmentText;
        TextView contactText;
        TextView locationText;
        TextView statusText;
        Button viewButton;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            employeeIdText = itemView.findViewById(R.id.employeeIdText);
            usernameText = itemView.findViewById(R.id.usernameText);
            designationText = itemView.findViewById(R.id.designationText);
            departmentText = itemView.findViewById(R.id.departmentText);
            contactText = itemView.findViewById(R.id.contactText);
            locationText = itemView.findViewById(R.id.locationText);
            statusText = itemView.findViewById(R.id.statusText);
            viewButton = itemView.findViewById(R.id.viewButton);
        }
    }
}
