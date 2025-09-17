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

public class ManagingDirectorOffersAdapter extends RecyclerView.Adapter<ManagingDirectorOffersAdapter.OfferViewHolder> {

    private List<ManagingDirectorOffersActivity.OfferItem> offersList;
    private Context context;

    public ManagingDirectorOffersAdapter(Context context, List<ManagingDirectorOffersActivity.OfferItem> offersList) {
        this.context = context;
        this.offersList = offersList;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_managing_director_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        ManagingDirectorOffersActivity.OfferItem offer = offersList.get(position);
        
        holder.offerNameText.setText(offer.getName());
        holder.offerStatusText.setText(offer.getStatus());
        
        // Set status color based on status
        String status = offer.getStatus();
        if ("1".equals(status)) {
            holder.offerStatusText.setText("Active");
            holder.offerStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if ("0".equals(status)) {
            holder.offerStatusText.setText("Inactive");
            holder.offerStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            holder.offerStatusText.setText(status);
            holder.offerStatusText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
        
        // Set image for offer
        if (offer.getImageUrl() != null && !offer.getImageUrl().isEmpty()) {
            // Construct full image URL
            String imageUrl = "https://emp.kfinone.com/backPanel/uploads/offers/" + offer.getImageUrl();
            // Load image from URL
            new LoadImageTask(holder.offerImage, imageUrl).execute(imageUrl);
            
            // Set click listener for image popup
            holder.offerImage.setOnClickListener(v -> showImagePopup(imageUrl, offer.getName()));
        } else {
            holder.offerImage.setImageResource(R.drawable.ic_offers);
        }
    }

    @Override
    public int getItemCount() {
        return offersList.size();
    }

    // Method to update the offers list
    public void updateOffersList(List<ManagingDirectorOffersActivity.OfferItem> newOffersList) {
        this.offersList.clear();
        this.offersList.addAll(newOffersList);
        notifyDataSetChanged();
    }

    // Method to show image popup
    private void showImagePopup(String imageUrl, String offerName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(offerName);
        
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
                imageView.setImageResource(R.drawable.ic_offers);
            }
        }
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView offerNameText;
        ImageView offerImage;
        TextView offerStatusText;

        OfferViewHolder(View itemView) {
            super(itemView);
            offerNameText = itemView.findViewById(R.id.offerNameText);
            offerImage = itemView.findViewById(R.id.offerImage);
            offerStatusText = itemView.findViewById(R.id.offerStatusText);
        }
    }
}
