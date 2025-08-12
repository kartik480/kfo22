package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import java.util.List;
import java.util.ArrayList;

public class PartnerAdapter extends ArrayAdapter<PartnerUser> {
    private Context context;
    private List<PartnerUser> partnerList;
    private List<PartnerUser> filteredPartnerList;

    public PartnerAdapter(Context context, List<PartnerUser> partnerList) {
        super(context, 0, partnerList != null ? partnerList : new ArrayList<>());
        this.context = context;
        this.partnerList = partnerList != null ? new ArrayList<>(partnerList) : new ArrayList<>();
        this.filteredPartnerList = new ArrayList<>(this.partnerList);
    }

    @Override
    public int getCount() {
        return filteredPartnerList.size();
    }

    @Override
    public PartnerUser getItem(int position) {
        return filteredPartnerList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.partner_list_item, parent, false);
            holder = new ViewHolder();
            holder.partnerCard = convertView.findViewById(R.id.partnerCard);
            holder.nameText = convertView.findViewById(R.id.partnerName);
            holder.companyText = convertView.findViewById(R.id.partnerCompany);
            holder.phoneText = convertView.findViewById(R.id.partnerPhone);
            holder.emailText = convertView.findViewById(R.id.partnerEmail);
            holder.statusText = convertView.findViewById(R.id.partnerStatus);
            holder.partnerTypeTag = convertView.findViewById(R.id.partnerTypeTag);
            holder.locationTag = convertView.findViewById(R.id.locationTag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        PartnerUser partner = filteredPartnerList.get(position);
        
        // Set partner name
        holder.nameText.setText(partner.getDisplayName());
        
        // Set company information
        if (partner.getDisplayCompany() != null && !partner.getDisplayCompany().equals("No company")) { 
            holder.companyText.setVisibility(View.VISIBLE);
            holder.companyText.setText(partner.getDisplayCompany());
        } else {
            holder.companyText.setVisibility(View.GONE);
        }
        
        // Set phone information
        holder.phoneText.setText(partner.getDisplayPhone());
        
        // Set email information
        if (partner.getDisplayEmail() != null && !partner.getDisplayEmail().equals("No email")) {
            holder.emailText.setVisibility(View.VISIBLE);
            holder.emailText.setText(partner.getDisplayEmail());
        } else {
            holder.emailText.setVisibility(View.GONE);
        }
        
        // Location information is not displayed in this layout
        
        // Set status with color
        if (partner.isActive()) {
            holder.statusText.setText("Active");
            holder.statusText.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.statusText.setText("Inactive");
            holder.statusText.setTextColor(context.getResources().getColor(R.color.red));
        }
        
        // Set partner type tag
        if (partner.getPartnerType() != null && !partner.getPartnerType().trim().isEmpty()) {
            holder.partnerTypeTag.setVisibility(View.VISIBLE);
            holder.partnerTypeTag.setText(partner.getPartnerType());
        } else {
            holder.partnerTypeTag.setVisibility(View.GONE);
        }
        
        // Set location tag
        if (partner.getState() != null && !partner.getState().trim().isEmpty()) {
            holder.locationTag.setVisibility(View.VISIBLE);
            holder.locationTag.setText(partner.getState());
        } else {
            holder.locationTag.setVisibility(View.GONE);
        }
        
        return convertView;
    }

    private static class ViewHolder {
        CardView partnerCard;
        TextView nameText;
        TextView companyText;
        TextView phoneText;
        TextView emailText;
        TextView statusText;
        TextView partnerTypeTag;
        TextView locationTag;
    }

    public void updateData(List<PartnerUser> newData) {
        if (newData != null) {
            // Create new lists to avoid clearing the original data
            this.partnerList = new ArrayList<>(newData);
            this.filteredPartnerList = new ArrayList<>(newData);
        }
        notifyDataSetChanged();
    }

    public void setPartnerList(List<PartnerUser> partnerList) {
        this.partnerList = partnerList;
        notifyDataSetChanged();
    }

    // Filter method for search functionality
    public void filter(String query) {
        List<PartnerUser> filteredList = new ArrayList<>();
        
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(partnerList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (PartnerUser partner : partnerList) {
                if (partner.getDisplayName().toLowerCase().contains(lowerCaseQuery) ||
                    partner.getDisplayCompany().toLowerCase().contains(lowerCaseQuery) ||
                    partner.getDisplayPhone().toLowerCase().contains(lowerCaseQuery) ||
                    partner.getDisplayEmail().toLowerCase().contains(lowerCaseQuery) ||
                    (partner.getLocation() != null && partner.getLocation().toLowerCase().contains(lowerCaseQuery)) ||
                    (partner.getState() != null && partner.getState().toLowerCase().contains(lowerCaseQuery))) {
                    filteredList.add(partner);
                }
            }
        }
        
        this.filteredPartnerList = filteredList;
        notifyDataSetChanged();
    }
} 