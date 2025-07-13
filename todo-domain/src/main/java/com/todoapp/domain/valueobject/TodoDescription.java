/* (C) 2025 Todo App Project */
package com.todoapp.domain.valueobject;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.todoapp.domain.common.Result;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Todoの説明を表現する値オブジェクト Effective Java: Item 55 (Optionalを適切に返す) 説明は任意項目のためOptionalで扱う */
@EqualsAndHashCode
@ToString
public final class TodoDescription {

  // ビジネスルール定数
  private static final int MAX_LENGTH = 1000;

  @Nullable private final String value;

  // プライベートコンストラクタ
  private TodoDescription(@Nullable String value) {
    this.value = value;
  }

  /** 空の説明を作成 */
  public static TodoDescription empty() {
    return new TodoDescription(null);
  }

  /**
   * 文字列から説明を作成
   *
   * @param value 説明文（nullまたは空文字列可）
   */
  public static Result<TodoDescription> of(@Nullable String value) {
    // nullや空文字列は許可
    if (value == null || value.trim().isEmpty()) {
      return Result.success(new TodoDescription(null));
    }

    String trimmed = value.trim();

    if (trimmed.length() > MAX_LENGTH) {
      return Result.failure(
          "Todo説明は" + MAX_LENGTH + "文字以内で入力してください（現在: " + trimmed.length() + "文字）");
    }

    return Result.success(new TodoDescription(trimmed));
  }

  /** 説明があるかどうかを判定 */
  public boolean hasValue() {
    return value != null && !value.isEmpty();
  }

  /** 説明値をOptionalで取得 */
  public Optional<String> getValue() {
    return hasValue() ? Optional.of(value) : Optional.empty();
  }

  /** 説明値を取得（説明がない場合はデフォルト値） */
  public String getValueOrDefault(@Nonnull String defaultValue) {
    return hasValue() ? value : defaultValue;
  }

  /** 説明の長さを取得 */
  public int length() {
    return hasValue() ? value.length() : 0;
  }

  /** 指定した文字列が含まれているかチェック */
  public boolean contains(@Nonnull String searchText) {
    if (!hasValue()) {
      return false;
    }
    return value.toLowerCase().contains(searchText.toLowerCase());
  }

  /** 別のTodoDescriptionと同じかどうかを判定 */
  public boolean isSameAs(@Nonnull TodoDescription other) {
    return this.equals(other);
  }
}
