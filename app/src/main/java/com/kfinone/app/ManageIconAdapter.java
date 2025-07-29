package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Removed Glide import - using simple image loading

import java.util.List;

public class ManageIconAdapter extends RecyclerView.Adapter<ManageIconAdapter.ViewHolder> {

    private Context context;
    private List<ManageIcon> manageIcons;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ManageIcon manageIcon);
    }

    public ManageIconAdapter(Context context, List<ManageIcon> manageIcons, OnItemClickListener listener) {
        this.context = context;
        this.manageIcons = manageIcons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_icon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManageIcon manageIcon = manageIcons.get(position);
        
        // Set icon name
        holder.iconName.setText(manageIcon.getIconName());
        
        // Set icon description
        holder.iconDescription.setText(manageIcon.getIconDescription());
        
        // Load icon image - using default icon for now
        // In a real app, you would implement proper image loading here
        holder.iconImage.setImageResource(R.drawable.ic_settings);
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(manageIcon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return manageIcons.size();
    }

    public void updateData(List<ManageIcon> newManageIcons) {
        this.manageIcons = newManageIcons;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView iconName;
        TextView iconDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImage);
            iconName = itemView.findViewById(R.id.iconName);
            iconDescription = itemView.findViewById(R.id.iconDescription);
        }
    }
} 