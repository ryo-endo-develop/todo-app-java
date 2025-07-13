# 開発手順書

## セットアップ手順

### 1. 前提条件の確認

```bash
# Java 17以上
java --version

# Maven 3.8以上
mvn --version

# Docker & Docker Compose
docker --version
docker compose version
```

### 2. データベース環境の構築

```bash
# PostgreSQLコンテナ起動
docker compose up -d

# 起動確認
docker compose ps

# ログ確認
docker compose logs postgres
```

### 3. データベースマイグレーション

```bash
# Infrastructure層に移動
cd todo-infrastructure

# マイグレーション実行（初回）
mvn liquibase:update

# 実行確認
mvn liquibase:status
```

### 4. データベース接続確認

```bash
# PostgreSQLクライアントで接続
docker exec -it todo-postgres psql -U todoapp -d todoapp

# テーブル確認
\dt

# サンプルデータ確認
SELECT * FROM users;
SELECT * FROM todos;

# 終了
\q
```

## 開発ワークフロー

### マイグレーション追加手順

1. **新しいマイグレーションファイル作成**

   ```bash
   # ファイル名形式: VXXX_description.sql
   touch todo-infrastructure/src/main/resources/db/changelog/changes/V005_add_new_feature.sql
   ```

2. **マイグレーション内容記述**

   ```sql
   -- liquibase formatted sql

   -- changeset todo-app:005-add-new-feature
   -- comment: 新機能のテーブル追加
   CREATE TABLE new_feature (
       id BIGSERIAL PRIMARY KEY,
       name VARCHAR(100) NOT NULL
   );

   -- rollback DROP TABLE new_feature;
   ```

3. **マイグレーション実行**
   ```bash
   cd todo-infrastructure
   mvn liquibase:update
   ```

### ロールバック手順

```bash
# 1つ前のバージョンに戻す
mvn liquibase:rollback -Dliquibase.rollbackCount=1

# 特定のchangesetまで戻す
mvn liquibase:rollback -Dliquibase.rollbackToDate=2024-01-01

# 確認用（実際には実行しない）
mvn liquibase:rollbackSQL -Dliquibase.rollbackCount=1
```

## トラブルシューティング

### よくある問題と解決方法

#### 1. データベース接続エラー

```bash
# コンテナ状態確認
docker compose ps

# ネットワーク確認
docker compose logs postgres

# 再起動
docker compose restart postgres
```

#### 2. マイグレーション失敗

```bash
# 状況確認
mvn liquibase:status

# 手動でchangelogテーブル確認
docker exec -it todo-postgres psql -U todoapp -d todoapp -c "SELECT * FROM databasechangelog ORDER BY dateexecuted DESC LIMIT 5;"

# 問題のあるchangesetを手動削除（注意して実行）
mvn liquibase:clearCheckSums
```

#### 3. 開発データリセット

```bash
# 全データ削除（注意！）
docker compose down -v

# 再構築
docker compose up -d
cd todo-infrastructure
mvn liquibase:update
```

## 最新の開発環境に追いつく手順

```bash
# 最新のコード取得
git pull origin main

# データベース更新
cd todo-infrastructure
mvn liquibase:update

# 依存関係更新
cd ..
mvn clean install
```

## データベース設計変更時の手順

1. **設計書更新**: `docs/database-design.md`の ER 図を更新
2. **マイグレーション作成**: 新しい VXXX\_\*.sql ファイル作成
3. **テスト**: 開発環境でマイグレーション実行
4. **レビュー**: PR 作成時にマイグレーションもレビュー対象
5. **本番適用**: 本番環境でのマイグレーション実行

## 便利なコマンド集

```bash
# 現在のスキーマ状況確認
mvn liquibase:diff

# 次に実行されるchangeset確認
mvn liquibase:status

# changelog詳細確認
mvn liquibase:history

# SQL生成（実行はしない）
mvn liquibase:updateSQL

# 特定のchangesetのみ実行
mvn liquibase:updateCount -Dliquibase.updateCount=1
```
