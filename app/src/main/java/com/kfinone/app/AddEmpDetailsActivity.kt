package com.kfinone.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class AddEmpDetailsActivity : AppCompatActivity() {
    private lateinit var firstNameInput: TextInputEditText
    private lateinit var lastNameInput: TextInputEditText
    private lateinit var empIdInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var personalPhoneInput: TextInputEditText
    private lateinit var personalEmailInput: TextInputEditText
    private lateinit var officialPhoneInput: TextInputEditText
    private lateinit var officialEmailInput: TextInputEditText
    private lateinit var aadhaarInput: TextInputEditText
    private lateinit var panInput: TextInputEditText
    private lateinit var accountNumberInput: TextInputEditText
    private lateinit var ifscInput: TextInputEditText
    private lateinit var branchStateDropdown: AutoCompleteTextView
    private lateinit var branchLocationDropdown: AutoCompleteTextView
    private lateinit var departmentDropdown: AutoCompleteTextView
    private lateinit var designationDropdown: AutoCompleteTextView
    private lateinit var bankNameDropdown: AutoCompleteTextView
    private lateinit var accountTypeDropdown: AutoCompleteTextView
    private lateinit var reportingToDropdown: AutoCompleteTextView
    private lateinit var presentAddressInput: TextInputEditText
    private lateinit var permanentAddressInput: TextInputEditText
    private lateinit var uploadPanButton: MaterialButton
    private lateinit var uploadAadhaarButton: MaterialButton
    private lateinit var uploadBankProofButton: MaterialButton
    private lateinit var uploadEmpImageButton: MaterialButton
    private lateinit var submitButton: MaterialButton

    private var panCardUri: Uri? = null
    private var aadhaarCardUri: Uri? = null
    private var bankProofUri: Uri? = null
    private var empImageUri: Uri? = null

    private val panCardLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            panCardUri = it
            uploadPanButton.text = "Pan Card Uploaded"
        }
    }

    private val aadhaarCardLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            aadhaarCardUri = it
            uploadAadhaarButton.text = "Aadhaar Card Uploaded"
        }
    }

    private val bankProofLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            bankProofUri = it
            uploadBankProofButton.text = "Bank Proof Uploaded"
        }
    }

    private val empImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            empImageUri = it
            uploadEmpImageButton.text = "Employee Image Uploaded"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_emp_details)

        // Set up back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        // Initialize views
        initializeViews()
        setupDropdowns()
        setupUploadButtons()
        setupSubmitButton()
    }

    private fun initializeViews() {
        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        empIdInput = findViewById(R.id.employeeIdInput)
        passwordInput = findViewById(R.id.passwordInput)
        personalPhoneInput = findViewById(R.id.personalPhoneInput)
        personalEmailInput = findViewById(R.id.personalEmailInput)
        officialPhoneInput = findViewById(R.id.officialPhoneInput)
        officialEmailInput = findViewById(R.id.officialEmailInput)
        branchStateDropdown = findViewById(R.id.branchStateInput)
        branchLocationDropdown = findViewById(R.id.branchLocationInput)
        departmentDropdown = findViewById(R.id.departmentInput)
        designationDropdown = findViewById(R.id.designationInput)
        aadhaarInput = findViewById(R.id.aadhaarNumberInput)
        panInput = findViewById(R.id.panNumberInput)
        accountNumberInput = findViewById(R.id.accountNumberInput)
        ifscInput = findViewById(R.id.ifscCodeInput)
        bankNameDropdown = findViewById(R.id.bankNameInput)
        accountTypeDropdown = findViewById(R.id.accountTypeInput)
        uploadPanButton = findViewById(R.id.panCardUploadButton)
        uploadAadhaarButton = findViewById(R.id.aadhaarCardUploadButton)
        uploadBankProofButton = findViewById(R.id.bankProofUploadButton)
        uploadEmpImageButton = findViewById(R.id.employeeImageUploadButton)
        presentAddressInput = findViewById(R.id.presentAddressInput)
        permanentAddressInput = findViewById(R.id.permanentAddressInput)
        reportingToDropdown = findViewById(R.id.reportingToInput)
        submitButton = findViewById(R.id.submitButton)
    }

    private fun setupDropdowns() {
        // Test with hardcoded data first
        testDropdownsWithHardcodedData()
        
        // Add click listeners to test dropdowns
        setupDropdownClickListeners()
        
        // Fetch branch states
        fetchBranchStates()
        
        // Fetch branch locations
        fetchBranchLocations()
        
        // Fetch departments
        fetchDepartments()
        
        // Fetch designations
        fetchDesignations()
        
        // Fetch banks
        fetchBanks()
        
        // Fetch account types
        fetchAccountTypes()
        
        // Fetch reporting users
        fetchReportingUsers()
    }
    
    private fun testDropdownsWithHardcodedData() {
        Log.d("AddEmpDetails", "Testing dropdowns with hardcoded data")
        
        try {
            // Test branch states
            val testStates = listOf("Test State 1", "Test State 2", "Test State 3")
            val statesAdapter = ArrayAdapter(this, R.layout.dropdown_item_selected, testStates)
            statesAdapter.setDropDownViewResource(R.layout.dropdown_item)
            branchStateDropdown.setAdapter(statesAdapter)
            Log.d("AddEmpDetails", "Branch states adapter set with ${testStates.size} items")
            
            // Test departments
            val testDepartments = listOf("Test Dept 1", "Test Dept 2", "Test Dept 3")
            val deptAdapter = ArrayAdapter(this, R.layout.dropdown_item_selected, testDepartments)
            deptAdapter.setDropDownViewResource(R.layout.dropdown_item)
            departmentDropdown.setAdapter(deptAdapter)
            Log.d("AddEmpDetails", "Department adapter set with ${testDepartments.size} items")
            
            // Test banks
            val testBanks = listOf("Test Bank 1", "Test Bank 2", "Test Bank 3")
            val bankAdapter = ArrayAdapter(this, R.layout.dropdown_item_selected, testBanks)
            bankAdapter.setDropDownViewResource(R.layout.dropdown_item)
            bankNameDropdown.setAdapter(bankAdapter)
            Log.d("AddEmpDetails", "Bank adapter set with ${testBanks.size} items")
            
            // Test designations
            val testDesignations = listOf("Test Designation 1", "Test Designation 2", "Test Designation 3")
            val designationAdapter = ArrayAdapter(this, R.layout.dropdown_item_selected, testDesignations)
            designationAdapter.setDropDownViewResource(R.layout.dropdown_item)
            designationDropdown.setAdapter(designationAdapter)
            Log.d("AddEmpDetails", "Designation adapter set with ${testDesignations.size} items")
            
            // Test branch locations
            val testLocations = listOf("Test Location 1", "Test Location 2", "Test Location 3")
            val locationAdapter = ArrayAdapter(this, R.layout.dropdown_item_selected, testLocations)
            locationAdapter.setDropDownViewResource(R.layout.dropdown_item)
            branchLocationDropdown.setAdapter(locationAdapter)
            Log.d("AddEmpDetails", "Branch location adapter set with ${testLocations.size} items")
            
            // Test account types
            val testAccountTypes = listOf("Test Account Type 1", "Test Account Type 2", "Test Account Type 3")
            val accountTypeAdapter = ArrayAdapter(this, R.layout.dropdown_item_selected, testAccountTypes)
            accountTypeAdapter.setDropDownViewResource(R.layout.dropdown_item)
            accountTypeDropdown.setAdapter(accountTypeAdapter)
            Log.d("AddEmpDetails", "Account type adapter set with ${testAccountTypes.size} items")
            
            // Test reporting users
            val testReportingUsers = listOf("Test User 1", "Test User 2", "Test User 3")
            val reportingAdapter = ArrayAdapter(this, R.layout.dropdown_item_selected, testReportingUsers)
            reportingAdapter.setDropDownViewResource(R.layout.dropdown_item)
            reportingToDropdown.setAdapter(reportingAdapter)
            Log.d("AddEmpDetails", "Reporting users adapter set with ${testReportingUsers.size} items")
            
            Log.d("AddEmpDetails", "All hardcoded data set successfully")
            
            // Show a toast to confirm
            Toast.makeText(this, "Test data loaded for dropdowns", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Log.e("AddEmpDetails", "Error setting test data", e)
            Toast.makeText(this, "Error setting test data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupDropdownClickListeners() {
        Log.d("AddEmpDetails", "Setting up dropdown click listeners")
        
        branchStateDropdown.setOnClickListener {
            Log.d("AddEmpDetails", "Branch State dropdown clicked")
            Toast.makeText(this, "Branch State dropdown clicked", Toast.LENGTH_SHORT).show()
            branchStateDropdown.showDropDown()
        }
        
        departmentDropdown.setOnClickListener {
            Log.d("AddEmpDetails", "Department dropdown clicked")
            Toast.makeText(this, "Department dropdown clicked", Toast.LENGTH_SHORT).show()
            departmentDropdown.showDropDown()
        }
        
        bankNameDropdown.setOnClickListener {
            Log.d("AddEmpDetails", "Bank Name dropdown clicked")
            Toast.makeText(this, "Bank Name dropdown clicked", Toast.LENGTH_SHORT).show()
            bankNameDropdown.showDropDown()
        }
    }

    private fun fetchBranchStates() {
        Log.d("AddEmpDetails", "Starting fetchBranchStates")
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://emp.kfinone.com/mobile/api/fetch_branch_states.php")
                Log.d("AddEmpDetails", "Fetching from URL: $url")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                Log.d("AddEmpDetails", "Branch States Response Code: $responseCode")
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("AddEmpDetails", "Branch States Response: $response")
                    val jsonResponse = JSONObject(response)
                    
                    if (jsonResponse.getBoolean("success")) {
                        val statesArray = jsonResponse.getJSONArray("data")
                        val states = mutableListOf<String>()
                        for (i in 0 until statesArray.length()) {
                            states.add(statesArray.getJSONObject(i).getString("branch_state_name"))
                        }
                        
                        Log.d("AddEmpDetails", "Branch States parsed: $states")
                        
                        withContext(Dispatchers.Main) {
                            val adapter = ArrayAdapter(this@AddEmpDetailsActivity, 
                                R.layout.dropdown_item_selected, states)
                            adapter.setDropDownViewResource(R.layout.dropdown_item)
                            branchStateDropdown.setAdapter(adapter)
                            Log.d("AddEmpDetails", "Branch States adapter set successfully")
                        }
                    } else {
                        Log.e("AddEmpDetails", "Failed to fetch branch states: ${jsonResponse.optString("message")}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddEmpDetailsActivity, 
                                "Failed to fetch branch states: ${jsonResponse.optString("message")}", 
                                Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e("AddEmpDetails", "Failed to connect to server. Response code: $responseCode")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddEmpDetailsActivity, 
                            "Failed to connect to server. Response code: $responseCode", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AddEmpDetails", "Error fetching branch states", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddEmpDetailsActivity, 
                        "Error fetching branch states: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun fetchBranchLocations() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://emp.kfinone.com/mobile/api/fetch_branch_locations.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    if (jsonResponse.getBoolean("success")) {
                        val locationsArray = jsonResponse.getJSONArray("data")
                        val locations = mutableListOf<String>()
                        for (i in 0 until locationsArray.length()) {
                            locations.add(locationsArray.getJSONObject(i).getString("branch_location"))
                        }
                        
                        withContext(Dispatchers.Main) {
                            val adapter = ArrayAdapter(this@AddEmpDetailsActivity, 
                                R.layout.dropdown_item_selected, locations)
                            adapter.setDropDownViewResource(R.layout.dropdown_item)
                            branchLocationDropdown.setAdapter(adapter)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddEmpDetailsActivity, 
                                "Failed to fetch branch locations: ${jsonResponse.optString("message")}", 
                                Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddEmpDetailsActivity, 
                            "Failed to connect to server. Response code: $responseCode", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddEmpDetailsActivity, 
                        "Error fetching branch locations: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun fetchDepartments() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://emp.kfinone.com/mobile/api/get_departments.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    if (jsonResponse.getString("status") == "success") {
                        val departmentsArray = jsonResponse.getJSONArray("data")
                        val departments = mutableListOf<String>()
                        for (i in 0 until departmentsArray.length()) {
                            departments.add(departmentsArray.getJSONObject(i).getString("department_name"))
                        }
                        
                        withContext(Dispatchers.Main) {
                            val adapter = ArrayAdapter(this@AddEmpDetailsActivity, 
                                R.layout.dropdown_item_selected, departments)
                            adapter.setDropDownViewResource(R.layout.dropdown_item)
                            departmentDropdown.setAdapter(adapter)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddEmpDetailsActivity, 
                                "Failed to fetch departments: ${jsonResponse.optString("message")}", 
                                Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddEmpDetailsActivity, 
                            "Failed to connect to server. Response code: $responseCode", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddEmpDetailsActivity, 
                        "Error fetching departments: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun fetchDesignations() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://emp.kfinone.com/mobile/api/get_designations.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    if (jsonResponse.getString("status") == "success") {
                        val designationsArray = jsonResponse.getJSONArray("data")
                        val designations = mutableListOf<String>()
                        for (i in 0 until designationsArray.length()) {
                            designations.add(designationsArray.getJSONObject(i).getString("designation_name"))
                        }
                        
                        withContext(Dispatchers.Main) {
                            val adapter = ArrayAdapter(this@AddEmpDetailsActivity, 
                                R.layout.dropdown_item_selected, designations)
                            adapter.setDropDownViewResource(R.layout.dropdown_item)
                            designationDropdown.setAdapter(adapter)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddEmpDetailsActivity, 
                                "Failed to fetch designations: ${jsonResponse.optString("message")}", 
                                Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddEmpDetailsActivity, 
                            "Failed to connect to server. Response code: $responseCode", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddEmpDetailsActivity, 
                        "Error fetching designations: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun fetchBanks() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://emp.kfinone.com/mobile/api/get_bank_list.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    if (jsonResponse.getString("status") == "success") {
                        val banksArray = jsonResponse.getJSONArray("data")
                        val banks = mutableListOf<String>()
                        for (i in 0 until banksArray.length()) {
                            banks.add(banksArray.getString(i))
                        }
                        
                        withContext(Dispatchers.Main) {
                            val adapter = ArrayAdapter(this@AddEmpDetailsActivity, 
                                R.layout.dropdown_item_selected, banks)
                            adapter.setDropDownViewResource(R.layout.dropdown_item)
                            bankNameDropdown.setAdapter(adapter)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddEmpDetailsActivity, 
                                "Failed to fetch banks: ${jsonResponse.optString("message")}", 
                                Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddEmpDetailsActivity, 
                            "Failed to connect to server. Response code: $responseCode", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddEmpDetailsActivity, 
                        "Error fetching banks: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun fetchAccountTypes() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://emp.kfinone.com/mobile/api/get_account_type_list.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    if (jsonResponse.getString("status") == "success") {
                        val accountTypesArray = jsonResponse.getJSONArray("data")
                        val accountTypes = mutableListOf<String>()
                        for (i in 0 until accountTypesArray.length()) {
                            accountTypes.add(accountTypesArray.getString(i))
                        }
                        
                        withContext(Dispatchers.Main) {
                            val adapter = ArrayAdapter(this@AddEmpDetailsActivity, 
                                R.layout.dropdown_item_selected, accountTypes)
                            adapter.setDropDownViewResource(R.layout.dropdown_item)
                            accountTypeDropdown.setAdapter(adapter)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddEmpDetailsActivity, 
                                "Failed to fetch account types: ${jsonResponse.optString("message")}", 
                                Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddEmpDetailsActivity, 
                            "Failed to connect to server. Response code: $responseCode", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddEmpDetailsActivity, 
                        "Error fetching account types: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun fetchReportingUsers() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://emp.kfinone.com/mobile/api/get_reporting_users_list.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    if (jsonResponse.getString("status") == "success") {
                        val usersArray = jsonResponse.getJSONArray("data")
                        val users = mutableListOf<String>()
                        for (i in 0 until usersArray.length()) {
                            users.add(usersArray.getJSONObject(i).getString("full_name"))
                        }
                        
                        withContext(Dispatchers.Main) {
                            val adapter = ArrayAdapter(this@AddEmpDetailsActivity, 
                                R.layout.dropdown_item_selected, users)
                            adapter.setDropDownViewResource(R.layout.dropdown_item)
                            reportingToDropdown.setAdapter(adapter)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddEmpDetailsActivity, 
                                "Failed to fetch reporting users: ${jsonResponse.optString("message")}", 
                                Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddEmpDetailsActivity, 
                            "Failed to connect to server. Response code: $responseCode", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddEmpDetailsActivity, 
                        "Error fetching reporting users: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupUploadButtons() {
        uploadPanButton.setOnClickListener { panCardLauncher.launch("application/pdf") }
        uploadAadhaarButton.setOnClickListener { aadhaarCardLauncher.launch("application/pdf") }
        uploadBankProofButton.setOnClickListener { bankProofLauncher.launch("application/pdf") }
        uploadEmpImageButton.setOnClickListener { empImageLauncher.launch("image/*") }
    }

    private fun setupSubmitButton() {
        submitButton.setOnClickListener {
            if (validateForm()) {
                submitForm()
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        val errors = mutableListOf<String>()

        // For testing, only validate essential fields
        if (empIdInput.text.toString().trim().isEmpty()) {
            errors.add("Employee ID is required")
            isValid = false
        }
        if (firstNameInput.text.toString().trim().isEmpty()) {
            errors.add("First Name is required")
            isValid = false
        }
        if (lastNameInput.text.toString().trim().isEmpty()) {
            errors.add("Last Name is required")
            isValid = false
        }
        if (passwordInput.text.toString().trim().isEmpty()) {
            errors.add("Password is required")
            isValid = false
        }
        if (personalPhoneInput.text.toString().trim().isEmpty()) {
            errors.add("Personal Phone Number is required")
            isValid = false
        }
        if (personalEmailInput.text.toString().trim().isEmpty()) {
            errors.add("Personal Email is required")
            isValid = false
        }

        // Show all validation errors at once
        if (!isValid) {
            val errorMessage = errors.joinToString("\n")
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }

        return isValid
    }

    private fun submitForm() {
        if (!validateForm()) {
            return
        }

        // Get all form values
        val formData = JSONObject().apply {
            put("emp_id", empIdInput.text.toString())
            put("emp_name", "${firstNameInput.text} ${lastNameInput.text}")
            put("emp_email", personalEmailInput.text.toString())
            put("emp_phone", personalPhoneInput.text.toString())
            put("emp_dob", empIdInput.text.toString())
            put("emp_doj", empIdInput.text.toString())
            put("emp_gender", empIdInput.text.toString())
            put("emp_blood_group", empIdInput.text.toString())
            put("emp_marital_status", empIdInput.text.toString())
            put("emp_address", presentAddressInput.text.toString())
            put("emp_permanent_address", permanentAddressInput.text.toString())
            put("emp_city", empIdInput.text.toString())
            put("emp_state", branchStateDropdown.text.toString())
            put("emp_pincode", empIdInput.text.toString())
            put("emp_emergency_contact", empIdInput.text.toString())
            put("emp_emergency_contact_name", empIdInput.text.toString())
            put("emp_emergency_contact_relation", empIdInput.text.toString())
            put("emp_bank_name", bankNameDropdown.text.toString())
            put("emp_bank_account_number", accountNumberInput.text.toString())
            put("emp_bank_ifsc_code", ifscInput.text.toString())
            put("emp_bank_account_type", accountTypeDropdown.text.toString())
            put("emp_bank_branch", empIdInput.text.toString())
            put("emp_bank_city", empIdInput.text.toString())
            put("emp_bank_state", branchStateDropdown.text.toString())
            put("emp_bank_pincode", empIdInput.text.toString())
            put("emp_department", departmentDropdown.text.toString())
            put("emp_designation", designationDropdown.text.toString())
            put("emp_reporting_to", reportingToDropdown.text.toString())
            put("emp_branch_state", branchStateDropdown.text.toString())
            put("emp_branch_location", branchLocationDropdown.text.toString())
            put("aadhaar_number", aadhaarInput.text.toString())
            put("pan_number", panInput.text.toString())
        }

        // Log the request data
        Log.d("AddEmpDetails", "Request URL: https://emp.kfinone.com/mobile/api/add_employee.php")
        Log.d("AddEmpDetails", "Request Data: $formData")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://emp.kfinone.com/mobile/api/add_employee.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                // Write the JSON data to the connection
                val outputStream = connection.outputStream
                val writer = OutputStreamWriter(outputStream)
                writer.write(formData.toString())
                writer.flush()
                writer.close()
                outputStream.close()

                // Get the response
                val responseCode = connection.responseCode
                Log.d("AddEmpDetails", "Response Code: $responseCode")

                val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream.bufferedReader().use { it.readText() }
                }
                Log.d("AddEmpDetails", "Response: $response")

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getString("status") == "success") {
                                Toast.makeText(this@AddEmpDetailsActivity, 
                                    "Employee details added successfully", 
                                    Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                Toast.makeText(this@AddEmpDetailsActivity, 
                                    "Error: ${jsonResponse.optString("message")}", 
                                    Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Log.e("AddEmpDetails", "Error parsing response", e)
                            Toast.makeText(this@AddEmpDetailsActivity, 
                                "Error parsing server response", 
                                Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@AddEmpDetailsActivity, 
                            "Server error: $response", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AddEmpDetails", "Error submitting form", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddEmpDetailsActivity, 
                        "Error: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 