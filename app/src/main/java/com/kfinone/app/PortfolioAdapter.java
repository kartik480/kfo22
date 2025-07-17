package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {

    private List<MyPortfolioActivity.PortfolioItem> portfolioList;

    public PortfolioAdapter(List<MyPortfolioActivity.PortfolioItem> portfolioList) {
        this.portfolioList = portfolioList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_portfolio_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyPortfolioActivity.PortfolioItem portfolio = portfolioList.get(position);
        
        holder.customerNameText.setText(portfolio.getCustomerName());
        holder.companyNameText.setText(portfolio.getCompanyName());
        holder.mobileText.setText(portfolio.getMobile());
        holder.stateText.setText(portfolio.getState());
        holder.locationText.setText(portfolio.getLocation());
        holder.createdByText.setText(portfolio.getCreatedBy());

        // Action button click listeners
        holder.viewButton.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "View portfolio: " + portfolio.getCustomerName(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to portfolio details
        });

        holder.editButton.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Edit portfolio: " + portfolio.getCustomerName(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to edit portfolio
        });

        holder.deleteButton.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Delete portfolio: " + portfolio.getCustomerName(), Toast.LENGTH_SHORT).show();
            // TODO: Show delete confirmation dialog
        });

        // Item click listener
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Selected: " + portfolio.getCustomerName(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to portfolio details
        });
    }

    @Override
    public int getItemCount() {
        return portfolioList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameText, companyNameText, mobileText, stateText, locationText, createdByText;
        ImageView viewButton, editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameText = itemView.findViewById(R.id.customerNameText);
            companyNameText = itemView.findViewById(R.id.companyNameText);
            mobileText = itemView.findViewById(R.id.mobileText);
            stateText = itemView.findViewById(R.id.stateText);
            locationText = itemView.findViewById(R.id.locationText);
            createdByText = itemView.findViewById(R.id.createdByText);
            viewButton = itemView.findViewById(R.id.viewButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 