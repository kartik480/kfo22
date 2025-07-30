package com.kfinone.app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BusinessHeadAddPortfolioActivity extends AppCompatActivity {

    // Customer Details
    private TextInputEditText customerNameInput, companyNameInput, phoneNumberInput, alternativeNumberInput;
    private TextInputEditText emailInput, pinCodeInput, dateOfBirthInput, addressInput;
    private AutoCompleteTextView stateDropdown, locationDropdown, subLocationDropdown;
    private AutoCompleteTextView customerTypeDropdown, industryTypeDropdown, businessTypeDropdown;

    // Document Details
    private TextInputEditText documentNameInput;
    private MaterialButton chooseFileButton, addDocumentButton;
    private TextView selectedFileNameText;

    // Loan Details
    private AutoCompleteTextView bankNameDropdown, loanTypeDropdown, roiDropdown, tenureMonthsDropdown;
    private TextInputEditText loanAmountInput, emiInput, loanAccountNameInput;
    private TextInputEditText firstEmiDateInput, lastEmiDateInput;
    private MaterialButton addLoanButton, submitButton;
    private TextView backButton;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private Uri selectedFileUri;
    
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    // Activity Result Launcher for file picker
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedFileUri = result.getData().getData();
                if (selectedFileUri != null) {
                    String fileName = getFileName(selectedFileUri);
                    selectedFileNameText.setText(fileName);
                    Toast.makeText(this, "File selected: " + fileName, Toast.LENGTH_SHORT).show();
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_add_portfolio);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupDropdowns();
        setupClickListeners();
        setupDatePickers();
    }

    private void initializeViews() {
        // Customer Details
        customerNameInput = findViewById(R.id.customerNameInput);
        companyNameInput = findViewById(R.id.companyNameInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        alternativeNumberInput = findViewById(R.id.alternativeNumberInput);
        emailInput = findViewById(R.id.emailInput);
        stateDropdown = findViewById(R.id.stateDropdown);
        locationDropdown = findViewById(R.id.locationDropdown);
        subLocationDropdown = findViewById(R.id.subLocationDropdown);
        pinCodeInput = findViewById(R.id.pinCodeInput);
        customerTypeDropdown = findViewById(R.id.customerTypeDropdown);
        industryTypeDropdown = findViewById(R.id.industryTypeDropdown);
        businessTypeDropdown = findViewById(R.id.businessTypeDropdown);
        dateOfBirthInput = findViewById(R.id.dateOfBirthInput);
        addressInput = findViewById(R.id.addressInput);

        // Document Details
        documentNameInput = findViewById(R.id.documentNameInput);
        chooseFileButton = findViewById(R.id.chooseFileButton);
        addDocumentButton = findViewById(R.id.addDocumentButton);
        selectedFileNameText = findViewById(R.id.selectedFileNameText);

        // Loan Details
        bankNameDropdown = findViewById(R.id.bankNameDropdown);
        loanTypeDropdown = findViewById(R.id.loanTypeDropdown);
        loanAmountInput = findViewById(R.id.loanAmountInput);
        roiDropdown = findViewById(R.id.roiDropdown);
        tenureMonthsDropdown = findViewById(R.id.tenureMonthsDropdown);
        emiInput = findViewById(R.id.emiInput);
        firstEmiDateInput = findViewById(R.id.firstEmiDateInput);
        lastEmiDateInput = findViewById(R.id.lastEmiDateInput);
        loanAccountNameInput = findViewById(R.id.loanAccountNameInput);
        addLoanButton = findViewById(R.id.addLoanButton);

        // Buttons
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);

        // Initialize calendar
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    private void setupDropdowns() {
        // State options
        String[] states = {"Maharashtra", "Delhi", "Karnataka", "Tamil Nadu", "Gujarat", "Uttar Pradesh", "West Bengal", "Telangana", "Andhra Pradesh", "Kerala"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, states);
        stateDropdown.setAdapter(stateAdapter);

        // Location options
        String[] locations = {"Mumbai", "Delhi", "Bangalore", "Chennai", "Ahmedabad", "Pune", "Hyderabad", "Kolkata", "Jaipur", "Surat"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
        locationDropdown.setAdapter(locationAdapter);

        // Sub Location options
        String[] subLocations = {"Andheri", "Bandra", "Worli", "BKC", "Powai", "Thane", "Navi Mumbai", "Vashi", "Panvel", "Kalyan"};
        ArrayAdapter<String> subLocationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subLocations);
        subLocationDropdown.setAdapter(subLocationAdapter);

        // Customer Type options
        String[] customerTypes = {"Individual", "Business", "Corporate", "SME", "Startup"};
        ArrayAdapter<String> customerTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, customerTypes);
        customerTypeDropdown.setAdapter(customerTypeAdapter);

        // Industry Type options
        String[] industryTypes = {"Technology", "Healthcare", "Finance", "Manufacturing", "Retail", "Education", "Real Estate", "Transportation", "Energy", "Others"};
        ArrayAdapter<String> industryTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, industryTypes);
        industryTypeDropdown.setAdapter(industryTypeAdapter);

        // Business Type options
        String[] businessTypes = {"Sole Proprietorship", "Partnership", "Private Limited", "Public Limited", "LLP", "Cooperative"};
        ArrayAdapter<String> businessTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, businessTypes);
        businessTypeDropdown.setAdapter(businessTypeAdapter);

        // Bank Name options
        String[] bankNames = {"HDFC Bank", "ICICI Bank", "State Bank of India", "Axis Bank", "Kotak Mahindra Bank", "Punjab National Bank", "Bank of Baroda", "Canara Bank", "Union Bank", "IDBI Bank"};
        ArrayAdapter<String> bankNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bankNames);
        bankNameDropdown.setAdapter(bankNameAdapter);

        // Loan Type options
        String[] loanTypes = {"Personal Loan", "Home Loan", "Business Loan", "Vehicle Loan", "Education Loan", "Gold Loan", "Property Loan", "Working Capital Loan"};
        ArrayAdapter<String> loanTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, loanTypes);
        loanTypeDropdown.setAdapter(loanTypeAdapter);

        // ROI options
        String[] roiOptions = {"8.5%", "9.0%", "9.5%", "10.0%", "10.5%", "11.0%", "11.5%", "12.0%", "12.5%", "13.0%"};
        ArrayAdapter<String> roiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roiOptions);
        roiDropdown.setAdapter(roiAdapter);

        // Tenure Months options
        String[] tenureOptions = {"12", "24", "36", "48", "60", "72", "84", "96", "108", "120"};
        ArrayAdapter<String> tenureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tenureOptions);
        tenureMonthsDropdown.setAdapter(tenureAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());

        // File picker
        chooseFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            filePickerLauncher.launch(intent);
        });

        addDocumentButton.setOnClickListener(v -> {
            if (validateDocumentSection()) {
                Toast.makeText(this, "Document added successfully!", Toast.LENGTH_SHORT).show();
                // Clear document fields
                documentNameInput.setText("");
                selectedFileNameText.setText("No file selected");
                selectedFileUri = null;
            }
        });

        addLoanButton.setOnClickListener(v -> {
            if (validateLoanSection()) {
                Toast.makeText(this, "Loan details added successfully!", Toast.LENGTH_SHORT).show();
                // Clear loan fields
                clearLoanFields();
            }
        });

        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                submitPortfolio();
            }
        });
    }

    private void setupDatePickers() {
        // Date of Birth picker
        dateOfBirthInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dateOfBirthInput.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // First EMI Date picker
        firstEmiDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    firstEmiDateInput.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Last EMI Date picker
        lastEmiDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    lastEmiDateInput.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
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

    private boolean validateForm() {
        boolean isValid = true;

        // Validate Customer Section
        if (!validateCustomerSection()) {
            isValid = false;
        }

        // Validate Document Section (at least one document)
        if (!validateDocumentSection()) {
            isValid = false;
        }

        // Validate Loan Section (at least one loan)
        if (!validateLoanSection()) {
            isValid = false;
        }

        return isValid;
    }

    private boolean validateCustomerSection() {
        boolean isValid = true;

        // Customer Name
        String customerName = customerNameInput.getText().toString().trim();
        if (TextUtils.isEmpty(customerName)) {
            customerNameInput.setError("Customer name is required");
            isValid = false;
        } else {
            customerNameInput.setError(null);
        }

        // Phone Number
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberInput.setError("Phone number is required");
            isValid = false;
        } else if (phoneNumber.length() < 10) {
            phoneNumberInput.setError("Invalid phone number");
            isValid = false;
        } else {
            phoneNumberInput.setError(null);
        }

        // Email
        String email = emailInput.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid email format");
            isValid = false;
        } else {
            emailInput.setError(null);
        }

        // State
        String state = stateDropdown.getText().toString().trim();
        if (TextUtils.isEmpty(state)) {
            stateDropdown.setError("State is required");
            isValid = false;
        } else {
            stateDropdown.setError(null);
        }

        // Location
        String location = locationDropdown.getText().toString().trim();
        if (TextUtils.isEmpty(location)) {
            locationDropdown.setError("Location is required");
            isValid = false;
        } else {
            locationDropdown.setError(null);
        }

        // Pin Code
        String pinCode = pinCodeInput.getText().toString().trim();
        if (TextUtils.isEmpty(pinCode)) {
            pinCodeInput.setError("Pin code is required");
            isValid = false;
        } else if (pinCode.length() != 6) {
            pinCodeInput.setError("Pin code must be 6 digits");
            isValid = false;
        } else {
            pinCodeInput.setError(null);
        }

        // Customer Type
        String customerType = customerTypeDropdown.getText().toString().trim();
        if (TextUtils.isEmpty(customerType)) {
            customerTypeDropdown.setError("Customer type is required");
            isValid = false;
        } else {
            customerTypeDropdown.setError(null);
        }

        // Address
        String address = addressInput.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            addressInput.setError("Address is required");
            isValid = false;
        } else {
            addressInput.setError(null);
        }

        return isValid;
    }

    private boolean validateDocumentSection() {
        String documentName = documentNameInput.getText().toString().trim();
        if (TextUtils.isEmpty(documentName)) {
            documentNameInput.setError("Document name is required");
            return false;
        }
        if (selectedFileUri == null) {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateLoanSection() {
        boolean isValid = true;

        // Bank Name
        String bankName = bankNameDropdown.getText().toString().trim();
        if (TextUtils.isEmpty(bankName)) {
            bankNameDropdown.setError("Bank name is required");
            isValid = false;
        } else {
            bankNameDropdown.setError(null);
        }

        // Loan Type
        String loanType = loanTypeDropdown.getText().toString().trim();
        if (TextUtils.isEmpty(loanType)) {
            loanTypeDropdown.setError("Loan type is required");
            isValid = false;
        } else {
            loanTypeDropdown.setError(null);
        }

        // Loan Amount
        String loanAmount = loanAmountInput.getText().toString().trim();
        if (TextUtils.isEmpty(loanAmount)) {
            loanAmountInput.setError("Loan amount is required");
            isValid = false;
        } else {
            loanAmountInput.setError(null);
        }

        // ROI
        String roi = roiDropdown.getText().toString().trim();
        if (TextUtils.isEmpty(roi)) {
            roiDropdown.setError("ROI is required");
            isValid = false;
        } else {
            roiDropdown.setError(null);
        }

        // Tenure Months
        String tenure = tenureMonthsDropdown.getText().toString().trim();
        if (TextUtils.isEmpty(tenure)) {
            tenureMonthsDropdown.setError("Tenure is required");
            isValid = false;
        } else {
            tenureMonthsDropdown.setError(null);
        }

        // First EMI Date
        String firstEmiDate = firstEmiDateInput.getText().toString().trim();
        if (TextUtils.isEmpty(firstEmiDate)) {
            firstEmiDateInput.setError("First EMI date is required");
            isValid = false;
        } else {
            firstEmiDateInput.setError(null);
        }

        // Last EMI Date
        String lastEmiDate = lastEmiDateInput.getText().toString().trim();
        if (TextUtils.isEmpty(lastEmiDate)) {
            lastEmiDateInput.setError("Last EMI date is required");
            isValid = false;
        } else {
            lastEmiDateInput.setError(null);
        }

        // Loan Account Name
        String loanAccountName = loanAccountNameInput.getText().toString().trim();
        if (TextUtils.isEmpty(loanAccountName)) {
            loanAccountNameInput.setError("Loan account name is required");
            isValid = false;
        } else {
            loanAccountNameInput.setError(null);
        }

        return isValid;
    }

    private void clearLoanFields() {
        bankNameDropdown.setText("");
        loanTypeDropdown.setText("");
        loanAmountInput.setText("");
        roiDropdown.setText("");
        tenureMonthsDropdown.setText("");
        emiInput.setText("");
        firstEmiDateInput.setText("");
        lastEmiDateInput.setText("");
        loanAccountNameInput.setText("");
    }

    private void submitPortfolio() {
        // Get all form data
        String customerName = customerNameInput.getText().toString().trim();
        String companyName = companyNameInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String alternativeNumber = alternativeNumberInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String state = stateDropdown.getText().toString().trim();
        String location = locationDropdown.getText().toString().trim();
        String subLocation = subLocationDropdown.getText().toString().trim();
        String pinCode = pinCodeInput.getText().toString().trim();
        String customerType = customerTypeDropdown.getText().toString().trim();
        String industryType = industryTypeDropdown.getText().toString().trim();
        String businessType = businessTypeDropdown.getText().toString().trim();
        String dateOfBirth = dateOfBirthInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        // TODO: Send data to server
        // For now, just show success message
        Toast.makeText(this, "Portfolio created successfully!", Toast.LENGTH_SHORT).show();
        
        // Go back to portfolio panel
        Intent intent = new Intent(this, BusinessHeadPortfolioActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }
    
    private void goBack() {
        Intent intent = new Intent(this, BusinessHeadPortfolioActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }
    
    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }
} 