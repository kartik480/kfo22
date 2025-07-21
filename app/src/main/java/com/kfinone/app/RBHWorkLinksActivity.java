package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RBHWorkLinksActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyEmpLinksAdapter adapter;
    private List<MyEmpLinksActivity.IconPermissionItem> iconList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_emp_links);
        ImageButton backButton = findViewById(R.id.backButton);
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText("RBH Work Links");
        backButton.setOnClickListener(v -> finish());
        // Add RecyclerView logic
        recyclerView = findViewById(R.id.recyclerView); // Add a RecyclerView to your layout
        iconList = new ArrayList<>();
        adapter = new MyEmpLinksAdapter(iconList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        // Get userId from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        loadRBHWorkLinks();
    }

    private void loadRBHWorkLinks() {
        if (userId == null || userId.isEmpty()) return;
        String url = "https://emp.kfinone.com/mobile/api/get_user_work_icons.php";
        JSONObject requestBody = new JSONObject();
        try { requestBody.put("userId", userId); } catch (JSONException e) { return; }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            parseRBHWorkIcons(response.getJSONArray("data"));
                        }
                    } catch (JSONException e) { }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) { }
            });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    private void parseRBHWorkIcons(JSONArray data) {
        iconList.clear();
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject icon = data.getJSONObject(i);
                MyEmpLinksActivity.IconPermissionItem item = new MyEmpLinksActivity.IconPermissionItem(
                    null,
                    icon.optString("icon_name", ""),
                    icon.optString("icon_image", ""),
                    icon.optString("icon_description", ""),
                    "Yes",
                    "Work"
                );
                iconList.add(item);
            }
        } catch (JSONException e) { }
        adapter.notifyDataSetChanged();
    }
} 