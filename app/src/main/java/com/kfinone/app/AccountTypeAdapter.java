package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AccountTypeAdapter extends RecyclerView.Adapter<AccountTypeAdapter.AccountTypeViewHolder> {
    private List<AccountTypeItem> accountTypesList;

    public AccountTypeAdapter(List<AccountTypeItem> accountTypesList) {
        this.accountTypesList = accountTypesList;
    }

    @NonNull
    @Override
    public AccountTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account_type, parent, false);
        return new AccountTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountTypeViewHolder holder, int position) {
        AccountTypeItem accountType = accountTypesList.get(position);
        holder.nameText.setText(accountType.getName());
        holder.statusText.setText(accountType.getStatus());
    }

    @Override
    public int getItemCount() {
        return accountTypesList.size();
    }

    static class AccountTypeViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView statusText;
        TextView actionsText;

        AccountTypeViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.accountTypeNameText);
            statusText = itemView.findViewById(R.id.accountTypeStatusText);
            actionsText = itemView.findViewById(R.id.accountTypeActionsText);
        }
    }
} 
