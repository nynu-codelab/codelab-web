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
├── docs/                        # 需求文档、视觉规范、交接文档
├── AGENTS.md
└── README.md
```

## 快速开始

### 方式一：本地开发（使用本机中间件）

**前置条件：** Java 17, Maven 3.9+, Node.js 20+, MySQL 8

1. 初始化数据库：

```bash
mysql -u root -p < deploy/mysql/init/01-init.sql
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
mysql -u root -p < deploy/mysql/init/01-init.sql
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

- `/design-preview` 继续保留为视觉参考页，同时接入本轮 PC 沉浸式背景层。
- 背景由动态网格、扫描线、低强度噪点、自研 Canvas 粒子网络、代码雨和能量流共同组成。
- Canvas 动效仅在 PC 端（`window.innerWidth >= 1024`）启用，并在组件卸载时取消动画帧、解绑事件监听。
- 继续支持 `prefers-reduced-motion`，用户偏好减少动画时会关闭主要 Canvas 与入场动画。
- 本轮不使用 Figma；正式首页 Hero 已引入 Three.js 作为 PC 端 WebGL 空间层，`prefers-reduced-motion` 或窄屏下自动降级。

该方向已在 `refine/pc-immersive-frontend` 分支继续深化到正式前台和后台，但 `/design-preview` 仍保留为视觉参考页，便于后续对照设计方向。

### PC 沉浸式前端视觉增强

当前正式前台在既有高冲击视觉系统上继续增强 PC 端沉浸式表现，覆盖以下页面：

- `/`、`/about`、`/directions`、`/members`、`/projects`、`/projects/{id}`
- `/articles`、`/articles/{id}`、`/recruit`、`/my-application`
- `/login`、`/register`、`/profile`（`/user` 兼容旧路径）、`/contact`、`/design-preview`、404

后台管理端保留 Element Plus，不混用其他 UI 组件库，并同步增强：

- `/admin/login`、`/admin/dashboard`、`/admin/users`
- `/admin/recruit`、`/admin/articles`、`/admin/projects`
- `/admin/members`、`/admin/directions`、`/admin/site`、`/admin/upload` 占位入口

实现策略：

- 前台沉淀 `web/src/styles/design-tokens.css`、`animations.css`、`markdown.css` 和 `components/app/*` 复用组件。
- 新增 `ParticleUniverse`、`CodeRainCanvas`、`EnergyFlowBackground`、`TerminalHero`、`CommandConsole`、`BuildPipeline`、`LabControlPanel`、`GitBranchMap`、`DataCounter` 等前台组件。
- 本轮已安装并调用 `taste-skill` / `redesign-skill`，在不推翻当前视觉方向的前提下继续增强审美细节：新增 `LabSignalField` 前景信号场，强化 Hero 终端状态条、逐行启动、光标闪烁、玻璃卡片 cursor spotlight、项目卡片工程 HUD、文章知识库轨道、方向卡片节点路线。
- 本轮继续使用 `frontend-design` 规则，将首屏“实验室空间”作为唯一高强度签名元素；新增 `LabSpatialScene` Three.js 组件，渲染空间网格、轨道环、节点网络和数据包，相机随鼠标产生轻量视差。
- `LabSignalField` 从静态装饰升级为 Canvas 信号网络，包含节点连线、脉冲扩散、数据流向和鼠标扰动，并在组件卸载时清理 RAF、resize、pointermove。
- 视觉组件使用规范已沉淀到 `docs/前端视觉组件规范.md`，后续新增页面优先复用现有 CodeLab 组件，不重新发明一套视觉语言。
- 首页 Hero 重做为 PC 大屏沉浸式终端控制台：左侧品牌与 CTA，右侧 boot terminal、Lab Control Center 和构建流水线。
- 项目卡片加入 branch、commit、test、deploy、pipeline、coverage、health 等工程 HUD；文章卡片加入 research log、Markdown 安全和知识库地形线；方向卡片加入研究矩阵和节点路线。
- 文章详情和项目详情继续使用 `markdown-it`，并保持 `html:false`，不直接渲染未清洗 HTML。
- 后台通过 `admin-web/src/styles/design-tokens.css` 与 `admin.css` 统一表格、弹窗、表单和按钮质感，并在数据概览页加入 ECharts 模块接入状态图。
- 本轮后台继续保留 Element Plus 并深度定制，不迁移组件库；管理壳层增加顶部遥测、系统时间、命令状态和右侧模块状态轨，数据概览增加构建通道、测试矩阵、部署监视和工程运行矩阵；表格、表单和弹窗逻辑不变。
- PC 优先：复杂 Canvas 动效只在 PC 端启用；移动端本轮只保证不严重白屏、不横向崩溃，并关闭或简化重动效。
- Logo 方向已确认并落地：原始确认稿保存于 `docs/assets/logo/nynu-code-lab-selected-logo.png`，前台和后台分别接入 `nynu-code-lab-mark.svg` 与 favicon，并加入轻量状态点呼吸和 hover 扫光效果。

新增依赖及用途：

| 模块 | 依赖 | 用途 |
|---|---|---|
| 前台 `web` | `gsap` | 首页终端 Hero 入场时间线动画 |
| 前台 `web` | `countup.js` | 指标数字动态计数 |
| 前台 `web` | `@lucide/vue` | Git、CPU、状态等工程图标 |
| 前台 `web` | `three` | 首页 Hero 的 PC 端 WebGL 实验室空间、节点网络和相机视差 |
| 前台 `web` dev | `@types/three` | 为 Three.js 组件提供 TypeScript 类型声明 |
| 后台 `admin-web` | `echarts` | 数据概览页模块状态可视化 |

本轮后台未新增依赖，继续复用 Element Plus 和 ECharts；前台新增 Three.js 仅用于 Hero 空间层，不改变接口和业务逻辑。

构建与验证：

- 本轮已执行 `web npm install && npm run build && npm run type-check`，通过；Vite 提示异步 `LabSpatialScene` WebGL chunk 约 502 kB，大于 500 kB 提醒阈值，但构建成功，首页主 chunk 已保持拆分。
- 本轮已执行 `admin-web npm install && npm run build`，通过；后台仍有既有 1 个 moderate、1 个 high audit 提示，Vite 仍提示 ECharts Dashboard chunk 大于 500 kB，并出现 `@vueuse/core` PURE 注释移除提醒，但构建成功。
- 本轮已执行根目录 `git diff --check`，通过。
- 本轮已执行 `rg "console.log|debugger" web/src admin-web/src`，无命中。
- 本轮已通过浏览器验证 `http://127.0.0.1:5173/`：桌面 1440×1000 下 `.lab-spatial-scene` canvas 可见，截图中 canvas 区域像素非空；移动 390×844 下 Three.js 与 `LabSignalField` 均降级隐藏，页面无横向溢出。
- 前台和后台当前均未配置 `npm run lint` 脚本，本轮未执行 lint。
- 以下为当前分支既有全量回归记录，非本轮重复执行项：
- `backend mvn clean package -DskipTests` 已通过。
- `docker compose --env-file .env config`、`up -d --build`、`ps` 已通过；本轮曾因 Colima Docker socket 失联先重启 Colima，随后 Docker 回归通过。
- 页面访问验收：前台 `/`、`/design-preview`、`/about`、`/directions`、`/members`、`/projects`、`/articles`、`/recruit`、`/login`、`/register`、`/profile`、`/my-application`、`/contact` 和后台 `/admin/`、`/admin/login`、`/admin/recruit`、`/admin/articles`、`/admin/projects`、`/admin/members`、`/admin/directions`、`/admin/site`、`/admin/upload` 均返回 200。
- 业务脚本：招新 18/18、文章 23/23、项目成果 21/21 均通过。

继续推进到全站深水区时建议：

1. 人工在 PC 大屏打开首页、项目、文章、招新、登录和后台数据概览，确认动效强度与信息密度。
2. 评估后台 ECharts chunk 体积，必要时改为更轻的 CSS 图表或动态拆包。
3. 接入成员、方向、站点配置和文件上传真实接口后，把占位页替换为真实管理页面。
4. 为文章和项目列表增加分页、分类筛选和更稳定的空状态。
5. 补充 E2E 页面级回归，覆盖登录、报名、文章、项目和后台审核的主要点击路径。

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
docker compose exec -T mysql mysql -u root -p < deploy/mysql/init/01-init.sql
# 输入 MYSQL_ROOT_PASSWORD
```

> 注意：该脚本使用 DROP TABLE IF EXISTS，会重建所有表并清空数据。如需保留现有数据，请先导出备份。

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
