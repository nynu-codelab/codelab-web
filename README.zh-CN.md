<div align="center">

<img src="admin-web/src/assets/brand/nynu-code-lab-mark.svg" width="96" alt="NYNU CodeLab" />

# NYNU CodeLab 官网与招新管理系统

**南阳师范学院 CodeLab 实验室的官方网站与招新管理系统。**

一套系统承载实验室的对外展示与纳新全流程：公开站点、八态报名审核流水线，以及内容、成员与站点配置的管理后台。

[![CI](https://github.com/nynu-codelab/codelab-web/actions/workflows/ci.yml/badge.svg)](https://github.com/nynu-codelab/codelab-web/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-3776AB?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Vue](https://img.shields.io/badge/Vue-3-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat-square&logo=typescript&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=flat-square&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
[![License: MIT](https://img.shields.io/badge/License-MIT-4EB1BA?style=flat-square)](LICENSE)

[English](./README.md) | 简体中文

[快速开始](#快速开始) · [架构](#架构) · [验证脚本](#验证脚本) · [参与贡献](#参与贡献)

</div>

---

## 项目简介

CodeLab 是一个学生实验室，每年都要做的一件事就是招新。这个仓库就是支撑这件事的系统——同时它也是一套供实验室成员阅读、扩展并接受评审的**全栈参考工程**。

一次部署，三个端：

- **公开站点**：实验室介绍、文章、项目成果展示与在线报名入口。
- **管理后台**：按八态流水线审核报名，发布文章，管理成员、项目、技术方向与站点配置。
- **API**：两个前端共用的 REST 接口，有完整文档。

## 功能特性

**招新流水线**

- 在线报名 + 完整审核工作流：已提交 → 已查看 → 已联系 → 初审 → 面试 → 终审，支持撤回。
- 报名者随时查看自己的状态；审核者看到带状态流转与历史的报名队列。
- 认证面接入 token 黑名单与登录限流。

**内容管理**

- Markdown 文章编辑与发布，区分草稿 / 已发布状态。
- 项目成果与成员档案作为一等实体管理。
- 技术方向（实验室的分组方向）独立于内容单独管理。
- 站点配置采用 key-value 设计，驱动联系方式与功能开关，改配置不用重新部署。

**管理后台**

- Vue 3 + Element Plus 控制台：仪表盘、各实体视图、上传管理。
- 角色隔离：所有管理面都在 JWT 认证之后。

**平台能力**

- 加固的文件上传：Magic Bytes 校验、路径穿越防护、UUID 重命名，共七层防御。
- 运维规范化：健康检查端点、结构化日志、成文的环境变量契约。
- PC 端沉浸式视觉：Three.js WebGL 空间层、Canvas 粒子网络、终端控制台风格。
- 脚本化端到端验证——三条核心流程共 **62 项自动化检查**（见[验证脚本](#验证脚本)）。

## 架构

```text
┌──────────────┐     ┌──────────────────┐     ┌─────────────┐
│  web (5173)  │     │  backend (8080)  │     │  MySQL 8    │
│  Vue 3 + TS  │◄────┤  Spring Boot 3   │────►│  Redis 7    │
└──────────────┘     │  MyBatis-Plus    │     └─────────────┘
┌──────────────┐     │  Sa-Token (JWT)  │            ▲
│ admin-web    │◄────┘                  │            │
│ (5174)       │     ┌──────────────────┐     ┌────┴────────┐
│ Element Plus │     │  nginx (80)      │────►│ uploads /   │
└──────────────┘     │  反向代理         │     │ 静态资源     │
                     └──────────────────┘     └─────────────┘
```

| 层 | 技术 |
| --- | --- |
| 后端 | Java 17, Spring Boot 3.x, Maven, MyBatis-Plus, Sa-Token (JWT) |
| 数据库 / 缓存 | MySQL 8, Redis 7（Token 黑名单 + 登录限流） |
| 前台 | Vue 3, TypeScript, Vite, Pinia, Vue Router 4 |
| 后台 | Vue 3, TypeScript, Vite, Element Plus |
| 交付 | Docker Compose, Nginx, GitHub Actions CI |

## 快速开始

### Docker Compose（推荐）

```bash
git clone https://github.com/nynu-codelab/codelab-web.git
cd codelab-web/deploy
cp .env.example .env          # 填写密码与 JWT 密钥
docker compose --env-file .env up -d --build
```

| 端 | 地址 |
| --- | --- |
| 前台 | http://localhost |
| 管理后台 | http://localhost/admin/ |
| 接口文档 | http://localhost/doc.html |

### 本地开发

前置条件：Java 17、Maven 3.9+、Node.js 20+、MySQL 8。

```bash
# 1. 初始化数据库
mysql -u root -p < deploy/mysql/init/01-init.sql

# 2. 后端（端口 8080）
cd backend && mvn spring-boot:run

# 3. 前台（端口 5173）
cd web && npm install && npm run dev

# 4. 管理后台（端口 5174）
cd admin-web && npm install && npm run dev
```

## 配置

复制 `deploy/.env.example` 为 `deploy/.env`，填入以下必填项：

| 变量 | 用途 | 生成方式 |
| --- | --- | --- |
| `MYSQL_ROOT_PASSWORD` | MySQL root 密码 | `openssl rand -base64 24` |
| `MYSQL_PASSWORD` | 应用数据库密码 | `openssl rand -base64 20` |
| `JWT_SECRET` | JWT 签名密钥 | `openssl rand -base64 64` |

Spring Profile：`local`（默认，`localhost:3306`，Redis 行为为内存实现）与 `docker`（`mysql:3306`，真实 Redis）。

## 验证脚本

三个脚本针对运行中的部署做端到端验证——**共 62 项检查**：

```bash
bash scripts/verify-recruitment-flow.sh   # 招新流水线，18 项
bash scripts/verify-article-flow.sh       # 文章生命周期，23 项
bash scripts/verify-project-flow.sh       # 项目成果，21 项
```

每一项都断言一个具体行为，失败即脚本失败——这套脚本是回归门禁，不是演示。

## 项目结构

```text
codelab-web/
├── backend/        # Spring Boot 3 API（端口 8080）
├── web/            # 前台 Vue 3 SPA（端口 5173）
├── admin-web/      # 管理后台 Vue 3 + Element Plus（端口 5174）
├── deploy/         # Docker Compose、Nginx、MySQL 初始化脚本
├── scripts/        # 运维与端到端验证脚本
└── docs/           # 项目、部署、接口与设计文档
```

## 文档

| 文档 | 内容 |
| --- | --- |
| [docs/项目说明.md](docs/项目说明.md) | 背景、用户、角色、功能清单、业务流程 |
| [docs/部署运行说明.md](docs/部署运行说明.md) | 环境要求、本地/Docker 启动、数据库初始化、备份、HTTPS、生产部署 |
| [docs/接口说明.md](docs/接口说明.md) | 接口规范、认证方式、响应格式、核心接口清单 |
| [docs/变更记录.md](docs/变更记录.md) | 变更记录与已知问题 |
| [docs/前端视觉组件规范.md](docs/前端视觉组件规范.md) | 视觉方向与组件使用规范 |

## 安全

- 默认管理员账号（`admin` / `admin123`）**仅用于本地开发**，任何真实部署前必须修改。
- `deploy/.env` 不入库，生产环境必须使用强密码。
- 生产环境必须启用 HTTPS；证书与私钥不进仓库。
- 漏洞报告见 [SECURITY 策略](https://github.com/nynu-codelab/.github/blob/main/SECURITY.md)。

## 参与贡献

欢迎贡献——实验室成员与外部贡献者都一样。实验室对所有改动执行同一条流水线：

> 需求 → 技术设计 → 接口契约 → Pull Request → CI → Code Review → 合入

1. 挑选或新建一个 [issue](https://github.com/nynu-codelab/codelab-web/issues)。
2. 建分支（`feature/...` 或 `fix/...`）。
3. 提交信息遵循 [Conventional Commits](https://www.conventionalcommits.org)。
4. 提 PR 并让 CI 通过——`Backend Test`、`Web Build`、`Admin Web Build` 为必需检查。
5. 由 code owner 评审合入。

规范与约定：[nynu-codelab/docs](https://github.com/nynu-codelab/docs) ·
[CONTRIBUTING](https://github.com/nynu-codelab/.github/blob/main/CONTRIBUTING.md)

## 声明

本项目由 CodeLab 学生实验室维护，**不是**南阳师范学院的官方网站。

## 开源协议

[MIT](LICENSE)
