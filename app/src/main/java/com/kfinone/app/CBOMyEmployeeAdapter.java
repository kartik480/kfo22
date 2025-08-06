package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Map;

public class CBOMyEmployeeAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> employees;
    private LayoutInflater inflater;

    public CBOMyEmployeeAdapter(Context context, List<Map<String, Object>> employees) {
        this.context = context;
        this.employees = employees;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return employees != null ? employees.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return employees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_cbo_my_employee, parent, false);
            holder = new ViewHolder();
            holder.employeeName = convertView.findViewById(R.id.employeeName);
            holder.employeeId = convertView.findViewById(R.id.employeeId);
            holder.employeePhone = convertView.findViewById(R.id.employeePhone);
            holder.employeeEmail = convertView.findViewById(R.id.employeeEmail);
            holder.manageIconsText = convertView.findViewById(R.id.manageIconsText);
            holder.dashboardIcon = convertView.findViewById(R.id.dashboardIcon);
            holder.settingsIcon = convertView.findViewById(R.id.settingsIcon);
            holder.analyticsIcon = convertView.findViewById(R.id.analyticsIcon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get employee data
        Map<String, Object> employee = employees.get(position);

        // Set employee details
        String fullName = getStringValue(employee, "full_name");
        String employeeId = getStringValue(employee, "employee_id");
        String mobile = getStringValue(employee, "mobile");
        String email = getStringValue(employee, "email");
        String manageIconsString = getStringValue(employee, "manage_icons_string");

        // Set values with fallbacks
        holder.employeeName.setText(fullName.isEmpty() ? "Unknown Employee" : fullName);
        holder.employeeId.setText(employeeId.isEmpty() ? "N/A" : employeeId);
        holder.employeePhone.setText(mobile.isEmpty() ? "N/A" : mobile);
        
        // Format email for display
        if (email.isEmpty()) {
            holder.employeeEmail.setText("N/A");
        } else if (email.length() > 20) {
            // Truncate long emails and add ellipsis
            String truncatedEmail = email.substring(0, 17) + "...";
            holder.employeeEmail.setText(truncatedEmail);
        } else {
            holder.employeeEmail.setText(email);
        }

        // Set manage icons text (hidden by default, can be shown if needed)
        holder.manageIconsText.setText(manageIconsString.isEmpty() ? "Dashboard, Settings, Analytics" : manageIconsString);

        // Set up icon click listeners
        final String finalEmployeeName = fullName;
        final String finalEmployeeId = employeeId;

        holder.dashboardIcon.setOnClickListener(v -> {
            Toast.makeText(context, "Dashboard for " + finalEmployeeName + " (" + finalEmployeeId + ")", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to employee dashboard
        });

        holder.settingsIcon.setOnClickListener(v -> {
            Toast.makeText(context, "Settings for " + finalEmployeeName + " (" + finalEmployeeId + ")", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to employee settings
        });

        holder.analyticsIcon.setOnClickListener(v -> {
            Toast.makeText(context, "Analytics for " + finalEmployeeName + " (" + finalEmployeeId + ")", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to employee analytics
        });

        return convertView;
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return "";
        }
        return value.toString().trim();
    }

    public void updateEmployees(List<Map<String, Object>> newEmployees) {
        this.employees = newEmployees;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView employeeName;
        TextView employeeId;
        TextView employeePhone;
        TextView employeeEmail;
        TextView manageIconsText;
        LinearLayout dashboardIcon;
        LinearLayout settingsIcon;
        LinearLayout analyticsIcon;
    }
}