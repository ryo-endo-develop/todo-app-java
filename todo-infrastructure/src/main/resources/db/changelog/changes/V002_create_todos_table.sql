-- liquibase formatted sql

-- changeset todo-app:002-create-todos-table
-- comment: Todoテーブルの作成
CREATE TABLE todos (
    id          BIGSERIAL                   PRIMARY KEY,
    user_id     BIGINT                      NOT NULL,
    title       VARCHAR(255)                NOT NULL,
    description TEXT,
    status      VARCHAR(20)                 NOT NULL DEFAULT 'TODO',
    due_date    TIMESTAMP,
    created_at  TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- comment: 外部キー制約
ALTER TABLE todos ADD CONSTRAINT fk_todos_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id) 
    ON DELETE CASCADE ON UPDATE CASCADE;

-- comment: ステータスのチェック制約
ALTER TABLE todos ADD CONSTRAINT chk_todos_status 
    CHECK (status IN ('TODO', 'IN_PROGRESS', 'COMPLETED', 'DELETED'));

-- comment: タイトルの空文字チェック制約
ALTER TABLE todos ADD CONSTRAINT chk_todos_title_not_empty 
    CHECK (LENGTH(TRIM(title)) > 0);

-- rollback DROP TABLE todos;
