# Deploy — 容器化部署

## 快速开始

```bash
# 一键初始化 & 启动（推荐）
./scripts/setup-docker.sh
```

或手动操作：

```bash
# 1. 准备环境变量
cp deploy/.env.example deploy/.env
# 编辑 .env，至少填写 MYSQL_ROOT_PASSWORD / MYSQL_PASSWORD / JWT_SECRET
# 或执行 setup-docker.sh 自动生成

# 2. 启动
docker compose --env-file deploy/.env -f deploy/docker-compose.yml up -d --build

# 3. 验证
curl http://localhost:80/api/health
```

## 目录结构

```
deploy/
├── docker-compose.yml       # Docker Compose 编排配置
├── .env.example             # 环境变量模板（复制为 .env 后修改）
├── .env                     # 实际环境变量（不提交到 Git）
├── mysql/
│   └── init/
│       └── 01-init.sql      # MySQL 首次启动初始化脚本
├── nginx/
│   ├── Dockerfile           # Nginx 统一托管 Dockerfile（多阶段构建）
│   └── nginx.conf           # Nginx 路由配置
└── README.md                # 本文件
```

## 容器架构

```
                    ┌──────────────────────────────┐
                    │       Nginx (port 80)         │
                    │  /          → web (前台 SPA)   │
                    │  /admin/    → admin-web (后台) │
                    │  /api/      → backend:8080     │
                    │  /uploads/  → backend:8080     │
                    │  /doc.html  → backend:8080     │
                    └──────────────┬───────────────┘
                                   │
                    ┌──────────────▼───────────────┐
                    │     Backend (port 8080)        │
                    │  Spring Boot 3 + Sa-Token JWT │
                    │  uploads → /app/uploads/       │
                    └──────┬────────────┬───────────┘
                           │            │
              ┌────────────▼──┐  ┌──────▼──────────┐
              │  MySQL 8      │  │  Redis 7         │
              │  (port 3306)  │  │  (port 6379)     │
              │               │  │  (预留，未使用)    │
              └───────────────┘  └─────────────────┘
```

## 服务端口映射

| 服务 | 容器内端口 | 宿主机端口（可配） | 健康检查 |
|---|---|---|---|
| Nginx | 80 | `${NGINX_PORT:-80}` | `wget localhost:80/api/health` |
| Backend | 8080 | `${BACKEND_PORT:-8080}` | `curl localhost:8080/api/health` |
| MySQL | 3306 | `${MYSQL_PORT:-3306}` | `mysqladmin ping` |
| Redis | 6379 | `${REDIS_PORT:-6379}` | `redis-cli ping` |

## Nginx 路由规则

| 路径 | 目标 | 说明 |
|---|---|---|
| `/` | web dist | 前台 SPA，Vue Router history 模式 |
| `/admin/` | admin-web dist | 后台 SPA，Vue Router history 模式 |
| `/api/` | backend:8080 | REST API 代理（含 `/api/health`） |
| `/uploads/` | backend:8080 | 上传文件静态服务（预留） |
| `/doc.html` | backend:8080 | Knife4j API 文档 |
| `/v3/` | backend:8080 | OpenAPI 规范 |
| `/swagger-ui/` | backend:8080 | Swagger UI |

## 环境变量

详见 `.env.example`。关键变量：

- `MYSQL_ROOT_PASSWORD` / `MYSQL_PASSWORD`：数据库密码，**必须设置**
- `JWT_SECRET`：JWT 签名密钥，**必须设置**，建议 `openssl rand -base64 64` 生成
- `SPRING_PROFILES_ACTIVE`：Spring profile，容器环境必须为 `docker`
- `BACKEND_PORT`：后端宿主机映射端口，默认 `8080`
- `NGINX_PORT`：Nginx 宿主机映射端口，默认 `80`

> **注意：** `SERVER_PORT` 固定为容器内 `8080`，**不建议修改**。Nginx upstream 和健康检查均通过容器内 `8080` 通信。如需改变宿主机端口，修改 `BACKEND_PORT` 即可。

## 容器间通信

所有容器通过 `nynu-code-lab-net` 桥接网络通信，使用 Docker Compose 服务名：
- 后端连接 MySQL：`jdbc:mysql://mysql:3306/nynu_code_lab`
- 后端连接 Redis：`redis:6379`（预留）
- Nginx 代理后端：`http://backend:8080`

**禁止在 Docker profile 中使用 `localhost`**，因为容器内 localhost 指向自身。

## 数据持久化

| 数据 | 卷 | 路径 |
|---|---|---|
| MySQL 数据 | `mysql-data` | `/var/lib/mysql` |
| Redis 数据 | `redis-data` | `/data` |
| 上传文件 | `uploads-data` | `/app/uploads` |
| MySQL 初始化脚本 | bind mount | `./mysql/init/`（只读） |

## 管理命令

```bash
# 启动
docker compose --env-file deploy/.env -f deploy/docker-compose.yml up -d --build

# 状态
docker compose --env-file deploy/.env -f deploy/docker-compose.yml ps

# 日志（所有服务）
docker compose --env-file deploy/.env -f deploy/docker-compose.yml logs -f

# 日志（仅后端）
docker compose --env-file deploy/.env -f deploy/docker-compose.yml logs -f backend

# 停止
docker compose --env-file deploy/.env -f deploy/docker-compose.yml down

# 停止并清理数据卷
docker compose --env-file deploy/.env -f deploy/docker-compose.yml down -v
```

## 当前已知限制

1. Nginx 配置未启用 HTTPS，生产环境需额外配置 SSL 证书
2. Redis 服务已预留但后端未使用，可在后续引入缓存/Session 共享
3. 前端构建跳过 `vue-tsc` 类型检查以加速容器构建，CI 中应单独运行类型检查
4. 未配置日志集中收集（ELK/Loki）
5. 文件上传功能尚未完全实现，相关卷和路由已预先配置
