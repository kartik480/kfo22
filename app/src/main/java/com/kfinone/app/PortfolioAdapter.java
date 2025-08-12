package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {
    
    private List<Portfolio> portfolios;
    private Context context;
    private OnPortfolioActionListener actionListener;
    
    public interface OnPortfolioActionListener {
        void onViewPortfolio(Portfolio portfolio);
        void onEditPortfolio(Portfolio portfolio);
        void onDeletePortfolio(Portfolio portfolio);
    }
    
    public PortfolioAdapter(List<Portfolio> portfolios, Context context, OnPortfolioActionListener actionListener) {
        this.portfolios = portfolios;
        this.context = context;
        this.actionListener = actionListener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_portfolio, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Portfolio portfolio = portfolios.get(position);
        
        // Set customer name
        holder.customerNameText.setText(portfolio.getCustomerName() != null ? portfolio.getCustomerName() : "N/A");
        
        // Set company name
        holder.companyNameText.setText(portfolio.getCompanyName() != null ? portfolio.getCompanyName() : "N/A");
        
        // Set mobile
        holder.mobileText.setText(portfolio.getMobile() != null ? portfolio.getMobile() : "N/A");
        
        // Set state
        holder.stateText.setText(portfolio.getState() != null ? portfolio.getState() : "N/A");
        
        // Set location
        holder.locationText.setText(portfolio.getLocation() != null ? portfolio.getLocation() : "N/A");
        
        // Set created by
        holder.createdByText.setText(portfolio.getCreatedBy() != null ? portfolio.getCreatedBy() : "N/A");
        
        // Set status with color coding
        String status = portfolio.getStatus() != null ? portfolio.getStatus() : "Unknown";
        holder.statusText.setText(status);
        
        // Set status color based on status value
        if ("Active".equalsIgnoreCase(status)) {
            holder.statusText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if ("Inactive".equalsIgnoreCase(status)) {
            holder.statusText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.statusText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
        
        // Set click listeners for action buttons
        holder.viewButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onViewPortfolio(portfolio);
            }
        });
        
        holder.editButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditPortfolio(portfolio);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeletePortfolio(portfolio);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return portfolios.size();
    }
    
    public void updateData(List<Portfolio> newPortfolios) {
        this.portfolios = newPortfolios;
        notifyDataSetChanged();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameText, companyNameText, mobileText, stateText, locationText, createdByText, statusText;
        Button viewButton, editButton, deleteButton;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameText = itemView.findViewById(R.id.customerNameText);
            companyNameText = itemView.findViewById(R.id.companyNameText);
            mobileText = itemView.findViewById(R.id.mobileText);
            stateText = itemView.findViewById(R.id.stateText);
            locationText = itemView.findViewById(R.id.locationText);
            createdByText = itemView.findViewById(R.id.createdByText);
            statusText = itemView.findViewById(R.id.statusText);
            viewButton = itemView.findViewById(R.id.viewButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 