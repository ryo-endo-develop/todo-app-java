/* (C) 2025 Todo App Project */
package com.todoapp.domain.enums;

import java.util.Objects;
import javax.annotation.Nonnull;

import lombok.Getter;

/**
 * ユーザーの役割を定義するEnum
 *
 * <p>責務: - 役割値の定義と型安全性の保証 - 基本的な属性情報の保持
 *
 * <p>非責務: - 権限管理ロジック（→ UserPermissionPolicy） - 複雑な認可処理（→ ドメインサービス）
 */
@Getter
public enum UserRole {
  USER("USER", "一般ユーザー", "通常の機能を使用できるユーザー", 1),
  MODERATOR("MODERATOR", "モデレーター", "一部の管理機能を使用できるユーザー", 5),
  ADMIN("ADMIN", "管理者", "システム管理機能にアクセスできるユーザー", 10),
  SUPER_ADMIN("SUPER_ADMIN", "スーパー管理者", "全ての機能にアクセスできるユーザー", 99);

  private final String code;
  private final String displayName;
  private final String description;
  private final int level; // 権限レベル（参考値）

  // Enumコンストラクタ：基本的な属性設定のみ
  UserRole(
      @Nonnull String code, @Nonnull String displayName, @Nonnull String description, int level) {
    this.code = Objects.requireNonNull(code, "Role code must not be null");
    this.displayName = Objects.requireNonNull(displayName, "Display name must not be null");
    this.description = Objects.requireNonNull(description, "Description must not be null");
    this.level = level;
  }

  /**
   * コードから対応するEnumを取得
   *
   * @param code 役割コード
   * @return 対応するUserRole
   * @throws IllegalArgumentException 不正なコードの場合
   */
  @Nonnull
  public static UserRole fromCode(@Nonnull String code) {
    Objects.requireNonNull(code, "Code must not be null");

    String normalizedCode = code.trim().toUpperCase();
    for (UserRole role : values()) {
      if (role.code.equals(normalizedCode)) {
        return role;
      }
    }

    throw new IllegalArgumentException("Unknown role code: " + code);
  }

  /** 名前から対応するEnumを取得 */
  @Nonnull
  public static UserRole fromName(@Nonnull String name) {
    Objects.requireNonNull(name, "Name must not be null");

    try {
      return UserRole.valueOf(name.toUpperCase().trim());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unknown role name: " + name, e);
    }
  }

  /** 指定されたコードと一致するかどうかを判定 */
  public boolean hasCode(@Nonnull String code) {
    Objects.requireNonNull(code, "Code must not be null");
    return this.code.equals(code.trim().toUpperCase());
  }

  /** 指定されたレベル以上かどうかを判定 基本的な比較のみ、複雑な権限ロジックは外部サービスで処理 */
  public boolean hasLevelOrHigher(int requiredLevel) {
    return this.level >= requiredLevel;
  }
}
