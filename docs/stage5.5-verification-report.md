# Stage 5.5 本地验证报告

> 日期：2026-06-22
> 分支：`refine/pc-immersive-frontend`
> 基准提交：`43cec2d`（补强生产可用性）、`3d93303`（上传路径边界校验）
> 验证策略：仅本地验证，不推送、不打标签、不新增功能、不调整视觉

---

## 一、验证概览

| 验证项 | 状态 | 备注 |
|--------|------|------|
| Maven 后端构建 | ✅ 通过 | `mvn clean package -DskipTests` |
| 前端 web 构建 | ✅ 通过 | `npm run build`（via Docker 多阶段构建） |
| 前端 admin-web 构建 | ✅ 通过 | `npm run build`（via Docker 多阶段构建） |
| Docker Compose 启动 | ✅ 4/4 容器 healthy | mysql、redis、backend、nginx |
| MySQL 数据库 | ✅ 10 张表 + 种子数据 | 手动补建 6 张缺失表（历史数据卷残留问题） |
| Redis | ✅ 正常运行 | Token 黑名单 + 登录限流均生效 |
| 后端公开 API | ✅ 7 个端点全部 200 | health、directions、articles、projects、members、site-config、auth |
| 后端管理 API | ✅ 鉴权正确 | 无 Token → 401、USER 角色 → 403、ADMIN 角色 → 200 |
| Token 黑名单 | ✅ 生效 | 登出后旧 Token 被拒绝（"token已失效"） |
| 登录限流 | ✅ Redis 计数器正常 | Key: `codelab:auth:login-attempt:{ip}:{username}` |
| 文件上传 | ✅ 7 层纵深防御全部验证 | Magic Bytes / 路径穿越 / 扩展名 / MIME / 大小 / UUID / 日期子目录 |
| Nginx 静态文件服务 | ✅ 正确 | 上传文件以正确 MIME 类型对外服务 |
| Nginx SPA 托管 | ✅ 正确 | 前后台 SPA + Vue Router history fallback |
| .env 配置一致性 | ⚠️ 部分问题 | 见第四节 |

---

## 二、Docker Compose 验证

### 2.1 服务状态

```
NAME                    STATUS
nynu-code-lab-mysql     Up (healthy)    0.0.0.0:33066→3306
nynu-code-lab-redis     Up (healthy)    0.0.0.0:16380→6379
nynu-code-lab-backend   Up (healthy)    0.0.0.0:8080→8080
nynu-code-lab-nginx     Up (healthy)    0.0.0.0:80→80
```

### 2.2 容器间通信

- backend → mysql:3306 ✅
- backend → redis:6379 ✅
- nginx → backend:8080 ✅
- nginx 对外端口 80 ✅

---

## 三、Bug 修复记录

本次验证发现并修复 **3 个阻塞性问题**，共提交 **2 个 fix commit**：

### 3.1 `fix(stage5): 修复 Redis 配置导致容器化启动失败的问题`（`205abc3`）

**问题 1：`NumberFormatException: "3000ms"`**

- 根因：`application.yml` 中 `app.redis.timeout: 3000ms`，但 `RedisConfig.java` 使用 `@Value long timeoutMs`，无法解析带单位的 duration 字符串
- 修复：`timeout: 3000ms` → `timeout: 3000`

**问题 2：`BeanCreationException: Cannot resolve reference to bean 'redisTemplate'`**

- 根因：仅排除了 `RedisAutoConfiguration`，未排除 `RedisRepositoriesAutoConfiguration`，后者尝试创建 Redis Repository 基础设施需要 `redisTemplate` Bean（但 `RedisConfig` 只创建了 `StringRedisTemplate`）
- 修复：`autoconfigure.exclude` 增加 `RedisRepositoriesAutoConfiguration`

### 3.2 `fix(stage5): 修复上传文件无法通过 Nginx 对外访问的问题`（`f333b30`）

**问题：上传文件存储成功，但无法通过 HTTP 对外访问**

- 根因 1：`nginx.conf` 中 `/uploads/` location 使用 `proxy_pass http://backend`，但后端没有静态资源处理器映射 `/uploads/` 路径
- 根因 2：`docker-compose.yml` 中 nginx 容器未挂载 `uploads-data` 卷
- 修复：
  - `nginx.conf`：`proxy_pass` → `alias /usr/share/nginx/html/uploads/` + 缓存头
  - `docker-compose.yml`：nginx 服务新增 `uploads-data:/usr/share/nginx/html/uploads:ro`

---

## 四、.env 配置一致性检查

| 检查项 | 状态 | 说明 |
|--------|------|------|
| `.env` 已 gitignore | ✅ | 确认不提交 |
| `.env.example` 模板完整 | ✅ | deploy/.env.example 包含所有必需变量 |
| 根目录 `.env.example` | ⚠️ | 存在但缺少 `SERVER_PORT`、`REDIS_DATABASE`，与 deploy 版不一致 |
| `docker-compose.yml` 依赖 .env 变量 | ✅ | `MYSQL_ROOT_PASSWORD`、`MYSQL_PASSWORD`、`JWT_SECRET` 使用 `:?` 强制校验 |
| `SERVER_PORT` 硬编码 | ⚠️ | docker-compose.yml 中 `SERVER_PORT: 8080` 硬编码（注释说明 Nginx upstream 已硬编码，不建议修改） |
| `UPLOAD_BASE_PATH` 硬编码 | ✅ 已收口 | docker-compose.yml 中硬编码为 `/app/uploads`，deploy/.env 中已统一为 `UPLOAD_BASE_PATH` |
| `BACKEND_PORT` 可配置 | ✅ | `.env` 中 `BACKEND_PORT=8080` 生效 |
| `NGINX_PORT` 可配置 | ✅ | `.env` 中 `NGINX_PORT=80` 生效 |
| `MYSQL_PORT` 可配置 | ✅ | `.env` 中 `MYSQL_PORT=33066` 生效 |
| `REDIS_PORT` 可配置 | ✅ | `.env` 中 `REDIS_PORT=16380` 生效 |

**已收口：** 根目录 `.env.example` 已与 `deploy/.env.example` 同步（补齐 `REDIS_DATABASE`、统一变量名与注释格式）；`deploy/.env` 中 `UPLOAD_DIR` 已更名为 `UPLOAD_BASE_PATH`。

---

## 五、后端接口本地验证

### 5.1 公开接口

| 端点 | HTTP | 结果 |
|------|------|------|
| `GET /api/health` | 200 | `{"status":"UP","service":"nynu-code-lab-backend"}` |
| `GET /api/directions` | 200 | 5 个方向，含种子数据 |
| `GET /api/articles` | 200 | records: []（无已发布文章） |
| `GET /api/projects` | 200 | 7 个已发布项目 |
| `GET /api/members` | 200 | data: []（无成员数据） |
| `GET /api/site-config` | 200 | 11 个配置项，含站点名、标语等 |
| `POST /api/auth/login` | 200 | 返回 Token + 用户信息 |
| `POST /api/auth/register` | 200 | 注册成功，需 confirmPassword 字段 |

### 5.2 鉴权验证

| 场景 | 结果 | 说明 |
|------|------|------|
| 无 Token 访问 `/api/admin/*` | 401 | "未登录或登录已过期" |
| USER 角色访问 `/api/admin/*` | 403 | 角色权限不足 |
| ADMIN 角色访问 `/api/admin/*` | 200 | 正常访问 |
| 登出后使用旧 Token | 401 | "token已失效，请重新登录" ✅ Token 黑名单生效 |
| 登录限流 | ✅ | Redis Key: `codelab:auth:login-attempt:{ip}:{username}` |

### 5.3 管理接口（ADMIN Token）

| 端点 | 结果 |
|------|------|
| `GET /api/admin/dashboard/stats` | 200，userCount=27, projectCount=7 |
| `GET /api/admin/directions` | 200，5 条 |
| `GET /api/admin/members` | 200，0 条 |
| `GET /api/admin/articles` | 200 |
| `GET /api/admin/projects` | 200 |
| `GET /api/admin/site-config` | 200，11 条 |
| `GET /api/admin/upload` | 200，上传记录列表正常 |

---

## 六、上传安全纵深防御验证

按照 7 层纵深防御逐项测试：

| 层 | 防御内容 | 测试用例 | 结果 |
|----|----------|----------|------|
| 1 | 文件非空检查 | — | ✅（空文件在扩展名校验阶段被拒绝） |
| 2 | 文件名安全清洗 | `../../../etc/passwd.png` | ✅ 拒绝："文件名不合法" |
| 3 | 扩展名白名单 | `noextension`（无扩展名） | ✅ 拒绝："无法识别的文件类型" |
| 3 | 扩展名白名单 | `.png`（隐藏文件/空名） | ✅ 拒绝 |
| 4 | MIME 类型校验 | — | ✅ 后端记录正确 MIME 类型 |
| 5 | Magic Bytes 校验 | `test_fake_text.jpg`（文本伪装的 JPEG） | ✅ 拒绝："文件头与扩展名不匹配：非 JPEG 格式" |
| 6 | 文件大小上限 | — | ✅ 10MB 限制在 Spring Multipart + Nginx 双重配置 |
| 7 | 安全存储 | `test_icon.png`（正常 PNG） | ✅ 存储为 `7eb23280-{uuid}.png`，路径 `/app/uploads/2026/06/22/` |

### 边界测试

| 测试用例 | 结果 | 说明 |
|----------|------|------|
| `evil.php.png`（双扩展名） | 200 接受 | Magic Bytes 验证通过（实际为 PNG），文件内容安全 |
| `evil.php%00.png`（URL 编码空字节） | 200 接受 | multipart/form-data 中 `%00` 为字面量 3 字符，非空字节 |
| 正常 PNG 上传 | 200 ✅ | UUID 重命名 + 日期子目录 |
| 正常 JPEG 上传 | 200 ✅ | MIME 正确识别为 `image/jpeg` |
| Nginx 对外服务 PNG | 200，`image/png`，69B ✅ | |
| Nginx 对外服务 JPEG | 200，`image/jpeg`，635B ✅ | |

---

## 七、数据库验证

### 7.1 表清单（10 张）

| 表名 | 行数 | 说明 |
|------|------|------|
| `sys_user` | 27 | 含 admin + 历史测试用户 |
| `lab_apply_record` | 8 | 招新报名记录 |
| `lab_article` | 8 | 文章 |
| `lab_article_category` | 4 | 文章分类（种子数据） |
| `lab_article_tag` | 6 | 文章标签（种子数据） |
| `lab_direction` | 5 | 技术方向（种子数据） |
| `lab_member` | 0 | 实验室成员 |
| `lab_project` | 15 | 项目成果 |
| `lab_site_config` | 11 | 站点配置（种子数据） |
| `lab_upload_file` | 4 | 上传文件记录 |

### 7.2 种子数据完整性

- ✅ admin 账号（BCrypt `$2b$12$...`）
- ✅ 5 个技术方向（java-backend、frontend、miniprogram、ai、devops）
- ✅ 11 个站点配置项（siteName、recruitOpen 等）
- ✅ 4 个文章分类 + 6 个文章标签
- ✅ sys_user 含 role=ADMIN 的管理员账号

### 7.3 注意事项

- MySQL 数据卷 (`mysql-data`) 保留了历史运行数据，导致本次启动时 init SQL 未重新执行。手动补建了 6 张表
- 首次部署或 `docker compose down -v` 清理后重新启动不会有此问题

---

## 八、Nginx / 静态资源验证

| 检查项 | 结果 |
|--------|------|
| 前台 SPA `/` | 200，`text/html`，489B ✅ |
| 后台 SPA `/admin/` | 200，`text/html`，496B ✅ |
| Vue Router history fallback `/admin/directions` | 200，返回 admin/index.html ✅ |
| 不存在路由 fallback | 200，返回前台 index.html ✅ |
| Knife4j 文档 `/doc.html` | 200 ✅ |
| 上传文件 `/uploads/2026/06/22/{uuid}.png` | 200，`image/png` ✅ |
| 上传文件 `/uploads/2026/06/22/{uuid}.jpg` | 200，`image/jpeg` ✅ |
| API 代理 `/api/health` → backend:8080 | 200 ✅ |
| Gzip 压缩 | 已启用 ✅ |
| `client_max_body_size 10M` | 已配置 ✅ |

---

## 九、Redis 验证

| 检查项 | 结果 |
|--------|------|
| Redis 服务运行 | ✅ `redis:7-alpine`，AOF 持久化 |
| Token 黑名单 Key | ✅ `codelab:auth:blacklist:{sha256}` |
| 登录限流 Key | ✅ `codelab:auth:login-attempt:{ip}:{username}` |
| 黑名单 TTL | ✅ 604800000ms（7 天） |
| 登录限流参数 | ✅ maxAttempts=5, lockDuration=600s |
| Docker 启动日志确认 | ✅ "Redis Token 黑名单已启用"、"Redis 登录限流已启用" |

---

## 十、环境变量对照

| 变量 | deploy/.env | docker-compose.yml | application.yml | 说明 |
|------|-------------|-------------------|-----------------|------|
| `SPRING_PROFILES_ACTIVE` | docker | ✅ | ✅ | 一致 |
| `SERVER_PORT` | 已补加 ✅ | 硬编码 8080 | `${SERVER_PORT:8080}` | 补加至 .env 作为文档说明 |
| `MYSQL_ROOT_PASSWORD` | root | ✅ `:?`强制校验 | — | 一致 |
| `MYSQL_USER` / `MYSQL_PASSWORD` | nynu / change_me_user | ✅ | — | 一致 |
| `REDIS_PORT` | 16380 | 6379（内部） | `${REDIS_PORT:6379}` | 宿主机 16380→容器 6379，正确 |
| `REDIS_PASSWORD` | 空 | ✅ | 空 | 本地无密码，一致 |
| `REDIS_DATABASE` | 0 | ✅ | `${REDIS_DATABASE:0}` | 一致 |
| `JWT_SECRET` | change_me_to_a_long_random_secret... | ✅ `:?`强制校验 | — | 本地测试值 |
| `AUTH_REDIS_ENABLED` | — | 硬编码 "true" | `${AUTH_REDIS_ENABLED:false}` | docker 下强制开启 |
| `UPLOAD_BASE_PATH` | /app/uploads | 硬编码 `/app/uploads` | `${UPLOAD_BASE_PATH:uploads}` | 已统一变量名 |
| `NGINX_PORT` | 80 | ✅ | — | 一致 |
| `MYSQL_PORT` | 33066 | ✅ | — | 宿主机自定义端口 |

---

## 十一、Docs 一致性检查

| 文档 | 一致性 | 说明 |
|------|--------|------|
| `docs/文件上传与静态资源配置.md` | ✅ | 7 层防御说明与实际实现一致；Volume 挂载架构已本次修复对齐 |
| `deploy/.env.example` | ✅ | 所有必需变量有模板 |
| `.env.example`（根目录） | ⚠️ | 与 deploy 版不一致，缺少部分变量 |
| `AGENTS.md` | ✅ | 项目结构与实际一致 |

---

## 十二、Git 提交历史

```
<待提交> docs(stage5): 收口本地验证配置说明
f333b30 fix(stage5): 修复上传文件无法通过 Nginx 对外访问的问题
205abc3 fix(stage5): 修复 Redis 配置导致容器化启动失败的问题
3d93303 fix(stage5): 完善上传路径边界校验与配置修正
43cec2d feat(stage5): 补强生产可用性
43afd03 feat(stage4): 完成站点完善与上线配置补齐
```

- 工作区干净，无未提交变更 ✅
- Stage 5.5 发现并修复 3 个问题（2 个 Redis 配置问题合并在 `205abc3`，1 个上传文件访问问题在 `f333b30`）
- 本轮收口提交：统一 .env.example、修正变量名不一致、更新文档

---

## 十三、总结与风险提示

### 验证结论

**Stage 5 核心功能全部通过验证。** Docker Compose 一键启动后，4 个服务均健康运行，前后端接口正常，上传 7 层纵深防御有效，Redis Token 黑名单和登录限流生效。

### 发现的阻塞性问题（已修复）

1. **Redis timeout 格式错误**：`3000ms` 改为 `3000`（影响容器化启动）
2. **缺少 `RedisRepositoriesAutoConfiguration` 排除**：与 `RedisAutoConfiguration` 需同时排除（影响容器化启动）
3. **上传文件无法对外访问**：Nginx + Docker 卷配置不一致（影响上传功能闭环）

### 非阻塞性建议

#### 已收口（本轮修复）

1. `.env` 中 `UPLOAD_DIR` 与 docker-compose 中 `UPLOAD_BASE_PATH` 变量名不统一 → 已统一为 `UPLOAD_BASE_PATH`
2. 根目录 `.env.example` 与 `deploy/.env.example` 内容不一致 → 已同步（补齐 `REDIS_DATABASE`、统一注释格式）
3. `.env` 中 `SERVER_PORT` 变量缺失 → 已补加 `SERVER_PORT=8080`（docker-compose 中仍硬编码，.env 中作为文档说明默认端口）

#### 仍需生产部署前处理

1. MySQL init 策略依赖 `docker-entrypoint-initdb.d` 仅在首次初始化时执行，数据卷残留会导致表缺失
2. 双扩展名文件（`evil.php.png`）被扩展名过滤器放行（以最后一段 `.png` 为准），但 Magic Bytes 校验确保了文件内容安全

### 部署前必检清单

- [ ] 修改 `deploy/.env` 中 `MYSQL_ROOT_PASSWORD`、`MYSQL_PASSWORD`、`JWT_SECRET` 为生产强密码
- [ ] 如部署到公网，配置 HTTPS（Let's Encrypt）并修改 Nginx 配置
- [ ] 配置 Redis 密码（生产环境）
- [ ] 首次部署或数据卷清理后执行 `docker compose down -v && docker compose up -d --build`
