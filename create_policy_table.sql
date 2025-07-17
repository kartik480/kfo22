-- Create policy table with correct structure
CREATE TABLE IF NOT EXISTS `tbl_policy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vendor_bank_id` int(11) NOT NULL,
  `loan_type_id` int(11) NOT NULL,
  `image` varchar(500) DEFAULT NULL,
  `content` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_policy_vendor_bank` (`vendor_bank_id`),
  KEY `fk_policy_loan_type` (`loan_type_id`),
  CONSTRAINT `fk_policy_vendor_bank` FOREIGN KEY (`vendor_bank_id`) REFERENCES `tbl_vendor_bank` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_policy_loan_type` FOREIGN KEY (`loan_type_id`) REFERENCES `tbl_loan_type` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample data for testing (assuming vendor_bank and loan_type tables have data)
-- Note: You'll need to replace the IDs with actual IDs from your tbl_vendor_bank and tbl_loan_type tables
INSERT INTO `tbl_policy` (`vendor_bank_id`, `loan_type_id`, `image`, `content`) VALUES
(1, 1, 'policy_doc1.pdf', 'This policy outlines the terms and conditions for personal loans offered by HDFC Bank.'),
(2, 2, 'home_loan_policy.pdf', 'Home loan policy details including eligibility criteria and documentation requirements.'),
(3, 3, 'business_policy.pdf', 'Business loan policy with interest rates and repayment terms.'),
(4, 4, 'vehicle_policy.pdf', 'Vehicle loan policy covering car and two-wheeler financing options.'); 