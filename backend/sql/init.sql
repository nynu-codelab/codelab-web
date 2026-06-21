-- =============================================
-- NYNU Code Lab 数据库初始化脚本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS nynu_code_lab
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE nynu_code_lab;

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

-- =============================================
-- 招新报名表
-- =============================================
DROP TABLE IF EXISTS lab_apply_record;
CREATE TABLE lab_apply_record (
    id                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id               BIGINT       NOT NULL                COMMENT '关联用户ID',
    real_name             VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '真实姓名',
    grade                 VARCHAR(20)  NOT NULL DEFAULT ''     COMMENT '年级',
    major                 VARCHAR(100) NOT NULL DEFAULT ''     COMMENT '专业',
    class_name            VARCHAR(100) NOT NULL DEFAULT ''     COMMENT '班级',
    phone                 VARCHAR(20)  NOT NULL DEFAULT ''     COMMENT '手机号',
    qq                    VARCHAR(20)  NOT NULL DEFAULT ''     COMMENT 'QQ号',
    direction             VARCHAR(100) NOT NULL DEFAULT ''     COMMENT '意向技术方向',
    has_programming_basis TINYINT      NOT NULL DEFAULT 0      COMMENT '是否有编程基础：0-否，1-是',
    skills                VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '已掌握技术',
    introduction          VARCHAR(2000) NOT NULL DEFAULT ''    COMMENT '个人介绍',
    reason                VARCHAR(2000) NOT NULL DEFAULT ''    COMMENT '加入实验室的原因',
    weekly_available_time VARCHAR(100) NOT NULL DEFAULT ''     COMMENT '每周可投入时间',
    portfolio_url         VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '项目/作品链接',
    status                VARCHAR(30)  NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/PRELIMINARY_PASSED/INTERVIEWING/PASSED/REJECTED/WITHDRAWN',
    review_remark         VARCHAR(1000) NOT NULL DEFAULT ''    COMMENT '审核备注',
    reviewer_id           BIGINT       DEFAULT NULL            COMMENT '审核人ID',
    reviewed_at           DATETIME     DEFAULT NULL            COMMENT '审核时间',
    deleted               TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='招新报名表';

-- =============================================
-- 文章表
-- =============================================
DROP TABLE IF EXISTS lab_article;
CREATE TABLE lab_article (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title            VARCHAR(200)  NOT NULL                COMMENT '文章标题',
    summary          VARCHAR(500)  NOT NULL DEFAULT ''     COMMENT '文章摘要',
    cover_url        VARCHAR(500)  NOT NULL DEFAULT ''     COMMENT '封面图URL',
    content_markdown MEDIUMTEXT    NOT NULL                COMMENT 'Markdown 正文',
    category         VARCHAR(50)   NOT NULL DEFAULT ''     COMMENT '文章分类',
    tags             VARCHAR(500)  NOT NULL DEFAULT ''     COMMENT '文章标签（JSON数组字符串）',
    status           VARCHAR(20)   NOT NULL DEFAULT 'DRAFT' COMMENT '文章状态：DRAFT-草稿，PUBLISHED-已发布，OFFLINE-已下架',
    view_count       INT           NOT NULL DEFAULT 0      COMMENT '浏览次数',
    sort_order       INT           NOT NULL DEFAULT 0      COMMENT '排序顺序（越大越靠前）',
    author_id        BIGINT        NOT NULL                COMMENT '作者ID（关联 sys_user.id）',
    published_at     DATETIME      DEFAULT NULL            COMMENT '发布时间',
    deleted          TINYINT       NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_author_id (author_id),
    KEY idx_published_at (published_at),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';
