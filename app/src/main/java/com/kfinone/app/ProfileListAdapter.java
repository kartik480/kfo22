package com.kfinone.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class ProfileListAdapter extends BaseAdapter {

    private Context context;
    private List<DirectorProfileActivity.ProfileItem> profileList;

    public ProfileListAdapter(Context context, List<DirectorProfileActivity.ProfileItem> profileList) {
        this.context = context;
        this.profileList = profileList;
    }

    @Override
    public int getCount() {
        return profileList.size();
    }

    @Override
    public Object getItem(int position) {
        return profileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_profile_list, parent, false);
            holder = new ViewHolder();
            holder.vendorBankTextView = convertView.findViewById(R.id.vendorBankTextView);
            holder.loanTypeTextView = convertView.findViewById(R.id.loanTypeTextView);
            holder.imagesTextView = convertView.findViewById(R.id.imagesTextView);
            holder.fileTextView = convertView.findViewById(R.id.fileTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DirectorProfileActivity.ProfileItem profile = profileList.get(position);
        holder.vendorBankTextView.setText(profile.getVendorBank());
        holder.loanTypeTextView.setText(profile.getLoanType());
        
        // Set clickable links for Images and File
        if (profile.getImage() != null && !profile.getImage().isEmpty()) {
            holder.imagesTextView.setText("📷 View Image");
            holder.imagesTextView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            holder.imagesTextView.setPaintFlags(holder.imagesTextView.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        } else {
            holder.imagesTextView.setText("No Image");
            holder.imagesTextView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.imagesTextView.setPaintFlags(holder.imagesTextView.getPaintFlags() & (~android.graphics.Paint.UNDERLINE_TEXT_FLAG));
        }
        
        if (profile.getFile() != null && !profile.getFile().isEmpty()) {
            holder.fileTextView.setText("📄 View File");
            holder.fileTextView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            holder.fileTextView.setPaintFlags(holder.fileTextView.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        } else {
            holder.fileTextView.setText("No File");
            holder.fileTextView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.fileTextView.setPaintFlags(holder.fileTextView.getPaintFlags() & (~android.graphics.Paint.UNDERLINE_TEXT_FLAG));
        }

        // Set click listeners for Images and File
        holder.imagesTextView.setOnClickListener(v -> {
            if (profile.getImage() != null && !profile.getImage().isEmpty()) {
                android.util.Log.d("ProfileAdapter", "Image URL: " + profile.getImage());
                openImage(profile.getImage());
            }
            // No else case - if no image, clicking does nothing
        });

        holder.fileTextView.setOnClickListener(v -> {
            if (profile.getFile() != null && !profile.getFile().isEmpty()) {
                openFile(profile.getFile());
            }
            // No else case - if no file, clicking does nothing
        });

        return convertView;
    }

    private void openImage(String imageFileName) {
        try {
            // Construct full image URL
            String fullImageUrl = "https://emp.kfinone.com/backPanel/uploads/tr_profile/" + imageFileName;
            
            Intent intent = new Intent(context, ImageViewerActivity.class);
            intent.putExtra("image_url", fullImageUrl);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openFile(String fileName) {
        try {
            // Construct full file URL
            String fullFileUrl = "https://emp.kfinone.com/backPanel/uploads/tr_profile/" + fileName;
            
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(fullFileUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static class ViewHolder {
        TextView vendorBankTextView;
        TextView loanTypeTextView;
        TextView imagesTextView;
        TextView fileTextView;
    }
}
