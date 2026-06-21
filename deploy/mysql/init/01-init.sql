-- =============================================
-- NYNU SE Lab 数据库初始化脚本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS nynu_se_lab
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE nynu_se_lab;

-- 创建用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(50)  NOT NULL                COMMENT '用户名',
    password    VARCHAR(200) NOT NULL                COMMENT '密码（BCrypt加密）',
    real_name   VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '真实姓名',
    phone       VARCHAR(20)  NOT NULL DEFAULT ''     COMMENT '手机号',
    grade       VARCHAR(20)  NOT NULL DEFAULT ''     COMMENT '年级',
    major       VARCHAR(100) NOT NULL DEFAULT ''     COMMENT '专业',
    class_name  VARCHAR(100) NOT NULL DEFAULT ''     COMMENT '班级',
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：USER-普通用户，ADMIN-管理员',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-正常，0-禁用',
    deleted     TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 插入管理员账号（密码 admin123 的 BCrypt 加密）
INSERT INTO sys_user (username, password, real_name, phone, grade, major, class_name, role, status)
VALUES ('admin', '$2b$12$Y/o9Q/YAkuXUl/yvknO6/.9KGNCkKwPs9bbpA1wDSzm5BMKpqIO7C', '系统管理员', '13800000000', '', '', '', 'ADMIN', 1);
