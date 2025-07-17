package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InsuranceAdapter extends RecyclerView.Adapter<InsuranceAdapter.InsuranceViewHolder> {

    private List<MyInsuranceActivity.InsurancePolicy> insuranceList;
    private Context context;

    public InsuranceAdapter(List<MyInsuranceActivity.InsurancePolicy> insuranceList, Context context) {
        this.insuranceList = insuranceList;
        this.context = context;
    }

    @NonNull
    @Override
    public InsuranceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_insurance, parent, false);
        return new InsuranceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsuranceViewHolder holder, int position) {
        MyInsuranceActivity.InsurancePolicy policy = insuranceList.get(position);
        holder.bind(policy);
    }

    @Override
    public int getItemCount() {
        return insuranceList.size();
    }

    public class InsuranceViewHolder extends RecyclerView.ViewHolder {
        private TextView customerNameText, policyNumberText, insuranceTypeText;
        private TextView premiumAmountText, coverageAmountText, expiryDateText, statusChip;
        private Button viewButton, editButton, renewButton;

        public InsuranceViewHolder(@NonNull View itemView) {
            super(itemView);
            
            customerNameText = itemView.findViewById(R.id.customerNameText);
            policyNumberText = itemView.findViewById(R.id.policyNumberText);
            insuranceTypeText = itemView.findViewById(R.id.insuranceTypeText);
            premiumAmountText = itemView.findViewById(R.id.premiumAmountText);
            coverageAmountText = itemView.findViewById(R.id.coverageAmountText);
            expiryDateText = itemView.findViewById(R.id.expiryDateText);
            statusChip = itemView.findViewById(R.id.statusChip);
            viewButton = itemView.findViewById(R.id.viewButton);
            editButton = itemView.findViewById(R.id.editButton);
            renewButton = itemView.findViewById(R.id.renewButton);
        }

        public void bind(MyInsuranceActivity.InsurancePolicy policy) {
            customerNameText.setText(policy.getCustomerName());
            policyNumberText.setText("Policy: " + policy.getPolicyNumber());
            insuranceTypeText.setText(policy.getInsuranceType());
            premiumAmountText.setText(policy.getPremiumAmount());
            coverageAmountText.setText(policy.getCoverageAmount());
            expiryDateText.setText(policy.getExpiryDate());
            statusChip.setText(policy.getStatus());

            // Set status chip background color based on status
            switch (policy.getStatus()) {
                case "Active":
                    statusChip.setBackgroundResource(R.drawable.status_background);
                    break;
                case "Expired":
                    statusChip.setBackgroundResource(R.drawable.status_expired_background);
                    break;
                case "Pending":
                    statusChip.setBackgroundResource(R.drawable.status_pending_background);
                    break;
                default:
                    statusChip.setBackgroundResource(R.drawable.status_background);
                    break;
            }

            // Setup button click listeners
            viewButton.setOnClickListener(v -> viewPolicy(policy));
            editButton.setOnClickListener(v -> editPolicy(policy));
            renewButton.setOnClickListener(v -> renewPolicy(policy));

            // Setup item click listener
            itemView.setOnClickListener(v -> viewPolicy(policy));
        }

        private void viewPolicy(MyInsuranceActivity.InsurancePolicy policy) {
            Toast.makeText(context, "Viewing policy: " + policy.getPolicyNumber(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to policy details activity
        }

        private void editPolicy(MyInsuranceActivity.InsurancePolicy policy) {
            Toast.makeText(context, "Editing policy: " + policy.getPolicyNumber(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to edit policy activity
        }

        private void renewPolicy(MyInsuranceActivity.InsurancePolicy policy) {
            Toast.makeText(context, "Renewing policy: " + policy.getPolicyNumber(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to renew policy activity
        }
    }
} 