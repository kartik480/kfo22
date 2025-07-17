-- SQL script to fix user status in tbl_user table
-- Run this in your MySQL database (phpMyAdmin, MySQL Workbench, or command line)

-- First, let's see the current status distribution
SELECT status, COUNT(*) as count 
FROM tbl_user 
GROUP BY status;

-- Check users with NULL or empty status
SELECT COUNT(*) as null_status_count 
FROM tbl_user 
WHERE status IS NULL OR status = '';

-- Update all users to have 'Active' status (except those explicitly marked as 'Inactive')
UPDATE tbl_user 
SET status = 'Active' 
WHERE status IS NULL OR status = '' OR status != 'Inactive';

-- Verify the update worked
SELECT status, COUNT(*) as count 
FROM tbl_user 
GROUP BY status;

-- Test the query that the API uses
SELECT id, firstName, lastName, status
FROM tbl_user 
WHERE (status = 'Active' OR status IS NULL OR status = '' OR status != 'Inactive')
AND firstName IS NOT NULL 
AND firstName != '' 
AND lastName IS NOT NULL 
AND lastName != ''
ORDER BY firstName ASC, lastName ASC; 