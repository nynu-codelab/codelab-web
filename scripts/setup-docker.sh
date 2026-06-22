#!/usr/bin/env bash
# ============================================================
# NYNU Code Lab — Docker 一键初始化 & 启动脚本
# ============================================================
# 使用方式：
#   chmod +x scripts/setup-docker.sh
#   ./scripts/setup-docker.sh
#
# 此脚本会：
#   1. 检查 Docker / Docker Compose 是否安装
#   2. 若 deploy/.env 不存在，从模板创建；自动生成缺失的密码和密钥
#   3. 构建镜像并启动所有容器
#   4. 等待服务健康检查通过
#   5. 显示访问地址和管理命令
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
DEPLOY_DIR="$PROJECT_ROOT/deploy"
ENV_FILE="$DEPLOY_DIR/.env"
ENV_EXAMPLE="$DEPLOY_DIR/.env.example"

# 颜色输出
BOLD="$(printf '\033[1m' 2>/dev/null || echo '')"
GREEN="$(printf '\033[32m' 2>/dev/null || echo '')"
YELLOW="$(printf '\033[33m' 2>/dev/null || echo '')"
RED="$(printf '\033[31m' 2>/dev/null || echo '')"
CYAN="$(printf '\033[36m' 2>/dev/null || echo '')"
RESET="$(printf '\033[0m' 2>/dev/null || echo '')"

log()  { printf '%b[✓]%b %s\n' "$GREEN" "$RESET" "$*"; }
warn() { printf '%b[!]%b %s\n' "$YELLOW" "$RESET" "$*"; }
err()  { printf '%b[✗]%b %s\n' "$RED" "$RESET" "$*"; }
info() { printf '%b[>]%b %s\n' "$CYAN" "$RESET" "$*"; }

echo ""
printf '%b════════════════════════════════════════%b\n' "$BOLD" "$RESET"
printf '%b  NYNU Code Lab — Docker 容器化启动%b\n' "$BOLD" "$RESET"
printf '%b════════════════════════════════════════%b\n' "$BOLD" "$RESET"
echo ""

# -----------------------------------------
# 1. 前置检查
# -----------------------------------------
command -v docker >/dev/null 2>&1 || { err "需要安装 Docker，请先安装 Docker Desktop 或 Docker Engine"; exit 1; }
log "Docker 已安装"

# 检查 Docker Compose 插件 (docker compose)
if docker compose version >/dev/null 2>&1; then
    DOCKER_COMPOSE="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
    DOCKER_COMPOSE="docker-compose"
else
    err "需要 Docker Compose 插件或 docker-compose 命令"
    exit 1
fi
log "Docker Compose 可用"

# -----------------------------------------
# 2. 环境变量文件初始化
# -----------------------------------------
if [ ! -f "$ENV_FILE" ]; then
    warn ".env 文件不存在，从 .env.example 创建..."
    cp "$ENV_EXAMPLE" "$ENV_FILE"
    log "已创建 $ENV_FILE"
fi

# 从 .env 读取当前值
source_env_val() {
    grep "^$1=" "$ENV_FILE" 2>/dev/null | cut -d= -f2- || true
}

EXISTING_MYSQL_ROOT=$(source_env_val MYSQL_ROOT_PASSWORD)
EXISTING_MYSQL_USER=$(source_env_val MYSQL_PASSWORD)
EXISTING_JWT=$(source_env_val JWT_SECRET)

NEED_UPDATE=false

# 如果 MYSQL_ROOT_PASSWORD 为空，自动生成
if [ -z "$EXISTING_MYSQL_ROOT" ]; then
    GENERATED_ROOT=$(openssl rand -base64 24 2>/dev/null || echo "auto_root_$(date +%s)")
    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "s/^MYSQL_ROOT_PASSWORD=.*/MYSQL_ROOT_PASSWORD=$GENERATED_ROOT/" "$ENV_FILE"
    else
        sed -i "s/^MYSQL_ROOT_PASSWORD=.*/MYSQL_ROOT_PASSWORD=$GENERATED_ROOT/" "$ENV_FILE"
    fi
    log "已生成 MYSQL_ROOT_PASSWORD"
    NEED_UPDATE=true
fi

# 如果 MYSQL_PASSWORD 为空，自动生成
if [ -z "$EXISTING_MYSQL_USER" ]; then
    GENERATED_USER=$(openssl rand -base64 20 2>/dev/null || echo "auto_user_$(date +%s)")
    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "s/^MYSQL_PASSWORD=.*/MYSQL_PASSWORD=$GENERATED_USER/" "$ENV_FILE"
    else
        sed -i "s/^MYSQL_PASSWORD=.*/MYSQL_PASSWORD=$GENERATED_USER/" "$ENV_FILE"
    fi
    log "已生成 MYSQL_PASSWORD"
    NEED_UPDATE=true
fi

# 如果 JWT_SECRET 为空，自动生成
if [ -z "$EXISTING_JWT" ]; then
    GENERATED_JWT=$(openssl rand -base64 64 2>/dev/null || echo "auto_jwt_$(date +%s)_$(date +%s)")
    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "s/^JWT_SECRET=.*/JWT_SECRET=$GENERATED_JWT/" "$ENV_FILE"
    else
        sed -i "s/^JWT_SECRET=.*/JWT_SECRET=$GENERATED_JWT/" "$ENV_FILE"
    fi
    log "已生成 JWT_SECRET（64 位 base64）"
    NEED_UPDATE=true
fi

if [ "$NEED_UPDATE" = true ]; then
    echo ""
    log "密钥已自动生成并写入 $ENV_FILE"
    echo "  如需查看：cat $ENV_FILE"
fi

# 最终校验：确保 MYSQL_ROOT_PASSWORD / MYSQL_PASSWORD / JWT_SECRET 非空
FINAL_ROOT=$(source_env_val MYSQL_ROOT_PASSWORD)
FINAL_USER=$(source_env_val MYSQL_PASSWORD)
FINAL_JWT=$(source_env_val JWT_SECRET)

if [ -z "$FINAL_ROOT" ] || [ -z "$FINAL_USER" ] || [ -z "$FINAL_JWT" ]; then
    err "MYSQL_ROOT_PASSWORD、MYSQL_PASSWORD、JWT_SECRET 不能为空"
    err "请编辑 $ENV_FILE 后重试"
    exit 1
fi

# -----------------------------------------
# 3. 构建 & 启动
# -----------------------------------------
echo ""
info "构建镜像并启动所有服务（首次启动需要几分钟）..."
echo ""

cd "$PROJECT_ROOT"
$DOCKER_COMPOSE --env-file "$ENV_FILE" -f "$DEPLOY_DIR/docker-compose.yml" up -d --build

echo ""

# -----------------------------------------
# 4. 等待服务就绪
# -----------------------------------------
info "等待服务就绪..."

MAX_WAIT=120
WAITED=0
while [ $WAITED -lt $MAX_WAIT ]; do
    # 检查 backend 和 nginx 是否 healthy
    BACKEND_STATE=$($DOCKER_COMPOSE --env-file "$ENV_FILE" -f "$DEPLOY_DIR/docker-compose.yml" ps -a --format json 2>/dev/null \
        | grep -o '"Name":"nynu-code-lab-backend"[^}]*"Health":"[^"]*"' \
        | grep -o '"Health":"healthy"' || true)
    NGINX_STATE=$($DOCKER_COMPOSE --env-file "$ENV_FILE" -f "$DEPLOY_DIR/docker-compose.yml" ps -a --format json 2>/dev/null \
        | grep -o '"Name":"nynu-code-lab-nginx"[^}]*"Health":"[^"]*"' \
        | grep -o '"Health":"healthy"' || true)

    if [ -n "$BACKEND_STATE" ] && [ -n "$NGINX_STATE" ]; then
        echo ""
        echo ""
        printf '%b════════════════════════════════════════%b\n' "$BOLD$GREEN" "$RESET"
        printf '%b  所有服务已就绪！%b\n' "$BOLD$GREEN" "$RESET"
        printf '%b════════════════════════════════════════%b\n' "$BOLD$GREEN" "$RESET"
        echo ""
        break
    fi

    sleep 3
    WAITED=$((WAITED + 3))
    printf '.'
done

if [ $WAITED -ge $MAX_WAIT ]; then
    echo ""
    warn "部分服务可能未完全就绪，请运行以下命令检查状态："
    echo "  $DOCKER_COMPOSE --env-file $ENV_FILE -f $DEPLOY_DIR/docker-compose.yml ps -a"
fi

# -----------------------------------------
# 5. 显示访问信息
# -----------------------------------------
NGINX_PORT=$(source_env_val NGINX_PORT)
NGINX_PORT=${NGINX_PORT:-80}

echo ""
printf '%b访问地址：%b\n' "$BOLD" "$RESET"
printf '  前台首页:  %bhttp://localhost:%s/%b\n' "$CYAN" "$NGINX_PORT" "$RESET"
printf '  后台管理:  %bhttp://localhost:%s/admin/%b\n' "$CYAN" "$NGINX_PORT" "$RESET"
printf '  API 文档:  %bhttp://localhost:%s/doc.html%b\n' "$CYAN" "$NGINX_PORT" "$RESET"
printf '  健康检查:  %bhttp://localhost:%s/api/health%b\n' "$CYAN" "$NGINX_PORT" "$RESET"
echo ""
printf '%b管理命令：%b\n' "$BOLD" "$RESET"
printf '  查看状态:  %b%s --env-file %s -f %s/docker-compose.yml ps%b\n' "$BOLD" "$DOCKER_COMPOSE" "$ENV_FILE" "$DEPLOY_DIR" "$RESET"
printf '  查看日志:  %b%s --env-file %s -f %s/docker-compose.yml logs -f%b\n' "$BOLD" "$DOCKER_COMPOSE" "$ENV_FILE" "$DEPLOY_DIR" "$RESET"
printf '  停止服务:  %b%s --env-file %s -f %s/docker-compose.yml down%b\n' "$BOLD" "$DOCKER_COMPOSE" "$ENV_FILE" "$DEPLOY_DIR" "$RESET"
printf '  清理数据:  %b%s --env-file %s -f %s/docker-compose.yml down -v%b\n' "$BOLD" "$DOCKER_COMPOSE" "$ENV_FILE" "$DEPLOY_DIR" "$RESET"
echo ""
printf '%b默认管理员账号：admin / admin123%b\n' "$YELLOW" "$RESET"
printf '%b首次登录后请立即修改密码！%b\n' "$YELLOW" "$RESET"
echo ""
