package com.kfinone.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ImageViewerActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        // Additional flags to ensure complete fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        
        setContentView(R.layout.activity_image_viewer);

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        
        // Set up back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        // Get image URL from intent
        imageUrl = getIntent().getStringExtra("image_url");
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Handle different URL formats
            String finalUrl = imageUrl;
            
            android.util.Log.d("ImageViewer", "Original URL: " + imageUrl);
            
            // If it's a relative path, make it absolute
            if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                if (imageUrl.startsWith("/")) {
                    // Remove leading slash and add base URL
                    finalUrl = "https://emp.kfinone.com" + imageUrl;
                } else {
                    // Add base URL
                    finalUrl = "https://emp.kfinone.com/" + imageUrl;
                }
            }
            
            android.util.Log.d("ImageViewer", "Final URL: " + finalUrl);
            loadImage(finalUrl);
        } else {
            Toast.makeText(this, "No image URL provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set click listener to close image viewer
        imageView.setOnClickListener(v -> finish());
    }

    private void loadImage(String url) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            
            Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert)
                .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(com.bumptech.glide.load.engine.GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ImageViewerActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
