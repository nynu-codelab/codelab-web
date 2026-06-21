# AGENTS.md — AI 编程助手项目上下文

## 项目基本信息

| 项 | 值 |
|---|---|
| 项目名称 | nynu-code-lab |
| 全称 | Nanyang Normal University Code Lab |
| 中文名称 | 南阳师范学院 Code Lab 实验室 |
| Git 仓库 | git@gitee.com:zeng-bohan-66/nynu-code-lab.git |
| 默认分支 | main |
| 当前工作分支 | release/mvp-production-readiness |

## 项目定位

本项目是南阳师范学院 Code Lab 实验室**自建展示站与招新管理系统**，不是学校官方门户网站。

核心目标：
- 实验室对外展示（技术方向、核心成员、项目成果、学习文章）
- 在线招新报名与审核
- 后台内容管理

网站页脚必须保留声明：

> 本网站为南阳师范学院 Code Lab 实验室自建展示站，非学校官方门户网站。

## 技术栈约定

### 后端

| 项 | 选型 |
|---|---|
| 语言 | Java 17 |
| 框架 | Spring Boot 3.x |
| 构建 | Maven |
| 数据库 | MySQL 8 |
| ORM | MyBatis-Plus 3.5+ |
| 认证 | Sa-Token（JWT 模式） |
| 密码加密 | BCrypt（通过 Sa-Token 内置或 Spring Security Crypto） |
| 接口文档 | Knife4j / OpenAPI 3 |
| 简化代码 | Lombok |
| 基础包名 | `cn.edu.nynu.codelab` |

### 前台 web

| 项 | 选型 |
|---|---|
| 框架 | Vue 3（Composition API + `<script setup>`） |
| 构建 | Vite |
| 语言 | TypeScript |
| 路由 | Vue Router 4 |
| 状态 | Pinia |
| HTTP | Axios |
| UI 组件 | 无（手写样式，现代清爽技术团队风） |

### 后台 admin-web

| 项 | 选型 |
|---|---|
| 框架 | Vue 3（Composition API + `<script setup>`） |
| 构建 | Vite |
| 语言 | TypeScript |
| 路由 | Vue Router 4 |
| 状态 | Pinia |
| HTTP | Axios |
| UI 组件 | **Element Plus**（已选定，全项目统一） |

> 后台 UI 组件库只能在 Element Plus 和 Arco Design Vue 中选择一种，选定后不得混用。当前选择：Element Plus。

## 目录结构约定

```
nynu-code-lab/
├── backend/                 # Spring Boot 后端（端口 8080）
│   ├── pom.xml
│   ├── sql/                 # 数据库初始化脚本
│   └── src/main/java/cn/edu/nynu/codelab/
│       ├── common/          # Result, GlobalExceptionHandler
│       ├── config/          # CORS, MyBatis-Plus, Sa-Token
│       ├── auth/            # 认证：注册、登录、获取当前用户
│       ├── user/            # 用户模块
│       ├── article/         # 文章模块（第二阶段）
│       ├── project/         # 项目模块（第二阶段）
│       ├── member/          # 成员模块（第二阶段）
│       ├── direction/       # 技术方向模块（第二阶段）
│       ├── recruit/         # 招新报名模块（第二阶段）
│       ├── upload/          # 文件上传模块（第二阶段）
│       └── site/            # 站点配置模块（第二阶段）
├── web/                     # 前台 Vue 3（端口 5173）
│   └── src/
│       ├── api/             # 接口封装（request.ts, auth.ts）
│       ├── router/          # Vue Router 配置
│       ├── stores/          # Pinia 状态（user.ts）
│       ├── views/           # 页面组件
│       │   ├── home/        # 首页
│       │   ├── about/       # 实验室介绍（第二阶段）
│       │   ├── directions/  # 技术方向（第二阶段）
│       │   ├── members/     # 成员展示（第二阶段）
│       │   ├── projects/    # 项目成果（第二阶段）
│       │   ├── articles/    # 学习文章（第二阶段）
│       │   ├── recruit/     # 招新报名（第二阶段）
│       │   ├── user/        # 个人中心
│       │   └── contact/     # 联系我们（第二阶段）
│       └── styles/          # 全局样式
├── admin-web/               # 后台管理 Vue 3 + Element Plus（端口 5174）
│   └── src/
│       ├── api/             # 接口封装
│       ├── router/          # 路由 + 登录守卫
│       ├── stores/          # Pinia 状态
│       ├── layout/          # AdminLayout（侧边栏+顶栏）
│       └── views/
│           ├── dashboard/   # 数据概览
│           ├── users/       # 用户管理
│           ├── recruit/     # 报名管理
│           ├── articles/    # 文章管理（第二阶段）
│           ├── projects/    # 项目管理（第二阶段）
│           ├── members/     # 成员管理（第二阶段）
│           ├── directions/  # 技术方向管理（第二阶段）
│           └── site/        # 站点配置（第二阶段）
├── docs/                    # 需求文档、接口文档、数据库设计
├── deploy/                  # Nginx、Docker、部署脚本
├── AGENTS.md                # 本文件
├── README.md
└── .gitignore
```

## 后端开发规范

### 包结构约定

每个业务模块按如下结构组织：

```
cn.edu.nynu.codelab.<module>/
├── controller/    # 接口控制器
├── service/       # 业务接口
│   └── impl/      # 业务实现
├── dto/           # 请求/响应 DTO
├── entity/        # 数据库实体
└── mapper/        # MyBatis-Plus Mapper
```

### 统一返回格式

所有接口使用 `Result<T>` 封装：

```java
Result.success(data)   // 成功，code=200
Result.error(msg)      // 失败，code=500
Result.error(code, msg)
```

### 异常处理

- 全局异常由 `GlobalExceptionHandler` 统一捕获，返回 `Result` 格式
- 业务异常直接 `throw new RuntimeException("message")`
- 参数校验使用 `jakarta.validation`，异常由全局处理器转换

### 密码安全

- 密码**必须**使用 BCrypt 加密存储
- 禁止明文存储密码
- 禁止使用简单 MD5 存储密码
- 返回用户信息时**必须**清除 `password` 字段

### 认证鉴权

- 使用 Sa-Token JWT 模式
- Token 通过 `Authorization` 请求头传递，前缀为空
- 公开接口在 `SaTokenConfig` 中排除
- 角色通过 `StpInterface` 返回，第一版只有 USER 和 ADMIN

### 数据库

- 表名前缀：系统表 `sys_`，业务表 `lab_`
- 逻辑删除字段：`deleted`，1=删除 0=未删除
- 自动填充：`create_time`, `update_time`
- 主键：自增 `BIGINT`

## 前台 web 开发规范

### 样式风格

- 现代、清爽、技术感、年轻化
- 不适合做传统学校门户风格
- 首页主色调偏深色/科技色
- PC 端适配，核心页面适配移动端

### 接口调用

- 统一通过 `src/api/request.ts` 的 Axios 实例
- 请求拦截器自动附加 `Authorization: Bearer <token>`
- 响应拦截器统一处理 401 → 跳转登录页
- API 方法定义在 `src/api/` 下按模块拆分

### 路由

- 公开路由：`/`, `/login`, `/register`
- 需登录路由：`/user`（通过 `router.beforeEach` 守卫）
- 路由元信息 `meta.requiresAuth` 标记是否需要登录

### 状态管理

- 用户状态在 `src/stores/user.ts`（Pinia）
- 持久化 Token 到 `localStorage`

## 后台 admin-web 开发规范

### UI 组件

- 统一使用 Element Plus 组件
- 通过 `unplugin-auto-import` 和 `unplugin-vue-components` 按需导入
- 不允许引入其他 UI 组件库

### 路由守卫

- 所有后台路由（除 `/login`）都需要登录
- 未登录 → 跳转 `/login`
- 登录后仅 ADMIN 角色可访问

### 布局

- 使用 `AdminLayout.vue`：左侧可折叠菜单 + 顶部栏 + 内容区
- 菜单项：数据概览、用户管理、报名管理（后续扩展）

## 核心业务范围

### 已实现（第一阶段）

| 模块 | 范围 |
|---|---|
| 用户认证 | 注册、登录、获取当前用户（JWT Token） |
| 用户实体 | `sys_user` 表，基础 CRUD |
| 统一返回 | `Result<T>` + 全局异常处理 |

### 待实现（第二阶段及以后）

| 模块 | 内容 |
|---|---|
| 招新报名 | 提交报名、查看我的报名、后台审核 |
| 技术方向 | CRUD + 前台展示 |
| 核心成员 | CRUD + 前台展示 |
| 项目成果 | CRUD + 前台展示 + 首页推荐 |
| 学习文章 | Markdown 编辑/渲染、分类、标签、列表/详情 |
| 站点配置 | 网站名称、标语、联系方式、二维码 |
| 文件上传 | 封面、头像、截图、Banner |
| 数据概览 | 统计数据卡片 |

## 角色与权限说明

| 角色 | 标识 | 权限 |
|---|---|---|
| 游客 | VISITOR | 浏览公开页面，不可报名 |
| 普通用户 | USER | 注册登录、提交报名、查看自己的报名状态 |
| 管理员 | ADMIN | 后台管理全部内容、审核报名 |

第一版不做复杂 RBAC，角色存储在 `sys_user.role` 字段。

## 报名业务规则

- 必须登录后才能提交报名
- 一个用户只能存在一条报名记录
- 报名状态：待审核 → 初筛通过 → 面试中 → 已通过/未通过/已撤回
- 管理员可修改状态并填写审核备注
- 第一版不做短信/邮件/QQ 通知
- 报名表字段见 `docs/requirements.md` 第 6 节

## 文章 Markdown 规则

- 文章正文使用 Markdown 格式存储
- 后端保存 Markdown 原文
- 前台通过 Markdown 渲染组件展示（含代码高亮）
- 文章状态：草稿、已发布、已下架
- 支持分类和标签

## 第一版 MVP 范围

### 必须实现

**前台页面**：首页、实验室介绍、技术方向、成员展示、项目展示、文章列表/详情、招新页面、注册、登录、个人中心、我的报名、联系我们

**后台页面**：管理员登录、数据概览、用户管理、报名管理、文章管理、项目管理、成员管理、技术方向管理、站点配置、文件上传

**后端接口**：见 `docs/requirements.md` 第 10 节接口模块规划

## 第一版暂不实现的功能

- 短信验证码
- 邮件通知
- QQ 通知
- 评论系统
- 点赞收藏
- 文章审核流
- 复杂 RBAC 权限
- 站内消息
- 在线考试
- 任务管理
- 实验室内部论坛
- 多租户
- 支付功能

## 安全要求

| 类别 | 要求 |
|---|---|
| 密码 | BCrypt 加密，禁止明文/MD5 |
| Token | JWT 模式，设置有效期 |
| 登录接口 | 需要基础限流 |
| 用户状态 | 支持禁用，禁用后不可登录 |
| 权限 | 游客只能访问公开接口；普通用户只能操作自己的数据；管理员才可访问后台接口 |
| 后台接口 | 必须统一鉴权 |
| 数据校验 | 注册字段、手机号格式、报名表单、文章标题/Markdown 内容、项目名称 |
| 文件上传 | 限制大小和类型，禁止可执行文件，重命名存储 |
| 敏感信息 | **禁止**提交数据库密码、Token 密钥、服务器账号、私钥等到 Git |

## Git 分支与提交规范

### 分支策略

- `main`：稳定分支，**不允许 AI 直接在 main 上大改**
- `feature/*`：功能开发分支
- AI 代码生成和项目初始化在 `feature/init-project-skeleton` 分支进行

### 提交规范

- 格式：`type: description`
- 类型：`feat`, `fix`, `docs`, `refactor`, `style`, `chore`
- 禁止提交：`node_modules/`, `dist/`, `target/`, `.env`, 敏感配置

## 每次任务前必须执行

AI 在每次开发任务开始前必须执行以下检查：

1. **阅读关键文档**：`README.md`、`AGENTS.md`、`docs/requirements.md`
2. **检查 Git 状态**：

```bash
git branch --show-current
git status --short
```

3. **如果工作区存在未提交改动**，必须先向用户说明当前改动内容，不允许直接覆盖或丢弃。

## 每次任务后必须执行

AI 在每次开发任务完成后必须：

1. 同步更新 `AGENTS.md`（更新"当前项目状态"、"最近一次任务记录"、"当前已知问题"、"下一步建议"等节）
2. **禁止只修改代码而不更新 AGENTS.md**
3. 执行验收命令确认无回归

## 当前项目状态

| 项 | 状态 |
|---|---|
| 分支 | `release/mvp-production-readiness` |
| 阶段 | 第六阶段：上线前全量验收 + 安全加固 + 准生产部署准备（当前） |
| 后端 | Spring Boot 项目已初始化，认证闭环已实现，招新报名模块已实现，文章管理模块已实现（草稿/发布/下架/删除全流程），项目成果模块已实现（草稿/发布/下架/删除全流程）；权限异常已统一返回业务码 401/403 |
| 前台 web | Vue 3 项目已初始化，首页/登录/注册/个人中心/招新报名/我的报名/文章列表/文章详情/项目列表/项目详情页面已实现，Markdown 渲染已实现，首页精选项目已实现；本地开发端口 5173 |
| 后台 admin-web | Vue 3 + Element Plus 已初始化，登录/布局/数据概览/用户管理/报名管理/文章管理/项目管理页面已实现；本地开发端口 5174 |
| 数据库 | `sys_user` + `lab_apply_record` + `lab_article` + `lab_project` 表 DDL 已编写，init.sql 已更新；`lab_apply_record` 已增加 `uk_apply_active_user` 非撤回报名唯一约束 |
| 容器化 | Docker Compose + Nginx 统一托管方案已完成，MySQL/Redis/Backend/Nginx 均已配置 |
| 健康检查 | `GET /api/health` 已实现，免鉴权 |
| Profile | `local`（本地开发，默认）/ `docker`（容器部署）双 profile 支持 |
| 构建状态 | backend ✅ web ✅ admin-web ✅ docker compose ✅ |
| 接口验证 | 招新报名闭环 18/18 ✅ / 文章闭环 23/23 ✅ / 项目成果闭环 21/21 ✅ |
| 页面访问 | `/`、登录注册、招新、我的报名、文章、项目、后台路由经 Nginx history fallback 均返回 200；成员/方向/联系/上传相关路由仅返回 SPA 壳，功能未实现 |
| 稳定 tag | stable-mvp-production-ready-20260622 |

## 容器化开发规范

### Profile 选择

| Profile | 数据库连接 | 使用场景 |
|---|---|---|
| `local`（默认） | `localhost:3306` | 本地开发，本机 MySQL |
| `docker` | `mysql:3306`（容器服务名） | Docker Compose 部署 |

- 本地开发时无需设置 `SPRING_PROFILES_ACTIVE`，默认使用 `local` profile
- Docker Compose 启动时自动注入 `SPRING_PROFILES_ACTIVE=docker`
- Docker profile 中**禁止写 `localhost`** 作为中间件地址，必须使用 Docker Compose service name

### 环境变量优先级

敏感配置（数据库密码、JWT 密钥等）通过环境变量覆盖：

```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:mysql}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:nynu_code_lab}?...
    username: ${MYSQL_USER:nynu}
    password: ${MYSQL_PASSWORD:change_me}
```

- Docker 环境：通过 `deploy/.env` 注入
- 本地开发：使用 `application-local.yml` 中的默认值
- 禁止在配置文件中硬编码真实密码

### 每次修改配置后必须检查

```bash
cd deploy
docker compose config              # 验证配置语法
docker compose up -d --build       # 重建和启动
docker compose ps                  # 确认所有服务运行
```

## 最近一次任务记录

| 日期 | 任务 | 变更 |
|---|---|---|
| 2026-06-22 | 第六阶段上线前全量验收 + 安全加固 | 新建 `release/mvp-production-readiness`；补齐 `docs/requirements.md` 并修复需求文档重复命名；权限异常从通用 500 改为统一业务码 401/403；`lab_apply_record` 新增 `active_user_id` 生成列与 `uk_apply_active_user` 唯一约束并新增 `03-add-apply-active-user-unique-key.sql`；同步 `backend/sql/init.sql` 与 `deploy/mysql/init/01-init.sql`；`deploy/.env.example` 敏感变量改为空并补充 `SERVER_PORT`；新增根目录 `.env.example`；本地 Vite 端口统一为 web 5173 / admin-web 5174；修正验证脚本权限断言；README/deploy 文档补齐准生产环境变量、迁移、Markdown、安全和未实现模块说明；Docker Compose、页面访问、三闭环脚本、权限、SQL、Markdown、Nginx 验收通过 |
| 2026-06-22 | 第四阶段项目成果展示 | 新增 lab_project 表；后端新增 project 模块（Project entity/mapper/service + ProjectController + AdminProjectController）；前台新增项目列表(/projects)和项目详情(/projects/:id)页面并使用 markdown-it 渲染（html:false 防XSS）；首页新增精选项目区域(GET /api/projects/featured)；后台新增项目管理页面（列表/创建/编辑/发布/下架/删除）；SaTokenConfig 排除 /api/projects/** 公开访问；更新 init.sql；新增 02-add-project-table.sql 迁移脚本；新增 scripts/verify-project-flow.sh |
| 2026-06-22 | 工程级重命名 | 旧工程标识 `nynu-se-lab` → 新工程标识 `nynu-code-lab`；正式展示名：南阳师范学院 Code Lab 实验室 / NYNU Code Lab；数据库名 `nynu_se_lab` → `nynu_code_lab`；Java 包名 `cn.edu.nynu.selab` → `cn.edu.nynu.codelab`；Maven artifactId `selab-backend` → `nynu-code-lab-backend`；前端 package name 同步更新；Docker container/network/image name 同步更新；根目录从 `nynu-se-lab` 重命名为 `nynu-code-lab`；Docker service key 保留不变（mysql/redis/backend/nginx）；Git remote 已更新为 `git@gitee.com:zeng-bohan-66/nynu-code-lab.git`；README/AGENTS/docs 全部同步更新；三模块构建验证通过 |
| 2026-06-22 | 容器化验收 + 提交固定 | 全链路验收通过：docker compose config ✅ / 4容器正常启动 ✅ / backend 1.749s 启动 ✅ / /api/health 200 ✅ / web 200 ✅ / admin-web 200 ✅ / localhost残留检查通过 ✅ / .gitignore 敏感文件排除 ✅ / MyBatisPlusSpringFix 兼容性修复确认有效 / Spring Boot 3.3.7 + MyBatis-Plus 3.5.16 / 提交 chore: add dockerized development environment |
| 2026-06-22 | 容器化改造 | 拆分 application.yml 为 local/docker profile；新增 GET /api/health 健康检查；创建 backend/web/admin-web/deploy/nginx 四个 Dockerfile；创建 deploy/docker-compose.yml（MySQL + Redis + Backend + Nginx）；创建 Nginx 统一托管配置；创建 .env.example；更新 .gitignore；更新 README.md / AGENTS.md / deploy/README.md；修复 admin-web 路由 base 支持 /admin/ 路径 |
| 2026-06-22 | 第一阶段项目骨架初始化 | 创建后端(Spring Boot)、前台(Vue3)、后台(Vue3+ElementPlus)三个项目，完成注册/登录/获取当前用户认证闭环，62个文件 |
| 2026-06-22 | 招新报名闭环 MVP | 新增 `lab_apply_record` 表；后端新增 recruit 模块（ApplyRecord entity/mapper/service + ApplyController + AdminApplyController）；前台新增招新报名页面(`/recruit`)和我的报名页面(`/my-application`)；后台报名管理重写为完整功能（列表/筛选/详情/审核）；修复 admin-web 响应拦截器 code 校验 bug (0→200)；更新 README.md 和 AGENTS.md |
| 2026-06-22 | 第二阶段收尾验收 | 全链路 API 验证 18/18 通过；三模块构建验证通过（backend mvn ✅ / web vite ✅ / admin-web vite ✅）；Docker Compose 4 容器正常启动；/api/health / web / admin-web / doc.html 全部 200；localhost 残留检查通过；新增 scripts/verify-recruitment-flow.sh 自动化验证脚本；README.md 新增安全提示、接口验证脚本说明、手工验证步骤；AGENTS.md 更新验收记录；提交 tag mvp-recruitment-workflow-20260622 |
| 2026-06-22 | 第三阶段文章管理+Markdown学习分享 | 新增 lab_article 表；后端新增 article 模块（Article entity/mapper/service + ArticleController + AdminArticleController）；SaTokenConfig 排除 /api/articles/** 公开访问；前台新增文章列表(/articles)和文章详情(/articles/:id)页面并使用 markdown-it 渲染（html:false 防XSS）；后台新增文章管理页面（列表/创建/编辑/发布/下架/删除）；HomeView 导航"学习文章"改为真实路由；更新 init.sql；新增 01-add-article-table.sql 迁移脚本；新增 scripts/verify-article-flow.sh（23/23 PASS） |
| 2026-06-22 | 全量回归验收 | 招募+文章双脚本全通过（18+23=41项）；三模块构建全通过；Docker Compose 4容器全healthy；localhost残留检查通过；Markdown XSS 安全确认（html:false）；DB 三表完整验证；README/AGENTS/需求文档同步更新；scripts/ 目录恢复（verify-recruitment-flow.sh + verify-article-flow.sh） |

## 当前已知问题

- 数据库密码和 JWT 密钥已改为环境变量注入，`deploy/.env.example` 中敏感变量留空；`deploy/.env` 需手动填写强密码和强 JWT 密钥
- 前台/后台用户管理页面仍为占位页面（UsersView.vue），未对接真实 API
- 缺少修改密码接口（需求文档中已规划 `POST /api/auth/change-password`）
- 缺少退出登录接口（需求文档中已规划 `POST /api/auth/logout`）
- Redis 服务已在 Docker Compose 中预留，但后端 pom.xml 未引入 Redis 依赖，当前业务未使用
- Nginx 未配置 HTTPS，生产环境需额外处理 SSL 证书
- 前端容器构建跳过 `vue-tsc` 类型检查以加速构建，CI 中应单独运行类型检查
- MyBatis-Plus 与 Spring 6.1+ 存在 `factoryBeanObjectType` 类型不兼容，通过 `MybatisPlusSpringFix`（BeanFactoryPostProcessor）绕过，待上游修复后移除
- 报名表 `lab_apply_record` 已通过 `uk_apply_active_user` 约束非撤回报名唯一；历史库执行迁移前如果已有重复非撤回记录，需要先人工清理
- 文章标签使用 JSON 字符串存储，未做独立标签表或标签管理功能
- 文章封面仅支持 URL 字段，不支持文件上传；Markdown 图片同样依赖外部 URL
- 文章分类使用自由文本字段，未做分类管理功能
- 文章浏览量直接在详情接口中递增，无防刷机制
- 前台文章列表未做分页，文章数量较多时性能可能不足
- 项目封面仅支持 URL 字段，不支持文件上传
- 项目成员/负责人使用文本字段，未与系统用户表关联
- 项目浏览量直接在详情接口中递增，无防刷机制
- 前台项目列表未做分页
- 前台 `/contact`、`/members`、`/directions`、`/about` 尚无真实 Vue 路由页面；Nginx 刷新返回 200 仅代表 SPA fallback 正常
- 后台成员、方向、站点配置、上传页面和对应后端接口未实现
- 文件上传模块未实现，当前没有上传目录、上传权限控制或上传静态资源映射

## 下一步建议

1. 将 `release/mvp-production-readiness` 部署到服务器准生产环境演示前，先填写生产 `.env` 并修改默认管理员密码
2. 生产 Nginx 配置 HTTPS、正式域名、证书续期和 HTTP 到 HTTPS 跳转
3. 实现技术方向 CRUD + 前台展示
4. 实现核心成员 CRUD + 前台展示
5. 完善修改密码和退出登录功能
6. 前台个人中心引入更多真实数据
7. 后台用户管理页面实现真实数据对接
8. 完善报名状态流转（增加更多中间状态约束和校验）
9. 引入 Redis 业务依赖（如 Session 共享、缓存）
10. 文章列表加分页、分类筛选优化
11. 项目列表加分页优化
12. 实现文件上传功能（文章封面、项目封面、成员头像等）

## 验收命令

```bash
# 后端编译
cd backend && mvn clean compile -q

# 后端打包
cd backend && mvn clean package -DskipTests

# 前台构建
cd web && npm run build

# 后台构建
cd admin-web && npm run build

# Docker Compose 验证
cd deploy
docker compose config
docker compose --env-file .env up -d --build
docker compose ps
docker compose logs --tail=100 backend
curl http://localhost/api/health

# 检查 Git 状态
git branch --show-current && git status --short

# 接口验证脚本
bash scripts/verify-recruitment-flow.sh
bash scripts/verify-article-flow.sh
bash scripts/verify-project-flow.sh

# 已初始化数据库的增量迁移
cd deploy
docker compose --env-file .env exec -T mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" "$MYSQL_DATABASE"' < ../backend/sql/migrations/03-add-apply-active-user-unique-key.sql
```
