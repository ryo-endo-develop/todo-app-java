/* (C) 2025 Todo App Project */
package com.todoapp.domain.service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.todoapp.domain.enums.UserRole;

/**
 * ユーザー権限管理のビジネスルールを管理するポリシークラス
 *
 * <p>設計原則: - Strategy Pattern: 権限チェックロジックを戦略として外部化 - 細粒度権限: 機能レベルでの詳細な権限制御 - 拡張性: 新しい権限・役割の動的追加
 */
@ThreadSafe
public final class UserPermissionPolicy {

  /** システム内の権限を定義する列挙型 新しい機能追加時に権限を追加 */
  public enum Permission {
    // Todo関連権限
    CREATE_TODO("Todoの作成"),
    READ_OWN_TODO("自分のTodoの閲覧"),
    UPDATE_OWN_TODO("自分のTodoの更新"),
    DELETE_OWN_TODO("自分のTodoの削除"),
    READ_ALL_TODOS("全てのTodoの閲覧"),
    UPDATE_ALL_TODOS("全てのTodoの更新"),
    DELETE_ALL_TODOS("全てのTodoの削除"),

    // ユーザー管理権限
    CREATE_USER("ユーザーの作成"),
    READ_USER_LIST("ユーザー一覧の閲覧"),
    UPDATE_USER_PROFILE("ユーザープロフィールの更新"),
    DELETE_USER("ユーザーの削除"),
    CHANGE_USER_ROLE("ユーザー役割の変更"),

    // システム管理権限
    SYSTEM_CONFIGURATION("システム設定の変更"),
    VIEW_SYSTEM_LOGS("システムログの閲覧"),
    BACKUP_RESTORE("バックアップ・復元"),

    // 監査・レポート権限
    VIEW_AUDIT_LOGS("監査ログの閲覧"),
    GENERATE_REPORTS("レポート生成"),
    EXPORT_DATA("データエクスポート");

    private final String description;

    Permission(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  // 役割と権限のマッピング
  private final Map<UserRole, Set<Permission>> rolePermissions;

  /** デフォルトの権限設定でポリシーを作成 */
  public UserPermissionPolicy() {
    this.rolePermissions = new ConcurrentHashMap<>();
    initializeDefaultPermissions();
  }

  /** カスタム権限設定でポリシーを作成 */
  public UserPermissionPolicy(@Nonnull Map<UserRole, Set<Permission>> customPermissions) {
    this.rolePermissions = new ConcurrentHashMap<>(customPermissions);
  }

  /** デフォルトの権限設定を初期化 */
  private void initializeDefaultPermissions() {
    // 一般ユーザー権限
    rolePermissions.put(
        UserRole.USER,
        Set.of(
            Permission.CREATE_TODO,
            Permission.READ_OWN_TODO,
            Permission.UPDATE_OWN_TODO,
            Permission.DELETE_OWN_TODO,
            Permission.UPDATE_USER_PROFILE));

    // モデレーター権限（一般ユーザー + 限定管理機能）
    rolePermissions.put(
        UserRole.MODERATOR,
        Set.of(
            Permission.CREATE_TODO,
            Permission.READ_OWN_TODO,
            Permission.UPDATE_OWN_TODO,
            Permission.DELETE_OWN_TODO,
            Permission.READ_ALL_TODOS,
            Permission.UPDATE_USER_PROFILE,
            Permission.READ_USER_LIST,
            Permission.VIEW_AUDIT_LOGS));

    // 管理者権限（モデレーター + ユーザー管理）
    rolePermissions.put(
        UserRole.ADMIN,
        Set.of(
            Permission.CREATE_TODO,
            Permission.READ_OWN_TODO,
            Permission.UPDATE_OWN_TODO,
            Permission.DELETE_OWN_TODO,
            Permission.READ_ALL_TODOS,
            Permission.UPDATE_ALL_TODOS,
            Permission.DELETE_ALL_TODOS,
            Permission.CREATE_USER,
            Permission.READ_USER_LIST,
            Permission.UPDATE_USER_PROFILE,
            Permission.DELETE_USER,
            Permission.CHANGE_USER_ROLE,
            Permission.VIEW_AUDIT_LOGS,
            Permission.GENERATE_REPORTS,
            Permission.EXPORT_DATA));

    // スーパー管理者権限（全権限）
    rolePermissions.put(UserRole.SUPER_ADMIN, Set.of(Permission.values()));
  }

  /**
   * 指定した役割が特定の権限を持つかどうかを判定
   *
   * @param role ユーザー役割
   * @param permission 確認する権限
   * @return 権限を持つ場合true
   */
  public boolean hasPermission(@Nonnull UserRole role, @Nonnull Permission permission) {
    Objects.requireNonNull(role, "Role must not be null");
    Objects.requireNonNull(permission, "Permission must not be null");

    Set<Permission> permissions = rolePermissions.get(role);
    return permissions != null && permissions.contains(permission);
  }

  /**
   * 指定した役割の全権限を取得
   *
   * @param role ユーザー役割
   * @return 権限一覧
   */
  @Nonnull
  public Set<Permission> getPermissions(@Nonnull UserRole role) {
    Objects.requireNonNull(role, "Role must not be null");
    return rolePermissions.getOrDefault(role, Set.of());
  }

  /**
   * 役割に権限を追加
   *
   * @param role ユーザー役割
   * @param permission 追加する権限
   */
  public void addPermission(@Nonnull UserRole role, @Nonnull Permission permission) {
    Objects.requireNonNull(role, "Role must not be null");
    Objects.requireNonNull(permission, "Permission must not be null");

    rolePermissions.computeIfAbsent(role, k -> ConcurrentHashMap.newKeySet()).add(permission);
  }

  /**
   * 役割から権限を削除
   *
   * @param role ユーザー役割
   * @param permission 削除する権限
   */
  public void removePermission(@Nonnull UserRole role, @Nonnull Permission permission) {
    Objects.requireNonNull(role, "Role must not be null");
    Objects.requireNonNull(permission, "Permission must not be null");

    Set<Permission> permissions = rolePermissions.get(role);
    if (permissions != null) {
      permissions.remove(permission);
    }
  }

  /**
   * 役割の全権限をクリア
   *
   * @param role ユーザー役割
   */
  public void clearPermissions(@Nonnull UserRole role) {
    Objects.requireNonNull(role, "Role must not be null");
    rolePermissions.remove(role);
  }

  /**
   * 新しい役割を権限セットと共に追加
   *
   * @param role 新しい役割
   * @param permissions 権限セット
   */
  public void addRole(@Nonnull UserRole role, @Nonnull Set<Permission> permissions) {
    Objects.requireNonNull(role, "Role must not be null");
    Objects.requireNonNull(permissions, "Permissions must not be null");

    rolePermissions.put(role, ConcurrentHashMap.newKeySet(permissions.size()));
    rolePermissions.get(role).addAll(permissions);
  }
}
