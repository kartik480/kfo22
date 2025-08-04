package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class EnhancedLoginActivity extends AppCompatActivity {
    private static final String TAG = "EnhancedLoginActivity";
    
    // UI Components
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private ImageView logoImage;
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_login);

        initializeViews();
        setupAnimations();
        setupClickListeners();
        setupInputValidation();
    }

    private void initializeViews() {
        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        logoImage = findViewById(R.id.logoImage);
    }

    private void setupAnimations() {
        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        
        // Apply animations
        logoImage.startAnimation(fadeIn);
        
        // Simple fade in animation for the bottom section
        Animation bottomFadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        bottomFadeIn.setDuration(1000);
        findViewById(R.id.bottomSection).startAnimation(bottomFadeIn);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            
            if (validateInputs(username, password)) {
                performLogin(username, password);
            }
        });

        // Add text change listeners for real-time validation
        usernameEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear any previous errors
                if (s.length() > 0) {
                    usernameEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        passwordEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear any previous errors
                if (s.length() > 0) {
                    passwordEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void setupInputValidation() {
        // Set up input types
        usernameEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private boolean validateInputs(String username, String password) {
        boolean isValid = true;
        
        // The original code had usernameLayout and passwordLayout which are not defined.
        // Assuming they are meant to be TextInputLayouts or similar.
        // For now, we'll just check if the EditTexts are empty.
        if (username.isEmpty()) {
            usernameEditText.setError("Please enter username");
            isValid = false;
        } else if (username.length() < 3) {
            usernameEditText.setError("Username must be at least 3 characters");
            isValid = false;
        }
        
        if (password.isEmpty()) {
            passwordEditText.setError("Please enter password");
            isValid = false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            isValid = false;
        }
        
        return isValid;
    }

    private void performLogin(String username, String password) {
        // Show loading state
        setLoadingState(true);
        
        Log.d(TAG, "Login attempt for user: " + username);
        
        executor.execute(() -> {
            try {
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("username", username);
                jsonData.put("password", password);

                String jsonString = jsonData.toString();
                byte[] postData = jsonString.getBytes("UTF-8");

                String urlString = "https://emp.kfinone.com/mobile/api/login.php";

                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

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
                        
                        // Extract user details
                        JSONObject user = jsonResponse.getJSONObject("user");
                        String firstName = user.optString("firstName", "");
                        String lastName = user.optString("lastName", "");
                        String fullName = (firstName + " " + lastName).trim();
                        String userId = user.optString("id", "");
                        
                        final String displayName = fullName.isEmpty() ? user.optString("username", "User") : fullName;
                        
                        // Check for special user (ID 8 or username 10002)
                        boolean isSpecialUser = jsonResponse.optBoolean("is_special_user", false);
                        String specialMessage = jsonResponse.optString("special_message", "");
                        
                        // Check if this is user 10002
                        boolean isUser10002 = "10002".equals(username);
                        
                        // Check if user is Chief Business Officer
                        boolean isChiefBusinessOfficer = jsonResponse.optBoolean("is_chief_business_officer", false);
                        
                        // Check if user is Regional Business Head
                        boolean isRegionalBusinessHead = jsonResponse.optBoolean("is_regional_business_head", false);
                        
                        // Check if user is Business Head
                        boolean isBusinessHead = jsonResponse.optBoolean("is_business_head", false);
                        
                        // Fallback: Check designation directly if API flag is not present
                        if (!isRegionalBusinessHead && user.has("designation_name")) {
                            String designation = user.getString("designation_name");
                            isRegionalBusinessHead = "Regional Business Head".equals(designation);
                        }
                        
                        if (!isBusinessHead && user.has("designation_name")) {
                            String designation = user.getString("designation_name");
                            isBusinessHead = "Business Head".equals(designation);
                        }
                        
                        // Make final for lambda
                        final boolean finalIsRegionalBusinessHead = isRegionalBusinessHead;
                        final boolean finalIsBusinessHead = isBusinessHead;
                        
                        Log.d(TAG, "Username: " + username + ", isUser10002: " + isUser10002);
                        Log.d(TAG, "User ID from server: " + userId);
                        Log.d(TAG, "Is Chief Business Officer: " + isChiefBusinessOfficer);
                        Log.d(TAG, "Is Regional Business Head: " + isRegionalBusinessHead);
                        Log.d(TAG, "Is Business Head: " + isBusinessHead);
                        Log.d(TAG, "Designation: " + user.optString("designation_name", "N/A"));
                        Log.d(TAG, "Full response: " + responseBody);

                        runOnUiThread(() -> {
                            try {
                                showSuccessMessage("Login successful!");
                                
                                // Save user data to SharedPreferences
                                saveUserDataToSharedPreferences(username, firstName, lastName, userId);
                                
                                if (isChiefBusinessOfficer) {
                                    Log.d(TAG, "Navigating to ChiefBusinessOfficerPanelActivity");
                                    // Navigate to Chief Business Officer panel
                                    Intent intent = new Intent(EnhancedLoginActivity.this, ChiefBusinessOfficerPanelActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", userId);
                                    startActivity(intent);
                                    finish();
                                } else if (finalIsBusinessHead) {
                                    Log.d(TAG, "Navigating to BusinessHeadPanelActivity");
                                    // Navigate to Business Head panel
                                    Intent intent = new Intent(EnhancedLoginActivity.this, BusinessHeadPanelActivity.class);
                                    intent.putExtra("USERNAME", username);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", userId);
                                    startActivity(intent);
                                    finish();
                                } else if (finalIsRegionalBusinessHead) {
                                    Log.d(TAG, "Navigating to RegionalBusinessHeadPanelActivity");
                                    // Navigate to Regional Business Head panel
                                    Intent intent = new Intent(EnhancedLoginActivity.this, RegionalBusinessHeadPanelActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", userId);
                                    startActivity(intent);
                                    finish();
                                } else if (isUser10002 || "10002".equals(userId)) {
                                    Log.d(TAG, "Navigating to User10002PanelActivity");
                                    // Navigate to new enhanced panel for user 10002
                                    Intent intent = new Intent(EnhancedLoginActivity.this, User10002PanelActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", userId);
                                    intent.putExtra("SPECIAL_MESSAGE", "Welcome to your enhanced dashboard!");
                                    startActivity(intent);
                                    finish();
                                } else if (isSpecialUser) {
                                    // Navigate to existing special panel
                                    Intent intent = new Intent(EnhancedLoginActivity.this, SpecialPanelActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", userId);
                                    intent.putExtra("SPECIAL_MESSAGE", specialMessage);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Navigate to regular home
                                    Intent intent = new Intent(EnhancedLoginActivity.this, HomeActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", userId);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error in UI thread: " + e.getMessage());
                                setLoadingState(false);
                            }
                        });
                    } else {
                        String error = jsonResponse.optString("message", "Login failed");
                        Log.e(TAG, "Login failed: " + error);
                        runOnUiThread(() -> {
                            showErrorMessage("Login failed: " + error);
                            setLoadingState(false);
                        });
                    }
                } else {
                    Log.e(TAG, "HTTP Error: " + responseCode);
                    runOnUiThread(() -> {
                        showErrorMessage("Server error: " + responseCode);
                        setLoadingState(false);
                    });
                }
                connection.disconnect();
                
            } catch (Exception e) {
                Log.e(TAG, "Exception during login: " + e.getMessage());
                runOnUiThread(() -> {
                    showErrorMessage("Network error: Please check your connection");
                    setLoadingState(false);
                });
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        runOnUiThread(() -> {
            if (isLoading) {
                loginButton.setEnabled(false);
                loginButton.setText("Logging in...");
                progressBar.setVisibility(View.VISIBLE);
                usernameEditText.setEnabled(false);
                passwordEditText.setEnabled(false);
            } else {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                progressBar.setVisibility(View.GONE);
                usernameEditText.setEnabled(true);
                passwordEditText.setEnabled(true);
            }
        });
    }

    private void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    private void saveUserDataToSharedPreferences(String username, String firstName, String lastName, String userId) {
        try {
            android.content.SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            
            if (username != null) editor.putString("USERNAME", username);
            if (firstName != null) editor.putString("FIRST_NAME", firstName);
            if (lastName != null) editor.putString("LAST_NAME", lastName);
            if (userId != null) editor.putString("USER_ID", userId);
            
            editor.apply();
            Log.d(TAG, "User data saved to SharedPreferences");
        } catch (Exception e) {
            Log.e(TAG, "Error saving user data to SharedPreferences: " + e.getMessage());
        }
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
} 