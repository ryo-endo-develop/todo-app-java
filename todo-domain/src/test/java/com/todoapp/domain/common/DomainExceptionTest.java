/* (C) 2025 Todo App Project */
package com.todoapp.domain.common;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * DomainExceptionの単体テスト
 *
 * <p>テスト観点: - 例外の基本的な動作 - ファクトリメソッドの正確性 - null安全性の検証 - メッセージフォーマットの一貫性
 */
@DisplayName("DomainException")
class DomainExceptionTest {

  @Nested
  @DisplayName("基本的な例外作成")
  class BasicExceptionCreation {

    @Test
    @DisplayName("メッセージのみで例外が作成される")
    void shouldCreateException_WithMessageOnly() {
      // Arrange
      String message = "ドメインエラーが発生しました";

      // Act
      DomainException exception = new DomainException(message);

      // Assert
      assertThat(exception.getMessage()).isEqualTo(message);
      assertThat(exception.getCause()).isNull();
      assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("メッセージと原因例外で例外が作成される")
    void shouldCreateException_WithMessageAndCause() {
      // Arrange
      String message = "ドメインエラーが発生しました";
      IllegalArgumentException cause = new IllegalArgumentException("原因例外");

      // Act
      DomainException exception = new DomainException(message, cause);

      // Assert
      assertThat(exception.getMessage()).isEqualTo(message);
      assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("nullメッセージは例外をスローする")
    void shouldThrowException_WhenMessageIsNull() {
      // Assert
      assertThatThrownBy(() -> new DomainException(null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Error message must not be null");
    }

    @Test
    @DisplayName("空のメッセージは例外をスローする")
    void shouldThrowException_WhenMessageIsEmpty() {
      // Assert
      assertThatThrownBy(() -> new DomainException(""))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Error message must not be empty");

      assertThatThrownBy(() -> new DomainException("   "))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Error message must not be empty");
    }

    @Test
    @DisplayName("nullの原因例外は例外をスローする")
    void shouldThrowException_WhenCauseIsNull() {
      // Assert
      assertThatThrownBy(() -> new DomainException("メッセージ", null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Cause must not be null");
    }
  }

  @Nested
  @DisplayName("ビジネスルール違反ファクトリメソッド")
  class BusinessRuleViolationFactory {

    @Test
    @DisplayName("ビジネスルール違反例外が正しく作成される")
    void shouldCreateBusinessRuleViolationException() {
      // Arrange
      String ruleName = "タイトル必須チェック";
      String details = "Todoタイトルは必須項目です";

      // Act
      DomainException exception = DomainException.businessRuleViolation(ruleName, details);

      // Assert
      assertThat(exception.getMessage())
          .isEqualTo("Business rule violation [タイトル必須チェック]: Todoタイトルは必須項目です");
      assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("ルール名がnullの場合は例外をスローする")
    void shouldThrowException_WhenRuleNameIsNull() {
      // Assert
      assertThatThrownBy(() -> DomainException.businessRuleViolation(null, "詳細"))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Rule name must not be null");
    }

    @Test
    @DisplayName("詳細がnullの場合は例外をスローする")
    void shouldThrowException_WhenDetailsIsNull() {
      // Assert
      assertThatThrownBy(() -> DomainException.businessRuleViolation("ルール", null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Details must not be null");
    }
  }

  @Nested
  @DisplayName("不正な状態ファクトリメソッド")
  class InvalidStateFactory {

    @Test
    @DisplayName("不正な状態例外が正しく作成される")
    void shouldCreateInvalidStateException() {
      // Arrange
      String currentState = "完了";
      String operation = "編集";

      // Act
      DomainException exception = DomainException.invalidState(currentState, operation);

      // Assert
      assertThat(exception.getMessage()).isEqualTo("Invalid state [完了] for operation [編集]");
      assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("現在の状態がnullの場合は例外をスローする")
    void shouldThrowException_WhenCurrentStateIsNull() {
      // Assert
      assertThatThrownBy(() -> DomainException.invalidState(null, "操作"))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Current state must not be null");
    }

    @Test
    @DisplayName("操作がnullの場合は例外をスローする")
    void shouldThrowException_WhenOperationIsNull() {
      // Assert
      assertThatThrownBy(() -> DomainException.invalidState("状態", null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Operation must not be null");
    }
  }

  @Nested
  @DisplayName("見つからないファクトリメソッド")
  class NotFoundFactory {

    @Test
    @DisplayName("見つからない例外が正しく作成される")
    void shouldCreateNotFoundException() {
      // Arrange
      String entityType = "Todo";
      String identifier = "123";

      // Act
      DomainException exception = DomainException.notFound(entityType, identifier);

      // Assert
      assertThat(exception.getMessage()).isEqualTo("Todo not found: 123");
      assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("エンティティタイプがnullの場合は例外をスローする")
    void shouldThrowException_WhenEntityTypeIsNull() {
      // Assert
      assertThatThrownBy(() -> DomainException.notFound(null, "123"))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Entity type must not be null");
    }

    @Test
    @DisplayName("識別子がnullの場合は例外をスローする")
    void shouldThrowException_WhenIdentifierIsNull() {
      // Assert
      assertThatThrownBy(() -> DomainException.notFound("Todo", null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Identifier must not be null");
    }
  }

  @Nested
  @DisplayName("重複ファクトリメソッド")
  class DuplicateFactory {

    @Test
    @DisplayName("重複例外が正しく作成される")
    void shouldCreateDuplicateException() {
      // Arrange
      String entityType = "User";
      String identifier = "user@example.com";

      // Act
      DomainException exception = DomainException.duplicate(entityType, identifier);

      // Assert
      assertThat(exception.getMessage()).isEqualTo("User already exists: user@example.com");
      assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("エンティティタイプがnullの場合は例外をスローする")
    void shouldThrowException_WhenEntityTypeIsNullInDuplicate() {
      // Assert
      assertThatThrownBy(() -> DomainException.duplicate(null, "identifier"))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Entity type must not be null");
    }

    @Test
    @DisplayName("識別子がnullの場合は例外をスローする")
    void shouldThrowException_WhenIdentifierIsNullInDuplicate() {
      // Assert
      assertThatThrownBy(() -> DomainException.duplicate("Entity", null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Identifier must not be null");
    }
  }

  @Nested
  @DisplayName("例外の継承とRuntime例外の性質")
  class ExceptionInheritanceAndRuntimeProperties {

    @Test
    @DisplayName("RuntimeExceptionを継承している")
    void shouldExtendRuntimeException() {
      // Arrange
      DomainException exception = new DomainException("テスト");

      // Assert
      assertThat(exception).isInstanceOf(RuntimeException.class);
      assertThat(exception).isInstanceOf(Exception.class);
      assertThat(exception).isInstanceOf(Throwable.class);
    }

    @Test
    @DisplayName("チェック例外ではない（unchecked exception）")
    void shouldBeUncheckedException() {
      // Assert - 以下のメソッドはコンパイルエラーにならない（チェック例外ではない証拠）
      assertThatCode(
              () -> {
                throw new DomainException("テスト例外");
              })
          .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("スタックトレースが適切に設定される")
    void shouldHaveProperStackTrace() {
      // Act
      DomainException exception = new DomainException("スタックトレーステスト");

      // Assert
      assertThat(exception.getStackTrace()).isNotEmpty();
      assertThat(exception.getStackTrace()[0].getMethodName())
          .isEqualTo("shouldHaveProperStackTrace");
    }
  }

  @Nested
  @DisplayName("実用的なシナリオ")
  class PracticalScenarios {

    @Test
    @DisplayName("ネストした例外チェーンが正しく動作する")
    void shouldWorkWithNestedExceptionChain() {
      // Arrange
      IllegalArgumentException rootCause = new IllegalArgumentException("根本原因");
      RuntimeException intermediateCause = new RuntimeException("中間原因", rootCause);
      DomainException domainException = new DomainException("ドメイン例外", intermediateCause);

      // Assert
      assertThat(domainException.getCause()).isEqualTo(intermediateCause);
      assertThat(domainException.getCause().getCause()).isEqualTo(rootCause);
    }

    @Test
    @DisplayName("ログ出力に適したメッセージ形式")
    void shouldHaveLoggingFriendlyMessages() {
      // Act
      DomainException businessRule = DomainException.businessRuleViolation("必須チェック", "値が不正");
      DomainException invalidState = DomainException.invalidState("削除済み", "更新");
      DomainException notFound = DomainException.notFound("Todo", "999");
      DomainException duplicate = DomainException.duplicate("User", "admin");

      // Assert - ログで識別しやすい形式
      assertThat(businessRule.getMessage()).startsWith("Business rule violation");
      assertThat(invalidState.getMessage()).startsWith("Invalid state");
      assertThat(notFound.getMessage()).contains("not found");
      assertThat(duplicate.getMessage()).contains("already exists");
    }

    @Test
    @DisplayName("国際化対応のメッセージ構造")
    void shouldSupportInternationalizationStructure() {
      // Arrange - 将来的な国際化を想定したテスト
      String englishRule = "Required field validation";
      String englishDetails = "Title field is required";

      // Act
      DomainException exception =
          DomainException.businessRuleViolation(englishRule, englishDetails);

      // Assert
      assertThat(exception.getMessage())
          .contains("Business rule violation")
          .contains(englishRule)
          .contains(englishDetails);
    }
  }
}
