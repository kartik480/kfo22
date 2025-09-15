-- Profiles Database Setup Script
-- Run this script on your emp_kfinone database

-- Create tbl_profiles table if it doesn't exist
CREATE TABLE IF NOT EXISTS `tbl_profiles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vendor_bank_id` int(11) DEFAULT NULL,
  `loan_type_id` int(11) DEFAULT NULL,
  `images` varchar(500) DEFAULT NULL,
  `file` varchar(500) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_vendor_bank` (`vendor_bank_id`),
  KEY `fk_loan_type` (`loan_type_id`),
  CONSTRAINT `fk_profiles_vendor_bank` 
  FOREIGN KEY (`vendor_bank_id`) REFERENCES `tbl_vendor_bank` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_profiles_loan_type` 
  FOREIGN KEY (`loan_type_id`) REFERENCES `tbl_loan_type` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample profiles
INSERT IGNORE INTO `tbl_profiles` (`id`, `vendor_bank_id`, `loan_type_id`, `images`, `file`, `description`) VALUES
(1, 1, 1, 'https://example.com/images/profile1.jpg', 'https://example.com/files/profile1.pdf', 'HDFC Personal Loan Profile'),
(2, 2, 2, 'https://example.com/images/profile2.jpg', 'https://example.com/files/profile2.pdf', 'ICICI Home Loan Profile'),
(3, 3, 3, 'https://example.com/images/profile3.jpg', 'https://example.com/files/profile3.pdf', 'SBI Car Loan Profile'),
(4, 4, 4, 'https://example.com/images/profile4.jpg', 'https://example.com/files/profile4.pdf', 'Axis Business Loan Profile'),
(5, 5, 5, 'https://example.com/images/profile5.jpg', 'https://example.com/files/profile5.pdf', 'Kotak Education Loan Profile');
