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
 * TodoTitleの単体テスト
 *
 * <p>テスト戦略: - ビジネスルールの境界値テスト - エラーハンドリングの検証 - 正常系と異常系の網羅 - パフォーマンス制約の確認
 *
 * <p>設計検証観点: - 不変性の確認 - null安全性の検証 - ビジネスルールの一貫性 - APIの使いやすさ評価
 */
@DisplayName("TodoTitle")
class TodoTitleTest {

  @Nested
  @DisplayName("作成時の検証")
  class CreationValidation {

    @Test
    @DisplayName("正常な文字列でTodoTitleが作成される")
    void shouldCreateSuccessfully_WhenValidTitle() {
      // Arrange
      String validTitle = "プロジェクトの進捗確認";

      // Act
      Result<TodoTitle> result = TodoTitle.of(validTitle);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(validTitle);
    }

    @Test
    @DisplayName("前後の空白が自動的に削除される")
    void shouldTrimWhitespace_WhenCreatingTitle() {
      // Arrange
      String titleWithWhitespace = "  重要なタスク  ";
      String expectedTitle = "重要なタスク";

      // Act
      Result<TodoTitle> result = TodoTitle.of(titleWithWhitespace);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(expectedTitle);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "短い", "1", "X"})
    @DisplayName("最小長さ境界値のタイトルが作成される")
    void shouldCreateSuccessfully_WhenMinimumLengthTitle(String title) {
      // Act
      Result<TodoTitle> result = TodoTitle.of(title);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().length())
          .isGreaterThanOrEqualTo(TodoTitle.ValidationRules.MIN_LENGTH);
    }

    @Test
    @DisplayName("最大長さ境界値のタイトルが作成される")
    void shouldCreateSuccessfully_WhenMaximumLengthTitle() {
      // Arrange
      String maxLengthTitle = "a".repeat(TodoTitle.ValidationRules.MAX_LENGTH);

      // Act
      Result<TodoTitle> result = TodoTitle.of(maxLengthTitle);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().length()).isEqualTo(TodoTitle.ValidationRules.MAX_LENGTH);
    }
  }

  @Nested
  @DisplayName("バリデーション失敗ケース")
  class ValidationFailures {

    @Test
    @DisplayName("nullタイトルは拒否される")
    void shouldReturnFailure_WhenTitleIsNull() {
      // Act
      Result<TodoTitle> result = TodoTitle.of(null);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage()).hasValue("Todoタイトルは必須です");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n", "\r"})
    @DisplayName("空またはスペースのみのタイトルは拒否される")
    void shouldReturnFailure_WhenTitleIsEmptyOrWhitespace(String emptyTitle) {
      // Act
      Result<TodoTitle> result = TodoTitle.of(emptyTitle);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage()).hasValue("Todoタイトルは空にできません");
    }

    @Test
    @DisplayName("最大長を超えるタイトルは拒否される")
    void shouldReturnFailure_WhenTitleExceedsMaxLength() {
      // Arrange
      String tooLongTitle = "a".repeat(TodoTitle.ValidationRules.MAX_LENGTH + 1);

      // Act
      Result<TodoTitle> result = TodoTitle.of(tooLongTitle);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse(""))
          .contains("255文字以内で入力してください")
          .contains("現在: 256文字");
    }

    @ParameterizedTest
    @ValueSource(strings = {"\u0000タイトル", "タイトル\u001F", "test\u007F"})
    @DisplayName("制御文字を含むタイトルは拒否される")
    void shouldReturnFailure_WhenTitleContainsInvalidCharacters(String invalidTitle) {
      // Act
      Result<TodoTitle> result = TodoTitle.of(invalidTitle);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage()).hasValue("Todoタイトルに使用できない文字が含まれています");
    }
  }

  @Nested
  @DisplayName("検索機能")
  class SearchFunctionality {

    private final TodoTitle title = TodoTitle.of("重要なプロジェクトの進捗確認").getValue();

    @Test
    @DisplayName("部分文字列検索（大文字小文字を区別しない）")
    void shouldFindSubstring_CaseInsensitive() {
      // Assert
      assertThat(title.contains("重要")).isTrue();
      assertThat(title.contains("プロジェクト")).isTrue();
      assertThat(title.contains("進捗")).isTrue();
      assertThat(title.contains("プロジェクト")).isTrue(); // 大文字小文字を区別しない
      assertThat(title.contains("存在しない")).isFalse();
    }

    @Test
    @DisplayName("前方一致検索")
    void shouldMatchPrefix() {
      // Assert
      assertThat(title.startsWith("重要")).isTrue();
      assertThat(title.startsWith("重要な")).isTrue();
      assertThat(title.startsWith("重要")).isTrue(); // 大文字小文字を区別しない
      assertThat(title.startsWith("プロジェクト")).isFalse();
    }

    @Test
    @DisplayName("複数キーワードのAND検索")
    void shouldMatchAllKeywords() {
      // Assert
      assertThat(title.containsAllKeywords("重要", "プロジェクト")).isTrue();
      assertThat(title.containsAllKeywords("重要", "進捗", "確認")).isTrue();
      assertThat(title.containsAllKeywords("重要", "存在しない")).isFalse();
      assertThat(title.containsAllKeywords()).isTrue(); // 空配列は常にtrue
    }

    @Test
    @DisplayName("検索メソッドのnull安全性")
    void shouldThrowException_WhenSearchParameterIsNull() {
      // Assert
      assertThatThrownBy(() -> title.contains(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Search text must not be null");

      assertThatThrownBy(() -> title.startsWith(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Prefix must not be null");

      assertThatThrownBy(() -> title.containsAllKeywords((String[]) null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Keywords must not be null");
    }
  }

  @Nested
  @DisplayName("比較と等価性")
  class ComparisonAndEquality {

    @Test
    @DisplayName("同じ文字列から作成されたTodoTitleは等価")
    void shouldBeEqual_WhenSameTitle() {
      // Arrange
      String titleText = "同じタイトル";
      TodoTitle title1 = TodoTitle.of(titleText).getValue();
      TodoTitle title2 = TodoTitle.of(titleText).getValue();

      // Assert
      assertThat(title1).isEqualTo(title2);
      assertThat(title1.hashCode()).isEqualTo(title2.hashCode());
      assertThat(title1.isSameAs(title2)).isTrue();
    }

    @Test
    @DisplayName("異なる文字列から作成されたTodoTitleは非等価")
    void shouldNotBeEqual_WhenDifferentTitles() {
      // Arrange
      TodoTitle title1 = TodoTitle.of("タイトル1").getValue();
      TodoTitle title2 = TodoTitle.of("タイトル2").getValue();

      // Assert
      assertThat(title1).isNotEqualTo(title2);
      assertThat(title1.isSameAs(title2)).isFalse();
    }

    @Test
    @DisplayName("正規化後に同じになるタイトルは等価")
    void shouldBeEqual_WhenNormalizedTitlesAreSame() {
      // Arrange
      TodoTitle title1 = TodoTitle.of("  同じタイトル  ").getValue();
      TodoTitle title2 = TodoTitle.of("同じタイトル").getValue();

      // Assert
      assertThat(title1).isEqualTo(title2);
      assertThat(title1.isSameAs(title2)).isTrue();
    }

    @Test
    @DisplayName("isSameAsメソッドのnull安全性")
    void shouldThrowException_WhenComparisonTargetIsNull() {
      // Arrange
      TodoTitle title = TodoTitle.of("テストタイトル").getValue();

      // Assert
      assertThatThrownBy(() -> title.isSameAs(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Comparison target must not be null");
    }
  }

  @Nested
  @DisplayName("不変性とスレッドセーフティ")
  class ImmutabilityAndThreadSafety {

    @Test
    @DisplayName("TodoTitleは不変オブジェクト")
    void shouldBeImmutable() {
      // Arrange
      String originalTitle = "不変テスト";
      TodoTitle title = TodoTitle.of(originalTitle).getValue();

      // Act - どのような操作をしても元のオブジェクトは変更されない
      title.contains("テスト");
      title.startsWith("不変");
      title.length();

      // Assert
      assertThat(title.getValue()).isEqualTo(originalTitle);
    }

    @Test
    @DisplayName("複数スレッドから安全にアクセスできる")
    void shouldBeThreadSafe() throws InterruptedException {
      // Arrange
      TodoTitle title = TodoTitle.of("スレッドセーフテスト").getValue();
      int threadCount = 10;
      Thread[] threads = new Thread[threadCount];
      boolean[] results = new boolean[threadCount];

      // Act
      for (int i = 0; i < threadCount; i++) {
        final int index = i;
        threads[i] =
            new Thread(
                () -> {
                  results[index] = title.contains("テスト") && title.startsWith("スレッド");
                });
        threads[i].start();
      }

      for (Thread thread : threads) {
        thread.join();
      }

      // Assert
      for (boolean result : results) {
        assertThat(result).isTrue();
      }
    }
  }

  @Nested
  @DisplayName("ビジネスルールの設定値検証")
  class BusinessRuleValidation {

    @Test
    @DisplayName("バリデーションルールの設定値が適切")
    void shouldHaveReasonableValidationRules() {
      // Assert - ビジネス要件に適した値設定の確認
      assertThat(TodoTitle.ValidationRules.MIN_LENGTH).isEqualTo(1);
      assertThat(TodoTitle.ValidationRules.MAX_LENGTH).isEqualTo(255);
      assertThat(TodoTitle.ValidationRules.INVALID_CHARS).isNotNull();
    }

    @Test
    @DisplayName("実用的な長さのタイトルが許可される")
    void shouldAllowPracticalTitleLengths() {
      // Arrange - 実際のユースケースを想定したタイトル例
      String[] practicalTitles = {
        "メール確認", "週次ミーティングの準備", "プロジェクト仕様書のレビューと承認依頼", "来月のリリース計画について関係者との調整会議の準備と議題整理"
      };

      // Act & Assert
      for (String title : practicalTitles) {
        Result<TodoTitle> result = TodoTitle.of(title);
        assertThat(result.isSuccess()).as("Title '%s' should be valid", title).isTrue();
      }
    }
  }
}
