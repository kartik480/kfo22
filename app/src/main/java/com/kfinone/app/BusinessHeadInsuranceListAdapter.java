package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BusinessHeadInsuranceListAdapter extends RecyclerView.Adapter<BusinessHeadInsuranceListAdapter.BusinessHeadInsuranceViewHolder> {

    private List<BusinessHeadInsuranceItem> insuranceList;
    private OnInsuranceActionClickListener listener;

    public interface OnInsuranceActionClickListener {
        void onViewClick(BusinessHeadInsuranceItem insuranceItem);
        void onEditClick(BusinessHeadInsuranceItem insuranceItem);
    }

    public BusinessHeadInsuranceListAdapter(List<BusinessHeadInsuranceItem> insuranceList, OnInsuranceActionClickListener listener) {
        this.insuranceList = insuranceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BusinessHeadInsuranceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business_head_insurance_list, parent, false);
        return new BusinessHeadInsuranceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessHeadInsuranceViewHolder holder, int position) {
        BusinessHeadInsuranceItem insuranceItem = insuranceList.get(position);
        
        holder.customerNameText.setText(insuranceItem.getCustomerName());
        holder.companyNameText.setText(insuranceItem.getCompanyName());
        holder.mobileText.setText(insuranceItem.getMobile());
        holder.stateText.setText(insuranceItem.getState());
        holder.locationText.setText(insuranceItem.getLocation());

        // Set up click listeners for action buttons
        holder.viewButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewClick(insuranceItem);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(insuranceItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return insuranceList.size();
    }

    public void updateInsuranceList(List<BusinessHeadInsuranceItem> newInsuranceList) {
        this.insuranceList = newInsuranceList;
        notifyDataSetChanged();
    }

    public static class BusinessHeadInsuranceViewHolder extends RecyclerView.ViewHolder {
        private TextView customerNameText;
        private TextView companyNameText;
        private TextView mobileText;
        private TextView stateText;
        private TextView locationText;
        private TextView viewButton;
        private TextView editButton;

        public BusinessHeadInsuranceViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameText = itemView.findViewById(R.id.customerNameText);
            companyNameText = itemView.findViewById(R.id.companyNameText);
            mobileText = itemView.findViewById(R.id.mobileText);
            stateText = itemView.findViewById(R.id.stateText);
            locationText = itemView.findViewById(R.id.locationText);
            viewButton = itemView.findViewById(R.id.viewButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}
