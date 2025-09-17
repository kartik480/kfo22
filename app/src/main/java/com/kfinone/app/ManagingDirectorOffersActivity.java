package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManagingDirectorOffersActivity extends AppCompatActivity {

    private TextView userNameText;
    private ImageView backButton;
    private RecyclerView offersRecyclerView;
    private ManagingDirectorOffersAdapter offersAdapter;
    
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_offers);
        
        initializeViews();
        setupUserData();
        setupClickListeners();
        setupRecyclerView();
        fetchOffersFromAPI();
    }

    private void initializeViews() {
        userNameText = findViewById(R.id.userNameText);
        backButton = findViewById(R.id.backButton);
        offersRecyclerView = findViewById(R.id.offersRecyclerView);
    }

    private void setupUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
            
            if (firstName != null && lastName != null) {
                userNameText.setText(firstName + " " + lastName);
            } else if (userName != null) {
                userNameText.setText(userName);
            }
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        offersAdapter = new ManagingDirectorOffersAdapter(this, new ArrayList<>());
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        offersRecyclerView.setAdapter(offersAdapter);
    }

    private void fetchOffersFromAPI() {
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    boolean success = jsonResponse.getBoolean("success");
                    
                    if (success) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        System.out.println("DEBUG: Total offers from API: " + dataArray.length());
                        List<ManagingDirectorOffersActivity.OfferItem> offersList = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject offerJson = dataArray.getJSONObject(i);
                            int id = offerJson.getInt("id");
                            String name = offerJson.getString("name");
                            String image = offerJson.getString("image");
                            String status = offerJson.getString("status");

                            // Only add offers with status "1" (active), exclude status "0" (inactive)
                            if ("1".equals(status)) {
                                System.out.println("DEBUG: Adding active offer - ID: " + id + ", Name: " + name + ", Status: '" + status + "'");
                                offersList.add(new ManagingDirectorOffersActivity.OfferItem(id, name, image, status));
                            } else {
                                System.out.println("DEBUG: Skipping inactive offer - ID: " + id + ", Name: " + name + ", Status: '" + status + "'");
                            }
                        }
                        
                        // Update UI on main thread
                        runOnUiThread(() -> {
                            System.out.println("DEBUG: Final offers list size: " + offersList.size());
                            offersAdapter.updateOffersList(offersList);
                            if (offersList.isEmpty()) {
                                Toast.makeText(this, "No offers found", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Found " + offersList.size() + " offers", Toast.LENGTH_SHORT).show();
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

    private void passUserDataToIntent(Intent intent) {
        if (intent != null) {
            intent.putExtra("USER_ID", userId);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("FIRST_NAME", firstName);
            intent.putExtra("LAST_NAME", lastName);
        }
    }

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

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
