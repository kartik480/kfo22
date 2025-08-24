package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import java.util.Map;

public class RBHEmployeeAdapter extends BaseAdapter {
    
    private Context context;
    private List<Map<String, Object>> employeeList;
    private LayoutInflater inflater;
    
    public RBHEmployeeAdapter(Context context, List<Map<String, Object>> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
        this.inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return employeeList != null ? employeeList.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return employeeList != null ? employeeList.get(position) : null;
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
            
            // Initialize views
            holder.employeeName = convertView.findViewById(R.id.employeeName);
            holder.employeeUsername = convertView.findViewById(R.id.employeeUsername);
            holder.employeeStatus = convertView.findViewById(R.id.employeeStatus);
            holder.employeePhone = convertView.findViewById(R.id.employeePhone);
            holder.employeeEmail = convertView.findViewById(R.id.employeeEmail);
            holder.employeeCompany = convertView.findViewById(R.id.employeeCompany);
            holder.employeeRank = convertView.findViewById(R.id.employeeRank);
            holder.employeeAddress = convertView.findViewById(R.id.employeeAddress);
            holder.employeeCreatedDate = convertView.findViewById(R.id.employeeCreatedDate);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        // Get employee data
        Map<String, Object> employee = employeeList.get(position);
        
        if (employee != null) {
            // Set employee name
            String firstName = getStringValue(employee, "first_name");
            String lastName = getStringValue(employee, "last_name");
            String fullName = (firstName + " " + lastName).trim();
            holder.employeeName.setText(fullName.isEmpty() ? "N/A" : fullName);
            
            // Set username
            String username = getStringValue(employee, "username");
            holder.employeeUsername.setText("Username: " + (username.isEmpty() ? "N/A" : username));
            
            // Set status
            String status = getStringValue(employee, "status");
            if (status == null || status.isEmpty() || status.equals("1") || status.equalsIgnoreCase("Active")) {
                holder.employeeStatus.setText("Active");
                holder.employeeStatus.setBackgroundResource(R.drawable.status_active_background);
            } else {
                holder.employeeStatus.setText("Inactive");
                holder.employeeStatus.setBackgroundResource(R.drawable.status_inactive_background);
            }
            
            // Set phone number
            String phone = getStringValue(employee, "Phone_number");
            holder.employeePhone.setText("Phone: " + (phone.isEmpty() ? "N/A" : phone));
            
            // Set email
            String email = getStringValue(employee, "email_id");
            holder.employeeEmail.setText("Email: " + (email.isEmpty() ? "N/A" : email));
            
            // Set company
            String company = getStringValue(employee, "company_name");
            holder.employeeCompany.setText("Company: " + (company.isEmpty() ? "KfinOne" : company));
            
            // Set rank
            String rank = getStringValue(employee, "rank");
            holder.employeeRank.setText("Rank: " + (rank.isEmpty() ? "N/A" : rank));
            
            // Set address
            String officeAddress = getStringValue(employee, "office_address");
            String residentialAddress = getStringValue(employee, "residential_address");
            String address = "";
            if (!officeAddress.isEmpty()) {
                address = "Office: " + officeAddress;
            }
            if (!residentialAddress.isEmpty()) {
                if (!address.isEmpty()) address += "\n";
                address += "Residential: " + residentialAddress;
            }
            holder.employeeAddress.setText(address.isEmpty() ? "Address: N/A" : address);
            
            // Set created date
            String createdDate = getStringValue(employee, "created_at");
            holder.employeeCreatedDate.setText("Created: " + (createdDate.isEmpty() ? "N/A" : createdDate));
        }
        
        return convertView;
    }
    
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    public void updateData(List<Map<String, Object>> newEmployeeList) {
        this.employeeList = newEmployeeList;
        notifyDataSetChanged();
    }
    
    private static class ViewHolder {
        TextView employeeName;
        TextView employeeUsername;
        TextView employeeStatus;
        TextView employeePhone;
        TextView employeeEmail;
        TextView employeeCompany;
        TextView employeeRank;
        TextView employeeAddress;
        TextView employeeCreatedDate;
    }
}
