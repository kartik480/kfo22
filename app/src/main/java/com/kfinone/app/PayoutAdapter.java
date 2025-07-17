package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PayoutAdapter extends RecyclerView.Adapter<PayoutAdapter.PayoutViewHolder> {
    private List<PayoutItem> payoutList;

    public PayoutAdapter(List<PayoutItem> payoutList) {
        this.payoutList = payoutList;
    }

    @NonNull
    @Override
    public PayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payout, parent, false);
        return new PayoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayoutViewHolder holder, int position) {
        PayoutItem item = payoutList.get(position);
        
        holder.payoutTypeText.setText(item.getPayoutTypeName() != null ? item.getPayoutTypeName() : "N/A");
        holder.vendorBankText.setText(item.getVendorBankName() != null ? item.getVendorBankName() : "N/A");
        holder.loanTypeText.setText(item.getLoanTypeName() != null ? item.getLoanTypeName() : "N/A");
        holder.categoryText.setText(item.getCategoryName() != null ? item.getCategoryName() : "N/A");
        holder.payoutText.setText(item.getPayout() != null ? "₹" + item.getPayout() : "N/A");
    }

    @Override
    public int getItemCount() {
        return payoutList.size();
    }

    public void updateData(List<PayoutItem> newPayoutList) {
        this.payoutList = newPayoutList;
        notifyDataSetChanged();
    }

    static class PayoutViewHolder extends RecyclerView.ViewHolder {
        TextView payoutTypeText, vendorBankText, loanTypeText, categoryText, payoutText;

        PayoutViewHolder(View itemView) {
            super(itemView);
            payoutTypeText = itemView.findViewById(R.id.payoutTypeText);
            vendorBankText = itemView.findViewById(R.id.vendorBankText);
            loanTypeText = itemView.findViewById(R.id.loanTypeText);
            categoryText = itemView.findViewById(R.id.categoryText);
            payoutText = itemView.findViewById(R.id.payoutText);
        }
    }
} 