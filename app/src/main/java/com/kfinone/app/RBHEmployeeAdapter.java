package com.kfinone.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import java.util.Map;

public class RBHEmployeeAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> employeeList;
    private LayoutInflater inflater;
    private String userId, userName, firstName, lastName;

    public RBHEmployeeAdapter(Context context, List<Map<String, Object>> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
        this.inflater = LayoutInflater.from(context);
    }

    // Method to set user data for navigation
    public void setUserData(String userId, String userName, String firstName, String lastName) {
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public int getCount() {
        return employeeList.size();
    }

    @Override
    public Object getItem(int position) {
        return employeeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_rbh_employee, parent, false);
            holder = new ViewHolder();
            
            holder.nameText = convertView.findViewById(R.id.nameText);
            holder.usernameText = convertView.findViewById(R.id.usernameText);
            holder.emailText = convertView.findViewById(R.id.emailText);
            holder.phoneText = convertView.findViewById(R.id.phoneText);
            holder.companyText = convertView.findViewById(R.id.companyText);
            holder.statusText = convertView.findViewById(R.id.statusText);
            holder.rankText = convertView.findViewById(R.id.rankText);
            holder.viewButton = convertView.findViewById(R.id.viewButton);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Map<String, Object> employee = employeeList.get(position);
        
        // Get employee data using the new field names from tbl_user
        String firstName = (String) employee.get("firstName");
        String lastName = (String) employee.get("lastName");
        String username = (String) employee.get("username");
        String email = (String) employee.get("email_id");
        String phone = (String) employee.get("mobile");
        String company = (String) employee.get("company_name");
        String status = (String) employee.get("status");
        String rank = (String) employee.get("rank");
        String employeeNo = (String) employee.get("employee_no");
        String designation = (String) employee.get("designation_name");
        String department = (String) employee.get("department_name");
        
        // Set employee name
        if (firstName != null && lastName != null && !firstName.equals("N/A") && !lastName.equals("N/A")) {
            holder.nameText.setText(firstName + " " + lastName);
        } else if (firstName != null && !firstName.equals("N/A")) {
            holder.nameText.setText(firstName);
        } else if (lastName != null && !lastName.equals("N/A")) {
            holder.nameText.setText(lastName);
        } else if (username != null && !username.equals("N/A")) {
            holder.nameText.setText(username);
        } else {
            holder.nameText.setText("Unknown User");
        }
        
        // Set other fields with enhanced information
        holder.usernameText.setText("Username: " + (username != null ? username : "N/A"));
        holder.emailText.setText("Email: " + (email != null ? email : "N/A"));
        holder.phoneText.setText("Phone: " + (phone != null ? phone : "N/A"));
        holder.companyText.setText("Company: " + (company != null ? company : "N/A"));
        holder.statusText.setText("Status: " + (status != null ? status : "N/A"));
        holder.rankText.setText("Rank: " + (rank != null ? rank : "N/A"));
        
        // Add employee number and designation info if available
        if (employeeNo != null && !employeeNo.equals("N/A")) {
            holder.usernameText.setText("Employee ID: " + employeeNo + " | Username: " + (username != null ? username : "N/A"));
        }
        
        if (designation != null && !designation.equals("N/A")) {
            holder.rankText.setText("Designation: " + designation + " | Rank: " + (rank != null ? rank : "N/A"));
        }
        
        // Set up view button click listener
        holder.viewButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, RBHEmployeeDetailActivity.class);
            intent.putExtra("EMPLOYEE_DATA", (java.io.Serializable) employee);
            intent.putExtra("SOURCE_PANEL", "RBH_EMPLOYEE_USERS");
            
            // Pass user data for navigation
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (userName != null) intent.putExtra("USERNAME", userName);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            
            context.startActivity(intent);
        });
        
        return convertView;
    }
    
    public void updateData(List<Map<String, Object>> newEmployeeList) {
        this.employeeList = newEmployeeList;
        notifyDataSetChanged();
    }
    
    static class ViewHolder {
        TextView nameText, usernameText, emailText, phoneText, companyText, statusText, rankText;
        Button viewButton;
    }
}
