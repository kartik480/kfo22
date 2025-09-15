-- Seminars Database Setup Script
-- Run this script on your emp_kfinone database

-- Create tbl_training_seminar table if it doesn't exist
CREATE TABLE IF NOT EXISTS `tbl_training_seminar` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `video_name` varchar(255) NOT NULL,
  `video_image` varchar(500) DEFAULT NULL,
  `video` varchar(500) DEFAULT NULL,
  `vendor_bank_id` int(11) DEFAULT NULL,
  `loan_type_id` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_vendor_bank` (`vendor_bank_id`),
  KEY `fk_loan_type` (`loan_type_id`),
  CONSTRAINT `fk_training_seminar_vendor_bank` 
  FOREIGN KEY (`vendor_bank_id`) REFERENCES `tbl_vendor_bank` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_training_seminar_loan_type` 
  FOREIGN KEY (`loan_type_id`) REFERENCES `tbl_loan_type` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample seminars with real video URLs
INSERT IGNORE INTO `tbl_training_seminar` (`id`, `video_name`, `video_image`, `video`, `vendor_bank_id`, `loan_type_id`, `description`) VALUES
(1, 'Personal Loan Seminar - HDFC', 'https://picsum.photos/300/200?random=1', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 1, 1, 'Comprehensive seminar on HDFC Personal Loan products'),
(2, 'Home Loan Seminar - ICICI', 'https://picsum.photos/300/200?random=2', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 2, 2, 'Detailed seminar on ICICI Home Loan offerings'),
(3, 'Car Loan Seminar - SBI', 'https://picsum.photos/300/200?random=3', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 3, 3, 'SBI Car Loan seminar for agents'),
(4, 'Business Loan Seminar - Axis', 'https://picsum.photos/300/200?random=4', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 4, 4, 'Axis Business Loan training seminar'),
(5, 'Education Loan Seminar - Kotak', 'https://picsum.photos/300/200?random=5', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 5, 5, 'Kotak Education Loan seminar'),
(6, 'Loan Processing Seminar', 'https://picsum.photos/300/200?random=6', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 1, 1, 'General loan processing training'),
(7, 'Customer Service Seminar', 'https://picsum.photos/300/200?random=7', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 2, 2, 'Customer service best practices'),
(8, 'Sales Techniques Seminar', 'https://picsum.photos/300/200?random=8', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 3, 3, 'Advanced sales techniques training');
