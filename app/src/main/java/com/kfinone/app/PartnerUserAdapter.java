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
            statusChip = itemView.findViewById(R.id.statusChip);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(PartnerUser partnerUser) {
            // Set basic information
            partnerName.setText(partnerUser.getFullName());
            partnerUsername.setText("Username: " + partnerUser.getUsername());
            partnerPhone.setText("Phone: " + partnerUser.getPhoneNumber());
            partnerEmail.setText("Email: " + partnerUser.getEmailId());
            partnerCompany.setText("Company: " + partnerUser.getCompanyName());
            partnerDepartment.setText("Department: " + partnerUser.getDepartment());
            partnerDesignation.setText("Designation: " + partnerUser.getDesignation());
            partnerEmployeeNo.setText("Employee No: " + partnerUser.getEmployeeNo());
            partnerBranchState.setText("Branch State: " + partnerUser.getBranchState());
            partnerBranchLocation.setText("Branch Location: " + partnerUser.getBranchLocation());
            partnerBankName.setText("Bank: " + partnerUser.getBankName());
            partnerAccountType.setText("Account Type: " + partnerUser.getAccountType());
            partnerStatus.setText("Status: " + partnerUser.getStatus());
            partnerCreatedAt.setText("Created: " + partnerUser.getCreatedAt());
            partnerCreatorName.setText("Created By: " + partnerUser.getCreatorName());

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