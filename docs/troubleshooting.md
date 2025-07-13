# Docker環境 トラブルシューティングガイド

## 🚨 よくある問題と解決方法

### 1. イメージ取得エラー

#### 症状
```
failed to resolve source metadata for docker.io/library/eclipse-temurin:17-jdk-alpine: no match for platform in manifest
```

#### 原因
- Apple Silicon Mac (M1/M2) での `eclipse-temurin:alpine` イメージ非対応
- プラットフォーム固有のイメージ問題

#### 解決方法

**方法1: 代替イメージ使用（推奨）**
```bash
# OpenJDK版を使用
make up-openjdk
make shell-openjdk

# または Amazon Corretto版
make up-corretto  
make shell-corretto
```

**方法2: プラットフォーム強制指定**
```bash
# AMD64プラットフォームを強制
docker compose up -d --platform linux/amd64
```

**方法3: 環境診断**
```bash
# プラットフォーム情報確認
make debug-platform

# イメージ再構築
make rebuild-openjdk
```

### 2. Maven Wrapper エラー

#### 症状
```
./mvnw: line X: java: command not found
```

#### 解決方法
```bash
# Maven Wrapper JAR を再ダウンロード
./scripts/download-maven-wrapper.sh

# 権限修正
make fix-permissions

# コンテナ再構築
make rebuild
```

### 3. ポート競合エラー

#### 症状
```
Error response from daemon: driver failed programming external connectivity on endpoint todo-postgres: Bind for 0.0.0.0:5432 failed: port is already allocated
```

#### 解決方法
```bash
# 既存コンテナ確認
docker ps -a

# 競合するコンテナ停止
docker stop $(docker ps -q --filter "publish=5432")

# または全環境クリーンアップ
make clean-docker
```

### 4. メモリ不足エラー

#### 症状
```
java.lang.OutOfMemoryError: Java heap space
```

#### 解決方法
```bash
# Docker Desktopでメモリ割り当て増加（4GB以上推奨）

# または環境変数で調整
export MAVEN_OPTS="-Xmx2048m"
make up
```

### 5. ファイル権限エラー（Linux）

#### 症状
```
Permission denied: ./mvnw
```

#### 解決方法
```bash
# 権限修正
make fix-permissions

# または手動で修正
chmod +x mvnw mvnw.cmd scripts/*.sh

# ユーザーID合わせ（必要に応じて）
sudo chown -R $USER:$USER .
```

## 🔧 環境別の使い分け

### プラットフォーム別推奨環境

| プラットフォーム | 推奨Docker環境 | コマンド |
|------------------|----------------|----------|
| **Apple Silicon** | OpenJDK | `make up-openjdk` |
| **Intel Mac** | デフォルト | `make up` |
| **Linux x86_64** | デフォルト | `make up` |
| **Windows** | OpenJDK | `make up-openjdk` |
| **AWS環境** | Corretto | `make up-corretto` |

### 用途別環境選択

#### 開発・テスト用
```bash
make up              # 軽量・高速
make test-domain
```

#### 本番環境想定
```bash
make up-corretto     # AWS Corretto（本番同等）
make test-corretto
```

#### 互換性重視
```bash
make up-openjdk      # 最大互換性
make test-openjdk
```

## 🛠️ 高度なトラブルシューティング

### Docker Desktop設定最適化

#### メモリ・CPU設定
```
Docker Desktop > Settings > Resources
- Memory: 4GB以上
- CPUs: 2コア以上
- Swap: 1GB以上
```

#### ディスク容量確保
```bash
# 不要なイメージ削除
docker image prune -a

# 不要なボリューム削除
docker volume prune

# ビルドキャッシュクリア
docker builder prune
```

### ネットワーク問題

#### DNS解決問題
```bash
# Docker Desktopの DNS設定確認
# 8.8.8.8, 1.1.1.1 等のパブリックDNS使用

# または企業プロキシ設定
docker compose --env-file .env.proxy up -d
```

#### プロキシ環境
```bash
# .env.proxy ファイル作成
HTTP_PROXY=http://proxy.company.com:8080
HTTPS_PROXY=http://proxy.company.com:8080
NO_PROXY=localhost,127.0.0.1
```

### パフォーマンス最適化

#### ビルド速度向上
```bash
# 並列ビルド有効化
export MAVEN_OPTS="-T 1C"

# 依存関係キャッシュ確認
docker volume ls | grep maven_cache
```

#### 起動時間短縮
```bash
# 事前イメージプル
docker compose pull

# バックグラウンド起動
docker compose up -d
```

## 📞 サポート情報

### ログ収集
```bash
# 全体ログ
make logs

# 特定サービス
docker compose logs postgres
docker compose logs java-dev

# リアルタイムログ
docker compose logs -f java-dev
```

### 環境情報出力
```bash
# システム情報
make debug-platform

# Docker情報
docker system info

# 健全性チェック
make health
```

### エスカレーション時の情報
問題解決困難時は以下の情報を添えて報告：

1. `make debug-platform` の出力
2. `docker compose logs` の関連部分
3. エラーメッセージ全文
4. 実行したコマンド履歴
5. OS・Dockerバージョン

この情報により、迅速な問題解決が可能になります。
