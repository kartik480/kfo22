package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DirectorMyPartnerActivity extends AppCompatActivity {
    private static final String TAG = "DirectorMyPartner";
    private RecyclerView partnerRecyclerView;
    private PartnerAdapter adapter;
    private List<PartnerItem> partnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_my_partner);
        partnerRecyclerView = findViewById(R.id.partnerRecyclerView);
        partnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partnerList = new ArrayList<>();
        adapter = new PartnerAdapter(partnerList);
        partnerRecyclerView.setAdapter(adapter);
        TextView headingText = findViewById(R.id.headingText);
        boolean showList = getIntent().getBooleanExtra("SHOW_LIST", false);
        if (showList) {
            loadPartnerData();
            partnerRecyclerView.setVisibility(View.VISIBLE);
            headingText.setText("Active Partner List");
        } else {
            partnerRecyclerView.setVisibility(View.GONE);
            headingText.setText("No partners yet. Add a partner to see the list.");
        }
    }

    private void loadPartnerData() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/director_get_partner_list.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    JSONObject json = new JSONObject(response.toString());
                    JSONArray data = json.optJSONArray("data");
                    if (data != null) {
                        partnerList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject partner = data.getJSONObject(i);
                            partnerList.add(new PartnerItem(
                                partner.optString("first_name", ""),
                                partner.optString("last_name", ""),
                                partner.optString("Phone_number", ""),
                                partner.optString("email_id", ""),
                                partner.optString("password", "")
                            ));
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } else {
                        runOnUiThread(() -> Toast.makeText(DirectorMyPartnerActivity.this, "No partners found.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(DirectorMyPartnerActivity.this, "Failed to load partners.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading partners", e);
                runOnUiThread(() -> Toast.makeText(DirectorMyPartnerActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private static class PartnerItem {
        String firstName, lastName, phone, email, password;
        PartnerItem(String firstName, String lastName, String phone, String email, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.email = email;
            this.password = password;
        }
    }

    private class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.ViewHolder> {
        private List<PartnerItem> partnerItems;
        PartnerAdapter(List<PartnerItem> partnerItems) {
            this.partnerItems = partnerItems;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_director_partner, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PartnerItem item = partnerItems.get(position);
            holder.nameText.setText("Name: " + item.firstName + " " + item.lastName);
            holder.phoneText.setText("Phone: " + item.phone);
            holder.emailText.setText("Email: " + item.email);
            holder.passwordText.setText("Password: " + item.password);
            holder.actionButton.setOnClickListener(v -> {
                // TODO: Action for partner row
            });
        }
        @Override
        public int getItemCount() {
            return partnerItems.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, phoneText, emailText, passwordText;
            Button actionButton;
            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.nameText);
                phoneText = view.findViewById(R.id.phoneText);
                emailText = view.findViewById(R.id.emailText);
                passwordText = view.findViewById(R.id.passwordText);
                actionButton = view.findViewById(R.id.actionButton);
            }
        }
    }
} 