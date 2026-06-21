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

3. 启动前台（端口 5173）：

```bash
cd web
npm install
npm run dev
```

4. 启动后台（端口 5174）：

```bash
cd admin-web
npm install
npm run dev
```

5. 访问：
   - 前台：http://localhost:5173
   - 后台：http://localhost:5174/admin/
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
| `SERVER_PORT` | `8080` | 后端容器内端口；如修改需同步 Nginx upstream |
| `BACKEND_PORT` | `8080` | 后端宿主机映射端口 |
| `MYSQL_ROOT_PASSWORD` | 空 | MySQL root 密码，**启动前必须填写，生产必须使用强密码** |
| `MYSQL_DATABASE` | `nynu_code_lab` | 数据库名 |
| `MYSQL_USER` | `nynu` | 数据库用户 |
| `MYSQL_PASSWORD` | 空 | 数据库密码，**启动前必须填写，生产必须使用强密码** |
| `MYSQL_PORT` | `3306` | MySQL 端口 |
| `REDIS_PORT` | `6379` | Redis 端口 |
| `REDIS_PASSWORD` | (空) | Redis 密码（当前未使用） |
| `JWT_SECRET` | 空 | JWT 签名密钥，**启动前必须填写，生产必须使用 `openssl rand -base64 64` 生成** |
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
- 创建 `lab_apply_record` 表（含 `uk_apply_active_user` 非撤回报名唯一约束）
- 创建 `lab_article` 表
- 创建 `lab_project` 表
- 插入默认管理员账号：`admin` / `admin123`

> ⚠️ **仅用于本地开发演示。** 生产环境必须修改默认密码。
> 生产部署前务必：修改 `deploy/.env` 中的 `MYSQL_ROOT_PASSWORD`、`MYSQL_PASSWORD`、`JWT_SECRET`，并创建新的管理员账号替换默认账号。

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
| 实验室介绍 | http://localhost/about |
| 技术方向 | http://localhost/directions |
| 核心成员 | http://localhost/members |
| 项目成果 | http://localhost/projects |
| 学习文章 | http://localhost/articles |
| 前台注册 | http://localhost/register |
| 前台登录 | http://localhost/login |
| 招新报名 | http://localhost/recruit（需登录） |
| 我的报名 | http://localhost/my-application（需登录） |
| 个人中心 | http://localhost/profile（需登录，`/user` 兼容旧路径） |
| 联系我们 | http://localhost/contact |
| 视觉预览保留页 | http://localhost/design-preview |
| 文章详情 | http://localhost/articles/{id} |
| 项目详情 | http://localhost/projects/{id} |
| 后台登录 | http://localhost/admin/login |
| 后台数据概览 | http://localhost/admin/dashboard（需管理员） |
| 后台报名管理 | http://localhost/admin/recruit（需管理员） |
| 后台文章管理 | http://localhost/admin/articles（需管理员） |
| 后台项目管理 | http://localhost/admin/projects（需管理员） |
| 后台成员管理占位 | http://localhost/admin/members（需管理员） |
| 后台方向管理占位 | http://localhost/admin/directions（需管理员） |
| 后台站点配置占位 | http://localhost/admin/site（需管理员） |
| 后台文件上传占位 | http://localhost/admin/upload（需管理员） |
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

**文章接口（新增）：**

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | `/api/articles` | 已发布文章列表（?category=筛选） | 无 |
| GET | `/api/articles/{id}` | 已发布文章详情 | 无 |

**管理后台文章接口（新增）：**

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | `/api/admin/articles` | 文章列表（?status=筛选） | ADMIN |
| GET | `/api/admin/articles/{id}` | 文章详情 | ADMIN |
| POST | `/api/admin/articles` | 创建文章 | ADMIN |
| PUT | `/api/admin/articles/{id}` | 编辑文章 | ADMIN |
| PUT | `/api/admin/articles/{id}/publish` | 发布文章 | ADMIN |
| PUT | `/api/admin/articles/{id}/offline` | 下架文章 | ADMIN |
| DELETE | `/api/admin/articles/{id}` | 删除文章（软删除） | ADMIN |

### 文章 / Markdown 学习分享

**文章状态：** 草稿（DRAFT）→ 已发布（PUBLISHED）→ 已下架（OFFLINE）

**完整流程：**

1. 管理员登录后台 http://localhost/admin/login
2. 进入文章管理 http://localhost/admin/articles
3. 新建文章：填写标题、摘要、分类、标签、Markdown 正文，保存草稿
4. 草稿文章不会在前台展示
5. 点击"发布"将文章状态改为已发布
6. 前台 http://localhost/articles 可看到已发布文章列表
7. 点击文章进入详情页 http://localhost/articles/{id}，Markdown 正确渲染
8. 管理员可随时"下架"文章（前台不可见）
9. 管理员可"删除"文章（软删除，前台不可见）

**Markdown 渲染安全策略：**

- 使用 `markdown-it` 进行 Markdown → HTML 转换
- 配置 `html: false`，关闭原始 HTML 标签解析，防止 XSS 攻击
- 文章正文中嵌入的 HTML 标签会被转义显示，不会执行
- 文章封面仅支持 URL 字段，不支持图片上传
- 第一版支持基础 Markdown 语法：标题、列表、代码块、链接、表格、引用等

**分类和标签：**
- 分类：自由文本字段，例如「学习笔记」「技术分享」「项目复盘」
- 标签：JSON 数组字符串格式，例如 `["Java","Spring Boot"]`

### 项目成果展示

**项目状态：** 草稿（DRAFT）→ 已发布（PUBLISHED）→ 已下架（OFFLINE）

**完整流程：**

1. 管理员登录后台 http://localhost/admin/login
2. 进入项目管理 http://localhost/admin/projects
3. 新建项目：填写项目名称、简介、类型、技术栈、负责人、Markdown 详细介绍，保存草稿
4. 草稿项目不会在前台展示
5. 点击"发布"将项目状态改为已发布
6. 前台 http://localhost/projects 可看到已发布项目列表
7. 点击项目进入详情页 http://localhost/projects/{id}，Markdown 正确渲染
8. 设置为"首页精选"的项目出现在首页精选项目区域
9. 管理员可随时"下架"项目（前台不可见）
10. 管理员可"删除"项目（软删除，前台不可见）

**项目详情 Markdown 渲染安全策略：**

- 使用 `markdown-it` 进行 Markdown → HTML 转换，与文章模块完全一致
- 配置 `html: false`，关闭原始 HTML 标签解析，防止 XSS 攻击
- 项目详情中嵌入的 HTML 标签会被转义显示，不会执行
- 项目封面仅支持 URL 字段，不支持图片上传
- 第一版支持基础 Markdown 语法：标题、列表、代码块、链接、表格、引用等

**项目字段说明：**
- 项目名称、项目简介、项目详细介绍（Markdown）
- 项目类型（Web/AI/IoT/课程设计/竞赛作品等）
- 技术栈、负责人姓名、参与成员（文本）
- 封面 URL（仅 URL，不做上传）
- 代码仓库链接、演示地址、文档地址
- 首页精选标记、排序值

**前台访问路径：**
| 入口 | 地址 |
|------|------|
| 项目列表 | http://localhost/projects |
| 项目详情 | http://localhost/projects/{id} |

**后台访问路径：**
| 入口 | 地址 |
|------|------|
| 后台项目管理 | http://localhost/admin/projects |

**核心接口说明：**

前台公开接口：

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | `/api/projects` | 已发布项目列表 | 无 |
| GET | `/api/projects/featured` | 精选项目列表（首页） | 无 |
| GET | `/api/projects/{id}` | 已发布项目详情 | 无 |

后台管理接口（需 ADMIN 权限）：

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | `/api/admin/projects` | 项目列表（?status=&featured=） | ADMIN |
| GET | `/api/admin/projects/{id}` | 项目详情 | ADMIN |
| POST | `/api/admin/projects` | 创建项目 | ADMIN |
| PUT | `/api/admin/projects/{id}` | 编辑项目 | ADMIN |
| PUT | `/api/admin/projects/{id}/publish` | 发布项目 | ADMIN |
| PUT | `/api/admin/projects/{id}/offline` | 下架项目 | ADMIN |
| DELETE | `/api/admin/projects/{id}` | 删除项目（软删除） | ADMIN |

### 前端高冲击视觉预览页

本项目新增一个前台视觉方向预览页，用于在不替换正式首页、不改后端接口、不改数据库的前提下，预览未来全站的高端动态视觉方向。

访问路径：

```text
http://localhost/design-preview
```

本页内容均为明确的预览表达，不接真实接口，不编造真实成员、真实联系方式或虚假运营数据。页面包含顶部导航预览、高冲击 Hero、动态背景、数据指标、技术方向卡片、精选项目卡片、文章卡片、招新 CTA 流程和页脚声明。

视觉方向：

```text
深色高级感
年轻技术团队
工程指挥舱气质
动态光网与终端窗口
玻璃拟态卡片
清爽但不寡淡
```

动效策略：

- CSS 动画优先，不引入 GSAP、Three.js 等重依赖。
- 背景使用动态网格、光晕、扫描线、低强度噪点纹理和鼠标跟随光效。
- 卡片使用轻微 hover 浮起和 3D 倾斜，按钮使用流光微交互。
- 区块使用滚动进入动画，移动端自动降低动效强度。
- 支持 `prefers-reduced-motion`，用户偏好减少动画时会关闭主要动画和过渡。

新增依赖：无。当前预览页完全使用 Vue 3 + CSS 实现，避免为了背景动效引入过重依赖。

该方向已在 `refactor/apply-high-impact-frontend` 分支推广到正式前台和后台，但 `/design-preview` 仍保留为视觉参考页，便于后续对照设计方向。

### 正式高冲击前端视觉系统

当前正式前台已将预览方向推广到以下页面：

- `/`、`/about`、`/directions`、`/members`、`/projects`、`/projects/{id}`
- `/articles`、`/articles/{id}`、`/recruit`、`/my-application`
- `/login`、`/register`、`/profile`（`/user` 兼容旧路径）、`/contact`、404

后台管理端保留 Element Plus，不混用其他 UI 组件库，并同步改造：

- `/admin/login`、`/admin/dashboard`、`/admin/users`
- `/admin/recruit`、`/admin/articles`、`/admin/projects`
- `/admin/members`、`/admin/directions`、`/admin/site`、`/admin/upload` 占位入口

实现策略：

- 前台沉淀 `web/src/styles/design-tokens.css`、`animations.css`、`markdown.css` 和 `components/app/*` 复用组件。
- 后台新增 `admin-web/src/styles/design-tokens.css` 与 `admin.css`，通过 Element Plus 变量和全局选择器统一表格、弹窗、表单和按钮质感。
- 动效继续以 CSS 为主：动态网格、光晕、扫描线、鼠标跟随光效、滚动进入动画、按钮流光、卡片轻微浮起。
- 所有动效遵守 `prefers-reduced-motion`，移动端降低 hover 和背景动效强度。
- 未新增运行时依赖；本次没有引入 GSAP、Three.js 或新的 UI 组件库。

继续推进到全站深水区时建议：

1. 接入成员、方向、站点配置和文件上传真实接口后，把占位页替换为真实管理页面。
2. 为文章和项目列表增加分页、分类筛选和更稳定的空状态。
3. 对后台表格密度、批量操作、审核流转和草稿编辑体验做专项 UX 打磨。
4. 补充 E2E 页面级回归，覆盖登录、报名、文章、项目和后台审核的主要点击路径。

### 数据库初始化说明

Docker Compose 首次启动时会自动执行 `deploy/mysql/init/01-init.sql`，创建以下表：
- `sys_user` — 系统用户表（含默认管理员 admin/admin123）
- `lab_apply_record` — 招新报名记录表
- `lab_article` — 文章表
- `lab_project` — 项目成果表

如果数据库已初始化过（数据卷已存在），新表不会自动创建。请根据需要执行：

**方式一：重建数据卷（会清空所有数据）**
```bash
cd deploy
docker compose down -v
docker compose --env-file .env up -d --build
```

**方式二：手动执行完整初始化 SQL**
```bash
docker compose exec mysql mysql -u root -p < backend/sql/init.sql
# 输入 MYSQL_ROOT_PASSWORD
```

**方式三：仅追加新表（不影响已有数据）**
```bash
# 仅创建 lab_article 表
docker compose exec -T mysql mysql -u root -p nynu_code_lab < backend/sql/migrations/01-add-article-table.sql
# 仅创建 lab_project 表
docker compose exec -T mysql mysql -u root -p nynu_code_lab < backend/sql/migrations/02-add-project-table.sql
# 为 lab_apply_record 添加非撤回报名唯一约束
docker compose exec -T mysql mysql -u root -p nynu_code_lab < backend/sql/migrations/03-add-apply-active-user-unique-key.sql
# 输入 MYSQL_ROOT_PASSWORD
```

执行 `03-add-apply-active-user-unique-key.sql` 前，如果历史数据中同一用户已有多条非撤回报名记录，需要先人工合并或将重复记录置为 `WITHDRAWN`，否则唯一索引会创建失败。

### 接口验证脚本

项目提供了自动化验证脚本，用于本地开发时快速验证招新报名闭环：

```bash
# 确保 Docker 服务已启动
cd deploy && docker compose --env-file .env up -d --build

# 运行验证脚本
bash scripts/verify-recruitment-flow.sh

# 或指定其他地址
BASE_URL=http://localhost:8080 bash scripts/verify-recruitment-flow.sh
```

脚本会依次验证：注册 → 登录 → 获取当前用户 → 提交报名 → 查看报名 → 修改报名 → 管理员审核 → 状态变更 → 权限隔离。

### 文章闭环验证脚本

```bash
bash scripts/verify-article-flow.sh
```

脚本会依次验证：管理员创建草稿 → 编辑 → 草稿前台不可见 → 发布 → 前台列表可见 → 详情Markdown渲染 → 下架 → 前台不可见 → 删除 → 普通用户权限隔离。

### 项目成果验证脚本

```bash
bash scripts/verify-project-flow.sh
```

脚本会依次验证：管理员创建草稿 → 编辑 → 草稿前台不可见 → 发布 → 前台列表/详情可见 → 精选可见 → Markdown 内容返回 → 下架 → 前台不可见 → 删除 → 普通用户权限隔离 → 游客访问验证。

### 手工验证步骤

如果不使用脚本，也可以按以下步骤手工验证：

1. 访问 http://localhost/register 注册一个普通用户账号
2. 使用注册的账号登录 http://localhost/login
3. 登录后访问 http://localhost/recruit 填写并提交报名表
4. 访问 http://localhost/my-application 查看报名状态（应为「待审核」）
5. 在待审核状态下点击编辑按钮，修改报名信息
6. 访问 http://localhost/admin/login 使用管理员账号登录（`admin` / `admin123`）
7. 进入后台报名管理 http://localhost/admin/recruit
8. 点击行查看报名详情，选择审核状态并填写备注提交
9. 切换回普通用户，刷新 http://localhost/my-application 查看状态变化
10. 尝试用普通用户 Token 访问 `GET /api/admin/applications`，应返回项目统一结构，业务码为 403

## 当前未实现模块

截至本次高冲击视觉推广，前台 `/about`、`/directions`、`/members`、`/contact` 已有正式 Vue 路由页面；后台 `/admin/members`、`/admin/directions`、`/admin/site`、`/admin/upload` 已有占位路由。成员管理、技术方向管理、站点配置和文件上传对应后端接口仍未实现，因此这些页面不接真实数据。项目封面、文章封面和 Markdown 图片当前仅支持外部 URL 字段，不支持本地上传。

文件上传模块未实现，因此当前不存在上传目录、上传静态资源路径或上传文件入库逻辑；生产环境不要把用户上传文件提交到 Git。

## 生产环境安全提示

- 默认管理员账号 `admin/admin123` 只用于本地开发演示，生产环境必须修改密码或创建新的管理员账号后禁用默认账号。
- `deploy/.env` 不允许提交到 Git；生产环境必须填写强随机 `MYSQL_ROOT_PASSWORD`、`MYSQL_PASSWORD` 和 `JWT_SECRET`。
- `JWT_SECRET` 建议使用 `openssl rand -base64 64` 生成，不要使用文档中的占位符。
- Nginx 当前未内置 HTTPS 配置；生产环境需要配置正式域名、SSL 证书、HTTPS 跳转和证书续期策略。
- 证书、私钥、服务器账号、真实数据库密码、真实 Token 密钥不得写入 README、AGENTS 或提交到 Git。
