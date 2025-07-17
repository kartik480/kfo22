package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Setup login button click listener
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            
            if (username.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter username", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Disable login button to prevent multiple clicks
            loginButton.setEnabled(false);
            loginButton.setText("Logging in...");
            
            // Perform login
            performLogin(username, password);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }

    private void performLogin(String username, String password) {
        Log.d(TAG, "Login attempt for user: " + username);
        
        executor.execute(() -> {
            try {
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("username", username);
                jsonData.put("password", password);

                // Create request body
                String jsonString = jsonData.toString();
                byte[] postData = jsonString.getBytes("UTF-8");

                String urlString = "https://emp.kfinone.com/mobile/api/login.php";

                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000); // Increased timeout
                connection.setReadTimeout(10000); // Increased timeout

                // Send data
                java.io.OutputStream os = connection.getOutputStream();
                os.write(postData);
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String responseBody = response.toString();
                    Log.d(TAG, "Response received successfully");

                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.getBoolean("success")) {
                        Log.d(TAG, "Login successful");
                        
                        // Extract user details from the JSON response
                        JSONObject user = jsonResponse.getJSONObject("user");
                        String firstName = user.optString("firstName", "");
                        String lastName = user.optString("lastName", "");
                        String fullName = (firstName + " " + lastName).trim();
                        String userId = user.optString("id", "");
                        
                        // If no full name, use username
                        final String displayName = fullName.isEmpty() ? user.optString("username", "User") : fullName;
                        
                        // Check if this is the special user (ID 8)
                        boolean isSpecialUser = jsonResponse.optBoolean("is_special_user", false);
                        String specialMessage = jsonResponse.optString("special_message", "");

                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                
                                if (isSpecialUser) {
                                    // Go to Special Panel for user ID 8
                                    Intent intent = new Intent(LoginActivity.this, SpecialPanelActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", userId);
                                    intent.putExtra("SPECIAL_MESSAGE", specialMessage);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Go to regular HomeActivity for other users
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", userId);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error in UI thread: " + e.getMessage());
                                resetLoginButton();
                            }
                        });
                    } else {
                        String error = jsonResponse.optString("message", "Login failed");
                        Log.e(TAG, "Login failed: " + error);
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.e(TAG, "Error showing error toast: " + e.getMessage());
                            }
                            resetLoginButton();
                        });
                    }
                } else {
                    Log.e(TAG, "HTTP Error: " + responseCode);
                    
                    // Try to read error stream
                    try {
                        java.io.BufferedReader errorReader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getErrorStream()));
                        StringBuilder errorResponse = new StringBuilder();
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            errorResponse.append(line);
                        }
                        errorReader.close();
                        Log.e(TAG, "Error Response: " + errorResponse.toString());
                    } catch (Exception e) {
                        Log.e(TAG, "Could not read error stream: " + e.getMessage());
                    }
                    
                    runOnUiThread(() -> {
                        try {
                            Toast.makeText(LoginActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error showing server error toast: " + e.getMessage());
                        }
                        resetLoginButton();
                    });
                }
                connection.disconnect();
                
            } catch (Exception e) {
                Log.e(TAG, "Exception during login: " + e.getMessage());
                runOnUiThread(() -> {
                    try {
                        Toast.makeText(LoginActivity.this, "Network error: Please check your connection", Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Log.e(TAG, "Error showing network error toast: " + ex.getMessage());
                    }
                    resetLoginButton();
                });
            }
        });
    }

    private void resetLoginButton() {
        if (loginButton != null) {
            loginButton.setEnabled(true);
            loginButton.setText("Login");
        }
    }
} 
