# Todo App アーキテクチャ設計書

## 概要
DDDアプローチを採用し、Effective Javaの原則に従ったTodoアプリケーションを開発します。

## 設計原則

### Effective Java準拠
- **不変性の重視**: 可能な限りimmutableなオブジェクトを使用
- **nullの排除**: Optional、Result型の活用
- **継承よりコンポジション**: interfaceと実装の分離
- **単一責務の原則**: 各クラスは1つの責務のみ
- **fail-fast**: 不正な状態を早期に検出

### DDD原則
- **レイヤードアーキテクチャ**: 関心の分離
- **ドメインモデルの独立性**: 技術的関心からの分離
- **集約の境界**: 一貫性の保証
- **ユビキタス言語**: ドメインエキスパートとの共通言語

## マルチモジュール構成

```
todo-app/
├── pom.xml                          # 親POM
├── todo-domain/                     # ドメイン層
│   ├── pom.xml
│   └── src/main/java/
│       └── com/todoapp/domain/
├── todo-application/                # アプリケーション層
│   ├── pom.xml
│   └── src/main/java/
│       └── com/todoapp/application/
├── todo-infrastructure/             # インフラストラクチャ層
│   ├── pom.xml
│   └── src/main/java/
│       └── com/todoapp/infrastructure/
└── todo-web/                       # プレゼンテーション層
    ├── pom.xml
    └── src/main/java/
        └── com/todoapp/web/
```

## 各層の責務

### Domain Layer (todo-domain)
**依存関係**: なし（他の層に依存しない）

**パッケージ構成**:
```
com.todoapp.domain/
├── model/                          # ドメインモデル
│   ├── todo/
│   │   ├── Todo.java              # 集約ルート
│   │   ├── TodoId.java            # 値オブジェクト
│   │   ├── TodoTitle.java         # 値オブジェクト
│   │   ├── TodoStatus.java        # 列挙型
│   │   └── TodoRepository.java    # リポジトリインターface
│   └── user/
│       ├── User.java              # 集約ルート
│       ├── UserId.java            # 値オブジェクト
│       └── UserRepository.java    # リポジトリインターface
├── service/                        # ドメインサービス
│   └── TodoDomainService.java
└── common/                         # 共通コンポーネント
    ├── Result.java                # 結果型
    ├── DomainException.java       # ドメイン例外
    └── ValueObject.java           # 値オブジェクト基底クラス
```

### Application Layer (todo-application)
**依存関係**: todo-domain

**パッケージ構成**:
```
com.todoapp.application/
├── usecase/                        # ユースケース
│   ├── todo/
│   │   ├── CreateTodoUseCase.java
│   │   ├── UpdateTodoUseCase.java
│   │   ├── DeleteTodoUseCase.java
│   │   └── GetTodoListUseCase.java
│   └── user/
│       ├── CreateUserUseCase.java
│       └── GetUserUseCase.java
├── dto/                           # データ転送オブジェクト
│   ├── todo/
│   │   ├── CreateTodoRequest.java
│   │   ├── UpdateTodoRequest.java
│   │   └── TodoResponse.java
│   └── user/
│       ├── CreateUserRequest.java
│       └── UserResponse.java
└── service/                       # アプリケーションサービス
    ├── TodoApplicationService.java
    └── UserApplicationService.java
```

### Infrastructure Layer (todo-infrastructure)
**依存関係**: todo-domain のみ（todo-applicationには依存しない）

**パッケージ構成**:
```
com.todoapp.infrastructure/
├── persistence/                    # データアクセス
│   ├── todo/
│   │   ├── TodoRepositoryImpl.java
│   │   └── TodoEntity.java
│   └── user/
│       ├── UserRepositoryImpl.java
│       └── UserEntity.java
├── config/                        # 設定
│   ├── DatabaseConfig.java
│   └── InfrastructureConfig.java
└── external/                      # 外部サービス連携
    └── notification/
        └── NotificationService.java
```

### Web Layer (todo-web)
**依存関係**: todo-application, todo-infrastructure（DIコンテナ設定のみ）

**パッケージ構成**:
```
com.todoapp.web/
├── controller/                     # REST API
│   ├── TodoController.java
│   └── UserController.java
├── config/                        # Web設定
│   └── WebConfig.java
├── exception/                     # 例外ハンドリング
│   └── GlobalExceptionHandler.java
└── TodoWebApplication.java        # メインクラス
```

## 依存関係の流れ（依存関係逆転の原則）

```
Web Layer
    ↓
Application Layer → Domain Layer ← Infrastructure Layer
```

**重要な原則**：
- **Application Layer**: Domain Layerの抽象化（interface）のみに依存
- **Infrastructure Layer**: Domain Layerのinterfaceを実装
- **Web Layer**: DIコンテナでInfrastructure Layerの実装をApplication Layerに注入
- **依存関係逆転**: Application LayerはInfrastructure Layerの具体的実装を知らない

## 技術スタック

### 必須依存関係
- **Java**: 17以上
- **Spring Boot**: 3.x
- **Spring Data JPA**: データアクセス
- **PostgreSQL**: 本番用データベース
- **Docker**: コンテナ化
- **Maven**: ビルドツール

### Effective Java対応
- **Immutable Objects**: すべてのValue Objectは不変
- **Optional**: nullの代替
- **Result Type**: 例外の代替（カスタム実装）
- **Builder Pattern**: 複雑なオブジェクトの構築
- **Factory Method**: オブジェクトの生成

## 今後の開発段階

1. **フェーズ1**: プロジェクト構造とPOMファイルの作成
2. **フェーズ2**: Domain Layerの実装（Value Object、Entity）
3. **フェーズ3**: Repository interfaceの定義
4. **フェーズ4**: Application Layerの実装
5. **フェーズ5**: Infrastructure Layerの実装
6. **フェーズ6**: Web Layerの実装
7. **フェーズ7**: テストの実装と統合

## 設計上の決定事項

### null安全性
- すべてのメソッドパラメータは非null
- 戻り値でnullの可能性がある場合はOptional使用
- 失敗の可能性があるオペレーションはResult型を使用

### 不変性
- すべてのValue Objectは不変
- Entityの状態変更は専用メソッドを通じて行う
- コレクションは防御的コピーを使用

### 継承の制限
- 継承は最小限に抑制
- 共通機能はinterfaceとコンポジションで実現
- 抽象基底クラスは避ける（必要な場合のみ使用）

## Docker環境

### 開発環境
```yaml
# compose.yml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: todoapp
      POSTGRES_USER: todoapp
      POSTGRES_PASSWORD: todoapp
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### 実行方法
```bash
# データベース起動
docker compose up -d

# アプリケーション起動
mvn spring-boot:run -pl todo-web
```

このアーキテクチャに基づいて、段階的に実装を進めていきます。