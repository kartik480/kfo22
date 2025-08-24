package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class BusinessHeadPartnerAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, Object>> partnerList;
    private LayoutInflater inflater;

    public BusinessHeadPartnerAdapter(Context context, List<Map<String, Object>> partnerList) {
        this.context = context;
        this.partnerList = partnerList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return partnerList.size();
    }

    @Override
    public Object getItem(int position) {
        return partnerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_business_head_partner, parent, false);
            holder = new ViewHolder();
            
            holder.nameText = convertView.findViewById(R.id.nameText);
            holder.usernameText = convertView.findViewById(R.id.usernameText);
            holder.emailText = convertView.findViewById(R.id.emailText);
            holder.phoneText = convertView.findViewById(R.id.phoneText);
            holder.companyText = convertView.findViewById(R.id.companyText);
            holder.statusText = convertView.findViewById(R.id.statusText);
            holder.rankText = convertView.findViewById(R.id.rankText);
            holder.employeeNoText = convertView.findViewById(R.id.employeeNoText);
            holder.departmentText = convertView.findViewById(R.id.departmentText);
            holder.designationText = convertView.findViewById(R.id.designationText);
            holder.branchStateText = convertView.findViewById(R.id.branchStateText);
            holder.branchLocationText = convertView.findViewById(R.id.branchLocationText);
            holder.bankNameText = convertView.findViewById(R.id.bankNameText);
            holder.accountTypeText = convertView.findViewById(R.id.accountTypeText);
            holder.createdAtText = convertView.findViewById(R.id.createdAtText);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        // Get partner data
        Map<String, Object> partner = partnerList.get(position);
        
        // Get partner data
        String firstName = (String) partner.get("first_name");
        String lastName = (String) partner.get("last_name");
        String username = (String) partner.get("username");
        String email = (String) partner.get("email_id");
        String phone = (String) partner.get("Phone_number");
        String company = (String) partner.get("company_name");
        String status = (String) partner.get("status");
        String rank = (String) partner.get("rank");
        String employeeNo = (String) partner.get("employee_no");
        String department = (String) partner.get("department");
        String designation = (String) partner.get("designation");
        String branchState = (String) partner.get("branchstate");
        String branchLocation = (String) partner.get("branchloaction");
        String bankName = (String) partner.get("bank_name");
        String accountType = (String) partner.get("account_type");
        String createdAt = (String) partner.get("created_at");
        
        // Set partner name
        if (firstName != null && lastName != null && !firstName.equals("N/A") && !lastName.equals("N/A")) {
            holder.nameText.setText(firstName + " " + lastName);
        } else if (firstName != null && !firstName.equals("N/A")) {
            holder.nameText.setText(firstName);
        } else if (lastName != null && !lastName.equals("N/A")) {
            holder.nameText.setText(lastName);
        } else if (username != null && !username.equals("N/A")) {
            holder.nameText.setText(username);
        } else {
            holder.nameText.setText("Unknown Partner");
        }
        
        // Set other fields
        holder.usernameText.setText("Username: " + (username != null ? username : "N/A"));
        holder.emailText.setText("Email: " + (email != null ? email : "N/A"));
        holder.phoneText.setText("Phone: " + (phone != null ? phone : "N/A"));
        holder.companyText.setText("Company: " + (company != null ? company : "N/A"));
        holder.statusText.setText("Status: " + (status != null ? status : "N/A"));
        holder.rankText.setText("Rank: " + (rank != null ? rank : "N/A"));
        holder.employeeNoText.setText("Employee No: " + (employeeNo != null ? employeeNo : "N/A"));
        holder.departmentText.setText("Department: " + (department != null ? department : "N/A"));
        holder.designationText.setText("Designation: " + (designation != null ? designation : "N/A"));
        holder.branchStateText.setText("Branch State: " + (branchState != null ? branchState : "N/A"));
        holder.branchLocationText.setText("Branch Location: " + (branchLocation != null ? branchLocation : "N/A"));
        holder.bankNameText.setText("Bank Name: " + (bankName != null ? bankName : "N/A"));
        holder.accountTypeText.setText("Account Type: " + (accountType != null ? accountType : "N/A"));
        holder.createdAtText.setText("Created: " + (createdAt != null ? createdAt : "N/A"));
        
        return convertView;
    }
    
    public void updateData(List<Map<String, Object>> newPartnerList) {
        this.partnerList.clear();
        this.partnerList.addAll(newPartnerList);
        notifyDataSetChanged();
    }
    
    static class ViewHolder {
        TextView nameText, usernameText, emailText, phoneText, companyText, statusText, rankText;
        TextView employeeNoText, departmentText, designationText, branchStateText, branchLocationText;
        TextView bankNameText, accountTypeText, createdAtText;
    }
}
