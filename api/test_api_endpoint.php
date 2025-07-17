<?php
// Test the API endpoint directly
echo "=== Testing API Endpoint ===\n\n";

$url = "https://pznstudio.shop/kfinone/fetch_sdsa_reporting_users.php";
echo "Testing URL: " . $url . "\n\n";

// Test using cURL
if (function_exists('curl_init')) {
    echo "Using cURL...\n";
    
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_TIMEOUT, 10);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    
    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $error = curl_error($ch);
    curl_close($ch);
    
    echo "HTTP Code: " . $httpCode . "\n";
    if ($error) {
        echo "cURL Error: " . $error . "\n";
    }
    echo "Response: " . $response . "\n\n";
    
    if ($httpCode == 200) {
        $data = json_decode($response, true);
        if ($data && isset($data['status'])) {
            if ($data['status'] == 'success') {
                echo "✅ API is working!\n";
                echo "Users found: " . count($data['data']) . "\n";
                if (isset($data['data']) && is_array($data['data'])) {
                    foreach ($data['data'] as $user) {
                        echo "- " . $user['name'] . " (ID: " . $user['id'] . ")\n";
                    }
                }
            } else {
                echo "❌ API returned error: " . ($data['message'] ?? 'Unknown error') . "\n";
            }
        } else {
            echo "❌ Invalid JSON response\n";
        }
    } else {
        echo "❌ HTTP request failed with code: " . $httpCode . "\n";
    }
} else {
    echo "cURL not available, trying file_get_contents...\n";
    
    $context = stream_context_create([
        'http' => [
            'timeout' => 10,
            'ignore_errors' => true
        ]
    ]);
    
    $response = file_get_contents($url, false, $context);
    if ($response === false) {
        echo "❌ Failed to fetch URL\n";
    } else {
        echo "Response: " . $response . "\n";
    }
}
?> 