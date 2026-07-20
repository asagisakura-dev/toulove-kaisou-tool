# 静的公開

公開版は `frontend` だけで動作します。`frontend/public/data/` 内のJSONをブラウザで読み込み、検索もブラウザ内で行うため、Spring BootとSQLiteは不要です。

## ローカル確認

```powershell
cd frontend
npm ci
npm run build:static
npm run preview
```

表示されたURLを開き、回想一覧、刀剣・戦場検索、詳細モーダルを確認してください。公開データを更新する場合は、`backend/src/main/resources/data/` のJSONを編集後、同じ内容を `frontend/public/data/` へ反映してからビルドします。

## GitHub Pages

`.github/workflows/deploy-pages.yml` を追加済みです。GitHubへリポジトリをpushした後、リポジトリの **Settings > Pages > Build and deployment** で **GitHub Actions** を選択してください。`main` ブランチへのpushで自動公開されます。

このワークフローはリポジトリ名をURLのベースパスとして自動設定します。独自ドメインを使う場合は、GitHub Pages側でドメインを設定し、`VITE_BASE_PATH=/` でビルドするようワークフローを変更してください。

## Netlify / Cloudflare Pages / Vercel

いずれもリポジトリを接続し、以下を指定すれば公開できます。

- Root directory: `frontend`
- Build command: `npm ci && npm run build:static`
- Publish directory: `frontend/dist`（Root directoryを指定する画面では `dist`）

これらのサービスでは、通常は `VITE_BASE_PATH` を設定せず、ルート(`/`)公開のままで利用できます。
