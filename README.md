# Intelligent Data Guardian System

智能数据守护者系统，基于 Spring Boot 3、MyBatis Plus、Vue3、Vite、Element Plus 构建。

## 功能范围

- 用户认证：注册、登录、JWT 鉴权、BCrypt 密码加密、个人中心。
- 敏感数据检测：手机号、身份证、邮箱、银行卡、地址、敏感词检测。
- AI 审核中心：预留 DeepSeek API 调用，未配置密钥时使用本地规则兜底。
- 审核管理：部门主管审核检测任务，记录审核意见。
- 风险预警：高风险任务自动生成预警记录。
- 敏感词管理：按分类维护敏感词。
- 数据统计：用户数、检测次数、风险事件、审核通过率、敏感词命中。
- RBAC 权限：用户、角色、权限、菜单扩展表结构。
- 系统日志：登录、操作、审核、异常日志表结构。

## 项目结构

```text
.
├── backend       Spring Boot 后端服务
├── frontend      Vue3 前端应用
├── database      MySQL 初始化脚本
└── pom.xml       Maven 聚合工程
```

## 快速启动

### 1. 配置环境变量

项目使用环境变量管理敏感信息（数据库密码、API 密钥等）。

**首次使用请复制模板文件：**

```bash
cp .env.example .env
```

然后编辑 `.env` 文件，填入你的实际配置：

```bash
# 数据库配置
DB_PASSWORD=your_database_password_here

# DeepSeek API 配置
DEEPSEEK_API_KEY=your_deepseek_api_key_here

# JWT 配置
JWT_SECRET=your_jwt_secret_key_here_change_this_in_production
```

**注意：** `.env` 文件已加入 `.gitignore`，不会被提交到版本控制系统。生产环境请使用更安全的方式管理密钥。

### 2. 初始化数据库

创建 MySQL 数据库并执行 `database/schema.sql`。
### 3. 配置后端

默认 MySQL 账号为 `root`；如本机不同，修改 `.env` 文件中的数据库配置。Redis 默认连接本地 `localhost:6379`。
### 4. 启动后端服务

后端会在首次启动时自动创建演示账号，默认密码均为 `123456`：

```
admin     系统管理员
employee  普通员工
manager   部门主管
security  数据安全员
```

建议使用 IDEA 自带 Maven 或项目内 `maven-settings.xml`，避免全局 Maven 仓库权限问题；该 settings 会把 Maven 仓库放到系统临时目录，避开 OneDrive 文件锁：

```bash
"D:\Program Files\JetBrains\IntelliJ IDEA 2025.3.4\plugins\maven\lib\maven3\bin\mvn.cmd" -s maven-settings.xml -pl backend spring-boot:run
```

### 5. 启动前端应用

```bash
cd frontend
npm install
npm run dev
```

首次登录后请及时修改管理员密码。
