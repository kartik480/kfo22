package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DirectorProfileActivity extends AppCompatActivity {

    private static final String TAG = "DirectorProfileActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    // UI Elements
    private TextView titleText;
    private Spinner vendorBankSpinner;
    private Spinner loanTypeSpinner;
    private Button filterButton;
    private Button resetButton;
    private ListView profileListView;

    // Data
    private List<VendorBank> vendorBankList;
    private List<LoanType> loanTypeList;
    private List<ProfileItem> profileList;
    private ProfileListAdapter profileAdapter;

    // User data
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        // Additional flags to ensure complete fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        
        setContentView(R.layout.activity_director_profile);

        initializeViews();
        setupUserData();
        setupClickListeners();
        loadVendorBanks();
        loadLoanTypes();
        loadProfiles();
    }

    private void initializeViews() {
        // Top navigation
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        titleText = findViewById(R.id.titleText);
        if (titleText != null) {
            titleText.setText("Profile List");
        }

        // Dropdowns
        vendorBankSpinner = findViewById(R.id.vendorBankSpinner);
        loanTypeSpinner = findViewById(R.id.loanTypeSpinner);

        // Buttons
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);

        // List
        profileListView = findViewById(R.id.profileListView);

        // Initialize lists
        vendorBankList = new ArrayList<>();
        loanTypeList = new ArrayList<>();
        profileList = new ArrayList<>();
        profileAdapter = new ProfileListAdapter(this, profileList);
        profileListView.setAdapter(profileAdapter);
    }

    private void setupUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
        }
    }

    private void setupClickListeners() {
        filterButton.setOnClickListener(v -> {
            filterProfiles();
        });

        resetButton.setOnClickListener(v -> {
            resetFilters();
        });

        profileListView.setOnItemClickListener((parent, view, position, id) -> {
            ProfileItem profile = profileList.get(position);
            Toast.makeText(this, "Opening profile: " + profile.getVendorBank(), Toast.LENGTH_SHORT).show();
            // TODO: Implement profile viewing
        });
    }

    private void loadVendorBanks() {
        String url = BASE_URL + "get_vendor_banks.php";
        
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        vendorBankList.clear();
                        vendorBankList.add(new VendorBank(0, "Select Vendor Bank")); // Default option
                        
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String name = jsonObject.getString("vendor_bank_name");
                            vendorBankList.add(new VendorBank(id, name));
                        }
                        
                        ArrayAdapter<VendorBank> adapter = new ArrayAdapter<VendorBank>(DirectorProfileActivity.this,
                            android.R.layout.simple_spinner_item, vendorBankList) {
                            @Override
                            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                ((TextView) view).setText(vendorBankList.get(position).getName());
                                return view;
                            }
                            
                            @Override
                            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                ((TextView) view).setText(vendorBankList.get(position).getName());
                                return view;
                            }
                        };
                        
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        vendorBankSpinner.setAdapter(adapter);
                        
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing vendor banks JSON", e);
                        Toast.makeText(DirectorProfileActivity.this, "Error parsing vendor banks - using fallback data", Toast.LENGTH_SHORT).show();
                        loadFallbackVendorBanks();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error loading vendor banks", error);
                    Toast.makeText(DirectorProfileActivity.this, "Error loading vendor banks - using fallback data", Toast.LENGTH_SHORT).show();
                    loadFallbackVendorBanks();
                }
            });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void loadLoanTypes() {
        String url = BASE_URL + "get_loan_types.php";
        
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        loanTypeList.clear();
                        loanTypeList.add(new LoanType(0, "Select Loan Type")); // Default option
                        
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String type = jsonObject.getString("loan_type");
                            loanTypeList.add(new LoanType(id, type));
                        }
                        
                        ArrayAdapter<LoanType> adapter = new ArrayAdapter<LoanType>(DirectorProfileActivity.this,
                            android.R.layout.simple_spinner_item, loanTypeList) {
                            @Override
                            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                ((TextView) view).setText(loanTypeList.get(position).getType());
                                return view;
                            }
                            
                            @Override
                            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                ((TextView) view).setText(loanTypeList.get(position).getType());
                                return view;
                            }
                        };
                        
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        loanTypeSpinner.setAdapter(adapter);
                        
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing loan types JSON", e);
                        Toast.makeText(DirectorProfileActivity.this, "Error parsing loan types - using fallback data", Toast.LENGTH_SHORT).show();
                        loadFallbackLoanTypes();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error loading loan types", error);
                    Toast.makeText(DirectorProfileActivity.this, "Error loading loan types - using fallback data", Toast.LENGTH_SHORT).show();
                    loadFallbackLoanTypes();
                }
            });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void loadProfiles() {
        String url = BASE_URL + "get_profiles.php";
        
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        profileList.clear();
                        
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String vendorBank = jsonObject.getString("vendor_bank");
                            String loanType = jsonObject.getString("loan_type");
                            String image = jsonObject.getString("image");
                            String file = jsonObject.getString("file");
                            
                            profileList.add(new ProfileItem(vendorBank, loanType, image, file));
                        }
                        
                        profileAdapter.notifyDataSetChanged();
                        
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing profiles JSON", e);
                        Toast.makeText(DirectorProfileActivity.this, "Error parsing profiles - using fallback data", Toast.LENGTH_SHORT).show();
                        loadFallbackProfiles();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error loading profiles", error);
                    Toast.makeText(DirectorProfileActivity.this, "Error loading profiles - using fallback data", Toast.LENGTH_SHORT).show();
                    loadFallbackProfiles();
                }
            });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void filterProfiles() {
        VendorBank selectedVendorBank = (VendorBank) vendorBankSpinner.getSelectedItem();
        LoanType selectedLoanType = (LoanType) loanTypeSpinner.getSelectedItem();
        
        // TODO: Implement filtering logic based on selected vendor bank and loan type
        Toast.makeText(this, "Filtering profiles...", Toast.LENGTH_SHORT).show();
    }

    private void resetFilters() {
        vendorBankSpinner.setSelection(0);
        loanTypeSpinner.setSelection(0);
        loadProfiles(); // Reload all profiles
        Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
    }
    
    private void loadFallbackVendorBanks() {
        vendorBankList.clear();
        vendorBankList.add(new VendorBank(0, "Select Vendor Bank"));
        vendorBankList.add(new VendorBank(1, "HDFC Bank"));
        vendorBankList.add(new VendorBank(2, "ICICI Bank"));
        vendorBankList.add(new VendorBank(3, "SBI Bank"));
        vendorBankList.add(new VendorBank(4, "Axis Bank"));
        vendorBankList.add(new VendorBank(5, "Kotak Bank"));
        
        ArrayAdapter<VendorBank> adapter = new ArrayAdapter<VendorBank>(this,
            android.R.layout.simple_spinner_item, vendorBankList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setText(vendorBankList.get(position).getName());
                return view;
            }
            
            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setText(vendorBankList.get(position).getName());
                return view;
            }
        };
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vendorBankSpinner.setAdapter(adapter);
    }
    
    private void loadFallbackLoanTypes() {
        loanTypeList.clear();
        loanTypeList.add(new LoanType(0, "Select Loan Type"));
        loanTypeList.add(new LoanType(1, "Personal Loan"));
        loanTypeList.add(new LoanType(2, "Home Loan"));
        loanTypeList.add(new LoanType(3, "Car Loan"));
        loanTypeList.add(new LoanType(4, "Business Loan"));
        loanTypeList.add(new LoanType(5, "Education Loan"));
        
        ArrayAdapter<LoanType> adapter = new ArrayAdapter<LoanType>(this,
            android.R.layout.simple_spinner_item, loanTypeList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setText(loanTypeList.get(position).getType());
                return view;
            }
            
            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setText(loanTypeList.get(position).getType());
                return view;
            }
        };
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loanTypeSpinner.setAdapter(adapter);
    }
    
    private void loadFallbackProfiles() {
        profileList.clear();
        // No fallback profiles - show empty list
        profileAdapter.notifyDataSetChanged();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Data classes
    public static class VendorBank {
        private int id;
        private String name;

        public VendorBank(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }
    }

    public static class LoanType {
        private int id;
        private String type;

        public LoanType(int id, String type) {
            this.id = id;
            this.type = type;
        }

        public int getId() { return id; }
        public String getType() { return type; }
    }

    public static class ProfileItem {
        private String vendorBank;
        private String loanType;
        private String image;
        private String file;

        public ProfileItem(String vendorBank, String loanType, String image, String file) {
            this.vendorBank = vendorBank;
            this.loanType = loanType;
            this.image = image;
            this.file = file;
        }

        public String getVendorBank() { return vendorBank; }
        public String getLoanType() { return loanType; }
        public String getImage() { return image; }
        public String getFile() { return file; }
    }
}
