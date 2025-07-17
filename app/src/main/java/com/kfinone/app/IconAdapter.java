package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {
    private List<IconItem> icons;

    public IconAdapter(List<IconItem> icons) {
        this.icons = icons;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_icon, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        IconItem icon = icons.get(position);
        holder.iconNameText.setText(icon.getName());
        holder.iconDescriptionText.setText(icon.getDescription());
        // TODO: Load image using Glide or Picasso
        // Glide.with(holder.itemView.getContext())
        //     .load(icon.getImageUrl())
        //     .into(holder.iconImage);
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public void addIcon(IconItem icon) {
        icons.add(icon);
        notifyItemInserted(icons.size() - 1);
    }

    static class IconViewHolder extends RecyclerView.ViewHolder {
        TextView iconNameText;
        TextView iconDescriptionText;
        ImageView iconImage;
        TextView iconActionsText;

        IconViewHolder(View itemView) {
            super(itemView);
            iconNameText = itemView.findViewById(R.id.iconNameText);
            iconDescriptionText = itemView.findViewById(R.id.iconDescriptionText);
            iconImage = itemView.findViewById(R.id.iconImage);
            iconActionsText = itemView.findViewById(R.id.iconActionsText);
        }
    }
} 
