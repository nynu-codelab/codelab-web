-- =============================================
-- NYNU Code Lab 数据库初始化脚本（唯一权威版本）
-- Docker Compose 首次启动时自动执行
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS nynu_code_lab
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE nynu_code_lab;

-- =============================================
-- 1. 系统用户表
-- =============================================
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
-- 2. 招新报名表
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
    status                VARCHAR(30)  NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/PRELIMINARY_PASSED/INTERVIEWING/VIEWED/CONTACTED/PASSED/REJECTED/WITHDRAWN',
    review_remark         VARCHAR(1000) NOT NULL DEFAULT ''    COMMENT '审核备注',
    reviewer_id           BIGINT       DEFAULT NULL            COMMENT '审核人ID',
    reviewed_at           DATETIME     DEFAULT NULL            COMMENT '审核时间',
    deleted               TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    active_user_id        BIGINT GENERATED ALWAYS AS (CASE WHEN deleted = 0 AND status <> 'WITHDRAWN' THEN user_id ELSE NULL END) STORED COMMENT '用于限制非撤回报名唯一',
    PRIMARY KEY (id),
    UNIQUE KEY uk_apply_active_user (active_user_id),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='招新报名表';

-- =============================================
-- 3. 文章表
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

-- =============================================
-- 4. 项目成果表
-- =============================================
DROP TABLE IF EXISTS lab_project;
CREATE TABLE lab_project (
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

-- =============================================
-- 5. 实验室成员表
-- =============================================
DROP TABLE IF EXISTS lab_member;
CREATE TABLE lab_member (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name          VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '成员姓名',
    avatar_url    VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '头像URL',
    role_title    VARCHAR(100) NOT NULL DEFAULT ''     COMMENT '角色/职位（如：后端负责人、前端成员）',
    direction_id  BIGINT       DEFAULT NULL            COMMENT '所属技术方向ID，关联 lab_direction.id',
    grade         VARCHAR(20)  NOT NULL DEFAULT ''     COMMENT '年级',
    bio           VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '个人简介',
    skills        VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '技能标签（JSON数组字符串）',
    github_url    VARCHAR(500) NOT NULL DEFAULT ''     COMMENT 'GitHub 主页',
    blog_url      VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '个人博客',
    email         VARCHAR(100) NOT NULL DEFAULT ''     COMMENT '联系邮箱',
    sort_order    INT          NOT NULL DEFAULT 0      COMMENT '排序顺序（越大越靠前）',
    status        TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-启用，0-停用',
    deleted       TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_direction_id (direction_id),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实验室成员表';

-- =============================================
-- 6. 技术方向表
-- =============================================
DROP TABLE IF EXISTS lab_direction;
CREATE TABLE lab_direction (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '方向名称',
    code        VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '方向编码（用于前端引用，如 java-backend）',
    summary     VARCHAR(200) NOT NULL DEFAULT ''     COMMENT '一句话简介',
    description VARCHAR(2000) NOT NULL DEFAULT ''    COMMENT '方向详细介绍',
    tags        VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '技术标签（JSON数组字符串）',
    icon        VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '图标URL或图标名称',
    cover_url   VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '封面图URL',
    sort_order  INT          NOT NULL DEFAULT 0      COMMENT '排序顺序（越大越靠前）',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-启用，0-停用',
    deleted     TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技术方向表';

-- 初始化 5 个技术方向（与前台当前展示一致）
INSERT INTO lab_direction (name, code, summary, description, tags, sort_order, status) VALUES
('Java 后端', 'java-backend', '学习服务端分层、接口设计、认证鉴权、数据库建模和上线前验证', '围绕 Spring Boot、接口设计、权限认证、数据库建模和部署链路进行项目实战。从单体应用到服务拆分，覆盖企业级后端开发的核心技能。', '["Java 17","Spring Boot","MyBatis-Plus","JWT"]', 50, 1),
('前端开发', 'frontend', '学习 Vue 3、TypeScript、路由、状态管理、组件化和响应式体验', '从 Vue 3、TypeScript、组件化、状态管理到可访问的交互体验，面向真实产品构建页面。重视工程化思维和用户体验打磨。', '["Vue 3","TypeScript","Vite","Pinia"]', 40, 1),
('微信小程序', 'miniprogram', '面向移动端场景训练登录态、接口联调、页面组织和发布流程', '面向移动端场景完成界面、接口、登录态和发布链路的完整训练。理解小程序生态的限制与优势，培养移动端产品思维。', '["小程序","移动端","接口联调","发布流程"]', 30, 1),
('人工智能', 'ai', '以应用实践为目标，探索数据处理、模型调用与智能化功能原型', '以应用实践为目标，探索数据处理、模型调用与智能化功能原型。不追求算法理论深度，注重将 AI 能力落地到实际项目中。', '["AI 应用","数据处理","原型验证"]', 20, 1),
('数据库与运维', 'devops', '理解 MySQL、Docker、Nginx、环境变量和上线前验证，补齐工程交付能力', '理解 MySQL、Docker、Nginx、环境变量和上线前验证，补齐工程交付能力。建立从开发到部署的完整认知链路。', '["MySQL","Docker","Nginx","部署"]', 10, 1);

-- =============================================
-- 7. 站点配置表
-- =============================================
DROP TABLE IF EXISTS lab_site_config;
CREATE TABLE lab_site_config (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    config_key   VARCHAR(100) NOT NULL DEFAULT ''     COMMENT '配置键名（如 siteName、contactEmail）',
    config_value TEXT         NOT NULL                 COMMENT '配置值',
    config_type  VARCHAR(20)  NOT NULL DEFAULT 'text' COMMENT '配置类型：text-文本，image-图片，richtext-富文本',
    group_name   VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '配置分组（如 site、contact、social）',
    remark       VARCHAR(200) NOT NULL DEFAULT ''     COMMENT '配置说明',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key),
    KEY idx_group_name (group_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点配置表';

-- 初始化非敏感站点配置（联系方式等敏感信息留空，由管理员上线后自行配置）
INSERT INTO lab_site_config (config_key, config_value, config_type, group_name, remark) VALUES
('siteName', '南阳师范学院 Code Lab', 'text', 'site', '网站名称，显示在浏览器标题和页头'),
('siteSlogan', '从代码实践到项目落地', 'text', 'site', '首页标语，展示在首页主视觉区域'),
('siteDescription', '南阳师范学院 Code Lab 实验室自建展示站与招新管理系统，关注项目实战、技术分享和工程能力培养。', 'text', 'site', '网站简介，用于 SEO 和关于页面'),
('footerText', '本网站为南阳师范学院 Code Lab 实验室自建展示站，非学校官方门户网站。', 'text', 'site', '页脚声明文字'),
('contactEmail', '', 'text', 'contact', '公开联系邮箱（由管理员上线后配置）'),
('contactPhone', '', 'text', 'contact', '公开联系电话（由管理员上线后配置）'),
('contactAddress', '', 'text', 'contact', '实验室地址（由管理员上线后配置）'),
('contactQrcodeUrl', '', 'image', 'contact', '招新咨询二维码图片URL（由管理员上线后上传配置）'),
('githubUrl', '', 'text', 'social', '实验室 GitHub 组织地址（由管理员上线后配置）'),
('announcement', '', 'text', 'site', '站点公告（留空则不展示公告栏）'),
('recruitOpen', 'true', 'text', 'recruit', '招新开关：true-开放报名，false-关闭报名');

-- =============================================
-- 8. 上传文件记录表
-- =============================================
DROP TABLE IF EXISTS lab_upload_file;
CREATE TABLE lab_upload_file (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    original_name VARCHAR(200) NOT NULL DEFAULT ''     COMMENT '原始文件名',
    stored_name   VARCHAR(200) NOT NULL DEFAULT ''     COMMENT '存储文件名（UUID 重命名）',
    file_url      VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '文件访问URL',
    file_path     VARCHAR(500) NOT NULL DEFAULT ''     COMMENT '文件存储路径（相对上传目录）',
    mime_type     VARCHAR(100) NOT NULL DEFAULT ''     COMMENT 'MIME 类型（如 image/png）',
    file_size     BIGINT       NOT NULL DEFAULT 0      COMMENT '文件大小（字节）',
    usage_type    VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '用途类型：avatar-头像，cover-封面，qrcode-二维码，other-其他',
    uploader_id   BIGINT       DEFAULT NULL            COMMENT '上传人ID，关联 sys_user.id',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_usage_type (usage_type),
    KEY idx_uploader_id (uploader_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='上传文件记录表';

-- =============================================
-- 9. 文章分类表
-- =============================================
DROP TABLE IF EXISTS lab_article_category;
CREATE TABLE lab_article_category (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '分类名称',
    code        VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '分类编码（用于前端引用，如 tech-article）',
    description VARCHAR(200) NOT NULL DEFAULT ''     COMMENT '分类描述',
    sort_order  INT          NOT NULL DEFAULT 0      COMMENT '排序顺序（越大越靠前）',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-启用，0-停用',
    deleted     TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- 初始化通用文章分类
INSERT INTO lab_article_category (name, code, description, sort_order, status) VALUES
('实验室动态', 'lab-news', '实验室新闻、活动、通知等动态信息', 40, 1),
('技术文章', 'tech-article', '技术教程、实践总结、问题排查等技术分享', 30, 1),
('招新公告', 'recruit-notice', '招新相关公告、面试安排、录取通知', 20, 1),
('项目复盘', 'project-review', '项目完成后的总结复盘和经验沉淀', 10, 1);

-- =============================================
-- 10. 文章标签表
-- =============================================
DROP TABLE IF EXISTS lab_article_tag;
CREATE TABLE lab_article_tag (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '标签名称',
    code        VARCHAR(50)  NOT NULL DEFAULT ''     COMMENT '标签编码（用于前端引用，如 java）',
    description VARCHAR(200) NOT NULL DEFAULT ''     COMMENT '标签描述',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-启用，0-停用',
    deleted     TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';

-- 初始化常用技术标签
INSERT INTO lab_article_tag (name, code, description, status) VALUES
('Java', 'java', 'Java 编程语言相关', 1),
('Spring Boot', 'spring-boot', 'Spring Boot 框架相关', 1),
('Vue', 'vue', 'Vue.js 前端框架相关', 1),
('小程序', 'miniprogram', '微信小程序开发相关', 1),
('Docker', 'docker', 'Docker 容器化相关', 1),
('AI', 'ai', '人工智能与机器学习应用相关', 1);
