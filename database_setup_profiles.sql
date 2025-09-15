-- Profiles Database Setup Script
-- Run this script on your emp_kfinone database

-- Create tbl_training_profile table if it doesn't exist
CREATE TABLE IF NOT EXISTS `tbl_training_profile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vendor_bank_id` int(11) DEFAULT NULL,
  `loan_type_id` int(11) DEFAULT NULL,
  `image` varchar(500) DEFAULT NULL,
  `file` varchar(500) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_vendor_bank` (`vendor_bank_id`),
  KEY `fk_loan_type` (`loan_type_id`),
  CONSTRAINT `fk_training_profile_vendor_bank` 
  FOREIGN KEY (`vendor_bank_id`) REFERENCES `tbl_vendor_bank` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_training_profile_loan_type` 
  FOREIGN KEY (`loan_type_id`) REFERENCES `tbl_loan_type` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample profiles with real image URLs
INSERT IGNORE INTO `tbl_training_profile` (`id`, `vendor_bank_id`, `loan_type_id`, `image`, `file`, `description`) VALUES
(1, 1, 1, 'https://picsum.photos/400/300?random=1', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'HDFC Personal Loan Profile'),
(2, 2, 2, 'https://picsum.photos/400/300?random=2', 'https://www.africau.edu/images/default/sample.pdf', 'ICICI Home Loan Profile'),
(3, 3, 3, 'https://picsum.photos/400/300?random=3', 'https://www.learningcontainer.com/wp-content/uploads/2019/09/sample-pdf-file.pdf', 'SBI Car Loan Profile'),
(4, 4, 4, 'https://picsum.photos/400/300?random=4', 'https://www.learningcontainer.com/wp-content/uploads/2019/09/sample-pdf-file.pdf', 'Axis Business Loan Profile'),
(5, 5, 5, 'https://picsum.photos/400/300?random=5', 'https://www.learningcontainer.com/wp-content/uploads/2019/09/sample-pdf-file.pdf', 'Kotak Education Loan Profile');
