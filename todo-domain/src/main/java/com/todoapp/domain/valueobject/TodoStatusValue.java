/* (C) 2025 Todo App Project */
package com.todoapp.domain.valueobject;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.todoapp.domain.common.Result;
import com.todoapp.domain.enums.TodoStatus;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Todoステータスの値オブジェクト
 *
 * <p>責務: - Enumをラップして追加機能を提供 - ビジネスロジックに必要な操作メソッド - 型安全性の保証とバリデーション
 *
 * <p>EnumとValueObjectの役割分担: - Enum: 値の定義、基本属性 - ValueObject: 操作メソッド、ビジネス固有の機能
 */
@Immutable
@ThreadSafe
@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TodoStatusValue {

  @Nonnull private final TodoStatus status;

  /** Enumから値オブジェクトを作成 */
  @Nonnull
  public static TodoStatusValue of(@Nonnull TodoStatus status) {
    Objects.requireNonNull(status, "Status must not be null");
    return new TodoStatusValue(status);
  }

  /** コードから値オブジェクトを作成 */
  @Nonnull
  public static Result<TodoStatusValue> fromCode(@Nonnull String code) {
    if (code == null) {
      return Result.failure("Status code must not be null");
    }

    try {
      TodoStatus status = TodoStatus.fromCode(code);
      return Result.success(new TodoStatusValue(status));
    } catch (IllegalArgumentException e) {
      return Result.failure("Invalid status code: " + code);
    }
  }

  /** 名前から値オブジェクトを作成 */
  @Nonnull
  public static Result<TodoStatusValue> fromName(@Nonnull String name) {
    if (name == null) {
      return Result.failure("Status name must not be null");
    }

    try {
      TodoStatus status = TodoStatus.fromName(name);
      return Result.success(new TodoStatusValue(status));
    } catch (IllegalArgumentException e) {
      return Result.failure("Invalid status name: " + name);
    }
  }

  // ビジネスロジック用の便利メソッド

  /** アクティブなステータスかどうかを判定 */
  public boolean isActive() {
    return status != TodoStatus.DELETED;
  }

  /** 完了状態かどうかを判定 */
  public boolean isCompleted() {
    return status == TodoStatus.COMPLETED;
  }

  /** 作業可能な状態かどうかを判定 */
  public boolean isWorkable() {
    return status == TodoStatus.TODO || status == TodoStatus.IN_PROGRESS;
  }

  /** 削除状態かどうかを判定 */
  public boolean isDeleted() {
    return status == TodoStatus.DELETED;
  }

  /** 進行中の状態かどうかを判定 */
  public boolean isInProgress() {
    return status == TodoStatus.IN_PROGRESS;
  }

  /** 指定したステータスと同じかどうかを判定 */
  public boolean is(@Nonnull TodoStatus targetStatus) {
    Objects.requireNonNull(targetStatus, "Target status must not be null");
    return this.status == targetStatus;
  }

  /** 指定したステータスのいずれかと一致するかどうかを判定 */
  public boolean isAnyOf(@Nonnull TodoStatus... statuses) {
    Objects.requireNonNull(statuses, "Statuses must not be null");

    for (TodoStatus targetStatus : statuses) {
      if (this.status == targetStatus) {
        return true;
      }
    }
    return false;
  }

  /** 基本的な属性へのアクセス */
  public String getCode() {
    return status.getCode();
  }

  public String getDisplayName() {
    return status.getDisplayName();
  }

  public String getDescription() {
    return status.getDescription();
  }

  /** 別のTodoStatusValueと同じかどうかを判定 */
  public boolean isSameAs(@Nonnull TodoStatusValue other) {
    Objects.requireNonNull(other, "Comparison target must not be null");
    return this.equals(other);
  }
}
