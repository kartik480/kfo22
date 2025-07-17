package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
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

public class AccountTypeActivity extends AppCompatActivity {
    private static final String TAG = "AccountTypeActivity";
    private TextInputEditText accountTypeInput;
    private MaterialButton submitButton;
    private LinearLayout accountTypeTableLayout;
    private List<AccountTypeItem> accountTypesList;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);

        // Initialize executor
        executor = Executors.newSingleThreadExecutor();

        // Initialize views
        initializeViews();
        
        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up submit button
        submitButton.setOnClickListener(v -> handleSubmit());

        // Load account type data from database
        loadAccountTypeData();
    }

    private void initializeViews() {
        accountTypeInput = findViewById(R.id.accountTypeInput);
        submitButton = findViewById(R.id.submitButton);
        accountTypeTableLayout = findViewById(R.id.accountTypeTableLayout);
        accountTypesList = new ArrayList<>();
    }

    private void loadAccountTypeData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_account_type_list.php";
                Log.d(TAG, "Loading account type data from: " + urlString);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Raw response for account type data: " + responseString);
                    
                    // Check if response starts with HTML (error)
                    if (responseString.trim().startsWith("<")) {
                        Log.e(TAG, "get_account_type_list.php returned HTML instead of JSON: " + responseString);
                        runOnUiThread(() -> showError("Server error: get_account_type_list.php returned HTML. Check server logs."));
                        return;
                    }
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<AccountTypeItem> accountTypeItems = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject accountTypeObj = dataArray.getJSONObject(i);
                            int id = accountTypeObj.getInt("id");
                            String accountTypeName = accountTypeObj.getString("account_type");
                            AccountTypeItem accountTypeItem = new AccountTypeItem(id, accountTypeName);
                            accountTypeItems.add(accountTypeItem);
                        }
                        
                        runOnUiThread(() -> {
                            populateAccountTypeTable(accountTypeItems);
                            showError("Loaded " + accountTypeItems.size() + " account types from database");
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("message");
                        Log.e(TAG, "get_account_type_list.php API error: " + errorMessage);
                        runOnUiThread(() -> showError("Failed to load account type data: " + errorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from get_account_type_list.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from get_account_type_list.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load account type data", e);
                runOnUiThread(() -> showError("Failed to load account type data: " + e.getMessage()));
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

    private void populateAccountTypeTable(List<AccountTypeItem> accountTypeItems) {
        // Remove existing data rows (keep header)
        int childCount = accountTypeTableLayout.getChildCount();
        for (int i = childCount - 1; i > 0; i--) {
            accountTypeTableLayout.removeViewAt(i);
        }
        
        if (accountTypeItems.isEmpty()) {
            // Add a "No data found" row
            LinearLayout noDataRow = new LinearLayout(this);
            noDataRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            noDataRow.setOrientation(LinearLayout.HORIZONTAL);
            noDataRow.setPadding(48, 48, 48, 48);
            noDataRow.setBackgroundResource(R.drawable.edit_text_background);
            
            TextView noDataText = new TextView(this);
            noDataText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            noDataText.setText("No account types found");
            noDataText.setTextSize(14);
            noDataText.setTextColor(getResources().getColor(R.color.red));
            noDataText.setGravity(Gravity.CENTER);
            noDataText.setPadding(16, 16, 16, 16);
            
            noDataRow.addView(noDataText);
            accountTypeTableLayout.addView(noDataRow);
            return;
        }
        
        // Add data rows
        for (AccountTypeItem item : accountTypeItems) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(48, 48, 48, 48);
            row.setBackgroundResource(R.drawable.edit_text_background);
            
            // Account Type Name
            TextView accountTypeNameText = createTableCell(item.getAccountType());
            row.addView(accountTypeNameText);
            
            // Status
            TextView statusText = createTableCell("Active");
            row.addView(statusText);
            
            // Actions
            LinearLayout actionsLayout = new LinearLayout(this);
            actionsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
            ));
            actionsLayout.setOrientation(LinearLayout.HORIZONTAL);
            actionsLayout.setGravity(Gravity.CENTER);
            
            ImageButton editButton = new ImageButton(this);
            editButton.setLayoutParams(new LinearLayout.LayoutParams(96, 96));
            editButton.setImageResource(R.drawable.ic_edit);
            editButton.setBackgroundResource(android.R.drawable.btn_default);
            editButton.setContentDescription("Edit");
            editButton.setPadding(16, 16, 16, 16);
            editButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
            
            ImageButton deleteButton = new ImageButton(this);
            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(96, 96));
            deleteButton.setImageResource(R.drawable.ic_delete);
            deleteButton.setBackgroundResource(android.R.drawable.btn_default);
            deleteButton.setContentDescription("Delete");
            deleteButton.setPadding(16, 16, 16, 16);
            deleteButton.setColorFilter(getResources().getColor(R.color.red));
            
            // Add delete functionality
            final String accountTypeToDelete = item.getAccountType();
            deleteButton.setOnClickListener(v -> deleteAccountType(accountTypeToDelete));
            
            actionsLayout.addView(editButton);
            actionsLayout.addView(deleteButton);
            row.addView(actionsLayout);
            
            accountTypeTableLayout.addView(row);
        }
    }

    private TextView createTableCell(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1
        ));
        textView.setText(text);
        textView.setTextSize(12);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setGravity(Gravity.CENTER);
        textView.setMaxLines(2);
        textView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        return textView;
    }

    private void handleSubmit() {
        String accountType = accountTypeInput.getText().toString().trim();
        if (!accountType.isEmpty()) {
            // Send account type data to database
            addAccountTypeToDatabase(accountType);
        } else {
            showError("Please enter an account type");
        }
    }

    private void addAccountTypeToDatabase(String accountType) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/add_account_type.php";
                Log.d(TAG, "Adding account type to database: " + accountType);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("account_type", accountType);
                
                // Send data
                String jsonString = jsonData.toString();
                connection.getOutputStream().write(jsonString.getBytes("UTF-8"));
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Raw response for adding account type: " + responseString);
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
            accountTypeInput.setText("");
                            showError("Account type added successfully: " + accountType);
                            // Reload the account type list
                            loadAccountTypeData();
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "add_account_type.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to add account type: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from add_account_type.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from add_account_type.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add account type", e);
                runOnUiThread(() -> showError("Failed to add account type: " + e.getMessage()));
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

    private void deleteAccountType(String accountType) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/delete_account_type.php";
                Log.d(TAG, "Deleting account type from database: " + accountType);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("account_type", accountType);
                
                // Send data
                String jsonString = jsonData.toString();
                connection.getOutputStream().write(jsonString.getBytes("UTF-8"));
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Raw response for deleting account type: " + responseString);
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
                            showError("Account type deleted successfully: " + accountType);
                            // Reload the account type list
                            loadAccountTypeData();
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "delete_account_type.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to delete account type: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from delete_account_type.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from delete_account_type.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to delete account type", e);
                runOnUiThread(() -> showError("Failed to delete account type: " + e.getMessage()));
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

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

    // Data class for Account Type items
    private static class AccountTypeItem {
        private int id;
        private String accountType;

        public AccountTypeItem(int id, String accountType) {
            this.id = id;
            this.accountType = accountType;
        }

        public int getId() { return id; }
        public String getAccountType() { return accountType; }
    }
} 
