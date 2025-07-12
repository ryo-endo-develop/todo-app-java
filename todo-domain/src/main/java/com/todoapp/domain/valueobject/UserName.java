package com.todoapp.domain.valueobject;

import com.todoapp.domain.common.Result;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * ユーザー名を表現する値オブジェクト
 * Effective Java: Item 62 (他の型が適切な場合は文字列を避ける)
 * ビジネスルールとバリデーションを内包
 */
@Getter
@EqualsAndHashCode
@ToString
public final class UserName {
    
    // ビジネスルール定数
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 100;
    
    // 使用可能文字パターン（英数字、アンダースコア、ハイフン、日本語）
    private static final Pattern VALID_CHARS = Pattern.compile("^[a-zA-Z0-9_\\-\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}]+$");
    
    private final String value;
    
    // プライベートコンストラクタ
    private UserName(String value) {
        this.value = value;
    }
    
    /**
     * 文字列からUserNameを作成
     * ビジネスルールによるバリデーション付き
     */
    public static Result<UserName> of(@Nonnull String value) {
        // 前後の空白を削除
        String trimmed = value.trim();
        
        if (trimmed.isEmpty()) {
            return Result.failure("ユーザー名は空にできません");
        }
        
        if (trimmed.length() < MIN_LENGTH) {
            return Result.failure("ユーザー名は" + MIN_LENGTH + "文字以上で入力してください");
        }
        
        if (trimmed.length() > MAX_LENGTH) {
            return Result.failure("ユーザー名は" + MAX_LENGTH + "文字以内で入力してください（現在: " + trimmed.length() + "文字）");
        }
        
        // 使用可能文字のチェック
        if (!VALID_CHARS.matcher(trimmed).matches()) {
            return Result.failure("ユーザー名には英数字、アンダースコア、ハイフン、日本語のみ使用できます");
        }
        
        // 先頭がハイフンやアンダースコアは禁止
        char firstChar = trimmed.charAt(0);
        if (firstChar == '-' || firstChar == '_') {
            return Result.failure("ユーザー名はハイフンやアンダースコアで始めることはできません");
        }
        
        return Result.success(new UserName(trimmed));
    }
    
    /**
     * ユーザー名の長さを取得
     */
    public int length() {
        return value.length();
    }
    
    /**
     * 指定した文字列が含まれているかチェック（大文字小文字を区別しない）
     */
    public boolean contains(@Nonnull String searchText) {
        return value.toLowerCase().contains(searchText.toLowerCase());
    }
    
    /**
     * 別のUserNameと同じかどうかを判定
     */
    public boolean isSameAs(@Nonnull UserName other) {
        return this.equals(other);
    }
}
