package com.kfinone.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryActivity extends AppCompatActivity implements CategoryAdapter.OnItemClickListener {

    private TextInputEditText categoryNameEditText;
    private MaterialButton submitButton;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter adapter;
    private List<CategoryItem> categoryList;
    private ExecutorService executorService;
    private static final String API_URL = "https://emp.kfinone.com/mobile/api/get_category_list.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Category");
        }

        // Initialize views
        categoryNameEditText = findViewById(R.id.categoryNameEditText);
        submitButton = findViewById(R.id.submitButton);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);

        // Initialize RecyclerView
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList, this);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setAdapter(adapter);

        // Initialize executor service for background tasks
        executorService = Executors.newSingleThreadExecutor();

        // Fetch categories from API
        fetchCategories();

        // Set click listener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = categoryNameEditText.getText().toString().trim();
                if (!categoryName.isEmpty()) {
                    // TODO: Save category to database via API
                    // For now, add to local list
                    CategoryItem newItem = new CategoryItem(categoryName, "Active", categoryList.size() + 1);
                    categoryList.add(newItem);
                    adapter.notifyItemInserted(categoryList.size() - 1);
                    
                    // Clear the input field
                    categoryNameEditText.setText("");
                    
                    Toast.makeText(CategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CategoryActivity.this, "Please enter category name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchCategories() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(API_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        // Debug: Print the response
                        System.out.println("API Response: " + response.toString());

                        // Parse JSON response
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray data = jsonResponse.getJSONArray("data");
                            categoryList.clear();
                            
                            // Debug: Print the number of items
                            System.out.println("Number of categories found: " + data.length());
                            
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.getJSONObject(i);
                                String categoryName = item.getString("category_name");
                                String status = item.optString("status", "Active");
                                
                                categoryList.add(new CategoryItem(categoryName, status, i + 1));
                            }

                            // Update UI on main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    // Debug: Show toast with count
                                    Toast.makeText(CategoryActivity.this, "Loaded " + categoryList.size() + " categories", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Debug: Show error message
                            final String errorMsg = jsonResponse.optString("message", "Unknown error");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CategoryActivity.this, "API Error: " + errorMsg, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        // Debug: Show HTTP error
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CategoryActivity.this, "HTTP Error: " + responseCode, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CategoryActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onEditClick(CategoryItem item) {
        // Handle edit action
        Toast.makeText(this, "Edit: " + item.getCategoryName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(CategoryItem item) {
        // Handle delete action
        categoryList.remove(item);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Deleted: " + item.getCategoryName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 
