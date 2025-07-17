package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InactiveSdsaListActivity extends AppCompatActivity {

    private static final String TAG = "InactiveSdsaListActivity";
    private RecyclerView recyclerView;
    private InactiveSdsaAdapter adapter;
    private List<InactiveSdsaItem> sdsaList;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inactive_sdsa_list);

        // Initialize back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.inactiveSdsaListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data and executor
        sdsaList = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();

        // Set adapter
        adapter = new InactiveSdsaAdapter(sdsaList);
        recyclerView.setAdapter(adapter);

        // Load inactive SDSA users from database
        loadInactiveSdsaUsers();
    }

    private void loadInactiveSdsaUsers() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/get_inactive_sdsa_users.php";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray usersArray = jsonResponse.getJSONArray("data");
                        List<InactiveSdsaItem> newUsers = new ArrayList<>();
                        
                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject userObj = usersArray.getJSONObject(i);
                            
                            // Use correct column names from tbl_sdsa_users
                            String firstName = userObj.optString("first_name", "");
                            String lastName = userObj.optString("last_name", "");
                            String fullName = (firstName + " " + lastName).trim();
                            if (fullName.isEmpty()) {
                                fullName = userObj.optString("username", "Unknown User");
                            }
                            
                            String phone = userObj.optString("Phone_number", "");
                            if (phone.isEmpty()) {
                                phone = userObj.optString("alternative_mobile_number", "No Phone");
                            }
                            
                            String email = userObj.optString("email_id", "");
                            int status = userObj.optInt("status", 0);
                            
                            newUsers.add(new InactiveSdsaItem(fullName, phone, email, "Inactive"));
                        }
                        
                        runOnUiThread(() -> {
                            sdsaList.clear();
                            sdsaList.addAll(newUsers);
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Loaded " + sdsaList.size() + " inactive SDSA users from database");
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("message"));
                        runOnUiThread(() -> {
                            Toast.makeText(InactiveSdsaListActivity.this, 
                                "Failed to load inactive SDSA users", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(InactiveSdsaListActivity.this, 
                            "Server error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load inactive SDSA users", e);
                runOnUiThread(() -> {
                    Toast.makeText(InactiveSdsaListActivity.this, 
                        "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

    private static class InactiveSdsaItem {
        String fullname;
        String mobile;
        String email;
        String status;

        InactiveSdsaItem(String fullname, String mobile, String email, String status) {
            this.fullname = fullname;
            this.mobile = mobile;
            this.email = email;
            this.status = status;
        }
    }

    private class InactiveSdsaAdapter extends RecyclerView.Adapter<InactiveSdsaAdapter.ViewHolder> {
        private List<InactiveSdsaItem> items;

        InactiveSdsaAdapter(List<InactiveSdsaItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_inactive_sdsa, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            InactiveSdsaItem item = items.get(position);
            holder.fullnameText.setText(item.fullname);
            holder.mobileText.setText(item.mobile);
            holder.emailText.setText(item.email);
            holder.statusText.setText(item.status);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView fullnameText, mobileText, emailText, statusText;

            ViewHolder(View view) {
                super(view);
                fullnameText = view.findViewById(R.id.fullnameText);
                mobileText = view.findViewById(R.id.mobileText);
                emailText = view.findViewById(R.id.emailText);
                statusText = view.findViewById(R.id.statusText);
            }
        }
    }
} 
