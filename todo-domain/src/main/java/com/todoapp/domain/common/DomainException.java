package com.todoapp.domain.common;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * ドメイン層の例外基底クラス
 * Effective Java: Item 70 (復旧可能な状況にはチェック例外を、プログラミングエラーには実行時例外を使う)
 * ビジネスルール違反やドメイン不整合を表現
 * 重要な基盤クラスのため、fail-fastを重視してObjects.requireNonNullを使用
 */
@Getter
public class DomainException extends RuntimeException {
    
    /**
     * エラーメッセージのみの例外
     * @param message エラーメッセージ（non-null）
     */
    public DomainException(@Nonnull String message) {
        super(Objects.requireNonNull(message, "Error message must not be null"));
        if (message.trim().isEmpty()) {
            throw new IllegalArgumentException("Error message must not be empty");
        }
    }
    
    /**
     * エラーメッセージと原因例外を持つ例外
     * @param message エラーメッセージ（non-null）
     * @param cause 原因例外（non-null）
     */
    public DomainException(@Nonnull String message, @Nonnull Throwable cause) {
        super(Objects.requireNonNull(message, "Error message must not be null"),
              Objects.requireNonNull(cause, "Cause must not be null"));
        if (message.trim().isEmpty()) {
            throw new IllegalArgumentException("Error message must not be empty");
        }
    }
    
    /**
     * ファクトリメソッド: ビジネスルール違反
     */
    @Nonnull
    public static DomainException businessRuleViolation(@Nonnull String ruleName, @Nonnull String details) {
        Objects.requireNonNull(ruleName, "Rule name must not be null");
        Objects.requireNonNull(details, "Details must not be null");
        return new DomainException(String.format("Business rule violation [%s]: %s", ruleName, details));
    }
    
    /**
     * ファクトリメソッド: 不正な状態
     */
    @Nonnull
    public static DomainException invalidState(@Nonnull String currentState, @Nonnull String operation) {
        Objects.requireNonNull(currentState, "Current state must not be null");
        Objects.requireNonNull(operation, "Operation must not be null");
        return new DomainException(String.format("Invalid state [%s] for operation [%s]", currentState, operation));
    }
    
    /**
     * ファクトリメソッド: 値が見つからない
     */
    @Nonnull
    public static DomainException notFound(@Nonnull String entityType, @Nonnull String identifier) {
        Objects.requireNonNull(entityType, "Entity type must not be null");
        Objects.requireNonNull(identifier, "Identifier must not be null");
        return new DomainException(String.format("%s not found: %s", entityType, identifier));
    }
    
    /**
     * ファクトリメソッド: 重複エラー
     */
    @Nonnull
    public static DomainException duplicate(@Nonnull String entityType, @Nonnull String identifier) {
        Objects.requireNonNull(entityType, "Entity type must not be null");
        Objects.requireNonNull(identifier, "Identifier must not be null");
        return new DomainException(String.format("%s already exists: %s", entityType, identifier));
    }
}
