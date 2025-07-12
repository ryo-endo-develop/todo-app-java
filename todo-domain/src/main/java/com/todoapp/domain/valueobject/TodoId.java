package com.todoapp.domain.valueobject;

import com.todoapp.domain.common.Result;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * TodoのIDを表現する値オブジェクト
 * 
 * 設計原則:
 * - Immutable: すべてのフィールドがfinal
 * - Thread-safe: 状態が不変なので同期不要
 * - Fail-fast: 不正な値を早期検出
 * - Single Responsibility: IDの表現と検証のみ
 * 
 * 拡張性:
 * - ファクトリメソッドパターンで生成方法を制御
 * - 将来的なID形式変更に対応可能
 * - バリデーション戦略の追加が容易
 */
@Immutable
@ThreadSafe
@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TodoId {
    
    /**
     * ID値の制約
     */
    private static final long MIN_VALUE = 1L;
    private static final long MAX_VALUE = Long.MAX_VALUE;
    
    private final long value;
    
    /**
     * DBで採番された値からTodoIdを作成
     * 
     * @param value ID値（1以上の正数）
     * @return 成功時はTodoId、失敗時はエラーメッセージ
     */
    @Nonnull
    public static Result<TodoId> of(long value) {
        if (value < MIN_VALUE) {
            return Result.failure(
                String.format("TodoId value must be at least %d, but was: %d", MIN_VALUE, value)
            );
        }
        if (value > MAX_VALUE) {
            return Result.failure(
                String.format("TodoId value must be at most %d, but was: %d", MAX_VALUE, value)
            );
        }
        return Result.success(new TodoId(value));
    }
    
    /**
     * 文字列からTodoIdを作成
     * 
     * @param value 数値文字列（non-null）
     * @return パース成功時はTodoId、失敗時はエラーメッセージ
     */
    @Nonnull
    public static Result<TodoId> of(@Nonnull String value) {
        if (value == null) {
            return Result.failure("TodoId string must not be null");
        }
        
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return Result.failure("TodoId string must not be empty");
        }
        
        try {
            long longValue = Long.parseLong(trimmed);
            return of(longValue);
        } catch (NumberFormatException e) {
            return Result.failure(
                String.format("TodoId string must be a valid number: '%s'", value)
            );
        }
    }
    
    /**
     * 別のTodoIdと同じかどうかを判定
     * 
     * @param other 比較対象（non-null）
     * @return 同じIDの場合true
     * @throws IllegalArgumentException otherがnullの場合
     */
    public boolean isSameAs(@Nonnull TodoId other) {
        if (other == null) {
            throw new IllegalArgumentException("Comparison target must not be null");
        }
        return this.equals(other);
    }
    
    /**
     * 指定されたIDより大きいかどうかを判定
     * ソート処理などで使用
     * 
     * @param other 比較対象（non-null）
     * @return このIDが大きい場合true
     */
    public boolean isGreaterThan(@Nonnull TodoId other) {
        if (other == null) {
            throw new IllegalArgumentException("Comparison target must not be null");
        }
        return this.value > other.value;
    }
    
    /**
     * 指定されたIDより小さいかどうかを判定
     * 
     * @param other 比較対象（non-null）
     * @return このIDが小さい場合true
     */
    public boolean isLessThan(@Nonnull TodoId other) {
        if (other == null) {
            throw new IllegalArgumentException("Comparison target must not be null");
        }
        return this.value < other.value;
    }
}
