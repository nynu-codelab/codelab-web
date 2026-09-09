# NYNU Code Lab — 南阳师范学院 Code Lab 实验室

官网与招新管理系统。

> 本网站为南阳师范学院 Code Lab 实验室自建展示站，非学校官方门户网站。

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Java 17, Spring Boot 3.x, Maven, MyBatis-Plus, Sa-Token (JWT) |
| 数据库 | MySQL 8 |
| 缓存 | Redis 7（Token 黑名单 + 登录限流） |
| 前台 | Vue 3, TypeScript, Vite, Pinia, Vue Router 4 |
| 后台 | Vue 3, TypeScript, Vite, Element Plus |
| 反向代理 | Nginx |

## 项目结构

```
nynu-code-lab/
├── backend/                     # Spring Boot 后端 (端口 8080)
├── web/                         # 前台 Vue 3 SPA (端口 5173)
├── admin-web/                   # 后台管理 Vue 3 + Element Plus (端口 5174)
├── deploy/                      # Docker Compose + Nginx + MySQL 初始化
├── scripts/                     # 运维与验证脚本
├── docs/                        # 项目文档
└── README.md
```

## 快速开始

### 本地开发

**前置条件：** Java 17, Maven 3.9+, Node.js 20+, MySQL 8

```bash
# 1. 初始化数据库
mysql -u root -p < deploy/mysql/init/01-init.sql

# 2. 启动后端
cd backend && mvn spring-boot:run

# 3. 启动前台 (端口 5173)
cd web && npm install && npm run dev

# 4. 启动后台 (端口 5174)
cd admin-web && npm install && npm run dev
```

访问：
- 前台：http://localhost:5173
- 后台：http://localhost:5174/admin/
- API 文档：http://localhost:8080/doc.html

### Docker Compose

**前置条件：** Docker Desktop / Docker Engine + Docker Compose v2

```bash
cd deploy
cp .env.example .env          # 编辑 .env 填写密码和密钥
docker compose --env-file .env up -d --build
```

访问：
- 前台：http://localhost
- 后台：http://localhost/admin/
- API 文档：http://localhost/doc.html

## 环境变量

复制 `deploy/.env.example` 为 `deploy/.env`，必填项：

| 变量 | 说明 | 生成方式 |
|------|------|----------|
| `MYSQL_ROOT_PASSWORD` | MySQL root 密码 | `openssl rand -base64 24` |
| `MYSQL_PASSWORD` | 数据库用户密码 | `openssl rand -base64 20` |
| `JWT_SECRET` | JWT 签名密钥 | `openssl rand -base64 64` |

> ⚠️ 默认管理员 `admin`/`admin123` 仅用于本地开发。生产环境必须修改密码。

## 核心功能

- **用户认证**：注册/登录/登出/修改密码，BCrypt 加密，JWT Token
- **招新报名**：在线报名 → 8 种状态流转审核 → 报名状态查询
- **内容管理**：文章 Markdown 编辑/发布、项目成果展示、成员管理、技术方向管理
- **站点配置**：key-value 动态配置，含联系方式、招新开关
- **文件上传**：7 层纵深防御（Magic Bytes + 路径穿越防护 + UUID 重命名）
- **PC 沉浸式视觉**：Three.js WebGL 空间层 + Canvas 粒子网络 + 终端控制台风格

详细功能说明见 [docs/项目说明.md](docs/项目说明.md)。

## 验证脚本

```bash
bash scripts/verify-recruitment-flow.sh   # 招新报名闭环 18 项
bash scripts/verify-article-flow.sh       # 文章闭环 23 项
bash scripts/verify-project-flow.sh       # 项目成果闭环 21 项
```

## Profile

| Profile | 数据库 | Redis 认证 | 使用场景 |
|---|---|---|---|
| `local`（默认） | `localhost:3306` | 内存实现 | 本地开发 |
| `docker` | `mysql:3306` | Redis 实现 | Docker Compose 部署 |

## 工程规范

- 统一协作流程：[CodeLab CONTRIBUTING](https://github.com/nynu-codelab/.github/blob/main/CONTRIBUTING.md)
- 技术与代码规范：[CodeLab Docs](https://github.com/nynu-codelab/docs)
- 当前仓库必须通过 `Backend Test`、`Web Build` 和 `Admin Web Build` 三项 CI
- 前台与后台的 lint / 自动化测试仍在补齐，跟踪于 [#2](https://github.com/nynu-codelab/codelab-web/issues/2)

## 文档索引

| 文档 | 说明 |
|------|------|
| [docs/项目说明.md](docs/项目说明.md) | 项目背景、目标用户、角色权限、功能清单、业务流程 |
| [docs/部署运行说明.md](docs/部署运行说明.md) | 环境要求、本地/Docker 启动、数据库初始化、备份恢复、HTTPS、生产部署 |
| [docs/接口说明.md](docs/接口说明.md) | 接口规范、认证方式、响应格式、核心接口清单 |
| [docs/变更记录.md](docs/变更记录.md) | 阶段变更记录、当前状态、已知问题、下一步计划 |
| [docs/前端视觉组件规范.md](docs/前端视觉组件规范.md) | 视觉方向、组件使用规范、动效原则 |

## 当前状态

- **分支**：`main`
- **阶段**：阶段 5.7 上线前 P0/P1 收口完成，待进入 Stage 6 实际生产部署
- **构建**：backend ✅ / web ✅ / admin-web ✅ / Docker Compose ✅
- **验证**：招新 18/18 ✅ / 文章 23/23 ✅ / 项目 21/21 ✅

## 安全提示

- 默认管理员密码仅用于本地开发，生产必须修改
- `deploy/.env` 不提交 Git，生产必须填写强密码
- 生产环境必须启用 HTTPS
- 证书、私钥、真实密码不得写入 README、AGENTS 或提交到 Git
