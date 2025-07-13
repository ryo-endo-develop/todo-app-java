package com.todoapp.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Result型の単体テスト
 * 
 * テスト方針:
 * - AAAパターンで構造化
 * - 成功・失敗の両パターンを網羅
 * - エラーケースの境界値テスト
 * - 関数型メソッドの動作確認
 */
@DisplayName("Result")
class ResultTest {

    @Nested
    @DisplayName("成功結果の作成")
    class SuccessCreation {

        @Test
        @DisplayName("有効な値で成功結果を作成できる")
        void shouldCreateSuccessResult_WhenValidValueProvided() {
            // Arrange
            String value = "test value";

            // Act
            Result<String> result = Result.success(value);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.isFailure()).isFalse();
            assertThat(result.getValue()).isEqualTo(value);
        }

        @Test
        @DisplayName("成功結果の値をOptionalで取得できる")
        void shouldReturnValueAsOptional_WhenSuccessResult() {
            // Arrange
            String value = "test value";
            Result<String> result = Result.success(value);

            // Act
            Optional<String> optional = result.getValueOptional();

            // Assert
            assertThat(optional).isPresent();
            assertThat(optional.get()).isEqualTo(value);
        }

        @Test
        @DisplayName("成功結果でエラーメッセージは空のOptional")
        void shouldReturnEmptyOptional_WhenGetErrorMessageFromSuccess() {
            // Arrange
            Result<String> result = Result.success("test");

            // Act
            Optional<String> errorMessage = result.getErrorMessage();

            // Assert
            assertThat(errorMessage).isEmpty();
        }
    }

    @Nested
    @DisplayName("失敗結果の作成")
    class FailureCreation {

        @Test
        @DisplayName("エラーメッセージで失敗結果を作成できる")
        void shouldCreateFailureResult_WhenErrorMessageProvided() {
            // Arrange
            String errorMessage = "Error occurred";

            // Act
            Result<String> result = Result.failure(errorMessage);

            // Assert
            assertThat(result.isFailure()).isTrue();
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getErrorMessage()).contains(errorMessage);
        }

        @Test
        @DisplayName("失敗結果の値はOptionalで空")
        void shouldReturnEmptyOptional_WhenGetValueFromFailure() {
            // Arrange
            Result<String> result = Result.failure("error");

            // Act
            Optional<String> optional = result.getValueOptional();

            // Assert
            assertThat(optional).isEmpty();
        }

        @Test
        @DisplayName("失敗結果でgetValue()は例外をスロー")
        void shouldThrowException_WhenGetValueFromFailure() {
            // Arrange
            Result<String> result = Result.failure("error");

            // Act & Assert
            assertThatThrownBy(() -> result.getValue())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot get value from failure result");
        }
    }

    @Nested
    @DisplayName("バリデーション")
    class Validation {

        @Test
        @DisplayName("nullで成功結果を作成すると例外")
        void shouldThrowException_WhenCreateSuccessWithNull() {
            // Act & Assert
            assertThatThrownBy(() -> Result.success(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Success value must not be null");
        }

        @Test
        @DisplayName("nullエラーメッセージで失敗結果を作成すると例外")
        void shouldThrowException_WhenCreateFailureWithNull() {
            // Act & Assert
            assertThatThrownBy(() -> Result.failure(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Error message must not be null");
        }

        @Test
        @DisplayName("空のエラーメッセージで失敗結果を作成すると例外")
        void shouldThrowException_WhenCreateFailureWithEmptyMessage() {
            // Act & Assert
            assertThatThrownBy(() -> Result.failure("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Error message must not be empty");
        }
    }

    @Nested
    @DisplayName("関数型操作")
    class FunctionalOperations {

        @Test
        @DisplayName("map: 成功結果を変換できる")
        void shouldTransformValue_WhenMapOnSuccess() {
            // Arrange
            Result<String> result = Result.success("123");

            // Act
            Result<Integer> mapped = result.map(Integer::parseInt);

            // Assert
            assertThat(mapped.isSuccess()).isTrue();
            assertThat(mapped.getValue()).isEqualTo(123);
        }

        @Test
        @DisplayName("map: 失敗結果はそのまま失敗結果")
        void shouldReturnFailure_WhenMapOnFailure() {
            // Arrange
            Result<String> result = Result.failure("original error");

            // Act
            Result<Integer> mapped = result.map(Integer::parseInt);

            // Assert
            assertThat(mapped.isFailure()).isTrue();
            assertThat(mapped.getErrorMessage()).contains("original error");
        }

        @Test
        @DisplayName("map: 変換中に例外が発生すると失敗結果")
        void shouldReturnFailure_WhenMapThrowsException() {
            // Arrange
            Result<String> result = Result.success("not-a-number");

            // Act
            Result<Integer> mapped = result.map(Integer::parseInt);

            // Assert
            assertThat(mapped.isFailure()).isTrue();
            assertThat(mapped.getErrorMessage().orElse("")).contains("Mapping failed");
        }

        @Test
        @DisplayName("flatMap: 成功結果でResultを返す変換")
        void shouldFlatMapSuccessfully_WhenValidTransformation() {
            // Arrange
            Result<String> result = Result.success("123");

            // Act
            Result<Integer> flatMapped = result.flatMap(s -> {
                try {
                    return Result.success(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    return Result.failure("Invalid number: " + s);
                }
            });

            // Assert
            assertThat(flatMapped.isSuccess()).isTrue();
            assertThat(flatMapped.getValue()).isEqualTo(123);
        }

        @Test
        @DisplayName("orElse: 失敗時にデフォルト値を返す")
        void shouldReturnDefaultValue_WhenOrElseOnFailure() {
            // Arrange
            Result<String> result = Result.failure("error");
            String defaultValue = "default";

            // Act
            String value = result.orElse(defaultValue);

            // Assert
            assertThat(value).isEqualTo(defaultValue);
        }

        @Test
        @DisplayName("orElseGet: 失敗時に供給関数の値を返す")
        void shouldReturnSuppliedValue_WhenOrElseGetOnFailure() {
            // Arrange
            Result<String> result = Result.failure("error");

            // Act
            String value = result.orElseGet(() -> "supplied value");

            // Assert
            assertThat(value).isEqualTo("supplied value");
        }
    }

    @Nested
    @DisplayName("アクション実行")
    class Actions {

        @Test
        @DisplayName("onSuccess: 成功時にアクションが実行される")
        void shouldExecuteAction_WhenOnSuccessWithSuccessResult() {
            // Arrange
            Result<String> result = Result.success("test");
            StringBuilder executed = new StringBuilder();

            // Act
            Result<String> returned = result.onSuccess(value -> executed.append("executed: ").append(value));

            // Assert
            assertThat(executed.toString()).isEqualTo("executed: test");
            assertThat(returned).isSameAs(result); // 同じインスタンスを返す
        }

        @Test
        @DisplayName("onSuccess: 失敗時にアクションが実行されない")
        void shouldNotExecuteAction_WhenOnSuccessWithFailureResult() {
            // Arrange
            Result<String> result = Result.failure("error");
            StringBuilder executed = new StringBuilder();

            // Act
            result.onSuccess(value -> executed.append("should not execute"));

            // Assert
            assertThat(executed.toString()).isEmpty();
        }

        @Test
        @DisplayName("onFailure: 失敗時にアクションが実行される")
        void shouldExecuteAction_WhenOnFailureWithFailureResult() {
            // Arrange
            Result<String> result = Result.failure("test error");
            StringBuilder executed = new StringBuilder();

            // Act
            result.onFailure(error -> executed.append("error: ").append(error));

            // Assert
            assertThat(executed.toString()).isEqualTo("error: test error");
        }
    }

    @Nested
    @DisplayName("等価性とハッシュコード")
    class EqualityAndHashCode {

        @Test
        @DisplayName("同じ値の成功結果は等価")
        void shouldBeEqual_WhenSameSuccessValues() {
            // Arrange
            Result<String> result1 = Result.success("test");
            Result<String> result2 = Result.success("test");

            // Act & Assert
            assertThat(result1).isEqualTo(result2);
            assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
        }

        @Test
        @DisplayName("同じエラーメッセージの失敗結果は等価")
        void shouldBeEqual_WhenSameFailureMessages() {
            // Arrange
            Result<String> result1 = Result.failure("error");
            Result<String> result2 = Result.failure("error");

            // Act & Assert
            assertThat(result1).isEqualTo(result2);
            assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
        }

        @Test
        @DisplayName("成功結果と失敗結果は等価でない")
        void shouldNotBeEqual_WhenSuccessAndFailureResults() {
            // Arrange
            Result<String> success = Result.success("test");
            Result<String> failure = Result.failure("error");

            // Act & Assert
            assertThat(success).isNotEqualTo(failure);
        }
    }
}
