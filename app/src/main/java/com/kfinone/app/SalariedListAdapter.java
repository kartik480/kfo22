package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SalariedListAdapter extends RecyclerView.Adapter<SalariedListAdapter.SalariedViewHolder> {

    private List<SalariedItem> salariedList;
    private OnSalariedActionClickListener listener;

    public interface OnSalariedActionClickListener {
        void onViewClick(SalariedItem salariedItem);
        void onEditClick(SalariedItem salariedItem);
    }

    public SalariedListAdapter(List<SalariedItem> salariedList, OnSalariedActionClickListener listener) {
        this.salariedList = salariedList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SalariedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salaried_list, parent, false);
        return new SalariedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalariedViewHolder holder, int position) {
        SalariedItem salariedItem = salariedList.get(position);
        
        holder.mobileNumberText.setText(salariedItem.getMobileNumber());
        holder.leadNameText.setText(salariedItem.getLeadName());
        holder.emailIdText.setText(salariedItem.getEmailId());
        holder.createdByText.setText(salariedItem.getCreatedBy());

        // Set up click listeners for action buttons
        holder.viewButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewClick(salariedItem);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(salariedItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return salariedList.size();
    }

    public void updateSalariedList(List<SalariedItem> newSalariedList) {
        this.salariedList = newSalariedList;
        notifyDataSetChanged();
    }

    public static class SalariedViewHolder extends RecyclerView.ViewHolder {
        private TextView mobileNumberText;
        private TextView leadNameText;
        private TextView emailIdText;
        private TextView createdByText;
        private TextView viewButton;
        private TextView editButton;

        public SalariedViewHolder(@NonNull View itemView) {
            super(itemView);
            mobileNumberText = itemView.findViewById(R.id.mobileNumberText);
            leadNameText = itemView.findViewById(R.id.leadNameText);
            emailIdText = itemView.findViewById(R.id.emailIdText);
            createdByText = itemView.findViewById(R.id.createdByText);
            viewButton = itemView.findViewById(R.id.viewButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}
