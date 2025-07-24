package com.kfinone.app;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DirectorMyInsuranceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_my_insurance);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.directorInsuranceRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Sample data for demonstration
        List<DirectorInsuranceItem> sampleList = new ArrayList<>();
        sampleList.add(new DirectorInsuranceItem("John Doe", "1234567890", "john@example.com", "password123"));
        sampleList.add(new DirectorInsuranceItem("Jane Smith", "9876543210", "jane@example.com", "secret456"));
        DirectorInsuranceAdapter adapter = new DirectorInsuranceAdapter(sampleList, this);
        recyclerView.setAdapter(adapter);
    }
} 