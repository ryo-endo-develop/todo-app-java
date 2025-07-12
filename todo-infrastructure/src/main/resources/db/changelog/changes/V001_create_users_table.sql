-- liquibase formatted sql

-- changeset todo-app:001-create-users-table
-- comment: ユーザーテーブルの作成
CREATE TABLE users (
    id          BIGSERIAL                   PRIMARY KEY,
    name        VARCHAR(100)                NOT NULL UNIQUE,
    role        VARCHAR(20)                 NOT NULL DEFAULT 'USER',
    created_at  TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- comment: ユーザー役割のチェック制約
ALTER TABLE users ADD CONSTRAINT chk_users_role 
    CHECK (role IN ('ADMIN', 'USER'));

-- comment: ユーザー名の空文字チェック制約
ALTER TABLE users ADD CONSTRAINT chk_users_name_not_empty 
    CHECK (LENGTH(TRIM(name)) > 0);

-- comment: ユーザー名のユニークインデックス
CREATE UNIQUE INDEX idx_users_name ON users(name);

-- rollback DROP TABLE users;
