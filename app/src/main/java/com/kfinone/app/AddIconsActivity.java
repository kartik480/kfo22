package com.kfinone.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.*;
import org.json.JSONObject;

public class AddIconsActivity extends AppCompatActivity {
    private static final String TAG = "AddIconsActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/"; // Change this to your server URL
    
    private TextInputEditText iconNameInput;
    private TextInputEditText urlNameInput;
    private TextInputEditText descriptionInput;
    private MaterialCardView chooseFileCard;
    private MaterialButton submitButton;
    private RecyclerView iconsRecyclerView;
    private IconAdapter iconAdapter;
    private List<IconItem> icons;
    private Uri selectedImageUri;
    private OkHttpClient client;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_icons);

        // Initialize HTTP client and executor
        client = new OkHttpClient();
        executor = Executors.newSingleThreadExecutor();

        // Initialize views
        iconNameInput = findViewById(R.id.iconNameInput);
        urlNameInput = findViewById(R.id.urlNameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        chooseFileCard = findViewById(R.id.chooseFileCard);
        submitButton = findViewById(R.id.submitButton);
        iconsRecyclerView = findViewById(R.id.iconsRecyclerView);

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize RecyclerView
        icons = new ArrayList<>();
        iconAdapter = new IconAdapter(icons);
        iconsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        iconsRecyclerView.setAdapter(iconAdapter);

        // Setup file chooser
        chooseFileCard.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        // Setup submit button
        submitButton.setOnClickListener(v -> submitIconData());
        
        // Load existing icons from database
        loadExistingIcons();
    }

    private void submitIconData() {
        String name = iconNameInput.getText().toString().trim();
        String urlName = urlNameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (name.isEmpty() || urlName.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable submit button to prevent multiple submissions
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        executor.execute(() -> {
            try {
                // Convert image to base64
                String imageBase64 = convertImageToBase64(selectedImageUri);
                
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("icon_name", name);
                jsonData.put("icon_url", urlName);
                jsonData.put("icon_image", imageBase64);
                jsonData.put("icon_description", description);

                // Create request body
                RequestBody body = RequestBody.create(
                    jsonData.toString(),
                    MediaType.parse("application/json; charset=utf-8")
                );

                // Create request
                Request request = new Request.Builder()
                    .url(BASE_URL + "add_data_icon.php")
                    .post(body)
                    .build();

                // Execute request
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Response: " + responseBody);

                    runOnUiThread(() -> {
                        submitButton.setEnabled(true);
                        submitButton.setText("Submit");

                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                if (jsonResponse.getBoolean("success")) {
                                    Toast.makeText(AddIconsActivity.this, 
                                        "Icon added successfully!", Toast.LENGTH_SHORT).show();
                                    
                                    // Clear inputs
                                    iconNameInput.setText("");
                                    urlNameInput.setText("");
                                    descriptionInput.setText("");
                                    selectedImageUri = null;
                                    
                                    // Refresh the icon list from database
                                    loadExistingIcons();
                                } else {
                                    String error = jsonResponse.optString("error", "Unknown error occurred");
                                    Toast.makeText(AddIconsActivity.this, 
                                        "Error: " + error, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing response", e);
                                Toast.makeText(AddIconsActivity.this, 
                                    "Error parsing server response", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddIconsActivity.this, 
                                "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error submitting icon data", e);
                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                    Toast.makeText(AddIconsActivity.this, 
                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String convertImageToBase64(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        inputStream.close();
        byteArrayOutputStream.close();
        
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

    private void loadExistingIcons() {
        executor.execute(() -> {
            try {
                // Create request
                Request request = new Request.Builder()
                    .url(BASE_URL + "get_manage_icons.php") // Changed endpoint
                    .get()
                    .build();

                // Execute request
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Icons response: " + responseBody);

                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                if (jsonResponse.getString("status").equals("success")) {
                                    // Parse the icons data and add to adapter
                                    icons.clear();
                                    
                                    try {
                                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                                        for (int i = 0; i < dataArray.length(); i++) {
                                            org.json.JSONObject iconObj = dataArray.getJSONObject(i);
                                            String iconName = iconObj.optString("icon_name", "");
                                            String iconUrl = iconObj.optString("icon_url", ""); // May not exist
                                            String iconImage = iconObj.optString("icon_image", "");
                                            String iconDescription = iconObj.optString("icon_description", "");
                                            
                                            // Create IconItem and add to list
                                            IconItem iconItem = new IconItem(iconName, iconDescription, iconImage, iconUrl);
                                            icons.add(iconItem);
                                        }
                                        
                                        iconAdapter.notifyDataSetChanged();
                                        Log.d(TAG, "Loaded " + icons.size() + " icons from database");
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error parsing icons data", e);
                                    }
                                } else {
                                    String error = jsonResponse.optString("message", "Unknown error occurred");
                                    Log.e(TAG, "Error loading icons: " + error);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing icons response", e);
                            }
                        } else {
                            Log.e(TAG, "Server error loading icons: " + response.code());
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading existing icons", e);
            }
        });
    }
} 
