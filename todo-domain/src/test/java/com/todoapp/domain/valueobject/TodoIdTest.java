/* (C) 2025 Todo App Project */
package com.todoapp.domain.valueobject;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.todoapp.domain.common.Result;

/**
 * TodoIdの単体テスト
 *
 * <p>テスト方針: - 正常値・境界値・異常値を網羅 - ファクトリメソッドの動作確認 - ValueObjectとしての振る舞い確認
 */
@DisplayName("TodoId")
class TodoIdTest {

  @Nested
  @DisplayName("long値からの作成")
  class CreationFromLong {

    @Test
    @DisplayName("正の値で正常に作成される")
    void shouldCreateSuccessfully_WhenPositiveValue() {
      // Arrange
      long validId = 123L;

      // Act
      Result<TodoId> result = TodoId.of(validId);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(validId);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, 999999L, Long.MAX_VALUE})
    @DisplayName("様々な正の値で正常に作成される")
    void shouldCreateSuccessfully_WhenVariousPositiveValues(long validId) {
      // Act
      Result<TodoId> result = TodoId.of(validId);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(validId);
    }

    @Test
    @DisplayName("ゼロは無効な値として失敗")
    void shouldReturnFailure_WhenValueIsZero() {
      // Act
      Result<TodoId> result = TodoId.of(0L);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse("")).contains("TodoId value must be at least 1");
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, -100L, Long.MIN_VALUE})
    @DisplayName("負の値は無効として失敗")
    void shouldReturnFailure_WhenNegativeValue(long invalidId) {
      // Act
      Result<TodoId> result = TodoId.of(invalidId);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse("")).contains("TodoId value must be at least 1");
    }
  }

  @Nested
  @DisplayName("String値からの作成")
  class CreationFromString {

    @Test
    @DisplayName("有効な数値文字列で正常に作成される")
    void shouldCreateSuccessfully_WhenValidNumberString() {
      // Arrange
      String validIdString = "123";

      // Act
      Result<TodoId> result = TodoId.of(validIdString);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(123L);
    }

    @Test
    @DisplayName("前後の空白を含む数値文字列で正常に作成される")
    void shouldCreateSuccessfully_WhenNumberStringWithWhitespace() {
      // Arrange
      String idStringWithWhitespace = "  456  ";

      // Act
      Result<TodoId> result = TodoId.of(idStringWithWhitespace);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(456L);
    }

    @Test
    @DisplayName("null文字列は失敗")
    void shouldReturnFailure_WhenStringIsNull() {
      // Act
      Result<TodoId> result = TodoId.of((String) null);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse("")).contains("TodoId string must not be null");
    }

    @Test
    @DisplayName("空文字列は失敗")
    void shouldReturnFailure_WhenStringIsEmpty() {
      // Act
      Result<TodoId> result = TodoId.of("");

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse("")).contains("TodoId string must not be empty");
    }

    @Test
    @DisplayName("空白のみの文字列は失敗")
    void shouldReturnFailure_WhenStringIsWhitespaceOnly() {
      // Act
      Result<TodoId> result = TodoId.of("   ");

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse("")).contains("TodoId string must not be empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "12.34", "12a", "a123", "123.0"})
    @DisplayName("無効な数値文字列は失敗")
    void shouldReturnFailure_WhenInvalidNumberString(String invalidIdString) {
      // Act
      Result<TodoId> result = TodoId.of(invalidIdString);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse(""))
          .contains("TodoId string must be a valid number");
    }

    @Test
    @DisplayName("ゼロの文字列は失敗")
    void shouldReturnFailure_WhenStringIsZero() {
      // Act
      Result<TodoId> result = TodoId.of("0");

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse("")).contains("TodoId value must be at least 1");
    }

    @Test
    @DisplayName("負数の文字列は失敗")
    void shouldReturnFailure_WhenStringIsNegative() {
      // Act
      Result<TodoId> result = TodoId.of("-123");

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse("")).contains("TodoId value must be at least 1");
    }
  }

  @Nested
  @DisplayName("比較操作")
  class ComparisonOperations {

    @Test
    @DisplayName("同じ値のTodoIdは等価")
    void shouldBeEqual_WhenSameValues() {
      // Arrange
      TodoId id1 = TodoId.of(123L).getValue();
      TodoId id2 = TodoId.of(123L).getValue();

      // Act & Assert
      assertThat(id1).isEqualTo(id2);
      assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    @DisplayName("異なる値のTodoIdは等価でない")
    void shouldNotBeEqual_WhenDifferentValues() {
      // Arrange
      TodoId id1 = TodoId.of(123L).getValue();
      TodoId id2 = TodoId.of(456L).getValue();

      // Act & Assert
      assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("isSameAs: 同じ値で真")
    void shouldReturnTrue_WhenIsSameAsWithSameValue() {
      // Arrange
      TodoId id1 = TodoId.of(123L).getValue();
      TodoId id2 = TodoId.of(123L).getValue();

      // Act
      boolean result = id1.isSameAs(id2);

      // Assert
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isSameAs: 異なる値で偽")
    void shouldReturnFalse_WhenIsSameAsWithDifferentValue() {
      // Arrange
      TodoId id1 = TodoId.of(123L).getValue();
      TodoId id2 = TodoId.of(456L).getValue();

      // Act
      boolean result = id1.isSameAs(id2);

      // Assert
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isSameAs: nullで例外")
    void shouldThrowException_WhenIsSameAsWithNull() {
      // Arrange
      TodoId id = TodoId.of(123L).getValue();

      // Act & Assert
      assertThatThrownBy(() -> id.isSameAs(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Comparison target must not be null");
    }

    @Test
    @DisplayName("isGreaterThan: より大きい値で真")
    void shouldReturnTrue_WhenIsGreaterThanWithSmallerValue() {
      // Arrange
      TodoId larger = TodoId.of(456L).getValue();
      TodoId smaller = TodoId.of(123L).getValue();

      // Act
      boolean result = larger.isGreaterThan(smaller);

      // Assert
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isGreaterThan: より小さい値で偽")
    void shouldReturnFalse_WhenIsGreaterThanWithLargerValue() {
      // Arrange
      TodoId smaller = TodoId.of(123L).getValue();
      TodoId larger = TodoId.of(456L).getValue();

      // Act
      boolean result = smaller.isGreaterThan(larger);

      // Assert
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isLessThan: より小さい値で真")
    void shouldReturnTrue_WhenIsLessThanWithLargerValue() {
      // Arrange
      TodoId smaller = TodoId.of(123L).getValue();
      TodoId larger = TodoId.of(456L).getValue();

      // Act
      boolean result = smaller.isLessThan(larger);

      // Assert
      assertThat(result).isTrue();
    }
  }

  @Nested
  @DisplayName("toString表現")
  class StringRepresentation {

    @Test
    @DisplayName("toString: 適切な文字列表現を返す")
    void shouldReturnCorrectStringRepresentation() {
      // Arrange
      TodoId id = TodoId.of(123L).getValue();

      // Act
      String result = id.toString();

      // Assert
      assertThat(result).contains("TodoId");
      assertThat(result).contains("123");
    }
  }

  @Nested
  @DisplayName("境界値テスト")
  class BoundaryValueTests {

    @Test
    @DisplayName("最小値(1)で正常に作成される")
    void shouldCreateSuccessfully_WhenMinimumValue() {
      // Act
      Result<TodoId> result = TodoId.of(1L);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(1L);
    }

    @Test
    @DisplayName("最大値で正常に作成される")
    void shouldCreateSuccessfully_WhenMaximumValue() {
      // Act
      Result<TodoId> result = TodoId.of(Long.MAX_VALUE);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("非常に大きな文字列数値でも正常に作成される")
    void shouldCreateSuccessfully_WhenVeryLargeStringNumber() {
      // Arrange
      String largeNumber = String.valueOf(Long.MAX_VALUE);

      // Act
      Result<TodoId> result = TodoId.of(largeNumber);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("Long.MAX_VALUE + 1の文字列は失敗")
    void shouldReturnFailure_WhenStringExceedsLongMaxValue() {
      // Arrange
      String tooLargeNumber = "9223372036854775808"; // Long.MAX_VALUE + 1

      // Act
      Result<TodoId> result = TodoId.of(tooLargeNumber);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse(""))
          .contains("TodoId string must be a valid number");
    }
  }
}
