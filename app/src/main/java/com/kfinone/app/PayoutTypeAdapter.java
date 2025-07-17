package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PayoutTypeAdapter extends RecyclerView.Adapter<PayoutTypeAdapter.ViewHolder> {
    private List<PayoutTypeItem> payoutTypeList;

    public PayoutTypeAdapter(List<PayoutTypeItem> payoutTypeList) {
        this.payoutTypeList = payoutTypeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payout_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PayoutTypeItem item = payoutTypeList.get(position);
        holder.payoutNameText.setText(item.getPayoutName());
        holder.statusText.setText(item.getStatus());
        holder.actionsText.setText("Edit | Delete");
    }

    @Override
    public int getItemCount() {
        return payoutTypeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView payoutNameText, statusText, actionsText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            payoutNameText = itemView.findViewById(R.id.payoutNameText);
            statusText = itemView.findViewById(R.id.statusText);
            actionsText = itemView.findViewById(R.id.actionsText);
        }
    }
} 
