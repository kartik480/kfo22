<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Super simple - just return hardcoded data for now
$payoutTypes = [
    ['id' => 1, 'payout_name' => 'Branch/Full Payout'],
    ['id' => 2, 'payout_name' => 'Service/Partner Payout'],
    ['id' => 3, 'payout_name' => 'Lead Base/Agent Payout'],
    ['id' => 4, 'payout_name' => 'Connector/Referral Payout'],
    ['id' => 5, 'payout_name' => 'Inter Branch Payout'],
    ['id' => 6, 'payout_name' => 'SDSA Payout']
];

echo json_encode([
    'status' => 'success',
    'message' => 'Payout types fetched successfully',
    'data' => $payoutTypes,
    'count' => count($payoutTypes)
]);
?>
