package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.util.List;

public class PartnerUserAdapter extends RecyclerView.Adapter<PartnerUserAdapter.PartnerUserViewHolder> {

    private Context context;
    private List<PartnerUser> partnerUserList;
    private OnPartnerUserActionListener actionListener;

    public interface OnPartnerUserActionListener {
        void onEditPartnerUser(PartnerUser partnerUser);
        void onDeletePartnerUser(PartnerUser partnerUser);
        void onViewDetails(PartnerUser partnerUser);
    }

    public PartnerUserAdapter(Context context, List<PartnerUser> partnerUserList) {
        this.context = context;
        this.partnerUserList = partnerUserList;
    }

    public void setActionListener(OnPartnerUserActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public PartnerUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.partner_user_card_item, parent, false);
        return new PartnerUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartnerUserViewHolder holder, int position) {
        PartnerUser partnerUser = partnerUserList.get(position);
        holder.bind(partnerUser);
    }

    @Override
    public int getItemCount() {
        return partnerUserList.size();
    }

    public void updateData(List<PartnerUser> newData) {
        this.partnerUserList.clear();
        this.partnerUserList.addAll(newData);
        notifyDataSetChanged();
    }

    public class PartnerUserViewHolder extends RecyclerView.ViewHolder {
        private TextView partnerName;
        private TextView partnerUsername;
        private TextView partnerPhone;
        private TextView partnerEmail;
        private TextView partnerCompany;
        private TextView partnerDepartment;
        private TextView partnerDesignation;
        private TextView partnerEmployeeNo;
        private TextView partnerBranchState;
        private TextView partnerBranchLocation;
        private TextView partnerBankName;
        private TextView partnerAccountType;
        private TextView partnerStatus;
        private TextView partnerCreatedAt;
        private TextView partnerCreatorName;
        private TextView partnerCreatorDesignation;
        private TextView partnerIdentityInfo;
        private Chip statusChip;
        private MaterialButton viewDetailsButton;
        private MaterialButton editButton;
        private MaterialButton deleteButton;

        public PartnerUserViewHolder(@NonNull View itemView) {
            super(itemView);
            
            partnerName = itemView.findViewById(R.id.partnerName);
            partnerUsername = itemView.findViewById(R.id.partnerUsername);
            partnerPhone = itemView.findViewById(R.id.partnerPhone);
            partnerEmail = itemView.findViewById(R.id.partnerEmail);
            partnerCompany = itemView.findViewById(R.id.partnerCompany);
            partnerDepartment = itemView.findViewById(R.id.partnerDepartment);
            partnerDesignation = itemView.findViewById(R.id.partnerDesignation);
            partnerEmployeeNo = itemView.findViewById(R.id.partnerEmployeeNo);
            partnerBranchState = itemView.findViewById(R.id.partnerBranchState);
            partnerBranchLocation = itemView.findViewById(R.id.partnerBranchLocation);
            partnerBankName = itemView.findViewById(R.id.partnerBankName);
            partnerAccountType = itemView.findViewById(R.id.partnerAccountType);
            partnerStatus = itemView.findViewById(R.id.partnerStatus);
            partnerCreatedAt = itemView.findViewById(R.id.partnerCreatedAt);
            partnerCreatorName = itemView.findViewById(R.id.partnerCreatorName);
            partnerCreatorDesignation = itemView.findViewById(R.id.partnerCreatorDesignation);
            partnerIdentityInfo = itemView.findViewById(R.id.partnerIdentityInfo);
            statusChip = itemView.findViewById(R.id.statusChip);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(PartnerUser partnerUser) {
            // Set comprehensive information
            partnerName.setText(partnerUser.getFullName());
            
            // Basic Information
            partnerUsername.setText("Username: " + partnerUser.getUsername());
            
            // Contact Information
            partnerPhone.setText("Phone: " + partnerUser.getPhoneNumber());
            
            partnerEmail.setText("Email: " + partnerUser.getEmailId());
            
            // Company Information
            String companyInfo = "Company: " + partnerUser.getCompanyName();
            if (!partnerUser.getEmployeeNo().isEmpty()) {
                companyInfo += " | Employee No: " + partnerUser.getEmployeeNo();
            }
            partnerCompany.setText(companyInfo);
            
            partnerDepartment.setText("Department: " + partnerUser.getDepartment());
            partnerDesignation.setText("Designation: " + partnerUser.getDesignation());
            
            // Location Information (simplified)
            partnerBranchState.setText("Branch State: N/A");
            
            // Banking Information (simplified)
            partnerBankName.setText("Bank: N/A");
            
            // Status and Timestamps
            String statusInfo = "Status: " + partnerUser.getStatus();
            if (!partnerUser.getRank().isEmpty()) {
                statusInfo += " | Rank: " + partnerUser.getRank();
            }
            partnerStatus.setText(statusInfo);
            
            String createdInfo = "Created: " + partnerUser.getCreatedAt();
            if (!partnerUser.getCreatedBy().isEmpty()) {
                createdInfo += " | By: " + partnerUser.getCreatedBy();
            }
            partnerCreatedAt.setText(createdInfo);
            
            // Creator Information
            String creatorInfo = "Created By: " + partnerUser.getCreatorName();
            if (!partnerUser.getCreatorDesignationName().isEmpty()) {
                creatorInfo += " (" + partnerUser.getCreatorDesignationName() + ")";
            }
            partnerCreatorName.setText(creatorInfo);
            
            // Identity Information (simplified)
            partnerIdentityInfo.setText("Identity: N/A");
            
            // Additional Information (using existing fields)
            String additionalInfo = "";
            if (!partnerUser.getReportingTo().isEmpty()) {
                additionalInfo += "Reports To: " + partnerUser.getReportingTo() + " | ";
            }
            if (!partnerUser.getPartnerTypeId().isEmpty()) {
                additionalInfo += "Partner Type ID: " + partnerUser.getPartnerTypeId();
            }
            
            if (!additionalInfo.isEmpty()) {
                partnerCreatorDesignation.setText(additionalInfo);
            } else {
                partnerCreatorDesignation.setText("Creator Designation: " + partnerUser.getCreatorDesignationName());
            }

            // Set status chip
            if ("1".equals(partnerUser.getStatus()) || "Active".equalsIgnoreCase(partnerUser.getStatus())) {
                statusChip.setText("Active");
                statusChip.setChipBackgroundColorResource(R.color.chip_background_color);
            } else {
                statusChip.setText("Inactive");
                statusChip.setChipBackgroundColorResource(android.R.color.holo_red_light);
            }

            // Set up action buttons
            viewDetailsButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onViewDetails(partnerUser);
                }
            });

            editButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditPartnerUser(partnerUser);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeletePartnerUser(partnerUser);
                }
            });
        }
    }
} 