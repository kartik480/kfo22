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

public class ManagingDirectorNewsAdapter extends RecyclerView.Adapter<ManagingDirectorNewsAdapter.NewsViewHolder> {

    private List<ManagingDirectorNewsActivity.NewsItem> newsList;
    private Context context;

    public ManagingDirectorNewsAdapter(Context context, List<ManagingDirectorNewsActivity.NewsItem> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_managing_director_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        ManagingDirectorNewsActivity.NewsItem news = newsList.get(position);
        
        holder.newsNameText.setText(news.getName());
        holder.newsStatusText.setText(news.getStatus());
        
        // Set status color based on status
        String status = news.getStatus();
        if ("1".equals(status)) {
            holder.newsStatusText.setText("Active");
            holder.newsStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if ("0".equals(status)) {
            holder.newsStatusText.setText("Inactive");
            holder.newsStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            holder.newsStatusText.setText(status);
            holder.newsStatusText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
        
        // Set image for news
        if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
            // Construct full image URL
            String imageUrl = "https://emp.kfinone.com/backPanel/uploads/news/" + news.getImageUrl();
            // Load image from URL
            new LoadImageTask(holder.newsImage, imageUrl).execute(imageUrl);
            
            // Set click listener for image popup
            holder.newsImage.setOnClickListener(v -> showImagePopup(imageUrl, news.getName()));
        } else {
            holder.newsImage.setImageResource(R.drawable.ic_news);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    // Method to update the news list
    public void updateNewsList(List<ManagingDirectorNewsActivity.NewsItem> newNewsList) {
        this.newsList.clear();
        this.newsList.addAll(newNewsList);
        notifyDataSetChanged();
    }

    // Method to show image popup
    private void showImagePopup(String imageUrl, String newsName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(newsName);
        
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
                imageView.setImageResource(R.drawable.ic_news);
            }
        }
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsNameText;
        ImageView newsImage;
        TextView newsStatusText;

        NewsViewHolder(View itemView) {
            super(itemView);
            newsNameText = itemView.findViewById(R.id.newsNameText);
            newsImage = itemView.findViewById(R.id.newsImage);
            newsStatusText = itemView.findViewById(R.id.newsStatusText);
        }
    }
}
