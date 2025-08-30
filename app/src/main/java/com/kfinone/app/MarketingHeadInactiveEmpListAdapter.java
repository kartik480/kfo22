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
        if (holder.nameText != null) {
            holder.nameText.setText(employee.getFullName());
        }
        
        // Set username
        if (holder.usernameText != null) {
            holder.usernameText.setText("Username: " + (employee.getUsername() != null ? employee.getUsername() : "N/A"));
        }
        
        // Set email
        if (holder.emailText != null) {
            holder.emailText.setText("Email: " + (employee.getEmailId() != null ? employee.getEmailId() : "N/A"));
        }
        
        // Set phone
        if (holder.phoneText != null) {
            holder.phoneText.setText("Phone: " + (employee.getPhoneNumber() != null ? employee.getPhoneNumber() : "N/A"));
        }
        
        // Set status
        if (holder.statusText != null) {
            holder.statusText.setText("Status: " + (employee.getStatus() != null ? employee.getStatus() : "N/A"));
        }
        
        // Set employee number
        if (holder.employeeNoText != null) {
            String employeeId = employee.getEmployeeNo() != null && !employee.getEmployeeNo().isEmpty() ? 
                              employee.getEmployeeNo() : "N/A";
            holder.employeeNoText.setText("Employee No: " + employeeId);
        }
        
        // Set designation (using empty string since we don't have designation in basic columns)
        if (holder.designationText != null) {
            holder.designationText.setText("Designation: Not Assigned");
        }
        
        // Set department (using empty string since we don't have department in basic columns)
        if (holder.departmentText != null) {
            holder.departmentText.setText("Department: Not Assigned");
        }
        
        // Set joining date (using created_at as joining date)
        if (holder.joiningDateText != null) {
            String joiningDate = employee.getCreatedAt() != null ? employee.getCreatedAt() : "N/A";
            holder.joiningDateText.setText("Joining Date: " + joiningDate);
        }
        
        // Set rank (using empty string since we don't have rank in basic columns)
        if (holder.rankText != null) {
            holder.rankText.setText("Rank: Not Assigned");
        }
        
        // Set work state (using empty string since we don't have work_state in basic columns)
        if (holder.workStateText != null) {
            holder.workStateText.setText("Work State: Not Assigned");
        }
        
        // Set work location (using empty string since we don't have work_location in basic columns)
        if (holder.workLocationText != null) {
            holder.workLocationText.setText("Work Location: Not Assigned");
        }
        
        // Set birth date (using empty string since we don't have dob in basic columns)
        if (holder.birthDateText != null) {
            holder.birthDateText.setText("Birth Date: Not Available");
        }
        
        // Set up view button
        if (holder.viewButton != null) {
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
    }
    
    @Override
    public int getItemCount() {
        return employees.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView usernameText;
        TextView emailText;
        TextView phoneText;
        TextView statusText;
        TextView employeeNoText;
        TextView designationText;
        TextView departmentText;
        TextView joiningDateText;
        TextView rankText;
        TextView workStateText;
        TextView workLocationText;
        TextView birthDateText;
        Button viewButton;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            usernameText = itemView.findViewById(R.id.usernameText);
            emailText = itemView.findViewById(R.id.emailText);
            phoneText = itemView.findViewById(R.id.phoneText);
            statusText = itemView.findViewById(R.id.statusText);
            employeeNoText = itemView.findViewById(R.id.employeeNoText);
            designationText = itemView.findViewById(R.id.designationText);
            departmentText = itemView.findViewById(R.id.departmentText);
            joiningDateText = itemView.findViewById(R.id.joiningDateText);
            rankText = itemView.findViewById(R.id.rankText);
            workStateText = itemView.findViewById(R.id.workStateText);
            workLocationText = itemView.findViewById(R.id.workLocationText);
            birthDateText = itemView.findViewById(R.id.birthDateText);
            viewButton = itemView.findViewById(R.id.viewButton);
        }
    }
}
