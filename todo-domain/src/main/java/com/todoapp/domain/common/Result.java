package com.todoapp.domain.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 関数型プログラミングスタイルのResult型
 * 
 * 設計原則:
 * - Effective Java Item 17: 不変性を最大化
 * - Effective Java Item 69: 例外は例外的な状況でのみ使用
 * - Thread-safe: 全フィールドがfinalで不変
 * - Fail-fast: 不正な状態を早期検出
 * 
 * @param <T> 成功時の値の型
 */
@Immutable
@ThreadSafe
@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Result<T> {
    
    @Nullable
    private final T value;
    
    @Nullable
    private final String errorMessage;
    
    private final boolean success;
    
    /**
     * 成功結果を作成
     * @param value 成功時の値（non-null）
     * @throws IllegalArgumentException valueがnullの場合
     */
    @Nonnull
    public static <T> Result<T> success(@Nonnull T value) {
        if (value == null) {
            throw new IllegalArgumentException("Success value must not be null");
        }
        return new Result<>(value, null, true);
    }
    
    /**
     * 失敗結果を作成
     * @param errorMessage エラーメッセージ（non-null, non-empty）
     * @throws IllegalArgumentException errorMessageがnullまたは空の場合
     */
    @Nonnull
    public static <T> Result<T> failure(@Nonnull String errorMessage) {
        if (errorMessage == null) {
            throw new IllegalArgumentException("Error message must not be null");
        }
        if (errorMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Error message must not be empty");
        }
        return new Result<>(null, errorMessage, false);
    }
    
    /**
     * 成功かどうかを判定
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * 失敗かどうかを判定
     */
    public boolean isFailure() {
        return !success;
    }
    
    /**
     * 成功時の値を取得（失敗時は例外）
     * @throws IllegalStateException 失敗結果に対して呼び出した場合
     */
    @Nonnull
    public T getValue() {
        if (isFailure()) {
            throw new IllegalStateException("Cannot get value from failure result: " + errorMessage);
        }
        return value; // success時はnon-nullが保証されている
    }
    
    /**
     * 成功時の値をOptionalで取得
     * Thread-safe: 状態は不変なので同期不要
     */
    @Nonnull
    public Optional<T> getValueOptional() {
        return isSuccess() ? Optional.of(value) : Optional.empty();
    }
    
    /**
     * エラーメッセージを取得
     */
    @Nonnull
    public Optional<String> getErrorMessage() {
        return isFailure() ? Optional.of(errorMessage) : Optional.empty();
    }
    
    /**
     * 関数型スタイルでの値変換
     * Thread-safe: 新しいResultオブジェクトを返すため
     * 
     * @param mapper 変換関数（non-null）
     * @throws IllegalArgumentException mapperがnullの場合
     */
    @Nonnull
    public <U> Result<U> map(@Nonnull Function<T, U> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper function must not be null");
        }
        
        if (isFailure()) {
            return Result.failure(errorMessage);
        }
        
        try {
            U mappedValue = mapper.apply(value);
            if (mappedValue == null) {
                return Result.failure("Mapped value must not be null");
            }
            return Result.success(mappedValue);
        } catch (Exception e) {
            return Result.failure("Mapping failed: " + e.getMessage());
        }
    }
    
    /**
     * 関数型スタイルでの値変換（Resultを返す場合）
     * @param mapper Result を返す変換関数（non-null）
     */
    @Nonnull
    public <U> Result<U> flatMap(@Nonnull Function<T, Result<U>> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper function must not be null");
        }
        
        if (isFailure()) {
            return Result.failure(errorMessage);
        }
        
        try {
            Result<U> result = mapper.apply(value);
            if (result == null) {
                return Result.failure("Mapped result must not be null");
            }
            return result;
        } catch (Exception e) {
            return Result.failure("FlatMapping failed: " + e.getMessage());
        }
    }
    
    /**
     * 成功時にアクションを実行
     * Thread-safe: thisを返すが状態は変更しない
     */
    @Nonnull
    public Result<T> onSuccess(@Nonnull Consumer<T> action) {
        if (action == null) {
            throw new IllegalArgumentException("Action must not be null");
        }
        
        if (isSuccess()) {
            action.accept(value);
        }
        return this;
    }
    
    /**
     * 失敗時にアクションを実行
     */
    @Nonnull
    public Result<T> onFailure(@Nonnull Consumer<String> action) {
        if (action == null) {
            throw new IllegalArgumentException("Action must not be null");
        }
        
        if (isFailure()) {
            action.accept(errorMessage);
        }
        return this;
    }
    
    /**
     * 失敗時のデフォルト値を提供
     */
    @Nonnull
    public T orElse(@Nonnull T defaultValue) {
        if (defaultValue == null) {
            throw new IllegalArgumentException("Default value must not be null");
        }
        return isSuccess() ? value : defaultValue;
    }
    
    /**
     * 失敗時のデフォルト値を供給関数で提供
     */
    @Nonnull
    public T orElseGet(@Nonnull Supplier<T> defaultSupplier) {
        if (defaultSupplier == null) {
            throw new IllegalArgumentException("Default supplier must not be null");
        }
        return isSuccess() ? value : defaultSupplier.get();
    }
    
    /**
     * 等価性の判定
     * Result の状態（成功/失敗）と内容を正しく比較する
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Result<?> other = (Result<?>) obj;
        
        // まず状態（成功/失敗）を比較
        if (this.success != other.success) return false;
        
        if (this.success) {
            // 成功結果の場合：値を比較
            return Objects.equals(this.value, other.value);
        } else {
            // 失敗結果の場合：エラーメッセージを比較
            return Objects.equals(this.errorMessage, other.errorMessage);
        }
    }
    
    /**
     * ハッシュコードの計算
     * equals契約に従って実装
     */
    @Override
    public int hashCode() {
        if (success) {
            return Objects.hash(success, value);
        } else {
            return Objects.hash(success, errorMessage);
        }
    }
}
