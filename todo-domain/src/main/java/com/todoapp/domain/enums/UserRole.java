package com.todoapp.domain.enums;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * ユーザーの役割を表現する列挙型
 * Effective Java: Item 34 (intの代わりにenumを使う)
 */
@Getter
public enum UserRole {
    
    USER("一般ユーザー", "通常の機能を使用できるユーザー"),
    ADMIN("管理者", "システム管理機能にアクセスできるユーザー");
    
    private final String displayName;
    private final String description;
    
    // Enumコンストラクタ: fail-fastが重要
    UserRole(@Nonnull String displayName, @Nonnull String description) {
        this.displayName = Objects.requireNonNull(displayName, "Display name must not be null");
        this.description = Objects.requireNonNull(description, "Description must not be null");
    }
    
    /**
     * 管理者権限があるかどうかを判定
     */
    public boolean hasAdminPrivileges() {
        return this == ADMIN;
    }
    
    /**
     * 指定した操作を実行する権限があるかどうかを判定
     * Strategy パターンの応用
     */
    public boolean canPerform(@Nonnull AdminOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Operation must not be null");
        }
        return operation.isAllowedFor(this);
    }
    
    /**
     * 文字列からUserRoleを取得
     */
    @Nonnull
    public static UserRole fromString(@Nonnull String role) {
        if (role == null) {
            throw new IllegalArgumentException("Role string must not be null");
        }
        
        try {
            return UserRole.valueOf(role.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserRole: " + role +
                ". Valid values are: USER, ADMIN", e);
        }
    }
    
    /**
     * 管理者操作を定義するStrategy パターンのインターフェース
     */
    public enum AdminOperation {
        USER_MANAGEMENT {
            @Override
            public boolean isAllowedFor(@Nonnull UserRole role) {
                return role.hasAdminPrivileges();
            }
        },
        SYSTEM_CONFIGURATION {
            @Override
            public boolean isAllowedFor(@Nonnull UserRole role) {
                return role.hasAdminPrivileges();
            }
        },
        VIEW_ALL_TODOS {
            @Override
            public boolean isAllowedFor(@Nonnull UserRole role) {
                return role.hasAdminPrivileges();
            }
        },
        MANAGE_OWN_TODOS {
            @Override
            public boolean isAllowedFor(@Nonnull UserRole role) {
                return true; // 全ユーザーが自分のTodoを管理可能
            }
        };
        
        public abstract boolean isAllowedFor(@Nonnull UserRole role);
    }
}
