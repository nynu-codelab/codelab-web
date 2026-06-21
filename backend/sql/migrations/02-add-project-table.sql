-- =============================================
-- NYNU Code Lab 数据库迁移 — 项目成果表
-- =============================================
-- 使用方法（Docker环境）：
--   cd deploy && docker compose exec -T mysql mysql -u root -p nynu_code_lab < ../backend/sql/migrations/02-add-project-table.sql
-- 使用方法（本地开发）：
--   mysql -u root -p nynu_code_lab < backend/sql/migrations/02-add-project-table.sql
-- =============================================

CREATE TABLE IF NOT EXISTS lab_project (
    id                    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title                 VARCHAR(200)  NOT NULL                COMMENT '项目名称',
    summary               VARCHAR(500)  NOT NULL DEFAULT ''     COMMENT '项目一句话简介',
    cover_url             VARCHAR(500)  NOT NULL DEFAULT ''     COMMENT '封面图URL',
    description_markdown  MEDIUMTEXT    NOT NULL                COMMENT '项目详细介绍（Markdown）',
    project_type          VARCHAR(50)   NOT NULL DEFAULT ''     COMMENT '项目类型（Web/AI/IoT/课程设计/竞赛作品等）',
    tech_stack            VARCHAR(500)  NOT NULL DEFAULT ''     COMMENT '技术栈',
    leader_name           VARCHAR(50)   NOT NULL DEFAULT ''     COMMENT '负责人姓名',
    members_text          VARCHAR(1000) NOT NULL DEFAULT ''     COMMENT '参与成员（文本）',
    repo_url              VARCHAR(500)  NOT NULL DEFAULT ''     COMMENT '代码仓库链接',
    demo_url              VARCHAR(500)  NOT NULL DEFAULT ''     COMMENT '演示地址',
    document_url          VARCHAR(500)  NOT NULL DEFAULT ''     COMMENT '文档地址',
    status                VARCHAR(20)   NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，PUBLISHED-已发布，OFFLINE-已下架',
    featured              TINYINT       NOT NULL DEFAULT 0      COMMENT '是否首页精选：0-否，1-是',
    view_count            INT           NOT NULL DEFAULT 0      COMMENT '浏览次数',
    sort_order            INT           NOT NULL DEFAULT 0      COMMENT '排序顺序（越大越靠前）',
    author_id             BIGINT        NOT NULL                COMMENT '创建管理员ID（关联 sys_user.id）',
    published_at          DATETIME      DEFAULT NULL            COMMENT '发布时间',
    deleted               TINYINT       NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_title (title),
    KEY idx_status (status),
    KEY idx_featured (featured),
    KEY idx_published_at (published_at),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成果表';
