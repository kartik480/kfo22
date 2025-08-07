package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class PartnerAdapter extends BaseAdapter {
    private Context context;
    private List<PartnerUser> partnerList;
    private List<PartnerUser> filteredList;

    public PartnerAdapter(Context context, List<PartnerUser> partnerList) {
        this.context = context;
        this.partnerList = partnerList;
        this.filteredList = new ArrayList<>(partnerList);
    }

    @Override
    public int getCount() {
        System.out.println("🔧 PartnerAdapter.getCount() called, returning: " + filteredList.size());
        return filteredList.size();
    }

    @Override
    public PartnerUser getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("🔧 PartnerAdapter.getView() called for position: " + position);
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.partner_list_item, parent, false);
            holder = new ViewHolder();
            
            holder.partnerNameText = convertView.findViewById(R.id.partnerNameText);
            holder.partnerUsernameText = convertView.findViewById(R.id.partnerUsernameText);
            holder.statusText = convertView.findViewById(R.id.statusText);
            holder.emailText = convertView.findViewById(R.id.emailText);
            holder.phoneText = convertView.findViewById(R.id.phoneText);
            holder.companyText = convertView.findViewById(R.id.companyText);
            holder.departmentText = convertView.findViewById(R.id.departmentText);
            holder.designationText = convertView.findViewById(R.id.designationText);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        PartnerUser partner = getItem(position);
        
        // Set partner name
        String fullName = partner.getFirstName() + " " + partner.getLastName();
        holder.partnerNameText.setText(fullName);
        
        // Set username
        holder.partnerUsernameText.setText(partner.getUsername());
        
        // Set status
        String status = partner.getStatus();
        String displayStatus = "1".equals(status) ? "Active" : (status != null ? status : "Inactive");
        holder.statusText.setText(displayStatus);
        
        // Set status color based on status
        if ("Active".equalsIgnoreCase(displayStatus) || "1".equals(status)) {
            holder.statusText.setTextColor(context.getResources().getColor(R.color.green));
            holder.statusText.setBackgroundResource(R.drawable.status_active_background);
        } else {
            holder.statusText.setTextColor(context.getResources().getColor(R.color.red));
            holder.statusText.setBackgroundResource(R.drawable.status_inactive_background);
        }
        
        // Set email
        String email = partner.getEmailId();
        holder.emailText.setText(email != null ? email : "N/A");
        
        // Set phone
        String phone = partner.getPhoneNumber();
        holder.phoneText.setText(phone != null ? phone : "N/A");
        
        // Set company
        String company = partner.getCompanyName();
        holder.companyText.setText(company != null ? company : "N/A");
        
        // Set department
        String department = partner.getDepartment();
        holder.departmentText.setText(department != null ? department : "N/A");
        
        // Set designation
        String designation = partner.getDesignation();
        holder.designationText.setText(designation != null ? designation : "N/A");
        
        return convertView;
    }
    
    public void filter(String query) {
        filteredList.clear();
        
        if (query == null || query.isEmpty()) {
            filteredList.addAll(partnerList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (PartnerUser partner : partnerList) {
                if (partner.getFirstName().toLowerCase().contains(lowerCaseQuery) ||
                    partner.getLastName().toLowerCase().contains(lowerCaseQuery) ||
                    partner.getUsername().toLowerCase().contains(lowerCaseQuery) ||
                    (partner.getEmailId() != null && partner.getEmailId().toLowerCase().contains(lowerCaseQuery)) ||
                    (partner.getPhoneNumber() != null && partner.getPhoneNumber().toLowerCase().contains(lowerCaseQuery)) ||
                    (partner.getCompanyName() != null && partner.getCompanyName().toLowerCase().contains(lowerCaseQuery))) {
                    filteredList.add(partner);
                }
            }
        }
        notifyDataSetChanged();
    }
    
    public void updateData(List<PartnerUser> newPartnerList) {
        System.out.println("🔧 PartnerAdapter.updateData() called with " + newPartnerList.size() + " partners");
        this.partnerList = newPartnerList;
        this.filteredList = new ArrayList<>(newPartnerList);
        System.out.println("🔧 PartnerAdapter: partnerList size = " + this.partnerList.size());
        System.out.println("🔧 PartnerAdapter: filteredList size = " + this.filteredList.size());
        notifyDataSetChanged();
        System.out.println("🔧 PartnerAdapter: notifyDataSetChanged() called");
    }
    
    private static class ViewHolder {
        TextView partnerNameText;
        TextView partnerUsernameText;
        TextView statusText;
        TextView emailText;
        TextView phoneText;
        TextView companyText;
        TextView departmentText;
        TextView designationText;
    }
} 