package com.kfinone.app

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CallingStatusActivity : AppCompatActivity() {
    private val TAG = "CallingStatusActivity"
    private val BASE_URL = "https://pznstudio.shop/kfinone/"
    
    private lateinit var callingStatusInput: TextInputEditText
    private lateinit var submitButton: MaterialButton
    private lateinit var callingStatusRecyclerView: RecyclerView
    private lateinit var callingStatusAdapter: CallingStatusAdapter
    private val callingStatuses = mutableListOf<CallingStatusItem>()
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling_status)

        // Set up back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        // Initialize views
        initializeViews()
        setupRecyclerView()
        setupSubmitButton()
        
        // Load existing calling statuses
        loadCallingStatuses()
    }

    private fun initializeViews() {
        callingStatusInput = findViewById(R.id.callingStatusInput)
        submitButton = findViewById(R.id.submitButton)
        callingStatusRecyclerView = findViewById(R.id.callingStatusRecyclerView)
    }

    private fun setupRecyclerView() {
        callingStatusAdapter = CallingStatusAdapter(callingStatuses)
        callingStatusRecyclerView.layoutManager = LinearLayoutManager(this)
        callingStatusRecyclerView.adapter = callingStatusAdapter
    }

    private fun setupSubmitButton() {
        submitButton.setOnClickListener {
            submitCallingStatus()
        }
    }

    private fun submitCallingStatus() {
        val status = callingStatusInput.text.toString().trim()
        
        if (status.isEmpty()) {
            Toast.makeText(this, "Please enter a calling status", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable submit button to prevent multiple submissions
        submitButton.isEnabled = false
        submitButton.text = "Submitting..."

        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    submitCallingStatusToServer(status)
                }
                
                if (result.success) {
                    Toast.makeText(this@CallingStatusActivity, 
                        "Calling status added successfully!", Toast.LENGTH_SHORT).show()
                    
                    // Clear input
                    callingStatusInput.setText("")
                    
                    // Reload the list to show the new item
                    loadCallingStatuses()
                } else {
                    Toast.makeText(this@CallingStatusActivity, 
                        "Error: ${result.error}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error submitting calling status", e)
                Toast.makeText(this@CallingStatusActivity, 
                    "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                // Re-enable submit button
                submitButton.isEnabled = true
                submitButton.text = "Submit"
            }
        }
    }

    private suspend fun submitCallingStatusToServer(status: String): SubmitResult {
        return withContext(Dispatchers.IO) {
            try {
                // Prepare JSON data
                val jsonData = JSONObject().apply {
                    put("calling_status", status)
                }

                // Create request body
                val body = RequestBody.create(
                    jsonData.toString(),
                    MediaType.parse("application/json; charset=utf-8")
                )

                // Create request
                val request = Request.Builder()
                    .url(BASE_URL + "add_calling_status.php")
                    .post(body)
                    .build()

                // Execute request
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string() ?: ""
                    Log.d(TAG, "Response: $responseBody")

                    if (response.isSuccessful) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            if (jsonResponse.getBoolean("success")) {
                                SubmitResult(success = true)
                            } else {
                                val error = jsonResponse.optString("error", "Unknown error occurred")
                                SubmitResult(success = false, error = error)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing response", e)
                            SubmitResult(success = false, error = "Error parsing server response")
                        }
                    } else {
                        SubmitResult(success = false, error = "Server error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network error", e)
                SubmitResult(success = false, error = e.message ?: "Network error")
            }
        }
    }

    private fun loadCallingStatuses() {
        Log.d(TAG, "Starting to load calling statuses...")
        
        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    loadCallingStatusesFromServer()
                }
                
                if (result.success) {
                    callingStatuses.clear()
                    callingStatuses.addAll(result.data)
                    callingStatusAdapter.notifyDataSetChanged()
                    
                    if (result.data.isEmpty()) {
                        Toast.makeText(this@CallingStatusActivity, 
                            "No calling statuses found in database", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@CallingStatusActivity, 
                            "Loaded ${result.data.size} calling statuses", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CallingStatusActivity, 
                        "Error loading calling statuses: ${result.error}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading calling statuses", e)
                Toast.makeText(this@CallingStatusActivity, 
                    "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun loadCallingStatusesFromServer(): LoadResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Creating network request...")
                
                // Create request
                val request = Request.Builder()
                    .url(BASE_URL + "get_calling_status_list.php")
                    .get()
                    .build()

                Log.d(TAG, "Executing network request to: ${BASE_URL}get_calling_status_list.php")

                // Execute request
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string() ?: ""
                    Log.d(TAG, "Load Response Code: ${response.code()}")
                    Log.d(TAG, "Load Response: $responseBody")

                    if (response.isSuccessful) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            Log.d(TAG, "Parsed JSON response: ${jsonResponse}")
                            
                            if (jsonResponse.getBoolean("success")) {
                                val dataArray = jsonResponse.getJSONArray("data")
                                Log.d(TAG, "Found ${dataArray.length()} calling statuses")
                                
                                val newCallingStatuses = mutableListOf<CallingStatusItem>()
                                
                                for (i in 0 until dataArray.length()) {
                                    val item = dataArray.getJSONObject(i)
                                    val id = item.getInt("id")
                                    val callingStatus = item.getString("calling_status")
                                    newCallingStatuses.add(CallingStatusItem(id, callingStatus))
                                    Log.d(TAG, "Added calling status: $callingStatus (ID: $id)")
                                }
                                
                                LoadResult(success = true, data = newCallingStatuses)
                            } else {
                                val error = jsonResponse.optString("error", "Unknown error occurred")
                                Log.e(TAG, "Server returned error: $error")
                                LoadResult(success = false, error = error)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing response", e)
                            LoadResult(success = false, error = "Error parsing server response")
                        }
                    } else {
                        LoadResult(success = false, error = "Server error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network error", e)
                LoadResult(success = false, error = e.message ?: "Network error")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    // Data classes for results
    data class SubmitResult(val success: Boolean, val error: String = "")
    data class LoadResult(val success: Boolean, val data: List<CallingStatusItem> = emptyList(), val error: String = "")
} 