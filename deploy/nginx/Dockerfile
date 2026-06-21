# ============================================================
# Nginx 统一托管 Dockerfile — 多阶段构建
# 构建 web（前台）和 admin-web（后台）静态资源，由 Nginx 统一托管
# ============================================================
# 构建上下文：项目根目录
# docker build -f deploy/nginx/Dockerfile -t nynu-se-lab-nginx .
# ============================================================

# --- Stage 1: 构建前台 web ---
FROM node:20-alpine AS web-builder
WORKDIR /app
COPY web/package*.json ./
RUN npm ci
COPY web/ ./
RUN npx vite build

# --- Stage 2: 构建后台 admin-web ---
FROM node:20-alpine AS admin-builder
WORKDIR /app
COPY admin-web/package*.json ./
RUN npm ci
COPY admin-web/ ./
RUN npx vite build

# --- Stage 3: Nginx 统一托管 ---
FROM nginx:alpine

# 复制 Nginx 配置
COPY deploy/nginx/nginx.conf /etc/nginx/conf.d/default.conf

# 复制前台静态资源 → /
COPY --from=web-builder /app/dist /usr/share/nginx/html

# 复制后台静态资源 → /admin/
COPY --from=admin-builder /app/dist /usr/share/nginx/html/admin

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
