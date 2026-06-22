# Nanyang Normal University Code Lab — Claude Code 项目配置

完整项目上下文见 `AGENTS.md`。本项目使用 Superpowers Skills + gstack 技能集进行 AI 辅助开发。

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Spring Boot 2.7.x + MyBatis Plus + SaToken + MySQL 8.0 + Redis 6.x |
| 前端 (用户) | Vue 3 + Vite + Pinia + Element Plus + ECharts |
| 前端 (管理) | Vue 3 + Element Plus Admin |
| 部署 | Docker Compose + Nginx + Let's Encrypt |
| 包管理 | Maven (后端) + pnpm (前端) |

## 项目目录

```
nynu-code-lab/
├── backend/          # Spring Boot 后端 (api/, business/, core/, plugin/, claude-code/)
├── web/              # Vue3 用户端 SPA
├── admin-web/        # Vue3 管理后台
├── deploy/           # Docker Compose + Nginx 配置 + MySQL 初始化脚本
├── docs/             # 项目文档
└── scripts/          # 运维脚本
```

## AI 行为准则

### Superpowers 使用策略

| 改动级别 | 策略 |
|---|---|
| 小改动（文案、样式微调、单文件小修） | 不强制启用 Superpowers |
| 中等改动（新增接口/组件、修改业务流程、测试） | 建议启用相关技能 |
| 高风险改动 | **必须启用 Superpowers** |

### 高风险改动清单

修改以下模块前，**必须**先启用对应 Superpowers 技能：

| 涉及模块 | 风险 | 必须启用 |
|---|---|---|
| 认证鉴权 (SaToken, StpUtil, Token 黑名单, 登录限流) | 安全漏洞、越权 | verification-before-completion |
| 数据库迁移 (deploy/mysql/init/) | 数据丢失 | writing-plans → verification |
| Docker 编排 (deploy/docker-compose.yml, .env) | 环境崩溃 | writing-plans → verification |
| 接口契约变更 (后端 DTO/Controller 路径) | 前后端不一致 | writing-plans |
| 跨 backend + web + admin-web 修改 | 连锁破坏 | subagent-driven-development |
| 前端权限控制逻辑 | 越权漏洞 | verification-before-completion |
| Nginx/HTTPS 配置 | 服务不可用 | writing-plans → verification |

### 执行约束

1. **读上下文**: 修改前读 `AGENTS.md`、`README.md` 和 `docs/` 下相关文档
2. **不扩大改动面**: 不修改无关文件，不擅自删除已有功能，不擅自替换技术栈
3. **验证**: 后端 `mvn test`、前端 `npm run build` / `npm run type-check`
4. **数据库**: 迁移前备份、验证 SQL 语法、记录回滚方案
5. **Docker**: 修改 `docker-compose.yml` 或 `.env` 后执行 `docker compose config` 验证
6. **认证/权限**: 修改后运行验证脚本、检查 SaTokenConfig 接口权限路径
7. **结束**: 更新 `AGENTS.md` 中的任务记录和项目状态

### 验证命令

```bash
# 后端编译+测试
cd backend && mvn test

# 前端类型检查+构建
cd web && npm run type-check && npm run build
cd admin-web && npm run type-check && npm run build

# Docker Compose 语法验证
cd deploy && docker compose config

# 整体检查
git status --short && git diff --check
```

## Superpowers 路由

| 场景 | 技能 |
|---|---|
| 设计新功能 | brainstorming → writing-plans |
| 实现已规划的功能 | executing-plans 或 subagent-driven-development |
| Bug 排查 | systematic-debugging |
| 写测试 | test-driven-development |
| 提交前自查 | verification-before-completion |
| 并行多任务 | dispatching-parallel-agents |
| 完成分支 | finishing-a-development-branch |
| 代码审查 | requesting-code-review / receiving-code-review |
| 隔离工作 | using-git-worktrees |
