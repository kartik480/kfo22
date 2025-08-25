-- Add Marketing Head designation to tbl_designation table
-- This script adds the "Marketing Head" designation if it doesn't already exist

-- Check if Marketing Head designation already exists
SELECT COUNT(*) as count FROM tbl_designation WHERE designation_name = 'Marketing Head';

-- Add Marketing Head designation if it doesn't exist
INSERT INTO tbl_designation (designation_name, status, created_at, updated_at) 
SELECT 'Marketing Head', 'Active', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM tbl_designation WHERE designation_name = 'Marketing Head'
);

-- Verify the designation was added
SELECT * FROM tbl_designation WHERE designation_name = 'Marketing Head';

-- Show all designations for reference
SELECT id, designation_name, status, created_at FROM tbl_designation ORDER BY id;

-- Instructions for assigning users to Marketing Head designation:
-- 1. First, find the ID of the Marketing Head designation:
--    SELECT id FROM tbl_designation WHERE designation_name = 'Marketing Head';
--
-- 2. Then update specific users to have this designation:
--    UPDATE tbl_user 
--    SET designation_id = (SELECT id FROM tbl_designation WHERE designation_name = 'Marketing Head')
--    WHERE username = 'your_marketing_head_username';
--
-- 3. Verify the user has been assigned:
--    SELECT u.username, u.firstName, u.lastName, d.designation_name 
--    FROM tbl_user u 
--    LEFT JOIN tbl_designation d ON u.designation_id = d.id 
--    WHERE d.designation_name = 'Marketing Head';
