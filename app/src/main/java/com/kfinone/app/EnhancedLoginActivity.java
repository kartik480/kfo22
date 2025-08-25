package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_login);

        // Initialize views first
        initializeViews();
        
        // Setup click listeners immediately for responsiveness
        setupClickListeners();
        setupInputValidation();
        
        // Defer animations to prevent blocking the main thread
        mainHandler.postDelayed(this::setupAnimations, 100);
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
        try {
            // Load animations in background to prevent blocking
            executor.execute(() -> {
                try {
                    // Load animations
                    Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
                    Animation bottomFadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
                    bottomFadeIn.setDuration(800); // Reduced duration
                    
                    // Apply animations on main thread
                    mainHandler.post(() -> {
                        try {
                            if (logoImage != null) {
                                logoImage.startAnimation(fadeIn);
                            }
                            
                            View bottomSection = findViewById(R.id.bottomSection);
                            if (bottomSection != null) {
                                bottomSection.startAnimation(bottomFadeIn);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error applying animations: " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error loading animations: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in setupAnimations: " + e.getMessage());
        }
    }

    private void setupClickListeners() {
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                
                if (validateInputs(username, password)) {
                    performLogin(username, password);
                }
            });
        }

        // Add text change listeners for real-time validation
        if (usernameEditText != null) {
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
        }

        if (passwordEditText != null) {
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
    }

    private void setupInputValidation() {
        // Set up input types
        if (usernameEditText != null) {
            usernameEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        }
        if (passwordEditText != null) {
            passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    private boolean validateInputs(String username, String password) {
        boolean isValid = true;
        
        if (usernameEditText != null) {
            if (username.isEmpty()) {
                usernameEditText.setError("Please enter username");
                isValid = false;
            } else if (username.length() < 3) {
                usernameEditText.setError("Username must be at least 3 characters");
                isValid = false;
            }
        }
        
        if (passwordEditText != null) {
            if (password.isEmpty()) {
                passwordEditText.setError("Please enter password");
                isValid = false;
            } else if (password.length() < 6) {
                passwordEditText.setError("Password must be at least 6 characters");
                isValid = false;
            }
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
                connection.setConnectTimeout(10000); // Reduced timeout
                connection.setReadTimeout(10000); // Reduced timeout

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
                        
                        // Debug: Log the user object to see what fields are available
                        Log.d(TAG, "=== User Object Details ===");
                        Log.d(TAG, "User object keys: " + user.toString());
                        Log.d(TAG, "firstName: " + firstName);
                        Log.d(TAG, "lastName: " + lastName);
                        Log.d(TAG, "fullName: " + fullName);
                        Log.d(TAG, "userId (id): " + userId);
                        Log.d(TAG, "username: " + user.optString("username", "N/A"));
                        
                        // Check all available fields in the user object
                        java.util.Iterator<String> keys = user.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String value = user.optString(key, "N/A");
                            Log.d(TAG, "Field: " + key + " = " + value);
                        }
                        Log.d(TAG, "========================");
                        
                        // Fallback: If userId is empty, try to get it from username
                        if ((userId == null || userId.isEmpty()) && username != null && username.matches("\\d+")) {
                            userId = username;
                            Log.d(TAG, "Using username as userId fallback: " + userId);
                        }
                        
                        // Create variable for lambda usage (not final since we may need to modify it)
                        String finalUserId = userId;
                        
                        final String displayName = fullName.isEmpty() ? user.optString("username", "User") : fullName;
                        
                        // Check for special user (ID 8 or username 10002)
                        final boolean isSpecialUser = jsonResponse.optBoolean("is_special_user", false);
                        final String specialMessage = jsonResponse.optString("special_message", "");
                        
                        // Check if this is user 10002
                        final boolean isUser10002 = "10002".equals(username);
                        
                        // Check if user is Chief Business Officer
                        final boolean isChiefBusinessOfficer = jsonResponse.optBoolean("is_chief_business_officer", false);
                        
                        // Check if user is Regional Business Head
                        boolean isRegionalBusinessHead = jsonResponse.optBoolean("is_regional_business_head", false);
                        
                        // Check if user is Business Head
                        boolean isBusinessHead = jsonResponse.optBoolean("is_business_head", false);
                        
                        // Check if user is Marketing Head
                        boolean isMarketingHead = jsonResponse.optBoolean("is_marketing_head", false);
                        
                        // Fallback: Check designation directly if API flag is not present
                        if (!isRegionalBusinessHead && user.has("designation_name")) {
                            String designation = user.getString("designation_name");
                            isRegionalBusinessHead = "Regional Business Head".equals(designation);
                        }
                        
                        if (!isBusinessHead && user.has("designation_name")) {
                            String designation = user.getString("designation_name");
                            isBusinessHead = "Business Head".equals(designation);
                        }
                        
                        if (!isMarketingHead && user.has("designation_name")) {
                            String designation = user.getString("designation_name");
                            isMarketingHead = "Marketing Head".equals(designation);
                        }
                        
                        // Make final for lambda
                        final boolean finalIsRegionalBusinessHead = isRegionalBusinessHead;
                        final boolean finalIsBusinessHead = isBusinessHead;
                        final boolean finalIsMarketingHead = isMarketingHead;
                        
                        Log.d(TAG, "Username: " + username + ", isUser10002: " + isUser10002);
                        Log.d(TAG, "User ID from server: " + userId);
                        Log.d(TAG, "Is Chief Business Officer: " + isChiefBusinessOfficer);
                        Log.d(TAG, "Is Regional Business Head: " + isRegionalBusinessHead);
                        Log.d(TAG, "Is Business Head: " + isBusinessHead);
                        Log.d(TAG, "Is Marketing Head: " + isMarketingHead);
                        Log.d(TAG, "Designation: " + user.optString("designation_name", "N/A"));
                        Log.d(TAG, "Full response: " + responseBody);
                        
                        // Critical check: Ensure we have a valid userId
                        if (finalUserId == null || finalUserId.isEmpty()) {
                            Log.e(TAG, "CRITICAL ERROR: No valid userId found!");
                            Log.e(TAG, "This will cause navigation issues in downstream activities");
                            Log.e(TAG, "Server response may be missing the 'id' field");
                            Log.e(TAG, "Username: " + username + ", userId: " + finalUserId);
                        } else if ("1".equals(finalUserId)) {
                            Log.e(TAG, "CRITICAL ERROR: userId is 1 (KRAJESHK - SuperAdmin)!");
                            Log.e(TAG, "This user is not a Regional Business Head!");
                            Log.e(TAG, "Username: " + username + ", userId: " + finalUserId);
                            
                            // Try to get correct user ID from username for known RBH users
                            if ("93000".equals(username)) {
                                finalUserId = "40"; // SHAIK JEELANI BASHA - Regional Business Head
                                Log.d(TAG, "Fixed: Mapped username 93000 to userId 40");
                            } else if ("chiranjeevi".equals(username)) {
                                finalUserId = "32"; // CHIRANJEEVI NARLAGIRI - Regional Business Head
                                Log.d(TAG, "Fixed: Mapped username chiranjeevi to userId 32");
                            } else {
                                Log.w(TAG, "Unknown username, cannot map to correct userId");
                            }
                        } else {
                            Log.d(TAG, "✓ Valid userId found: " + finalUserId);
                        }
                        
                        // Create final variable for lambda usage after all modifications are done
                        final String finalUserIdForLambda = finalUserId;

                        mainHandler.post(() -> {
                            try {
                                showSuccessMessage("Login successful!");
                                
                                // Save user data to SharedPreferences
                                saveUserDataToSharedPreferences(username, firstName, lastName, finalUserIdForLambda);
                                
                                if (isChiefBusinessOfficer) {
                                    Log.d(TAG, "Navigating to ChiefBusinessOfficerPanelActivity");
                                    // Navigate to Chief Business Officer panel
                                    Intent intent = new Intent(EnhancedLoginActivity.this, ChiefBusinessOfficerPanelActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", finalUserIdForLambda);
                                    startActivity(intent);
                                    finish();
                                } else if (finalIsBusinessHead) {
                                    Log.d(TAG, "Navigating to BusinessHeadPanelActivity");
                                    // Navigate to Business Head panel
                                    Intent intent = new Intent(EnhancedLoginActivity.this, BusinessHeadPanelActivity.class);
                                    intent.putExtra("USERNAME", username);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", finalUserIdForLambda);
                                    startActivity(intent);
                                    finish();
                                } else if (finalIsMarketingHead) {
                                    Log.d(TAG, "Navigating to MarketingHeadPanelActivity");
                                    // Navigate to Marketing Head panel
                                    Intent intent = new Intent(EnhancedLoginActivity.this, MarketingHeadPanelActivity.class);
                                    intent.putExtra("USERNAME", username);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", finalUserIdForLambda);
                                    startActivity(intent);
                                    finish();
                                } else if (finalIsRegionalBusinessHead) {
                                    Log.d(TAG, "Navigating to RegionalBusinessHeadPanelActivity");
                                    // Navigate to Regional Business Head panel
                                    Intent intent = new Intent(EnhancedLoginActivity.this, RegionalBusinessHeadPanelActivity.class);
                                    intent.putExtra("USERNAME", username); // Use actual username instead of displayName
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", finalUserIdForLambda);
                                    startActivity(intent);
                                    finish();
                                } else if (isUser10002 || "10002".equals(finalUserIdForLambda)) {
                                    Log.d(TAG, "Navigating to User10002PanelActivity");
                                    // Navigate to new enhanced panel for user 10002
                                    Intent intent = new Intent(EnhancedLoginActivity.this, User10002PanelActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", finalUserIdForLambda);
                                    intent.putExtra("SPECIAL_MESSAGE", "Welcome to your enhanced dashboard!");
                                    startActivity(intent);
                                    finish();
                                } else if (isSpecialUser) {
                                    // Navigate to existing special panel
                                    Intent intent = new Intent(EnhancedLoginActivity.this, SpecialPanelActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", finalUserIdForLambda);
                                    intent.putExtra("SPECIAL_MESSAGE", specialMessage);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Navigate to regular home
                                    Intent intent = new Intent(EnhancedLoginActivity.this, HomeActivity.class);
                                    intent.putExtra("USERNAME", displayName);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("LAST_NAME", lastName);
                                    intent.putExtra("USER_ID", finalUserIdForLambda);
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
                        mainHandler.post(() -> {
                            showErrorMessage("Login failed: " + error);
                            setLoadingState(false);
                        });
                    }
                } else {
                    Log.e(TAG, "HTTP Error: " + responseCode);
                    mainHandler.post(() -> {
                        showErrorMessage("Server error: " + responseCode);
                        setLoadingState(false);
                    });
                }
                connection.disconnect();
                
            } catch (Exception e) {
                Log.e(TAG, "Exception during login: " + e.getMessage());
                mainHandler.post(() -> {
                    showErrorMessage("Network error: Please check your connection");
                    setLoadingState(false);
                });
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        mainHandler.post(() -> {
            try {
                if (isLoading) {
                    if (loginButton != null) {
                        loginButton.setEnabled(false);
                        loginButton.setText("Logging in...");
                    }
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    if (usernameEditText != null) {
                        usernameEditText.setEnabled(false);
                    }
                    if (passwordEditText != null) {
                        passwordEditText.setEnabled(false);
                    }
                } else {
                    if (loginButton != null) {
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");
                    }
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (usernameEditText != null) {
                        usernameEditText.setEnabled(true);
                    }
                    if (passwordEditText != null) {
                        passwordEditText.setEnabled(true);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in setLoadingState: " + e.getMessage());
            }
        });
    }

    private void showSuccessMessage(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing success message: " + e.getMessage());
        }
    }

    private void showErrorMessage(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing error message: " + e.getMessage());
        }
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
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }
} 