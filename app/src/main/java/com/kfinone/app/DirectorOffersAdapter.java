package com.kfinone.app;

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

public class DirectorOffersAdapter extends RecyclerView.Adapter<DirectorOffersAdapter.OfferViewHolder> {

    private List<DirectorOffersActivity.OfferItem> offersList;
    private Context context;

    public DirectorOffersAdapter(List<DirectorOffersActivity.OfferItem> offersList, Context context) {
        this.offersList = offersList;
        this.context = context;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_director_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        DirectorOffersActivity.OfferItem offer = offersList.get(position);
        
        holder.offerNameText.setText(offer.getName());
        holder.offerStatusText.setText(offer.getStatus());
        
        // Set status color based on status
        switch (offer.getStatus().toLowerCase()) {
            case "active":
                holder.offerStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "inactive":
                holder.offerStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "expired":
                holder.offerStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            default:
                holder.offerStatusText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }
        
        // Set image for offer
        if (offer.getImageUrl() != null && !offer.getImageUrl().isEmpty()) {
            // Construct full image URL
            String imageUrl = "https://emp.kfinone.com/backPanel/uploads/offers/" + offer.getImageUrl();
            // Load image from URL
            new LoadImageTask(holder.offerImage).execute(imageUrl);
        } else {
            holder.offerImage.setImageResource(R.drawable.ic_offers);
        }
    }

    @Override
    public int getItemCount() {
        return offersList.size();
    }

    // Method to update the offers list
    public void updateOffersList(List<DirectorOffersActivity.OfferItem> newOffersList) {
        this.offersList.clear();
        this.offersList.addAll(newOffersList);
        notifyDataSetChanged();
    }

    // AsyncTask to load images from URL
    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
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
