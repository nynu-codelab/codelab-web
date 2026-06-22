# Stage 5.6 部署预演计划

> 日期：2026-06-22
> 分支：`refine/pc-immersive-frontend`
> 基准提交：`98b0889` docs(stage5): 收口本地验证配置说明
> 目标：产出部署预演准备材料，不实际部署生产

---

## 一、概述

本阶段为部署预演准备，不进入 Stage 6、不开发新功能、不做视觉增强、不实际部署生产。产出内容包括服务器目录结构方案、Docker Compose 生产预演命令、Nginx/HTTPS/域名接入方案，以及服务验证步骤。

---

## 二、推荐服务器目录结构

```
/opt/codelab/
├── app/                           # 项目代码（Git 仓库）
│   └── nynu-code-lab/             # 当前项目
│       ├── backend/
│       ├── web/
│       ├── admin-web/
│       ├── deploy/
│       ├── docs/
│       ├── scripts/
│       └── ...
├── data/                          # 持久化数据（容器 volume 映射）
│   ├── mysql/                     # MySQL 数据文件
│   ├── redis/                     # Redis AOF/RDB 持久化文件
│   └── uploads/                   # 用户上传文件
├── backup/                        # 备份目录
│   ├── mysql/                     # 数据库 SQL 备份
│   └── uploads/                   # 上传文件归档备份
├── logs/                          # 日志留存
│   ├── nginx/                     # Nginx 访问/错误日志
│   └── backend/                   # 后端应用日志
├── ssl/                           # HTTPS 证书（不提交 Git）
│   ├── fullchain.pem
│   └── privkey.pem
└── deploy/                        # 生产部署配置
    ├── .env                       # 生产环境变量（不提交 Git）
    └── docker-compose.prod.yml    # 生产 Compose 文件（如与开发版不同）
```

### 2.1 目录说明

| 目录 | 持久化 | 备份 | 说明 |
|------|--------|------|------|
| `app/` | 否（Git 管理） | 否 | 项目代码，通过 Git 版本控制 |
| `data/mysql/` | **是** | **是** | MySQL 数据文件，`docker-compose.yml` 中 `mysql-data` 卷映射到此 |
| `data/redis/` | 可选 | 可选 | Redis AOF 持久化文件，非核心业务主数据 |
| `data/uploads/` | **是** | **是** | 用户上传文件，业务数据，必须持久化和备份 |
| `backup/mysql/` | 否 | — | 存放数据库 SQL 导出文件，建议离线保存 |
| `backup/uploads/` | 否 | — | 存放上传文件 tar.gz 归档 |
| `logs/` | 可选 | 否 | 便于问题排查，建议配置 logrotate |
| `ssl/` | 否 | 否 | HTTPS 证书，不提交 Git，通过 certbot 或云服务商管理 |

### 2.2 权限设置

| 目录 | 权限 | 说明 |
|------|------|------|
| `data/uploads/` | `755`，owner 为 Docker 运行用户 | 后端容器需写入，Nginx 容器只读挂载 |
| `backup/` | `700` | 含数据库备份，限制访问 |
| `ssl/` | `600` 或 `400` | 证书和私钥，禁止其他用户读取 |
| `logs/` | `755` | 日志留存目录 |

### 2.3 Nginx 只读挂载

当前 `docker-compose.yml` 中 Nginx 对 uploads 使用只读挂载：

```yaml
nginx:
  volumes:
    - uploads-data:/usr/share/nginx/html/uploads:ro
```

这符合安全最佳实践：Nginx 仅对外提供上传文件的静态访问，不修改文件。

---

## 三、Docker Compose 生产预演命令

基于当前 `deploy/docker-compose.yml`，以下是生产部署预演核心命令序列。

### 3.1 首次部署

```bash
# 0. 进入项目目录
cd /opt/codelab/app/nynu-code-lab

# 1. 拉取基础镜像（MySQL、Redis、Nginx 等）
docker compose --env-file deploy/.env -f deploy/docker-compose.yml pull

# 2. 构建应用镜像并启动所有服务
docker compose --env-file deploy/.env -f deploy/docker-compose.yml up -d --build

# 3. 查看服务状态（等待所有服务 healthy）
docker compose --env-file deploy/.env -f deploy/docker-compose.yml ps

# 4. 查看关键日志
docker compose --env-file deploy/.env -f deploy/docker-compose.yml logs --tail=200 backend
docker compose --env-file deploy/.env -f deploy/docker-compose.yml logs --tail=100 nginx
docker compose --env-file deploy/.env -f deploy/docker-compose.yml logs --tail=100 mysql
docker compose --env-file deploy/.env -f deploy/docker-compose.yml logs --tail=100 redis
```

### 3.2 更新部署

```bash
# 1. 拉取最新代码
cd /opt/codelab/app/nynu-code-lab
git pull origin main  # 或对应分支

# 2. 重建受影响的服务
docker compose --env-file deploy/.env -f deploy/docker-compose.yml up -d --build

# 3. 清理旧镜像（可选，节省磁盘空间）
docker image prune -f
```

### 3.3 日常运维

```bash
# 查看实时日志
docker compose --env-file deploy/.env -f deploy/docker-compose.yml logs -f backend

# 重启单个服务
docker compose --env-file deploy/.env -f deploy/docker-compose.yml restart backend

# 停止所有服务（保留数据卷）
docker compose --env-file deploy/.env -f deploy/docker-compose.yml down

# 停止并清理数据卷（⚠️ 会删除所有数据）
docker compose --env-file deploy/.env -f deploy/docker-compose.yml down -v
```

### 3.4 服务健康验证

```bash
# 各服务健康检查
docker compose --env-file deploy/.env -f deploy/docker-compose.yml ps
# 预期输出：mysql(healthy), redis(healthy), backend(healthy), nginx(healthy)

# 后端健康检查接口
curl -f http://localhost/api/health
# 预期：{"status":"UP","service":"nynu-code-lab-backend"}

# 公开接口验证
curl -f http://localhost/api/directions     # 技术方向
curl -f http://localhost/api/articles       # 文章列表
curl -f http://localhost/api/projects       # 项目列表
curl -f http://localhost/api/members        # 成员列表
curl -f http://localhost/api/site-config    # 站点配置

# 管理接口未登录拒绝（预期 401）
curl -s http://localhost/api/admin/dashboard/stats | head -c 200
# 预期：返回业务码 401 "未登录或登录已过期"

# 后台登录
TOKEN=$(curl -s -X POST http://localhost/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.data')
echo "Token: ${TOKEN:0:20}..."

# 管理接口登录后正常（预期 200）
curl -s -H "Authorization: Bearer $TOKEN" http://localhost/api/admin/dashboard/stats | jq '.code'

# 上传文件可访问（需先上传测试文件获取 URL）
# curl -f http://localhost/uploads/2026/06/22/{uuid}.png

# Token 黑名单验证
curl -s -X POST http://localhost/api/auth/logout \
  -H "Authorization: Bearer $TOKEN"
# 再次使用同一 Token 应返回 401
curl -s -H "Authorization: Bearer $TOKEN" http://localhost/api/auth/me | jq '.message'
# 预期："token已失效，请重新登录"

# 登录限流验证（连续 5 次错误密码，第 6 次被锁定）
for i in {1..6}; do
  echo "Attempt $i:"
  curl -s -X POST http://localhost/api/auth/login \
    -H 'Content-Type: application/json' \
    -d '{"username":"admin","password":"wrong"}' | jq '.message'
done
```

### 3.5 前端页面验证

```bash
# 前台页面
curl -s -o /dev/null -w "%{http_code}" http://localhost/               # 首页 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/about          # 介绍 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/directions     # 方向 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/members        # 成员 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/projects       # 项目 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/articles       # 文章 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/recruit        # 招新 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/login          # 登录 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/register       # 注册 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/profile        # 个人中心 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/contact        # 联系我们 200

# 后台页面
curl -s -o /dev/null -w "%{http_code}" http://localhost/admin/         # 后台 200
curl -s -o /dev/null -w "%{http_code}" http://localhost/admin/login    # 后台登录 200

# Vue Router history fallback（SPA 刷新不 404）
curl -s -o /dev/null -w "%{http_code}" http://localhost/admin/dashboard
# 预期：200（返回 admin/index.html）
```

---

## 四、Nginx / HTTPS / 域名接入方案

### 4.1 当前 Nginx 配置

当前 `deploy/nginx/nginx.conf` 支持 HTTP 80 端口，路由如下：

| 路径 | 目标 | 说明 |
|------|------|------|
| `/api/` | `proxy_pass http://backend` | 后端 REST API |
| `/uploads/` | `alias` 静态文件 | 上传文件对外访问，30d 缓存 |
| `/doc.html`、`/v3/`、`/swagger-ui/` | `proxy_pass http://backend` | Knife4j API 文档 |
| `/admin/` | `alias` 静态文件 + `try_files` | 后台 SPA（Vue Router history） |
| `/` | `root` 静态文件 + `try_files` | 前台 SPA（Vue Router history） |

关键安全配置：
- `client_max_body_size 10M` — 与后端 10MB 上传限制对齐
- `uploads-data` 卷以 `:ro`（只读）挂载到 Nginx
- Gzip 压缩已启用

### 4.2 域名接入步骤

1. **购买/配置域名**：例如 `codelab.nynu.edu.cn`，解析到服务器公网 IP
2. **配置 DNS A 记录**：`codelab.nynu.edu.cn → 服务器公网 IP`
3. **验证解析生效**：`dig codelab.nynu.edu.cn` 或 `nslookup`

### 4.3 HTTP → HTTPS 升级方案

生产环境必须启用 HTTPS，推荐使用 Let's Encrypt 免费证书。

#### 步骤一：安装 certbot

```bash
# Ubuntu/Debian
apt update && apt install -y certbot

# 或使用 Docker 方式（推荐）
docker run -it --rm -v /opt/codelab/ssl:/etc/letsencrypt \
  certbot/certbot certonly --standalone \
  -d codelab.nynu.edu.cn
```

#### 步骤二：配置 Nginx HTTPS

在 `deploy/nginx/nginx.conf` 中增加 HTTPS server 块（或在现有基础上修改）：

```nginx
# HTTP → HTTPS 跳转
server {
    listen 80;
    server_name codelab.nynu.edu.cn;
    return 301 https://$server_name$request_uri;
}

# HTTPS 主配置
server {
    listen 443 ssl http2;
    server_name codelab.nynu.edu.cn;

    ssl_certificate /etc/nginx/ssl/fullchain.pem;
    ssl_certificate_key /etc/nginx/ssl/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    client_max_body_size 10M;
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml text/javascript image/svg+xml;
    gzip_min_length 1024;
    gzip_vary on;

    # ...其余 location 配置与 HTTP 版本相同...
}
```

#### 步骤三：Docker Compose 挂载证书

在 `docker-compose.yml` nginx 服务中增加证书卷挂载：

```yaml
nginx:
  volumes:
    - uploads-data:/usr/share/nginx/html/uploads:ro
    - /opt/codelab/ssl:/etc/nginx/ssl:ro    # 证书只读挂载
```

#### 步骤四：证书自动续期

Let's Encrypt 证书有效期 90 天，需配置自动续期：

```bash
# 添加 crontab（每月 1 日和 15 日凌晨 3 点尝试续期）
0 3 1,15 * * certbot renew --quiet --deploy-hook "docker compose -f /opt/codelab/app/nynu-code-lab/deploy/docker-compose.yml restart nginx"
```

### 4.4 安全加固要点

| 要点 | 说明 |
|------|------|
| 禁止目录列表 | Nginx 默认不开启 `autoindex`，无需额外配置 |
| uploads 不执行文件 | `alias` 仅返回静态文件，不经过 CGI/FastCGI，不会被解析执行 |
| 上传大小限制 | `client_max_body_size 10M` 与后端 10MB 一致 |
| SPA history fallback | `/` 和 `/admin/` 分别配置 `try_files ... /index.html`，刷新不 404 |
| 禁止访问隐藏文件 | 可在 Nginx 中增加 `location ~ /\. { deny all; }` |
| 防火墙 | 仅开放 80/443 端口，不暴露 3306/6379/8080 到公网 |

### 4.5 完整生产 Nginx 配置模板

> 以下为生产环境 Nginx 配置模板。证书路径中的 `/etc/nginx/ssl/` 需通过 Docker Compose volume 挂载宿主机目录。
> 证书文件和私钥不提交 Git。

```nginx
upstream backend {
    server backend:8080;
}

# HTTP → HTTPS 跳转
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

# HTTPS 主服务
server {
    listen 443 ssl http2;
    server_name your-domain.com;

    # SSL 证书
    ssl_certificate /etc/nginx/ssl/fullchain.pem;
    ssl_certificate_key /etc/nginx/ssl/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    client_max_body_size 10M;

    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml text/javascript image/svg+xml;
    gzip_min_length 1024;
    gzip_vary on;

    # API 代理
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 30s;
        proxy_read_timeout 60s;
    }

    # 上传文件静态服务
    location /uploads/ {
        alias /usr/share/nginx/html/uploads/;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }

    # API 文档代理
    location /doc.html { proxy_pass http://backend; }
    location /v3/      { proxy_pass http://backend; }
    location /swagger-ui/ { proxy_pass http://backend; }

    # 后台 SPA
    location /admin/ {
        alias /usr/share/nginx/html/admin/;
        try_files $uri $uri/ /admin/index.html;
        index index.html;
    }

    # 前台 SPA
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
        index index.html;
    }
}
```

---

## 五、当前已知限制（部署层面）

1. 当前 Nginx 配置仅支持 HTTP 80 端口，未包含 HTTPS server 块
2. 未配置日志集中收集（ELK / Loki）
3. 未配置容器监控（Prometheus / Grafana）
4. 前端构建在 Nginx 多阶段构建中完成，首次构建耗时较长（约 3-5 分钟）
5. 后端无水平扩展机制（当前单实例部署）
6. 未配置 CI/CD 自动部署流水线

以上项目可在后续迭代中逐步完善，当前阶段不强制处理。
