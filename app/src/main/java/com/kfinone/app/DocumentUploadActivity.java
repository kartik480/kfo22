package com.kfinone.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DocumentUploadActivity extends AppCompatActivity implements DocumentUploadAdapter.OnDocumentActionListener {

    private static final int PICK_FILE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    
    private TextInputEditText documentNameInput;
    private MaterialCardView fileSelectionCard;
    private TextView fileNameText;
    private MaterialButton submitButton;
    private ImageButton backButton;
    
    // List components
    private TextInputEditText filterDocumentNameInput;
    private MaterialButton filterButton;
    private MaterialButton resetButton;
    private RecyclerView documentListRecyclerView;
    private DocumentUploadAdapter documentAdapter;
    private List<DocumentUploadItem> documentList;
    
    private Uri selectedFileUri;
    private String selectedFilePath;
    private String selectedFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        // Initialize views
        documentNameInput = findViewById(R.id.documentNameInput);
        fileSelectionCard = findViewById(R.id.fileSelectionCard);
        fileNameText = findViewById(R.id.fileNameText);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
        
        // Initialize list views
        filterDocumentNameInput = findViewById(R.id.filterDocumentNameInput);
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);
        documentListRecyclerView = findViewById(R.id.documentListRecyclerView);

        // Initialize document list
        documentList = new ArrayList<>();
        documentAdapter = new DocumentUploadAdapter(this, documentList, this);
        documentListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        documentListRecyclerView.setAdapter(documentAdapter);

        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up file selection
        fileSelectionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickFile();
            }
        });

        // Set up submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDocument();
            }
        });

        // Set up filter button
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterText = filterDocumentNameInput.getText().toString().trim();
                documentAdapter.filterDocuments(filterText);
            }
        });

        // Set up reset button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDocumentNameInput.setText("");
                documentAdapter.resetFilter();
            }
        });

        // Load document list
        loadDocumentList();
    }

    private void loadDocumentList() {
        String url = "https://emp.kfinone.com/mobile/api/fetch_document_uploads.php";
        
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Log the response for debugging
                            Log.d("DocumentUpload", "API Response: " + response);
                            
                            // Check if response starts with HTML error
                            if (response.trim().startsWith("<")) {
                                Log.e("DocumentUpload", "Received HTML error instead of JSON");
                                Toast.makeText(DocumentUploadActivity.this, "Server error: Received HTML response", Toast.LENGTH_LONG).show();
                                return;
                            }
                            
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            
                            if ("success".equals(status)) {
                                JSONArray documentsArray = jsonResponse.getJSONArray("data");
                                List<DocumentUploadItem> newList = new ArrayList<>();
                                
                                for (int i = 0; i < documentsArray.length(); i++) {
                                    JSONObject documentObj = documentsArray.getJSONObject(i);
                                    DocumentUploadItem item = new DocumentUploadItem(
                                        documentObj.getInt("id"),
                                        documentObj.getString("document_name"),
                                        documentObj.getString("uploaded_file")
                                    );
                                    newList.add(item);
                                }
                                
                                documentAdapter.updateDocumentList(newList);
                                
                                // Show count message
                                int count = newList.size();
                                if (count == 0) {
                                    Toast.makeText(DocumentUploadActivity.this, "No documents found", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DocumentUploadActivity.this, "Loaded " + count + " documents", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String message = jsonResponse.getString("message");
                                Toast.makeText(DocumentUploadActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e("DocumentUpload", "JSON parsing error: " + e.getMessage());
                            Log.e("DocumentUpload", "Response was: " + response);
                            Toast.makeText(DocumentUploadActivity.this, "Error parsing server response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("DocumentUpload", "Unexpected error: " + e.getMessage());
                            Toast.makeText(DocumentUploadActivity.this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DocumentUpload", "Volley error: " + error.getMessage());
                        String errorMessage = "Network error";
                        if (error.networkResponse != null) {
                            errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                        }
                        Toast.makeText(DocumentUploadActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void checkPermissionAndPickFile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 
                    PERMISSION_REQUEST_CODE);
        } else {
            pickFile();
        }
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), PICK_FILE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFile();
            } else {
                Toast.makeText(this, "Permission denied to read files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                selectedFileName = getFileName(selectedFileUri);
                selectedFilePath = selectedFileUri.toString();
                fileNameText.setText(selectedFileName);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception e) {
                Log.e("DocumentUpload", "Error getting file name", e);
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void submitDocument() {
        String documentName = documentNameInput.getText().toString().trim();
        
        // Validation
        if (documentName.isEmpty()) {
            documentNameInput.setError("Please enter document name");
            return;
        }
        
        if (selectedFileUri == null) {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        submitButton.setEnabled(false);
        submitButton.setText("Uploading...");

        // Upload to server
        uploadDocumentToServer(documentName, selectedFileUri);
    }

    private void uploadDocumentToServer(String documentName, Uri fileUri) {
        String url = "https://emp.kfinone.com/mobile/api/add_document_upload.php";
        
        // Convert file to base64 string
        final String fileBase64;
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                byte[] fileBytes = new byte[inputStream.available()];
                inputStream.read(fileBytes);
                fileBase64 = android.util.Base64.encodeToString(fileBytes, android.util.Base64.DEFAULT);
                inputStream.close();
            } else {
                Log.e("DocumentUpload", "Error reading file: inputStream is null");
                submitButton.setEnabled(true);
                submitButton.setText("Submit");
                Toast.makeText(this, "Error reading file: inputStream is null", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (Exception e) {
            Log.e("DocumentUpload", "Error reading file", e);
            submitButton.setEnabled(true);
            submitButton.setText("Submit");
            Toast.makeText(this, "Error reading file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        submitButton.setEnabled(true);
                        submitButton.setText("Submit");
                        
                        try {
                            Log.d("DocumentUpload", "Upload response: " + response);
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            
                            if ("success".equals(status)) {
                                Toast.makeText(DocumentUploadActivity.this, message, Toast.LENGTH_LONG).show();
                                // Clear form
                                documentNameInput.setText("");
                                fileNameText.setText("Select a file to upload");
                                selectedFileUri = null;
                                selectedFileName = null;
                                
                                // Reload document list
                                loadDocumentList();
                            } else {
                                Toast.makeText(DocumentUploadActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e("DocumentUpload", "JSON parsing error", e);
                            Log.e("DocumentUpload", "Response was: " + response);
                            Toast.makeText(DocumentUploadActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        submitButton.setEnabled(true);
                        submitButton.setText("Submit");
                        Log.e("DocumentUpload", "Volley error", error);
                        Toast.makeText(DocumentUploadActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("document_name", documentName);
                params.put("uploaded_file", fileBase64);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onDownloadClick(DocumentUploadItem document) {
        Toast.makeText(this, "Download functionality for: " + document.getDocumentName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement actual download functionality
    }

    @Override
    public void onDeleteClick(DocumentUploadItem document) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Document")
                .setMessage("Are you sure you want to delete '" + document.getDocumentName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteDocument(document);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteDocument(DocumentUploadItem document) {
        // For now, just show a message since we don't have a delete API yet
        Toast.makeText(this, "Delete functionality will be implemented later", Toast.LENGTH_SHORT).show();
        
        // TODO: Implement delete API when needed
        // String url = "https://emp.kfinone.com/mobile/api/delete_document_upload.php";
        // ... implement delete API call
    }
} 
