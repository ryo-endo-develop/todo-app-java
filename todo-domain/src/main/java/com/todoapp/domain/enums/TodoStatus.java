package com.todoapp.domain.enums;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Todoのステータスを表現する列挙型
 * Effective Java: Item 34 (intの代わりにenumを使う)
 * 不変性とタイプセーフ性を保証
 */
@Getter
public enum TodoStatus {
    
    TODO("未完了", "新しく作成されたTodo"),
    IN_PROGRESS("進行中", "作業中のTodo"),
    COMPLETED("完了", "完了したTodo"),
    DELETED("削除", "論理削除されたTodo");
    
    private final String displayName;
    private final String description;
    
    // Enumコンストラクタ: fail-fastが重要なのでObjects.requireNonNullを使用
    TodoStatus(@Nonnull String displayName, @Nonnull String description) {
        this.displayName = Objects.requireNonNull(displayName, "Display name must not be null");
        this.description = Objects.requireNonNull(description, "Description must not be null");
    }
    
    /**
     * アクティブなステータスかどうかを判定
     * 削除されたTodoは非アクティブ
     */
    public boolean isActive() {
        return this != DELETED;
    }
    
    /**
     * 完了状態かどうかを判定
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }
    
    /**
     * 進行可能な状態かどうかを判定
     * 削除されたTodoは進行不可能
     */
    public boolean canProgress() {
        return this == TODO || this == IN_PROGRESS;
    }
    
    /**
     * 指定したステータスへの遷移が可能かどうかを判定
     * ビジネスルールに基づく状態遷移の制御
     */
    public boolean canTransitionTo(@Nonnull TodoStatus newStatus) {
        // publicメソッドなので引数バリデーション
        if (newStatus == null) {
            throw new IllegalArgumentException("New status must not be null");
        }
        
        // 削除されたTodoは他の状態に遷移できない
        if (this == DELETED) {
            return false;
        }
        
        // 削除への遷移は常に可能（論理削除）
        if (newStatus == DELETED) {
            return true;
        }
        
        // 同じ状態への遷移は無意味だが許可
        if (this == newStatus) {
            return true;
        }
        
        // ビジネスルールに基づく遷移規則
        return switch (this) {
            case TODO -> newStatus == IN_PROGRESS || newStatus == COMPLETED;
            case IN_PROGRESS -> newStatus == TODO || newStatus == COMPLETED;
            case COMPLETED -> newStatus == TODO || newStatus == IN_PROGRESS;
            case DELETED -> false; // 上で既にチェック済み
        };
    }
    
    /**
     * 文字列からTodoStatusを取得
     * 大文字小文字を区別しない
     */
    @Nonnull
    public static TodoStatus fromString(@Nonnull String status) {
        if (status == null) {
            throw new IllegalArgumentException("Status string must not be null");
        }
        
        try {
            return TodoStatus.valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TodoStatus: " + status +
                ". Valid values are: TODO, IN_PROGRESS, COMPLETED, DELETED", e);
        }
    }
}
