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

public class RegionalBusinessHeadTrainingVideoActivity extends AppCompatActivity {

    private static final String TAG = "RegionalBusinessHeadTrainingVideoActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    // UI Elements
    private TextView titleText;
    private Spinner videoCategorySpinner;
    private Button filterButton;
    private Button resetButton;
    private ListView videoListView;

    // Data
    private List<VideoCategory> videoCategoryList;
    private List<DirectorTrainingVideoActivity.TrainingVideoItem> videoList;
    private TrainingVideoListAdapter videoAdapter;

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
        
        setContentView(R.layout.activity_director_training_video);

        initializeViews();
        setupUserData();
        setupClickListeners();
        loadVideoCategories();
        loadTrainingVideos();
    }

    private void initializeViews() {
        // Top navigation
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        titleText = findViewById(R.id.titleText);
        if (titleText != null) {
            titleText.setText("Training Video List");
        }

        // Dropdown
        videoCategorySpinner = findViewById(R.id.videoCategorySpinner);

        // Buttons
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);

        // List
        videoListView = findViewById(R.id.videoListView);

        // Initialize lists
        videoCategoryList = new ArrayList<>();
        videoList = new ArrayList<>();
        videoAdapter = new TrainingVideoListAdapter(this, videoList);
        videoListView.setAdapter(videoAdapter);
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
            filterVideos();
        });

        resetButton.setOnClickListener(v -> {
            resetFilters();
        });

        videoListView.setOnItemClickListener((parent, view, position, id) -> {
            DirectorTrainingVideoActivity.TrainingVideoItem video = videoList.get(position);
            Toast.makeText(this, "Playing video: " + video.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Implement video playback
        });
    }

    private void loadVideoCategories() {
        String url = BASE_URL + "get_video_categories.php";
        
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        videoCategoryList.clear();
                        videoCategoryList.add(new VideoCategory(0, "Select Video Category")); // Default option
                        
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String name = jsonObject.getString("category_name");
                            videoCategoryList.add(new VideoCategory(id, name));
                        }
                        
                        ArrayAdapter<VideoCategory> adapter = new ArrayAdapter<VideoCategory>(RegionalBusinessHeadTrainingVideoActivity.this,
                            android.R.layout.simple_spinner_item, videoCategoryList) {
                            @Override
                            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                ((TextView) view).setText(videoCategoryList.get(position).getName());
                                return view;
                            }
                            
                            @Override
                            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                ((TextView) view).setText(videoCategoryList.get(position).getName());
                                return view;
                            }
                        };
                        
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        videoCategorySpinner.setAdapter(adapter);
                        
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing video categories JSON", e);
                        Toast.makeText(RegionalBusinessHeadTrainingVideoActivity.this, "Error parsing video categories - using fallback data", Toast.LENGTH_SHORT).show();
                        loadFallbackVideoCategories();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error loading video categories", error);
                    Toast.makeText(RegionalBusinessHeadTrainingVideoActivity.this, "Error loading video categories - using fallback data", Toast.LENGTH_SHORT).show();
                    loadFallbackVideoCategories();
                }
            });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void loadTrainingVideos() {
        String url = BASE_URL + "get_training_videos.php";
        
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        videoList.clear();
                        
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String name = jsonObject.getString("name");
                            String category = jsonObject.getString("category");
                            String videoUrl = jsonObject.getString("video_url");
                            
                            videoList.add(new DirectorTrainingVideoActivity.TrainingVideoItem(name, category, videoUrl));
                        }
                        
                        videoAdapter.notifyDataSetChanged();
                        
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing training videos JSON", e);
                        Toast.makeText(RegionalBusinessHeadTrainingVideoActivity.this, "Error parsing training videos - using fallback data", Toast.LENGTH_SHORT).show();
                        loadFallbackTrainingVideos();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error loading training videos", error);
                    Toast.makeText(RegionalBusinessHeadTrainingVideoActivity.this, "Error loading training videos - using fallback data", Toast.LENGTH_SHORT).show();
                    loadFallbackTrainingVideos();
                }
            });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void filterVideos() {
        VideoCategory selectedCategory = (VideoCategory) videoCategorySpinner.getSelectedItem();
        
        // TODO: Implement filtering logic based on selected category
        Toast.makeText(this, "Filtering videos...", Toast.LENGTH_SHORT).show();
    }

    private void resetFilters() {
        videoCategorySpinner.setSelection(0);
        loadTrainingVideos(); // Reload all videos
        Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
    }
    
    private void loadFallbackVideoCategories() {
        videoCategoryList.clear();
        videoCategoryList.add(new VideoCategory(0, "Select Video Category"));
        videoCategoryList.add(new VideoCategory(1, "Product Training"));
        videoCategoryList.add(new VideoCategory(2, "Sales Training"));
        videoCategoryList.add(new VideoCategory(3, "Customer Service"));
        videoCategoryList.add(new VideoCategory(4, "Compliance Training"));
        videoCategoryList.add(new VideoCategory(5, "Leadership Training"));
        
        ArrayAdapter<VideoCategory> adapter = new ArrayAdapter<VideoCategory>(this,
            android.R.layout.simple_spinner_item, videoCategoryList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setText(videoCategoryList.get(position).getName());
                return view;
            }
            
            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setText(videoCategoryList.get(position).getName());
                return view;
            }
        };
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        videoCategorySpinner.setAdapter(adapter);
    }
    
    private void loadFallbackTrainingVideos() {
        videoList.clear();
        // No fallback videos - show empty list
        videoAdapter.notifyDataSetChanged();
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
    public static class VideoCategory {
        private int id;
        private String name;

        public VideoCategory(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }
    }
}
