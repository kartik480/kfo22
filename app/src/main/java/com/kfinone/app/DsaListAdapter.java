package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DsaListAdapter extends RecyclerView.Adapter<DsaListAdapter.ViewHolder> {
    private List<DsaItem> dsaList;

    public DsaListAdapter(List<DsaItem> dsaList) {
        this.dsaList = dsaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dsa_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DsaItem item = dsaList.get(position);
        holder.tvVendorBank.setText(item.getVendorBank());
        holder.tvDsaCode.setText(item.getDsaCode());
        holder.tvDsaName.setText(item.getDsaName());
        holder.tvLoanType.setText(item.getLoanType());
        holder.tvState.setText(item.getState());
        holder.tvLocation.setText(item.getLocation());
    }

    @Override
    public int getItemCount() {
        return dsaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorBank, tvDsaCode, tvDsaName, tvLoanType, tvState, tvLocation;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendorBank = itemView.findViewById(R.id.tvVendorBank);
            tvDsaCode = itemView.findViewById(R.id.tvDsaCode);
            tvDsaName = itemView.findViewById(R.id.tvDsaName);
            tvLoanType = itemView.findViewById(R.id.tvLoanType);
            tvState = itemView.findViewById(R.id.tvState);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
} 