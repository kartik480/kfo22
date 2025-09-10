package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PayoutBoxAdapter extends RecyclerView.Adapter<PayoutBoxAdapter.PayoutBoxViewHolder> {

    private List<PayoutBox> payoutList;
    private OnPayoutBoxClickListener clickListener;

    public interface OnPayoutBoxClickListener {
        void onPayoutBoxClick(PayoutBox payoutBox);
    }

    public PayoutBoxAdapter(List<PayoutBox> payoutList, OnPayoutBoxClickListener clickListener) {
        this.payoutList = payoutList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public PayoutBoxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payout_box, parent, false);
        return new PayoutBoxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayoutBoxViewHolder holder, int position) {
        PayoutBox payoutBox = payoutList.get(position);
        holder.bind(payoutBox);
    }

    @Override
    public int getItemCount() {
        return payoutList.size();
    }

    class PayoutBoxViewHolder extends RecyclerView.ViewHolder {
        private TextView payoutNameText;
        private TextView payoutTypeIdText;
        private TextView payoutAmountText;
        private TextView payoutStatusText;

        public PayoutBoxViewHolder(@NonNull View itemView) {
            super(itemView);
            payoutNameText = itemView.findViewById(R.id.payoutNameText);
            payoutTypeIdText = itemView.findViewById(R.id.payoutTypeIdText);
            payoutAmountText = itemView.findViewById(R.id.payoutAmountText);
            payoutStatusText = itemView.findViewById(R.id.payoutStatusText);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onPayoutBoxClick(payoutList.get(position));
                    }
                }
            });
        }

        public void bind(PayoutBox payoutBox) {
            payoutNameText.setText(payoutBox.getDisplayName());
            payoutTypeIdText.setText("Type ID: " + payoutBox.getPayoutTypeId());
            payoutAmountText.setText(payoutBox.getFormattedPayout());
            
            // Set status with appropriate styling
            String status = payoutBox.getStatus();
            if (status == null || status.isEmpty()) {
                status = "Unknown";
            }
            payoutStatusText.setText(status);
            
            // Set status color based on status
            if ("Active".equalsIgnoreCase(status)) {
                payoutStatusText.setBackgroundResource(R.drawable.status_background_active);
                payoutStatusText.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else if ("Inactive".equalsIgnoreCase(status)) {
                payoutStatusText.setBackgroundResource(R.drawable.status_background_inactive);
                payoutStatusText.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            } else {
                payoutStatusText.setBackgroundResource(R.drawable.status_background_pending);
                payoutStatusText.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
            }
        }
    }
}
