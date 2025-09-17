package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class DirectorOffersActivity extends AppCompatActivity {

    private static final String TAG = "DirectorOffersActivity";

    // Top navigation elements
    private View backButton;
    private TextView titleText;

    // RecyclerView and adapter
    private RecyclerView offersRecyclerView;
    private DirectorOffersAdapter offersAdapter;

    // User data
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

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
        
        setContentView(R.layout.activity_director_offers);

        initializeViews();
        setupUserData();
        setupClickListeners();
        setupOffersList();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        if (titleText != null) {
            titleText.setText("Offers List");
        }

        // RecyclerView
        offersRecyclerView = findViewById(R.id.offersRecyclerView);
    }

    private void setupUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
        }
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupOffersList() {
        // Setup RecyclerView with empty list initially
        offersAdapter = new DirectorOffersAdapter(new ArrayList<>(), this);
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        offersRecyclerView.setAdapter(offersAdapter);
        
        // Fetch offers data from database
        fetchOffersFromDatabase();
    }

    private void fetchOffersFromDatabase() {
        new Thread(() -> {
            try {
                String apiUrl = "https://emp.kfinone.com/mobile/api/offers_api.php";
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    boolean success = jsonResponse.getBoolean("success");
                    
                    if (success) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<DirectorOffersActivity.OfferItem> offersList = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject offerJson = dataArray.getJSONObject(i);
                            int id = offerJson.getInt("id");
                            String name = offerJson.getString("name");
                            String image = offerJson.getString("image");
                            String status = offerJson.getString("status");
                            
                            offersList.add(new DirectorOffersActivity.OfferItem(id, name, image, status));
                        }
                        
                        // Update UI on main thread
                        runOnUiThread(() -> {
                            offersAdapter.updateOffersList(offersList);
                            if (offersList.isEmpty()) {
                                Toast.makeText(this, "No offers found", Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                    } else {
                        String message = jsonResponse.getString("message");
                        runOnUiThread(() -> {
                            Toast.makeText(this, "API Error: " + message, Toast.LENGTH_SHORT).show();
                        });
                    }
                    
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "HTTP Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                
                connection.disconnect();
                
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error fetching offers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Inner class for Offer item data
    public static class OfferItem {
        private int id;
        private String name;
        private String imageUrl;
        private String status;

        public OfferItem(int id, String name, String imageUrl, String status) {
            this.id = id;
            this.name = name;
            this.imageUrl = imageUrl;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
