package com.kfinone.app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PartnerUsersAdapter extends ArrayAdapter<CboMyPartnerUsersActivity.PartnerUser> {
    private Context context;
    private List<CboMyPartnerUsersActivity.PartnerUser> partnerUsers;

    public PartnerUsersAdapter(Context context, List<CboMyPartnerUsersActivity.PartnerUser> partnerUsers) {
        super(context, 0, partnerUsers);
        this.context = context;
        this.partnerUsers = partnerUsers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_partner_user, parent, false);
        }

        CboMyPartnerUsersActivity.PartnerUser partnerUser = getItem(position);

        if (partnerUser != null) {
            // Set user name
            TextView nameText = convertView.findViewById(R.id.nameText);
            nameText.setText(partnerUser.getFullName());

            // Set username
            TextView usernameText = convertView.findViewById(R.id.usernameText);
            if (partnerUser.getUsername() != null && !partnerUser.getUsername().isEmpty()) {
                usernameText.setText("Username: " + partnerUser.getUsername());
                usernameText.setVisibility(View.VISIBLE);
            } else {
                usernameText.setVisibility(View.GONE);
            }

            // Set phone number
            TextView phoneText = convertView.findViewById(R.id.phoneText);
            if (partnerUser.getPhoneNumber() != null && !partnerUser.getPhoneNumber().isEmpty()) {
                phoneText.setText("Phone: " + partnerUser.getPhoneNumber());
                phoneText.setVisibility(View.VISIBLE);
            } else {
                phoneText.setVisibility(View.GONE);
            }

            // Set email
            TextView emailText = convertView.findViewById(R.id.emailText);
            if (partnerUser.getEmail() != null && !partnerUser.getEmail().isEmpty()) {
                emailText.setText("Email: " + partnerUser.getEmail());
                emailText.setVisibility(View.VISIBLE);
            } else {
                emailText.setVisibility(View.GONE);
            }

            // Set company name
            TextView companyText = convertView.findViewById(R.id.companyText);
            if (partnerUser.getCompanyName() != null && !partnerUser.getCompanyName().isEmpty()) {
                companyText.setText("Company: " + partnerUser.getCompanyName());
                companyText.setVisibility(View.VISIBLE);
            } else {
                companyText.setVisibility(View.GONE);
            }

            // Set status with color coding
            TextView statusText = convertView.findViewById(R.id.statusText);
            String status = partnerUser.getStatus();
            if (status != null && !status.isEmpty()) {
                statusText.setText("Status: " + status);
                if ("Active".equals(status) || "1".equals(status)) {
                    statusText.setTextColor(Color.parseColor("#28a745")); // Green for active
                } else {
                    statusText.setTextColor(Color.parseColor("#dc3545")); // Red for inactive
                }
                statusText.setVisibility(View.VISIBLE);
            } else {
                statusText.setVisibility(View.GONE);
            }

            // Set created date
            TextView createdAtText = convertView.findViewById(R.id.createdAtText);
            if (partnerUser.getCreatedAt() != null && !partnerUser.getCreatedAt().isEmpty()) {
                try {
                    // Try to parse and format the date
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    Date date = inputFormat.parse(partnerUser.getCreatedAt());
                    String formattedDate = outputFormat.format(date);
                    createdAtText.setText("Created: " + formattedDate);
                } catch (Exception e) {
                    // If parsing fails, show the original date
                    createdAtText.setText("Created: " + partnerUser.getCreatedAt());
                }
                createdAtText.setVisibility(View.VISIBLE);
            } else {
                createdAtText.setVisibility(View.GONE);
            }

            // Set created by information
            TextView creatorText = convertView.findViewById(R.id.creatorText);
            if (creatorText != null) {
                if (partnerUser.getCreatedBy() != null && !partnerUser.getCreatedBy().isEmpty()) {
                    creatorText.setText("Created By: " + partnerUser.getCreatedBy());
                    creatorText.setVisibility(View.VISIBLE);
                } else {
                    creatorText.setVisibility(View.GONE);
                }
            }
        }

        return convertView;
    }
} 