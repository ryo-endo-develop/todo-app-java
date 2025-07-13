# テスト実行コマンド

## 単体テスト実行

```bash
# Domain層のテストを実行
cd todo-domain
mvn test

# 特定のテストクラスのみ実行
mvn test -Dtest=ResultTest

# カバレッジレポート付きで実行
mvn test jacoco:report
```

## テスト結果の確認

```bash
# テスト結果の確認
cat target/surefire-reports/TEST-*.xml

# カバレッジレポートの確認（HTML）
open target/site/jacoco/index.html
```

## Phase 1 完了基準

- [ ] ResultTest: 全テストケース通過
- [ ] TodoIdTest: 全テストケース通過
- [ ] TodoTitleTest: 実装予定
- [ ] TodoDescriptionTest: 実装予定
- [ ] UserNameTest: 実装予定
- [ ] TodoStatusTest: 実装予定
- [ ] UserRoleTest: 実装予定

## カバレッジ目標

- Line Coverage: 90%以上
- Branch Coverage: 85%以上
- Method Coverage: 95%以上
