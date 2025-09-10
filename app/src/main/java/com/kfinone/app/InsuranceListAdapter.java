package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InsuranceListAdapter extends RecyclerView.Adapter<InsuranceListAdapter.InsuranceViewHolder> {

    private List<InsuranceItem> insuranceList;
    private OnInsuranceActionClickListener clickListener;

    public interface OnInsuranceActionClickListener {
        void onViewClick(InsuranceItem insuranceItem);
        void onEditClick(InsuranceItem insuranceItem);
    }

    public InsuranceListAdapter(List<InsuranceItem> insuranceList, OnInsuranceActionClickListener clickListener) {
        this.insuranceList = insuranceList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public InsuranceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_insurance_list, parent, false);
        return new InsuranceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsuranceViewHolder holder, int position) {
        InsuranceItem insuranceItem = insuranceList.get(position);
        holder.bind(insuranceItem);
    }

    @Override
    public int getItemCount() {
        return insuranceList.size();
    }

    class InsuranceViewHolder extends RecyclerView.ViewHolder {
        private TextView customerNameText;
        private TextView companyNameText;
        private TextView mobileText;
        private TextView stateText;
        private TextView locationText;
        private TextView viewButton;
        private TextView editButton;

        public InsuranceViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameText = itemView.findViewById(R.id.customerNameText);
            companyNameText = itemView.findViewById(R.id.companyNameText);
            mobileText = itemView.findViewById(R.id.mobileText);
            stateText = itemView.findViewById(R.id.stateText);
            locationText = itemView.findViewById(R.id.locationText);
            viewButton = itemView.findViewById(R.id.viewButton);
            editButton = itemView.findViewById(R.id.editButton);

            // Set click listeners for action buttons
            viewButton.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onViewClick(insuranceList.get(position));
                    }
                }
            });

            editButton.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onEditClick(insuranceList.get(position));
                    }
                }
            });

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onViewClick(insuranceList.get(position));
                    }
                }
            });
        }

        public void bind(InsuranceItem insuranceItem) {
            customerNameText.setText(insuranceItem.getDisplayName());
            companyNameText.setText(insuranceItem.getCompanyName() != null ? insuranceItem.getCompanyName() : "N/A");
            mobileText.setText(insuranceItem.getFormattedMobile());
            stateText.setText(insuranceItem.getState() != null ? insuranceItem.getState() : "N/A");
            locationText.setText(insuranceItem.getLocation() != null ? insuranceItem.getLocation() : "N/A");
        }
    }
}
