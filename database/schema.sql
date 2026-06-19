CREATE DATABASE IF NOT EXISTS guardian DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE guardian;

CREATE TABLE IF NOT EXISTS sys_department (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  department_name VARCHAR(100) NOT NULL,
  parent_id BIGINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(60) NOT NULL UNIQUE,
  password VARCHAR(120) NOT NULL,
  real_name VARCHAR(60) NOT NULL,
  phone VARCHAR(30),
  email VARCHAR(100),
  department_id BIGINT,
  role_code VARCHAR(40) NOT NULL,
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(40) NOT NULL UNIQUE,
  role_name VARCHAR(80) NOT NULL,
  description VARCHAR(255),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  permission_code VARCHAR(80) NOT NULL UNIQUE,
  permission_name VARCHAR(100) NOT NULL,
  menu_path VARCHAR(120),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS detect_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  task_name VARCHAR(120) NOT NULL,
  content_type VARCHAR(30) NOT NULL,
  source_content LONGTEXT NOT NULL,
  status VARCHAR(30) NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS detect_result (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_id BIGINT NOT NULL,
  risk_level VARCHAR(30) NOT NULL,
  risk_score INT NOT NULL,
  hit_summary TEXT,
  desensitized_content LONGTEXT,
  ai_suggestion LONGTEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS audit_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  detect_task_id BIGINT NOT NULL,
  reviewer_id BIGINT,
  status VARCHAR(30) NOT NULL,
  audit_opinion VARCHAR(500),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS audit_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  audit_task_id BIGINT NOT NULL,
  reviewer_id BIGINT NOT NULL,
  action VARCHAR(30) NOT NULL,
  opinion VARCHAR(500),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS warning_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  detect_task_id BIGINT NOT NULL,
  warning_level VARCHAR(30) NOT NULL,
  warning_content VARCHAR(1000) NOT NULL,
  status VARCHAR(30) NOT NULL,
  receiver_id BIGINT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sensitive_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sensitive_word (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL,
  word VARCHAR(120) NOT NULL,
  risk_level VARCHAR(30) NOT NULL,
  enabled TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS system_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  config_key VARCHAR(100) NOT NULL UNIQUE,
  config_value VARCHAR(500),
  description VARCHAR(255),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

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

CREATE TABLE IF NOT EXISTS operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  log_type VARCHAR(40) NOT NULL,
  operation VARCHAR(120) NOT NULL,
  detail TEXT,
  ip_address VARCHAR(80),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

INSERT IGNORE INTO sys_department (id, department_name, parent_id) VALUES
(1, '总部', 0),
(2, '研发部', 1),
(3, '财务部', 1),
(4, '市场部', 1);

INSERT IGNORE INTO sys_role (id, role_code, role_name, description) VALUES
(1, 'EMPLOYEE', '普通员工', '提交检测任务并查看个人记录'),
(2, 'MANAGER', '部门主管', '审核部门检测任务并处理风险'),
(3, 'SECURITY_OFFICER', '数据安全员', '维护敏感词库、风险等级和预警记录'),
(4, 'ADMIN', '系统管理员', '维护用户、角色、权限、日志和系统配置');

INSERT IGNORE INTO sensitive_category (id, category_name, description) VALUES
(1, '个人隐私', '手机号、身份证、地址等个人信息'),
(2, '商业机密', '客户名单、报价策略、合同细节'),
(3, '财务数据', '收款账号、利润、成本、预算等'),
(4, '技术资料', '源代码、接口密钥、架构方案等');

INSERT IGNORE INTO sensitive_word (id, category_id, word, risk_level, enabled) VALUES
(1, 2, '客户名单', 'HIGH', 1),
(2, 2, '商业机密', 'HIGH', 1),
(3, 3, '财务数据', 'HIGH', 1),
(4, 4, '源代码', 'MEDIUM', 1),
(5, 4, '接口密钥', 'CRITICAL', 1);

INSERT IGNORE INTO sys_permission (id, permission_code, permission_name, menu_path) VALUES
(1, 'detect:text', '文本检测', '/detect'),
(2, 'detect:file', '文件检测', '/detect'),
(3, 'audit:handle', '审核申请', '/audit'),
(4, 'warning:handle', '风险处理', '/warnings'),
(5, 'sensitive:manage', '敏感词库管理', '/sensitive-words'),
(6, 'admin:user', '用户管理', '/users'),
(7, 'admin:role', '角色管理', '/roles'),
(8, 'admin:permission', '权限管理', '/permissions');

INSERT IGNORE INTO system_config (id, config_key, config_value, description) VALUES
(1, 'jwt.expiration-minutes', '120', 'JWT 登录有效期，单位分钟'),
(2, 'deepseek.model', 'deepseek-chat', 'DeepSeek 对话模型名称'),
(3, 'detect.max-file-size-mb', '20', '文件检测最大上传大小');

INSERT IGNORE INTO risk_level_config (id, level_code, level_name, min_score, max_score, handle_rule, enabled) VALUES
(1, 'LOW', '低风险', 0, 49, '记录检测结果，员工可自行确认。', 1),
(2, 'MEDIUM', '中风险', 50, 74, '建议脱敏后提交审核。', 1),
(3, 'HIGH', '高风险', 75, 89, '自动触发预警，主管与安全员处理。', 1),
(4, 'CRITICAL', '严重风险', 90, 100, '阻断发布流程，必须复核并留痕。', 1);

-- 演示账号由后端 DataInitializer 在首次启动时使用 BCrypt 自动创建。
-- 用户名：admin / employee / manager / security，默认密码均为 123456。
