package com.kfinone.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PayoutTypeActivity extends AppCompatActivity {
    private EditText payoutNameInput;
    private Button submitButton;
    private RecyclerView payoutTypeRecyclerView;
    private PayoutTypeItemAdapter payoutTypeAdapter;
    private List<PayoutTypeItem> payoutTypeList;
    private static final String FETCH_PAYOUT_TYPES_URL = "https://emp.kfinone.com/mobile/api/fetch_payout_types.php";
    private static final String TEST_URL = "https://emp.kfinone.com/mobile/api/test_db.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout_type);

        // Initialize views
        payoutNameInput = findViewById(R.id.payoutNameInput);
        submitButton = findViewById(R.id.submitButton);
        payoutTypeRecyclerView = findViewById(R.id.payoutTypeRecyclerView);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize RecyclerView
        payoutTypeList = new ArrayList<>();
        payoutTypeAdapter = new PayoutTypeItemAdapter(payoutTypeList);
        payoutTypeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        payoutTypeRecyclerView.setAdapter(payoutTypeAdapter);

        // Set up submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payoutName = payoutNameInput.getText().toString().trim();
                if (!payoutName.isEmpty()) {
                    // TODO: Implement submit functionality
                    Toast.makeText(PayoutTypeActivity.this, "Payout Type: " + payoutName, Toast.LENGTH_SHORT).show();
                    payoutNameInput.setText("");
                    fetchPayoutTypes(); // Refresh the list
                } else {
                    Toast.makeText(PayoutTypeActivity.this, "Please enter payout name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Test network connection first
        testNetworkConnection();
        
        // Fetch payout types
        fetchPayoutTypes();
    }

    private void testNetworkConnection() {
        RequestQueue queue = Volley.newRequestQueue(this);
        
        StringRequest stringRequest = new StringRequest(Request.Method.GET, TEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(PayoutTypeActivity.this, "Network OK - DB Test: " + response.substring(0, Math.min(50, response.length())), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PayoutTypeActivity.this, "Network test failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Set timeout to 30 seconds
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000, // 30 seconds timeout
                0, // no retries
                1.0f // no backoff multiplier
        ));

        queue.add(stringRequest);
    }

    private void fetchPayoutTypes() {
        RequestQueue queue = Volley.newRequestQueue(this);
        
        // Add more detailed logging
        Toast.makeText(PayoutTypeActivity.this, "Fetching from: " + FETCH_PAYOUT_TYPES_URL, Toast.LENGTH_LONG).show();
        
        StringRequest stringRequest = new StringRequest(Request.Method.GET, FETCH_PAYOUT_TYPES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            
                            if ("success".equals(status)) {
                                JSONArray data = jsonResponse.getJSONArray("data");
                                payoutTypeList.clear();
                                
                                Toast.makeText(PayoutTypeActivity.this, "Fetched " + data.length() + " payout types", Toast.LENGTH_SHORT).show();
                                
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject = data.getJSONObject(i);
                                    String payoutName = jsonObject.getString("payout_name");
                                    String payoutStatus = jsonObject.getString("status");
                                    
                                    PayoutTypeItem item = new PayoutTypeItem(payoutName, payoutStatus);
                                    payoutTypeList.add(item);
                                }
                                payoutTypeAdapter.notifyDataSetChanged();
                            } else {
                                String message = jsonResponse.getString("message");
                                Toast.makeText(PayoutTypeActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PayoutTypeActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error fetching data";
                        if (error.networkResponse != null) {
                            errorMessage += " - Status: " + error.networkResponse.statusCode;
                        }
                        if (error.getMessage() != null) {
                            errorMessage += " - " + error.getMessage();
                        }
                        Toast.makeText(PayoutTypeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });

        // Set timeout to 30 seconds
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000, // 30 seconds timeout
                0, // no retries
                1.0f // no backoff multiplier
        ));

        queue.add(stringRequest);
    }
} 
