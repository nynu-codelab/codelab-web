# ============================================================
# NYNU Code Lab 后端 Dockerfile — 多阶段构建
# ============================================================
# 构建：docker build -t nynu-code-lab-backend .
# 运行：docker run -e SPRING_PROFILES_ACTIVE=docker -p 8080:8080 nynu-code-lab-backend
# ============================================================

# --- Stage 1: Maven 构建 ---
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 先复制 pom.xml 并下载依赖（利用 Docker 缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# 复制源码并打包
COPY src/ ./src/
RUN mvn package -DskipTests -B -q

# --- Stage 2: 运行镜像 ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# 创建非 root 用户运行
RUN addgroup app && adduser --ingroup app --disabled-password app
USER app

# 复制构建产物
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# 通过环境变量指定 profile，默认 docker
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]
