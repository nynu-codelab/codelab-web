# NYNU Code Lab — 南阳师范学院 Code Lab 实验室

官网与招新管理系统。

> 本网站为南阳师范学院 Code Lab 实验室自建展示站，非学校官方门户网站。

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Java 17, Spring Boot 3.x, Maven, MyBatis-Plus, Sa-Token (JWT) |
| 数据库 | MySQL 8 |
| 缓存 | Redis 7（已预留，当前后端未使用） |
| 前台 | Vue 3, TypeScript, Vite, Pinia, Vue Router 4 |
| 后台 | Vue 3, TypeScript, Vite, Element Plus |
| 反向代理 | Nginx |

## 项目结构

```
nynu-code-lab/
├── backend/                     # Spring Boot 后端
│   ├── Dockerfile
│   ├── sql/                     # 数据库初始化脚本
│   └── src/
├── web/                         # 前台 Vue 3
│   └── Dockerfile
├── admin-web/                   # 后台管理 Vue 3 + Element Plus
│   └── Dockerfile
├── deploy/                      # 容器化部署
│   ├── docker-compose.yml
│   ├── .env.example
│   ├── mysql/init/              # MySQL 初始化 SQL
│   ├── nginx/                   # Nginx 配置与 Dockerfile
│   └── README.md
├── docs/                        # 需求文档
├── AGENTS.md
└── README.md
```

## 快速开始

### 方式一：本地开发（使用本机中间件）

**前置条件：** Java 17, Maven 3.9+, Node.js 20+, MySQL 8

1. 初始化数据库：

```bash
mysql -u root -p < backend/sql/init.sql
```

2. 启动后端（默认 `local` profile，连接 `localhost:3306`）：

```bash
cd backend
mvn spring-boot:run
# 或指定 profile：
# mvn spring-boot:run -Dspring-boot.run.profiles=local
```

3. 启动前台（端口 3000）：

```bash
cd web
npm install
npm run dev
```

4. 启动后台（默认 Vite 端口）：

```bash
cd admin-web
npm install
npm run dev
```

5. 访问：
   - 前台：http://localhost:3000
   - 后台：http://localhost:5173/admin/
   - API 文档：http://localhost:8080/doc.html

### 方式二：Docker Compose（容器化启动）

**前置条件：** Docker Desktop / Docker Engine + Docker Compose v2

```bash
# 1. 进入部署目录
cd deploy

# 2. 创建环境变量文件
cp .env.example .env
# 编辑 .env 修改密码和密钥（尤其是 JWT_SECRET）

# 3. 启动所有服务
docker compose --env-file .env up -d --build

# 4. 查看运行状态
docker compose ps

# 5. 查看日志
docker compose logs -f backend     # 后端日志
docker compose logs -f nginx       # Nginx 日志
docker compose logs -f mysql       # 数据库日志

# 6. 停止服务
docker compose down

# 7. 停止并清理数据卷（会删除数据库数据）
docker compose down -v
```

访问：
- 前台：http://localhost
- 后台：http://localhost/admin/
- API：http://localhost/api/health
- API 文档：http://localhost/doc.html

## 环境变量说明

复制 `deploy/.env.example` 为 `deploy/.env`，修改以下变量：

| 变量 | 默认值 | 说明 |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `docker` | Spring 激活的 profile |
| `BACKEND_PORT` | `8080` | 后端端口 |
| `MYSQL_ROOT_PASSWORD` | `change_me_root` | MySQL root 密码，**生产必须修改** |
| `MYSQL_DATABASE` | `nynu_code_lab` | 数据库名 |
| `MYSQL_USER` | `nynu` | 数据库用户 |
| `MYSQL_PASSWORD` | `change_me_user` | 数据库密码，**生产必须修改** |
| `MYSQL_PORT` | `3306` | MySQL 端口 |
| `REDIS_PORT` | `6379` | Redis 端口 |
| `REDIS_PASSWORD` | (空) | Redis 密码（当前未使用） |
| `JWT_SECRET` | `change_me_...` | JWT 签名密钥，**生产必须修改**（`openssl rand -base64 64`） |
| `NGINX_PORT` | `80` | Nginx 对外端口 |

## Profile 说明

| Profile | 数据库连接 | 使用场景 |
|---|---|---|
| `local`（默认） | `localhost:3306` | 本地开发，本机 MySQL |
| `docker` | `mysql:3306`（容器服务名） | Docker Compose 部署 |

本地开发时无需设置 `SPRING_PROFILES_ACTIVE`，默认使用 `local` profile。Docker Compose 启动时自动注入 `SPRING_PROFILES_ACTIVE=docker`。

## 数据库初始化

Docker Compose 首次启动时会自动执行 `deploy/mysql/init/01-init.sql`：
- 创建 `nynu_code_lab` 数据库（utf8mb4）
- 创建 `sys_user` 表
- 插入默认管理员账号：`admin` / `admin123`

手动初始化（本地开发）：

```bash
mysql -u root -p < backend/sql/init.sql
```

## 从旧项目名迁移

本项目原名 `nynu-se-lab`（南阳师范学院软件工程实验室），已于 2026-06-22 完成工程级重命名。

如果你之前使用过旧名称的项目：

### 1. 本地目录重命名

```bash
cd /Users/zengbohan/Documents/project
mv nynu-se-lab nynu-code-lab
cd nynu-code-lab
```

### 2. 重建 Docker 数据卷

旧数据卷中数据库名为 `nynu_se_lab`，新版本使用 `nynu_code_lab`。如果你没有需要保留的数据，直接清理重建：

```bash
cd deploy
docker compose down -v    # 删除旧数据卷
docker compose up -d --build
```

如果你有需要保留的数据，可以手动迁移：

```bash
# 1. 导出旧数据库
docker compose exec mysql mysqldump -u root -p nynu_se_lab > old_data.sql

# 2. 清理并重建
docker compose down -v
docker compose up -d --build

# 3. 在新数据库名中导入
docker compose exec -T mysql mysql -u root -p nynu_code_lab < old_data.sql
```

### 3. 更新 Git 远程仓库（如果已改名）

```bash
git remote set-url origin git@gitee.com:zeng-bohan-66/nynu-code-lab.git
```

## 常见问题

### Q: 端口被占用？

修改 `deploy/.env` 中的端口配置：

```env
NGINX_PORT=8081
BACKEND_PORT=8082
MYSQL_PORT=3307
```

### Q: 如何重建某个服务？

```bash
cd deploy
docker compose up -d --build backend    # 重建后端
docker compose up -d --build nginx      # 重建 Nginx（含前后端构建）
```

### Q: 如何进入容器调试？

```bash
docker compose exec backend sh          # 后端
docker compose exec mysql bash          # MySQL
```

### Q: 如何查看数据库？

```bash
docker compose exec mysql mysql -u nynu -p
# 输入 MYSQL_PASSWORD
```

## 停止和清理

```bash
cd deploy

# 停止所有服务（保留数据）
docker compose down

# 停止并删除数据卷（数据库数据会丢失）
docker compose down -v

# 清理未使用的镜像和构建缓存
docker system prune -a
```

## 日志查看

```bash
cd deploy

# 实时查看后端日志
docker compose logs -f backend

# 查看最近 100 行 Nginx 日志
docker compose logs --tail=100 nginx

# 查看所有服务日志
docker compose logs
```

## 核心功能说明

### 注册与登录

系统支持两种角色：普通用户（USER）和管理员（ADMIN）。

- **普通用户注册**：访问前台 `/register` 页面，填写用户名、密码、姓名、手机号、年级、专业、班级即可注册。
- **普通用户登录**：访问前台 `/login` 页面，使用用户名+密码登录。
- **管理员登录**：访问后台 `/admin/login`，使用管理员账号登录（默认：`admin` / `admin123`）。
- **获取当前用户**：`GET /api/auth/me`，返回当前登录用户的详细信息。
- 密码使用 BCrypt 加密存储，登录后返回 JWT Token。

### 招新报名闭环

完整流程：**注册/登录 → 提交报名 → 查看我的报名 → 后台审核报名**

1. 登录后访问 `/recruit` 填写报名表（姓名、年级、专业、班级、手机号、QQ号、意向技术方向、编程基础、已掌握技术、个人介绍、加入原因、每周可投入时间、项目链接）
2. 提交后在 `/my-application` 查看报名状态
3. 待审核（PENDING）状态下可修改报名信息
4. 管理员在后台 `/admin/recruit` 查看报名列表，点击行查看详情，选择审核状态并填写备注提交
5. 审核状态流转：待审核 → 初筛通过 → 面试中 → 已通过 / 未通过 / 已撤回

约束：
- 一个用户只能有一条有效报名记录（已撤回的不计入）
- 普通用户只能查看和修改自己的报名
- 只有管理员才能访问后台报名管理接口
- 审核结果仅在系统内展示（无短信/邮件/QQ通知）

### 访问地址

| 入口 | 地址 |
|------|------|
| 前台首页 | http://localhost |
| 前台注册 | http://localhost/register |
| 前台登录 | http://localhost/login |
| 招新报名 | http://localhost/recruit（需登录） |
| 我的报名 | http://localhost/my-application（需登录） |
| 个人中心 | http://localhost/user（需登录） |
| 后台登录 | http://localhost/admin/login |
| 后台报名管理 | http://localhost/admin/recruit（需管理员） |
| API 文档 | http://localhost/doc.html |

### 核心接口说明

**认证接口（已有）：**

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 无 |
| POST | `/api/auth/login` | 用户登录（含管理员） | 无 |
| GET | `/api/auth/me` | 获取当前用户信息 | 需登录 |

**报名接口（新增）：**

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | `/api/applications` | 提交报名 | 需登录 |
| GET | `/api/applications/my` | 查看我的报名 | 需登录 |
| PUT | `/api/applications/my` | 修改我的报名（仅PENDING） | 需登录 |

**管理后台报名接口（新增）：**

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | `/api/admin/applications` | 报名列表（?status=筛选） | ADMIN |
| GET | `/api/admin/applications/{id}` | 报名详情 | ADMIN |
| PUT | `/api/admin/applications/{id}/review` | 审核报名 | ADMIN |

### 数据库初始化说明

Docker Compose 首次启动时会自动执行 `deploy/mysql/init/01-init.sql`，创建以下表：
- `sys_user` — 系统用户表（含默认管理员 admin/admin123）
- `lab_apply_record` — 招新报名记录表

如果数据库已初始化过（数据卷已存在），新表不会自动创建。请根据需要执行：

**方式一：重建数据卷（会清空所有数据）**
```bash
cd deploy
docker compose down -v
docker compose --env-file .env up -d --build
```

**方式二：手动执行 SQL**
```bash
docker compose exec mysql mysql -u root -p < backend/sql/init.sql
# 输入 MYSQL_ROOT_PASSWORD
```
