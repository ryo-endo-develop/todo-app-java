# Todo App テスト戦略・方針書

## テスト戦略の基本方針

### 1. テストピラミッド戦略

```
    E2E Tests (少数)
      ↑
  Integration Tests (中程度)
      ↑
   Unit Tests (多数)
```

- **Unit Tests (70%)**: 高速・安定・独立
- **Integration Tests (20%)**: コンポーネント間の連携
- **E2E Tests (10%)**: ユーザーシナリオの確認

### 2. 段階的テスト導入計画

#### Phase 1: 基盤クラステスト（現在）

**対象**: 変更頻度が低く、安定したクラス

- `Result<T>`
- ValueObject クラス（`TodoId`, `TodoTitle`, `UserName`等）
- Enum クラス（`TodoStatus`, `UserRole`）

**理由**:

- 設計変更に強い
- 他のクラスの基盤となる
- バグの影響範囲が大きい

#### Phase 2: ドメインロジックテスト（Entity 完成後）

**対象**: ビジネスロジックを含むクラス

- Entity クラス（`Todo`, `User`）
- ドメインサービス（`TodoStatusTransitionPolicy`等）

#### Phase 3: 統合テスト（全体完成後）

**対象**: コンポーネント間の連携

- Repository 実装
- Application Service
- Web Controller

## テスト実装ガイドライン

### 1. AAA パターンの徹底

```java
@Test
void shouldCreateValidTodoId_WhenValidValueProvided() {
    // Arrange（準備）
    long validId = 123L;

    // Act（実行）
    Result<TodoId> result = TodoId.of(validId);

    // Assert（検証）
    assertTrue(result.isSuccess());
    assertEquals(validId, result.getValue().getValue());
}
```

### 2. テスト命名規則

**パターン**: `should{期待する結果}\_When{条件}`

```java
@Test
void shouldReturnFailure_WhenTodoIdIsZero() { }

@Test
void shouldReturnFailure_WhenTodoIdIsNegative() { }

@Test
void shouldCreateTodoTitle_WhenValidStringProvided() { }

@Test
void shouldRejectTodoTitle_WhenStringTooLong() { }
```

### 3. Mock 使用の原則

#### ✅ Mock を使う場合

- **外部依存**: データベース、外部 API、ファイルシステム
- **時間依存**: 現在時刻、ランダム値
- **複雑な協調**: 複数のサービス間の連携

#### ❌ Mock を使わない場合

- **ValueObject**: 不変・純粋関数
- **Enum**: 状態を持たない
- **純粋なドメインロジック**: 外部依存なし

```java
// ✅ Good: ValueObjectは実際のオブジェクトを使用
@Test
void shouldCalculateLength_WhenTodoTitleCreated() {
    // Arrange
    String title = "Sample Todo";

    // Act
    Result<TodoTitle> result = TodoTitle.of(title);

    // Assert
    assertTrue(result.isSuccess());
    assertEquals(title.length(), result.getValue().length());
}

// ✅ Good: 外部依存はMockを使用
@Test
void shouldSaveTodo_WhenValidTodoProvided() {
    // Arrange
    TodoRepository mockRepository = mock(TodoRepository.class);
    Todo todo = createValidTodo();
    when(mockRepository.save(todo)).thenReturn(Result.success(null));

    // Act & Assert
    // ...
}
```

### 4. テストデータの管理

#### Test Data Builder パターンの使用

```java
public class TestDataBuilder {

    public static class TodoIdBuilder {
        private long value = 1L;

        public TodoIdBuilder withValue(long value) {
            this.value = value;
            return this;
        }

        public Result<TodoId> build() {
            return TodoId.of(value);
        }

        public TodoId buildValid() {
            return build().getValue();
        }
    }

    public static TodoIdBuilder todoId() {
        return new TodoIdBuilder();
    }
}

// 使用例
@Test
void test() {
    TodoId todoId = TestDataBuilder.todoId()
        .withValue(123L)
        .buildValid();
}
```

### 5. アサーションの原則

#### 1 テスト 1 アサーション（推奨）

```java
// ✅ Good: 1つの関心事をテスト
@Test
void shouldReturnSuccessResult_WhenValidIdProvided() {
    Result<TodoId> result = TodoId.of(123L);
    assertTrue(result.isSuccess());
}

@Test
void shouldContainCorrectValue_WhenValidIdProvided() {
    Result<TodoId> result = TodoId.of(123L);
    assertEquals(123L, result.getValue().getValue());
}
```

#### 関連するアサーションのグループ化（許可）

```java
// ✅ Acceptable: 密接に関連するアサーション
@Test
void shouldCreateValidResult_WhenValidParametersProvided() {
    Result<TodoTitle> result = TodoTitle.of("Valid Title");

    assertAll(
        () -> assertTrue(result.isSuccess()),
        () -> assertEquals("Valid Title", result.getValue().getValue()),
        () -> assertEquals(11, result.getValue().length())
    );
}
```

### 6. エラーケースのテスト戦略

#### 境界値テスト

```java
@Test
void shouldRejectTodoTitle_WhenLengthExceedsLimit() {
    // Arrange
    String tooLongTitle = "a".repeat(256); // MAX_LENGTH = 255

    // Act
    Result<TodoTitle> result = TodoTitle.of(tooLongTitle);

    // Assert
    assertTrue(result.isFailure());
    assertTrue(result.getErrorMessage().orElse("").contains("255文字以内"));
}
```

#### 異常値テスト

```java
@Test
void shouldRejectTodoId_WhenValueIsNull() {
    // 現在はプリミティブ型なのでnullテストは不要
    // 将来的にLongに変更した場合のテスト例
}

@Test
void shouldRejectTodoTitle_WhenValueIsNull() {
    Result<TodoTitle> result = TodoTitle.of(null);

    assertTrue(result.isFailure());
    assertTrue(result.getErrorMessage().orElse("").contains("必須"));
}
```

## 壊れにくいテストの原則

### 1. 実装詳細ではなく、振る舞いをテスト

```java
// ❌ Bad: 実装詳細に依存
@Test
void shouldCallValidationMethod() {
    // 内部メソッドの呼び出しをチェック - 壊れやすい
}

// ✅ Good: 公開された振る舞いをテスト
@Test
void shouldRejectInvalidInput_WhenProvidedInvalidData() {
    // 期待される結果をチェック - 安定
}
```

### 2. テスト間の独立性

```java
// ✅ Good: 各テストが独立
@Test
void test1() {
    TodoId id = TestDataBuilder.todoId().withValue(1L).buildValid();
    // テスト固有のロジック
}

@Test
void test2() {
    TodoId id = TestDataBuilder.todoId().withValue(2L).buildValid();
    // 別のテスト固有のロジック
}
```

### 3. 決定論的なテスト

```java
// ❌ Bad: 非決定論的
@Test
void shouldCreateUniqueId() {
    TodoId id1 = TodoId.generate(); // ランダム値
    TodoId id2 = TodoId.generate();
    assertNotEquals(id1, id2); // 時々失敗する可能性
}

// ✅ Good: 決定論的
@Test
void shouldCreateTodoId_WhenSpecificValueProvided() {
    Result<TodoId> result = TodoId.of(123L);
    assertEquals(123L, result.getValue().getValue());
}
```

## テストツール・ライブラリ

### 必須ライブラリ

- **JUnit 5**: テストフレームワーク
- **AssertJ**: 流暢なアサーション
- **Mockito**: モックライブラリ

### 推奨ライブラリ

- **Testcontainers**: 統合テスト用（将来）
- **ArchUnit**: アーキテクチャテスト用（将来）

## 現在の実装対象

### Phase 1 で実装するテスト

1. `Result<T>` クラス
2. `TodoId` クラス
3. `TodoTitle` クラス
4. `TodoDescription` クラス
5. `UserName` クラス
6. `TodoStatus` Enum
7. `UserRole` Enum

### テスト網羅率の目標

- **Unit Tests**: 90%以上
- **重要なビジネスロジック**: 100%
- **エラーハンドリング**: 100%

## CI/CD でのテスト実行

### 実行方針

```bash
# 高速テスト（単体テスト）- 毎回実行
mvn test

# 統合テスト - PR時実行
mvn verify -P integration-test

# E2Eテスト - リリース前実行
mvn verify -P e2e-test
```

### 品質ゲート

- **テスト成功率**: 100%
- **カバレッジ**: 90%以上
- **実行時間**: 単体テスト 30 秒以内

この方針に基づいて、段階的にテストを実装していきます。
