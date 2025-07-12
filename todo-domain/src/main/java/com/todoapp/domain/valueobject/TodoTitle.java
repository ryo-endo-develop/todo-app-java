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
import java.util.regex.Pattern;

/**
 * Todoのタイトルを表現する値オブジェクト
 * 
 * 責務:
 * - タイトル文字列の検証と正規化
 * - ビジネスルールの適用（長さ制限、文字種制限）
 * - 検索・比較機能の提供
 * 
 * 拡張性:
 * - バリデーション戦略パターンの追加が容易
 * - 国際化対応の文字種制限変更が可能
 * - 新しいビジネスルール追加に対応
 */
@Immutable
@ThreadSafe
@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TodoTitle {
    
    /**
     * ビジネスルール定数
     * 将来的な要件変更に備えて設定可能に設計
     */
    public static final class ValidationRules {
        public static final int MIN_LENGTH = 1;
        public static final int MAX_LENGTH = 255;
        
        // 禁止文字パターン（制御文字や一部記号）
        // 将来的に国際化要件に応じて調整可能
        public static final Pattern INVALID_CHARS = 
            Pattern.compile("[\\p{Cntrl}\\p{So}&&[^\\t\\n\\r]]");
        
        private ValidationRules() {} // ユーティリティクラス
    }
    
    private final String value;
    
    /**
     * 文字列からTodoTitleを作成
     * 
     * 処理フロー:
     * 1. 基本的なnullチェック
     * 2. 前後空白の削除（正規化）
     * 3. 長さ制限の検証
     * 4. 文字種制限の検証
     * 
     * @param value タイトル文字列（non-null）
     * @return 検証成功時はTodoTitle、失敗時はエラーメッセージ
     */
    @Nonnull
    public static Result<TodoTitle> of(@Nonnull String value) {
        if (value == null) {
            return Result.failure("Todoタイトルは必須です");
        }
        
        // 前後の空白を削除（正規化）
        String normalized = value.trim();
        
        // 長さ制限の検証
        Result<Void> lengthValidation = validateLength(normalized);
        if (lengthValidation.isFailure()) {
            return Result.failure(lengthValidation.getErrorMessage().orElse("Unknown validation error"));
        }
        
        // 文字種制限の検証
        Result<Void> characterValidation = validateCharacters(normalized);
        if (characterValidation.isFailure()) {
            return Result.failure(characterValidation.getErrorMessage().orElse("Unknown validation error"));
        }
        
        return Result.success(new TodoTitle(normalized));
    }
    
    /**
     * 長さ制限の検証
     * Strategy パターンで将来的なルール拡張に対応
     */
    @Nonnull
    private static Result<Void> validateLength(@Nonnull String value) {
        if (value.isEmpty()) {
            return Result.failure("Todoタイトルは空にできません");
        }
        
        if (value.length() < ValidationRules.MIN_LENGTH) {
            return Result.failure(
                String.format("Todoタイトルは%d文字以上で入力してください", ValidationRules.MIN_LENGTH)
            );
        }
        
        if (value.length() > ValidationRules.MAX_LENGTH) {
            return Result.failure(
                String.format("Todoタイトルは%d文字以内で入力してください（現在: %d文字）", 
                            ValidationRules.MAX_LENGTH, value.length())
            );
        }
        
        return Result.success(null); // Voidの成功結果
    }
    
    /**
     * 文字種制限の検証
     */
    @Nonnull
    private static Result<Void> validateCharacters(@Nonnull String value) {
        if (ValidationRules.INVALID_CHARS.matcher(value).find()) {
            return Result.failure("Todoタイトルに使用できない文字が含まれています");
        }
        
        return Result.success(null);
    }
    
    /**
     * タイトルの長さを取得
     */
    public int length() {
        return value.length();
    }
    
    /**
     * 部分文字列検索（大文字小文字を区別しない）
     * 
     * @param searchText 検索文字列（non-null）
     * @return 含まれている場合true
     * @throws IllegalArgumentException searchTextがnullの場合
     */
    public boolean contains(@Nonnull String searchText) {
        if (searchText == null) {
            throw new IllegalArgumentException("Search text must not be null");
        }
        return value.toLowerCase().contains(searchText.toLowerCase());
    }
    
    /**
     * 前方一致検索（大文字小文字を区別しない）
     * 
     * @param prefix プレフィックス文字列（non-null）
     * @return 前方一致する場合true
     */
    public boolean startsWith(@Nonnull String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix must not be null");
        }
        return value.toLowerCase().startsWith(prefix.toLowerCase());
    }
    
    /**
     * 別のTodoTitleと同じかどうかを判定
     * 
     * @param other 比較対象（non-null）
     * @return 同じタイトルの場合true
     */
    public boolean isSameAs(@Nonnull TodoTitle other) {
        if (other == null) {
            throw new IllegalArgumentException("Comparison target must not be null");
        }
        return this.equals(other);
    }
    
    /**
     * タイトルが特定のキーワードを含むかどうかを判定
     * 複数キーワードのAND検索
     * 
     * @param keywords 検索キーワード配列（non-null）
     * @return すべてのキーワードが含まれている場合true
     */
    public boolean containsAllKeywords(@Nonnull String... keywords) {
        if (keywords == null) {
            throw new IllegalArgumentException("Keywords must not be null");
        }
        
        String lowerValue = value.toLowerCase();
        for (String keyword : keywords) {
            if (keyword != null && !lowerValue.contains(keyword.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}
