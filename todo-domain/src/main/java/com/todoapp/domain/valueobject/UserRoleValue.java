package com.todoapp.domain.valueobject;

import com.todoapp.domain.common.Result;
import com.todoapp.domain.enums.UserRole;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;

/**
 * ユーザー役割の値オブジェクト
 * 
 * 責務:
 * - Enumをラップして追加機能を提供
 * - 基本的な比較・判定機能
 * - 型安全性の保証
 * 
 * 非責務:
 * - 複雑な権限判定（→ UserPermissionPolicy）
 * - 認可ロジック（→ ドメインサービス）
 */
@Immutable
@ThreadSafe
@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserRoleValue {
    
    @Nonnull
    private final UserRole role;
    
    /**
     * Enumから値オブジェクトを作成
     */
    @Nonnull
    public static UserRoleValue of(@Nonnull UserRole role) {
        Objects.requireNonNull(role, "Role must not be null");
        return new UserRoleValue(role);
    }
    
    /**
     * コードから値オブジェクトを作成
     */
    @Nonnull
    public static Result<UserRoleValue> fromCode(@Nonnull String code) {
        if (code == null) {
            return Result.failure("Role code must not be null");
        }
        
        try {
            UserRole role = UserRole.fromCode(code);
            return Result.success(new UserRoleValue(role));
        } catch (IllegalArgumentException e) {
            return Result.failure("Invalid role code: " + code);
        }
    }
    
    /**
     * 名前から値オブジェクトを作成
     */
    @Nonnull
    public static Result<UserRoleValue> fromName(@Nonnull String name) {
        if (name == null) {
            return Result.failure("Role name must not be null");
        }
        
        try {
            UserRole role = UserRole.fromName(name);
            return Result.success(new UserRoleValue(role));
        } catch (IllegalArgumentException e) {
            return Result.failure("Invalid role name: " + name);
        }
    }
    
    // 基本的な判定メソッド
    
    /**
     * 指定した役割と同じかどうかを判定
     */
    public boolean is(@Nonnull UserRole targetRole) {
        Objects.requireNonNull(targetRole, "Target role must not be null");
        return this.role == targetRole;
    }
    
    /**
     * 指定した役割のいずれかと一致するかどうかを判定
     */
    public boolean isAnyOf(@Nonnull UserRole... roles) {
        Objects.requireNonNull(roles, "Roles must not be null");
        
        for (UserRole targetRole : roles) {
            if (this.role == targetRole) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 指定されたレベル以上かどうかを判定
     * 単純な数値比較のみ、複雑な権限ロジックは外部サービスで処理
     */
    public boolean hasLevelOrHigher(int requiredLevel) {
        return role.hasLevelOrHigher(requiredLevel);
    }
    
    /**
     * 他の役割より高いレベルかどうかを判定
     */
    public boolean hasHigherLevelThan(@Nonnull UserRoleValue other) {
        Objects.requireNonNull(other, "Comparison target must not be null");
        return this.role.getLevel() > other.role.getLevel();
    }
    
    /**
     * 基本的な属性へのアクセス
     */
    public String getCode() {
        return role.getCode();
    }
    
    public String getDisplayName() {
        return role.getDisplayName();
    }
    
    public String getDescription() {
        return role.getDescription();
    }
    
    public int getLevel() {
        return role.getLevel();
    }
    
    /**
     * 別のUserRoleValueと同じかどうかを判定
     */
    public boolean isSameAs(@Nonnull UserRoleValue other) {
        Objects.requireNonNull(other, "Comparison target must not be null");
        return this.equals(other);
    }
}
