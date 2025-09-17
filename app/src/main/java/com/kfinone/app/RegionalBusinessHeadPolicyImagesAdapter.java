package com.kfinone.app;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

public class RegionalBusinessHeadPolicyImagesAdapter extends RecyclerView.Adapter<RegionalBusinessHeadPolicyImagesAdapter.PolicyImageViewHolder> {

    private List<RegionalBusinessHeadPolicyImagesActivity.PolicyImageItem> policyImagesList;
    private Context context;

    public RegionalBusinessHeadPolicyImagesAdapter(Context context, List<RegionalBusinessHeadPolicyImagesActivity.PolicyImageItem> policyImagesList) {
        this.context = context;
        this.policyImagesList = policyImagesList;
    }

    @NonNull
    @Override
    public PolicyImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_regional_business_head_policy_images, parent, false);
        return new PolicyImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PolicyImageViewHolder holder, int position) {
        RegionalBusinessHeadPolicyImagesActivity.PolicyImageItem policyImage = policyImagesList.get(position);
        
        holder.policyImageNameText.setText(policyImage.getName());
        holder.policyImageStatusText.setText(policyImage.getStatus());
        
        // Set status color based on status
        String status = policyImage.getStatus();
        if ("1".equals(status)) {
            holder.policyImageStatusText.setText("Active");
            holder.policyImageStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if ("0".equals(status)) {
            holder.policyImageStatusText.setText("Inactive");
            holder.policyImageStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            holder.policyImageStatusText.setText(status);
            holder.policyImageStatusText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
        
        // Set image for policy image
        if (policyImage.getImageUrl() != null && !policyImage.getImageUrl().isEmpty()) {
            // Construct full image URL
            String imageUrl = "https://emp.kfinone.com/backPanel/uploads/policy/" + policyImage.getImageUrl();
            // Load image from URL
            new LoadImageTask(holder.policyImageImageView, imageUrl).execute(imageUrl);
            
            // Set click listener for image popup
            holder.policyImageImageView.setOnClickListener(v -> showImagePopup(imageUrl, policyImage.getName()));
        } else {
            holder.policyImageImageView.setImageResource(R.drawable.ic_policy);
        }
    }

    @Override
    public int getItemCount() {
        return policyImagesList.size();
    }

    // Method to update the policy images list
    public void updatePolicyImagesList(List<RegionalBusinessHeadPolicyImagesActivity.PolicyImageItem> newPolicyImagesList) {
        this.policyImagesList.clear();
        this.policyImagesList.addAll(newPolicyImagesList);
        notifyDataSetChanged();
    }

    // Method to show image popup
    private void showImagePopup(String imageUrl, String policyImageName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(policyImageName);
        
        // Create ImageView for popup
        ImageView popupImageView = new ImageView(context);
        popupImageView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        popupImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        popupImageView.setAdjustViewBounds(true);
        
        builder.setView(popupImageView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Load image for popup
        new LoadImageTask(popupImageView, imageUrl).execute(imageUrl);
    }

    // AsyncTask to load images from URL
    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private String imageUrl;

        LoadImageTask(ImageView imageView, String imageUrl) {
            this.imageView = imageView;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                // Set default image if loading fails
                imageView.setImageResource(R.drawable.ic_policy);
            }
        }
    }

    static class PolicyImageViewHolder extends RecyclerView.ViewHolder {
        TextView policyImageNameText;
        ImageView policyImageImageView;
        TextView policyImageStatusText;

        PolicyImageViewHolder(View itemView) {
            super(itemView);
            policyImageNameText = itemView.findViewById(R.id.policyImageNameText);
            policyImageImageView = itemView.findViewById(R.id.policyImageImageView);
            policyImageStatusText = itemView.findViewById(R.id.policyImageStatusText);
        }
    }
}
