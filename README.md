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

### アプリケーション起動（今後実装予定）
```bash
mvn spring-boot:run -pl todo-web
```

## アーキテクチャ
詳細は [ARCHITECTURE.md](./ARCHITECTURE.md) を参照
