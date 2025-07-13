/* (C) 2025 Todo App Project */
package com.todoapp.domain.enums;

import java.util.Objects;
import javax.annotation.Nonnull;

import lombok.Getter;

/**
 * Todoのステータスを定義するEnum
 *
 * <p>責務: - ステータス値の定義と型安全性の保証 - 不正な値の防止 - 基本的な属性情報の保持
 *
 * <p>非責務: - 状態遷移ロジック（→ TodoStatusTransitionPolicy） - 複雑なビジネスルール（→ ドメインサービス）
 */
@Getter
public enum TodoStatus {
  TODO("TODO", "未完了", "新しく作成されたTodo"),
  IN_PROGRESS("IN_PROGRESS", "進行中", "作業中のTodo"),
  COMPLETED("COMPLETED", "完了", "完了したTodo"),
  DELETED("DELETED", "削除", "論理削除されたTodo");

  private final String code;
  private final String displayName;
  private final String description;

  // Enumコンストラクタ：基本的な属性設定のみ
  TodoStatus(@Nonnull String code, @Nonnull String displayName, @Nonnull String description) {
    this.code = Objects.requireNonNull(code, "Status code must not be null");
    this.displayName = Objects.requireNonNull(displayName, "Display name must not be null");
    this.description = Objects.requireNonNull(description, "Description must not be null");
  }

  /**
   * コードから対応するEnumを取得
   *
   * @param code ステータスコード
   * @return 対応するTodoStatus
   * @throws IllegalArgumentException 不正なコードの場合
   */
  @Nonnull
  public static TodoStatus fromCode(@Nonnull String code) {
    Objects.requireNonNull(code, "Code must not be null");

    String normalizedCode = code.trim().toUpperCase();
    for (TodoStatus status : values()) {
      if (status.code.equals(normalizedCode)) {
        return status;
      }
    }

    throw new IllegalArgumentException("Unknown status code: " + code);
  }

  /**
   * 名前から対応するEnumを取得（大文字小文字を区別しない）
   *
   * @param name ステータス名
   * @return 対応するTodoStatus
   * @throws IllegalArgumentException 不正な名前の場合
   */
  @Nonnull
  public static TodoStatus fromName(@Nonnull String name) {
    Objects.requireNonNull(name, "Name must not be null");

    try {
      return TodoStatus.valueOf(name.toUpperCase().trim());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unknown status name: " + name, e);
    }
  }

  /** 指定されたコードと一致するかどうかを判定 */
  public boolean hasCode(@Nonnull String code) {
    Objects.requireNonNull(code, "Code must not be null");
    return this.code.equals(code.trim().toUpperCase());
  }
}
