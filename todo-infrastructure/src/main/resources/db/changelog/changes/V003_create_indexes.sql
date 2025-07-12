-- liquibase formatted sql

-- changeset todo-app:003-create-indexes
-- comment: パフォーマンス最適化のためのインデックス作成

-- comment: ユーザーID検索用インデックス
CREATE INDEX idx_todos_user_id ON todos(user_id);

-- comment: ステータス検索用インデックス
CREATE INDEX idx_todos_status ON todos(status);

-- comment: 作成日時ソート用インデックス
CREATE INDEX idx_todos_created_at ON todos(created_at DESC);

-- comment: 期限日時検索用インデックス（NULL値含む）
CREATE INDEX idx_todos_due_date ON todos(due_date);

-- comment: ユーザー別ステータス検索 + ソート用複合インデックス
CREATE INDEX idx_todos_user_status_created ON todos(user_id, status, created_at DESC);

-- comment: ユーザー別期限検索用複合インデックス
CREATE INDEX idx_todos_user_due_date ON todos(user_id, due_date);

-- rollback DROP INDEX IF EXISTS idx_todos_user_id;
-- rollback DROP INDEX IF EXISTS idx_todos_status;
-- rollback DROP INDEX IF EXISTS idx_todos_created_at;
-- rollback DROP INDEX IF EXISTS idx_todos_due_date;
-- rollback DROP INDEX IF EXISTS idx_todos_user_status_created;
-- rollback DROP INDEX IF EXISTS idx_todos_user_due_date;
