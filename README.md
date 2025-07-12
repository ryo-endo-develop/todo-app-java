# Todo App

## 開発環境セットアップ

### 前提条件
- Java 17以上
- Maven 3.8以上
- Docker & Docker Compose

### データベース起動
```bash
docker compose up -d
```

### データベース停止
```bash
docker compose down
```

### データベース完全削除（データも削除）
```bash
docker compose down -v
```

### データベース接続情報
- Host: localhost
- Port: 5432
- Database: todoapp
- Username: todoapp
- Password: todoapp

### データベースマイグレーション
```bash
# マイグレーション実行
cd todo-infrastructure
mvn liquibase:update

# マイグレーション状況確認
mvn liquibase:status

# ロールバック（1つ前に戻す）
mvn liquibase:rollback -Dliquibase.rollbackCount=1

# 特定タグまでロールバック
mvn liquibase:rollback -Dliquibase.rollbackTag=v1.0

# SQL生成（実行せず確認のみ）
mvn liquibase:updateSQL
```

### アプリケーション起動（今後実装予定）
```bash
mvn spring-boot:run -pl todo-web
```

## アーキテクチャ
詳細は [ARCHITECTURE.md](./ARCHITECTURE.md) を参照
