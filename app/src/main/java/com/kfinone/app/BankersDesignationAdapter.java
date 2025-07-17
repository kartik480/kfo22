package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BankersDesignationAdapter extends RecyclerView.Adapter<BankersDesignationAdapter.DesignationViewHolder> {
    private List<BankersDesignationItem> designationsList;

    public BankersDesignationAdapter(List<BankersDesignationItem> designationsList) {
        this.designationsList = designationsList;
    }

    @NonNull
    @Override
    public DesignationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bankers_designation, parent, false);
        return new DesignationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DesignationViewHolder holder, int position) {
        BankersDesignationItem designation = designationsList.get(position);
        holder.nameText.setText(designation.getName());
        holder.statusText.setText(designation.getStatus());
    }

    @Override
    public int getItemCount() {
        return designationsList.size();
    }

    static class DesignationViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView statusText;
        TextView actionsText;

        DesignationViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.designationNameText);
            statusText = itemView.findViewById(R.id.designationStatusText);
            actionsText = itemView.findViewById(R.id.designationActionsText);
        }
    }
} 
