-- Create app_versions table for managing app updates
CREATE TABLE IF NOT EXISTS `app_versions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version_code` int(11) NOT NULL COMMENT 'Android version code (increment for each release)',
  `version_name` varchar(20) NOT NULL COMMENT 'Version name like 1.0.1',
  `update_message` text COMMENT 'Message to show to users about the update',
  `force_update` tinyint(1) DEFAULT 0 COMMENT 'Whether this update is mandatory',
  `download_url` varchar(500) DEFAULT NULL COMMENT 'Custom download URL (optional)',
  `min_required_version` int(11) DEFAULT 1 COMMENT 'Minimum version required to run the app',
  `release_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT 1 COMMENT 'Whether this version is currently active',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `version_code` (`version_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert initial version data
INSERT INTO `app_versions` (`version_code`, `version_name`, `update_message`, `force_update`, `download_url`, `min_required_version`) VALUES
(1, '1.0.0', 'Initial release of Kurakulas Partners app', 0, NULL, 1),
(2, '1.0.1', 'Bug fixes and performance improvements. Enhanced user experience with better navigation and faster loading times.', 0, NULL, 1);

-- Create index for better performance
CREATE INDEX idx_version_code ON app_versions(version_code);
CREATE INDEX idx_is_active ON app_versions(is_active); 