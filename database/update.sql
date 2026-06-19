USE guardian;

CREATE TABLE IF NOT EXISTS risk_level_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  level_code VARCHAR(30) NOT NULL UNIQUE,
  level_name VARCHAR(60) NOT NULL,
  min_score INT NOT NULL,
  max_score INT NOT NULL,
  handle_rule VARCHAR(500),
  enabled TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ai_review_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reviewer_id BIGINT NOT NULL,
  review_scene VARCHAR(120) NOT NULL,
  data_type VARCHAR(80) NOT NULL,
  publish_scope VARCHAR(80) NOT NULL,
  content_type VARCHAR(30) NOT NULL,
  file_name VARCHAR(255),
  source_content LONGTEXT NOT NULL,
  review_result LONGTEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

INSERT IGNORE INTO sys_permission (id, permission_code, permission_name, menu_path) VALUES
(1, 'detect:text', 'Text Detect', '/detect'),
(2, 'detect:file', 'File Detect', '/detect'),
(3, 'audit:handle', 'Audit Request', '/audit'),
(4, 'warning:handle', 'Warning Handle', '/warnings'),
(5, 'sensitive:manage', 'Sensitive Word Manage', '/sensitive-words'),
(9, 'ai:record', 'AI Review Record', '/ai-review-records'),
(6, 'admin:user', 'User Manage', '/users'),
(7, 'admin:role', 'Role Manage', '/roles'),
(8, 'admin:permission', 'Permission Manage', '/permissions');

INSERT IGNORE INTO system_config (id, config_key, config_value, description) VALUES
(1, 'jwt.expiration-minutes', '120', 'JWT expiration minutes'),
(2, 'deepseek.model', 'deepseek-chat', 'DeepSeek model name'),
(3, 'detect.max-file-size-mb', '20', 'Max upload file size in MB');

INSERT IGNORE INTO risk_level_config (id, level_code, level_name, min_score, max_score, handle_rule, enabled) VALUES
(1, 'LOW', 'Low Risk', 0, 49, 'Record result and allow employee confirmation.', 1),
(2, 'MEDIUM', 'Medium Risk', 50, 74, 'Suggest desensitization before audit.', 1),
(3, 'HIGH', 'High Risk', 75, 89, 'Trigger warning and require manager/security handling.', 1),
(4, 'CRITICAL', 'Critical Risk', 90, 100, 'Block release and require review trace.', 1);
