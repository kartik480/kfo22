package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

public class BanksActivity extends AppCompatActivity {
    private static final String TAG = "BanksActivity";
    private TextInputEditText bankNameInput;
    private MaterialButton addBankButton;
    private LinearLayout bankTableLayout;
    private List<BankItem> banksList;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banks);

        // Initialize executor
        executor = Executors.newSingleThreadExecutor();

        // Initialize views
        initializeViews();
        
        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up add bank button
        addBankButton.setOnClickListener(v -> handleAddBank());

        // Load bank data from database
        loadBankData();
    }

    private void initializeViews() {
        bankNameInput = findViewById(R.id.bankNameInput);
        addBankButton = findViewById(R.id.addBankButton);
        bankTableLayout = findViewById(R.id.bankTableLayout);
        banksList = new ArrayList<>();
    }

    private void loadBankData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                // Use the real database endpoint to fetch from tbl_bank
                String urlString = "https://emp.kfinone.com/mobile/api/get_bank_list.php";
                Log.d(TAG, "Loading bank data from: " + urlString);
                
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
                    Log.d(TAG, "Raw response for bank data: " + responseString);
                    
                    // Check if response starts with HTML (error)
                    if (responseString.trim().startsWith("<")) {
                        Log.e(TAG, "get_bank_list.php returned HTML instead of JSON: " + responseString);
                        runOnUiThread(() -> showError("Server error: get_bank_list.php returned HTML. Check server logs."));
                        return;
                    }
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<BankItem> bankItems = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            String bankName = dataArray.getString(i);
                            BankItem bankItem = new BankItem(i + 1, bankName); // Use index + 1 as ID
                            bankItems.add(bankItem);
                        }
                        
                        runOnUiThread(() -> {
                            populateBankTable(bankItems);
                            showError("Loaded " + bankItems.size() + " banks from database");
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("message");
                        Log.e(TAG, "get_bank_list.php API error: " + errorMessage);
                        runOnUiThread(() -> showError("Failed to load bank data: " + errorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from get_bank_list.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from get_bank_list.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load bank data", e);
                runOnUiThread(() -> showError("Failed to load bank data: " + e.getMessage()));
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

    private void populateBankTable(List<BankItem> bankItems) {
        // Remove existing data rows (keep header)
        int childCount = bankTableLayout.getChildCount();
        for (int i = childCount - 1; i > 0; i--) {
            bankTableLayout.removeViewAt(i);
        }
        
        if (bankItems.isEmpty()) {
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
            noDataText.setText("No banks found");
            noDataText.setTextSize(14);
            noDataText.setTextColor(getResources().getColor(R.color.red));
            noDataText.setGravity(Gravity.CENTER);
            noDataText.setPadding(16, 16, 16, 16);
            
            noDataRow.addView(noDataText);
            bankTableLayout.addView(noDataRow);
            return;
        }
        
        // Add data rows
        for (BankItem item : bankItems) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(48, 48, 48, 48);
            row.setBackgroundResource(R.drawable.edit_text_background);
            
            // Bank Name
            TextView bankNameText = createTableCell(item.getBankName());
            row.addView(bankNameText);
            
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
            
            actionsLayout.addView(editButton);
            actionsLayout.addView(deleteButton);
            row.addView(actionsLayout);
            
            bankTableLayout.addView(row);
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

    private void handleAddBank() {
        String bankName = bankNameInput.getText().toString().trim();
        if (!bankName.isEmpty()) {
            // Send bank data to database
            addBankToDatabase(bankName);
        } else {
            showError("Please enter a bank name");
        }
    }

    private void addBankToDatabase(String bankName) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/add_bank.php";
                Log.d(TAG, "Adding bank to database: " + bankName);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("bank_name", bankName);
                
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
                    Log.d(TAG, "Raw response for adding bank: " + responseString);
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
            bankNameInput.setText("");
                            showError("Bank added successfully: " + bankName);
                            // Reload the bank list
                            loadBankData();
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "add_bank.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to add bank: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from add_bank.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from add_bank.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add bank", e);
                runOnUiThread(() -> showError("Failed to add bank: " + e.getMessage()));
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

    // Data class for Bank items
    private static class BankItem {
        private int id;
        private String bankName;

        public BankItem(int id, String bankName) {
            this.id = id;
            this.bankName = bankName;
        }

        public int getId() { return id; }
        public String getBankName() { return bankName; }
    }
} 
