/* (C) 2025 Todo App Project */
package com.todoapp.domain.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.todoapp.domain.enums.TodoStatus;

/**
 * Todoステータス遷移のビジネスルールを管理するポリシークラス
 *
 * <p>設計方針: - EnumのTodoStatusを直接使用して型安全性を保証 - 複雑な遷移ロジックをサービス層で管理 - 設定の外部化により柔軟性を確保
 */
@ThreadSafe
public final class TodoStatusTransitionPolicy {

  // Enumを直接使用してマッピング
  private final Map<TodoStatus, Set<TodoStatus>> transitionRules;

  /** デフォルトの遷移ルールでポリシーを作成 */
  public TodoStatusTransitionPolicy() {
    this.transitionRules = new ConcurrentHashMap<>();
    initializeDefaultRules();
  }

  /** カスタム遷移ルールでポリシーを作成 */
  public TodoStatusTransitionPolicy(@Nonnull Map<TodoStatus, Set<TodoStatus>> customRules) {
    this.transitionRules = new ConcurrentHashMap<>(customRules);
  }

  /** デフォルトの遷移ルールを初期化 Enumを直接使用して型安全性を保証 */
  private void initializeDefaultRules() {
    // TODO状態からの遷移
    transitionRules.put(
        TodoStatus.TODO, Set.of(TodoStatus.IN_PROGRESS, TodoStatus.COMPLETED, TodoStatus.DELETED));

    // IN_PROGRESS状態からの遷移
    transitionRules.put(
        TodoStatus.IN_PROGRESS, Set.of(TodoStatus.TODO, TodoStatus.COMPLETED, TodoStatus.DELETED));

    // COMPLETED状態からの遷移
    transitionRules.put(
        TodoStatus.COMPLETED, Set.of(TodoStatus.TODO, TodoStatus.IN_PROGRESS, TodoStatus.DELETED));

    // DELETED状態からの遷移（通常は不可）
    transitionRules.put(TodoStatus.DELETED, Set.of());
  }

  /**
   * 指定したステータス遷移が可能かどうかを判定
   *
   * @param currentStatus 現在のステータス（Enum）
   * @param newStatus 遷移先ステータス（Enum）
   * @return 遷移可能な場合true
   */
  public boolean canTransition(@Nonnull TodoStatus currentStatus, @Nonnull TodoStatus newStatus) {
    if (currentStatus == null) {
      throw new IllegalArgumentException("Current status must not be null");
    }
    if (newStatus == null) {
      throw new IllegalArgumentException("New status must not be null");
    }

    // 同じステータスへの遷移は常に許可
    if (currentStatus == newStatus) {
      return true;
    }

    Set<TodoStatus> allowedTransitions = transitionRules.get(currentStatus);
    return allowedTransitions != null && allowedTransitions.contains(newStatus);
  }

  /** 指定したステータスから遷移可能なステータス一覧を取得 */
  @Nonnull
  public Set<TodoStatus> getAllowedTransitions(@Nonnull TodoStatus currentStatus) {
    if (currentStatus == null) {
      throw new IllegalArgumentException("Current status must not be null");
    }

    return transitionRules.getOrDefault(currentStatus, Set.of());
  }

  /** 新しい遷移ルールを追加 Enumの型安全性によりコンパイル時にチェック */
  public void addTransitionRule(@Nonnull TodoStatus fromStatus, @Nonnull TodoStatus toStatus) {
    if (fromStatus == null) {
      throw new IllegalArgumentException("From status must not be null");
    }
    if (toStatus == null) {
      throw new IllegalArgumentException("To status must not be null");
    }

    transitionRules.computeIfAbsent(fromStatus, k -> ConcurrentHashMap.newKeySet()).add(toStatus);
  }

  /** 遷移ルールを削除 */
  public void removeTransitionRule(@Nonnull TodoStatus fromStatus, @Nonnull TodoStatus toStatus) {
    if (fromStatus == null) {
      throw new IllegalArgumentException("From status must not be null");
    }
    if (toStatus == null) {
      throw new IllegalArgumentException("To status must not be null");
    }

    Set<TodoStatus> transitions = transitionRules.get(fromStatus);
    if (transitions != null) {
      transitions.remove(toStatus);
    }
  }

  /** 批准ワークフローのような複雑な遷移ルール判定 ビジネスロジックをサービス層に集約 */
  public boolean canTransitionWithApproval(
      @Nonnull TodoStatus currentStatus, @Nonnull TodoStatus newStatus, boolean hasApproval) {
    // 基本的な遷移チェック
    if (!canTransition(currentStatus, newStatus)) {
      return false;
    }

    // 特定の遷移では承認が必要
    if (currentStatus == TodoStatus.IN_PROGRESS && newStatus == TodoStatus.COMPLETED) {
      return hasApproval;
    }

    return true;
  }

  /** 緊急時の特別遷移ルール */
  public boolean canEmergencyTransition(
      @Nonnull TodoStatus currentStatus, @Nonnull TodoStatus newStatus, boolean isEmergency) {
    if (!isEmergency) {
      return canTransition(currentStatus, newStatus);
    }

    // 緊急時は削除状態以外から任意のステータスに遷移可能
    return currentStatus != TodoStatus.DELETED;
  }
}
