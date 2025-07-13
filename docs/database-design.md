# Todo App データベース設計書

## ER 図

```mermaid
erDiagram
    users {
        bigint id PK "ユーザーID（自動生成）"
        varchar(100) name UK "ユーザー名（ユニーク）"
        varchar(20) role "ユーザー役割（ADMIN, USER）"
        timestamp created_at "作成日時"
        timestamp updated_at "更新日時"
    }

    todos {
        bigint id PK "TodoID（自動生成）"
        bigint user_id FK "ユーザーID"
        varchar(255) title "Todoタイトル"
        text description "Todo説明（NULL可）"
        varchar(20) status "ステータス（TODO, IN_PROGRESS, COMPLETED, DELETED）"
        timestamp due_date "期限日時（NULL可）"
        timestamp created_at "作成日時"
        timestamp updated_at "更新日時"
    }

    users ||--o{ todos : "ユーザーは複数のTodoを持つ"
```

## テーブル定義

### users テーブル

| カラム名   | データ型     | 制約                                | 説明                    |
| ---------- | ------------ | ----------------------------------- | ----------------------- |
| id         | BIGSERIAL    | PRIMARY KEY                         | ユーザー ID（自動生成） |
| name       | VARCHAR(100) | NOT NULL, UNIQUE                    | ユーザー名              |
| role       | VARCHAR(20)  | NOT NULL, DEFAULT 'USER'            | ユーザー役割            |
| created_at | TIMESTAMP    | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 作成日時                |
| updated_at | TIMESTAMP    | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 更新日時                |

**役割（role）の値**:

- `ADMIN`: 管理者
- `USER`: 一般ユーザー

### todos テーブル

| カラム名    | データ型     | 制約                                | 説明               |
| ----------- | ------------ | ----------------------------------- | ------------------ |
| id          | BIGSERIAL    | PRIMARY KEY                         | TodoID（自動生成） |
| user_id     | BIGINT       | NOT NULL, FOREIGN KEY               | ユーザー ID        |
| title       | VARCHAR(255) | NOT NULL                            | Todo タイトル      |
| description | TEXT         | NULL                                | Todo 説明          |
| status      | VARCHAR(20)  | NOT NULL, DEFAULT 'TODO'            | ステータス         |
| due_date    | TIMESTAMP    | NULL                                | 期限日時           |
| created_at  | TIMESTAMP    | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 作成日時           |
| updated_at  | TIMESTAMP    | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 更新日時           |

**ステータス（status）の値**:

- `TODO`: 未完了
- `IN_PROGRESS`: 進行中
- `COMPLETED`: 完了
- `DELETED`: 削除（論理削除）

### 制約

#### 外部キー制約

- `todos.user_id` → `users.id`
  - `ON DELETE CASCADE`: ユーザー削除時に関連する Todo も削除
  - `ON UPDATE CASCADE`: ユーザー ID 更新時に関連する Todo 側も更新

#### インデックス

- `users.name`: ユニークインデックス（ログイン用）
- `todos.user_id`: 外部キーインデックス（検索最適化）
- `todos.status`: 条件検索用インデックス
- `todos.created_at`: 作成日時順ソート用インデックス
- `todos.due_date`: 期限検索用インデックス（NULL 値含む）

#### チェック制約

- `users.role`: `role IN ('ADMIN', 'USER')`
- `todos.status`: `status IN ('TODO', 'IN_PROGRESS', 'COMPLETED', 'DELETED')`
- `todos.title`: `LENGTH(TRIM(title)) > 0`（空文字列禁止）

## パフォーマンス設計

### 想定クエリパターン

1. **ユーザー別 Todo 一覧**

   ```sql
   SELECT * FROM todos
   WHERE user_id = ? AND status != 'DELETED'
   ORDER BY created_at DESC;
   ```

2. **ステータス別 Todo 検索**

   ```sql
   SELECT * FROM todos
   WHERE user_id = ? AND status = ?
   ORDER BY created_at DESC;
   ```

3. **期限切れ Todo 検索**
   ```sql
   SELECT * FROM todos
   WHERE user_id = ? AND due_date < CURRENT_TIMESTAMP AND status != 'COMPLETED'
   ORDER BY due_date ASC;
   ```

### 複合インデックス

- `(user_id, status, created_at)`: ユーザー別ステータス検索 + ソート
- `(user_id, due_date)`: ユーザー別期限検索

## 設計方針

### DDD 観点

- **集約境界**: `User`と`Todo`は独立した集約
- **整合性**: ユーザー削除時の Todo 削除は CASCADE で対応
- **値オブジェクト**: `title`、`description`等はアプリケーション層で値オブジェクト化

### CQRS 観点

- **Command 側**: 正規化されたテーブル構造でデータ整合性重視
- **Query 側**: 必要に応じて VIEW やマテリアライズド VIEW で最適化
- **読み取り最適化**: 複合インデックスで一般的な検索パターンをカバー

### 将来の拡張性

- **Todo 詳細情報**: 必要に応じて`todo_details`テーブルを追加
- **タグ機能**: `tags`テーブルと`todo_tags`中間テーブル
- **コメント機能**: `todo_comments`テーブル
- **添付ファイル**: `todo_attachments`テーブル

## Liquibase 移行戦略

### 初期セットアップ

1. `V001_create_users_table.sql`: users テーブル作成
2. `V002_create_todos_table.sql`: todos テーブル作成
3. `V003_create_indexes.sql`: インデックス作成
4. `V004_insert_sample_data.sql`: サンプルデータ投入

### バージョニング

- 形式: `VXXX_description.sql`
- メジャーバージョン: スキーマ変更
- マイナーバージョン: データ変更、インデックス追加
