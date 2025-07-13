# Todo App 開発環境セットアップ

## 📋 前提条件

### 必須ツール
- **Docker**: 24.0以上
- **Docker Compose**: 2.0以上

### 不要なツール（Docker化により）
- ❌ Java（ローカルインストール不要）
- ❌ Maven（Maven Wrapper使用）

## 🚀 クイックスタート

### 1. プロジェクトのクローン
```bash
git clone <repository-url>
cd todo-app
```

### 2. 開発環境の起動
```bash
# データベースのみ起動
docker compose up -d postgres

# Java開発環境も起動
docker compose up -d java-dev

# すべてのサービス起動（将来のWeb層含む）
docker compose --profile web up -d
```

### 3. 開発用コンテナに接続
```bash
# Java開発環境に接続
docker compose exec java-dev bash

# コンテナ内でMavenコマンド実行
./mvnw clean compile
./mvnw test
```

## 💻 開発コマンド

### Maven Wrapper使用（推奨）

#### ローカル実行（Java 21対応）

**🚀 シンプルなテスト実行手順**

Maven Wrapperを使用（ローカルMaven設定に依存しない）：

```bash
# 環境設定（初回のみ）
export MAVEN_PROJECTBASEDIR="$(pwd)"
unset MAVEN_OPTS

# 基本的なテスト実行
./mvnw clean test -pl todo-domain

# 特定のテストクラスのみ実行
./mvnw test -pl todo-domain -Dtest=ResultTest
./mvnw test -pl todo-domain -Dtest=TodoIdTest

# カバレッジレポート付きで実行
./mvnw clean test jacoco:report -pl todo-domain

# レポート確認
open todo-domain/target/site/jacoco/index.html
```

**現在のテストケース（todo-domainモジュール）**

| テストクラス | テストケース数 | 説明 |
|------------|-------------|------|
| `ResultTest` | 25 | Result型の機能テスト（成功・失敗・関数型操作） |
| `TodoIdTest` | 18 | TodoId値オブジェクトのテスト（作成・比較・検証） |

**ローカルMavenを使用する場合（3.9.0以上）**
```bash
# ローカルのMavenを使用（推奨）
mvn clean test -pl todo-domain
mvn test -pl todo-domain -Dtest=ResultTest
mvn clean test jacoco:report -pl todo-domain
```

#### Docker環境での実行
```bash
# 開発コンテナでコマンド実行
docker compose exec java-dev ./mvnw clean compile
docker compose exec java-dev ./mvnw test

# ワンショットコマンド実行
docker compose run --rm java-dev ./mvnw test -pl todo-domain

# カバレッジレポート付きテスト
docker compose run --rm java-dev ./mvnw test jacoco:report
```

### データベース操作

#### データベース接続
```bash
# PostgreSQLクライアントで接続
docker compose exec postgres psql -U todoapp -d todoapp

# テーブル確認
\dt

# サンプルデータ確認
SELECT * FROM users;
SELECT * FROM todos;
```

#### マイグレーション実行
```bash
# Liquibaseマイグレーション
docker compose exec java-dev ./mvnw liquibase:update -pl todo-infrastructure

# マイグレーション状況確認
docker compose exec java-dev ./mvnw liquibase:status -pl todo-infrastructure
```

## 🐳 Docker環境の詳細

### コンテナ構成

| サービス | 用途 | ポート | ボリューム |
|----------|------|--------|------------|
| **postgres** | データベース | 5432 | `postgres_data` |
| **java-dev** | 開発環境 | - | ソースコード, Mavenキャッシュ |
| **app** | アプリケーション | 8080 | ソースコード |

### ボリューム管理
```bash
# Mavenキャッシュの確認
docker volume ls | grep maven

# ボリュームのクリア（必要時）
docker compose down -v
docker volume prune
```

### パフォーマンス最適化
```bash
# Mavenキャッシュを利用した高速ビルド
docker compose exec java-dev ./mvnw dependency:go-offline

# 並列ビルド
docker compose exec java-dev ./mvnw -T 1C clean compile
```

## 🔧 開発ワークフロー

### 1. 新機能開発
```bash
# 1. 開発環境起動
docker compose up -d postgres java-dev

# 2. 開発コンテナに接続
docker compose exec java-dev bash

# 3. テスト駆動開発
./mvnw test -pl todo-domain
# コード修正
./mvnw test -pl todo-domain

# 4. 全体テスト
./mvnw test
```

### 2. データベーススキーマ変更
```bash
# 1. マイグレーションファイル作成
# todo-infrastructure/src/main/resources/db/changelog/changes/VXXX_*.sql

# 2. マイグレーション実行
docker compose exec java-dev ./mvnw liquibase:update -pl todo-infrastructure

# 3. 変更確認
docker compose exec postgres psql -U todoapp -d todoapp -c "\dt"
```

### 3. CI/CD準備
```bash
# 本番ビルドのテスト
docker compose run --rm java-dev ./mvnw clean package

# セキュリティチェック
docker compose run --rm java-dev ./mvnw dependency:check

# 静的解析
docker compose run --rm java-dev ./mvnw spotbugs:check
```

## 🧪 テスト環境

### 単体テスト
```bash
# Domain層のテスト
docker compose exec java-dev ./mvnw test -pl todo-domain

# カバレッジレポート
docker compose exec java-dev ./mvnw test jacoco:report -pl todo-domain

# レポート確認（ホストから）
open todo-domain/target/site/jacoco/index.html
```

### 統合テスト（将来実装）
```bash
# データベース付き統合テスト
docker compose exec java-dev ./mvnw verify -P integration-test
```

## 🛠️ トラブルシューティング

### Maven Wrapper関連の問題

#### 1. Maven Wrapperの実行権限エラー
```bash
# 実行権限を付与
chmod +x mvnw
```

#### 2. Maven Wrapperのダウンロードエラー
```bash
# キャッシュをクリア
rm -rf ~/.m2/wrapper/
```

### Docker環境の問題

#### 1. ポート競合
```bash
# ポート使用状況確認
docker compose ps
lsof -i :5432

# 別ポートで起動
docker compose -f compose.override.yml up -d
```

#### 2. Mavenキャッシュ問題
```bash
# キャッシュクリア
docker compose exec java-dev ./mvnw dependency:purge-local-repository

# 強制的な依存関係ダウンロード
docker compose exec java-dev ./mvnw dependency:resolve -U
```

#### 3. パーミッション問題（Linux）
```bash
# ユーザーIDを合わせてコンテナ実行
docker compose exec --user $(id -u):$(id -g) java-dev bash
```

#### 4. メモリ不足
```bash
# Maven JVMオプション調整
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
docker compose exec java-dev ./mvnw clean compile
```

## 📊 開発メトリクス

### パフォーマンス目標
- **コンパイル時間**: < 30秒
- **単体テスト実行**: < 60秒
- **コンテナ起動時間**: < 30秒

### 品質目標
- **テストカバレッジ**: > 90%
- **ビルド成功率**: 100%
- **セキュリティ脆弱性**: 0件

この環境により、**Java/Mavenのローカルインストール不要**で、**チーム全体で統一された開発環境**を提供できます。
