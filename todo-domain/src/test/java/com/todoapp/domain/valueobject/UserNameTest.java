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
 * UserNameの単体テスト
 *
 * <p>テスト観点: - ユーザー名のバリデーションルール - 国際化対応（日本語文字） - セキュリティ要件（不正文字の排除） - ビジネスルールの境界値テスト
 */
@DisplayName("UserName")
class UserNameTest {

  @Nested
  @DisplayName("正常な作成パターン")
  class ValidCreation {

    @ParameterizedTest
    @ValueSource(
        strings = {"user123", "test_user", "admin-account", "田中太郎", "yamada123", "user_123"})
    @DisplayName("有効な文字で構成されたユーザー名が作成される")
    void shouldCreateSuccessfully_WhenValidUserName(String validName) {
      // Act
      Result<UserName> result = UserName.of(validName);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(validName);
    }

    @Test
    @DisplayName("日本語文字が含まれるユーザー名が作成される")
    void shouldCreateSuccessfully_WhenJapaneseCharacters() {
      // Arrange
      String[] japaneseNames = {"田中太郎", "やまだ花子", "サトウケンジ", "user田中", "山田123", "テストユーザー"};

      // Act & Assert
      for (String name : japaneseNames) {
        Result<UserName> result = UserName.of(name);
        assertThat(result.isSuccess()).as("Japanese name '%s' should be valid", name).isTrue();
      }
    }

    @Test
    @DisplayName("最小長さ境界値のユーザー名が作成される")
    void shouldCreateSuccessfully_WhenMinimumLength() {
      // Arrange
      String minLengthName = "abc"; // 3文字

      // Act
      Result<UserName> result = UserName.of(minLengthName);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().length()).isEqualTo(3);
    }

    @Test
    @DisplayName("最大長さ境界値のユーザー名が作成される")
    void shouldCreateSuccessfully_WhenMaximumLength() {
      // Arrange
      String maxLengthName = "a".repeat(100); // 100文字

      // Act
      Result<UserName> result = UserName.of(maxLengthName);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().length()).isEqualTo(100);
    }

    @Test
    @DisplayName("前後の空白が自動的に削除される")
    void shouldTrimWhitespace_WhenCreating() {
      // Arrange
      String nameWithWhitespace = "  validuser  ";
      String expectedName = "validuser";

      // Act
      Result<UserName> result = UserName.of(nameWithWhitespace);

      // Assert
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue().getValue()).isEqualTo(expectedName);
    }
  }

  @Nested
  @DisplayName("バリデーション失敗パターン")
  class ValidationFailures {

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("空またはスペースのみのユーザー名は拒否される")
    void shouldReturnFailure_WhenEmptyOrWhitespace(String emptyName) {
      // Act
      Result<UserName> result = UserName.of(emptyName);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage()).hasValue("ユーザー名は空にできません");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "a", "12"})
    @DisplayName("最小長さ未満のユーザー名は拒否される")
    void shouldReturnFailure_WhenTooShort(String shortName) {
      // Act
      Result<UserName> result = UserName.of(shortName);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse("")).contains("3文字以上で入力してください");
    }

    @Test
    @DisplayName("最大長さを超えるユーザー名は拒否される")
    void shouldReturnFailure_WhenTooLong() {
      // Arrange
      String tooLongName = "a".repeat(101); // 101文字

      // Act
      Result<UserName> result = UserName.of(tooLongName);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage().orElse(""))
          .contains("100文字以内で入力してください")
          .contains("現在: 101文字");
    }

    @ParameterizedTest
    @ValueSource(
        strings = {"user@domain", "user.name", "user name", "user#123", "user$", "user%", "user!"})
    @DisplayName("使用禁止文字を含むユーザー名は拒否される")
    void shouldReturnFailure_WhenInvalidCharacters(String invalidName) {
      // Act
      Result<UserName> result = UserName.of(invalidName);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage()).hasValue("ユーザー名には英数字、アンダースコア、ハイフン、日本語のみ使用できます");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-user", "_user", "-admin", "_test"})
    @DisplayName("ハイフンやアンダースコアで始まるユーザー名は拒否される")
    void shouldReturnFailure_WhenStartsWithSpecialCharacter(String invalidName) {
      // Act
      Result<UserName> result = UserName.of(invalidName);

      // Assert
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrorMessage()).hasValue("ユーザー名はハイフンやアンダースコアで始めることはできません");
    }
  }

  @Nested
  @DisplayName("検索機能")
  class SearchFunctionality {

    private final UserName userName = UserName.of("TestUser123").getValue();

    @Test
    @DisplayName("部分文字列検索（大文字小文字を区別しない）")
    void shouldFindSubstring_CaseInsensitive() {
      // Assert
      assertThat(userName.contains("test")).isTrue();
      assertThat(userName.contains("Test")).isTrue();
      assertThat(userName.contains("USER")).isTrue();
      assertThat(userName.contains("user")).isTrue();
      assertThat(userName.contains("123")).isTrue();
      assertThat(userName.contains("notfound")).isFalse();
    }

    @Test
    @DisplayName("日本語ユーザー名での検索")
    void shouldSearchJapaneseUserName() {
      // Arrange
      UserName japaneseUserName = UserName.of("田中太郎123").getValue();

      // Assert
      assertThat(japaneseUserName.contains("田中")).isTrue();
      assertThat(japaneseUserName.contains("太郎")).isTrue();
      assertThat(japaneseUserName.contains("123")).isTrue();
      assertThat(japaneseUserName.contains("佐藤")).isFalse();
    }

    @Test
    @DisplayName("検索メソッドのnull安全性")
    void shouldThrowException_WhenSearchTextIsNull() {
      // Assert
      assertThatThrownBy(() -> userName.contains(null)).isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  @DisplayName("比較と等価性")
  class ComparisonAndEquality {

    @Test
    @DisplayName("同じ文字列から作成されたUserNameは等価")
    void shouldBeEqual_WhenSameUserName() {
      // Arrange
      String name = "testuser";
      UserName userName1 = UserName.of(name).getValue();
      UserName userName2 = UserName.of(name).getValue();

      // Assert
      assertThat(userName1).isEqualTo(userName2);
      assertThat(userName1.hashCode()).isEqualTo(userName2.hashCode());
      assertThat(userName1.isSameAs(userName2)).isTrue();
    }

    @Test
    @DisplayName("異なる文字列から作成されたUserNameは非等価")
    void shouldNotBeEqual_WhenDifferentUserNames() {
      // Arrange
      UserName userName1 = UserName.of("user1").getValue();
      UserName userName2 = UserName.of("user2").getValue();

      // Assert
      assertThat(userName1).isNotEqualTo(userName2);
      assertThat(userName1.isSameAs(userName2)).isFalse();
    }

    @Test
    @DisplayName("正規化後に同じになるユーザー名は等価")
    void shouldBeEqual_WhenNormalizedNamesAreSame() {
      // Arrange
      UserName userName1 = UserName.of("  testuser  ").getValue();
      UserName userName2 = UserName.of("testuser").getValue();

      // Assert
      assertThat(userName1).isEqualTo(userName2);
      assertThat(userName1.isSameAs(userName2)).isTrue();
    }

    @Test
    @DisplayName("大文字小文字が異なるユーザー名は非等価")
    void shouldNotBeEqual_WhenDifferentCase() {
      // Arrange
      UserName userName1 = UserName.of("TestUser").getValue();
      UserName userName2 = UserName.of("testuser").getValue();

      // Assert
      assertThat(userName1).isNotEqualTo(userName2);
      assertThat(userName1.isSameAs(userName2)).isFalse();
    }
  }

  @Nested
  @DisplayName("セキュリティとビジネスルール")
  class SecurityAndBusinessRules {

    @Test
    @DisplayName("SQLインジェクション攻撃パターンが拒否される")
    void shouldRejectSqlInjectionPatterns() {
      // Arrange
      String[] sqlInjectionPatterns = {
        "admin'; DROP TABLE users; --",
        "user' OR '1'='1",
        "admin'/**/OR/**/'1'='1",
        "user'; DELETE * FROM todos; --"
      };

      // Act & Assert
      for (String pattern : sqlInjectionPatterns) {
        Result<UserName> result = UserName.of(pattern);
        assertThat(result.isFailure())
            .as("SQL injection pattern '%s' should be rejected", pattern)
            .isTrue();
      }
    }

    @Test
    @DisplayName("XSS攻撃パターンが拒否される")
    void shouldRejectXssPatterns() {
      // Arrange
      String[] xssPatterns = {
        "<script>alert('xss')</script>",
        "javascript:alert('xss')",
        "<img src=x onerror=alert(1)>",
        "user<script>",
        "admin</script>"
      };

      // Act & Assert
      for (String pattern : xssPatterns) {
        Result<UserName> result = UserName.of(pattern);
        assertThat(result.isFailure()).as("XSS pattern '%s' should be rejected", pattern).isTrue();
      }
    }

    @Test
    @DisplayName("実用的なユーザー名パターンが許可される")
    void shouldAllowPracticalUserNamePatterns() {
      // Arrange
      String[] practicalNames = {
        "admin",
        "test_user",
        "user-123",
        "田中太郎",
        "yamada_hanako",
        "developer1",
        "project_manager",
        "システム管理者",
        "営業部田中"
      };

      // Act & Assert
      for (String name : practicalNames) {
        Result<UserName> result = UserName.of(name);
        assertThat(result.isSuccess()).as("Practical name '%s' should be valid", name).isTrue();
      }
    }

    @Test
    @DisplayName("予約語的なユーザー名も技術的には許可される")
    void shouldAllowReservedWordLikeNames() {
      // Arrange - ビジネスルールで禁止していない限り技術的には有効
      String[] reservedWordLike = {"admin", "root", "system", "user", "guest", "test"};

      // Act & Assert
      for (String name : reservedWordLike) {
        Result<UserName> result = UserName.of(name);
        assertThat(result.isSuccess())
            .as("Reserved-like name '%s' should be technically valid", name)
            .isTrue();
      }
    }
  }

  @Nested
  @DisplayName("国際化とエンコーディング")
  class InternationalizationAndEncoding {

    @Test
    @DisplayName("ひらがな、カタカナ、漢字の組み合わせが許可される")
    void shouldAllowJapaneseCharacterCombinations() {
      // Arrange
      String[] japaneseCombinations = {
        "ひらがな",
        "カタカナ",
        "漢字",
        "ひらがなカタカナ",
        "ひらがな漢字",
        "カタカナ漢字",
        "ひらがなカタカナ漢字",
        "田中ひろし",
        "サトウ花子",
        "やまだ太郎"
      };

      // Act & Assert
      for (String name : japaneseCombinations) {
        Result<UserName> result = UserName.of(name);
        assertThat(result.isSuccess())
            .as("Japanese combination '%s' should be valid", name)
            .isTrue();
      }
    }

    @Test
    @DisplayName("英数字と日本語の混在パターンが許可される")
    void shouldAllowMixedAlphanumericAndJapanese() {
      // Arrange
      String[] mixedPatterns = {"user田中", "田中123", "admin山田", "テスト123user", "開発者dev", "manager管理者"};

      // Act & Assert
      for (String name : mixedPatterns) {
        Result<UserName> result = UserName.of(name);
        assertThat(result.isSuccess()).as("Mixed pattern '%s' should be valid", name).isTrue();
      }
    }
  }
}
