package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BanksAdapter extends RecyclerView.Adapter<BanksAdapter.BankViewHolder> {
    private List<BankItem> banksList;

    public BanksAdapter(List<BankItem> banksList) {
        this.banksList = banksList;
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bank, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        BankItem bank = banksList.get(position);
        holder.nameText.setText(bank.getName());
        holder.statusText.setText(bank.getStatus());
    }

    @Override
    public int getItemCount() {
        return banksList.size();
    }

    static class BankViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView statusText;
        TextView actionsText;

        BankViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.bankNameText);
            statusText = itemView.findViewById(R.id.bankStatusText);
            actionsText = itemView.findViewById(R.id.bankActionsText);
        }
    }
} 
