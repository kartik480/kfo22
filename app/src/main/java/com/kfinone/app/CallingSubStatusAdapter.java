package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CallingSubStatusAdapter extends RecyclerView.Adapter<CallingSubStatusAdapter.ViewHolder> {

    private List<CallingSubStatusItem> subStatusList;

    public CallingSubStatusAdapter(List<CallingSubStatusItem> subStatusList) {
        this.subStatusList = subStatusList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calling_sub_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallingSubStatusItem item = subStatusList.get(position);
        holder.statusTextView.setText(item.getStatus());
        holder.callingStatusTextView.setText(item.getCallingStatus());
        holder.callingSubStatusTextView.setText(item.getCallingSubStatus());
    }

    @Override
    public int getItemCount() {
        return subStatusList.size();
    }

    public void updateData(List<CallingSubStatusItem> newSubStatusList) {
        this.subStatusList.clear();
        this.subStatusList.addAll(newSubStatusList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView statusTextView;
        public TextView callingStatusTextView;
        public TextView callingSubStatusTextView;

        public ViewHolder(View view) {
            super(view);
            statusTextView = view.findViewById(R.id.statusTextView);
            callingStatusTextView = view.findViewById(R.id.callingStatusTextView);
            callingSubStatusTextView = view.findViewById(R.id.callingSubStatusTextView);
        }
    }
} 
