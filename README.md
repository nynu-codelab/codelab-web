# NYNU SE Lab — 南阳师范学院软件工程实验室

官网与招新管理系统。

> 本网站为南阳师范学院软件工程实验室自建展示站，非学校官方门户网站。

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
nynu-se-lab/
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
| `MYSQL_DATABASE` | `nynu_se_lab` | 数据库名 |
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
- 创建 `nynu_se_lab` 数据库（utf8mb4）
- 创建 `sys_user` 表
- 插入默认管理员账号：`admin` / `admin123`

手动初始化（本地开发）：

```bash
mysql -u root -p < backend/sql/init.sql
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
