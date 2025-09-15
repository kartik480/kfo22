-- Training Videos Database Setup Script
-- Run this script on your emp_kfinone database

-- Create tbl_training_videos table if it doesn't exist
CREATE TABLE IF NOT EXISTS `tbl_training_videos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `vendor_bank_id` int(11) DEFAULT NULL,
  `loan_type_id` int(11) DEFAULT NULL,
  `video_url` varchar(500) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_vendor_bank` (`vendor_bank_id`),
  KEY `fk_loan_type` (`loan_type_id`),
  CONSTRAINT `fk_training_videos_vendor_bank` FOREIGN KEY (`vendor_bank_id`) REFERENCES `tbl_vendor_bank` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_training_videos_loan_type` FOREIGN KEY (`loan_type_id`) REFERENCES `tbl_loan_type` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data for testing (optional)
-- You can uncomment these lines to add sample data

/*
INSERT INTO `tbl_training_videos` (`name`, `vendor_bank_id`, `loan_type_id`, `video_url`, `description`) VALUES
('Home Loan Basics', 1, 1, 'https://example.com/videos/home_loan_basics.mp4', 'Introduction to home loan products'),
('Personal Loan Guide', 2, 2, 'https://example.com/videos/personal_loan_guide.mp4', 'Complete guide to personal loans'),
('Business Loan Overview', 1, 3, 'https://example.com/videos/business_loan_overview.mp4', 'Understanding business loan requirements'),
('Car Loan Process', 3, 4, 'https://example.com/videos/car_loan_process.mp4', 'Step-by-step car loan application process');
*/

-- Verify the tables exist and have data
SELECT 'Vendor Banks:' as info;
SELECT COUNT(*) as count FROM tbl_vendor_bank;

SELECT 'Loan Types:' as info;
SELECT COUNT(*) as count FROM tbl_loan_type;

SELECT 'Training Videos:' as info;
SELECT COUNT(*) as count FROM tbl_training_videos;
