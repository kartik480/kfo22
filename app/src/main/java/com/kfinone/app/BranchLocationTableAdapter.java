package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BranchLocationTableAdapter extends RecyclerView.Adapter<BranchLocationTableAdapter.ViewHolder> {
    private List<BranchLocationItem> branchLocationList;

    public BranchLocationTableAdapter(List<BranchLocationItem> branchLocationList) {
        this.branchLocationList = branchLocationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_branch_location_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BranchLocationItem item = branchLocationList.get(position);
        
        // Set alternating background colors for better table visualization
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.drawable.table_row_background);
        } else {
            holder.itemView.setBackgroundResource(android.R.color.white);
        }

        holder.tvId.setText(String.valueOf(item.getId()));
        holder.tvLocation.setText(item.getBranchLocation());
        holder.tvState.setText(String.valueOf(item.getBranchStateId()));
        holder.tvCreatedDate.setText(item.getCreatedAt());
        holder.tvStatus.setText(item.getStatus());
    }

    @Override
    public int getItemCount() {
        return branchLocationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvLocation, tvState, tvCreatedDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvState = itemView.findViewById(R.id.tvState);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
} 
