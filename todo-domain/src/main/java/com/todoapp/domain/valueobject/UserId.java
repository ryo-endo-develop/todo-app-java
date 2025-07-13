/* (C) 2025 Todo App Project */
package com.todoapp.domain.valueobject;

import javax.annotation.Nonnull;

import com.todoapp.domain.common.Result;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** ユーザーのIDを表現する値オブジェクト Effective Java: Item 17 (可変性を最小限に抑える) Lombokで簡潔に実装、DBからの採番値を使用 */
@Getter
@EqualsAndHashCode
@ToString
public final class UserId {

  private final long value;

  // プライベートコンストラクタ
  private UserId(long value) {
    this.value = value;
  }

  /**
   * DBで採番された値からUserIdを作成
   *
   * @param value ID値（1以上の正数）
   */
  public static Result<UserId> of(long value) {
    if (value <= 0) {
      return Result.failure("UserId value must be positive: " + value);
    }
    return Result.success(new UserId(value));
  }

  /** 文字列からUserIdを作成 */
  public static Result<UserId> of(@Nonnull String value) {
    if (value.trim().isEmpty()) {
      return Result.failure("UserId string must not be empty");
    }

    try {
      long longValue = Long.parseLong(value.trim());
      return of(longValue);
    } catch (NumberFormatException e) {
      return Result.failure("UserId string must be a valid number: " + value);
    }
  }

  /** 別のUserIdと同じかどうかを判定 */
  public boolean isSameAs(@Nonnull UserId other) {
    return this.equals(other);
  }
}
