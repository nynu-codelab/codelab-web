-- =============================================
-- NYNU Code Lab 数据库迁移 — 文章表
-- =============================================
-- 使用方法（Docker环境）：
--   cd deploy && docker compose exec -T mysql mysql -u root -p nynu_code_lab < ../backend/sql/migrations/01-add-article-table.sql
-- 使用方法（本地开发）：
--   mysql -u root -p nynu_code_lab < backend/sql/migrations/01-add-article-table.sql
-- =============================================

CREATE TABLE IF NOT EXISTS lab_article (
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
