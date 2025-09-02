package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MDPortfolioAdapter extends RecyclerView.Adapter<MDPortfolioAdapter.ViewHolder> {
    private List<MDPortfolio> portfolios;

    public MDPortfolioAdapter(List<MDPortfolio> portfolios) {
        this.portfolios = portfolios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_portfolio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MDPortfolio portfolio = portfolios.get(position);
        
        holder.customerNameText.setText(portfolio.getCustomerName() != null ? portfolio.getCustomerName() : "N/A");
        holder.companyNameText.setText(portfolio.getCompanyName() != null ? portfolio.getCompanyName() : "N/A");
        holder.mobileText.setText(portfolio.getPhoneNumber() != null ? portfolio.getPhoneNumber() : "N/A");
        holder.emailText.setText(portfolio.getEmailId() != null ? portfolio.getEmailId() : "N/A");
        holder.locationText.setText(portfolio.getLocation() != null ? portfolio.getLocation() : "N/A");
        holder.creatorText.setText(portfolio.getCreatorFullName() != null ? portfolio.getCreatorFullName() : "N/A");
        holder.designationText.setText(portfolio.getCreatorDesignationName() != null ? portfolio.getCreatorDesignationName() : "N/A");
        holder.statusText.setText(portfolio.getStatus() != null ? portfolio.getStatus() : "N/A");
    }

    @Override
    public int getItemCount() {
        return portfolios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameText;
        TextView companyNameText;
        TextView mobileText;
        TextView emailText;
        TextView locationText;
        TextView creatorText;
        TextView designationText;
        TextView statusText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameText = itemView.findViewById(R.id.customerNameText);
            companyNameText = itemView.findViewById(R.id.companyNameText);
            mobileText = itemView.findViewById(R.id.mobileText);
            emailText = itemView.findViewById(R.id.emailText);
            locationText = itemView.findViewById(R.id.locationText);
            creatorText = itemView.findViewById(R.id.creatorText);
            designationText = itemView.findViewById(R.id.designationText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
}
