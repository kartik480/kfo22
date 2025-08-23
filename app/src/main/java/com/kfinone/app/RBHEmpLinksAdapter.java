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

public class RBHEmpLinksAdapter extends RecyclerView.Adapter<RBHEmpLinksAdapter.ViewHolder> {

    private Context context;
    private List<ManageIcon> manageIcons;

    public RBHEmpLinksAdapter(Context context, List<ManageIcon> manageIcons) {
        this.context = context;
        this.manageIcons = manageIcons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rbh_emp_link_icon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManageIcon icon = manageIcons.get(position);
        
        // Set icon name
        holder.iconNameText.setText(icon.getIconName());
        
        // Set icon description
        holder.iconDescriptionText.setText(icon.getIconDescription());
        
        // Set icon image (you can use Glide or Picasso for loading images from URLs)
        // For now, using a default icon
        holder.iconImageView.setImageResource(R.drawable.ic_settings);
        
        // Set click listener to open URL
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
        return manageIcons != null ? manageIcons.size() : 0;
    }

    public void updateData(List<ManageIcon> newIcons) {
        this.manageIcons = newIcons;
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
