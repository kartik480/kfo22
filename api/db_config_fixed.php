<?php
// Database configuration
$host = "localhost";
$dbname = "kfinone";
$username = "root";
$password = "";

// Test connection
try {
    $conn = new mysqli($host, $username, $password, $dbname);
    if ($conn->connect_error) {
        error_log("Database connection failed: " . $conn->connect_error);
    } else {
        error_log("Database connected successfully");
    }
} catch (Exception $e) {
    error_log("Database connection error: " . $e->getMessage());
}
?> 