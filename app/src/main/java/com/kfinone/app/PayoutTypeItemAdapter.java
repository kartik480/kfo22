package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PayoutTypeItemAdapter extends RecyclerView.Adapter<PayoutTypeItemAdapter.ViewHolder> {
    private List<PayoutTypeItem> payoutTypeList;

    public PayoutTypeItemAdapter(List<PayoutTypeItem> payoutTypeList) {
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
        holder.payoutTypeName.setText(item.getPayoutName());
        holder.payoutTypeId.setText("Status: " + item.getStatus());
    }

    @Override
    public int getItemCount() {
        return payoutTypeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView payoutTypeName, payoutTypeId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            payoutTypeName = itemView.findViewById(R.id.payoutTypeName);
            payoutTypeId = itemView.findViewById(R.id.payoutTypeId);
        }
    }
}
