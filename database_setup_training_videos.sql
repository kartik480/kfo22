-- Training Videos Database Setup Script
-- Run this script on your emp_kfinone database
-- Create tbl_training_video_category table if it doesn't exist
CREATE TABLE IF NOT EXISTS `tbl_training_video_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample video categories
INSERT IGNORE INTO `tbl_training_video_category` (`id`, `category_name`, `description`) VALUES
(1, 'Product Training', 'Training videos about products and services'),
(2, 'Sales Training', 'Sales techniques and strategies'),
(3, 'Customer Service', 'Customer service best practices'),
(4, 'Compliance Training', 'Regulatory and compliance training'),
(5, 'Leadership Training', 'Leadership and management skills');

-- Create tbl_training_videos table if it doesn't exist
CREATE TABLE IF NOT EXISTS `tbl_training_videos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `video_url` varchar(500) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_category` (`category_id`),
  CONSTRAINT `fk_training_videos_category` 
  FOREIGN KEY (`category_id`) REFERENCES `tbl_training_video_category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample training videos
INSERT IGNORE INTO `tbl_training_videos` (`id`, `name`, `category_id`, `video_url`, `description`) VALUES
(1, 'Product Overview Training', 1, 'https://example.com/product_overview.mp4', 'Introduction to our products'),
(2, 'Sales Techniques', 2, 'https://example.com/sales_techniques.mp4', 'Effective sales strategies'),
(3, 'Customer Service Excellence', 3, 'https://example.com/customer_service.mp4', 'Best practices for customer service'),
(4, 'Compliance Guidelines', 4, 'https://example.com/compliance.mp4', 'Regulatory compliance training'),
(5, 'Leadership Skills', 5, 'https://example.com/leadership.mp4', 'Leadership and management training');
