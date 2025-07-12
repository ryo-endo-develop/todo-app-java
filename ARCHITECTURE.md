# Todo App アーキテクチャ設計書

## 概要

DDD + CQRS アプローチを採用し、Effective Java の原則に従った Todo アプリケーションを開発します。

## 設計原則

### CQRS（Command Query Responsibility Segregation）

- **Command 側**: 書き込み操作、ドメインモデル中心、Repository 使用
- **Query 側**: 読み取り操作、DTO 中心、Query Service 使用
- **責務の分離**: 更新と参照のモデルを分離
- **パフォーマンス最適化**: それぞれに最適なデータアクセス方法

### Effective Java 準拠

- **不変性の重視**: 可能な限り immutable なオブジェクトを使用
- **null の排除**: Optional、Result 型の活用
- **継承よりコンポジション**: interface と実装の分離
- **単一責務の原則**: 各クラスは 1 つの責務のみ
- **fail-fast**: 不正な状態を早期に検出

### DDD 原則

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
├── entity/                         # エンティティ（集約ルート）
│   ├── Todo.java                  # Todo集約ルート
│   └── User.java                  # User集約ルート
├── valueobject/                   # 値オブジェクト
│   ├── TodoId.java               # Todo識別子
│   ├── TodoTitle.java            # Todoタイトル
│   ├── TodoDescription.java      # Todo説明
│   ├── UserId.java               # User識別子
│   └── UserName.java             # ユーザー名
├── enums/                         # 列挙型
│   ├── TodoStatus.java           # Todoステータス
│   └── UserRole.java             # ユーザー役割
├── repository/                    # Repository interface（書き込み側）
│   ├── TodoRepository.java       # Todo Repository
│   └── UserRepository.java       # User Repository
├── query/                         # Query側
│   ├── todo/
│   │   ├── TodoQueryService.java # Query Service interface
│   │   ├── TodoListQuery.java    # リスト表示用DTO
│   │   └── TodoDetailQuery.java  # 詳細表示用DTO
│   └── user/
│       ├── UserQueryService.java # Query Service interface
│       └── UserDetailQuery.java  # 詳細表示用DTO
├── service/                       # ドメインサービス
│   └── TodoDomainService.java
└── common/                        # 共通コンポーネント
    ├── Result.java               # 結果型
    ├── DomainException.java      # ドメイン例外
    └── ValueObject.java          # 値オブジェクト基底クラス
```

### Application Layer (todo-application)

**依存関係**: todo-domain

**パッケージ構成**:

```
com.todoapp.application/
├── command/                        # Command側（書き込み）
│   ├── todo/
│   │   ├── CreateTodoCommand.java     # コマンドDTO
│   │   ├── UpdateTodoCommand.java     # コマンドDTO
│   │   ├── DeleteTodoCommand.java     # コマンドDTO
│   │   └── TodoCommandHandler.java    # コマンドハンドラー
│   └── user/
│       ├── CreateUserCommand.java     # コマンドDTO
│       └── UserCommandHandler.java    # コマンドハンドラー
├── query/                          # Query側（読み取り）
│   ├── todo/
│   │   ├── GetTodoListQuery.java      # クエリDTO
│   │   ├── GetTodoDetailQuery.java    # クエリDTO
│   │   └── TodoQueryHandler.java      # クエリハンドラー
│   └── user/
│       ├── GetUserDetailQuery.java    # クエリDTO
│       └── UserQueryHandler.java      # クエリハンドラー
└── service/                       # アプリケーションサービス
    ├── TodoApplicationService.java   # Command/Queryの協調
    └── UserApplicationService.java   # Command/Queryの協調
```

### Infrastructure Layer (todo-infrastructure)

**依存関係**: todo-domain のみ（todo-application には依存しない）

**パッケージ構成**:

```
com.todoapp.infrastructure/
├── persistence/                    # データアクセス
│   ├── command/                   # Command側（書き込み）
│   │   ├── todo/
│   │   │   ├── TodoMapper.java        # MyBatis Mapper
│   │   │   ├── TodoRepositoryImpl.java # Repository実装
│   │   │   ├── TodoEntity.java        # DB Entity
│   │   │   └── TodoTableDef.java      # DynamicSQL Table定義
│   │   └── user/
│   │       ├── UserMapper.java        # MyBatis Mapper
│   │       ├── UserRepositoryImpl.java # Repository実装
│   │       ├── UserEntity.java        # DB Entity
│   │       └── UserTableDef.java      # DynamicSQL Table定義
│   └── query/                     # Query側（読み取り）
│       ├── todo/
│       │   ├── TodoQueryMapper.java   # 読み取り専用Mapper
│       │   ├── TodoQueryServiceImpl.java # Query Service実装
│       │   └── TodoQueryDto.java      # 読み取り専用DTO
│       └── user/
│           ├── UserQueryMapper.java   # 読み取り専用Mapper
│           ├── UserQueryServiceImpl.java # Query Service実装
│           └── UserQueryDto.java      # 読み取り専用DTO
├── config/                        # 設定
│   ├── MyBatisConfig.java         # MyBatis設定
│   └── InfrastructureConfig.java
└── external/                      # 外部サービス連携
    └── notification/
        └── NotificationService.java
```

### Web Layer (todo-web)

**依存関係**: todo-application, todo-infrastructure（DI コンテナ設定のみ）

**パッケージ構成**:

```
com.todoapp.web/
├── controller/                     # REST API
│   ├── command/                   # Command API（書き込み）
│   │   ├── TodoCommandController.java
│   │   └── UserCommandController.java
│   └── query/                     # Query API（読み取り）
│       ├── TodoQueryController.java
│       └── UserQueryController.java
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

- **Application Layer**: Domain Layer の抽象化（interface）のみに依存
- **Infrastructure Layer**: Domain Layer の interface を実装
- **Web Layer**: DI コンテナで Infrastructure Layer の実装を Application Layer に注入
- **依存関係逆転**: Application Layer は Infrastructure Layer の具体的実装を知らない

## 技術スタック

### 必須依存関係

- **Java**: 17 以上
- **Spring Boot**: 3.x
- **MyBatis**: 3.x（ORM）
- **MyBatis DynamicSQL**: タイプセーフなクエリビルダー
- **PostgreSQL**: 本番用データベース
- **Docker**: コンテナ化
- **Maven**: ビルドツール

### Effective Java 対応

- **Immutable Objects**: すべての Value Object は不変
- **Optional**: null の代替
- **Result Type**: 例外の代替（カスタム実装）
- **Builder Pattern**: 複雑なオブジェクトの構築
- **Factory Method**: オブジェクトの生成

### MyBatis DynamicSQL の利点

- **タイプセーフ**: コンパイル時に SQL エラーを検出
- **リファクタリング対応**: IDE の自動リファクタリングが効く
- **SQL 追跡可能**: 生成される SQL を実行時に確認可能
- **Java 統合**: XML ではなく Java コードでクエリを記述

### CQRS の利点

- **責務の分離**: 更新と参照の最適化が独立
- **スケーラビリティ**: Command/Query で異なるスケーリング戦略
- **パフォーマンス**: 読み取り専用クエリの最適化
- **保守性**: 複雑な業務ロジックと表示ロジックの分離

## 今後の開発段階

1. **フェーズ 1**: プロジェクト構造と POM ファイルの作成
2. **フェーズ 2**: Domain Layer の実装（Value Object、Entity）
3. **フェーズ 3**: Repository interface の定義
4. **フェーズ 4**: Application Layer の実装
5. **フェーズ 5**: Infrastructure Layer の実装
6. **フェーズ 6**: Web Layer の実装
7. **フェーズ 7**: テストの実装と統合

## 設計上の決定事項

### null 安全性

- すべてのメソッドパラメータは非 null
- 戻り値で null の可能性がある場合は Optional 使用
- 失敗の可能性があるオペレーションは Result 型を使用

### 不変性

- すべての Value Object は不変
- Entity の状態変更は専用メソッドを通じて行う
- コレクションは防御的コピーを使用

### 継承の制限

- 継承は最小限に抑制
- 共通機能は interface とコンポジションで実現
- 抽象基底クラスは避ける（必要な場合のみ使用）
