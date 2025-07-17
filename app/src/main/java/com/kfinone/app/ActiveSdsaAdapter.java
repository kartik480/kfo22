package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ActiveSdsaAdapter extends RecyclerView.Adapter<ActiveSdsaAdapter.ViewHolder> {

    private List<ActiveSdsaItem> sdsaList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(ActiveSdsaItem item);
        void onDeleteClick(ActiveSdsaItem item);
    }

    public ActiveSdsaAdapter(List<ActiveSdsaItem> sdsaList, OnItemClickListener listener) {
        this.sdsaList = sdsaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_sdsa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActiveSdsaItem item = sdsaList.get(position);
        
        // Set the main name (first name + last name)
        holder.nameTextView.setText(item.getFullName());
        
        // Set alias name
        holder.aliasNameTextView.setText("Alias: " + (item.getAliasName() != null ? item.getAliasName() : "N/A"));
        
        // Set phone number
        holder.phoneTextView.setText("Phone: " + (item.getPhoneNumber() != null ? item.getPhoneNumber() : "N/A"));
        
        // Set email
        holder.emailTextView.setText("Email: " + (item.getEmailId() != null ? item.getEmailId() : "N/A"));
        
        // Set company name
        holder.companyTextView.setText("Company: " + (item.getCompanyName() != null ? item.getCompanyName() : "N/A"));
        
        // Set employee number
        holder.employeeNoTextView.setText("Employee No: " + (item.getEmployeeNo() != null ? item.getEmployeeNo() : "N/A"));
        
        // Set department
        holder.departmentTextView.setText("Department: " + (item.getDepartment() != null ? item.getDepartment() : "N/A"));
        
        // Set designation
        holder.designationTextView.setText("Designation: " + (item.getDesignation() != null ? item.getDesignation() : "N/A"));
        
        // Set status
        holder.statusTextView.setText("Status: " + (item.getStatus() != null ? item.getStatus() : "N/A"));

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(item);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sdsaList.size();
    }

    public void updateData(List<ActiveSdsaItem> newList) {
        this.sdsaList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView aliasNameTextView;
        TextView phoneTextView;
        TextView emailTextView;
        TextView companyTextView;
        TextView employeeNoTextView;
        TextView departmentTextView;
        TextView designationTextView;
        TextView statusTextView;
        ImageButton editButton;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            aliasNameTextView = itemView.findViewById(R.id.aliasNameTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            companyTextView = itemView.findViewById(R.id.companyTextView);
            employeeNoTextView = itemView.findViewById(R.id.employeeNoTextView);
            departmentTextView = itemView.findViewById(R.id.departmentTextView);
            designationTextView = itemView.findViewById(R.id.designationTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 
