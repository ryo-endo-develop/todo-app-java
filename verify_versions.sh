#!/bin/bash

# 依存関係バージョン確認スクリプト

echo "=== 依存関係バージョン確認 ==="

cd /Users/ryo/Repositories/claude/todo-app

echo "1. Maven プロジェクト情報:"
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
echo ""

echo "2. 主要な依存関係バージョン:"
echo "Spring Boot: $(mvn help:evaluate -Dexpression=spring-boot.version -q -DforceStdout)"
echo "JaCoCo: $(mvn help:evaluate -Dexpression=jacoco.version -q -DforceStdout)"
echo "JUnit: $(mvn help:evaluate -Dexpression=junit.version -q -DforceStdout)"
echo "Lombok: $(mvn help:evaluate -Dexpression=lombok.version -q -DforceStdout)"
echo ""

echo "3. JaCoCo プラグイン動作確認:"
mvn jacoco:help -pl todo-domain -q

echo ""
echo "4. テスト実行とカバレッジ生成:"
mvn clean test jacoco:report -pl todo-domain -q

if [ -f "todo-domain/target/site/jacoco/index.html" ]; then
    echo "✅ JaCoCoレポートが正常に生成されました"
    echo "📊 レポート場所: todo-domain/target/site/jacoco/index.html"
else
    echo "❌ JaCoCoレポートの生成に失敗しました"
fi

echo ""
echo "=== 検証完了 ==="
