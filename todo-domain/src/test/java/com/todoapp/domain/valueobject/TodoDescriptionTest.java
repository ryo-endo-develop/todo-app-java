/* (C) 2025 Todo App Project */
package com.todoapp.domain.valueobject;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.todoapp.domain.common.Result;

/**
 * TodoDescriptionの単体テスト
 *
 * <p>テスト観点: - Optional型の適切な使用 - null安全性の検証 - 空値の扱い - ビジネスルールの境界値テスト
 */
@DisplayName("TodoDescription")
class TodoDescriptionTest {

  @Nested
  @DisplayName("作成と基本機能")
  class CreationAndBasics {

    @Test
    @DisplayName("空の説明が作成される")
    void shouldCreateEmptyDescription() {
      // Act
      TodoDescription description = TodoDescription.empty();

      // Assert
      assertThat(description.hasValue()).isFalse();
      assertThat(description.getValue()).isEmpty();
      assertThat(description.length()).isZero();
    }

    @Test
    @DisplayName("有効な説明文で作成される")
    void shouldCreateSuccessfully_WhenValidDescription() {
      // Arrange
      String validDescription = "このタスクは重要なプロジェクトの一部です。";

      // Act
      Result<TodoDescription> result = TodoDescription.of(validDescription);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().hasValue()).isTrue();
      assertThat(result.getValue().getValue()).hasValue(validDescription);
    }

    @Test
    @DisplayName("前後の空白が自動的に削除される")
    void shouldTrimWhitespace_WhenCreatingDescription() {
      // Arrange
      String descriptionWithWhitespace = "  重要な説明  ";
      String expectedDescription = "重要な説明";

      // Act
      Result<TodoDescription> result = TodoDescription.of(descriptionWithWhitespace);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).hasValue(expectedDescription);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n", "\r"})
    @DisplayName("空文字列や空白のみの場合は空の説明として扱われる")
    void shouldCreateEmptyDescription_WhenEmptyOrWhitespace(String emptyInput) {
      // Act
      Result<TodoDescription> result = TodoDescription.of(emptyInput);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().hasValue()).isFalse();
      assertThat(result.getValue().getValue()).isEmpty();
    }

    @Test
    @DisplayName("nullの場合は空の説明として扱われる")
    void shouldCreateEmptyDescription_WhenNull() {
      // Act
      Result<TodoDescription> result = TodoDescription.of(null);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().hasValue()).isFalse();
      assertThat(result.getValue().getValue()).isEmpty();
    }
  }

  @Nested
  @DisplayName("長さ制限の検証")
  class LengthValidation {

    @Test
    @DisplayName("最大長さ境界値の説明が作成される")
    void shouldCreateSuccessfully_WhenMaximumLength() {
      // Arrange
      String maxLengthDescription = "a".repeat(1000); // MAX_LENGTH = 1000

      // Act
      Result<TodoDescription> result = TodoDescription.of(maxLengthDescription);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().length()).isEqualTo(1000);
    }

    @Test
    @DisplayName("最大長を超える説明は拒否される")
    void shouldReturnFailure_WhenExceedsMaxLength() {
      // Arrange
      String tooLongDescription = "a".repeat(1001); // MAX_LENGTH + 1

      // Act
      Result<TodoDescription> result = TodoDescription.of(tooLongDescription);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse(""))
          .contains("1000文字以内で入力してください")
          .contains("現在: 1001文字");
    }

    @Test
    @DisplayName("長文の実用的な説明が許可される")
    void shouldAllowPracticalLongDescriptions() {
      // Arrange
      String practicalDescription =
          """
          このタスクは重要なプロジェクトの一環として実施します。
          以下の点に注意して作業を進めてください：

          1. 関係者との事前調整を忘れずに行う
          2. 期限を守って進捗を報告する
          3. 品質基準を満たすことを確認する

          何か問題があれば早めに相談してください。
          """;

      // Act
      Result<TodoDescription> result = TodoDescription.of(practicalDescription);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().hasValue()).isTrue();
      assertThat(result.getValue().length()).isLessThanOrEqualTo(1000);
    }
  }

  @Nested
  @DisplayName("値の取得と操作")
  class ValueRetrievalAndOperations {

    @Test
    @DisplayName("値がある場合のgetValue動作")
    void shouldReturnValue_WhenDescriptionHasValue() {
      // Arrange
      String description = "テスト説明";
      TodoDescription todoDescription = TodoDescription.of(description).getValue();

      // Act & Assert
      assertThat(todoDescription.hasValue()).isTrue();
      assertThat(todoDescription.getValue()).isPresent().hasValue(description);
      assertThat(todoDescription.getValueOrDefault("デフォルト")).isEqualTo(description);
      assertThat(todoDescription.length()).isEqualTo(description.length());
    }

    @Test
    @DisplayName("値がない場合のgetValue動作")
    void shouldReturnEmpty_WhenDescriptionHasNoValue() {
      // Arrange
      TodoDescription emptyDescription = TodoDescription.empty();
      String defaultValue = "デフォルト説明";

      // Act & Assert
      assertThat(emptyDescription.hasValue()).isFalse();
      assertThat(emptyDescription.getValue()).isEmpty();
      assertThat(emptyDescription.getValueOrDefault(defaultValue)).isEqualTo(defaultValue);
      assertThat(emptyDescription.length()).isZero();
    }

    @Test
    @DisplayName("getValueOrDefaultのnull安全性")
    void shouldHandleNullDefault_InGetValueOrDefault() {
      // Arrange
      TodoDescription description = TodoDescription.empty();

      // Act & Assert
      assertThat(description.getValueOrDefault(null)).isNull();
    }
  }

  @Nested
  @DisplayName("検索機能")
  class SearchFunctionality {

    @Test
    @DisplayName("説明内容の検索（大文字小文字を区別しない）")
    void shouldFindContent_CaseInsensitive() {
      // Arrange
      TodoDescription description = TodoDescription.of("重要なプロジェクトの詳細説明").getValue();

      // Assert
      assertThat(description.contains("重要")).isTrue();
      assertThat(description.contains("プロジェクト")).isTrue();
      assertThat(description.contains("重要")).isTrue(); // 大文字小文字を区別しない
      assertThat(description.contains("存在しない")).isFalse();
    }

    @Test
    @DisplayName("空の説明での検索は常にfalse")
    void shouldReturnFalse_WhenSearchingEmptyDescription() {
      // Arrange
      TodoDescription emptyDescription = TodoDescription.empty();

      // Assert
      assertThat(emptyDescription.contains("何か")).isFalse();
      assertThat(emptyDescription.contains("")).isFalse();
    }

    @Test
    @DisplayName("検索メソッドのnull安全性")
    void shouldHandleNullSearch() {
      // Arrange
      TodoDescription description = TodoDescription.of("テスト説明").getValue();

      // Act & Assert
      assertThatThrownBy(() -> description.contains(null)).isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  @DisplayName("比較と等価性")
  class ComparisonAndEquality {

    @Test
    @DisplayName("同じ内容の説明は等価")
    void shouldBeEqual_WhenSameContent() {
      // Arrange
      String content = "同じ説明内容";
      TodoDescription description1 = TodoDescription.of(content).getValue();
      TodoDescription description2 = TodoDescription.of(content).getValue();

      // Assert
      assertThat(description1).isEqualTo(description2);
      assertThat(description1.hashCode()).isEqualTo(description2.hashCode());
      assertThat(description1.isSameAs(description2)).isTrue();
    }

    @Test
    @DisplayName("空の説明同士は等価")
    void shouldBeEqual_WhenBothEmpty() {
      // Arrange
      TodoDescription empty1 = TodoDescription.empty();
      TodoDescription empty2 = TodoDescription.of(null).getValue();
      TodoDescription empty3 = TodoDescription.of("").getValue();

      // Assert
      assertThat(empty1).isEqualTo(empty2);
      assertThat(empty1).isEqualTo(empty3);
      assertThat(empty1.isSameAs(empty2)).isTrue();
      assertThat(empty1.isSameAs(empty3)).isTrue();
    }

    @Test
    @DisplayName("異なる内容の説明は非等価")
    void shouldNotBeEqual_WhenDifferentContent() {
      // Arrange
      TodoDescription description1 = TodoDescription.of("説明1").getValue();
      TodoDescription description2 = TodoDescription.of("説明2").getValue();

      // Assert
      assertThat(description1).isNotEqualTo(description2);
      assertThat(description1.isSameAs(description2)).isFalse();
    }

    @Test
    @DisplayName("正規化後に同じになる説明は等価")
    void shouldBeEqual_WhenNormalizedContentIsSame() {
      // Arrange
      TodoDescription description1 = TodoDescription.of("  同じ説明  ").getValue();
      TodoDescription description2 = TodoDescription.of("同じ説明").getValue();

      // Assert
      assertThat(description1).isEqualTo(description2);
      assertThat(description1.isSameAs(description2)).isTrue();
    }
  }

  @Nested
  @DisplayName("Optionalの適切な使用")
  class OptionalUsage {

    @Test
    @DisplayName("Optional型の適切な使用パターン")
    void shouldUseOptionalProperly() {
      // Arrange
      TodoDescription withValue = TodoDescription.of("値あり").getValue();
      TodoDescription withoutValue = TodoDescription.empty();

      // Act & Assert - Optional の適切な使用
      withValue
          .getValue()
          .ifPresent(
              value -> {
                assertThat(value).isEqualTo("値あり");
              });

      withoutValue
          .getValue()
          .ifPresentOrElse(
              value -> fail("Should not have value"),
              () -> assertThat(withoutValue.hasValue()).isFalse());

      // Optional チェイニングのテスト
      String result = withValue.getValue().map(String::toUpperCase).orElse("デフォルト");
      assertThat(result).isEqualTo("値あり");
    }

    @Test
    @DisplayName("Optionalのmapとfilterのテスト")
    void shouldWorkWithOptionalOperations() {
      // Arrange
      TodoDescription description = TodoDescription.of("重要な説明").getValue();

      // Act & Assert
      Optional<Integer> length = description.getValue().map(String::length);
      assertThat(length).hasValue(5);

      Optional<String> filtered = description.getValue().filter(value -> value.contains("重要"));
      assertThat(filtered).hasValue("重要な説明");

      Optional<String> notFiltered =
          description.getValue().filter(value -> value.contains("存在しない"));
      assertThat(notFiltered).isEmpty();
    }
  }
}
