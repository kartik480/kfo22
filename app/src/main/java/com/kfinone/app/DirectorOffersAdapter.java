package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
            // For now, set default image. You can implement image loading library like Glide or Picasso here
            holder.offerImage.setImageResource(R.drawable.ic_offers);
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
