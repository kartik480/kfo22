package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VendorBanksAdapter extends RecyclerView.Adapter<VendorBanksAdapter.VendorBankViewHolder> {
    private List<VendorBankItem> vendorBanksList;

    public VendorBanksAdapter(List<VendorBankItem> vendorBanksList) {
        this.vendorBanksList = vendorBanksList;
    }

    @NonNull
    @Override
    public VendorBankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vendor_bank, parent, false);
        return new VendorBankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorBankViewHolder holder, int position) {
        VendorBankItem vendorBank = vendorBanksList.get(position);
        holder.nameText.setText(vendorBank.getName());
        holder.statusText.setText(vendorBank.getStatus());
    }

    @Override
    public int getItemCount() {
        return vendorBanksList.size();
    }

    static class VendorBankViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView statusText;
        TextView actionsText;

        VendorBankViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.vendorBankNameText);
            statusText = itemView.findViewById(R.id.vendorBankStatusText);
            actionsText = itemView.findViewById(R.id.vendorBankActionsText);
        }
    }
} 
