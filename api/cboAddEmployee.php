<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$response = [
    'branch_states' => [],
    'branch_locations' => [],
    'bank_names' => [],
    'account_types' => [],
    'reporting_to' => [],
    'error' => null
];

try {
    $pdo = getConnection();

    // Branch States
    $stmt = $pdo->query('SELECT branch_state_name FROM tbl_branch_state');
    $response['branch_states'] = $stmt->fetchAll(PDO::FETCH_COLUMN);

    // Branch Locations
    $stmt = $pdo->query('SELECT branch_location FROM tbl_branch_location');
    $response['branch_locations'] = $stmt->fetchAll(PDO::FETCH_COLUMN);

    // Bank Names
    $stmt = $pdo->query('SELECT bank_name FROM tbl_bank');
    $response['bank_names'] = $stmt->fetchAll(PDO::FETCH_COLUMN);

    // Account Types
    $stmt = $pdo->query('SELECT account_type FROM tbl_account_type');
    $response['account_types'] = $stmt->fetchAll(PDO::FETCH_COLUMN);

    // Reporting To (firstName, lastName, designation_id)
    $stmt = $pdo->query('SELECT firstName, lastName, designation_id FROM tbl_user');
    $response['reporting_to'] = $stmt->fetchAll(PDO::FETCH_ASSOC);

} catch (Exception $e) {
    $response['error'] = $e->getMessage();
}

echo json_encode($response); 