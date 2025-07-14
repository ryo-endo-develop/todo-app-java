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
 * UserIdの単体テスト
 *
 * <p>テスト観点: - ID値の基本的な制約（正数） - 文字列変換の安全性 - 等価性の一貫性 - エラーハンドリングの適切性
 */
@DisplayName("UserId")
class UserIdTest {

  @Nested
  @DisplayName("long値からの作成")
  class CreationFromLong {

    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, 999999L, Long.MAX_VALUE})
    @DisplayName("正の値でUserIdが正常に作成される")
    void shouldCreateSuccessfully_WhenPositiveValue(long validId) {
      // Act
      Result<UserId> result = UserId.of(validId);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(validId);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L, Long.MIN_VALUE})
    @DisplayName("ゼロや負の値は無効として失敗")
    void shouldReturnFailure_WhenNonPositiveValue(long invalidId) {
      // Act
      Result<UserId> result = UserId.of(invalidId);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse(""))
          .contains("UserId value must be positive")
          .contains(String.valueOf(invalidId));
    }
  }

  @Nested
  @DisplayName("String値からの作成")
  class CreationFromString {

    @ParameterizedTest
    @ValueSource(strings = {"1", "123", "999999", "9223372036854775807"}) // Long.MAX_VALUE
    @DisplayName("有効な数値文字列でUserIdが作成される")
    void shouldCreateSuccessfully_WhenValidNumberString(String validIdString) {
      // Act
      Result<UserId> result = UserId.of(validIdString);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(Long.parseLong(validIdString));
    }

    @Test
    @DisplayName("前後の空白を含む数値文字列で正常に作成される")
    void shouldCreateSuccessfully_WhenNumberStringWithWhitespace() {
      // Arrange
      String idStringWithWhitespace = "  456  ";

      // Act
      Result<UserId> result = UserId.of(idStringWithWhitespace);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(456L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("空文字列や空白のみの文字列は失敗")
    void shouldReturnFailure_WhenEmptyOrWhitespace(String emptyString) {
      // Act
      Result<UserId> result = UserId.of(emptyString);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage()).hasValue("UserId string must not be empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "12.34", "12a", "a123", "0x123", "1e5"})
    @DisplayName("無効な数値文字列は失敗")
    void shouldReturnFailure_WhenInvalidNumberString(String invalidString) {
      // Act
      Result<UserId> result = UserId.of(invalidString);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse(""))
          .contains("UserId string must be a valid number")
          .contains(invalidString);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-123"})
    @DisplayName("ゼロや負数の文字列は失敗")
    void shouldReturnFailure_WhenNonPositiveString(String nonPositiveString) {
      // Act
      Result<UserId> result = UserId.of(nonPositiveString);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse("")).contains("UserId value must be positive");
    }

    @Test
    @DisplayName("Long.MAX_VALUE + 1の文字列は失敗")
    void shouldReturnFailure_WhenStringExceedsLongMaxValue() {
      // Arrange
      String tooLargeNumber = "9223372036854775808"; // Long.MAX_VALUE + 1

      // Act
      Result<UserId> result = UserId.of(tooLargeNumber);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse(""))
          .contains("UserId string must be a valid number");
    }
  }

  @Nested
  @DisplayName("比較操作")
  class ComparisonOperations {

    @Test
    @DisplayName("同じ値のUserIdは等価")
    void shouldBeEqual_WhenSameValues() {
      // Arrange
      UserId id1 = UserId.of(123L).getValue();
      UserId id2 = UserId.of(123L).getValue();

      // Assert
      assertThat(id1).isEqualTo(id2);
      assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
      assertThat(id1.isSameAs(id2)).isTrue();
    }

    @Test
    @DisplayName("異なる値のUserIdは非等価")
    void shouldNotBeEqual_WhenDifferentValues() {
      // Arrange
      UserId id1 = UserId.of(123L).getValue();
      UserId id2 = UserId.of(456L).getValue();

      // Assert
      assertThat(id1).isNotEqualTo(id2);
      assertThat(id1.isSameAs(id2)).isFalse();
    }

    @Test
    @DisplayName("文字列と数値から作成した同じ値のUserIdは等価")
    void shouldBeEqual_WhenCreatedFromStringAndLong() {
      // Arrange
      UserId fromLong = UserId.of(123L).getValue();
      UserId fromString = UserId.of("123").getValue();

      // Assert
      assertThat(fromLong).isEqualTo(fromString);
      assertThat(fromLong.isSameAs(fromString)).isTrue();
    }

    @Test
    @DisplayName("isSameAsメソッドのnull安全性")
    void shouldThrowException_WhenIsSameAsWithNull() {
      // Arrange
      UserId id = UserId.of(123L).getValue();

      // Act & Assert
      assertThatThrownBy(() -> id.isSameAs(null)).isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  @DisplayName("toString表現")
  class StringRepresentation {

    @Test
    @DisplayName("toString が適切な文字列表現を返す")
    void shouldReturnCorrectStringRepresentation() {
      // Arrange
      UserId id = UserId.of(123L).getValue();

      // Act
      String result = id.toString();

      // Assert
      assertThat(result).contains("UserId");
      assertThat(result).contains("123");
    }

    @Test
    @DisplayName("ログ出力に適した形式")
    void shouldBeLoggingFriendly() {
      // Arrange
      UserId[] ids = {
        UserId.of(1L).getValue(), UserId.of(999L).getValue(), UserId.of(Long.MAX_VALUE).getValue()
      };

      // Act & Assert
      for (UserId id : ids) {
        String toString = id.toString();
        assertThat(toString).isNotNull().isNotBlank();
        assertThat(toString).contains(String.valueOf(id.getValue()));
      }
    }
  }

  @Nested
  @DisplayName("境界値とエッジケース")
  class BoundaryValuesAndEdgeCases {

    @Test
    @DisplayName("最小値（1）で正常に作成される")
    void shouldCreateSuccessfully_WhenMinimumValue() {
      // Act
      Result<UserId> result = UserId.of(1L);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(1L);
    }

    @Test
    @DisplayName("最大値で正常に作成される")
    void shouldCreateSuccessfully_WhenMaximumValue() {
      // Act
      Result<UserId> result = UserId.of(Long.MAX_VALUE);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("実用的なID範囲での動作確認")
    void shouldWorkWithPracticalIdRanges() {
      // Arrange - 実際のアプリケーションで使用されそうなID範囲
      long[] practicalIds = {
        1L, // 最初のユーザー
        1000L, // 千人目のユーザー
        100000L, // 十万人目のユーザー
        1000000L, // 百万人目のユーザー
        999999999L // 実用的な大きな値
      };

      // Act & Assert
      for (long id : practicalIds) {
        Result<UserId> result = UserId.of(id);
        assertThat(result.isSuccess()).as("Practical ID %d should be valid", id).isTrue();
        assertThat(result.getValue().getValue()).isEqualTo(id);
      }
    }
  }

  @Nested
  @DisplayName("不変性とスレッドセーフティ")
  class ImmutabilityAndThreadSafety {

    @Test
    @DisplayName("UserIdは不変オブジェクト")
    void shouldBeImmutable() {
      // Arrange
      long originalValue = 123L;
      UserId id = UserId.of(originalValue).getValue();

      // Act - どのような操作をしても元のオブジェクトは変更されない
      id.getValue();
      id.toString();
      id.hashCode();

      // Assert
      assertThat(id.getValue()).isEqualTo(originalValue);
    }

    @Test
    @DisplayName("複数スレッドから安全にアクセスできる")
    void shouldBeThreadSafe() throws InterruptedException {
      // Arrange
      UserId id = UserId.of(12345L).getValue();
      int threadCount = 10;
      Thread[] threads = new Thread[threadCount];
      long[] results = new long[threadCount];

      // Act
      for (int i = 0; i < threadCount; i++) {
        final int index = i;
        threads[i] =
            new Thread(
                () -> {
                  results[index] = id.getValue();
                });
        threads[i].start();
      }

      for (Thread thread : threads) {
        thread.join();
      }

      // Assert
      for (long result : results) {
        assertThat(result).isEqualTo(12345L);
      }
    }
  }
}
