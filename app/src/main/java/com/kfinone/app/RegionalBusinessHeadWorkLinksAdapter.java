package com.kfinone.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegionalBusinessHeadWorkLinksAdapter extends RecyclerView.Adapter<RegionalBusinessHeadWorkLinksAdapter.ViewHolder> {
    
    private Context context;
    private List<WorkIcon> workIcons;
    
    public RegionalBusinessHeadWorkLinksAdapter(Context context, List<WorkIcon> workIcons) {
        this.context = context;
        this.workIcons = workIcons;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rbh_work_link_icon, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkIcon icon = workIcons.get(position);
        
        holder.iconNameText.setText(icon.getIconName());
        holder.iconDescriptionText.setText(icon.getIconDescription());
        
        // Set default icon image
        holder.iconImageView.setImageResource(R.drawable.ic_work);
        
        // Set click listener to open icon_url
        holder.itemView.setOnClickListener(v -> {
            String url = icon.getIconUrl();
            if (url != null && !url.isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to open link: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No URL available for this icon", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return workIcons.size();
    }
    
    public void updateData(List<WorkIcon> newWorkIcons) {
        this.workIcons.clear();
        this.workIcons.addAll(newWorkIcons);
        notifyDataSetChanged();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView iconNameText;
        TextView iconDescriptionText;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            iconNameText = itemView.findViewById(R.id.iconNameText);
            iconDescriptionText = itemView.findViewById(R.id.iconDescriptionText);
        }
    }
}
