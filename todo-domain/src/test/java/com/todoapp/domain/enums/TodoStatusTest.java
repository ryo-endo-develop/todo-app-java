/* (C) 2025 Todo App Project */
package com.todoapp.domain.enums;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * TodoStatusの単体テスト
 *
 * <p>テスト観点: - Enumの基本的な安全性 - 文字列変換の正確性 - 不正値の適切な拒否 - null安全性の検証
 */
@DisplayName("TodoStatus")
class TodoStatusTest {

  @Nested
  @DisplayName("基本的な属性とEnum定義")
  class BasicAttributesAndEnumDefinition {

    @ParameterizedTest
    @EnumSource(TodoStatus.class)
    @DisplayName("すべてのステータスが基本属性を持つ")
    void shouldHaveBasicAttributes_ForAllStatuses(TodoStatus status) {
      // Assert
      assertThat(status.getCode()).isNotNull().isNotBlank();
      assertThat(status.getDisplayName()).isNotNull().isNotBlank();
      assertThat(status.getDescription()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("期待される数のステータスが定義されている")
    void shouldHaveExpectedNumberOfStatuses() {
      // Assert
      assertThat(TodoStatus.values()).hasSize(4);
    }

    @Test
    @DisplayName("各ステータスの基本属性が正しく設定されている")
    void shouldHaveCorrectBasicAttributes() {
      // Assert
      assertThat(TodoStatus.TODO.getCode()).isEqualTo("TODO");
      assertThat(TodoStatus.TODO.getDisplayName()).isEqualTo("未完了");

      assertThat(TodoStatus.IN_PROGRESS.getCode()).isEqualTo("IN_PROGRESS");
      assertThat(TodoStatus.IN_PROGRESS.getDisplayName()).isEqualTo("進行中");

      assertThat(TodoStatus.COMPLETED.getCode()).isEqualTo("COMPLETED");
      assertThat(TodoStatus.COMPLETED.getDisplayName()).isEqualTo("完了");

      assertThat(TodoStatus.DELETED.getCode()).isEqualTo("DELETED");
      assertThat(TodoStatus.DELETED.getDisplayName()).isEqualTo("削除");
    }
  }

  @Nested
  @DisplayName("コードによる変換")
  class CodeConversion {

    @Test
    @DisplayName("正しいコードから対応するステータスを取得")
    void shouldReturnCorrectStatus_WhenValidCode() {
      // Assert
      assertThat(TodoStatus.fromCode("TODO")).isEqualTo(TodoStatus.TODO);
      assertThat(TodoStatus.fromCode("IN_PROGRESS")).isEqualTo(TodoStatus.IN_PROGRESS);
      assertThat(TodoStatus.fromCode("COMPLETED")).isEqualTo(TodoStatus.COMPLETED);
      assertThat(TodoStatus.fromCode("DELETED")).isEqualTo(TodoStatus.DELETED);
    }

    @Test
    @DisplayName("小文字のコードも正しく変換される")
    void shouldReturnCorrectStatus_WhenLowercaseCode() {
      // Assert
      assertThat(TodoStatus.fromCode("todo")).isEqualTo(TodoStatus.TODO);
      assertThat(TodoStatus.fromCode("in_progress")).isEqualTo(TodoStatus.IN_PROGRESS);
      assertThat(TodoStatus.fromCode("completed")).isEqualTo(TodoStatus.COMPLETED);
      assertThat(TodoStatus.fromCode("deleted")).isEqualTo(TodoStatus.DELETED);
    }

    @Test
    @DisplayName("前後の空白があるコードも正しく変換される")
    void shouldReturnCorrectStatus_WhenCodeWithWhitespace() {
      // Assert
      assertThat(TodoStatus.fromCode("  TODO  ")).isEqualTo(TodoStatus.TODO);
      assertThat(TodoStatus.fromCode("\tIN_PROGRESS\n")).isEqualTo(TodoStatus.IN_PROGRESS);
    }

    @ParameterizedTest
    @ValueSource(strings = {"UNKNOWN", "INVALID", "", "NULL"})
    @DisplayName("無効なコードでは例外が発生")
    void shouldThrowException_WhenInvalidCode(String invalidCode) {
      // Assert
      assertThatThrownBy(() -> TodoStatus.fromCode(invalidCode))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Unknown status code: " + invalidCode);
    }

    @Test
    @DisplayName("nullコードでは例外が発生")
    void shouldThrowException_WhenNullCode() {
      // Assert
      assertThatThrownBy(() -> TodoStatus.fromCode(null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Code must not be null");
    }
  }

  @Nested
  @DisplayName("名前による変換")
  class NameConversion {

    @Test
    @DisplayName("正しい名前から対応するステータスを取得")
    void shouldReturnCorrectStatus_WhenValidName() {
      // Assert
      assertThat(TodoStatus.fromName("TODO")).isEqualTo(TodoStatus.TODO);
      assertThat(TodoStatus.fromName("IN_PROGRESS")).isEqualTo(TodoStatus.IN_PROGRESS);
      assertThat(TodoStatus.fromName("COMPLETED")).isEqualTo(TodoStatus.COMPLETED);
      assertThat(TodoStatus.fromName("DELETED")).isEqualTo(TodoStatus.DELETED);
    }

    @Test
    @DisplayName("小文字の名前も正しく変換される")
    void shouldReturnCorrectStatus_WhenLowercaseName() {
      // Assert
      assertThat(TodoStatus.fromName("todo")).isEqualTo(TodoStatus.TODO);
      assertThat(TodoStatus.fromName("in_progress")).isEqualTo(TodoStatus.IN_PROGRESS);
      assertThat(TodoStatus.fromName("completed")).isEqualTo(TodoStatus.COMPLETED);
      assertThat(TodoStatus.fromName("deleted")).isEqualTo(TodoStatus.DELETED);
    }

    @Test
    @DisplayName("前後の空白がある名前も正しく変換される")
    void shouldReturnCorrectStatus_WhenNameWithWhitespace() {
      // Assert
      assertThat(TodoStatus.fromName("  TODO  ")).isEqualTo(TodoStatus.TODO);
      assertThat(TodoStatus.fromName("\tCOMPLETED\n")).isEqualTo(TodoStatus.COMPLETED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"UNKNOWN", "INVALID", "", "NOT_A_STATUS"})
    @DisplayName("無効な名前では例外が発生")
    void shouldThrowException_WhenInvalidName(String invalidName) {
      // Assert
      assertThatThrownBy(() -> TodoStatus.fromName(invalidName))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Unknown status name: " + invalidName);
    }

    @Test
    @DisplayName("nullの名前では例外が発生")
    void shouldThrowException_WhenNullName() {
      // Assert
      assertThatThrownBy(() -> TodoStatus.fromName(null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Name must not be null");
    }
  }

  @Nested
  @DisplayName("コード一致判定")
  class CodeMatching {

    @Test
    @DisplayName("指定されたコードと一致するかどうかを正しく判定")
    void shouldReturnTrue_WhenCodeMatches() {
      // Assert
      assertThat(TodoStatus.TODO.hasCode("TODO")).isTrue();
      assertThat(TodoStatus.IN_PROGRESS.hasCode("IN_PROGRESS")).isTrue();
      assertThat(TodoStatus.COMPLETED.hasCode("COMPLETED")).isTrue();
      assertThat(TodoStatus.DELETED.hasCode("DELETED")).isTrue();
    }

    @Test
    @DisplayName("大文字小文字を区別せずに一致判定")
    void shouldReturnTrue_WhenCodeMatchesCaseInsensitive() {
      // Assert
      assertThat(TodoStatus.TODO.hasCode("todo")).isTrue();
      assertThat(TodoStatus.IN_PROGRESS.hasCode("in_progress")).isTrue();
      assertThat(TodoStatus.COMPLETED.hasCode("completed")).isTrue();
      assertThat(TodoStatus.DELETED.hasCode("deleted")).isTrue();
    }

    @Test
    @DisplayName("前後の空白を無視して一致判定")
    void shouldReturnTrue_WhenCodeMatchesWithWhitespace() {
      // Assert
      assertThat(TodoStatus.TODO.hasCode("  TODO  ")).isTrue();
      assertThat(TodoStatus.IN_PROGRESS.hasCode("\tIN_PROGRESS\n")).isTrue();
    }

    @Test
    @DisplayName("異なるコードでは一致しない")
    void shouldReturnFalse_WhenCodeDoesNotMatch() {
      // Assert
      assertThat(TodoStatus.TODO.hasCode("COMPLETED")).isFalse();
      assertThat(TodoStatus.IN_PROGRESS.hasCode("TODO")).isFalse();
      assertThat(TodoStatus.COMPLETED.hasCode("DELETED")).isFalse();
      assertThat(TodoStatus.DELETED.hasCode("IN_PROGRESS")).isFalse();
    }

    @Test
    @DisplayName("無効なコードでは一致しない")
    void shouldReturnFalse_WhenInvalidCode() {
      // Assert
      assertThat(TodoStatus.TODO.hasCode("INVALID")).isFalse();
      assertThat(TodoStatus.TODO.hasCode("")).isFalse();
    }

    @Test
    @DisplayName("nullコードでは例外が発生")
    void shouldThrowException_WhenNullCodeInHasCode() {
      // Assert
      assertThatThrownBy(() -> TodoStatus.TODO.hasCode(null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("Code must not be null");
    }
  }

  @Nested
  @DisplayName("Enumの基本的な性質")
  class EnumBasicProperties {

    @Test
    @DisplayName("Enumの順序が保持される")
    void shouldMaintainOrder() {
      // Arrange
      TodoStatus[] statuses = TodoStatus.values();

      // Assert
      assertThat(statuses[0]).isEqualTo(TodoStatus.TODO);
      assertThat(statuses[1]).isEqualTo(TodoStatus.IN_PROGRESS);
      assertThat(statuses[2]).isEqualTo(TodoStatus.COMPLETED);
      assertThat(statuses[3]).isEqualTo(TodoStatus.DELETED);
    }

    @Test
    @DisplayName("valueOf メソッドが正しく動作")
    void shouldWorkWithValueOf() {
      // Assert
      assertThat(TodoStatus.valueOf("TODO")).isEqualTo(TodoStatus.TODO);
      assertThat(TodoStatus.valueOf("IN_PROGRESS")).isEqualTo(TodoStatus.IN_PROGRESS);
      assertThat(TodoStatus.valueOf("COMPLETED")).isEqualTo(TodoStatus.COMPLETED);
      assertThat(TodoStatus.valueOf("DELETED")).isEqualTo(TodoStatus.DELETED);
    }

    @Test
    @DisplayName("name メソッドが正しく動作")
    void shouldReturnCorrectName() {
      // Assert
      assertThat(TodoStatus.TODO.name()).isEqualTo("TODO");
      assertThat(TodoStatus.IN_PROGRESS.name()).isEqualTo("IN_PROGRESS");
      assertThat(TodoStatus.COMPLETED.name()).isEqualTo("COMPLETED");
      assertThat(TodoStatus.DELETED.name()).isEqualTo("DELETED");
    }

    @Test
    @DisplayName("ordinal メソッドが正しく動作")
    void shouldReturnCorrectOrdinal() {
      // Assert
      assertThat(TodoStatus.TODO.ordinal()).isEqualTo(0);
      assertThat(TodoStatus.IN_PROGRESS.ordinal()).isEqualTo(1);
      assertThat(TodoStatus.COMPLETED.ordinal()).isEqualTo(2);
      assertThat(TodoStatus.DELETED.ordinal()).isEqualTo(3);
    }
  }

  @Nested
  @DisplayName("比較と等価性")
  class ComparisonAndEquality {

    @Test
    @DisplayName("同じステータスは等価")
    void shouldBeEqual_WhenSameStatus() {
      // Assert
      assertThat(TodoStatus.TODO).isEqualTo(TodoStatus.TODO);
      assertThat(TodoStatus.TODO.hashCode()).isEqualTo(TodoStatus.TODO.hashCode());
    }

    @Test
    @DisplayName("異なるステータスは非等価")
    void shouldNotBeEqual_WhenDifferentStatus() {
      // Assert
      assertThat(TodoStatus.TODO).isNotEqualTo(TodoStatus.COMPLETED);
      assertThat(TodoStatus.IN_PROGRESS).isNotEqualTo(TodoStatus.DELETED);
    }

    @Test
    @DisplayName("Enumの比較が正しく動作")
    void shouldCompareCorrectly() {
      // Assert - ordinal の順序で比較される
      assertThat(TodoStatus.TODO.compareTo(TodoStatus.IN_PROGRESS)).isLessThan(0);
      assertThat(TodoStatus.COMPLETED.compareTo(TodoStatus.TODO)).isGreaterThan(0);
      assertThat(TodoStatus.DELETED.compareTo(TodoStatus.DELETED)).isEqualTo(0);
    }
  }

  @Nested
  @DisplayName("toString とログ出力")
  class ToStringAndLogging {

    @ParameterizedTest
    @EnumSource(TodoStatus.class)
    @DisplayName("toString が適切に動作")
    void shouldProvideReadableToString(TodoStatus status) {
      // Act
      String toString = status.toString();

      // Assert
      assertThat(toString).isNotNull().isNotBlank();
      assertThat(toString).isEqualTo(status.name()); // Enumのデフォルト動作
    }

    @Test
    @DisplayName("ログ出力に適した形式")
    void shouldBeLoggingFriendly() {
      // Act & Assert - ログでの可読性を確認
      assertThat(TodoStatus.TODO.toString()).isEqualTo("TODO");
      assertThat(TodoStatus.IN_PROGRESS.toString()).isEqualTo("IN_PROGRESS");

      // Display name を使ったログフレンドリーな出力も可能
      assertThat(TodoStatus.TODO.getDisplayName()).isEqualTo("未完了");
      assertThat(TodoStatus.IN_PROGRESS.getDisplayName()).isEqualTo("進行中");
    }
  }

  @Nested
  @DisplayName("実用性とビジネス要件")
  class PracticalityAndBusinessRequirements {

    @Test
    @DisplayName("実際のビジネスシナリオでの使用")
    void shouldWorkInBusinessScenarios() {
      // Arrange - 実際のビジネスロジックを模倣
      String[] statusCodes = {"TODO", "IN_PROGRESS", "COMPLETED"};

      // Act & Assert
      for (String code : statusCodes) {
        TodoStatus status = TodoStatus.fromCode(code);
        assertThat(status.getDisplayName()).isNotBlank();
        assertThat(status.hasCode(code)).isTrue();
      }
    }

    @Test
    @DisplayName("APIとの互換性")
    void shouldBeApiCompatible() {
      // Arrange - API レスポンスでの使用を想定
      TodoStatus status = TodoStatus.IN_PROGRESS;

      // Act & Assert - JSON シリアライゼーション等で使用される値
      assertThat(status.getCode()).isEqualTo("IN_PROGRESS");
      assertThat(status.getDisplayName()).isEqualTo("進行中");
      assertThat(status.name()).isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("データベース格納値との互換性")
    void shouldBeDatabaseCompatible() {
      // Arrange - データベース格納を想定
      for (TodoStatus status : TodoStatus.values()) {
        // Act & Assert - データベースに格納する値として適切
        assertThat(status.getCode()).matches("^[A-Z_]+$"); // 大文字とアンダースコアのみ
        assertThat(status.getCode().length()).isLessThanOrEqualTo(20); // 適切な長さ
      }
    }
  }
}
