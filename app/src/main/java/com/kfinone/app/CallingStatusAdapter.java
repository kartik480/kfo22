package com.kfinone.app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CallingStatusAdapter extends RecyclerView.Adapter<CallingStatusAdapter.ViewHolder> {
    private static final String TAG = "CallingStatusAdapter";
    private List<CallingStatusItem> callingStatuses;

    public CallingStatusAdapter(List<CallingStatusItem> callingStatuses) {
        this.callingStatuses = callingStatuses;
        Log.d(TAG, "Adapter created with " + callingStatuses.size() + " items");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating new ViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calling_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallingStatusItem item = callingStatuses.get(position);
        Log.d(TAG, "Binding item at position " + position + ": " + item.getCallingStatus());
        holder.callingStatusText.setText(item.getCallingStatus());
        holder.statusText.setText("Active");
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount called, returning: " + callingStatuses.size());
        return callingStatuses.size();
    }

    public void addCallingStatus(CallingStatusItem callingStatus) {
        Log.d(TAG, "Adding calling status: " + callingStatus.getCallingStatus());
        callingStatuses.add(callingStatus);
        notifyItemInserted(callingStatuses.size() - 1);
    }

    public void updateCallingStatuses(List<CallingStatusItem> newCallingStatuses) {
        Log.d(TAG, "Updating calling statuses. Old count: " + callingStatuses.size() + ", New count: " + newCallingStatuses.size());
        callingStatuses.clear();
        callingStatuses.addAll(newCallingStatuses);
        notifyDataSetChanged();
        Log.d(TAG, "Adapter updated, new item count: " + callingStatuses.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView callingStatusText;
        TextView statusText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            callingStatusText = itemView.findViewById(R.id.callingStatusText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
} 
