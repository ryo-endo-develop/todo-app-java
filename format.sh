#!/bin/bash

# Spotless フォーマッター実行スクリプト
# Usage: ./format.sh [options]

set -euo pipefail

# カラー定義
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 使用方法表示
print_usage() {
    echo "Usage: $0 [options]"
    echo ""
    echo "Options:"
    echo "  --check         フォーマットをチェックのみ（修正なし）"
    echo "  --apply         フォーマットを適用"
    echo "  --module <mod>  特定のモジュールのみ処理 (default: todo-domain)"
    echo "  -h, --help      ヘルプを表示"
    echo ""
    echo "Examples:"
    echo "  $0              # フォーマットチェック"
    echo "  $0 --apply      # フォーマット適用"
    echo "  $0 --check      # チェックのみ（デフォルト）"
}

# プロジェクトディレクトリに移動
PROJECT_DIR="/Users/ryo/Repositories/claude/todo-app"
cd "$PROJECT_DIR"

# 引数解析
ACTION="check"
MODULE="todo-domain"

while [[ $# -gt 0 ]]; do
    case $1 in
        --check)
            ACTION="check"
            shift
            ;;
        --apply)
            ACTION="apply"
            shift
            ;;
        --module)
            MODULE="$2"
            shift 2
            ;;
        -h|--help)
            print_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            print_usage
            exit 1
            ;;
    esac
done

print_info "=== Spotless コードフォーマッター ==="
print_info "アクション: $ACTION"
print_info "モジュール: $MODULE"

# Spotless実行
case $ACTION in
    "check")
        print_info "コードフォーマットをチェック中..."
        if mvn spotless:check -pl "$MODULE" -q; then
            print_success "✅ コードフォーマット: 問題なし"
        else
            print_error "❌ コードフォーマット: 修正が必要です"
            print_warning "修正するには: $0 --apply"
            exit 1
        fi
        ;;
    "apply")
        print_info "コードフォーマットを適用中..."
        if mvn spotless:apply -pl "$MODULE" -q; then
            print_success "✅ コードフォーマット: 適用完了"
            print_info "変更されたファイルをgit statusで確認してください"
        else
            print_error "❌ コードフォーマット: 適用に失敗しました"
            exit 1
        fi
        ;;
esac

print_info "=== 完了 ==="
