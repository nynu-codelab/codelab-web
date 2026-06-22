# AGENTS.md — AI 编程助手项目上下文

## 项目基本信息

| 项 | 值 |
|---|---|
| 项目名称 | nynu-code-lab（南阳师范学院 Code Lab 实验室） |
| 定位 | 实验室自建展示站与招新管理系统（非学校官方门户） |
| Git 仓库 | `git@gitee.com:zeng-bohan-66/nynu-code-lab.git` |
| 默认分支 | `main` |

页脚必须保留声明：*"本网站为南阳师范学院 Code Lab 实验室自建展示站，非学校官方门户网站。"*

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Java 17 / Spring Boot 3.x / Maven / MyBatis-Plus 3.5+ / Sa-Token JWT / MySQL 8 / Redis 6.x / Lombok / Knife4j |
| 前台 web | Vue 3 (Composition API + `<script setup>`) / Vite / TypeScript / Pinia / Axios / 手写样式（无 UI 库） |
| 后台 admin-web | Vue 3 / Vite / TypeScript / Pinia / Axios / **Element Plus**（唯一 UI 库，禁止混用其他） |
| 部署 | Docker Compose / Nginx / Let's Encrypt |
| 基础包名 | `cn.edu.nynu.codelab` |

## 目录结构

```
nynu-code-lab/
├── backend/                 # Spring Boot（端口 8080）
│   └── src/main/java/cn/edu/nynu/codelab/
│       ├── common/          # Result, GlobalExceptionHandler
│       ├── config/          # CORS, MyBatis-Plus, Sa-Token
│       └── {module}/        # auth/user/article/project/member/direction/recruit/upload/site/dashboard
│           ├── controller/  ├── service/impl/  ├── dto/  ├── entity/  └── mapper/
├── web/                     # 前台 Vue 3（端口 5173）
│   └── src/
│       ├── api/  ├── router/  ├── stores/  ├── views/  └── styles/
├── admin-web/               # 后台管理 Vue 3 + Element Plus（端口 5174）
│   └── src/
│       ├── api/  ├── router/  ├── stores/  ├── layout/  └── views/
├── deploy/                  # Docker Compose + Nginx + MySQL 初始化脚本
├── docs/                    # 项目说明、部署说明、接口说明、变更记录
├── scripts/                 # 运维与接口验证脚本
└── AGENTS.md                # 本文件
```

## 核心开发规范

### 后端

- **统一返回**: 所有接口用 `Result<T>` 封装（`Result.success(data)` / `Result.error(msg)`）
- **异常**: 全局 `GlobalExceptionHandler` 统一捕获；业务异常 `throw new RuntimeException("msg")`
- **密码**: 必须 BCrypt 加密；返回用户信息时必须清除 `password` 字段
- **认证**: Sa-Token JWT 模式，Token 通过 `Authorization` 头传递；公开接口在 `SaTokenConfig` 排除
- **数据库**: 表前缀 `sys_`/`lab_`；逻辑删除 `deleted`；自动填充 `create_time`/`update_time`；主键自增 `BIGINT`
- **参数校验**: `jakarta.validation`，异常由全局处理器转换

### 前台 web

- **样式**: 现代、清爽、技术感；PC 端深色/科技色主色调；核心页面适配移动端
- **接口**: `src/api/request.ts` Axios 实例，自动附加 `Authorization: Bearer <token>`，401 → 跳转登录
- **路由**: 公开 `/`, `/login`, `/register`；需登录路由通过 `meta.requiresAuth` + `router.beforeEach`
- **状态**: Pinia (`src/stores/user.ts`)，Token 持久化到 `localStorage`
- **Markdown**: 使用 `markdown-it` 渲染，`html:false` 防 XSS

### 后台 admin-web

- **组件**: 统一 Element Plus，`unplugin-auto-import` + `unplugin-vue-components` 按需导入
- **路由**: 除 `/login` 外均需登录 + ADMIN 角色
- **布局**: `AdminLayout.vue`（左侧可折叠菜单 + 顶部栏 + 内容区）

## 角色与权限

| 角色 | 标识 | 权限 |
|---|---|---|
| 游客 | VISITOR | 浏览公开页面 |
| 普通用户 | USER | 注册登录、提交报名、查看自己的报名 |
| 管理员 | ADMIN | 后台管理全部内容、审核报名 |

第一版不做复杂 RBAC，角色存储在 `sys_user.role` 字段。

## 核心业务规则

- **报名**: 登录后提交，一个用户一条非撤回记录；8 种审核状态（PENDING/VIEWED/CONTACTED/PRELIMINARY_PASSED/INTERVIEWING/PASSED/REJECTED/WITHDRAWN）；后台管理员可直接设置任一有效状态并填写审核备注
- **文章**: Markdown 存储，状态：草稿/已发布/已下架，支持分类和标签
- **文件上传**: 7 层纵深防御（Magic Bytes + 路径穿越防护 + 扩展名白名单 + UUID 重命名 + 日期子目录 + 10MB 限制 + 物理删除）
- **分页**: 所有公开列表接口均使用 `PageResult<T>`

## 安全要求

- 密码 BCrypt 加密，禁止明文/MD5
- JWT Token 设置有效期；登出进入 Token 黑名单（Redis/内存条件切换）
- 登录限流：5 次失败锁定 10 分钟（Redis/内存条件切换）
- 游客只能访问公开接口；用户只能操作自己的数据；管理员才可访问后台接口
- 文件上传限制大小和类型，禁止可执行文件
- **禁止**提交数据库密码、Token 密钥、私钥等到 Git

## MVP 范围

**已实现**: 用户认证闭环、招新报名管理（8 状态 + 开关）、文章管理（CRUD + 分页 + 搜索）、项目成果管理（CRUD + 精选）、站点配置管理、核心成员管理、技术方向管理、文件上传、数据统计（9 项指标）、用户管理、前台 13 页面、后台 11 页面、PC 沉浸式视觉增强（Canvas + Three.js + 60fps 优化）

**暂不实现**: 短信/邮件/QQ 通知、评论、点赞收藏、复杂 RBAC、站内消息、在线考试、任务管理、论坛、多租户、支付

## Profile 与环境

| Profile | 数据库 | 场景 |
|---|---|---|
| `local`（默认） | `localhost:3306` | 本地开发 |
| `docker` | `mysql:3306`（容器服务名） | Docker Compose 部署 |

- Docker profile 中禁止写 `localhost`，必须用容器服务名
- 敏感配置通过 `deploy/.env` 环境变量注入
- 修改配置后必须：`docker compose config` → `docker compose up -d --build` → `docker compose ps`

## 当前项目状态

| 项 | 状态 |
|---|---|
| 分支 | `refine/pc-immersive-frontend` |
| 阶段 | 阶段 5.7 上线前 P0/P1 收口完成；待进入 Stage 6 生产环境部署 |
| 数据库 | 10 张表，`deploy/mysql/init/01-init.sql`（哨兵检查 + 首启专用） |
| 容器化 | Docker Compose（MySQL + Redis + Backend + Nginx），均配置 healthcheck + 资源限制 + 日志轮转；上传 volume 权限、Nginx 模板、MySQL 首启哨兵已验证 |
| 文档 | 7 个核心文档 |
| 构建 | backend `mvn test` ✅ / admin-web `npm run build` ✅（2026-06-23 报名详情弹窗对比度 + 全量审核状态修复后复验） / web build ✅ / Docker config/build ✅ / Nginx config ✅ |
| 稳定 tag | `stable-mvp-production-ready-20260622` |

## 当前已知问题

- `/design-preview` 保留为视觉参考页，使用静态预览数据
- 自研 Canvas/Three.js 视觉效果仅在 PC 端启用，移动端降级隐藏
- 后台 ECharts Dashboard chunk > 500 kB，后续可拆包优化
- 后台 `npm audit` 既有 1 moderate + 1 high（非本轮引入）
- 文章标签用 JSON 字符串存储，`lab_article_category`/`lab_article_tag` 表已建但未关联
- 项目成员/负责人用文本字段，未关联用户表
- 文章/项目浏览量无防刷机制
- 后端零单元测试覆盖
- Nginx HTTPS 配置已完成（注释状态），待证书部署后启用
- 备份脚本仅提供模板

## 下一步建议

1. **生产环境部署**（Stage 6）— 填写 `deploy/.env`、配置 HTTPS、域名、防火墙、crontab 备份
2. 文章分类/标签独立管理接口
3. ECharts 体积优化
4. 移动端适配系统验证
5. 浏览量防刷
6. 后端单元测试补齐
7. 项目成员关联用户表

## 验收命令

```bash
# 构建
cd backend && mvn clean package -DskipTests
cd web && npm run build
cd admin-web && npm run build

# Docker
cd deploy
docker compose config
docker compose --env-file .env up -d --build
docker compose ps
curl http://localhost/api/health

# 接口验证
bash scripts/verify-recruitment-flow.sh
bash scripts/verify-article-flow.sh
bash scripts/verify-project-flow.sh

# Git 检查
git branch --show-current && git status --short && git diff --check
```

## 任务纪律

1. 修改前读 `AGENTS.md`、`README.md` 和 `docs/` 下相关文档
2. 不修改无关文件，不擅自删除已有功能或替换技术栈
3. 高风险改动（认证/数据库/Docker/HTTPS/跨模块）必须先启用 Superpowers 对应技能（详见 `CLAUDE.md`）
4. 每轮结束后更新本文件的"当前项目状态"和"已知问题"
5. 提交前执行 `git status --short && git diff --check`
