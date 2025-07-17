package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PartnerTypeAdapter extends RecyclerView.Adapter<PartnerTypeAdapter.ViewHolder> {

    private List<PartnerTypeItem> partnerTypeList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(PartnerTypeItem item);
        void onDeleteClick(PartnerTypeItem item);
    }

    public PartnerTypeAdapter(List<PartnerTypeItem> partnerTypeList, OnItemClickListener listener) {
        this.partnerTypeList = partnerTypeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_partner_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PartnerTypeItem item = partnerTypeList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.statusTextView.setText(item.getStatus());

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(item);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return partnerTypeList.size();
    }

    public void updateData(List<PartnerTypeItem> newList) {
        this.partnerTypeList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView statusTextView;
        ImageButton editButton;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 
