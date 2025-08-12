package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RBHDocumentCheckListActivity extends AppCompatActivity {

    private static final String TAG = "RBHDocumentCheckListActivity";

    private RecyclerView documentRecyclerView;
    private DocumentAdapter documentAdapter;
    private List<DocCheckListActivity.DocumentItem> documentList;
    private List<DocCheckListActivity.DocumentItem> filteredList;

    private TextInputEditText documentNameInput;
    private Button filterButton, resetButton;
    private LinearLayout emptyStateLayout, loadingLayout;
    private TextView backButton;
    private View refreshButton;
    
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_document_check_list);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        setupSearchAndFilter();
        loadDocumentData();
    }

    private void initializeViews() {
        documentRecyclerView = findViewById(R.id.documentRecyclerView);
        documentNameInput = findViewById(R.id.documentNameInput);
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);

        // Setup RecyclerView
        documentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        documentList = new ArrayList<>();
        filteredList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(filteredList, this);
        documentRecyclerView.setAdapter(documentAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        filterButton.setOnClickListener(v -> filterDocuments());
        resetButton.setOnClickListener(v -> resetFilter());
    }

    private void setupSearchAndFilter() {
        documentNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Real-time search as user types
                searchDocuments(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void goBack() {
        Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing document list...", Toast.LENGTH_SHORT).show();
        loadDocumentData();
    }

    private void filterDocuments() {
        String searchQuery = documentNameInput.getText().toString().trim();
        if (searchQuery.isEmpty()) {
            Toast.makeText(this, "Please enter a document name to filter", Toast.LENGTH_SHORT).show();
            return;
        }
        searchDocuments(searchQuery);
        Toast.makeText(this, "Filtered by: " + searchQuery, Toast.LENGTH_SHORT).show();
    }

    private void resetFilter() {
        documentNameInput.setText("");
        filteredList.clear();
        filteredList.addAll(documentList);
        documentAdapter.notifyDataSetChanged();
        updateEmptyState();
        Toast.makeText(this, "Filter reset", Toast.LENGTH_SHORT).show();
    }

    private void searchDocuments(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(documentList);
        } else {
            for (DocCheckListActivity.DocumentItem document : documentList) {
                if (document.getName().toLowerCase().contains(query.toLowerCase()) ||
                    document.getDocumentName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(document);
                }
            }
        }
        documentAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void loadDocumentData() {
        showLoading(true);
        
        // Fetch data from server
        new Thread(() -> {
            try {
                Log.d(TAG, "Fetching document data from server");
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_document_upload.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Document data response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Document data response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        
                        documentList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject document = data.getJSONObject(i);
                            documentList.add(new DocCheckListActivity.DocumentItem(
                                document.getString("document_name"),
                                document.getString("uploaded_file"),
                                document.optString("uploaded_file", "") // Use uploaded_file as download URL
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            filteredList.clear();
                            filteredList.addAll(documentList);
                            documentAdapter.notifyDataSetChanged();
                            showLoading(false);
                            updateEmptyState();
                            Log.d(TAG, "Updated document list with " + documentList.size() + " documents");
                            Toast.makeText(RBHDocumentCheckListActivity.this, 
                                "Loaded " + documentList.size() + " documents", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(RBHDocumentCheckListActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(RBHDocumentCheckListActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading document data: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(RBHDocumentCheckListActivity.this, "Error loading documents: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadSampleData() {
        documentList.clear();
        // Only fetch real data here. No sample data.
        // documentAdapter.notifyDataSetChanged();
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingLayout.setVisibility(View.VISIBLE);
            documentRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.GONE);
        } else {
            loadingLayout.setVisibility(View.GONE);
            documentRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            documentRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            documentRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

}
