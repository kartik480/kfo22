package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RBHPortfolioAdapter extends RecyclerView.Adapter<RBHPortfolioAdapter.ViewHolder> {
    private List<RBHPortfolio> portfolios;
    private OnPortfolioActionListener actionListener;

    public interface OnPortfolioActionListener {
        void onViewPortfolio(RBHPortfolio portfolio);
        void onEditPortfolio(RBHPortfolio portfolio);
    }

    public RBHPortfolioAdapter(List<RBHPortfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public void setActionListener(OnPortfolioActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rbh_portfolio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RBHPortfolio portfolio = portfolios.get(position);
        
        holder.customerNameText.setText(portfolio.getCustomerName() != null ? portfolio.getCustomerName() : "N/A");
        holder.companyNameText.setText(portfolio.getCompanyName() != null ? portfolio.getCompanyName() : "N/A");
        holder.mobileText.setText(portfolio.getPhoneNumber() != null ? portfolio.getPhoneNumber() : "N/A");
        holder.stateText.setText(portfolio.getState() != null ? portfolio.getState() : "N/A");
        holder.locationText.setText(portfolio.getLocation() != null ? portfolio.getLocation() : "N/A");
        holder.createdByText.setText(portfolio.getCreatedBy() != null ? portfolio.getCreatedBy() : "N/A");

        // Set click listeners for action buttons
        holder.viewActionButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onViewPortfolio(portfolio);
            }
        });

        holder.editActionButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditPortfolio(portfolio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return portfolios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameText;
        TextView companyNameText;
        TextView mobileText;
        TextView stateText;
        TextView locationText;
        TextView createdByText;
        TextView viewActionButton;
        TextView editActionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameText = itemView.findViewById(R.id.customerNameText);
            companyNameText = itemView.findViewById(R.id.companyNameText);
            mobileText = itemView.findViewById(R.id.mobileText);
            stateText = itemView.findViewById(R.id.stateText);
            locationText = itemView.findViewById(R.id.locationText);
            createdByText = itemView.findViewById(R.id.createdByText);
            viewActionButton = itemView.findViewById(R.id.viewActionButton);
            editActionButton = itemView.findViewById(R.id.editActionButton);
        }
    }
}
