-- NYNU Code Lab 数据库迁移 — 报名记录非撤回状态唯一约束
-- 作用：防止同一用户在 PENDING/PRELIMINARY_PASSED/INTERVIEWING/PASSED/REJECTED 状态下产生多条有效报名。
-- 注意：执行前如果历史数据已存在同一用户多条非撤回报名，需要先人工合并或撤回重复记录。

ALTER TABLE lab_apply_record
    ADD COLUMN active_user_id BIGINT
        GENERATED ALWAYS AS (
            CASE
                WHEN deleted = 0 AND status <> 'WITHDRAWN' THEN user_id
                ELSE NULL
            END
        ) STORED COMMENT '用于限制非撤回报名唯一',
    ADD UNIQUE KEY uk_apply_active_user (active_user_id);
