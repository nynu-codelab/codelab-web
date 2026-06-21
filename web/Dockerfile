# ============================================================
# NYNU SE Lab 前台 Web Dockerfile — 仅构建静态资源
# 构建产物 dist/ 供 deploy/nginx/Dockerfile 使用
# ============================================================
# 单独构建：docker build -t nynu-se-lab-web .
# 提取产物：docker create --name tmp nynu-se-lab-web && docker cp tmp:/app/dist ./dist && docker rm tmp
# ============================================================

FROM node:20-alpine AS builder
WORKDIR /app

# 利用缓存：先装依赖
COPY package*.json ./
RUN npm ci

# 复制源码并构建（跳过 vue-tsc 类型检查以加速容器构建）
COPY . .
RUN npx vite build
