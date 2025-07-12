-- liquibase formatted sql

-- changeset todo-app:004-insert-sample-data
-- comment: 開発用サンプルデータの投入

-- comment: サンプルユーザーの作成
INSERT INTO users (name, role) VALUES 
    ('admin', 'ADMIN'),
    ('user1', 'USER'),
    ('user2', 'USER');

-- comment: サンプルTodoの作成
INSERT INTO todos (user_id, title, description, status, due_date) VALUES 
    (2, '食材を買う', '野菜、肉、調味料を購入', 'TODO', CURRENT_TIMESTAMP + INTERVAL '1 day'),
    (2, 'プレゼンテーション準備', '来週の会議用資料作成', 'IN_PROGRESS', CURRENT_TIMESTAMP + INTERVAL '3 days'),
    (2, '読書', 'プログラミング本を読む', 'TODO', NULL),
    (3, '運動する', 'ジョギング30分', 'COMPLETED', NULL),
    (3, 'メール返信', '重要なメールに返信', 'TODO', CURRENT_TIMESTAMP + INTERVAL '2 hours');

-- rollback DELETE FROM todos WHERE user_id IN (2, 3);
-- rollback DELETE FROM users WHERE name IN ('admin', 'user1', 'user2');
