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

public class BusinessHeadEmpLinksAdapter extends RecyclerView.Adapter<BusinessHeadEmpLinksAdapter.ViewHolder> {
    private List<BusinessHeadManageIcon> icons;
    private Context context;

    public BusinessHeadEmpLinksAdapter(Context context, List<BusinessHeadManageIcon> icons) {
        this.context = context;
        this.icons = icons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_head_emp_link_icon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusinessHeadManageIcon icon = icons.get(position);
        
        // Set icon name
        holder.iconNameText.setText(icon.getIconName());
        
        // Set icon description
        if (icon.getIconDescription() != null && !icon.getIconDescription().isEmpty()) {
            holder.iconDescriptionText.setText(icon.getIconDescription());
            holder.iconDescriptionText.setVisibility(View.VISIBLE);
        } else {
            holder.iconDescriptionText.setVisibility(View.GONE);
        }
        
        // Set click listener to open icon_url
        holder.itemView.setOnClickListener(v -> {
            if (icon.getIconUrl() != null && !icon.getIconUrl().isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(icon.getIconUrl()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to open link: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No link available for this icon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public void updateIcons(List<BusinessHeadManageIcon> newIcons) {
        this.icons = newIcons;
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
