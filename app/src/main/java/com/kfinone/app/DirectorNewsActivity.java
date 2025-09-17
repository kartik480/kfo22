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

public class DirectorNewsActivity extends AppCompatActivity {

    private TextView userNameText;
    private ImageView backButton;
    private RecyclerView newsRecyclerView;
    private DirectorNewsAdapter newsAdapter;
    
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_news);
        
        initializeViews();
        setupUserData();
        setupClickListeners();
        setupRecyclerView();
        fetchNewsFromAPI();
    }

    private void initializeViews() {
        userNameText = findViewById(R.id.userNameText);
        backButton = findViewById(R.id.backButton);
        newsRecyclerView = findViewById(R.id.newsRecyclerView);
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
        newsAdapter = new DirectorNewsAdapter(this, new ArrayList<>());
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);
    }

    private void fetchNewsFromAPI() {
        new Thread(() -> {
            try {
                String apiUrl = "https://emp.kfinone.com/mobile/api/news_api.php";
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
                        System.out.println("DEBUG: Total news from API: " + dataArray.length());
                        List<DirectorNewsActivity.NewsItem> newsList = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject newsJson = dataArray.getJSONObject(i);
                            int id = newsJson.getInt("id");
                            String name = newsJson.getString("name");
                            String image = newsJson.getString("image");
                            String status = newsJson.getString("status");

                            // Only add news with status "1" (active), exclude status "0" (inactive)
                            if ("1".equals(status)) {
                                System.out.println("DEBUG: Adding active news - ID: " + id + ", Name: " + name + ", Status: '" + status + "'");
                                newsList.add(new DirectorNewsActivity.NewsItem(id, name, image, status));
                            } else {
                                System.out.println("DEBUG: Skipping inactive news - ID: " + id + ", Name: " + name + ", Status: '" + status + "'");
                            }
                        }
                        
                        // Update UI on main thread
                        runOnUiThread(() -> {
                            System.out.println("DEBUG: Final news list size: " + newsList.size());
                            newsAdapter.updateNewsList(newsList);
                            if (newsList.isEmpty()) {
                                Toast.makeText(this, "No news found", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Found " + newsList.size() + " news items", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, "Error fetching news: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public static class NewsItem {
        private int id;
        private String name;
        private String imageUrl;
        private String status;

        public NewsItem(int id, String name, String imageUrl, String status) {
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
