package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyEmpLinksAdapter extends RecyclerView.Adapter<MyEmpLinksAdapter.ViewHolder> {

    private List<MyEmpLinksActivity.IconPermissionItem> iconList;

    public MyEmpLinksAdapter(List<MyEmpLinksActivity.IconPermissionItem> iconList) {
        this.iconList = iconList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_emp_links, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyEmpLinksActivity.IconPermissionItem item = iconList.get(position);
        
        // Set icon name
        holder.iconNameText.setText(item.iconName);
        
        // Set icon description
        if (item.iconDescription != null && !item.iconDescription.isEmpty()) {
            holder.iconDescriptionText.setText(item.iconDescription);
        } else {
            holder.iconDescriptionText.setText("No description available");
        }
        
        // Set icon type with proper styling
        holder.iconTypeText.setText(item.type + " Icon");
        
        // Set permission status
        if ("Yes".equals(item.hasPermission)) {
            holder.permissionStatusText.setText("✓ Granted");
            holder.permissionStatusText.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_green_dark));
            holder.permissionStatusText.setBackgroundColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_green_light));
        } else {
            holder.permissionStatusText.setText("✗ Not Granted");
            holder.permissionStatusText.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_red_dark));
            holder.permissionStatusText.setBackgroundColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_red_light));
        }

        // Set icon image if available
        if (item.iconImage != null && !item.iconImage.isEmpty()) {
            // TODO: Load image using Glide or Picasso
            // For now, set a placeholder based on type
            switch (item.type) {
                case "Manage":
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_manage);
                    break;
                case "Data":
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_info_details);
                    break;
                case "Work":
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_edit);
                    break;
                default:
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_help);
                    break;
            }
        } else {
            // Set default icon based on type
            switch (item.type) {
                case "Manage":
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_manage);
                    break;
                case "Data":
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_info_details);
                    break;
                case "Work":
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_edit);
                    break;
                default:
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_help);
                    break;
            }
        }

        // Set background color based on type
        switch (item.type) {
            case "Manage":
                holder.typeIndicator.setBackgroundColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_green_dark));
                holder.iconTypeText.setBackgroundColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "Data":
                holder.typeIndicator.setBackgroundColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_blue_dark));
                holder.iconTypeText.setBackgroundColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "Work":
                holder.typeIndicator.setBackgroundColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_orange_dark));
                holder.iconTypeText.setBackgroundColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_orange_dark));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View typeIndicator;
        public ImageView iconImageView;
        public TextView iconNameText;
        public TextView iconDescriptionText;
        public TextView iconTypeText;
        public TextView permissionStatusText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeIndicator = itemView.findViewById(R.id.typeIndicator);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            iconNameText = itemView.findViewById(R.id.iconNameText);
            iconDescriptionText = itemView.findViewById(R.id.iconDescriptionText);
            iconTypeText = itemView.findViewById(R.id.iconTypeText);
            permissionStatusText = itemView.findViewById(R.id.permissionStatusText);
        }
    }
} 